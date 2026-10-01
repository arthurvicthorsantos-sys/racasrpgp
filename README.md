# Racas RPG (NeoForge 1.21.1) - versao 0.1.0

Mod de racas com evolucao por missoes.

## Como jogar
- `/raca`           abre o menu (escolher raca ou ver status e evoluir)
- `/raca status`    status em texto no chat
- `/raca info`      lista todas as racas em texto
- `/raca escolher <raca>` e `/raca evoluir`  (o menu ja faz isso com botoes)
- `/raca resetar`   (so OP/cheats) remove a raca, util para testar
O menu abre sozinho ao entrar no mundo se voce ainda nao tem raca.

Missoes por raca: Humano = abater monstros; Elfo = abater monstros a distancia (arco/besta);
Anao = minerar minerios; Orc = abater monstros no corpo a corpo.
Cada raca tem 3 estagios com bonus/penalidades de atributos diferentes.

## Como compilar (voce precisa fazer isso uma vez)
1. Instale o **JDK 21** (por ex. Temurin 21).
2. Abra um terminal nesta pasta e rode:
   - Windows: `gradlew.bat build`
   - Linux/Mac: `./gradlew build`
   (a primeira vez baixa varias coisas e demora alguns minutos; precisa de internet)
3. O mod fica em `build/libs/racasrpg-0.1.0.jar`. Coloque na pasta `mods` do Minecraft 1.21.1 com NeoForge.

## Testar direto do projeto (sem instalar)
`gradlew runClient` abre o jogo ja com o mod.

Se der erro ao compilar ou ao abrir o jogo, mande o texto completo do erro.


## Versao 0.3 - conteudo novo
- 9 racas (Humano, Elfo, Anao, Orc, Goblin, Draconato, Halfling, Minotauro, Tritao).
- Mobs agressivos: Saqueador Goblin, Brutamontes Orc, Arqueiro Sombrio (aparecem no overworld).
- Chefes: Rei Goblin (covil) e Campeao Sombrio (ruina sombria), com barra de vida. Derrota-los conta para evolucoes.
- NPC Mestre da Raca (nas vilas): escolhe raca e entrega missoes com recompensa.
- 9 vilas raciais + 2 covis de chefes geradas no mundo (so em mundos/chunks novos).
Testes: `/locate structure racasrpg:vila_anao`, `/summon racasrpg:goblin_king`,
`/summon racasrpg:race_master ~ ~ ~ {Raca:"anao"}`
