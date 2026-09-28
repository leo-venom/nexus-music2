package com.leo.nexusmusic2;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * NEXUS MUSIC — app autônomo (100% no celular).
 *
 * Não precisa de computador nem de servidor externo:
 *   1. o app lê a biblioteca de músicas DO CELULAR (MediaStore);
 *   2. baixa do YouTube direto pela internet (NewPipe Extractor) — funciona
 *      em qualquer rede, inclusive dados móveis;
 *   3. serve a interface e os arquivos por um servidor HTTP local em
 *      127.0.0.1, que é o que o WebView carrega.
 */
public class MainActivity extends Activity {

    private static final String TAG = "NexusMusic";
    private static final int PEDIDO_PERMISSAO = 42;

    private WebView webView;
    private NexusServer servidor;
    private Library biblioteca;
    private YtDownload baixador;

    private View fullscreenView;
    private WebChromeClient.CustomViewCallback fullscreenCallback;
    private long ultimoToqueVoltar = 0L;
    private boolean jsPronto = false;
    private String jsPendente = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Redimensiona a janela quando o teclado aparece para que a Home
           possa subir e o campo de pesquisa do YouTube permaneça visível. */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        /* o PlayerService precisa desta referência para mandar os
           controles da tela de bloqueio / fone para o JavaScript */
        ativa = new WeakReference<>(this);

        /* é um player: mantém a tela acesa */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        biblioteca = new Library(this);
        baixador = new YtDownload(this, biblioteca);

