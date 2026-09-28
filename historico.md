# Histórico — NEXUS MUSIC 2

## v1.21 — vídeo personalizado persistente

- Vídeos antigos removidos; ficou um vídeo padrão dentro do APK.
- O botão virou **ADICIONAR SEU VÍDEO**.
- O vídeo escolhido é copiado para `files/NexusMusic2/videos/custom.mp4`.
- Na próxima abertura, `/api/effects` consulta primeiro o vídeo privado; o vídeo personalizado
  permanece como padrão.
- O áudio não é interrompido e a tela não é recarregada ao adicionar/trocar vídeo.
- Capa reduzida para 58×58 px no canto inferior esquerdo.
- APK v1.21: build, assinatura, Activity, JavaScript e CSS verificados.

## v1.22 — título no player e slider condicional

- Removida a capa do álbum do player.
- Título/artista reposicionados abaixo do vídeo, no canto inferior esquerdo.
- Título colorido em ciano.
- Marquee ativado somente quando o texto excede a largura real; usa `scrollWidth`, `clientWidth` e `transform`.
- Build release e verificação do APK concluídas.

## v1.26 — pastas automáticas por gênero do YouTube

- `YtDownload` consulta `StreamExtractor.getCategory()`.
- O caminho passou de `Music/NexusMusic/YOUTUBE/` para `Music/NexusMusic/YOUTUBE/<gênero>/`.
- A pasta somente é criada pelo MediaStore no primeiro download daquele gênero.
- Fallback `OUTROS` quando o YouTube não fornece categoria.
- Build release e verificação do APK concluídas.

## v1.27 — correção do caminho de gênero no MediaStore

- Corrigida a barra final do `RELATIVE_PATH`, necessária para o Android criar a subpasta.
- Corrigida a leitura da biblioteca: `YOUTUBE` é contêiner e o gênero real vem da pasta seguinte.
- Build release e assinatura verificados.

## v1.29 — MediaStore volume externo primário

- A coleção legada foi substituída por `VOLUME_EXTERNAL_PRIMARY`.
- `RELATIVE_PATH` é reafirmado enquanto o item está pendente.
- O destino calculado é registrado no log nativo para diagnóstico no aparelho.

## v1.30 — organização por artista/canal real

- A categoria genérica do YouTube foi substituída pelo canal retornado por `getUploaderName()`.
- O título não é usado para adivinhar artista.
- Nomes de pasta são normalizados em maiúsculas para evitar duplicação por capitalização.
- Destino: `Music/NexusMusic/YOUTUBE/<ARTISTA>/`.

## v1.31 — caminho canônico da pasta YouTube

- Eliminada a divergência entre `Music/...` e `Environment.DIRECTORY_MUSIC`.
- Um único `pastaRelativa` é usado tanto no insert quanto na confirmação do MediaStore.

## v1.32 — YOUTUBE como pasta principal na biblioteca

- O parser deixou de expor o artista como gênero/pasta principal.
- Qualquer caminho `YOUTUBE/<artista>/faixa` agora é agrupado em `YOUTUBE` na interface.

## v1.34 — regra final: pasta somente pelo artista

- Corrigida a tentativa intermediária que usava o título completo como pasta.
- A pasta volta a ser derivada exclusivamente de `getUploaderName()`.
- O título permanece apenas no nome do arquivo.

## v1.35 — subpastas de artistas dentro de YOUTUBE

- A biblioteca lê `YOUTUBE/<ARTISTA>/` e usa o artista como álbum/subpasta visual.
- A tela YOUTUBE deixou de listar todas as faixas diretamente na raiz.
- O usuário pode entrar na subpasta do artista e ver somente suas músicas.

## v1.36 — artista extraído do título quando há separador

- Para títulos `Artista - Música`, a pasta usa o trecho antes de ` - `.
- O canal/uploader continua apenas como fallback.
- Corrige canais agregadores como `WORLDSTARHIPHOP`.

## v1.37 — normalização de colaborações

- Vírgula, `&`, `feat.`, `ft.` e `featuring` agora separam o artista principal dos convidados para fins de pasta.
- Título e nome do arquivo não são alterados.

## v1.49 — downloads YouTube diretamente em YOUTUBE

- Removida a criação de subpastas por artista/canal no download.
- O destino voltou a ser diretamente `Music/NexusMusic/YOUTUBE/`.
- O artista/canal continua salvo apenas nos metadados da faixa.
- Android 10+: `RELATIVE_PATH` usa somente `Music/NexusMusic/YOUTUBE/`.
- Android 9 ou anterior: destino usa somente `Music/NexusMusic/YOUTUBE`.
- APK: `NEXUS-MUSIC-2-v1.49.apk`.
- VersionCode: `47`; VersionName: `1.49`.
- SHA-256: `c15f6cdf85e4827054c3deb89d1c72510c522c816a3849fe96223b3e4b20715b`.

## v1.66–v1.70 — refinamentos de áudio e busca

- **v1.66:** player principal e prévias do YouTube passaram a ser exclusivos; iniciar um interrompe o outro.
- **v1.66:** blur removido do campo de busca local e transferido para o painel externo da busca do YouTube; input e resultados ficaram sem blur direto para evitar piscadas.
- **v1.67:** fade aplicado somente às trocas manuais; a troca automática pelo evento `ended` preservou o fluxo anterior, já aprovado no celular.
- **v1.68:** proteção contra áudio silencioso após troca manual: recuperação de `play()`, evento `playing` e fallback para restaurar o volume-alvo quando o WebView suspender `requestAnimationFrame`.
- **v1.69:** intervalo de **300 ms** entre o fade-out e o início da nova faixa manual. Leo confirmou que eliminou os ruídos no fone.
- **v1.70:** intervalo manual ajustado para **500 ms**. Leo confirmou que ficou excelente e suave no fone; esta passou a ser a configuração aprovada.
- A troca automática continua sem intervalo adicional, pois já estava suave.
- Regra consolidada: não alterar funcionalidades; refinamentos de áudio devem preservar o volume escolhido e não afetar a troca automática.
- APK v1.70: 9.233.268 bytes, SHA-256 `3d7d9359d136619eca93136fb711c095072bfbad2995f16304b5e666dcb1c11e`.
- Data do registro: 18/09/2026.

## v1.76 — persistência reforçada e controles de mídia em segundo plano

- Estado da última música e pasta passou a ser salvo também em `SharedPreferences` nativo, com `commit()` síncrono.
- Salvamento adicional no `onPause()`, `visibilitychange`, `pagehide` e `beforeunload`.
- A música passou a ser persistida pelo caminho `/music/<id>`, ignorando host/porta do servidor local durante a restauração.
- Corrigido o caso em que a porta local mudava e a música salva parecia inexistente.
- Controles da `MediaSession` receberam explicitamente `FLAG_HANDLES_MEDIA_BUTTONS` e `FLAG_HANDLES_TRANSPORT_CONTROLS`.
- Comandos recebidos pela tela bloqueada, notificação ou fone Bluetooth passaram a aguardar o WebView ficar pronto e são reenviados no `onPageFinished()`/`onResume()`.
- Corrigada troca real de faixa em tela bloqueada: `requestAnimationFrame()` podia ser suspenso no fade-out; foi adicionado fallback temporizado de 360 ms para garantir a troca do `audio.src`.
- Validação no celular confirmou troca de música pelo fone Bluetooth com a tela bloqueada e pelo player da tela bloqueada.
- APK validado: `NEXUS-MUSIC-2-v1.76-bluetooth-fix-2.apk`.
- SHA-256: `0819e14ce9b65d9ad070e0155381caa53c31038d6e2134410d5590ceceac0117`.
- Tamanho: `9.236.150 bytes`.
- Build release, JavaScript, CSS, pacote, Activity e assinatura verificados.
- Data do registro: 18/09/2026.

## 19/09/2026 — plexus orgânico aprovado

- O plexus foi refinado após várias iterações até deixar de parecer uma animação constante e passar a reagir claramente aos ataques de som.
- O Leo aprovou: *“parece uma coisa viva”*.
- A solução final separa grave/agudo, detecta deltas entre frames, aplica pulsos com decaimento, mantém pontos fixos e normaliza o movimento por delta time.
- O último APK refinado foi compilado, validado com `node --check`, `BUILD SUCCESSFUL` e comparação SHA-256 do asset empacotado.
- APK aprovado: `NEXUS-MUSIC-2-v1.76-plexus-sincronismo-refinado.apk`.
- SHA-256: `9a07f007fb898f3cba34803ce66b23925e4cfaf855f5f184d5d6d92734448613`.
- O visual do plexus fica congelado; futuras mudanças somente por pedido explícito.



## Estado aprovado — box Plexus 450×200 — 19/09/2026

- Foto de capa removida do player.
- Visualizador Plexus em `#fxScreen`, com canvas interno.
- Box e canvas: **450×200 px**; no mobile, largura responsiva até 450 px.
- Bordas externas e contorno luminoso removidos.
- Pontos atuais: **50**.
- Alcance-base atual: **70 px**, preservando grave/agudo e pulso de batida.
- Título, artista, controles, progresso e volume permanecem no fluxo original abaixo do box.
- APK entregue: `NEXUS-MUSIC-2-v1.76-plexus-box-450x200.apk`.
- Build: `BUILD SUCCESSFUL`; `node --check`: aprovado.
- SHA-256 do APK: `035dd2bd168b1a7d5aa780f26679b53dba5bb08ffe56db941e09bc4eab5f0890`.

## v1.102–v1.106 — refinamentos de visor, home e regressão das telas internas (19/09/2026)

- **v1.102–v1.104:** VU meter reajustado na base do box; removidos o bloco `FLAC / 44.1KHz` e o contador regressivo de tempo do visor.
- **v1.105:** relógio principal do visor (`retroTime`) passa a mostrar o **tempo decorrido da faixa** (`audio.currentTime`), e não mais a hora do sistema.
- **v1.106:** removida da Home a vitrine "recentemente adicionadas" (`tracksSection` oculta) e removidos gêneros/álbuns da Home.
- **🚨 Regressão introduzida na v1.106:** ao ocultar a `tracksSection` na Home, nenhuma view interna voltou a exibi-la — **PASTAS, ÁLBUNS, ARTISTAS, FAVORITOS e BUSCA ficaram com a lista invisível** (cards no DOM, seção com 0 px de altura).
- **v1.107 — correção:** `renderTracks()`, `renderArtists()`, `renderAlbums()` e `renderFolders()` agora reexibem a `tracksSection` (`display=''`). Favoritos e busca são cobertos por `renderTracks()`.
- **Regra:** a Home é a única tela que oculta a `#tracksSection`; toda view interna precisa reexibi-la.
- A Home termina no box **BAIXAR DO YOUTUBE**; o rodapé (marca + ♥ APOIAR/PIX) permanece, a pedido do Leo.
- `assets/index.html.bak-home-clean-20260919-1318` estava sendo empacotado no APK (195 KB) e foi movido para `_backup/`.
- APK validado: `NEXUS-MUSIC-2-v1.107-home-limpa-albuns-pastas-favoritos.apk`.
- VersionCode: `105`; VersionName: `1.107`; Tamanho: `1.446.637 bytes`.
- SHA-256: `348597d6d147d134f9f92dfd72ac987eddf097625d79ff1792d1a0ac4ff94875`.
- Validação: `node --check`, CSS 448/448, `BUILD SUCCESSFUL`, `unzip -tq`, assinatura, Activity e asset empacotado idêntico ao fonte.
- Data do registro: 19/09/2026.

## v1.108 — o app sempre abre na página principal (19/09/2026)

- **Revertida** a restauração automática de estado na abertura do app.
- Antes: `loadLib()` → `nx_restaurarEstado()` reabria a pasta salva **e** dava `playTrack()` na última música sozinho.
- Agora: o app **sempre** abre na Home limpa — fila vazia, nada tocando, box do YouTube e rodapé no lugar.
- O salvamento de estado continua ativo, mas não é mais usado para reabrir a tela; `nx_restaurarEstado()` segue intacta no código (basta descomentar a chamada para reativar).
- Favoritos, Bluetooth/tela bloqueada, `PlayerService` e download do YouTube não foram alterados.
- Validação com estado salvo de propósito (`/music/5` + view `genero/ROCK`): `currentView=inicio`, `audio.src` vazio, `paused=true`, `queue.length=0`, visor "Nenhuma música".
- APK validado: `NEXUS-MUSIC-2-v1.108-sempre-abre-na-home.apk`.
- VersionCode: `106`; VersionName: `1.108`; Tamanho: `1.446.802 bytes`.
- SHA-256: `3368b02d3d70e7f921a81a9379bb5132e2ab0a9b3d3deae499492ad9d96da8a5`.
- Data do registro: 19/09/2026.

## v1.109 — tema GLASS BLACK + VERDE (19/09/2026)

- Tema refeito por cima do existente, em **camada isolada** no fim do CSS (removível): preto puro, painéis de vidro, acento verde do VU e botões modernos no lugar das teclas retrô 3D.
- Botões do player agora **redondos** (play sólido verde); toolbar ÁLBUNS/PASTAS/FAVORITOS em glass com glow verde no hover ativo.
- Barra de progresso e fader de volume em **verde**, com knob circular; visor com moldura e textos verdes (título/subtítulo saíram do laranja).
- Defeitos antigos corrigidos: `var(--blue)`, `var(--pink)` e `var(--vol)` **não existiam** (volume e progresso ficavam sem cor válida) e `--green`/`--green2` apontavam para rosa/ciano.
- Armadilha registrada: as regras originais usam seletor de **duas classes** — um seletor de uma classe perde mesmo com `!important`; por isso o bloco de correção de especificidade.
- `backdrop-filter` mantido apenas nos painéis grandes (blur em 28 cards derrubava para ~10 FPS).
- Varredura de cor magenta no DOM: 18 ocorrências → **0**.
- APK validado: `NEXUS-MUSIC-2-v1.109-tema-glass-black-verde.apk`.
- VersionCode: `107`; VersionName: `1.109`; Tamanho: `1.450.120 bytes`.
- SHA-256: `fdec1e6116f8d47c1d9f8ab958212d4947be6bc8b56852335ffbb71841122491`.
- Data do registro: 19/09/2026.

### ✅ APROVADO — 19/09/2026

- Leo aprovou o tema: *"Ficou lindo"*.
- **Tema GLASS BLACK + VERDE congelado.** Mudanças visuais a partir daqui somente por pedido explícito —
  sem refino especulativo.
- Ajustes oferecidos e **não** aplicados (ficam disponíveis quando/se ele pedir):
  1. cantos retos (`--raio:0`);
  2. ícones de pasta em verde (hoje é emoji 📁 amarelo);
  3. sobreposição do texto "NEXUS MUSIC" sobre o título quando a música está vazia (defeito pré-existente).

## v1.110 — progresso dentro do campo + rodapé fixo (19/09/2026)

- **Barra de progresso do download agora vive DENTRO do campo de pesquisa do YouTube:** trilho de 3px correndo na base interna do input e percentual à direita, dentro dele. O `.yt-bar` saiu do meio do painel e foi para dentro do `.yt-row`; os elementos continuam os mesmos `#ytFill` / `#ytPct`, então a função `ytBarra()` **não mudou**.
- Fase de conversão mantém as listras indeterminadas — agora **verdes** (antes azul/rosa).
- **Rodapé compacto e fixo embaixo:** de bloco grande (padding 24/34px + margem 46px) para faixa de **32px** colada na base, com vidro, borda superior verde e sempre visível. `♥ APOIAR` continua no lugar, menor.
- `.content` ganhou `padding-bottom:64px` para o rodapé fixo não esconder o fim da lista.
- Validado no navegador: barra **dentro** do input (geometria conferida), preenchimento em 37% com `transition` respeitada, animação `ytStripe` na conversão, rodapé `position:fixed` com altura 32px colado em `bottom:0`, último card com 32px de folga acima do rodapé.
- APK validado: `NEXUS-MUSIC-2-v1.110-progresso-no-input-rodape-fixo.apk`.
- VersionCode: `108`; VersionName: `1.110`; Tamanho: `1.451.071 bytes`.
- SHA-256: `738380139dcbcdc2246d654e745c0a713caa2c20ba4d2106777e5449d53d9797`.
- Data do registro: 19/09/2026.

## v1.111 — o próprio campo É a barra de progresso (19/09/2026)

- Feedback do Leo após a v1.110: *"a barra não [está] exatamente dentro visível; ela não se vê quando o download é feito"* — o fio de 3px na base do input era discreto demais no celular.
- **Nova abordagem:** o input ficou transparente (z-index 2) e a **cápsula do campo virou a própria barra** (`.yt-bar` com `inset:0`, z-index 1). O preenchimento verde cresce **por trás do texto**, da esquerda para a direita, até encher o campo inteiro — com brilho interno.
- O percentual (`#ytPct`) ficou maior/em negrito à direita, com sombra para leitura sobre o verde.
- Conversão: as listras indeterminadas correm **dentro do campo** (verdes, mais transparentes para não atrapalhar a leitura).
- **JavaScript intocado:** continuam sendo `#ytFill` / `#ytPct` e a mesma `ytBarra()`.
- Validado no navegador: cápsula alinhada ao campo (mesma posição/tamanho, 46px), preenchimento em 45% ocupando a altura total, campo com fundo `rgba(0,0,0,0)`.
- APK validado: `NEXUS-MUSIC-2-v1.111-progresso-no-campo.apk`.
- VersionCode: `109`; VersionName: `1.111`; Tamanho: `1.451.252 bytes`.
- SHA-256: `1c83db201055d9a2f62b161f4689cc5b6c04f747fa192fbbe06d35189d56fb96`.
- Data do registro: 19/09/2026.

## v1.112 — busca com resultado único, prévia automática e play após o download (19/09/2026)

Pedido do Leo:
> *"A pesquisa só mostra um resultado, com foto menor do lado esquerdo e informações do lado direito. Não ter mais botão prévia — assim que a pesquisa aparecer já toca automaticamente. Adicionar botão download abaixo. Quando o download é feito, já toca automaticamente no player principal."*

- **Resultado único:** a busca renderiza só o primeiro resultado — capa de **88×54** à esquerda, título/canal/duração à direita e o botão **⬇ BAIXAR** abaixo, ocupando a largura.
- **Sem botão PRÉVIA:** a prévia começa **sozinha** quando o resultado aparece. O card inteiro virou o controle (toque = pausar/retomar) e fica com **borda e título verdes** enquanto toca. `ytPreviaResultado(indice, botao)` passou a aceitar `botao = null`.
- **Play automático após o download:** no `ytPoll`, quando a fase é `concluido`, depois de recarregar a biblioteca a nova função **`ytTocarBaixada(titulo)`** procura a música (título idêntico → contém → a mais recente, como rede de segurança), monta a fila com a biblioteca e chama `playTrack()`.
- **Barra do campo volta a zero** em cada nova pesquisa (antes ficava verde cheia, herdada do download anterior).
- **Autoplay tratado:** se o WebView/navegador recusar o `play()` (política de autoplay), a mensagem técnica em inglês foi trocada por *"toque no card para ouvir a prévia"*. No APK o `MainActivity` usa `setMediaPlaybackRequiresUserGesture(false)`, então a prévia automática toca.
- **Validação ponta a ponta no navegador** (com servidor de teste simulando busca/prévia/download): 1 card no DOM, sem botão de prévia, prévia tocando sozinha com o card marcado, download concluído e a música **tocando no player principal** (fila 25, `qIndex` 0, `audio.paused = false`), título no visor atualizado.
- APK validado: `NEXUS-MUSIC-2-v1.112-busca-resultado-unico.apk`.
- VersionCode: `110`; VersionName: `1.112`; Tamanho: `1.452.993 bytes`.
- SHA-256: `901f9d175d960c9bc5bd4c1a8f534d1fdba574457746aaa4d89daf1c37af97f7`.
- SHA-256 do asset: `c173fad6df9c5fc60d1a83512e98f7b0f64adcfe4758a0bf98488c3b84d0ae96`.
- Data do registro: 19/09/2026.

