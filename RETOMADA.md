# NEXUS MUSIC 2 — Retomada

> Documento de continuação. **Leia isto primeiro ao retomar o trabalho.**
> **Build corrente:** v1.192 (`versionCode 190`) — limpeza final + **ícone novo** + PLEXUS no visor.
> **APK:** `NEXUS-MUSIC-2-v1.192-final-icone-novo.apk`
> **Fonte:** `01_Projeto/APK/NexusMusic2/app/src/main/assets/index.html` + `app/src/main/java/com/leo/nexusmusic2/`

---

## 🧹 ESTADO DO CÓDIGO (após a limpeza da v1.192)

- `index.html` **337.289 bytes**; CSS **780 regras**; JS válido; zero exceções no console.
- **Funções mortas removidas:** `atualizarAtaques`, `desenharFilamento`, `retroFormatarTempo`,
  `ytDownloadResetarTodos`, `atacarCapa` + objeto `ataques` + listeners de `load` da capa.
  Eram sobras das ondas laterais.
- **CSS:** 17 regras de seletores mortos removidas. Ainda restam seletores mortos dentro de
  **listas agrupadas** (ex.: `..., .nav-btn:active, ...`) — **não remover**: não reduzem o CSS final
  e o risco de quebrar uma regra viva não compensa.
- **Desempenho: 60 FPS** em todos os cenários (parado / tocando / rolado). O desenho do visor custa
  **0,01 ms por quadro**. Não há gargalo conhecido.

### Ícone
Feito por `scripts/gerar-icone.py` (reprodutível): monograma **N** cromado + malha diamante do fundo
+ aro metálico. **Legível em 48 px.** Para trocar: editar o script e rodar — ele regenera os 5 mipmaps.
⚠️ Não usar a capa padrão nem os espinhos do fundo como ícone: são carregados demais e somem em
tamanho pequeno (tentado e reprovado na v1.192).

---

## 🎛️ VISOR — estado atual do efeito

Só existe **um** efeito no visor: o **PLEXUS** (pontos em movimento ligados por linhas).

- Roda no **mesmo canvas** `#visorOndasCanvas` e no **mesmo `requestAnimationFrame`** que os efeitos
  anteriores usavam — **nunca** criar canvas/rAF/timer novo (canvas extra + `backdrop-filter` derruba FPS).
- Parâmetros atuais (bloco `PLEXUS_*` no JS): **20 pontos**, `PLEXUS_DIST 110`, `PLEXUS_VEL 0.42`,
  raio **0,7–1,6**.
- **Colisão** nas paredes do visor **e** na capa do álbum (`colidirComCapa`): a saída é pelo lado mais
  próximo e a velocidade inverte naquele eixo. Sem capa visível, nenhuma colisão.
- `redimensionar()` recria os pontos para o tamanho novo do visor.

### Efeitos que existiram e foram REMOVIDOS (não reintroduzir sem pedido)
| Efeito | Situação |
|---|---|
| Ondas laterais (filamentos nas bordas) | removido na v1.187 |
| Raios da capa às paredes | removido na v1.189 (existiu nas v1.186–v1.188) |

As funções antigas podem ter ficado inertes no arquivo; **não** são chamadas no `desenhar()`.

---

## 🔴 REGRAS INVIOLÁVEIS DO DOWNLOAD DO YOUTUBE

O download **não baixava nenhuma música** nas v1.182–v1.184. A causa foi confirmada
decompilando o APK v1.175 funcional com jadx:

| | v1.175 (funciona) | v1.182–v1.184 (quebrado) |
|---|---|---|
| Áudio escolhido | **prefere M4A** | maior bitrate geral → Opus/WebM |
| Arquivo salvo | `.m4a` / `audio/mp4` | `.webm` / `audio/webm` |

**O MediaStore/MediaScanner do aparelho NÃO indexa `.webm`/Opus como faixa de música.** O
download termina mas nada aparece na biblioteca. **Sempre salvar M4A.**

**Nunca reintroduzir validação de Range** (`RangeSupport`, `sondarRange`, `DownloadPlan`,
checagem de `Content-Range` nos workers). Isso já derrubou o download **três vezes**.

Código-base correto do downloader: `_backup/YtDownload-before-range-guard-20260925-102255.java`.
O APK funcional **não** tem `AudioQuality` nem `RangeSupport` no DEX.

### Se o Leo enviar um APK funcional, EXTRAIR dele — nunca adivinhar
```bash
unzip -Z1 apk | grep assets/          # listar assets
unzip -o apk 'assets/*' -d extraido   # extrair (index.html, imagens, fontes)
/tmp/jadx/bin/jadx -d src --no-res apk # decompilar o Java
```
Comparar SHA-256 asset a asset e conferir quais classes existem no DEX. Foi assim que a causa
do M4A vs WebM apareceu, depois de várias tentativas no escuro falharem.

---

## 🎨 Estado visual (não mexer sem pedido)

### Containers
- **`.player-top` é o container pai**. Visual: `rgba(0,0,0,.02)` + `blur(8px)`, raio 24px, sem borda.
- **ÁLBUNS/FAVORITOS:** `#tracksSection` é um **segundo box colado** ao pai (classe `view-listagem`
  no `#content`), mesmo fundo/blur, cantos de cima retos e 24px embaixo.
  ⚠️ As abordagens com `::after`/"superfície única" foram **abandonadas** — havia regra antiga
  `.player-top::before/::after{content:none}` e `overflow:hidden` cortando. Leo pediu a solução simples.
