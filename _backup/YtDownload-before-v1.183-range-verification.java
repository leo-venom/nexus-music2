package com.leo.nexusmusic2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import org.schabi.newpipe.extractor.Image;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.MediaFormat;
import org.schabi.newpipe.extractor.search.SearchExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.ServiceList;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.downloader.Request;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;
import org.schabi.newpipe.extractor.stream.AudioStream;
import org.schabi.newpipe.extractor.stream.StreamExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Download do YouTube DENTRO do app — funciona em qualquer rede (dados móveis,
 * Wi-Fi de terceiros), sem precisar do computador.
 *
 * Usa o **NewPipe Extractor** (Java puro, mantido pela equipe do NewPipe) para
 * descobrir o stream de áudio, e baixa o arquivo direto.
 *
 * Salva em `Music/NexusMusic/YOUTUBE/` via MediaStore, então a música aparece
 * para os outros players do celular também. A capa vai para a pasta privada do
 * app (servida em `/cover/<id>`).
 *
 * ⚠️ O YouTube muda com frequência: se parar de baixar, é sinal de que o
 * NewPipe Extractor precisa de uma versão mais nova — basta atualizar a
 * dependência no `app/build.gradle` e recompilar.
 */
public class YtDownload {

    private static final String TAG = "NexusYt";

    /** Caminho relativo único usado pelo MediaStore. */
    private static final String SUBPASTA_BASE = Environment.DIRECTORY_MUSIC
            + "/NexusMusic/YOUTUBE/";

    private final Context ctx;
    private final Library library;

    // -------- estado do job (lido pelo /api/youtube/status) -------- //
    private volatile boolean ativo = false;
    private volatile boolean cancelarSolicitado = false;
    private volatile double percentual = 0;
    private volatile String fase = "parado";
    private volatile String etapa = "";
    private volatile String titulo = "";
    private volatile String erro = "";
    private volatile String mensagem = "";

    private static boolean newPipePronto = false;
    private static final long CACHE_PESQUISA_MS = 60_000L;
    private final Map<String, CachePesquisa> cachePesquisa = new HashMap<>();
    private final Map<String, CachePrevia> cachePrevia = new HashMap<>();

    /**
     * Trava CURTA só para os caches (HashMap não é thread-safe).
     *
     * <p>Antes, {@code previa()} e {@code pesquisar()} eram {@code synchronized}
     * no MESMO monitor do objeto: uma prévia em andamento (que demora em vídeo
     * longo) BLOQUEAVA a pesquisa seguinte, e a interface parecia travada.
     * Agora o trabalho de rede roda em paralelo e só o acesso ao cache é
     * protegido, por alguns microssegundos (a pedido do Leo, 19/09/2026).</p>
     */
    private final Object travarCache = new Object();

    /**
     * Resoluções de prévia EM ANDAMENTO, por URL do vídeo (19/09/2026).
     *
     * <p>Serve para dois ganhos de velocidade: (1) a pesquisa AQUECE a prévia
     * do primeiro resultado em segundo plano, sobrepondo o tempo de rede do
     * YouTube com o desenho do card; (2) se a interface pedir a prévia enquanto
     * ela já está sendo resolvida, ninguém baixa a página do vídeo duas vezes —
     * quem chega depois espera a mesma resolução.</p>
     */
    private final Map<String, CompletableFuture<String>> previasEmAndamento = new ConcurrentHashMap<>();

    private static final class CachePrevia {
        final long criado;
        final String url;
        CachePrevia(long criado, String url) {
            this.criado = criado;
            this.url = url;
        }
    }

    private static final class CachePesquisa {
        final long criado;
        final String json;
        CachePesquisa(long criado, String json) {
            this.criado = criado;
            this.json = json;
        }
    }

    public YtDownload(Context ctx, Library library) {
        this.ctx = ctx;
        this.library = library;
    }

    // ------------------------------------------------------------------ //
    //  Inicialização do NewPipe
    // ------------------------------------------------------------------ //

    public static synchronized void preparar() {
        if (newPipePronto) return;
        NewPipe.init(new NexusDownloader());
        newPipePronto = true;
        Log.i(TAG, "NewPipe Extractor pronto");
    }