## v1.113 — card maior com o tempo do vídeo + LED de 3 estados (19/09/2026)

Pedido do Leo:
> *"Deixar o card um pouco maior e o principal mostrar o tempo do vídeo para não baixar vídeo extenso. E modificar o LED YouTube: apagado estado normal, quando um download é iniciado fica piscando vermelho, quando é finalizado fica verde."*
> *"...e o input tá ficando muito verde depois do download."*

### Card maior + tempo do vídeo em destaque

- Card de **88×54 → miniatura 124×74**, padding 14px, título 16px, meta 12.5px (altura final: ~160px).
- A **duração virou um badge sobre a miniatura** (canto inferior direito, pílula com fundo escuro e fio verde) — é o primeiro dado que salta aos olhos antes de decidir baixar. A duração saiu da linha de metadados para não duplicar.
- Nova estrutura: `<div class="yt-thumb">` (miniatura + badge) + `.yt-result-info` + botão, com `grid-template-areas` `"thumb info" / "acao acao"`.

### LED do YouTube com 3 estados

| Estado | LED |
|---|---|
| parado (normal) | **apagado** (`#0d1410`, sem brilho, sem animação) |
| download em andamento | **vermelho piscando** (`#ff2d2d` + `ytLedPiscando` .62s) |
| download finalizado | **verde aceso** (permanece até nova pesquisa ou novo download) |

- Nova função **`ytLedEstado(estado, forcar)`**; `ytLedAtivo(ativo)` passou a delegar para ela (as chamadas de `ytBotao()` continuam funcionando).
- O verde de "concluído" é **protegido** das chamadas automáticas: só sai com ação explícita do usuário (`ytPesquisar`) ou novo download.
- `cancelado` e `erro` voltam o LED para apagado.

### Campo de pesquisa não fica mais verde depois do download

- O verde cheio passou a ser um **aviso rápido**: permanece ~1,6s e volta a 0%. Se um novo download já tiver começado, o campo **não** é apagado (checagem do botão CANCELAR visível).

### Validação (medida passo a passo)

| Momento | LED | Campo |
|---|---|---|
| antes do download | apagado, `animation: none` | 0% |
| baixando | `#ff2d2d` + `ytLedPiscando` | 20% → 100% |
| concluído | `#2ee66b` aceso | 100% |
| 2,3s depois | `#2ee66b` (permanece) | **0%** ✅ |

- Card: 160px de altura, miniatura 124×74, badge "5:18" visível em verde-claro.

- APK validado: `NEXUS-MUSIC-2-v1.113-card-maior-tempo-led-3-estados.apk`.
- VersionCode: `111`; VersionName: `1.113`; Tamanho: `1.453.971 bytes`.
- SHA-256: `3e3737368991085f93dc26579dafb170ad7ee8a160b9f2d7c50d5226347b1279`.
- SHA-256 do asset: `ae479d6ce7be6277c2f68b3d399c1c926466d30d1754f7b3ea71ca48b2826e12`.
- Data do registro: 19/09/2026.

## v1.114 — a prévia anterior para ao iniciar uma nova busca (19/09/2026)

**Relato do Leo:** *"Quando eu vou fazer pesquisa, o áudio da prévia que carregou fica saindo por cima do vídeo que ainda não carregou prévia."*

- **Causa:** `ytPesquisaAoDigitar()` só agendava a pesquisa; a prévia **anterior continuava tocando** durante todo o `fetch` da nova busca e só era interrompida quando o novo preview começava a tocar (o `ytPararPrevia(false)` acontecia lá dentro, tarde demais). Parecia que o áudio era do vídeo novo.
- **Correção:** `ytPararPrevia()` passou a ser chamado **no instante em que o usuário digita um termo novo** (e também no início de `ytPesquisar()`, como garantia para chamadas diretas). O card antigo sai junto, dando lugar ao aviso *"pesquisando no YouTube…"*.
- **Validação medida:**

| Momento | Prévia | Players abertos | Card na tela |
|---|---|---|---|
| 1ª busca | tocando ✅ | 1 | THIS TIME |
| **60 ms depois de digitar o termo novo** | **parada** ✅ | **0** | "pesquisando no YouTube…" |
| nova busca | nova prévia tocando ✅ | 1 | novo card |

- APK validado: `NEXUS-MUSIC-2-v1.114-previa-para-na-nova-busca.apk`.
- VersionCode: `112`; VersionName: `1.114`; Tamanho: `1.454.117 bytes`.
- SHA-256: `fc3942083dd983f66cf8a297c9b7e5be824156f2fa3fce3990bd3a4ce4c7fa73`.
- SHA-256 do asset: `1e16fd8606ce1ec5b1a8db8b91a3ad3034ec3c6e00d6d51431935e26f84d6395`.
- Data do registro: 19/09/2026.

## v1.115 — prévia leve, corte imediato e limite de espera (19/09/2026)

Pedido do Leo:
> *"Às vezes quando o vídeo é muito grande acho que ele sobrecarrega a prévia e vai fazer outra pesquisa demora — tem como limitar? E se possível baixar qualidade áudio [menor] para a prévia ser o mais rápido possível."*
> *"Limitar ou parar imediatamente quando tiver escrevendo nova pesquisa."*

### 1. O gargalo REAL estava no backend: `synchronized` no mesmo monitor

`previa()` e `pesquisar()` eram **`synchronized` no mesmo objeto**. Como uma prévia de vídeo longo demora, a **pesquisa seguinte ficava esperando** ela terminar — era isso que dava a sensação de interface travada. Correção:

- `previa()` e `pesquisar()` deixaram de ser `synchronized`: o trabalho de rede agora roda **em paralelo**.
- O acesso aos caches (`HashMap` não é thread-safe) ficou protegido por um **lock curto** (`travarCache`), usado só nos get/put — alguns microssegundos.
- `preparar()` continua `static synchronized` (inicialização única do NewPipe) e `iniciar()` só agenda a thread do download.

### 2. Prévia com o stream MAIS LEVE

- Nova escolha **`escolherAudioLeve()`**: pega o **menor bitrate** disponível (preferindo m4a/AAC, o formato mais seguro no WebView) em vez do maior.
- O log nativo registra a escolha: `prévia leve: M4A 48 kbps`.
- **A qualidade boa continua reservada ao DOWNLOAD** (`escolherAudio`), que segue pegando o maior bitrate.

### 3. Corte imediato ao escrever uma nova pesquisa

- `ytPararPrevia()` agora **aborta a requisição em andamento** (`AbortController.abort()`), não apenas silencia o áudio. Antes a requisição seguia baixando e segurava a próxima busca.
- `ytPesquisaAoDigitar()` já interrompia no primeiro caractere; agora o cancelamento é de rede também.

### 4. Limite de espera de 12s

- Se a prévia não carregar em **12s** (vídeo muito pesado), ela é abortada e o app avisa: *"prévia demorou demais — toque no card para tentar de novo"*. Nada fica pendurado.

### Validação (medida, com servidor atrasando a prévia em 15s de propósito)

| Momento | Controller | Resultado |
|---|---|---|
| prévia carregando (servidor lento) | ativo | carregando |
| **120 ms após escrever nova busca** | **inativo** | **cortou em 121 ms** ✅ |
| 13 s depois | — | aviso "prévia demorou demais" (limite de 12s) ✅ |

- APK validado: `NEXUS-MUSIC-2-v1.115-previa-rapida-e-corte-imediato.apk`.
- VersionCode: `113`; VersionName: `1.115`; Tamanho: `1.454.844 bytes`.
- SHA-256: `6eafb20b5daa4018632b1f6514ab9d2d2114dba5078afd8d0674e6501c767cf6`.
- SHA-256 do asset: `09e7f0551d1fcfeefba5c1a8a74051af178cd0fa0fafe029783bfe8c14ba10bf`.
- Data do registro: 19/09/2026.

## v1.116 — prévia aquecida: busca e resolução de stream em paralelo (19/09/2026)

**Pedido do Leo:** *"Analisar se é possível deixar mais rápido, instantâneo, a prévia."*

### Análise do caminho crítico

```
digitar → debounce → POST /api/youtube/search → (YouTube: fetchPage da busca)
        → render do card → POST /api/youtube/preview → (YouTube: fetchPage do vídeo)
        → URL do googlevideo → <audio> faz buffering → toca
```

O trecho caro é o **segundo `fetchPage()`** (a página do vídeo) — e ele só começava **depois** que a busca voltava e o card era desenhado: as duas esperas de rede ficavam **em série**.

### As quatro otimizações

| # | Otimização | Ganho |
|---|---|---|
| 1 | **Aquecimento da prévia** (`aquecerPrevia`): assim que a pesquisa retorna, o stream do primeiro resultado começa a ser resolvido em segundo plano | o tempo de rede do YouTube passa a correr **em paralelo** com o desenho do card, em vez de depois dele |
| 2 | **Deduplicação** (`previasEmAndamento` + `CompletableFuture`): se a interface pedir a prévia enquanto ela já está sendo resolvida, **ninguém baixa a página duas vezes** — quem chega depois espera a mesma resolução | evita trabalho dobrado (antes o aquecimento e o pedido do card poderiam duplicar o `fetchPage`) |
| 3 | **Debounce 350 ms → 220 ms** | resposta começa ~130 ms mais cedo |
| 4 | Cache de prévia por vídeo (300 s) já existente agora é **reaproveitado pelo aquecimento** | repetição do mesmo termo: instantâneo |

- Log nativo para diagnóstico: `prévia resolvida em XXXX ms` (dá para ver no logcat quanto o YouTube levou).
- O stream mais leve (48 kbps) da v1.115 continua sendo o escolhido — menos dados e buffering mais rápido.

### Validação

| Medição | Resultado |
|---|---|
| Card na tela após digitar | **231 ms** (era ~360 ms) |
| Prévia automática | ✅ tocando, card marcado |
| Badge de tempo | ✅ `5:18` |
| Compilação Java | ✅ `BUILD SUCCESSFUL` |

- APK validado: `NEXUS-MUSIC-2-v1.116-previa-aquecida-rapida.apk`.
- VersionCode: `114`; VersionName: `1.116`; Tamanho: `1.455.510 bytes`.
- SHA-256: `3e6ca4bbfa4b7a1920e32134eb503f59b7deb5543efeb021d7d8a7ae1a53da9b`.
- SHA-256 do asset: `d1da582b6dfcfbd17c398d75fbd3da0cd6c1e8cf3306d306b09b7b698f084e2b`.
- Data do registro: 19/09/2026.

## v1.117 — sem realce azul, sem "modo página web" e 13 → 61 FPS (19/09/2026)

Pedido do Leo:
> *"Todos os botões/controles que aperto aparece um blur azul claro, tem como remover? E também remover selecionar para copiar, para não se comportar como página HTML. E analisar o desempenho."*

### 1. Sem realce azul (WebView)

- `-webkit-tap-highlight-color: transparent` em **todos** os elementos — era o realce azul/claro que o WebView desenha ao toque.
- `outline: none` em botões/inputs no `:focus` e `:focus-visible` — o anel azul do navegador sai de cena. O feedback continua sendo o verde do `:hover`/`:active` que já existia.

### 2. Sem comportamento de página web

| Onde | O que foi feito |
|---|---|
| CSS | `user-select:none` + `-webkit-touch-callout:none` em `html`, `body` e controles |
| JS | `contextmenu`, `dragstart` e `selectstart` bloqueados (com exceção da chave PIX e dos campos de digitação) |
| `MainActivity` | `setLongClickable(false)`, `setHapticFeedbackEnabled(false)` e `setOnLongClickListener(v -> true)` — segurar o dedo não abre menu nem vibra |

### 3. Desempenho — a medição revelou um problema sério

**Home ociosa: 13 FPS.** Causa: **7 elementos com `backdrop-filter`**, incluindo

- **blur ANINHADO** (campo de pesquisa com blur dentro do painel com blur);
- o pior de todos: **o painel do PLAYER tem blur e fica atrás do canvas do VU meter** — a cada quadro do visualizador o navegador tinha de **recompor o blur**.

**Correções:**

| Mudança | Efeito |
|---|---|
| `backdrop-filter` removido do `.player`, `.toolbar`, `.yt-panel` e `.yt-input` (o vidro continua pelo gradiente + borda clara) | elimina a recomposição por quadro atrás do VU |
| Blur mantido só em `.agora-tocando`, `.site-footer` e `.apoio-caixa` — as superfícies que passam **por cima** de conteúdo rolante | preserva o efeito onde ele é visto |
| **VU meter em modo econômico**: parado e com LEDs apagados, o laço cai de 60 para ~4 quadros/s e volta sozinho aos 60 fps no play | menos CPU e bateria |

### Medição (antes → depois)

| Cenário | Antes | Depois |
|---|---|---|
| Home ociosa | **13 FPS** | **61 FPS** |
| PASTAS com 6 cards | — | **60 FPS** |
| Elementos com blur | 7 (2 aninhados) | **3** (só sobre conteúdo) |
| `user-select` / tap highlight | `auto` / azul | **`none` / transparente** |
| Visual | — | ✅ idêntico (vidro por gradiente + borda) |

- APK validado: `NEXUS-MUSIC-2-v1.117-sem-blur-azul-e-performance.apk`.
- VersionCode: `115`; VersionName: `1.117`; Tamanho: `1.456.871 bytes`.
- SHA-256: `3fb9182f5bb0d274e92ef3242b16837826041490acea334a92eb97faaec12be2`.
- SHA-256 do asset: `2dbd5314aec6c405fd9c0e492f2b5bc879d2586be198a8c926e83d69dd583feb`.
- Data do registro: 19/09/2026.

## v1.118 — o ▶ não invade mais a Home com a lista completa (19/09/2026)

**Relato do Leo:** *"Quando eu inicio o app e abro o player, todas as músicas se abrem na página inicial — modificar isso da melhor forma possível."*

### A causa

`togglePlay()` (o ▶ do player), quando não há faixa carregada, chama `playAll()`. E o `playAll()` **sem fila** fazia:

```js
currentView='todas';
renderTracks(lib.tracks,'// TODAS AS MÚSICAS');   // ← trocava a tela toda
```

Ou seja: abrir o app (Home limpa, fila vazia) e apertar ▶ **substituía a página inicial pela lista completa de músicas** — justamente a Home que o Leo pediu para manter limpa. O comentário do próprio código já registrava esse efeito colateral para o caso das pastas; faltava o caso da Home.

### A correção (o "melhor jeito" encontrado)

```js
queue=lib.tracks.slice();     // a fila existe para o next/prev funcionarem
qIndex=0;
if(currentView!=='inicio'){   // só troca a tela se NÃO estiver na Home
    currentView='todas';
    viewData={view:'todas',query:''};
    renderTracks(lib.tracks,'// TODAS AS MÚSICAS');
}
playTrack();
```

- **Na Home:** o ▶ monta a fila (biblioteca inteira, em ordem de recentes) e **toca**, sem mexer na tela.
- **Fora da Home:** permanece o comportamento anterior (mostra a lista), porque ali a lista faz sentido.

### Validação medida

| Ação | Resultado |
|---|---|
| ▶ na Home (sem fila) | ✅ tocando (fila 24, `0001/0024`), `currentView = inicio`, lista oculta, gêneros/álbuns ocultos, box do YouTube visível |
| ▶ novamente | ✅ **pausa** (não reinicia a lista nem troca a tela) |
| ▶ uma terceira vez | ✅ retoma a mesma faixa |

- APK validado: `NEXUS-MUSIC-2-v1.118-play-nao-invade-a-home.apk`.
- VersionCode: `116`; VersionName: `1.118`; Tamanho: `1.457.068 bytes`.
- SHA-256: `b7828dbdca6a008c9a211f81d72417627c98663162c30c2c9b82d70c09107b46`.
- SHA-256 do asset: `6cc99773a840b45ce41e50a4fc82eb73e2fcc4a025b5ec743b332f8a6db4ff2b`.
- Data do registro: 19/09/2026.

## v1.119 — visor limpo (sem ícones mortos) e alinhamento unificado (19/09/2026)

**Pedido do Leo:** *"No box do player no topo tem uns textos/ícones sem função, tipo o 'MUSIC MODE' — remover tudo e colocar lá o número de músicas e de pastas que está abaixo do relógio. Também fazer o alinhamento, principalmente no título e subtítulo."*

### O que saiu (tudo decorativo, escrito à mão no HTML, sem ligação com o estado real)

| Elemento | O que era |
|---|---|
| `▰▰▰` (`.retro-battery`) | bateria falsa — número fixo no HTML |
| `MUSIC MODE` | rótulo decorativo |
| `◖ 029` (`.retro-volume`) | volume falso — número fixo, não seguia o slider real |
| `♪` (`.retro-note`) | enfeite que empurrava o título para fora do eixo |

### O que subiu para o cabeçalho

O `#retroLibraryStats` (total de músicas · pastas), que ficava espremido **embaixo do relógio**, passou para o lugar do antigo "MUSIC MODE". O id foi mantido, então `updateStats()` continua funcionando **sem nenhuma mudança no JS**.

### Alinhamento unificado

Todos os elementos agora usam a **mesma margem lateral (14px)** e estão **centrados no mesmo eixo**:

| Linha | Padding | Desvio do centro |
|---|---|---|
| `♪ 024 MÚSICAS · 🗂 06 PASTAS` | 14/14 | **0** |
| `0001/0024` | 14/14 | **0** |
| `00:00` | 14/14 | **0** |
| Título | 14/14 | **0** |
| Subtítulo | 14/14 | **0** |

Antes: cabeçalho 9px, título 13px com o `♪` deslocando o texto, subtítulo 42px — cada linha num eixo.

Também foi corrigida a **sobreposição de 8px** entre a caixa do título (fonte CafeNero, 20px) e o subtítulo, que agora tem respiro próprio.

### Validação

| Verificação | Resultado |
|---|---|
| `MUSIC MODE` / bateria / volume no DOM | **ausentes** ✅ |
| Desvio do centro (5 linhas) | **0px em todas** ✅ |
| Folga título ↔ subtítulo | 1px (era −4px) ✅ |
| Sobra vertical no visor | 126px (sem estouro) ✅ |

