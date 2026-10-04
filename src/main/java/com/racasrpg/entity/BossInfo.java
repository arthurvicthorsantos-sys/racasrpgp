package com.racasrpg.entity;

import org.jetbrains.annotations.Nullable;

/** Os 15 chefes: um para cada raça (o "nêmese" da raça), com o covil onde ele vive. */
public enum BossInfo {
    GOBLIN_KING("goblin_king", "Rei Goblin", "goblin", "covil_goblin"),
    SHADOW_CHAMPION("shadow_champion", "Campeão Sombrio", "drow", "ruina_sombria"),
    STONE_COLOSSUS("stone_colossus", "Colosso de Pedra", "anao", "torre_colosso"),
    ORC_WARLORD("orc_warlord", "Senhor da Guerra Orc", "orc", "acampamento_orc"),
    SPIDER_QUEEN("spider_queen", "Rainha Aranha", "elfo", "ninho_aranha"),
    FLAME_WYRM("flame_wyrm", "Dragão Flamejante", "draconato", "covil_dragao"),
    ROT_LICH("rot_lich", "Rei Lich", "humano", "cripta_lich"),
    HARVEST_SCARECROW("harvest_scarecrow", "Espantalho da Colheita", "halfling", "campo_maldito"),
    LABYRINTH_GUARDIAN("labyrinth_guardian", "Guardião do Labirinto", "minotauro", "labirinto"),
    DROWNED_KING("drowned_king", "Rei Afogado", "tritao", "templo_afogado"),
    GNOME_AUTOMATON("gnome_automaton", "Autômato Gnômico", "gnomo", "oficina_gnomica"),
    ARCHER_COLOSSUS("archer_colossus", "Colosso Arqueiro", "centauro", "arena_centauro"),
    BASILISK("basilisk", "Basilisco", "reptiliano", "toca_basilisco"),
    FALLEN_ANGEL("fallen_angel", "Anjo Caído", "anjo", "templo_caido"),
    PACT_MASTER("pact_master", "Mestre do Pacto", "infernal", "altar_infernal");

    private final String id;
    private final String displayName;
    private final String raceId;
    private final String lairId;

    BossInfo(String id, String displayName, String raceId, String lairId) {
        this.id = id;
        this.displayName = displayName;
        this.raceId = raceId;
        this.lairId = lairId;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public String raceId() {
        return raceId;
    }

    public String lairId() {
        return lairId;
    }

    @Nullable
    public static BossInfo forRace(String raceId) {
        for (BossInfo b : values()) {
            if (b.raceId.equals(raceId)) return b;
        }
        return null;
    }
}