    /**
     * Implementação mínima do Downloader do NewPipe usando HttpURLConnection —
     * evita trazer OkHttp só para isso.
     */
    static class NexusDownloader extends Downloader {
        @Override
        public Response execute(Request request) throws IOException, ReCaptchaException {
            HttpURLConnection conexao = (HttpURLConnection) new URL(request.url()).openConnection();
            conexao.setRequestMethod(request.httpMethod());
            conexao.setConnectTimeout(10000);
            conexao.setReadTimeout(15000);
            conexao.setInstanceFollowRedirects(true);

            for (Map.Entry<String, List<String>> cabecalho : request.headers().entrySet()) {
                for (String valor : cabecalho.getValue()) {
                    conexao.addRequestProperty(cabecalho.getKey(), valor);
                }
            }

            byte[] dados = request.dataToSend();
            if (dados != null && dados.length > 0) {
                conexao.setDoOutput(true);
                try (OutputStream os = conexao.getOutputStream()) {
                    os.write(dados);
                }
            }

            int codigo = conexao.getResponseCode();
            String mensagemHttp = conexao.getResponseMessage();

            InputStream fluxo = codigo >= 400 ? conexao.getErrorStream() : conexao.getInputStream();
            String corpo = fluxo == null ? "" : lerTexto(fluxo);

            return new Response(codigo, mensagemHttp, conexao.getHeaderFields(), corpo,
                    conexao.getURL().toString());
        }

        private static String lerTexto(InputStream is) throws IOException {
            try (InputStream entrada = is) {
                java.io.ByteArrayOutputStream saida = new java.io.ByteArrayOutputStream();
                byte[] buffer = new byte[8192];
                int n;
                while ((n = entrada.read(buffer)) > 0) {
                    saida.write(buffer, 0, n);
                }
                return saida.toString("UTF-8");
            }
        }
    }

    // ------------------------------------------------------------------ //
    //  API do job
    // ------------------------------------------------------------------ //

    public boolean ativo() {
        return ativo;
    }

    public synchronized String cancelar() {
        if (!ativo) return "{\"ok\":false,\"erro\":\"Nenhum download ativo\"}";
        cancelarSolicitado = true;
        fase = "cancelando";
        etapa = "cancelando download";
        return "{\"ok\":true}";
    }
    public synchronized String iniciar(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "{\"ok\":false,\"erro\":\"Informe o link do YouTube\"}";
        }
        String limpa = url.trim();
        if (!limpa.contains("youtube.com") && !limpa.contains("youtu.be")) {
            return "{\"ok\":false,\"erro\":\"Só links do YouTube\"}";
        }
        if (ativo) {
            return "{\"ok\":false,\"erro\":\"Já existe um download em andamento\"}";
        }

        ativo = true;
        cancelarSolicitado = false;
        percentual = 0;
        fase = "baixando";
        etapa = "preparando";
        titulo = "";
        erro = "";
        mensagem = "";

        Thread t = new Thread(() -> baixar(limpa), "nexus-yt");
        t.setDaemon(true);
        t.start();