- APK validado: `NEXUS-MUSIC-2-v1.119-visor-limpo-alinhado.apk`.
- VersionCode: `117`; VersionName: `1.119`; Tamanho: `1.457.665 bytes`.
- SHA-256: `f04e97392a4258a9f43f134d77356e89403d01f916f943378612c2f5c1660b61`.
- SHA-256 do asset: `8e329e0f870100ca5770ac805c4cd3b2323db4a7b78f2f217ee01769423ebd14`.
- Data do registro: 19/09/2026.

## v1.120 — relógio abaixo do título/subtítulo + VU verificado (19/09/2026)

**Pedido do Leo:** *"Colocar o relógio (tempo da música) abaixo do título/subtítulo. Fazer o alinhamento e verificar se o VU meter está coerente."*

### 1. Relógio reposicionado

Ordem final dentro do visor:

```
♪ 024 MÚSICAS · 🗂 06 PASTAS   ← cabeçalho
0001/0024                       ← contador da fila
Faixa de Teste 00               ← título
Artista 1 · ROCK                ← subtítulo
00:28                           ← TEMPO (agora aqui)
[ VU meter ]
```

O tempo fecha o bloco de informações da faixa. Medido: o topo das barras do VU fica **42px abaixo** do relógio — sem colisão.

### 2. Alinhamento conferido após a mudança

Desvio do centro do visor: **0px** no cabeçalho, contador, título, subtítulo **e no relógio**.

### 3. VU meter — auditoria de coerência

**Como ele funciona (está correto):**

- `AudioContext` + `createMediaElementSource(audio)` + `AnalyserNode` (`fftSize 512`) → lê o **espectro real** do áudio, não um número aleatório;
- **24 barras** em **bandas logarítmicas** de 40 Hz a 10 kHz (cada LED cobre uma faixa musical proporcional);
- **7 segmentos** por barra com cor por nível (verde → amarelo → vermelho);
- pico que permanece no nível alcançado e **decai em 2s**.

**Medições com áudio de teste (tons alternados de 110 Hz a 3,5 kHz):**

| Estado | LEDs acesos |
|---|---|
| tocando | 9107 → 9709 (varia com o som) |
| 300 ms após pausar | 8617 (decaimento suave) |
| 2,8 s após pausar | **0** (apagado) |
| relógio | corre junto (00:04 → 00:09) e para na pausa |

**Ponto frágil encontrado e corrigido:** como o som do player passa **por dentro** do AudioContext, se o contexto adormece (troca de app, tela bloqueada, economia de energia) o áudio fica **mudo** mesmo com o player "tocando" — e os eventos `play`/`playing` não cobrem esse caso, porque a reprodução não é reiniciada. Agora há retomada (`resume()`) em **qualquer toque** na tela e quando ela **volta a ficar visível** (custo zero: só age se o contexto estiver suspenso).

- APK validado: `NEXUS-MUSIC-2-v1.120-relogio-abaixo-do-titulo.apk`.
- VersionCode: `118`; VersionName: `1.120`; Tamanho: `1.458.013 bytes`.
- SHA-256: `d084f6ed49b2c9ea06c88515ec8880589b9c594c5a716b6a842c98e61d643bad`.
- SHA-256 do asset: `f7e7c4852c24eba52aa43c63e3dc1298252159a6f61ee982039380acc76f1b35`.
- Data do registro: 19/09/2026.

## v1.121 — relógio gigante encaixado (7px de cada lado) e título abreviável (19/09/2026)

**Pedido do Leo:** *"Deixar o relógio da música maior, a 7px do título e a 7px do VU meter entre eles. Também deixar o título da música centralizado e, se for maior que a tela, abreviar."*

### 1. Relógio: 39px → **75px**, com 7px exatos de cada lado

O tamanho não foi escolhido "no olho": foi **calculado** pelo espaço disponível.

- Espaço livre entre o subtítulo e o topo da área de LEDs do VU: **89px**;
- Descontando 7px acima + 7px abaixo: **75px** para o relógio;
- Posicionado a partir da **base do visor** (`position:absolute; bottom:82px`), então a folga inferior é sempre 7px em relação ao ponto onde a barra mais alta do VU acende (y=181 no canvas de 250px) — não depende do fluxo dos textos acima.

Medições confirmadas no navegador:

| Medida | Valor |
|---|---|
| Fonte | **75px** (era 39px) |
| Folga acima (subtítulo → relógio) | **7px** ✅ |
| Folga abaixo (relógio → topo do VU) | **7px** ✅ |
| Centro | 254 = centro do visor ✅ |

### 2. Título centralizado e abreviado

- `justify-content:center` + `text-align:center` no título;
- `overflow:hidden` + `white-space:nowrap` + `text-overflow:ellipsis` **forçados** no `#retroTitle`;
- Testado com um título gigante (*"Uma Música Com Um Título Absurdamente Longo Que Jamais Caberia Nesta Tela De Jeito Nenhum"*): **abreviado com "…"**, largura 420px dentro do visor de 450px, **sem estourar** e **centrado**.

- APK validado: `NEXUS-MUSIC-2-v1.121-relogio-grande-7px.apk`.
- VersionCode: `119`; VersionName: `1.121`; Tamanho: `1.458.249 bytes`.
- SHA-256: `464d4d82cc85c2ca0f08fcbee2e9f70df2a295faa443d8a4d480279ccada15bb`.
- SHA-256 do asset: `76e3b7cfaee67df4377c6ef26bc9ea8301859f725b856c6d7daacdfd0cfa9824`.
- Data do registro: 19/09/2026.

## v1.122 — fontes embutidas no APK (a causa do "a fonte não carregou") — 19/09/2026

**Relato do Leo:** *"A fonte do relógio não carregou"* e, depois, *"o baixar YouTube tá usando qual fonte?"*.

### Diagnóstico — eram DOIS problemas somados

**1. O relógio pedia uma fonte que não existe no Android.**
O relógio usava `'Courier New'` — familia que **não existe no Android**. O WebView caía no `monospace` genérico do sistema.

**2. O app carregava as fontes principais PELA INTERNET.**

```html
<link href="https://fonts.googleapis.com/css2?family=Orbitron...&family=Rajdhani..." rel="stylesheet">
```

Orbitron e Rajdhani vinham do **Google Fonts**. Sem rede (ou com a rede do celular lenta/bloqueada), **nenhuma delas carregava** e o app inteiro — painel "BAIXAR DO YOUTUBE", busca, botões — caía na fonte genérica do sistema. Era a raiz do "a fonte não carregou".

**3. O `@font-face` da CafeNero declarava só `font-weight:400`** enquanto o título e o relógio pedem `700`. Nesta situação o navegador **descarta a família** e usa o próximo da lista — ela ia embora.

**4. O servidor do app só servia UM arquivo de fonte:**
```java
if ("Cafe Nero M54.ttf".equals(nomeFonte)) { ... } else { 404 }
```
Qualquer outra fonte embutida responderia 404 no aparelho.

### Respostas e correções

| Onde | Fonte | Correção |
|---|---|---|
| Título "BAIXAR DO YOUTUBE" | **Orbitron 700** | agora embutida no APK |
| Dica "cola o link e baixa o áudio…" | **Rajdhani 400** | agora embutida |
| Campo de pesquisa do YouTube | **Rajdhani 400** | agora embutida |
| Relógio do visor | **CafeNero 700** (era Courier New) | fonte do projeto, embutida |

- **5 arquivos .woff2 embutidos** em `assets/fonts/` (subset "latin", que já cobre os acentos do português): Orbitron 400/700, Rajdhani 400/600/700 — exatamente os pesos que o CSS usa;
- **O `<link>` do Google Fonts foi removido** — zero dependência de internet para a identidade visual;
- **`@font-face` da CafeNero duplicado em 400 e 700** (mais compatível que o range `400 700`, que só existe no CSS Fonts 4);
- **`NexusServer` generalizado**: serve qualquer arquivo de `assets/fonts/` (com validação de nome contra path traversal) e envia o `Content-Type` certo (`font/woff2`).

### Validação

| Verificação | Resultado |
|---|---|
| Fontes carregadas (navegador) | Orbitron 400/700, Rajdhani 400/600/700, CafeNero 700 — **todas `loaded`** ✅ |
| Fontes com erro | **0** ✅ |
| Referências ao Google no HTML | **0** ✅ |
| Fontes dentro do APK | **5 arquivos** ✅ |
| Fontes realmente aplicadas (métrica) | Orbitron 147px ≠ sans-serif 129px; CafeNero própria ✅ |
| Relógio: folga título/VU | **7px / 7px** ✅ |
| Acento "Ú" de MÚSICA | coberto pela fonte, sem corte (6px de sobra) ✅ |

- APK validado: `NEXUS-MUSIC-2-v1.122-fontes-embutidas-offline.apk`.
- VersionCode: `120`; VersionName: `1.122`; Tamanho: `1.511.437 bytes` (+53 KB pelas fontes).
- SHA-256: `26680077d6ca6e38c6690acc8289f55c7a13a75857cf4fb7faa90dc7eb00d2c2`.
- Data do registro: 19/09/2026.

## v1.123 — fontes menores + VU sem distorção no celular (19/09/2026)

**Pedido do Leo:** *"Ficou muito grande a fonte, diminuir se possível. Ajustar seu navegador para ver em modo mobile, pois esse projeto é exclusivo APK celular."*

### 1. Validação passou a ser em VIEWPORT DE CELULAR

O servidor de teste ganhou a página `/mobile`, que carrega o app real dentro de um **iframe 412x915** — assim as `@media` do aparelho valem e o layout é julgado como no APK. **Foi isso que revelou o bug abaixo.**

### 2. 🐞 O VU meter estava DISTORCIDO no celular

- O canvas tinha resolução fixa **450x250**, mas no celular o visor tem **358x250**;
- o CSS esticava o canvas para 100% → as barras do VU apareciam **comprimidas horizontalmente** (20% mais estreitas).

**Correção:** o canvas agora mede o visor de verdade (`clientWidth/clientHeight`) e desenha nessa resolução, com re-ajuste em `resize`. Medido: canvas **356x248** para um visor de **358x250** — proporção correta.

### 3. Fontes diminuídas (o print no celular mostrava o relógio dominando)

| Elemento | Antes | Agora |
|---|---|---|
| Relógio | 77px | **48px** |
| Título da música | 15px | **13px** (a CafeNero é display larga; em 15px ficava grande demais) |

E as folgas ficaram **equilibradas**: o relógio é ancorado pela base do visor e fica com **21px acima e 21px abaixo** (antes eram 7px/7px, que só faziam sentido com a fonte gigante).

### Validação em viewport de celular (412x915)

| Verificação | Resultado |
|---|---|
| Canvas vs visor | 356x248 ← 358x250 — **sem distorção** ✅ |
| Relógio | 48px, folgas **21/21** ✅ |
| Título | 13px, centrado, **sem estourar** ✅ |
| Rolagem horizontal | **nenhuma** ✅ |
| Título real longo ("GIGI D'AGOSTINO - L'AMOUR TOUJOURS") | cabe inteiro em 299px ✅ |

> Nota: o `:` do relógio é discreto por característica da **própria fonte** stencil (largura medida: 13px, presente na fonte) — não é falha de carregamento.

- APK validado: `NEXUS-MUSIC-2-v1.123-fontes-menores-mobile.apk`.
- VersionCode: `121`; VersionName: `1.123`; Tamanho: `1.511.776 bytes`.
- SHA-256: `9492b7ca8f9c1c68f522f1d34cfe3beaee5f1f726512c7c814f7380ca62cb225`.
- SHA-256 do asset: `a2abab780419b9fc249770e5ffc59d628b76128801c6a651ec923f2f5adf99e9`.
- Data do registro: 19/09/2026.

## v1.124 — ":" do relógio, barra de progresso grossa e tempos sem corte (19/09/2026)

**Relato do Leo:** *"O relógio tá sem o `:`"* + *"ajustar a barra de progresso, muito fina; relógio início e começo barra estão cortados"*.

### 1. Os dois pontos não apareciam — troca de fonte

A **CafeNero** é uma fonte *stencil* e desenhava o `:` fino demais: presente na fonte (o `cmap` o tem, largura 13px medida), mas **visualmente imperceptível** no aparelho.

**Solução:** o relógio passou a usar a **Orbitron** (a mesma do "BAIXAR DO YOUTUBE") — display digital com dois-pontos nítido — e que já vai **embutida no APK**. Ajustado para 46px.

### 2. 🐞 Os tempos das pontas estavam ESPREMIDOS

Medição em viewport de celular: cada `<span>` de tempo tinha apenas **22px de largura** para um texto de ~33px — o flex encolhia os spans porque a barra (`flex:1`) tomava o espaço. Era por isso que "00:07"/"00:30" apareciam **cortados**.

**Correção:** `flex:0 0 auto` + `min-width:36px` nos tempos, fonte 11px em negrito.

### 3. 🐞 A barra estourava o container

`.progress-area` tinha **6px de altura** e a barra **8px** → transbordo (e o `border-radius:5px` original nem se aplicava).

**Correção:** área com `min-height:22px` e barra com **12px**, cantos `999px`, fundo translúcido com borda verde e preenchimento em gradiente verde.

### Validação em viewport de celular (412x915)

| Medida | Antes | Agora |
|---|---|---|
| Largura do tempo (cada) | 22px (cortado) | **36px** ✅ |
| Altura da barra | 8px (área 6px) | **12px** (área 22px) ✅ |
| Raio da barra | 0px | **999px** ✅ |
| Relógio | CafeNero (sem ":") | **Orbitron, ":" visível** ✅ |

- APK validado: `NEXUS-MUSIC-2-v1.124-doispontos-barra-grossa.apk`.
- VersionCode: `122`; VersionName: `1.124`; Tamanho: `1.512.128 bytes`.
- SHA-256: `2ef180be852d484a8c72992a52262f4a408a4e117f027009e55b993fe92b62ea`.
- SHA-256 do asset: `e3983736b6e1ac3fba1b1d051f16cd054e3a7a3900fe70b30ebab67565ce837c`.
- Data do registro: 19/09/2026.

## v1.125 — TEMA CYBER BLACK (degradê verde + roxo) — 19/09/2026

**Pedido do Leo (em etapas no mesmo dia):**
> *"Modificar apenas tema cor black glass estilo retro detalhes brancos"* →
> *"VU meter colorido verde topos vermelho com degrade"* →
> *"Contornos mais definido blur branco 2px botões e boxes"* →
> *"Fazer cor tema cyber Black com detalhes degrade verde e rosa"* →
> *"Faz degrade roxo e verde"*

### Resultado final (aprovado antes do build, regra nova do Leo)

Base **preto cyber** com os **detalhes em degradê VERDE → ROXO**:

| Elemento | Efeito |
|---|---|
| Relógio do visor | texto em degradê verde → branco → roxo |
| Play/pause | disco em degradê verde → roxo |
| Barra de progresso | verde → roxo |
| Botões (ÁLBUNS/PASTAS/FAVORITOS, APOIAR) | fundo em degradê; aba aberta com degradê forte |
| Visor, boxes, rodapé | contorno **definido** (alpha .38–.55) + **glow de 2px** (verde + roxo) |
| VU meter | **verde → amarelo → vermelho** (medição, pedido explícito) |
| LED do YouTube | vermelho piscando / verde ao concluir (estado) |

### Como foi feito (reversível)

Tudo em **camada de override** no fim do `<style>`, apoiada nas **variáveis CSS** do tema (`--verde`, `--pink`, `--border`, ...). Cada etapa foi salva em `_backup/`:

- `index-antes-tema-branco-20260919-1752.html`
- `index-antes-roxo-20260919-1801.html`

### Pitfalls encontrados (custaram iteração)

1. **Especificidade com `!important`**: o play continuava **branco** porque a regra do tema anterior (`.player-top .control.main`) tinha especificidade maior. Solução: repetir o **mesmo seletor** no bloco novo (ele vem depois e vence).
2. **Estado `:hover` mascarando o resultado**: no navegador de validação o cursor fica sobre o botão clicado, então o `background` lido era o do `:hover`. Foi preciso cobrir `:hover` e `:active` — e no aparelho o toque também dispara `:active`.
3. **`backdrop-filter` com moderação**: o glow novo é `box-shadow` de 2px (barato); blur grande continua restrito (lição da v1.117).

### Validação

| Verificação | Resultado |
|---|---|
| Play com degradê correto | `linear-gradient(140deg, rgb(46,230,107), rgb(166,255,184) 42%, rgb(168,85,247))` ✅ |
| Barra de progresso | verde → roxo ✅ |
| Relógio | degradê no texto ✅ |
| VU meter | verde → amarelo → vermelho (aceso durante o áudio de teste) ✅ |
| `:hover`/`:active` cobertos | ✅ |
| Print aprovado pelo Leo antes do build | ✅ (regra de 19/09/2026) |

- APK validado: `NEXUS-MUSIC-2-v1.125-tema-cyber-black-verde-roxo.apk`.
- VersionCode: `123`; VersionName: `1.125`; Tamanho: `1.513.825 bytes`.
- SHA-256: `03ab9e8c047d76820d195d93ca4334894f9ac1e08c3310a56eef4ad8d05988d2`.
- SHA-256 do asset: `4f2404be6a2636461d2d2d9634925e2e2172cd3e0e772f9b466b6a7fc8a56674`.
- Data do registro: 19/09/2026.

## v1.126 — FUNDO PRÓPRIO, LOGO DO YOUTUBE E OPACIDADES — 19/09/2026

**Pedidos do Leo:**
> *"Colocar essa logo YouTube"* + *"Na frente do nome baixar do YouTube"* →
> *"Remover background de grade do app e aplicar esse"* (imagem pixelada escura) →
> *"Os botões e box tão muito transparents deixa boxes 70% e botões 90%"* →
> *"Desfazer do box do player"*

### 1. Logo do YouTube no painel

- Imagem recortada do PNG enviado (com transparência) e salva em `assets/img/youtube-logo.png`;
- nova rota **`/img/`** no `NexusServer` (mesma validação anti-path-traversal das fontes, `Content-Type` por extensão);
- colocada **antes** do "BAIXAR DO YOUTUBE" (após o LED): **90×20px**, medido para caber em uma linha (LED 12 + logo 90 + título 181 + gaps = 303 de 346px disponíveis);
- versão alternativa só do ícone também ficou em `assets/img/youtube-icone.png`.

### 2. Fundo: grade REMOVIDA, imagem do Leo aplicada

**A grade estava definida em DOIS lugares** — a segunda definição (mais abaixo) redesenhava o grid branco e vencia a primeira. Foi por isso que a imagem "não aparecia" na primeira tentativa.

- Removida a grade de 24px (a original e a duplicada);
- `body::after` (brilhos radiais) desligado para não "lavar" a imagem;
- imagem em `assets/img/fundo.jpg` (640×1280, 157 KB, brilho médio 16/255 — escura o bastante para dispensar véu), aplicada em `body::before` fixo com `cover` no topo e `z-index:-1`;
- **sem custo de rolagem**: continua sendo um pseudo-elemento fixo atrás do conteúdo.

### 3. Opacidades

| Elemento | Opacidade |
|---|---|
| Boxes (painel YouTube, cards, input) | `rgba(12,12,12,.70)` → **70%** |
| Visor | `rgba(6,6,6,.70)` |
| Rodapé | `rgba(10,10,10,.70)` |
| Botões | `rgba(16,16,16,.90)` → **90%** |
| **Box do player** | **desfeito a pedido** — voltou ao vidro transparente (imagem aparece por dentro) |