- **YouTube (`.yt-panel`)**: mesmo visual do pai.

### Cards
- **170×170**, grade `repeat(2,170px)`, `gap:10px`, centralizada.
- Fundo `rgba(7,7,7,.34)`; aro metálico 1px (pseudo-elemento mascarado); raio 16px.
- **Estrela de favoritos:** rodapé direito, 30px, aro de vidro; marcada = `#ffd23f` com halo. Classe `.fav-btn` (+ `.on`).

### Botões de navegação e BAIXAR
- Preenchimento igual ao dos controles de música: véu de vidro + `rgba(16,16,16,.90)`.
- **Aro metálico 2px** só na borda (`padding-box,padding-box,border-box`); raio 8px; fonte Rajdhani **11px**, sem emoji.
- **BAIXAR:** largura fixa 118px; rótulos `BAIXAR` → `BAIXANDO 45%` → `CONVERTENDO`; barra branca dentro.

### Timeline e controles
- Barra **220×15px** no viewport 412px; conjunto com os relógios = 312px. Encolhe em viewports estreitos.
- ◀ ⏯ ▶ **mantidos originais** (redondos, borda 1px). O aro metálico neles foi **reprovado e revertido**.

### Fundo
- `app/src/main/assets/img/fundo.jpg` — **720×1280**, JPEG progressivo, 125.888 bytes; malha diamante
  prata + formas cromadas em espinho.
- Uso único: `body::before` → `url('/img/fundo.jpg')`, `cover`, `center top`, `z-index:-1`.
- Para trocar: substituir só o arquivo (720×1280), com backup antes. **O CSS não muda.**

---

## 🔊 Áudio ao fechar o app (não regredir)

- `MainActivity.onDestroy()` e o fechamento por duplo toque em voltar chamam **`nx_pararTodoAudio()`** —
  pausa o player principal **e** as prévias do YouTube (`ytPararPrevia()`), que são `Audio()` separados.
- **Nunca** pausar áudio em `visibilitychange`/`onStop`: minimizar **deve** manter a música tocando.

---

## 🛠️ Como retomar (comandos)

### Validar JS/CSS antes de qualquer build
```bash
cd "/home/leo/Documents/Obsidian Vault/01_Projeto/APK/NexusMusic2"
bash scripts/validar-interface.sh
```
> ⚠️ `./gradlew testReleaseUnitTest` **não funciona** neste projeto (sem dependência de teste e
> `com.sun.net.httpserver` ausente no classpath Android). Não existe mais pasta `app/src/test`.

### Testar o asset no navegador (viewport real 412×915)
```bash
python3 -m http.server 8765 --bind 127.0.0.1 \
  --directory ".../APK/NexusMusic2/app/src/main/assets"
chromium --headless --disable-gpu --no-sandbox --remote-debugging-port=9223 \
  --user-data-dir=/tmp/nexus-cdp-profile about:blank
# Emulation.setDeviceMetricsOverride {412,915,mobile:true} + Page.captureScreenshot
# DESABILITAR CACHE (Network.setCacheDisabled) — cache já mascarou CSS
```
Para sem autoplay bloqueado: `--autoplay-policy=no-user-gesture-required`.
Sonda pronta: `~/.hermes/skills/web/nexus-music/scripts/cdp-viewport-probe.js`.

### Build
```bash
cd "/home/leo/Documents/Obsidian Vault/01_Projeto/APK/NexusMusic2"
# subir versionCode/versionName no app/build.gradle PRIMEIRO
./gradlew clean assembleRelease lintRelease --no-daemon
bash scripts/verificar-apk.sh app/build/outputs/apk/release/app-release.apk
```
Build leva ~1 min. **Verificar depois que `assets/index.html` empacotado tem o mesmo hash da fonte.**

### Enviar o APK
Copiar para `/tmp` com nome **sem espaços** e enviar por `MEDIA:`. Resumo curto: só as mudanças.

---

## 📚 Onde está documentado

- **Histórico completo:** `01_Projeto/APK/NexusMusic2/historico.md`
- **Memórias:** `03_Memórias/Soluções/nexusmusic2-*.md` (a da restauração: `nexusmusic2-v1.185-restaurado-do-apk-v175-20260925.md`)
- **Skill:** `nexus-music` → `references/youtube-download-stability-quality.md` (regras do download)

## 🔖 Backups
`01_Projeto/APK/NexusMusic2/_backup/` — sempre criar um backup novo antes de editar o `index.html`,
o `YtDownload.java`/`MainActivity.java` **ou substituir uma imagem**.

## ⚠️ Pendências conhecidas
1. **UI de upload morta** (`.content > .toolbar{display:none !important}` esconde `#uploadInput`/`#search`).
   Código inerte, sem custo. Não mexer sem o pedido.
2. **`server.py` web da pasta `HTML/Nexus_Music/` está defasado** (porta 7788, index antigo).
   Para testar o app use o servidor de assets acima, não o dele.
3. **Sem `adb` nesta máquina:** gravação no MediaStore e áudio nos alto-falantes do telefone nunca
   foram testados aqui — só o caminho de rede e a decodificação no WebView.