        /* sobe o servidor local e só então carrega a interface */
        try {
            servidor = new NexusServer(this, biblioteca, baixador);
            int porta = servidor.iniciar();
            Log.i(TAG, "porta local: " + porta);
            montarWebView("http://127.0.0.1:" + porta + "/");
        } catch (Exception e) {
            Log.e(TAG, "falha subindo o servidor: " + e);
            Toast.makeText(this, "Falha ao iniciar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        pedirPermissoes();
    }

    // ------------------------------------------------------------------ //
    //  WebView
    // ------------------------------------------------------------------ //

    private void montarWebView(String endereco) {
        webView = new WebView(this);

        WebSettings cfg = webView.getSettings();
        cfg.setJavaScriptEnabled(true);
        cfg.setDomStorageEnabled(true);
        cfg.setDatabaseEnabled(true);
        cfg.setMediaPlaybackRequiresUserGesture(false);   /* toca sozinho */
        cfg.setBuiltInZoomControls(false);
        cfg.setDisplayZoomControls(false);
        cfg.setLoadWithOverviewMode(true);
        cfg.setUseWideViewPort(true);
        cfg.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        cfg.setAllowFileAccess(false);                    /* tudo vem do servidor local */
        cfg.setAllowContentAccess(true);

        webView.setBackgroundColor(0xFF000000);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setScrollbarFadingEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);

        /* O app NÃO se comporta como página web (a pedido, 19/09/2026):
           segurar o dedo não abre o menu de "copiar/selecionar" nem vibra.
           O realce azul do toque é removido no CSS (-webkit-tap-highlight-color). */
        webView.setLongClickable(false);
        webView.setHapticFeedbackEnabled(false);
        webView.setOnLongClickListener(v -> true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                jsPronto = true;
                enviarJsPendente();
            }
        });

        /* Ponte para o "copiar chave PIX": dentro do WebView o
           navigator.clipboard pode ser bloqueado pelo sistema, então
           expomos um caminho nativo que sempre funciona. O JS tenta
           esta ponte antes de cair no fallback do navegador. */
        webView.addJavascriptInterface(new PonteClipboard(), "AndroidClip");

        /* Persistência nativa do estado: localStorage pode não ser
           sincronizado quando o processo é morto pelo gerenciador de tarefas. */
        webView.addJavascriptInterface(new PonteEstado(), "AndroidState");

        /* Ponte da mídia: a interface avisa o Android o que está tocando,
           e o PlayerService devolve os controles (tela bloqueada/fone). */
        webView.addJavascriptInterface(new PonteMedia(), "AndroidMedia");

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (fullscreenView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                fullscreenView = view;
                fullscreenCallback = callback;
                webView.setVisibility(View.GONE);
                ((FrameLayout) findViewById(android.R.id.content)).addView(
                        fullscreenView, new FrameLayout.LayoutParams(-1, -1));
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            }

            @Override
            public void onHideCustomView() {
                sairDaTelaCheia();
            }
        });

        /* O botão "⬇ DOWNLOAD" dos cards entrega o MP3 aqui: usamos o
           DownloadManager, então o arquivo vai para Downloads/NexusMusic/. */
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {
                try {
                    String nome = URLUtil.guessFileName(url, contentDisposition, mimeType);
                    DownloadManager.Request pedido = new DownloadManager.Request(Uri.parse(url));
                    pedido.setMimeType(mimeType);
                    pedido.addRequestHeader("User-Agent", userAgent);

                    String cookies = CookieManager.getInstance().getCookie(url);
                    if (cookies != null) {
                        pedido.addRequestHeader("Cookie", cookies);
                    }

                    pedido.setTitle(nome);
                    pedido.setDescription("NEXUS MUSIC");
                    pedido.setNotificationVisibility(
                            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    pedido.setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS, "NexusMusic/" + nome);

                    DownloadManager gerenciador =
                            (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    if (gerenciador != null) {
                        gerenciador.enqueue(pedido);
                        Toast.makeText(MainActivity.this, "Baixando: " + nome,
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Falha ao baixar",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        setContentView(webView);
        webView.loadUrl(endereco);
    }

    private void sairDaTelaCheia() {
        if (fullscreenView == null) return;
        ((FrameLayout) fullscreenView.getParent()).removeView(fullscreenView);
        fullscreenView = null;
        webView.setVisibility(View.VISIBLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        if (fullscreenCallback != null) {
            fullscreenCallback.onCustomViewHidden();
            fullscreenCallback = null;
        }
    }

    // ------------------------------------------------------------------ //
    //  Permissões + biblioteca
    // ------------------------------------------------------------------ //

    private void pedirPermissoes() {
        List<String> faltando = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                faltando.add(Manifest.permission.READ_MEDIA_AUDIO);
            }
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                faltando.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            faltando.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            faltando.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (faltando.isEmpty()) {
            varrerBiblioteca();
            return;
        }
        requestPermissions(faltando.toArray(new String[0]), PEDIDO_PERMISSAO);
    }

    @Override
    public void onRequestPermissionsResult(int codigo, String[] permissoes, int[] resultados) {
        super.onRequestPermissionsResult(codigo, permissoes, resultados);
        if (codigo != PEDIDO_PERMISSAO) return;

        boolean algumOk = false;
        for (int r : resultados) {
            if (r == PackageManager.PERMISSION_GRANTED) algumOk = true;
        }

        if (algumOk) {
            varrerBiblioteca();
        } else {
            Toast.makeText(this,
                    "Sem permissão para ler as músicas do celular.\n"
                            + "O app ainda pode baixar do YouTube.",
                    Toast.LENGTH_LONG).show();
        }
    }

    /** Varre o MediaStore num thread (pode demorar com biblioteca grande). */
    private void varrerBiblioteca() {
        new Thread(() -> {
            biblioteca.recarregar();
            runOnUiThread(() -> {
                if (webView != null) {
                    webView.evaluateJavascript("loadLib();", null);   /* atualiza sem piscar a tela */
                }
            });
        }, "nexus-scan").start();
    }

    // ------------------------------------------------------------------ //
    //  Ciclo de vida
    // ------------------------------------------------------------------ //

    @Override
    protected void onResume() {
        super.onResume();
        enviarJsPendente();
        /* voltou ao app: procura música nova (downloads, arquivos copiados) */
        if (biblioteca != null) {
            new Thread(biblioteca::recarregar, "nexus-scan").start();
        }
    }

    @Override
    protected void onPause() {
        /* O Android chama onPause antes de deixar a Activity em segundo
           plano. Solicita uma gravação imediata no WebView, cobrindo o
           caminho em que o gerenciador de tarefas não entrega onBackPressed. */
        if (webView != null) {
            try {
                webView.evaluateJavascript(
                        "if (typeof nx_salvarEstado === 'function') { nx_salvarEstado(); }",
                        null);
            } catch (Exception e) {
                Log.w(TAG, "salvamento de estado em onPause: " + e);
            }
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // App minimized ou prestes a ser destruída.
        // O PlayerService continua rodando enquanto o app ficar em segundo
        // plano (música não interrompida). Só é parado em onDestroy() ou
        // quando o usuário confirma o fechamento por back.
    }

    @Override
    protected void onDestroy() {

        ativa.clear();

        if (servidor != null) {
            servidor.parar();
            servidor = null;
        }

        /* Para TODO o áudio do WebView antes de destruir — mesmo quando o app
           é fechado pelo gerenciador de tarefas (swipe nos recentes), o
           WebView ainda está vivo aqui e o áudio pode ser parado.
           `nx_pararTodoAudio()` pausa o player principal E as prévias do
           YouTube; antes só `audio` era pausado e as prévias continuavam
           tocando depois de fechar o app. */
        if (webView != null) {
            try {
                webView.evaluateJavascript(
                        "if (typeof nx_pararTodoAudio === 'function') { nx_pararTodoAudio(); }"
                                + " else if (typeof audio !== 'undefined' && audio) { audio.pause(); }",
                        null);
            } catch (Exception e) {
                Log.w(TAG, "pausa do audio em onDestroy: " + e);
            }
        }

        /* Se o PlayerService ainda estiver vivo, encerra.
           Isso cobrem dois cenários:
             • o usuário confirmou o fechamento por back (já parou antes,
               mas o call é idempotente — stopService com serviço já parado
               não faz mal);
             • o sistema matou o app (swipe no recents, baixa memória) sem
               passar por onBackPressed — aqui é a última chance de limpar. */
        PlayerService.encerrar(this);

        webView = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (fullscreenView != null) {
            sairDaTelaCheia();
            return;
        }

        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return;
        }

        /* Já na página inicial: confirmação antes de fechar.
           O único momento em que o app vai para o segundo plano é quando
           o usuário minimiza (home / gesto de recentes sem fechar) — nesse
           caso o PlayerService continua tocando. Aqui, com a confirmação,
           o app é encerrado de verdade. */
        long agora = SystemClock.elapsedRealtime();
        if (agora - ultimoToqueVoltar < 2000) {
            /* 1. parar TODO o áudio do WebView antes de fechar — senão o HTML
               continua tocando mesmo com o serviço parado. Inclui o player
               principal E as prévias do YouTube. */
            if (webView != null) {
                try {
                    webView.evaluateJavascript(
                            "if (typeof nx_pararTodoAudio === 'function') { nx_pararTodoAudio(); }"
                                    + " else if (typeof audio !== 'undefined' && audio) { audio.pause(); }",
                            null);
                } catch (Exception e) {
                    Log.w(TAG, "pausa do audio falhou: " + e);
                }
            }
            /* 2. parar o PlayerService (tira a notificação e a MediaSession). */
            PlayerService.encerrar(this);
            super.onBackPressed();
        } else {
            ultimoToqueVoltar = agora;
            Toast.makeText(this, "Toque em voltar de novo para sair",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Persistência nativa do último estado conhecido do player.
     * O WebView grava localStorage de forma assíncrona; em um swipe no
     * gerenciador de tarefas o processo pode morrer antes dessa gravação.
     */
    private class PonteEstado {
        private final android.content.SharedPreferences prefs =
                getSharedPreferences("nexus_music_state", MODE_PRIVATE);

        @JavascriptInterface
        public void salvar(String url, String viewJson) {
            android.content.SharedPreferences.Editor editor = prefs.edit()
                    .putString("last_view", viewJson == null ? "" : viewJson);
            if (url != null && !url.trim().isEmpty()) {
                editor.putString("last_url", url);
            }
            editor.commit();
        }

        @JavascriptInterface
        public String lerUrl() {
            return prefs.getString("last_url", "");
        }

        @JavascriptInterface
        public String lerView() {
            return prefs.getString("last_view", "");
        }
    }

    /**
     * Copia texto para a área de transferência a pedido da interface.
     * Exposta ao JavaScript como `AndroidClip.copiar(texto)`.
     */
    private class PonteClipboard {

        @JavascriptInterface
        public void copiar(final String texto) {
            if (texto == null || texto.trim().isEmpty()) {
                return;
            }
            runOnUiThread(() -> {
                ClipboardManager area =
                        (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (area != null) {
                    area.setPrimaryClip(ClipData.newPlainText("PIX NEXUS MUSIC", texto.trim()));
                }
            });
        }
    }


    /** Instância viva da Activity — referência fraca para não vazar Context. */
    private static WeakReference<MainActivity> ativa = new WeakReference<>(null);

    /** Reenvia o último comando recebido enquanto o WebView estava indisponível. */
    private void enviarJsPendente() {
        if (!jsPronto || jsPendente == null || webView == null) return;
        String comando = jsPendente;
        jsPendente = null;
        try {
            webView.evaluateJavascript(comando, null);
        } catch (Exception e) {
            jsPendente = comando;
            Log.w(TAG, "reenvio de controle: " + e);
        }
    }

    /** Executa JavaScript na interface (chamado pelo PlayerService). */
    public static void chamarJs(final String js) {

        final MainActivity instancia = ativa.get();

        if (instancia == null || instancia.webView == null) {
            return;
        }

        instancia.runOnUiThread(() -> {
            if (!instancia.jsPronto) {
                instancia.jsPendente = js;
                return;
            }
            try {
                instancia.webView.evaluateJavascript(js, null);
            } catch (Exception e) {
                instancia.jsPendente = js;
                Log.w("NexusMusic", "chamarJs: " + e);
            }
        });
    }

    /**
     * Recebe da interface o que está tocando e repassa ao PlayerService
     * (que publica na tela de bloqueio, na notificação e nos botões do fone).
     */
    private class PonteMedia {

        @JavascriptInterface
        public void atualizar(final String titulo, final String artista, final boolean tocando) {
            runOnUiThread(() -> {
                try {
                    PlayerService.atualizar(
                            MainActivity.this,
                            titulo,
                            artista,
                            tocando);
                } catch (Exception e) {
                    Log.w("NexusMusic", "atualizar midia: " + e);
                }
            });
        }
    }

}