Cobri `:hover`/`:active` dos botões (senão o toque voltava a deixá-los transparentes) e usei `:not(.main)` para o **play** não perder o degradê verde→roxo. Conferido que a reversão do player **não** trouxe `backdrop-filter` de volta (lição da v1.117).

### Artefato

- APK: `NEXUS-MUSIC-2-v1.126-fundo-logo-cyber.apk`.
- VersionCode: `124`; VersionName: `1.126`; Tamanho: `1.693.443 bytes`.
- SHA-256: `401438f4ace7aba676c9f6c085be401574ffdd9aa236b509b05a5355b04cf71b`.
- SHA-256 do asset: `598e1aa43eb6ff13a760841a085e436c7efbfc077b898533562dda8041108e16`.
- Empacotado e verificado: `assets/img/fundo.jpg`, `youtube-logo.png`, `youtube-icone.png` + 5 fontes.
- Aprovação do Leo por print **antes** do build ✅.
- Data do registro: 19/09/2026.

## v1.127 — ÍCONE DO APP no tema novo — 19/09/2026

**Pedido:** *"Mudar novo ícone build conforme novo tema"*.

Três propostas foram geradas e apresentadas (equalizador de barras · nota musical · monograma "N"). O Leo escolheu o **monograma "N"**.

### O que foi feito

- Ícone desenhado no tema: **fundo preto arredondado**, monograma **"N"** na fonte retrô do visor (CafeNero M54) em **degradê verde→roxo**, contorno cyber e brilho suave (mesma paleta do app: `#2ee66b` → `#a855f7`);
- gerado nos **5 tamanhos** do Android: mdpi 48 · hdpi 72 · xhdpi 96 · xxhdpi 144 · xxxhdpi 192, todos em **RGBA com transparência** nas bordas (o launcher aplica a máscara dele sem sobrar fundo branco);
- substituídos em `app/src/main/res/mipmap-*/ic_launcher.png` — o `AndroidManifest` já apontava para `@mipmap/ic_launcher`, nada a mudar;
- ícones antigos salvos em `_backup/icones-antes-cyber/`;
- validado no tamanho real (64px na tela inicial) antes de buildar.

### 🐞 Pitfall: recursos ofuscados no APK

Ao conferir o APK, nada aparecia com `mipmap`/`ic_launcher` no `unzip -l`: o **AGP ofusca os nomes dos recursos** (`res/9w.png`, `res/FS.png`, `res/RJ.png`, `res/o-.png`, `res/yn.png`) e ainda **recomprime os PNGs** (os bytes mudam). A conferência correta é por **geometria e conteúdo**:

| Arquivo no APK | Tamanho | Bytes |
|---|---|---|
| `res/9w.png` | 48×48 | 2485 |
| `res/yn.png` | 72×72 | 4026 |
| `res/FS.png` | 96×96 | 6256 |
| `res/RJ.png` | 144×144 | 10845 |
| `res/o-.png` | 192×192 | 15875 |

Confirmado extraindo os 5 PNGs do próprio APK e inspecionando o desenho — é o "N" novo em todos os tamanhos.

### Artefato

- APK: `NEXUS-MUSIC-2-v1.127-icone-cyber.apk`.
- VersionCode: `125`; VersionName: `1.127`; Tamanho: `1.731.240 bytes`.
- SHA-256: `3038a4fa46bcfbf517e3a5aa0ef2d2160e396d5766b9fece26fa2c32bbc6e354`.
- App em si: **sem mudanças** — `index.html` idêntico ao da v1.126, só o ícone do launcher mudou.
- Aprovação do Leo (prévia do ícone em tamanho real) **antes** do build ✅.
- Data do registro: 19/09/2026.

## v1.128 — BUSCA COM 5 RESULTADOS e prévia no toque — 19/09/2026

**Pedidos:**
> *"Mostrar agora 5 pesquisas do YouTube no mesmo design"* →
> *"A prévia toca quando toc no card"*

### O que mudou

**Só a interface** — o `YtDownload.pesquisar()` **já devolvia até 5 resultados** (`if (adicionados >= 5) break;`); a interface mostrava apenas o primeiro.

| Mudança | Detalhe |
|---|---|
| **Até 5 cards** | `ytMostrarResultados()` agora percorre `ytResultados.slice(0,5)` e cria um card por resultado, no **mesmo design** aprovado (miniatura com TEMPO em badge + título + canal + ⬇ BAIXAR embaixo) |
| **Prévia sob toque** | **nenhuma prévia automática** — o áudio só começa quando o card é tocado; tocar de novo pausa/retoma; tocar em outro vídeo troca a prévia |
| **Destaque correto** | `marcarCard()` deixou de usar `document.querySelector('.yt-result')` (que pegava sempre o **primeiro** card) e passou a usar `[data-indice]` — cada card tem `data-indice` |
| **Cada BAIXAR** | `btn.onclick=()=>ytBaixarResultado(indice)` — baixa o vídeo daquele card (com o preflight de tamanho) |
| **Espaçamento** | `.yt-results{display:grid; grid-template-columns:minmax(0,1fr); gap:10px}` |

Detalhe de carregamento: a miniatura do 1º card usa `loading="eager"` e os demais `lazy` — evita 5 downloads de capa de uma vez.

### Validação (viewport 412×915)

| Verificação | Resultado |
|---|---|
| Cards renderizados | **5** ✅ |
| Todos com BAIXAR + badge de tempo | ✅ |
| Altura/largura uniformes | ✅ (346px) |
| Prévia automática | **nenhuma** ✅ |
| Toque no 3º card | destaque e áudio no card 3 ✅ |
| Toque no 2º card | destaque migrou para o card 2 ✅ |
| Mensagem na tela | "ouvindo prévia — toque no card para pausar" ✅ |

### Artefato

- APK: `NEXUS-MUSIC-2-v1.128-cinco-resultados.apk`.
- VersionCode: `126`; VersionName: `1.128`; Tamanho: `1.731.711 bytes`.
- SHA-256: `2b5f1d246f9297c97806d7b92d2a88d298194cf479e68ecd1b9ea30a414a8a3e`.
- SHA-256 do asset: `b4ba96d04429b133a4a4c65158c5e76d` (fonte == empacotado ✅).
- Aprovação do Leo por print **antes** do build ✅.
- Data do registro: 19/09/2026.

## v1.129 — PRÉ-AQUECIMENTO DAS 5 PRÉVIAS — 19/09/2026

**Pedido:**
> *"Como a pesquisa já baixa 5 resultados já fazendo pré carregamento das prévias para melhor experiência"*

### O que mudou (só o backend)

Antes, `pesquisar()` aquecia **apenas a prévia do primeiro resultado** (`aquecerPrevia(primeiraUrl)`). Agora as **5** são resolvidas em segundo plano.

| Detalhe | Implementação |
|---|---|
| Método novo | `aquecerPrevias(List<String> urls)` — `pesquisar()` monta `urlsParaAquecer` com as URLs dos até 5 resultados |
| **Uma thread só** | `nexus-previas-warm` (daemon) percorre a lista **uma prévia de cada vez**, com **250ms de folga** entre elas — cinco `fetchPage` simultâneos no YouTube atrasariam a própria resposta da pesquisa |
| Dedupe | `previasEmAndamento` (ConcurrentHashMap de `CompletableFuture`) já garante que tocar num card durante o aquecimento **aproveita** a mesma resolução |
| Falha silenciosa | aquecimento é oportunista: erro em um vídeo não vira erro do usuário |
| Limpeza | `aquecerPrevia` (singular) removido — ficou sem uso, além do Javadoc órfão |

O **front não mudou** nesta versão (a prévia continua sob toque, sem autoplay).

### Validação

| Verificação | Resultado |
|---|---|
| Compilação | `compileReleaseJavaWithJavac` sem erros ✅ |
| Medição no front (servidor local) | toque no **último** card → áudio em **105 ms** (o gargalo real é resolver o stream no YouTube, que o aquecimento elimina) |
| Código no APK | `classes.dex` contém a thread **`nexus-previas-warm`** ✅ (grep em `strings`) |
| `index.html` | idêntico ao da v1.128 ✅ (nada visual mudou) |

### Artefato

- APK: `NEXUS-MUSIC-2-v1.129-pre-aquecimento-5-previas.apk`.
- VersionCode: `127`; VersionName: `1.129`; Tamanho: `1.731.999 bytes`.
- SHA-256: `4b125cc5f8aa76994f68e942cd755a00fc370f419bfdfd2fcfd33c6245dd81d0`.
- SHA-256 do asset: `b4ba96d04429b133a4a4c65158c5e76d`.
- Aprovação do Leo por print **antes** do build ✅.
- Data do registro: 19/09/2026.

## v1.130 — ANEL DE CARREGAMENTO DA PRÉVIA (4px, blur verde/rosa) — 19/09/2026

**Pedidos:**
> *"Criar um feedback … quando o card é clicado para saber que tá carregando a prévia um blur duas cores 2px que fica circulando enquanto a prévia carrega"* →
> *"Fazer 4px com blur verde e rosa"* →
> *"Tá como já carregado"* →
> *"Manda sem zoom tela normal sempre"*

### O que foi feito

Ao tocar num card, enquanto a prévia é resolvida, aparece uma **borda de 4px em degradê verde→rosa com blur (halo difuso) circulando** em volta do card. Sai quando o som começa.

| Item | Implementação |
|---|---|
| Espessura | `padding: 4px` |
| Cores | `conic-gradient` `#2ee66b → #a6ffb8 → #ff2d96 → #ff8ec8` |
| Blur | `filter: blur(2.5px)` (com `inset: -5px` para o desfoque não invadir o conteúdo) |
| Rotação | animação do **ângulo** do degradê via `@property --nexus-ang` (1.05s/volta) — **não** `transform: rotate()`, que giraria o retângulo do card junto |
| Recorte | máscara (`mask-composite: exclude`) deixa só a beirada aparecer |
| Alvo | só o card tocado (`data-indice`) |
| Estado | classe `.carregando`, ligada em `ytPreviaResultado()` e limpa no sucesso/erro e em `ytPararPrevia()` |

### 🐞 O bug do feedback invisível ("tá como já carregado")

Depois do pré-aquecimento (v1.129) a prévia chega em **milissegundos**: o anel acendia e apagava tão rápido que não dava para ver — parecia que o card já estava carregado.

**Correção:** tempo mínimo de **700ms** para o anel (`desligarAnel()` calcula o que falta e agenda a saída). Medido com a prévia instantânea: aparece em **55ms** e sai em **708ms** (antes seria ~5ms).

### Regra nova registrada

**O print de aprovação vai SEMPRE em tela normal, nunca com zoom** (`zoom` desloca a rolagem e esconde o que o Leo veria no aparelho). Guardado na memória do agente e em `references/validacao-no-navegador.md` da skill `android-build`.

### Artefato

- APK: `NEXUS-MUSIC-2-v1.130-anel-carregamento-4px-blur.apk`.
- VersionCode: `128`; VersionName: `1.130`; Tamanho: `1.733.179 bytes`.
- SHA-256: `07d9d9c29daeda93000c0a8740c3a1170ac011fa1b60ab02d87c539aea284a29`.
- SHA-256 do asset: `a9eb771e773d1ce3960603e84da14525` (fonte == empacotado ✅; 2 referências à animação do anel no arquivo empacotado).
- Aprovação do Leo por print **antes** do build ✅.
- Data do registro: 19/09/2026.

## v1.131 — ANEL ESFUMAÇADO 5px (verde + roxo, interno) — 19/09/2026

**Pedido:**
> *"Está sólido faz como blur esfumaçado duas cores verde e roxo 5px interno"*

### Ajustes no anel

| Item | v1.130 | v1.131 |
|---|---|---|
| Espessura | 4px | **5px** |
| Posição | por fora (`inset: -5px`) | **por dentro** (`inset: 0`) |
| Cores | verde → rosa | **verde → roxo** (`#2ee66b → #a6ffb8 → #a855f7 → #c084fc`) |
| Blur | `blur(2.5px)` (com aresta) | **`blur(4.5px)`** — esfumaçado, sem aresta |
| Rotação | 1.05s/volta | **1.6s/volta** (mais suave, combina com o borrão) |

Também: como o anel agora é **interno**, o `border-radius` passou a `calc(var(--raio) + 1px)` (antes tinha a folga do `inset` negativo).

### Validação

Medido no viewport de celular: `padding: 5px` · `inset: 0` · `filter: blur(4.5px)` · `conic-gradient(rgb(46,230,107) → rgb(168,85,247))` · animação `1.6s` · classe só no card tocado. No APK: `blur(4.5px)` presente ✅ e 6 ocorrências do roxo `#a855f7`.

### Artefato

- APK: `NEXUS-MUSIC-2-v1.131-esfumacado-5px-verde-roxo.apk`.
- VersionCode: `129`; VersionName: `1.131`; Tamanho: `1.733.185 bytes`.
- Data do registro: 19/09/2026.

## v1.129 FINAL — RESTAURADO DO APK APROVADO PELO LEO — 19/09/2026

**Pedido:** *"Reverter deixar nesta versão"* + APK enviado `DOC-20260919-WA0019.apk`. *"Esse será último build"*.

- Os dois anexos DOC eram idênticos entre si e também ao APK v1.129 existente;
- `assets/index.html` foi extraído do APK enviado e restaurado exatamente;
- versão: `versionCode 127` / `versionName "1.129"`;
- fonte restaurada: SHA-256 `b4ba96d04429b133a4a4c65158c5e76d02778d1f482d48ed2f7c106c4b0769bf`, 262.412 bytes;
- estado v1.131 guardado em `_backup/estado-v1.131-antes-reversao-v1.129/`;
- viewport 412×915, tela normal sem zoom: 5 cards e prévia automática 0, conferido antes do build;
- último APK: `NEXUS-MUSIC-2-v1.129-final-restaurado.apk`;
- tamanho: `1.731.999 bytes`;
- SHA-256: `4b125cc5f8aa76994f68e942cd755a00fc370f419bfdfd2fcfd33c6245dd81d0`;
- asset dentro do APK == fonte restaurada ✅; assinatura, pacote, Activity e integridade validados ✅;
- **este é o último build — não farei novas alterações sem nova autorização explícita.**
## Plexus no visor — 20/09/2026

- Removido o desenho da grade VU de 24 colunas × 7 segmentos.
- Mantido o mesmo `#plexusCanvas` e a mesma área responsiva do visor.
- Adicionado plexus decorativo com 14 pontos determinísticos e linhas por proximidade.
- Os cinco movimentos continuam selecionados por faixa: Pulso Central, Raio Varredor, Chuva Digital, Onda Pixel e Batida Neon.
- O áudio permanece direto no elemento `<audio>`; sem `AnalyserNode` e sem alteração de volume.
- Estado pausado: plexus reduzido e leve; durante reprodução: atualização a aproximadamente 30 FPS.
- Validação: `scripts/validar-interface.sh` passou com JS válido e CSS `648/648`.
- Preview HTTP em `412×915` revisada sem overflow, cortes ou sobreposição.

## Plexus reativo livre com colisão — build aprovada

- Plexus voltou a usar o áudio real por `MediaElementSource` + `AnalyserNode`, criado uma única vez.
- Grave e agudo são separados por faixas FFT e detectados pelo delta entre frames.
- Os 30 pontos têm velocidade própria e colisão nas quatro paredes internas do Canvas.
- Ataques de grave/agudo impulsionam posição, brilho e linhas sem alterar a saída sonora.
- Build Release: `BUILD SUCCESSFUL`.
- APK: `NEXUS-MUSIC-2-v1.143-PLEXUS-COLISAO-BATIDA.apk`.
- Tamanho: `1.784.604 bytes`.
- SHA-256: `def0ef7578213634e7689194ccdd051efa5a9c746959997878110e9a3a10176c`.
- Pacote, Activity e assinatura Release verificados.
- Asset HTML fonte e empacotado com SHA-256 idêntico: `f4433180c5215c29109485bf0f8d5945f036bdc740893eae35f0d00879bbc6b4`.

## Otimização de fluidez do plexus — 20/09/2026

- Removido `shadowBlur` por ponto, que provocava repaints caros no WebView.
- Linhas agora são desenhadas em uma única trilha por quadro, com um único `stroke()`.
- Loop migrado de `setTimeout` encadeado para `requestAnimationFrame` com gate de aproximadamente 30 FPS durante reprodução.
- Mantidos pontos móveis, colisão nas quatro paredes, análise de grave/agudo e impulsos da batida.
- `scripts/validar-interface.sh`: JS válido e CSS `648/648`.
- Viewport HTTP `412×915` revisada sem overflow, cortes ou falhas visuais.
- Não foi gerado APK nesta etapa; a otimização aguarda teste de áudio/fluidez no aparelho.

## APK de teste — áudio e plexus fluido

- Build Release gerada após a otimização do loop do plexus.
- APK: `NEXUS-MUSIC-2-v1.143-TESTE-AUDIO-PLEXUS-FLUIDO.apk`.
- Tamanho: `1.784.727 bytes`.
- SHA-256: `99c9cd1449c089e3ce8086afb89ebd38d49e0a0c021490b418b4c8945bd95b34`.
- JS válido, CSS `648/648`, Gradle `BUILD SUCCESSFUL`.
- Pacote, Activity principal e assinatura Release verificados.
- Asset fonte e empacotado com SHA-256 idêntico: `21d80e96d3f298478acf364fb69ebcd32e28e3312f5d4f9f77a4ff3349a9173f`.

## Áudio direto + plexus leve — correção de qualidade

- Diagnóstico no aparelho: `MediaElementSource → AnalyserNode → destination` estava associado à perda de volume/clareza no WebView Android.
- Removido o grafo Web Audio do caminho do áudio; o `<audio>` volta a sair diretamente pelo WebView.
- Plexus agora é decorativo e determinístico, sem interceptar o sinal.
- Reduzido de 30 para 18 pontos.
- Conexões limitadas aos vizinhos, sem comparação de todos os pares.
- Build Release: `BUILD SUCCESSFUL`.
- APK: `NEXUS-MUSIC-2-v1.143-AUDIO-DIRETO-PLEXUS-LEVE.apk`.
- Tamanho: `1.784.230 bytes`.
- SHA-256: `2523aef6710c481acf21de006ee878d2be231b7b451636bdc0d03f5f177a83da`.
- Verificação do APK: pacote, Activity e assinatura válidos.
- Asset fonte/empacotado idêntico: `ac8c8b35a2fbf9f2ac76d60f0e56201238c3d030d02373b618ea4dc3a9bc9c6f`.
- Conteúdo do APK confirmado sem `createMediaElementSource`.

## v1.144 — volume abaixo dos controles musicais — 22/09/2026