        return "{\"ok\":true}";
    }

    /** Verifica o tamanho do áudio antes de iniciar o download. */
    public synchronized String preflight(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "{\"ok\":false,\"erro\":\"Resultado inválido\"}";
        }
        try {
            preparar();
            StreamExtractor extrator = ServiceList.YouTube.getStreamExtractor(url.trim());
            extrator.fetchPage();
            AudioStream escolhido = escolherAudio(extrator.getAudioStreams());
            if (escolhido == null) return "{\"ok\":false,\"erro\":\"Áudio indisponível\"}";
            long tamanho = descobrirTamanho(escolhido.getUrl());
            return "{\"ok\":true,\"titulo\":\""
                    + NexusServer.escapar(extrator.getName())
                    + "\",\"bytes\":" + tamanho
                    + ",\"grande\":" + (tamanho >= 20L * 1024L * 1024L) + "}";
        } catch (Exception e) {
            Log.w(TAG, "pré-checagem: " + e.getMessage());
            return "{\"ok\":false,\"erro\":\"Não foi possível verificar o tamanho\"}";
        }
    }


    public String previa(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "{\"ok\":false,\"erro\":\"Resultado inválido\"}";
        }
        String chavePrevia = url.trim();

        /* 1) cache (300s): resposta instantânea para quem já foi resolvido */
        CachePrevia previa;
        synchronized (travarCache) {
            previa = cachePrevia.get(chavePrevia);
        }
        if (previa != null && System.currentTimeMillis() - previa.criado < 300_000L) {
            return "{\"ok\":true,\"url\":\""
                    + NexusServer.escapar(previa.url) + "\"}";
        }

        /* 2) já está sendo resolvida (aquecida pela pesquisa, ou por um pedido
           anterior)? Quem chegar depois ESPERA essa mesma resolução — ninguém
           baixa a página do YouTube duas vezes. */
        return resolverPrevia(chavePrevia);
    }

    /**
     * Resolve a prévia UMA ÚNICA VEZ por vídeo, com deduplicação (19/09/2026).
     *
     * <p>O primeiro a pedir faz o trabalho; os demais aproveitam o mesmo
     * resultado. É o que permite aquecer a prévia junto com a pesquisa e
     * entregá-la instantaneamente quando a interface pedir.</p>
     */
    private String resolverPrevia(String chave) {
        CompletableFuture<String> meu = new CompletableFuture<>();
        CompletableFuture<String> jaEmAndamento = previasEmAndamento.putIfAbsent(chave, meu);
        boolean souEu = (jaEmAndamento == null);
        CompletableFuture<String> futuro = souEu ? meu : jaEmAndamento;

        if (souEu) {
            long t0 = System.currentTimeMillis();
            try {
                meu.complete(resolverPreviaDeVerdade(chave));
            } catch (Throwable t) {
                Log.w(TAG, "falha na prévia: " + t.getMessage());
                meu.complete("{\"ok\":false,\"erro\":\"Não foi possível reproduzir a prévia\"}");
            } finally {
                previasEmAndamento.remove(chave);
                Log.i(TAG, "prévia resolvida em " + (System.currentTimeMillis() - t0) + " ms");
            }
        }
        try {
            return futuro.get(20, TimeUnit.SECONDS);
        } catch (Exception e) {
            return "{\"ok\":false,\"erro\":\"prévia demorou demais\"}";
        }
    }

    /** Trabalho real da prévia: extrai o stream mais leve e guarda no cache. */
    private String resolverPreviaDeVerdade(String chavePrevia) throws Exception {
        preparar();
        StreamExtractor extrator = ServiceList.YouTube.getStreamExtractor(chavePrevia);
        extrator.fetchPage();
        /* A prévia usa o stream de MAIOR bitrate disponível para preservar
           a qualidade sonora. O carregamento continua limitado pelo timeout
           e pelo cache, sem reduzir deliberadamente a resolução do áudio. */
        AudioStream escolhido = escolherAudioLeve(extrator.getAudioStreams());
        if (escolhido == null) {
            return "{\"ok\":false,\"erro\":\"Prévia indisponível\"}";
        }
        String audioUrl = escolhido.getUrl();
        synchronized (travarCache) {
            cachePrevia.put(chavePrevia, new CachePrevia(System.currentTimeMillis(), audioUrl));
            /* 24 entradas (era 12): a busca passou a devolver 10 resultados, e
               cada um aquece a sua prévia — com 12 a própria lista se
               expulsaria (a pedido, 20/09/2026). */
            if (cachePrevia.size() > 24) {
                cachePrevia.remove(cachePrevia.keySet().iterator().next());
            }
        }
        return "{\"ok\":true,\"url\":\""
                + NexusServer.escapar(audioUrl) + "\"}";
    }

    /**
     * <p><b>PRÉ-AQUECIMENTO DAS PRÉVIAS</b> (a pedido, 19/09/2026; passou a
     * cobrir os 10 resultados em 20/09/2026). Antes só o PRIMEIRO resultado
     * tinha a prévia resolvida em segundo plano. Agora todos os resultados já
     * vêm com a resolução em andamento, para o toque no card começar a tocar na
     * hora.</p>
     *
     * <p>Duas travas para não pesar: roda em <b>UMA única thread</b> e <b>uma
     * prévia de cada vez</b>, com uma folga curta entre elas — abrir dez
     * {@code fetchPage} simultâneos no YouTube atrasaria a própria resposta da
     * pesquisa. E não há trabalho desperdiçado: {@code previasEmAndamento}
     * deduplica por chave, então se o Leo tocar num card enquanto o aquecimento
     * corre, o toque aproveita a resolução que já está em andamento.</p>
     */
    private void aquecerPrevias(List<String> urls) {
        if (urls == null || urls.isEmpty()) return;
        Thread t = new Thread(() -> {
            for (int i = 0; i < urls.size(); i++) {
                String url = urls.get(i);
                if (url != null && !url.trim().isEmpty()) {
                    try {
                        previa(url.trim());
                    } catch (Throwable ignored) {
                        /* o aquecimento é oportunista: falhar aqui não é erro do usuário */
                    }
                }
                if (i < urls.size() - 1) {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException interrompido) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }, "nexus-previas-warm");
        t.setDaemon(true);
        t.start();
    }


    public String pesquisar(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return "{\"ok\":false,\"erro\":\"Digite o nome da música\",\"resultados\":[]}";
        }
        String busca = termo.trim();
        if (busca.length() > 120) busca = busca.substring(0, 120);
        String chave = normalizarTitulo(busca);
        CachePesquisa anterior;
        synchronized (travarCache) {
            anterior = cachePesquisa.get(chave);
        }
        long agora = System.currentTimeMillis();
        if (anterior != null && agora - anterior.criado < CACHE_PESQUISA_MS) {
            return anterior.json;
        }
        try {
            preparar();
            SearchExtractor extrator = ServiceList.YouTube.getSearchExtractor(busca);
            extrator.fetchPage();
            List<InfoItem> itens = extrator.getInitialPage().getItems();
            StringBuilder json = new StringBuilder("{\"ok\":true,\"resultados\":[");
            int adicionados = 0;
            /* todas as URLs desta pesquisa vão para o pré-aquecimento das prévias
               (a pedido, 19/09/2026) */
            List<String> urlsParaAquecer = new ArrayList<>();
            for (InfoItem item : itens) {
                if (!(item instanceof StreamInfoItem)) continue;
                StreamInfoItem video = (StreamInfoItem) item;
                if (adicionados >= 10) break;
                if (adicionados > 0) json.append(',');
                urlsParaAquecer.add(video.getUrl());
                String capa = "";
                List<Image> imagens = video.getThumbnails();
                if (imagens != null && !imagens.isEmpty()) {
                    capa = imagens.get(0).getUrl();
                }
                String artista = video.getUploaderName();
                if (artista == null || artista.isEmpty()) artista = "YouTube";
                json.append("{\"titulo\":\"").append(NexusServer.escapar(video.getName()))
                        .append("\",\"artista\":\"").append(NexusServer.escapar(artista))
                        .append("\",\"duracao\":\"").append(formatarDuracao(video.getDuration()))
                        .append("\",\"url\":\"").append(NexusServer.escapar(video.getUrl()))
                        .append("\",\"capa\":\"").append(NexusServer.escapar(capa))
                        .append("\"}");
                adicionados++;
            }
            json.append("]}");
            String resultado = json.toString();
            synchronized (travarCache) {
                cachePesquisa.put(chave, new CachePesquisa(System.currentTimeMillis(), resultado));
                if (cachePesquisa.size() > 12) {
                    cachePesquisa.remove(cachePesquisa.keySet().iterator().next());
                }
            }
            /* AQUECIMENTO DAS PRÉVIAS (19/09/2026 · 10 resultados em 20/09/2026):
               as prévias de TODOS os resultados começam a ser resolvidas AGORA,
               em segundo plano, uma de cada vez — quando o Leo toca num card, a
               resolução já está em andamento (ou pronta no cache). */
            aquecerPrevias(urlsParaAquecer);
            return resultado;
        } catch (Exception e) {
            Log.w(TAG, "falha na pesquisa: " + e.getMessage());
            return "{\"ok\":false,\"erro\":\"Não foi possível pesquisar agora\",\"resultados\":[]}";
        }
    }

    private static AudioStream escolherAudio(List<AudioStream> audios) {
        if (audios == null || audios.isEmpty()) return null;
        int[] bitrates = new int[audios.size()];
        boolean[] m4a = new boolean[audios.size()];
        for (int i = 0; i < audios.size(); i++) {
            AudioStream stream = audios.get(i);
            bitrates[i] = AudioQuality.bitrateDe(stream.getAverageBitrate(), stream.getBitrate());
        }
        int indice = AudioQuality.escolherIndice(bitrates);
        return indice < 0 ? null : audios.get(indice);
    }

    /** A prévia usa a mesma regra de maior qualidade de origem. */
    private static AudioStream escolherAudioLeve(List<AudioStream> audios) {
        AudioStream escolhido = escolherAudio(audios);
        if (escolhido != null) {
            Log.i(TAG, "prévia áudio: " + escolhido.getFormat() + " "
                    + AudioQuality.bitrateDe(escolhido.getAverageBitrate(), escolhido.getBitrate())
                    + " kbps");
        }
        return escolhido;
    }

    private static String formatarDuracao(long segundos) {
        if (segundos <= 0) return "";
        long minutos = segundos / 60;
        long resto = segundos % 60;
        return minutos + ":" + (resto < 10 ? "0" : "") + resto;
    }
    public String statusJson() {
        StringBuilder json = new StringBuilder(200);
        json.append("{\"ativo\":").append(ativo)
                .append(",\"percentual\":").append(String.format(Locale.US, "%.1f", percentual))
                .append(",\"fase\":\"").append(NexusServer.escapar(fase)).append("\"")
                .append(",\"etapa\":\"").append(NexusServer.escapar(etapa)).append("\"")
                .append(",\"titulo\":\"").append(NexusServer.escapar(titulo)).append("\"")
                .append(",\"erro\":\"").append(NexusServer.escapar(erro)).append("\"")
                .append(",\"mensagem\":\"").append(NexusServer.escapar(mensagem)).append("\"}");
        return json.toString();
    }

    // ------------------------------------------------------------------ //
    //  O trabalho
    // ------------------------------------------------------------------ //

    private void baixar(String url) {
        try {
            preparar();

            fase = "baixando";
            etapa = "lendo o vídeo";
            percentual = 0;

            StreamExtractor extrator = ServiceList.YouTube.getStreamExtractor(url);
            extrator.fetchPage();

            titulo = extrator.getName();
            String canal = extrator.getUploaderName();

            /* na v0.26.5 as thumbs vêm numa lista de Image (não existe mais
               getThumbnailUrl()); a primeira é a de maior resolução. */
            String thumb = null;
            List<Image> thumbs = extrator.getThumbnails();
            if (thumbs != null && !thumbs.isEmpty()) {
                thumb = thumbs.get(0).getUrl();
            }
            if (canal == null || canal.isEmpty()) canal = "YouTube";

            /* Impede baixar novamente a mesma faixa que já está diretamente
               em Music/NexusMusic/YOUTUBE/. A verificação acontece antes de
               criar o item pendente no MediaStore. */
            if (jaExisteNoYoutube(titulo)) {
                throw new DownloadDuplicadoException(
                        "⚠️ Esta música já está na pasta YOUTUBE");
            }

            /* Os downloads do YouTube ficam diretamente na pasta YOUTUBE,
               como no comportamento anterior. O artista continua apenas como
               metadado da faixa; não cria subpastas por artista. */

            /* --- escolhe o maior bitrate disponível --- */
            List<AudioStream> audios = extrator.getAudioStreams();
            if (audios == null || audios.isEmpty()) {
                throw new IOException("Não encontrei faixa de áudio nesse vídeo");
            }

            AudioStream escolhido = escolherAudio(audios);

            etapa = "baixando o áudio";
            String ext = escolhido.getFormat() == MediaFormat.M4A ? "m4a" : "webm";
            String tipo = escolhido.getFormat() == MediaFormat.M4A ? "audio/mp4" : "audio/webm";

            String nomeBase = nomeSeguro(titulo);
            String nomeArquivo = nomeBase + "." + ext;

            Uri destino = criarDestino(nomeArquivo, tipo);
            baixarRapido(escolhido.getUrl(), destino);

            percentual = 100;
            etapa = "finalizando";

            /* no Android 10+ o arquivo nasce "pendente" para não aparecer pela
               metade na galeria/players; agora que terminou, libera. */
            liberarPendente(destino);

            /* --- grava metadados que o MediaStore aceita --- */
            ContentValues tags = new ContentValues();
            tags.put(MediaStore.Audio.Media.TITLE, titulo);
            tags.put(MediaStore.Audio.Media.ARTIST, canal);
            tags.put(MediaStore.Audio.Media.ALBUM, "NEXUS MUSIC · YouTube");
            tags.put(MediaStore.Audio.Media.IS_MUSIC, 1);
            try {
                ctx.getContentResolver().update(destino, tags, null, null);
            } catch (Exception ignored) {
            }

            /* --- capa: baixa a thumb para a pasta privada do app --- */
            String id = destino.getLastPathSegment();
            if (thumb != null && !thumb.isEmpty() && id != null && id.matches("\\d+")) {
                try {
                    baixarCapa(thumb, id);
                } catch (Exception e) {
                    Log.w(TAG, "capa: " + e.getMessage());
                }
            }

            /* --- a biblioteca recarrega para a música aparecer na hora --- */
            library.recarregar();

            fase = "concluido";
            etapa = "";
            mensagem = "✔ " + titulo;

        } catch (Exception e) {
            Log.w(TAG, "falha no download: " + e);
            fase = cancelarSolicitado ? "cancelado" : "erro";
            erro = cancelarSolicitado ? "Download cancelado" : mensagemDeErro(e);
        } finally {
            ativo = false;
        }
    }

    private String mensagemDeErro(Exception e) {
        String texto = String.valueOf(e.getMessage());
        if (texto.contains("reCaptcha") || texto.contains("Recaptcha")) {
            return "O YouTube pediu verificação — tente outro link";
        }
        if (texto.contains("Unable to resolve host") || texto.contains("UnknownHost")) {
            return "Sem conexão com a internet";
        }
        if (texto.length() > 120) {
            return texto.substring(0, 120);
        }
        return texto;
    }

    // ------------------------------------------------------------------ //
    //  Gravação
    // ------------------------------------------------------------------ //

    /** Retorna true quando já existe uma faixa com o mesmo título em YOUTUBE,
     * incluindo subpastas antigas criadas por versões anteriores. */
    private boolean jaExisteNoYoutube(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) return false;

        ContentResolver resolver = ctx.getContentResolver();
        Uri colecao = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String colunaCaminho = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                ? MediaStore.Audio.Media.RELATIVE_PATH
                : MediaStore.Audio.Media.DATA;
        String[] colunas = {MediaStore.Audio.Media.TITLE, colunaCaminho};
        String selecao = MediaStore.Audio.Media.TITLE + " IS NOT NULL AND "
                + colunaCaminho + " LIKE ?";
        String prefixo = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                ? SUBPASTA_BASE + "%"
                : "%/Music/NexusMusic/YOUTUBE/%";
        String alvo = normalizarTitulo(titulo);

        try (Cursor c = resolver.query(colecao, colunas, selecao,
                new String[]{prefixo}, null)) {
            if (c == null) return false;
            int iTitulo = c.getColumnIndex(MediaStore.Audio.Media.TITLE);
            while (c.moveToNext()) {
                if (iTitulo >= 0 && alvo.equals(normalizarTitulo(c.getString(iTitulo)))) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "não foi possível conferir duplicidade: " + e.getMessage());
        }
        return false;
    }

    private static String normalizarTitulo(String texto) {
        String s = Normalizer.normalize(texto == null ? "" : texto,
                Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return s.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    private static final class DownloadDuplicadoException extends IOException {
        DownloadDuplicadoException(String mensagem) {
            super(mensagem);
        }
    }

    private Uri criarDestino(String nomeArquivo, String tipoAudio) {
        ContentResolver resolver = ctx.getContentResolver();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues valores = new ContentValues();
            valores.put(MediaStore.Audio.Media.DISPLAY_NAME, nomeArquivo);
            valores.put(MediaStore.Audio.Media.MIME_TYPE, tipoAudio);
            String pastaRelativa = SUBPASTA_BASE;
            valores.put(MediaStore.Audio.Media.RELATIVE_PATH, pastaRelativa);
            valores.put(MediaStore.Audio.Media.IS_PENDING, 1);

            Uri colecaoAudio = MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri uri = resolver.insert(colecaoAudio, valores);
            if (uri == null) {
                throw new IllegalStateException("MediaStore recusou o arquivo");
            }
            /* Alguns MediaStore/OEMs aceitam o insert mas só fixam a pasta
               quando RELATIVE_PATH é reafirmado no item pendente. */
            ContentValues caminhoConfirmado = new ContentValues();
            caminhoConfirmado.put(MediaStore.Audio.Media.RELATIVE_PATH, pastaRelativa);
            resolver.update(uri, caminhoConfirmado, null, null);
            Log.i(TAG, "destino YouTube RELATIVE_PATH: " + pastaRelativa);
            /* libera o arquivo quando o download terminar */
            ContentValues pronto = new ContentValues();
            pronto.put(MediaStore.Audio.Media.IS_PENDING, 0);
            return uri;
        }

        /* Android 9 ou anterior: grava direto na pasta pública */
        File pasta = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC), "NexusMusic/YOUTUBE");
        if (!pasta.exists() && !pasta.mkdirs()) {
            throw new IllegalStateException("Não consegui criar a pasta " + pasta);
        }
        File arquivo = new File(pasta, nomeArquivo);
        ContentValues valores = new ContentValues();
        valores.put(MediaStore.Audio.Media.DATA, arquivo.getAbsolutePath());
        return Uri.fromFile(arquivo);
    }

    /** Solta o arquivo no MediaStore depois de escrever. */
    public void liberarPendente(Uri destino) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues pronto = new ContentValues();
            pronto.put(MediaStore.Audio.Media.IS_PENDING, 0);
            try {
                ctx.getContentResolver().update(destino, pronto, null, null);
            } catch (Exception ignored) {
            }
        }
    }

    // ------------------------------------------------------------------ //
    //  Download em PARALELO (o grande ganho de velocidade)
    // ------------------------------------------------------------------ //

    /**
     * Baixa o áudio com **várias conexões simultâneas** (Range requests).
     *
     * Por que isso importa: o YouTube limita a taxa **por conexão**, então um
     * download sequencial fica lento mesmo com internet boa. Dividindo o arquivo
     * em partes e baixando em paralelo, a velocidade final é a soma das conexões.
     * É exatamente o que o yt-dlp faz com `--concurrent-fragments`, e também como
     * qualquer gerenciador de downloads (IDM, aria2) acelera as coisas.
     */
    private void baixarRapido(String url, Uri destino) throws Exception {
        long total = descobrirTamanho(url);

        /* arquivo pequeno ou tamanho desconhecido: uma conexão já resolve */
        if (total <= 0 || total < 1024 * 1024) {
            try (InputStream entrada = abrirStream(url);
                 OutputStream saida = ctx.getContentResolver().openOutputStream(destino)) {
                if (saida == null) throw new IOException("Não consegui gravar o arquivo");
                byte[] buffer = new byte[128 * 1024];
                long baixado = 0;
                int n;
                while ((n = entrada.read(buffer)) > 0) {
                    if (cancelarSolicitado) throw new IOException("Download cancelado");
                    saida.write(buffer, 0, n);
                    baixado += n;
                    if (total > 0) percentual = Math.min(99.0, baixado * 100.0 / total);
                }
            }
            return;
        }

        final int conexoes = 5;
        final long tamanhoParte = total / conexoes + 1;

        final java.util.concurrent.atomic.AtomicLong baixado =
                new java.util.concurrent.atomic.AtomicLong(0);
        final java.util.concurrent.atomic.AtomicReference<String> falha =
                new java.util.concurrent.atomic.AtomicReference<>(null);

        final List<File> partes = new ArrayList<>();
        final List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < conexoes; i++) {
            final long inicio = i * tamanhoParte;
            final long fim = Math.min(inicio + tamanhoParte - 1, total - 1);
            if (inicio >= total) break;

            final File temporario = new File(ctx.getCacheDir(), "nexus_part_" + i + "_"
                    + System.nanoTime());
            partes.add(temporario);

            Thread t = new Thread(() -> {
                try {
                    baixarParte(url, inicio, fim, temporario, baixado);
                } catch (Exception e) {
                    falha.compareAndSet(null, String.valueOf(e.getMessage()));
                }
            }, "nexus-parte-" + i);
            t.setDaemon(true);
            threads.add(t);
            t.start();
        }

        /* acompanha o progresso e mostra a VELOCIDADE na tela */
        long marco = System.currentTimeMillis();
        long ultimoTotal = 0;
        while (true) {
            if (cancelarSolicitado) throw new IOException("Download cancelado");
            boolean algumaViva = false;
            for (Thread t : threads) {
                if (t.isAlive()) {
                    algumaViva = true;
                    break;
                }
            }
            if (!algumaViva) break;

            Thread.sleep(600);
            long agora = System.currentTimeMillis();
            long atual = baixado.get();
            double segundos = (agora - marco) / 1000.0;
            if (segundos > 0) {
                double mbPorSegundo = (atual - ultimoTotal) / segundos / (1024.0 * 1024.0);
                percentual = Math.min(99.0, atual * 100.0 / total);
                etapa = String.format(Locale.US, "baixando · %.1f MB/s", mbPorSegundo);
            }
            marco = agora;
            ultimoTotal = atual;

            if (falha.get() != null) break;
        }

        for (Thread t : threads) {
            t.join(3000);
        }

        if (falha.get() != null) {
            for (File f : partes) {
                //noinspection ResultOfMethodCallIgnored
                f.delete();
            }
            throw new IOException(falha.get());
        }

        /* junta as partes, na ordem, dentro do arquivo final */
        try (OutputStream saida = ctx.getContentResolver().openOutputStream(destino)) {
            if (saida == null) throw new IOException("Não consegui gravar o arquivo");
            byte[] buffer = new byte[128 * 1024];
            for (File parte : partes) {
                try (InputStream is = new BufferedInputStream(
                        new java.io.FileInputStream(parte), 128 * 1024)) {
                    int n;
                    while ((n = is.read(buffer)) > 0) {
                        saida.write(buffer, 0, n);
                    }
                }
                //noinspection ResultOfMethodCallIgnored
                parte.delete();
            }
        }
    }

    /** Baixa um pedaço (Range) para um arquivo temporário. */
    private void baixarParte(String url, long inicio, long fim, File destino,
                             java.util.concurrent.atomic.AtomicLong contador) throws IOException {
        HttpURLConnection conexao = abrirConexao(url);
        conexao.setRequestProperty("Range", "bytes=" + inicio + "-" + fim);

        try (InputStream entrada = new BufferedInputStream(conexao.getInputStream(), 128 * 1024);
             OutputStream saida = new java.io.BufferedOutputStream(
                     new FileOutputStream(destino), 128 * 1024)) {
            byte[] buffer = new byte[128 * 1024];
            int n;
            while ((n = entrada.read(buffer)) > 0) {
                if (cancelarSolicitado) throw new IOException("Download cancelado");
                saida.write(buffer, 0, n);
                contador.addAndGet(n);
            }
        } finally {
            conexao.disconnect();
        }
    }

    /**
     * Descobre o tamanho do áudio sem baixar tudo: pede 1 byte e lê o
     * `Content-Range` ("bytes 0-0/12345678").
     */
    private long descobrirTamanho(String url) {
        try {
            HttpURLConnection conexao = abrirConexao(url);
            conexao.setRequestProperty("Range", "bytes=0-0");
            int codigo = conexao.getResponseCode();

            if (codigo == 206) {
                String faixa = conexao.getHeaderField("Content-Range");
                if (faixa != null && faixa.contains("/")) {
                    long tamanho = Long.parseLong(faixa.substring(faixa.indexOf('/') + 1).trim());
                    conexao.disconnect();
                    return tamanho;
                }
            }
            long tamanho = conexao.getContentLengthLong();
            conexao.disconnect();
            return tamanho;
        } catch (Exception e) {
            Log.w(TAG, "tamanho: " + e.getMessage());
            return -1;
        }
    }

    private InputStream abrirStream(String url) throws IOException {
        HttpURLConnection conexao = abrirConexao(url);
        return new BufferedInputStream(conexao.getInputStream(), 128 * 1024);
    }

    /** Conexão padronizada (UA de navegador + sem compressão, senão o Range quebra). */
    private HttpURLConnection abrirConexao(String url) throws IOException {
        HttpURLConnection conexao = (HttpURLConnection) new URL(url).openConnection();
        conexao.setConnectTimeout(20000);
        conexao.setReadTimeout(30000);
        conexao.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Linux; Android 12) AppleWebKit/537.36 (KHTML, like Gecko) "
                        + "Chrome/110.0 Mobile Safari/537.36");
        conexao.setRequestProperty("Accept", "*/*");
        /* sem compressão: precisamos dos bytes exatos para o Range funcionar */
        conexao.setRequestProperty("Accept-Encoding", "identity");
        return conexao;
    }

    private void baixarCapa(String url, String id) throws IOException {
        HttpURLConnection conexao = (HttpURLConnection) new URL(url).openConnection();
        conexao.setConnectTimeout(15000);
        conexao.setReadTimeout(15000);
        try (InputStream entrada = conexao.getInputStream();
             FileOutputStream saida = new FileOutputStream(
                     new File(library.pastaCapas(), id + ".jpg"))) {
            byte[] buffer = new byte[16 * 1024];
            int n;
            while ((n = entrada.read(buffer)) > 0) {
                saida.write(buffer, 0, n);
            }
        }
    }

    static String artistaAntesDoSeparador(String titulo) {
        if (titulo == null) return "";
        int separador=titulo.indexOf(" - ");
        if (separador <= 0) return "";
        String artista=titulo.substring(0,separador).trim();
        /* Colaborações continuam na faixa, mas não criam outra pasta. */
        artista=artista.replaceFirst("(?i)\\s*,.*$", "")
                .replaceFirst("(?i)\\s+&.*$", "")
                .replaceFirst("(?i)\\s+(feat\\.?|ft\\.?|featuring)\\s+.*$", "")
                .trim();
        return artista.length() > 80 ? artista.substring(0,80).trim() : artista;
    }

    /** Converte artista/canal em nome seguro e estável de pasta. */
    static String nomePasta(String texto) {
        if (texto == null || texto.trim().isEmpty()) return "OUTROS";
        String limpo = texto.trim().replaceAll("[\\/:*?\"<>|]", "_")
                .replaceAll("[\\p{Cntrl}]", "")
                .replaceAll("\\s+", " ").trim();
        if (limpo.length() > 45) limpo = limpo.substring(0,45).trim();
        /* Pasta canônica: diferenças só de maiúsculas/minúsculas não criam
           diretórios duplicados no armazenamento. */
        return limpo.isEmpty() ? "OUTROS" : limpo.toUpperCase(Locale.ROOT);
    }

    /** Nome de arquivo seguro (sem barras, dois-pontos nem caracteres de controle). */
    static String nomeSeguro(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "audio";
        }
        String limpo = texto.replaceAll("[\\\\/:*?\"<>|]", "_")
                .replaceAll("[\\p{Cntrl}]", "")
                .replaceAll("\\s+", " ")
                .trim();
        if (limpo.length() > 90) {
            limpo = limpo.substring(0, 90).trim();
        }
        return limpo.isEmpty() ? "audio" : limpo;
    }
}