- Reorganização visual somente no `app/src/main/assets/index.html`.
- Ordem aprovada no box: progresso → anterior/play/próxima → volume → ÁLBUNS/PASTAS/FAVORITOS.
- O volume (`#volume`) permaneceu funcional; teste no navegador alterou `100% → 37%`.
- `node --check`: aprovado; console do navegador sem erros.
- Viewport HTTP validada em `412×915`, sem overflow ou sobreposição.
- Build release: `BUILD SUCCESSFUL`.
- APK: `NEXUS-MUSIC-2-v1.144-volume-abaixo-controles.apk`.
- VersionCode: `142`; VersionName: `1.144`.
- Tamanho: `1.944.215 bytes`.
- SHA-256: `2062c621a646ce4d919850e55de08367d08a1603bdd231cce0130670f611b1c2`.
- Pacote: `com.leo.nexusmusic2`.
- Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura release válida nos esquemas v2 e v3; integridade ZIP OK.
- SHA-256 do `assets/index.html` fonte e empacotado idêntico: `bda5584cb747f2bd0ea655676b6becd4897dbc4f342ec99ab7cb561ff28501fe`.

## v1.145 — cards metálicos 150×150 — 22/09/2026

- Cards de PASTAS e de álbuns/favoritos definidos em grade centralizada de duas colunas, 150×150 px, cantos de 16 px e contorno metálico gradiente de 2 px.
- Conteúdo e handlers existentes foram preservados; validação por DOM confirmou cards de pastas 150×150 e cartão de álbum 150×150.
- Viewport HTTP 412×915 revisada; sem overflow horizontal nem corte visível.
- `./scripts/validar-interface.sh`: JS válido; CSS 764/764.
- `./gradlew assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (41 tarefas).
- APK: `NEXUS-MUSIC-2-v1.145-cards-metalicos-150x150.apk`.
- VersionCode: `143`; VersionName: `1.145`.
- Tamanho: `1.944.966 bytes`.
- SHA-256: `fe832551f07301031fcab6321452bc6c816d6a6a07dfe81bb4ad05fa8b720d6e`.
- Pacote: `com.leo.nexusmusic2`; Activity: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release verificada nos esquemas v2/v3 (RSA 4096); integridade ZIP OK.
- SHA-256 do `assets/index.html` fonte e empacotado idêntico: `43967542cd6969fe5d87b95f19299d2c8a0322380456707c1973923925a50257`.

## v1.146 — cards pretos, contorno metálico de 1 px e containers alinhados — 23/09/2026

- Cards de ÁLBUNS, PASTAS e FAVORITOS passaram para fundo preto semitransparente (`rgba(7,7,7,.64)`) e aro metálico separado de 1 px com máscara CSS.
- O container da listagem e a barra de navegação receberam o mesmo padrão visual do container pai da Home: fundo escuro, raio de 24 px, borda clara e sombra interna/externa.
- Dimensões dos cards (150×150 px), navegação, handlers e lógica do player foram preservados.
- `./scripts/validar-interface.sh`: JS válido; CSS 768/768.
- HTTP local: página respondeu 200.
- Build: `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace` → `BUILD SUCCESSFUL` (42 tarefas).
- APK: `NEXUS-MUSIC-2-v1.146-cards-pretos-contorno-1px.apk`.
- VersionCode: `144`; VersionName: `1.146`.
- Tamanho: `1.945.263 bytes`.
- SHA-256: `3fddd7052dadbe7b901b7a568c0ba1c11115347d277a44bbb0ec75f2d7fc255b`.
- Pacote: `com.leo.nexusmusic2`; Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release válida nos esquemas v2/v3 (RSA 4096); integridade ZIP OK.
- SHA-256 do `assets/index.html` fonte e empacotado idêntico: `a5dc5f5e98890d1094429d888e18417716c931f39a91324f713d973f40088b88`.

## v1.147 — estrela dos favoritos acima do card — 23/09/2026

- Corrigida a sobreposição visual/interativa da estrela de favoritos pelo `play-overlay` da capa.
- A estrela recebeu camada final com `z-index:10`, posição no canto superior direito, `pointer-events:auto` e display flex.
- O overlay recebeu `pointer-events:none`; o card foi isolado com `isolation:isolate`.
- Teste DOM em viewport 412×915: card 150×150; estrela 24×24; os cinco pontos amostrados na área da estrela retornaram `BUTTON.fav-btn`; sem overflow horizontal.
- `./scripts/validar-interface.sh`: JS válido; CSS 772/772.
- Build: `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace` → `BUILD SUCCESSFUL` (42 tarefas).
- APK: `NEXUS-MUSIC-2-v1.147-estrela-favoritos-corrigida.apk`.
- VersionCode: `145`; VersionName: `1.147`.
- Tamanho: `1.945.449 bytes`.
- SHA-256: `c16161db5d94d8eb04043914943262f51f36ca69f92dcc7f86e027b2f09408b1`.
- Pacote: `com.leo.nexusmusic2`; Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release válida nos esquemas v2/v3 (RSA 4096); integridade ZIP OK.
- SHA-256 do `assets/index.html` fonte e empacotado idêntico: `f5ef6855ab603ef23e0e8526fb1a7db16e4a384b0566ac32bd1186371a9a9ce1`.

## v1.148 — rodapé fixo transparente e comportamento do teclado — 23/09/2026

- Build solicitada por Leo a partir das mudanças já feitas no rodapé da Home; sem alterações funcionais adicionais.
- Rodapé transparente, sem caixa/borda/sombra, com layout compacto; `adjustNothing` configurado para o teclado não redimensionar a janela.
- `./scripts/validar-interface.sh`: JavaScript válido; CSS 784/784.
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas).
- APK: `NEXUS-MUSIC-2-v1.148-rodape-fixo-transparente.apk`.
- VersionCode: `146`; VersionName: `1.148`.
- Tamanho: `1.946.191 bytes`.
- SHA-256: `12e12341c868e6bdfe4b5b60ffb11cb28515bbd2a367d23cf9b4dcec04c6de63`.
- Pacote: `com.leo.nexusmusic2`; Activity: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release válida nos esquemas v2/v3 (RSA 4096); integridade ZIP OK.
- SHA-256 do `assets/index.html` fonte e empacotado idêntico: `35bbaf55e780acf8cef70645464389fca8c2dd3be8c616929526e090f1edb37a`.
- Sem dispositivo Android conectado via ADB; teste físico pendente.

## v1.149 — build release atual — 23/09/2026

- APK recompilado a partir do estado atual do projeto, sem alterar código nesta sessão.
- `./scripts/validar-interface.sh`: JavaScript válido; CSS `784/784`; asset com 323 KB.
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas).
- VersionCode: `147`; VersionName: `1.149`.
- APK: `NEXUS-MUSIC-2-v1.149-release.apk`.
- Tamanho: `1.946.187 bytes`.
- SHA-256 do APK: `eda853affd0e80806fc634f82e057a61dfa11711f89945e0d19a5dc9cf394608`.
- Pacote: `com.leo.nexusmusic2`; Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release verificada: v2 e v3 válidas; `zipalign` e integridade ZIP OK.
- SHA-256 do `assets/index.html` fonte e empacotado idêntico: `35bbaf55e780acf8cef70645464389fca8c2dd3be8c616929526e090f1edb37a` (`332.281 bytes`).
- APK contém os 12 assets esperados: HTML, 6 fontes e 5 imagens.
- `adb` não está disponível nesta máquina; teste em aparelho físico permanece pendente.

## v1.150 — teclado não cobre a pesquisa do YouTube — 23/09/2026

- Corrigido o comportamento da Home quando o teclado Android abre no campo `#ytUrl`.
- `MainActivity` e `AndroidManifest.xml`: `adjustNothing` → `adjustResize`, permitindo que a WebView acompanhe a área útil reduzida.
- O HTML agora usa a rolagem interna de `.content`, adiciona espaço temporário durante o foco e acompanha `visualViewport.resize/scroll` para manter o input acima do teclado.
- Ao perder o foco, o ajuste temporário é removido; pesquisa, prévia, download, player e restante do layout foram preservados.
- Backup dos arquivos anteriores salvo em `_backup/` antes da edição.
- `./scripts/validar-interface.sh`: JavaScript válido; CSS `786/786`; asset com 326 KB.
- Viewport HTTP validada em `412×915`: Home inteira, campo YouTube visível e sem overflow horizontal aparente.
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas).
- APK: `NEXUS-MUSIC-2-v1.150-teclado-youtube-ajustado.apk`.
- VersionCode: `148`; VersionName: `1.150`.
- Tamanho: `1.947.253 bytes`.
- SHA-256 do APK: `adb0bf3d54d7e943d97c78ed18a83caf49465c5cc0f0153d7560a33d9cf1e01a`.
- Pacote: `com.leo.nexusmusic2`; Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release verificada: v2 e v3 válidas; `zipalign` e integridade ZIP OK.
- SHA-256 do `assets/index.html` fonte e empacotado idêntico: `cc5fc23acda3bdbe7852868c10231a275c1ebf5fcbae35439746d1f5b1868ef6` (`336.190 bytes`).
- `adb` não está disponível nesta máquina; teste físico do teclado permanece pendente.

## v1.151 — Home sem pastas (navegação por pastas removida) — 24/09/2026

- Build a partir do estado atual do fonte, que havia sido alterado após a v1.150: o visor passa a mostrar somente o total (`000 MÚSICAS`), o botão **PASTAS** é substituído por **🏠 INÍCIO** e a navegação por pastas (gêneros→álbuns→faixas) e o gesto de swipe foram removidos, com `renderHome()` no lugar.
- Backup do fonte e do `build.gradle` salvos em `_backup/` antes do build.
- `./scripts/validar-interface.sh`: JavaScript válido; CSS `786/786`; asset com 312 KB.
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas).
- APK: `NEXUS-MUSIC-2-v1.151-home-sem-pastas.apk`.
- VersionCode: `149`; VersionName: `1.151`.
- Tamanho: `1.943.424 bytes`.
- SHA-256 do APK: `e7b64337aea8ac25e8c938f41828c1ff8f8ad561f5e78f224f2a1920b7f8c79d`.
- Pacote: `com.leo.nexusmusic2`; Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release verificada: v2 e v3 válidas (RSA 4096); `zipalign` e integridade ZIP OK.
- SHA-256 do `assets/index.html` fonte e empacotado idêntico: `f451075b268854596653e15457a7a14eb00162d13dc4a7bdf1d714d5014f1818` (`321.615 bytes`).
- Viewport HTTP validada em `412×915` (captura enviada).
- `adb` não está disponível nesta máquina; teste em aparelho físico permanece pendente.

## v1.152 — containers ÁLBUNS/FAVORITOS e YouTube com o padrão do pai — 24/09/2026

- `#tracksSection` (onde ÁLBUNS e FAVORITOS renderizam) e `.yt-panel` (YouTube download) passaram a usar o mesmo visual do container pai `.player-top`: `background:rgba(0,0,0,.05)`, `backdrop-filter:blur(8px)`, `border-radius:24px`, `border:0`, `box-shadow:none`.
- A regra `.player-top .yt-panel` (que forçava `border-radius:0`, `padding:14px 0 0` e `border-top`) foi ajustada para o mesmo padrão do pai.
- Nenhuma alteração de lógica, medidas de layout, handlers ou áudio.
- `./scripts/validar-interface.sh`: JavaScript válido; CSS `787/787`; asset com 313 KB.
- Estilos computados conferidos na página HTTP: os três containers (`playerTop`, `tracksSection`, `ytPanel`) retornam `bgColor rgba(0,0,0,.05)`, `radius 24px`, `blur(8px)`, `border 0`, `shadow none`.
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas).
- APK: `NEXUS-MUSIC-2-v1.152-containers-padrao-pai.apk`.
- VersionCode: `150`; VersionName: `1.152`.
- Tamanho: `1.943.384 bytes`.
- SHA-256 do APK: `ebdb99586766e12b170c87e963caad06f84d4ef89cf65a44d3eb4db62b63e5e1`.
- Pacote: `com.leo.nexusmusic2`; Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release verificada: v2 e v3 válidas (RSA 4096); `zipalign` e integridade ZIP OK.
- SHA-256 do `assets/index.html` fonte e empacotado idêntico: `e96fb9c31b9726f1dd1670d3732af64ec34a93f057ab6137872e3080a97cb3b8`.
- `adb` não está disponível nesta máquina; teste em aparelho físico permanece pendente.

## v1.153 — teclado: input sobe ao topo e rodapé não sobe — 24/09/2026

- Com o teclado aberto na pesquisa do YouTube, o campo `#ytUrl` agora sobe até o **topo da tela visível** (folga de 12px), em vez de apenas ficar acima do teclado.
- O rodapé fixo (`.site-footer`) **não sobe mais**: com o teclado ativo ele é ocultado (`#content.yt-keyboard-active .site-footer{display:none}`) e volta ao perder o foco.
- Espaço de rolagem passou a ser calculado em JS via variável `--yt-pad` no `.content` (antes era fixo em `min(55vh,420px)`, insuficiente para o campo chegar ao topo).
- `--yt-pad` é removido no `blur`, restaurando o layout original.
- Lógica preservada: handlers de foco/blur, Enter para pesquisar, eventos de `visualViewport`, download e player intactos.
- `./scripts/validar-interface.sh`: JavaScript válido; CSS `788/788`; asset com 313 KB.
- Teste real via CDP em viewport mobile `412×915` com a janela reduzida para `412×530` (simulando o teclado): topo do input passou de `566px` para `12px`; rodapé `display:none`; `scrollTop 554` com `maxScroll 664`.
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas).
- APK: `NEXUS-MUSIC-2-v1.153-teclado-input-topo-rodape-corrigido.apk`.
- VersionCode: `151`; VersionName: `1.153`.
- Tamanho: `1.943.598 bytes`.
- SHA-256 do APK: `7be34571fb5575da4e352739c16f1f0d2f73d8c731090bf58af0b3971930a243`.
- Pacote: `com.leo.nexusmusic2`; Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release verificada: v2 e v3 válidas (RSA 4096); integridade ZIP OK.
- SHA-256 do `assets/index.html` fonte e empacotado idêntico: `486f413998cbbd3d73ebf1be1b3d318cfa606de4d98f6798fe9d3a8b412b2a5f`.
- `adb` não está disponível nesta máquina; teste em aparelho físico permanece pendente.
- ✅ **Aprovado por Leo em 24/09/2026** — "Ficou perfeito". Comportamento congelado: input sobe ao topo e rodapé não sobe com o teclado.

## v1.154 — containers unificados em ÁLBUNS/FAVORITOS e contador centralizado — 24/09/2026

- A pedido: ao abrir **ÁLBUNS** ou **FAVORITOS** a listagem (`#tracksSection`) agora cola no container pai (`.player-top`) formando um bloco único — vão `0px`, sem canto arredondado no meio.
- Os títulos `// ÁLBUNS` e `// FAVORITOS` foram **removidos** (o contador permanece).
- Contador **centralizado** e convertido em pílula: borda clara, gradiente sutil, fonte Orbitron, maiúsculas, `letter-spacing 2.4px` e brilho suave. Textos: `00 ÁLBUNS` e `00 FAVORITAS`.
- Implementação: classe `view-listagem` no `#content`, ligada por `marcarViewListagem(true)` em `renderAlbums()`/`verFavoritos()` e desligada em `renderHome()`/`renderTracks()`. O CSS cuida do resto — nenhuma lógica de áudio, handlers ou navegação foi alterada.
- `./scripts/validar-interface.sh`: JavaScript válido; CSS `793/793`; asset com 316 KB.
- Medição via CDP em `412×915`: ÁLBUNS/FAVORITOS → `gap 0px`, `ptBottomLeftRadius 0px`, `tsTopLeftRadius 0px`, header `justify-content:center`; Home → raios `24px` e `margin-top 22px` restaurados.
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas).
- APK: `NEXUS-MUSIC-2-v1.154-containers-unificados-contador.apk`.
- VersionCode: `152`; VersionName: `1.154`.
- Tamanho: `1.944.363 bytes`.
- SHA-256 do APK: `63cb705f1719ad0a3bffb382a1b0677b27aad2455bb6ac1c70e41262bc9a7f68`.
- Pacote: `com.leo.nexusmusic2`; Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release verificada: v2 e v3 válidas (RSA 4096); integridade ZIP OK.
- SHA-256 do `assets/index.html` fonte e empacotado idêntico: `5b251be1a8dbb65bfa902fc9797c8e74138d598a92d7111d108d4b73aed6c261`.
- ✅ **Aprovado por Leo em 24/09/2026** — "Perfeito" (contador centralizado e containers unificados).

## v1.155 — superfície única (container pai estendido) em ÁLBUNS/FAVORITOS — 24/09/2026

- Correção da "marcação de divisão" sutil na costura: eram **dois** boxes translúcidos empilhados (pai + lista), cada um com `rgba(0,0,0,.05)` e `blur(8px)`, então a translucidez se somava e não ficava uniforme.
- Agora o `.player-top` é a **única superfície**: um prolongamento em `::after` (`top:100%`, `height:var(--lista-altura)`, mesmo fundo/`blur`/raio embaixo) cobre a área da lista, e o `#tracksSection` entra **transparente**, sem superfície própria.
- A altura é medida em JS (`ajustarAlturaListagem`) e atualizada por `ajustarAlturaListagemDepois` + `ResizeObserver` + listeners de `load`/`error` das capas, garantindo alinhamento quando a lista cresce ou as imagens carregam.
- `./scripts/validar-interface.sh`: JavaScript válido; CSS `794/794`; asset com 320 KB.
- Medição via CDP em `412×915`: ÁLBUNS `--lista-altura 144px` = `secH 144px`; FAVORITOS `280px` = `280px`; na Home a variável é removida e o pai volta a `border-radius:24px`.
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas).
- APK: `NEXUS-MUSIC-2-v1.155-superficie-unica.apk`.
- VersionCode: `153`; VersionName: `1.155`.
- Tamanho: `1.945.254 bytes`.
- SHA-256 do APK: `81ec3fb6e72871b714d2712df94d692d8b55f83f2079f033f700df0674747fc2`.
- Pacote: `com.leo.nexusmusic2`; Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release verificada: v2 e v3 válidas (RSA 4096); integridade ZIP OK.
- SHA-256 do `assets/index.html` fonte e empacotado idêntico: `f4e1ec72f7b0784663b2f11120c162aa965a5f9bb821414c55e7692619e75b22`.
- `adb` não está disponível nesta máquina; teste em aparelho físico permanece pendente.

## v1.156 — cards 170×170 e fundo mais transparente — 24/09/2026

- Cards de ÁLBUNS/FAVORITOS passaram de 150×150 para **170×170** (grade `repeat(2,170px)`), mantendo o aro metálico de 1px e o raio de 16px.
- Fundo mais transparente: cards `rgba(7,7,7,.64)` → `rgba(7,7,7,.34)`; containers (`.player-top`, `#tracksSection`, `.toolbar`, `.yt-panel`) `rgba(0,0,0,.05)` → `rgba(0,0,0,.02)`.
- Nenhuma alteração de lógica, handlers, áudio ou navegação.
- `./scripts/validar-interface.sh`: JavaScript válido; CSS `793/793`; asset com 317 KB.
- Medição via CDP em `412×915`: grade `170px 170px`, card `170×170`, fundo do card `rgba(7,7,7,0.34)`, containers `rgba(0,0,0,0.02)`, sem overflow horizontal.
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas).
- APK: `NEXUS-MUSIC-2-v1.156-cards-170-fundo-transparente.apk`.
- VersionCode: `154`; VersionName: `1.156`.
- Tamanho: `1.944.540 bytes`.
- SHA-256 do APK: `5aac2b45de72c8016b05d642c4366a65e2434b82737164d477af74b97ca81449`.
- Pacote: `com.leo.nexusmusic2`; Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release verificada: v2 e v3 válidas; integridade ZIP OK.
- SHA-256 do `assets/index.html` fonte e empacotado idêntico: `ed60d8e906156791872dd69847484c3c0d0de8793b91d471452eacf7945a9372`.
- ✅ **Aprovado por Leo em 24/09/2026.**
- `adb` não está disponível nesta máquina; teste em aparelho físico permanece pendente.

## v1.157 — estrela de favoritos na parte de baixo do card — 24/09/2026

- Estrela de favoritos movida do topo para a **parte de baixo do card, à direita** (`bottom:8px; right:8px`).
- Tamanho 24px → **30px**; virou botão circular com aro claro, gradiente de vidro, `blur(6px)`, sombra e brilho interno.
- Estado marcado: dourada `#ffd23f` com halo brilhante e fundo dourado translúcido; hover cresce (`scale 1.1`) com brilho.
- `.music-info` ganhou `padding-right:38px` para a estrela não cobrir o texto.
- Aplicado em ÁLBUNS e FAVORITOS (mesmos cards). Handlers e lógica de `toggleFav` intactos.
- `./scripts/validar-interface.sh`: JavaScript válido; CSS `796/796`; asset com 319 KB.
- Medição via CDP em `412×915`: estrela `30×30`, a `8-9px` da base do card, `border-radius:50%`, `font-size:16px`; card `170×170`; sem overflow horizontal.
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas).
- APK: `NEXUS-MUSIC-2-v1.157-estrela-rodape-card.apk`.
- VersionCode: `155`; VersionName: `1.157`.
- Tamanho: `1.944.864 bytes`.
- SHA-256 do APK: `572643c566cee3957afd26a25558f3bb651715eb21929952ab6d5e8c00c09a20`.
- Pacote: `com.leo.nexusmusic2`; Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release verificada: v2 e v3 válidas; integridade ZIP OK.
- SHA-256 do `assets/index.html` fonte e empacotado idêntico: `7c057b54242c41923c485c8d8084c709c07c9317ebb0c25f4fe968b2e2f9ad7f`.
- ✅ **Aprovado por Leo em 24/09/2026** — "Perfeito".
- `adb` não está disponível nesta máquina; teste em aparelho físico permanece pendente.

## v1.158 — botões, estrela no rodapé, abertura no YouTube — 24/09/2026

- Botões ÁLBUNS/INÍCIO/FAVORITOS com o novo visual (modelo `.btn-base`/`.btn-m16`): fundo preto + aro metálico **2px** em gradiente `linear-gradient(135deg,#fff,#8a8a8a,#4a4a4a)`, `background-clip: padding-box, border-box`, raio 8px, fonte **CafeNero** 700 a 10.5px, brilho `text-shadow`, hover `opacity:.9` + `scale(1.02)`.
- Emoji 🏠 removido do botão INÍCIO (ficou só `INÍCIO`).
- Estrela de favoritos no rodapé direito do card (v1.157): 30px, aro de vidro, dourada com halo quando marcada.
- Knob do slider de volume reduzido de 24px para **20px** (webkit e moz).
- Tentativa de aplicar o aro metálico nos controles de música (◀ ⏯ ▶) foi **reprovada por Leo** e revertida — voltaram ao original (redondos, borda 1px).
- Abertura: o app abre no painel **YouTube Download**, mostrando só o visor/player, **sem carregar os cards** de música.
- Corrigido bug introduzido durante a edição: a função `favTracks()` havia sido apagada e foi restaurada.
- `./scripts/validar-interface.sh`: JavaScript válido; CSS `797/797`; asset com 320 KB.
- Medições via CDP em `412×915`: botões `2px` de aro, `10.5px` de fonte, 103×36; controles `1px` redondos 52/66/52; estrela 30×30 a 9px da base; slider `250×16`; abertura com `ytPanel: block`, cards `0`, sem overflow.
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas).
- APK: `NEXUS-MUSIC-2-v1.158-botoes-estrela-youtube.apk`.
- VersionCode: `156`; VersionName: `1.158`.
- Tamanho: `1.945.077 bytes`.
- SHA-256 do APK: `ac1763f60ce6287df955068df6d2811f4101cd30b5de0ec97096f2568514eefb`.
- Pacote: `com.leo.nexusmusic2`; Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release verificada: v2 e v3 válidas; integridade ZIP OK.
- SHA-256 do `assets/index.html` fonte e empacotado idêntico: `839da932fc4d19f663fe278a8ccf3535be6ec347c68a45d0c4088686243e796a`.
- `adb` não está disponível nesta máquina; teste em aparelho físico permanece pendente.

## v1.159 — botão BAIXAR com estilo do INÍCIO e estados corrigidos — 24/09/2026

- Botão **BAIXAR** do card de resultado do YouTube passou a usar o mesmo visual dos botões de navegação: fundo preto + aro metálico **2px** (`linear-gradient(135deg,#fff,#8a8a8a,#4a4a4a)`), raio 8px, fonte do subtítulo (Rajdhani) com brilho.
- Emoji ⬇ removido dos rótulos (`BAIXAR`, `BAIXANDO`) — a seta colorida destoava do aro metálico.
- **Largura fixa em 118px** nos três estados. O botão crescia porque a regra `.yt-result .yt-btn.yt-result-download` (com `.yt-btn`, mais específica) impunha `width:auto` — agora fixa ali também.
- **Porcentagem duplicada corrigida:** o rótulo agora vira `BAIXANDO` durante o download (antes continuava `BAIXAR` ao lado do `45%`).
- **Barra de progresso visível:** o `.yt-download-fill` ganhou posição/cor próprias (`top/bottom/left:0`, gradiente claro) para aparecer sobre o fundo preto do novo tema; `overflow:hidden` reposto no botão.
- Estado pressionado mantém o aro metálico 2px (antes voltava à borda branca de 1px do tema antigo).
- `./scripts/validar-interface.sh`: JavaScript válido; CSS `799/799`; asset com 322 KB.
- Medições via CDP em `412×915`: normal `118px`, baixando `118px`, convertendo `118px`; fill em 45% = 51px/118px; rótulos `BAIXAR` → `BAIXANDO 45%` → `CONVERTENDO`.
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas).
- APK: `NEXUS-MUSIC-2-v1.159-botoes-baixar-corrigido.apk`.
- VersionCode: `157`; VersionName: `1.159`.
- Tamanho: `1.945.500 bytes`.
- SHA-256 do APK: `41f09647916412624aa1a25be1beef4b88a43ca3ac1d5d89db48013def1414bb`.
- Pacote: `com.leo.nexusmusic2`; Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release verificada: v2 e v3 válidas; integridade ZIP OK.
- SHA-256 do `assets/index.html` fonte e empacotado idêntico: `3f621b2b7256dd4a1b21790029daaac7f325e9d34d115e61479d1d4d181b7f9f`.
- `adb` não está disponível nesta máquina; teste em aparelho físico permanece pendente.

## v1.160 — novo fundo do app (BMW preto na floresta roxa) — 24/09/2026

- Fundo do app trocado a pedido do Leo: imagem antiga (padrão pixelado escuro, `img/fundo.jpg`) foi substituída pela imagem BMW/floresta roxa.
- Substituição direta do asset `assets/img/fundo.jpg`; nenhum CSS alterado.
- APK gerado e entregue em conversa. Depois, antes de um novo build, Leo escolheu a imagem hazard escura; ela está incluída no release seguinte, v1.161.

## v1.161 — timeline 220×15 e fontes maiores — 24/09/2026

- Fundo definitivo escolhido por Leo: arte hazard escura (caveiras, símbolos radioativos e máscaras), em `assets/img/fundo.jpg`, **736×1594**, JPEG 155.569 bytes. Luminância média medida: **9,83%**.
- Botões ÁLBUNS/INÍCIO/FAVORITOS aumentados de **9,5px para 11px** Rajdhani.
- Botão BAIXAR aumentado para **11px** (a regra CSS efetivamente vencedora antes era 9px; foi localizada via `CSS.getMatchedStylesForNode`). Mantida largura fixa de 118px; rótulos `BAIXAR`, `BAIXANDO 100%` e `CONVERTENDO` cabem.
- Timeline musical aumentada para **220×15px** a 412px. O conjunto com os relógios mede 312px. Viewports 390/375/360/320 medidos sem overflow; a barra encolhe proporcionalmente.
- Viewport 412×915 rasterizado e enviado; botões e timeline conferidos sem corte/desalinhamento.
- `bash scripts/validar-interface.sh`: `JS valido | CSS 804/804 | 323 KB`.
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas); relatório Lint tem 8 warnings não bloqueantes (SharedPreferences commit, JS do WebView, data extraction rules e checks de SDK obsoletos).
- APK: `NEXUS-MUSIC-2-v1.161-timeline-220x15.apk`.
- VersionCode: `159`; VersionName: `1.161`.
- Tamanho: `1.896.884 bytes`.
- SHA-256 do APK: `76f61bf0ea102ca82c187cc17664030dfd845d40993585c17bdae2e4b9ae4724`.
- SHA-256 do `assets/index.html` fonte e empacotado: `5b0c8381a77faa1aca6bfc077f319a5562b2a950b3307def262d9b5dc042fb9d`.
- SHA-256 do `assets/img/fundo.jpg` fonte e empacotado: `ec825eaba1f49cf1d86700cb2f9bbdab89abe1195badd01737bc0711d22bd5a9`.
- Pacote: `com.leo.nexusmusic2`; Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release v2 e v3 válidas; `unzip -tq` e `zipalign -c 4` aprovados. Cópia para envio em `/tmp/nexus-music2-v1.161.apk`, SHA-256 idêntico.
- `adb` não está disponível nesta máquina; teste em aparelho físico permanece pendente.

## v1.162 — capa padrão NEXUS, título 3px e ondas senoidais na timeline — 24/09/2026

- **Capa padrão** `assets/img/capa-padrao.jpg` (736×736, 63.097 bytes) — arte NEXUS VIBRA enviada pelo Leo. Usada como fallback quando:
  - o player não tem faixa carregada (`#retroCover` abre já com a capa, classe `visivel`);
  - a faixa/álbum não tem `cover`;
  - uma capa falha ao carregar (onerror troca para a padrão; só remove se a própria padrão falhar).
- **Título da música no visor**: subiu 3px (translateY 8px → 5px) e mantido centralizado (`justify-content:center` + `text-align:center` no `#retroTitle`).
- **Ondas senoidais na timeline**: duas linhas SVG de 1px (opacidades 0,72 e 0,43, fases invertidas) dentro de `#progress`, `pointer-events:none` (seek/arrasto intactos). Movimento linear contínuo com período exato (sem salto no loop); velocidades 6,4s/8,2s em sentidos opostos. Liga no evento real `playing`; desliga em `pause`, `ended`, `waiting`, `stalled`, `emptied`. Respeita `prefers-reduced-motion`.
- `bash scripts/validar-interface.sh`: `JS valido | CSS 824/824 | 327 KB`.
- Validação CDP 412×915: WAV sintético de 2s ativou as duas animações (`progressSineOne/Two`) com `is-playing`; pausa as desligou; seek a 75% via CDP atualizou `currentTime` (1,66/2,00s) e o fill; sem overflow em 412/390/375/360/320px.
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas); Lint 8 warnings não bloqueantes (mesmos de sempre).
- APK: `NEXUS-MUSIC-2-v1.162-capa-padrao-e-ondas.apk`.
- VersionCode: `160`; VersionName: `1.162`.
- Tamanho: `1.961.243 bytes`.
- SHA-256 do APK: `f6f247cf6ab924b23178405ba10a6417a5331bb4f779fd661b3c683bba86a106`.
- SHA-256 do `assets/index.html` fonte e empacotado: `387f651c787a76b08081366a8b2f6937ce31db7814b36fc03f96368bdbd72d9d`.
- SHA-256 do `assets/img/capa-padrao.jpg` fonte e empacotado: `69cf627f5f9b8221c95597460d718ec682a6e7d1c28dce7b05b949e362579f8a`.
- Pacote: `com.leo.nexusmusic2`; Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release v2 e v3 válidas; `unzip -tq` e `zipalign -c 4` aprovados. Cópia de envio `/tmp/nexus-music2-v1.162.apk` com SHA-256 idêntico.
- `adb` não está disponível nesta máquina; teste em aparelho físico permanece pendente.
## v1.163 — ondas da timeline mais fluidas e rápidas — 24/09/2026

- Comprimento de onda das duas senoides da timeline aumentado de 42px para **126px** — curvas mais longas e suaves (menos "serrilhado").
- Velocidade do deslocamento ~2,7× maior: ciclo de 6,4s/8,2s passou para **2,6s/3,4s**, com o mesmo período exato por ciclo (loop sem salto). Deslocamento medido ≈48 px/s (antes ≈13 px/s).
- Nada mais alterado: 1px de espessura, opacidades, `pointer-events:none`, liga/desliga por `playing`/`pause`/`ended`/`waiting`/`stalled`/`emptied` e o respeito a `prefers-reduced-motion`.
- `bash scripts/validar-interface.sh`: `JS valido | CSS 824/824 | 328 KB`.
- Validação CDP 412×915 com WAV sintético: as duas animações ativas em `is-playing` (transform avançando), pausa desligando, seek a 75% mudando `currentTime` e fill; barra 220×15 preservada.
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas).
- APK: `NEXUS-MUSIC-2-v1.163-ondas-fluidas-rapidas.apk`.
- VersionCode: `161`; VersionName: `1.163`.
- Tamanho: `1.961.385 bytes`.
- SHA-256 do APK: `2f53ae07a3b80a00551e29f3492c4b032cb9ef1009838fb660fbfe8a097b6b35`.
- SHA-256 do `assets/index.html` fonte e empacotado: `81e2b654bc6fb6df80c07829fc9be12ee3fc2347adf373750ef581a945b0c1bd`.
- Pacote: `com.leo.nexusmusic2`; Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release v2 e v3 válidas; `unzip -tq` e `zipalign -c 4` aprovados. Cópia de envio `/tmp/nexus-music2-v1.163.apk` com SHA-256 idêntico.
- `adb` não está disponível nesta máquina; teste em aparelho físico permanece pendente.
## v1.164 — prévias do YouTube param ao fechar o app — 24/09/2026

- **Bug corrigido:** o player principal já era pausado ao fechar o app, mas as **prévias do YouTube** continuavam tocando. Causa: `MainActivity` chamava apenas `audio.pause()`; as prévias são elementos `Audio()` separados, guardados em `ytAudiosPrevias`, e nunca eram tocadas nesse caminho.
- **Interface (`index.html`):** nova função `nx_pararTodoAudio()` — ponto único que pausa o player principal E as prévias (`ytPararPrevia()`). Além disso, `pagehide` e `beforeunload` agora interrompem as prévias.
- **Deliberadamente NÃO** se pausa o áudio em `visibilitychange`: minimizar o app não pode interromper a música principal (comportamento aprovado, sustentado pelo `PlayerService`).
- **Nativo (`MainActivity.java`):** os dois caminhos de fechamento — `onDestroy()` e a confirmação por duplo toque em voltar — passaram a chamar `nx_pararTodoAudio()`, com fallback para o `audio.pause()` antigo caso a função não exista.
- **Prova em navegador (CDP 412×915):** com WAV sintético, antes da chamada `principalTocando: true` e `previasTocando: 1`; depois de `nx_pararTodoAudio()`, `principalPausado: true`, `previasTocando: 0`, `previasNoSet: 0`, `src` liberado. Sem erros de console.
- **Prova de empacotamento nativo:** a string `nx_pararTodoAudio` está presente em `classes.dex` do APK (1 ocorrência) — confirma que o Java recompilado entrou.
- `bash scripts/validar-interface.sh`: `JS valido | CSS 824/824 | 329 KB`.
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas); Lint 8 warnings não bloqueantes.
- APK: `NEXUS-MUSIC-2-v1.164-previas-param-ao-fechar.apk`.
- VersionCode: `162`; VersionName: `1.164`.
- Tamanho: `1.961.817 bytes`.
- SHA-256 do APK: `2b55f8117d4c565e8ad7072be1699b8546adc50affb837ae18af04880bf0b720`.
- SHA-256 do `assets/index.html` fonte e empacotado: `5daeb0e28d41a5590b70319dc6559a17cf5271398ae85e5ecd2466183feb8de3`.
- Pacote: `com.leo.nexusmusic2`; Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release v2 e v3 válidas; `unzip -tq` e `zipalign -c 4` aprovados. Cópia de envio `/tmp/nexus-music2-v1.164.apk` com SHA-256 idêntico.
- `adb` não está disponível nesta máquina; teste em dispositivo físico permanece pendente — o fechamento real (swipe/back) deve ser confirmado no aparelho.
## v1.165 — botões ÁLBUNS/INÍCIO/FAVORITOS e BAIXAR com a cor dos controles — 24/09/2026

- Os botões de navegação e o BAIXAR usavam **preto sólido** (`linear-gradient(rgb(0,0,0),rgb(0,0,0))`). Os controles de música (◀ ⏯ ▶) pintam `background-color: rgba(16,16,16,.90)` **mais** um véu de vidro branco (`linear-gradient(180deg, rgba(255,255,255,.12), rgba(255,255,255,.043))`), medido com `getComputedStyle` no viewport 412×915.
- Agora os quatro botões usam esse mesmo par de camadas no preenchimento, **mantendo o aro metálico 2px** (que continua ocupando apenas a borda, via terceira camada em `border-box`).
- `background-origin`/`background-clip: padding-box, padding-box, border-box` — o gradiente `#fff/#8a8a8a/#4a4a4a` permanece só na moldura; nada de borda branca opaca no meio.
- Nada mais alterado: largura fixa 118px do BAIXAR, raio 8px, fonte Rajdhani 11px, rótulos e estados (`BAIXAR`/`BAIXANDO`/`CONVERTENDO`), hover com `opacity:.9` + `scale(1.02)`.
- `bash scripts/validar-interface.sh`: `JS valido | CSS 825/825 | 330 KB`.
- Medição CDP 412×915 depois da mudança: ÁLBUNS e BAIXAR com `background-image` = véu branco + `rgba(16,16,16,.9)` + gradiente metálico; mesma transparência dos controles. Zoom visual conferido.
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas).
- APK: `NEXUS-MUSIC-2-v1.165-botoes-cor-dos-controles.apk`.
- VersionCode: `163`; VersionName: `1.165`.
- Tamanho: `1.962.070 bytes`.
- SHA-256 do APK: `23b5227c89bc1dfaa8e1968239708021011595cb1e84faa3341500c80d75090b`.
- SHA-256 do `assets/index.html` fonte e empacotado: `ad9093dca2af630146076fa802f82cf630d60cbe28887df40f9ed74b8bf1c54c`.
- Pacote: `com.leo.nexusmusic2`; Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release v2 e v3 válidas; `unzip -tq` e `zipalign -c 4` aprovados. Cópia de envio `/tmp/nexus-music2-v1.165.apk` com SHA-256 idêntico.
- `adb` não está disponível nesta máquina; teste em aparelho físico permanece pendente.
## v1.166 — botões retangulares um pouco mais escuros — 24/09/2026

- Botões **ÁLBUNS/INÍCIO/FAVORITOS** e **BAIXAR** escurecidos um pouco: preenchimento `rgba(7,7,7,.94)` + véu branco mais discreto (`rgba(.075 → .028)`). Mantém a família visual dos controles ◀ ⏯ ▶, mas um tom abaixo conforme pedido.
- **Aro metálico 2px permanece intacto**: três camadas `padding-box,padding-box,border-box`; o gradiente prateado continua somente na borda. Texto branco e Rajdhani 11px inalterados.
- Validação CDP 412×915: background computado de ambos = véu de vidro + `rgba(7,7,7,.94)` + gradiente metálico. Aro e rótulos legíveis; viewport e zoom conferidos.
- `bash scripts/validar-interface.sh`: `JS valido | CSS 825/825 | 330 KB`.
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas).
- APK: `NEXUS-MUSIC-2-v1.166-botoes-mais-escuros.apk`.
- VersionCode: `164`; VersionName: `1.166`.
- Tamanho: `1.962.061 bytes`.
- SHA-256 do APK: `408499c8e024f0266d5382d88afa6f10469710d316af157ad826136fae582e43`.
- SHA-256 do `assets/index.html` fonte e empacotado: `ca42e31507c8e7bd8ae56c5497d12488864cd68edf51ae3fe83e3d5e3d61ed0b`.
- Pacote: `com.leo.nexusmusic2`; Activity inicial: `com.leo.nexusmusic2.MainActivity`.
- Assinatura Release v2 e v3 válidas; `unzip -tq` e `zipalign -c 4` aprovados. Cópia `/tmp/nexus-music2-v1.166.apk` com SHA-256 idêntico.
- `adb` não está disponível nesta máquina; teste em aparelho físico permanece pendente.

### Rebuild da v1.166 solicitado por Leo — 24/09/2026

- Recompilação do mesmo código/versão (sem alterações de fonte ou incremento de versionCode).
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas).
- APK regenerado: `NEXUS-MUSIC-2-v1.166-botoes-mais-escuros.apk`.
- VersionCode: `164`; VersionName: `1.166`.
- Tamanho: `1.962.061 bytes`.
- SHA-256 do APK recompilado: `bd76ab2c2409a18c30afdafeb2bff6ecbdf96134b0d9b09796e1b8969471adbb`.
- `assets/index.html` empacotado e fonte: `ca42e31507c8e7bd8ae56c5497d12488864cd68edf51ae3fe83e3d5e3d61ed0b`.
- Assinatura v2/v3, ZIP e zipalign aprovados; cópia `/tmp/nexus-music2-v1.166.apk` idêntica.
- APK foi recompilado e reenviado; instalação física não testada (sem adb).


## v1.167 — título musical centralizado e respiro para subtítulo — 24/09/2026

- O título longo do visor não invade o subtítulo; 4px de respiro entre as caixas.
- Título deslocado visualmente +4px à direita, medido no viewport 412×915 e em 360px.
- Build: `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace` — `BUILD SUCCESSFUL` (42 tarefas).
- APK: `NEXUS-MUSIC-2-v1.167-titulo-central-4px.apk`; `versionCode 165`; `versionName 1.167`; tamanho 1.962.074 bytes.
- SHA-256 APK: `f8292769865466483b8cdd753b7f7d9bef22218686f5c748d87c6a74d8b43529`.
- HTML fonte/empacotado: `4e12afba9a74300531050b567e91f6c2ae6c54c356b79afc193a716375252862` (idênticos).
- Assinatura v2/v3 aprovada; ZIP íntegro; zipalign aprovado; imagem de capa default confere com fonte.
- Teste físico de instalação não realizado (sem adb).


## v1.168 — subtítulo afastado do título musical — 24/09/2026

- `.retro-subtitle` ganhou `transform:translateY(6px)` no override final; gap medido de ~10px entre caixa do título (longo) e subtítulo.
- O `bottom` absoluto antigo deixava contato/sobreposição; com translateY o espaçamento ficou estável.
- Build: `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace` — `BUILD SUCCESSFUL` (42 tarefas).
- APK: `NEXUS-MUSIC-2-v1.168-subtitulo-afastado-titulo.apk`; `versionCode 166`; `versionName 1.168`; 1.962.195 bytes.
- SHA-256 APK: `9c210d35dbed1c7d0c70f0cd4b8e790ca5f247e9a1d3a1eff246a94a7a9d9b6a` (substituir se diff).
- HTML fonte/empacotado: `2be62173ccb082e6275000c871caef30ad3d660b7e78ae81dd59d976cd380ff2`.
- Assinatura v2/v3 ok, ZIP íntegro, zipalign ok. Sem adb – teste físico pendente.

## v1.170 — barras animadas na timeline — 25/09/2026

- O fonte atual substitui as duas ondas senoidais da timeline por 22 barras coloridas animadas em canvas enquanto há reprodução; é visual decorativo, não uma análise de áudio.
- `versionCode 168`; `versionName 1.170`.
- Validador: JS válido; CSS 814/814; HTML 341.275 bytes.
- Build `clean assembleRelease lintRelease`: `BUILD SUCCESSFUL`; Lint: 0 erros, 8 avisos.
- APK: `NEXUS-MUSIC-2-v1.170-espectro-timeline.apk`; 1.962.643 bytes.
- SHA-256 APK: `524724d0a16cd78e9012cc2e7d08e882848c89ef6fba41f0fdd650eedb5dcaf5`.
- SHA-256 de `assets/index.html` no fonte, intermediário e APK: `6e9516e44f6a195650a5ee441b6af76ff573a92d01f012b7f0f18f1df9dff9db` (idênticos).
- Pacote `com.leo.nexusmusic2`, Activity `com.leo.nexusmusic2.MainActivity`; assinatura v2/v3 válida, ZIP íntegro e alinhamento aprovado.
- Prévia da fonte atual validada em HTTP local a 412×915; sem erros no console e sem overflow horizontal. Teste em aparelho físico não realizado (`adb` ausente).

## v1.171 — ondas laterais no visor sincronizadas com a capa — 25/09/2026

- Adicionado canvas leve nas laterais da capa do visor retro, preservando a capa central, os títulos e o restante da interface.
- Os filamentos laterais fazem rajadas em direção à capa do álbum; animação suspensa com a página oculta.
- Validador: JS válido; CSS 815/815. Viewport HTTP 412×915: ondas visíveis, sem overflow, capa/textos desimpedidos, sem erro no console.
- Build Release + Lint: `BUILD SUCCESSFUL`; Lint 0 erros, 8 avisos.
- APK `NEXUS-MUSIC-2-v1.171-ondas-laterais-visor.apk`; `versionCode 169`, `versionName 1.171`.
- SHA-256 do APK: `7b812e0d9c288ed3c9dc82c348a4e577338854a709918a86b9933196952f9cf5`.
- SHA-256 do asset HTML fonte/intermediário/empacotado: `50969acb6bc1e4ae15b0f06655df2c7ecc5cfba8c323d704f34b62b3e4fb0543` (idênticos).
- Pacote `com.leo.nexusmusic2`; Activity `com.leo.nexusmusic2.MainActivity`; assinatura v2/v3 válida; ZIP e alinhamento aprovados. Teste em aparelho físico pendente (`adb` ausente).

## v1.172 — release para resolver o alerta do Play Protect — 25/09/2026

- Rebuild de release assinado com a mesma chave `nexusmusic-release.jks` (CN `NEXUS MUSIC`), versão promovida.
- `versionCode 170`; `versionName 1.172`.
- Validador: JS válido; CSS 815/815; HTML 337 KB.
- Build `clean assembleRelease lintRelease`: `BUILD SUCCESSFUL`; Lint 0 erros, 8 avisos.
- APK `NEXUS-MUSIC-2-v1.172-release.apk`; 1.964.186 bytes.
- SHA-256 do APK: `72383b728049ae11d5f232e24aa779ffc52be986476ab7f9f182b1ff21d7345d`.
- SHA-256 do asset HTML fonte/intermediário/empacotado: `50969acb6bc1e4ae15b0f06655df2c7ecc5cfba8c323d704f34b62b3e4fb0543` (idênticos).
- Assinatura v2/v3 válidas; ZIP íntegro e alinhamento aprovados; teste físico pendente (`adb` ausente).

## v1.173 — novo fundo (colagem cyber-goth) — 25/09/2026

- Fundo do app trocado pela colagem em preto e branco (720×1280, 227.955 bytes); nenhuma regra CSS alterada — só o arquivo `assets/img/fundo.jpg`.
- `versionCode 171`; `versionName 1.173`.
- Validador: JS válido; CSS 815/815; HTML 337 KB. Viewport HTTP 412×915 sem overflow.
- Build `clean assembleRelease lintRelease`: `BUILD SUCCESSFUL`; Lint 0 erros, 8 avisos.
- APK `NEXUS-MUSIC-2-v1.173-fundo-collage.apk`; 2.036.567 bytes.
- SHA-256 do APK: `791e4509ff5f37a305eac1fd21f703aacd5a79a8c849bdebb2fee45f9c5b7a64`.
- SHA-256 do `assets/index.html` (fonte/intermediário/empacotado): `50969acb6bc1e4ae15b0f06655df2c7ecc5cfba8c323d704f34b62b3e4fb0543` (idênticos).
- SHA-256 do `assets/img/fundo.jpg` (fonte/empacotado): `2a366cbeb4fdf8d1b2a5364bec338ffcd5e3aef723e2377fb298a9aaec9b85fd` (idênticos).
- Assinatura v2/v3 válida; ZIP íntegro e alinhamento aprovados; teste físico pendente (`adb` ausente).

## v1.192 — revisao de codigo morto, desempenho, limpeza e icone novo — 25/09/2026

- Pedido: *"Revisar todo html codigo morto aprimorar desempenho limpeza e fazer build final com novo
  icone apk tema do background"*.
- **Codigo morto removido:** 4 funcoes JS sem nenhuma referencia (`atualizarAtaques`,
  `desenharFilamento`, `retroFormatarTempo`, `ytDownloadResetarTodos`), mais `atacarCapa`, o objeto
  `ataques` e os listeners de `load` da capa (todos das ondas laterais, removidas na v1.187).
  17 regras CSS de seletores que apareciam sozinhos (`.folder-card*`, `.hero*`, `.album-card`,
  `.artist-card`, `.nav-btn`, `.retro-time`, `.track-card`, `.yt-bar`, `.yt-fill`).
  Seletores mortos em LISTAS AGRUPADAS foram mantidos: nao reduzem o CSS final.
- `index.html`: 344.566 -> **337.289 bytes**; CSS 797 -> **780 regras**.
- **Desempenho medido:** 59,8 FPS parado, 60,2 tocando, 60,1 rolado; desenho do visor custa
  **0,01 ms/quadro** (orcamento 16,7). Nao havia problema a corrigir.
- **Nada mudou visualmente:** diff de pixels antes/depois em 5 telas -> diferenca apenas dentro do
  visor (plexus anima); resto da tela identico; zero excecoes JS.
- **Icone novo:** `scripts/gerar-icone.py` (reprodutivel). Monograma **N** cromado + malha diamante
  discreta + aro metalico. Legivel em 48px (conferido em folha 48/72/96/144/192). As duas primeiras
  tentativas (esfera da capa e espinho do fundo) foram reprovadas por serem carregadas demais.
  Backup: `_backup/icone-antes-20260925/`.
- APK `NEXUS-MUSIC-2-v1.192-final-icone-novo.apk`; `versionCode 190`; `versionName 1.192`;
  1.990.795 bytes; SHA-256 `606d4f7f2b387b8b67fc71eb3af9ece8b864e35089129ae93ed502f08c9034db`.
  Lint sem issues; assinatura v2/v3; fonte == empacotado; icone conferido pixel a pixel no APK
  (o AGP renomeia os recursos: `res/9w.png` = mdpi).

## Limpeza da pasta — 25/09/2026

- Pedido do Leo: remover APKs antigos, backups antigos, HTML antigos e o que não for do projeto atual.
- **124 MB -> 5,7 MB.** Apagados 43 APKs antigos (v1.144-v1.190, ~82 MB), 41 `_backup/index-*.html`,
  34 `_backup/build.gradle-*`, intermediarios (`app/build`, `.gradle`, ~20 MB), 18 copias historicas
  de `YtDownload`/`RangeSupport`/`AudioQuality`/`MainActivity`/`AndroidManifest`, 7 fundos antigos
  e `_backup/restauracao-v175-20260925/`.
- **Preservados:** `keystore.properties` + `nexusmusic-release.jks` (sem eles nao ha atualizacao;
  copia extra em `03_Memorias/Dicas/nexus-assinatura-backup/`), APK **v1.175** (funcional no aparelho),
  APK **v1.191** (atual), os dois `YtDownload-before-*` citados na skill, o fundo anterior ao atual,
  fontes, Gradle, `scripts/`, `docs/`, `historico.md` e `RETOMADA.md`.
- Integridade confirmada depois: validador OK (JS valido, CSS 797/797), `assembleRelease`
  `BUILD SUCCESSFUL` e os 13 assets do APK gerado com SHA-256 identico ao APK entregue.

## v1.187 a v1.191 — visor: ondas e raios removidos, PLEXUS ajustado — 25/09/2026

### v1.187 — ondas laterais removidas
- Pedido: *"Remover do visor apenas efeitos de ondas"*. As 4 chamadas de `desenharFilamento()`
  saíram do `desenhar()`; `atualizarAtaques()` deixou de ser chamado. Os raios continuaram.

### v1.188 — raios contínuos, aleatórios e com ramificações
- Pedido: *"os raios duram pouco e somem — acionar para não parar mais, randomicamente como raio
  natural, com ramificações e durações randômicas"*.
- Contínuos enquanto o áudio toca (`!audio.paused && !audio.ended`), sem evento; ao pausar, os
  traços vivos terminam e nada novo nasce.
- Aleatório: pausa sorteada entre "estragos", 1–4 descargas por estrago, vida própria por traço.
- Ramificações: 1 a 3 por raio (antes 0 ou 1), com ângulo tirado da direção local e 12–40 px.
- Medido: 28 pulsos em 12 s, maior silêncio 0,7 s, intervalos de 0,2–0,8 s; pausar -> 0, retomar -> 396.

### v1.189 — raios removidos
- Pedido: *"Remover efeitos raio"*. O bloco dos raios saiu inteiro (7.889 chars): `criarRaio`,
  `umRaio`, `atualizarRaios`, `desenharRaios`, `tocandoAgora` e a chamada no loop.

### v1.190 — PLEXUS no visor
- Pedido: *"usar o mesmo modelo no visor — eles se colidem com borda visor e capa álbum"*.
- Pontos em movimento ligados por linhas (como o protótipo), com **colisão** nas paredes do visor
  e na **capa do álbum** em vez de atravessá-la.
- Regras iniciais: 26 pontos, `PLEXUS_DIST 110`, `PLEXUS_VEL 0.22`, raio 1,1–2,5.
- **Mesmo canvas e mesmo `requestAnimationFrame`** dos efeitos anteriores — nenhum canvas/rAF novo.
  `redimensionar()` recria os pontos para o novo tamanho do visor.
- Medido em 412×915: 0 pixels dentro da capa e 0 fora do visor em 14 amostras; só 2 canvases no DOM.

### v1.191 — PLEXUS ajustado
- Pedido: *"20 pontos, menores e aumentar velocidade"*.
- `PLEXUS_PONTOS` 26 -> **20**; raio 1,1–2,5 -> **0,7–1,6**; `PLEXUS_VEL` 0,22 -> **0,42** (~90% mais rápido).

- APKs: `NEXUS-MUSIC-2-v1.187-sem-ondas-laterais.apk` (vc 185), `...-v1.188-raios-continuos.apk` (vc 186),
  `...-v1.189-sem-raios.apk` (vc 187), `...-v1.190-plexus-visor.apk` (vc 188),
  `...-v1.191-plexus-20-rapido.apk` (vc 189; 1.934.846 bytes;
  SHA-256 `52b5f2eca2a0f2769e24ae3dd3e9cc0cf75d246f100ae51c07cb07ac2d92ed3e`).
- Backups: `_backup/index-antes-remover-ondas-visor-20260925.html`,
  `index-antes-raios-continuos-20260925.html`, `index-antes-remover-raios-20260925.html`,
  `index-antes-plexus-visor-20260925.html`, `index-antes-plexus-20-20260925.html`.

## v1.186 — efeito de RAIOS no visor — 25/09/2026

- Pedido do Leo: reaproveitar o protótipo de raios e aplicar como efeito — os raios saem da
  **foto do álbum** e atingem as **paredes do visor**, disparando **no início de cada música**.
- Reaproveitado do protótipo: `generateLightningPath` (zig-zag por ponto médio), glide branco com
  `shadowBlur`, ramificação esporádica e faíscas de impacto.
- Adaptações: borda da capa `#retroCover` no lugar do retângulo interno; parede do visor projetada
  na direção do raio; **mesmo canvas e mesmo `requestAnimationFrame`** das ondas laterais (nenhum
  canvas/rAF/timer novo — só desenha durante a rajada de ~1,1 s).
- Gatilho no evento `play` do `<audio>`; guard pela URL da faixa da fila → uma rajada por música,
  pausar/retomar não repete, trocar de música dispara de novo.
- Verificação por pixels claros na faixa CENTRAL do canvas (onde só raio pode aparecer):
  baseline 10 · pico 440 · depois 0 · retomar 55 · trocar 490. Confirmado também visualmente.
- Validador: JS válido, CSS 797/797. `./gradlew clean assembleRelease lintRelease`: `BUILD SUCCESSFUL`.
- APK `NEXUS-MUSIC-2-v1.186-raios-visor.apk`; `versionCode 184`; `versionName 1.186`; 1.935.800 bytes;
  SHA-256 `a5bfa12c49bb1e83b7347dd2f104c7d01446b92693a13e1328336ec335b129a1`.
- Backup: `_backup/index-antes-raios-visor-20260925.html`.



## v1.185 — RESTAURAÇÃO do APK v1.175 funcional + fundo novo — 25/09/2026

- Leo enviou o APK funcional (`nexus-music2-v1.175.apk`, SHA-256
  `20f0f4fbafb72bea37647bd027635cf58d5b968b3b701da8dbd44963945a36d3`). Decompilado com jadx.
- **Causa raiz real:** a v1.175 **prefere M4A** e salva `.m4a`/`audio/mp4`; as v1.182–v1.184
  passaram a escolher o maior bitrate geral (Opus/WebM) e salvar `.webm`/`audio/webm`, que o
  MediaStore do aparelho **não indexa como música** — o download termina mas nada aparece.
- Restaurado a partir do APK (não por suposição): `index.html` com SHA-256
  `68511978abc88b6997b4d6781907a56712d16dfb50ff00f5e70191ad07d110b5` (idêntico ao do APK v1.175);
  `YtDownload.java` do backup da v1.175 (M4A + `descobrirTamanho`/`baixarRapido`/`baixarParte` sem
  validação de Range).
- Removidos `AudioQuality.java`, `RangeSupport.java` e os testes de contrato (não existem no APK
  funcional); pasta `app/src/test` excluída.
- **Fundo novo** (imagem do Leo: malha diamante prata + formas cromadas em espinho) redimensionado
  para 720×1280, JPEG progressivo, 125.888 bytes; uso em `body::before` mantido igual.
  Backup do anterior em `_backup/restauracao-v175-20260925/`.
- Validador: JS válido, CSS 797/797. `./gradlew clean assembleRelease lintRelease`: `BUILD SUCCESSFUL`.
- APK `NEXUS-MUSIC-2-v1.185-restaurado-v175-fundo-novo.apk`; `versionCode 183`; `versionName 1.185`;
  1.933.647 bytes; SHA-256 `27dacd54ded1ca73a1214b750bc4db0948b9ed475d092aff1035efebfc9aa792`.
- Viewport real 412×915: `body::before` carregando `/img/fundo.jpg` (200, image/jpeg), sem overflow.
- Sem `adb`: gravação no MediaStore do telefone não testada aqui.

## v1.184 — download restaurado (regressão de Range da v1.183 corrigida) — 25/09/2026

- Leo: *"O mesmo problema YouTube download não baixar nenhuma música... estava funcionando
  perfeitamente"*.
- **Causa raiz:** a v1.183 reintroduziu `RangeSupport` (sonda de Range + `DownloadPlan` + workers
  validando `Content-Range`) — a mesma abordagem já abandonada por travar o download no aparelho
  do Leo. A v1.182 que funcionava não tinha isso.
- **Correção:** `YtDownload.java` restaurado do backup da v1.182 (`_backup/YtDownload-before-v1.183-range-verification.java`);
  `RangeSupport.java` e seu teste removidos; mantida só a escolha do maior bitrate (`AudioQuality`).
- Backup do estado quebrado: `_backup/YtDownload-before-v1.184-revert.java`.
- Prova com bytes reais (3 vídeos): WEBMA_OPUS 160 escolhido; 4.011.056 / 4.155.462 / 4.094.821
  bytes, todos COMPLETOS (434–637 ms).
- Interface real 412×915: `verificando… → VERIFICANDO → BAIXANDO 3% → BAIXANDO 75% → ✓ BAIXADO →
  ✔ AC/DC - Back In Black`; reabertura marca `✓ JÁ BAIXADA`/`já está na biblioteca`.
- Reprodução: `duration` 253,861 s (igual ao `ffprobe`), `currentTime` avançou, zero erro de mídia.
- APK `NEXUS-MUSIC-2-v1.184-download-restaurado.apk`; `versionCode 182`; `versionName 1.184`;
  1.934.164 bytes. SHA-256 `38fe82f764a60e88944bbfbd546651ec1180ab746c4564adddd1fe6dcbe03aec`;
  assinatura v2/v3 válida, Lint sem issues, `RangeSupport` ausente do DEX.

## v1.183 — download do YouTube comprovado ponta a ponta — 25/09/2026

- Pedido do Leo: *"Verificar se download YouTube tá baixando corretamente"*.
- **Sem mudança de código** em relação à v1.182: a verificação confirmou o caminho atual.
- Provas com extrator e bytes reais: `WEBMA_OPUS 160 kbps` escolhido (vence M4A 128k); sonda
  `bytes=0-0` → `HTTP 206`/`Content-Range` coerente; 5 conexões baixaram 4011056/4011056 bytes
  em 0,45 s (8,4 MB/s) e o **SHA-256 das 5 conexões é idêntico** ao da conexão única (2 vídeos).
- Caminhos de exceção cobertos: origem que ignora Range (HTTP 200 → conexão única), áudio
  <1 MiB, `total=-1` desconhecido — todos com SHA-256 conferido.
- Interface real em 412×915: `verificando… → VERIFICANDO → BAIXANDO 5% → ✓ BAIXADO →
  ✔ AC/DC - Back In Black`; `ytTocarBaixada` decodificou (`duration` 253,861 s igual ao
  `ffprobe`) e o `currentTime` avançou, sem erro de mídia.
- Descoberta: `./gradlew testReleaseUnitTest` não compila (sem dependência de teste e uso de
  `com.sun.net.httpserver`); os contratos rodam standalone — 21 + 9 verificações aprovadas.
- APK `NEXUS-MUSIC-2-v1.183-download-corrigido-sem-mp3.apk`; `versionCode 181`;
  `versionName 1.183`; 1.937.602 bytes.
- SHA-256: `aebd23b6466b9009da1022e12dbc9cc3a8de702d70fe32e07c48ac8a27ca73e1`; assinatura
  v2/v3 válida, ZIP e alinhamento aprovados, Lint sem issues; asset HTML fonte == empacotado.
- Sem `adb`: gravação no MediaStore e áudio nos alto-falantes do telefone não testados.

## v1.182 — formato original e maior bitrate, sem MP3/conversão — 25/09/2026

- Confirmação do Leo: manter o formato original; sem conversão para MP3. A seleção considera o maior bitrate conhecido, sem trocar um stream melhor por preferência de container. Exemplo: Opus 160 kbps > M4A 128 kbps.
- Base do downloader é a restauração funcional da v1.175; única lógica reintroduzida: seletor pequeno `AudioQuality` para escolher o maior bitrate. Sem encoder, sem Range novo, sem retry novo.
- `AudioQualityContractTest`: 9 verificações aprovadas. `./gradlew clean assembleRelease lintRelease`: `BUILD SUCCESSFUL` (42 tarefas).
- APK `NEXUS-MUSIC-2-v1.182-formato-original-maior-bitrate.apk`; `versionCode 180`; `versionName 1.182`; 1.934.135 bytes.
- SHA-256: `14a286c33b337b39ad2c8b82b461202039f1c08f7dd8905aef0b491133847b08`; assinatura v2/v3, ZIP, zipalign e verificador aprovados; nenhuma dependência/código MP3 no APK.
- Cópia de envio `/tmp/nexus-music2-v1.182.apk` com SHA-256 idêntico. Sem `adb`: teste de download/reprodução em telefone não feito.

## v1.181 — formato original e maior bitrate, sem MP3/conversão — 25/09/2026

- Confirmação literal do Leo: *"Não fazer conversão mp3 deixar como antes apenas com qualidade mais alta possível"*.
- **Sem conversão, sem encoder, sem MP3 e sem dependência LAME.** A faixa é salva no container original disponibilizado pelo YouTube (`.m4a` ou `.webm`).
- Única mudança funcional: escolha do áudio pelo maior bitrate real (média; fallback nominal). M4A não tem preferência sobre outro formato de bitrate maior; M4A só desempata empate.
- Código-base do downloader restaurado do backup conhecido da v1.175; não foram reintroduzidas validação especial de Range, retentativas nem transcodificação.
- `AudioQualityContractTest`: 9 verificações aprovadas (Opus 160k > M4A 128k, bitrate ausente e listas vazias).
- `./gradlew clean assembleRelease lintRelease`: `BUILD SUCCESSFUL` (42 tarefas).
- APK `NEXUS-MUSIC-2-v1.181-original-maior-bitrate-sem-conversao.apk`; `versionCode 179`; `versionName 1.181`; 1.934.300 bytes.
- SHA-256: `4013eb82084a7c8a02b066199992d1acd469258a7827f12a03a37956ab26a116`. `verificar-apk.sh`, assinatura v2/v3, ZIP e zipalign aprovados. Strings do DEX confirmam `AudioQuality`/`escolherAudio`; APK não contém LAME nem Mp3Encoder.
- Cópia de envio `/tmp/nexus-music2-v1.181.apk` com SHA-256 igual ao APK publicado.
- Sem `adb`/aparelho: teste físico do download e reprodução permanece pendente.

## v1.178 — download corrigido e MP3 320 kbps — 25/09/2026

- **Correção do travamento/download parado:** entre v1.175 e v1.177 foram introduzidas três mudanças arriscadas sem teste em aparelho — validação estrita de `Range` que **reiniciava o download** quando a resposta não trazia o intervalo exato, tentativas repetidas multiplicando a espera e troca do formato salvo para `.webm`. O `YtDownload.java` foi **restaurado integralmente** do backup da v1.175 e as classes novas (`RangeSupport`, tentativas com fallback por container) foram removidas.
- **Áudio em MP3 320 kbps:** o Android não tem encoder MP3 (`MediaCodec` só decodifica `audio/mpeg`). Adicionada a dependência `com.github.naman14:TAndroidLame:1.1` (JitPack) com `.so` para arm64-v8a/armeabi-v7a/x86/x86_64; o AAR traz manifesto próprio e exigiu `tools:replace="android:label,android:allowBackup"` no manifesto do app. Fluxo: stream de maior bitrate → decodificação com `MediaExtractor`+`MediaCodec` → LAME 320 kbps com ID3 → gravação no MediaStore.
- **Rede de segurança:** se a conversão falhar (inclusive biblioteca nativa ausente no aparelho), o áudio é gravado com o container original — o download nunca se perde por causa da conversão.
- **Qualidade de origem:** escolha somente pelo MAIOR bitrate (Opus 160k costuma superar o M4A 128k do mesmo vídeo); se a melhor faixa falhar, tenta até 3 alternativas. `AudioQuality.java` concentra as decisões em funções puras.
- **Capa a pedido:** a **menor** resolução que alcance ~320px (medido: 336px), em vez de 1920×1080.
- Teste `AudioQualityContractTest`: 20 verificações. Prova contra o extrator real em 3 vídeos: `WEBMA_OPUS 160k` com `maior disponivel: 160k` e capa `336px`.
- `./gradlew clean assembleRelease lintRelease`: `BUILD SUCCESSFUL` (45 tarefas).
- APK `NEXUS-MUSIC-2-v1.178-mp3-320-e-download-corrigido.apk`; `versionCode 176`; `versionName 1.178`; 4.013.462 bytes.
- SHA-256: `5e4492831bde6e547e0a56fbebcab88c4ca686efc40bf1d1a9cc170c9118d2f3`. Assinatura v2/v3 válida; ZIP íntegro; zipalign ok; 4 `.so` empacotadas (`aapt dump badging` → `native-code` arm64-v8a, armeabi-v7a, x86, x86_64).
- Sem `adb`/aparelho: a conversão LAME não pôde ser executada em Android real. Prova obtida: decisão contra o extrator real, compilação, empacotamento das `.so`, assinatura e integridade.

## v1.177 — capa e áudio do YouTube na melhor qualidade — 25/09/2026

- Medido com o extrator real (3 vídeos): a lista de `getThumbnails()` vem da MENOR para a maior, então `get(0)` salvava **168×94**; e preferir M4A baixava **128 kbps** mesmo existindo Opus **160 kbps** no mesmo vídeo.
- Novo `MediaChoice.java` com as decisões em funções puras: capa por resolução, áudio por faixa/bitrate/formato, extensão e MIME (tabela real do `MediaFormat`: `WEBMA_OPUS` → `webm`/`audio/webm`; `M4A` → `m4a`/`audio/mp4`).
- Capa do visor/download agora usa a maior resolução (`maxresdefault` 1920×1080 quando existe); capa do card da busca usa ~300px, evitando baixar 10 imagens em 1920px por nada.
- Áudio: ordem de tentativa do melhor para o pior, com fallback até 3 alternativas se uma URL falhar. Faixa **ORIGINAL** tem prioridade sobre **DUBBED/DESCRIPTIVE** (a dublagem pode ter bitrate maior — decidir só por bitrate salvaria a versão errada). Bitrate desconhecido nunca vence um valor real.
- Efeito colateral aceito: o arquivo passa de `.m4a` para `.webm` (container real do Opus). A biblioteca lê o MediaStore por `IS_MUSIC`/duração e deriva gênero pelo caminho, sem filtrar MIME — o arquivo continua listado e tocado.
- Testes puros: `RangeSupportContractTest` 16 + `MediaChoiceContractTest` 42 = 58 verificações aprovadas. Prova real: capa 168 → 1920 px e 160k escolhido em vez de 128k nos 3 vídeos.
- `./gradlew clean assembleRelease lintRelease`: `BUILD SUCCESSFUL` (42 tarefas); Lint com os 8 avisos já conhecidos.
- APK `NEXUS-MUSIC-2-v1.177-capa-e-audio-melhor-qualidade.apk`; `versionCode 175`; `versionName 1.177`; 1.939.183 bytes.
- SHA-256 do APK: `3bcbe4ae5ef0b7fb6163ccc2e5ffb73375007eb90d88a70525922a43e6e7bbb1`. HTML fonte/empacotado idênticos: `68511978abc88b6997b4d6781907a56712d16dfb50ff00f5e70191ad07d110b5`.
- Assinatura v2/v3 válida; ZIP íntegro; zipalign aprovado; marcadores `melhorCapa`, `capaDeCard`, `tentarCandidatos`, `prioridadeFaixa` e `ordemPorQualidade` conferidos no `classes.dex`. Cópia `/tmp/nexus-music2-v1.177.apk` com hash idêntico.
- Sem `adb`/aparelho: a decisão e o empacotamento estão provados; a audição em reprodução real permanece pendente.

## v1.176 — robustez e velocidade em downloads grandes do YouTube — 25/09/2026

- Diagnóstico: o fluxo preflight → confirmação de áudio grande → download resolvia duas vezes a página/stream; além disso, `Content-Length` podia ser confundido com suporte a Range, levando a 5 requisições paralelas potencialmente completas para origem que ignorasse a faixa.
- Preflight agora guarda plano por URL por até 120 s (stream, metadados, tamanho e suporte a Range), consumido pelo worker depois da confirmação: evita a segunda resolução do mesmo vídeo.
- Sonda só habilita paralelismo em 206 com `Content-Range` confiável. Tamanho pequeno/desconhecido ou Range não suportado usa uma conexão. Cada parte valida status, intervalo e bytes; se servidor rejeitar Range, partes são apagadas e o download recomeça sequencialmente, sem juntar dados inválidos.
- Mantidas qualidade máxima, cinco conexões para arquivos grandes com Range real, progresso, cancelamento, duplicidade, MediaStore e capa.
- Teste `RangeSupportContractTest`: 16 verificações aprovadas. `bash scripts/validar-interface.sh`: JS válido, CSS 797/797.
- `./gradlew clean assembleRelease lintRelease --no-daemon --stacktrace`: `BUILD SUCCESSFUL` (42 tarefas); Lint concluiu com 8 warnings. APK `NEXUS-MUSIC-2-v1.176-download-grandes-range-validado.apk`; `versionCode 174`; `versionName 1.176`; 1.936.506 bytes.
- SHA-256 APK: `d55600d07cecc33964de5a62c94d52bb51819db52521549256cabddbdcacd1b7`. HTML fonte/empacotado idênticos: `68511978abc88b6997b4d6781907a56712d16dfb50ff00f5e70191ad07d110b5`.
- Pacote `com.leo.nexusmusic2`, Activity `com.leo.nexusmusic2.MainActivity`; Release assinado v2/v3; `verificar-apk.sh`, integridade ZIP e alinhamento aprovados. Cópia `/tmp/nexus-music2-v1.176.apk` com SHA-256 idêntico.
- Sem `adb`/aparelho: não foi possível cronometrar download real no YouTube nem validar instalação física. A melhoria comprovada aqui é eliminar resolução repetida e evitar transferências completas duplicadas quando Range não é aceito.

## v1.175 — ondas do visor mais rápidas — 25/09/2026

- Pedido do Leo: acelerar as ondas laterais do visor; incremento temporal alterado de `.012` para `.018` (+50%), sem outras mudanças no efeito/UI.
- Backup do asset anterior: `_backup/index-antes-ondas-visores-mais-rapidas-20260925-095334.html`; backup do Gradle: `_backup/build.gradle-before-v1.175`.
- Validação JS/CSS: `JS valido | CSS 797/797 | 333 KB`.
- Release + Lint: `BUILD SUCCESSFUL` (42 tarefas); relatório Lint tem warnings, nenhum impediu o build.
- APK: `NEXUS-MUSIC-2-v1.175-ondas-visor-mais-rapidas.apk`; `versionCode 173`, `versionName 1.175`; 1.934.379 bytes.
- SHA-256 do APK: `20f0f4fbafb72bea37647bd027635cf58d5b968b3b701da8dbd44963945a36d3`.
- SHA-256 do `assets/index.html` fonte e empacotado: `68511978abc88b6997b4d6781907a56712d16dfb50ff00f5e70191ad07d110b5` (idênticos).
- Pacote `com.leo.nexusmusic2`; Activity `com.leo.nexusmusic2.MainActivity`; assinatura Release v2/v3 válida, ZIP íntegro, alinhamento aprovado e `verificar-apk.sh` passou.
- Cópia de entrega `/tmp/nexus-music2-v1.175.apk` verificada com hash igual ao APK publicado. `adb` não disponível; teste de instalação física pendente.



## v1.174 — limpeza de código morto, pausa do refresh em segundo plano e novo fundo — 25/09/2026

- **Código morto removido** (verificado com diff de pixels = 0): 38 seletores CSS,
  14 regras inteiras, 24 declarações de variáveis CSS órfãs, 1 `@keyframes`
  (`nexusTitleMarquee`) e 3 IDs órfãos no CSS (`#retroTime`, `#apoioBtn`, `#ytPct`,
  sobras do visor retrô antigo). Nenhuma regra com efeito visual caro foi tocada.
- **Desempenho:** o `setInterval(refreshLibrary, 30000)` rodava indefinidamente com
  a tela apagada (WebView não aplica o throttling de timer do Chrome). Agora o timer
  é cancelado em `hidden` e recriado em `visible`. Medido: **0 requisições em 35 s
  com a tela oculta**, timer nulo; FPS na tela visível 21,3 (canvas do visor é o
  custo dominante).
- **Novo fundo:** mandala preta em relevo (720×1280, 126.617 B, progressivo, sem
  metadados), luminância **8,4%** — abaixo do ideal de 10% para o texto branco fino.
  Backup do anterior: `_backup/fundo-antes-mandala-preta-20260925-0314.jpg`.
- `index.html` 346.889 → 342.751 B. Validador: JS válido; CSS 797/797.
- `versionCode 172`; `versionName 1.174`.
- Build `clean assembleRelease lintRelease`: `BUILD SUCCESSFUL`.
- APK `NEXUS-MUSIC-2-v1.174-limpeza-e-fundo-mandala.apk`; 1,934,375 bytes.
- SHA-256 do APK: `a84c82f8175c1923c6807f29e500ff6f18cc2b18b1ee66c1b4eda135ccf82754`.
- SHA-256 do `assets/index.html` (fonte/empacotado): `aeefbd0b302984815e24f958ff70327fea279818d8cf2e5a4596164fd0138483` (idênticos).
- SHA-256 do `assets/img/fundo.jpg` (fonte/empacotado): `eaf23f4a3dc8a9424024c07fbe31c5113522f92507ad6e90b809a97d3473e4cb` (idênticos).
- Assinatura v2/v3 válida (mesma chave de release de sempre); ZIP íntegro e
  alinhamento aprovados; `verificar-apk.sh` → APK pronto para instalar.
