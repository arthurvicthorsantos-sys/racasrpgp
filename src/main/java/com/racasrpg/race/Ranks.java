package com.racasrpg.race;

import java.util.EnumMap;
import java.util.Map;

/** Hierarquia (patentes) de cada raça. A patente sobe com a honra ganha em combate, missões e chefes. */
public final class Ranks {
    private Ranks() {
    }

    /** Honra necessária para cada patente (0 a 4). */
    public static final int[] THRESHOLDS = {0, 150, 600, 1800, 5000};

    private static final Map<Race, String[]> TITLES = new EnumMap<>(Race.class);

    static {
        TITLES.put(Race.HUMANO, new String[]{"Camponês Jurado", "Soldado", "Capitão", "Comandante", "Rei Herói"});
        TITLES.put(Race.ELFO, new String[]{"Aprendiz da Folha", "Guardião", "Sentinela", "Mestre das Flechas", "Alto Rei Élfico"});
        TITLES.put(Race.ANAO, new String[]{"Aprendiz de Forja", "Mineiro", "Mestre Ferreiro", "Capitão da Montanha", "Rei Anão"});
        TITLES.put(Race.ORC, new String[]{"Filhote de Clã", "Guerreiro", "Capitão de Bando", "Chefe de Guerra", "Grão-Senhor"});
        TITLES.put(Race.GOBLIN, new String[]{"Ratazana", "Batedor", "Saqueador-Mor", "Chefe de Bando", "Grão-Rei Goblin"});
        TITLES.put(Race.DRACONATO, new String[]{"Ovo", "Escamoso", "Guardião das Brasas", "Lorde Dracônico", "Dragão-Rei"});
        TITLES.put(Race.HALFLING, new String[]{"Jovem do Condado", "Colhedor", "Prefeito", "Mestre da Colheita", "Thane do Condado"});
        TITLES.put(Race.MINOTAURO, new String[]{"Novato do Labirinto", "Guardião", "Caçador de Masmorras", "Senhor do Labirinto", "Rei-Touro"});
        TITLES.put(Race.TRITAO, new String[]{"Marujo", "Pescador de Pérolas", "Guarda das Marés", "Capitão do Recife", "Rei das Marés"});
        TITLES.put(Race.GNOMO, new String[]{"Aprendiz de Engenhoca", "Mecânico", "Inventor", "Mestre Artífice", "Grande Engenheiro"});
        TITLES.put(Race.DROW, new String[]{"Neófito", "Lâmina das Sombras", "Mestre de Adaga", "Senhor da Casa", "Rei das Sombras"});
        TITLES.put(Race.CENTAURO, new String[]{"Potro", "Galopador", "Batedor", "Chefe de Manada", "Rei das Planícies"});
        TITLES.put(Race.REPTILIANO, new String[]{"Ovo do Pântano", "Caçador", "Garra Antiga", "Sacerdote-Lagarto", "Rei Escamoso"});
        TITLES.put(Race.ANJO, new String[]{"Luz Nascente", "Vigilante", "Serafim", "Guardião da Aurora", "Arcanjo"});
        TITLES.put(Race.INFERNAL, new String[]{"Brasa", "Condenado", "Arauto do Abismo", "Duque Infernal", "Senhor dos Demônios"});
    }

    /** Índice da patente (0 a 4) para uma quantidade de honra. */
    public static int rankIndex(int honor) {
        int rank = 0;
        for (int i = 0; i < THRESHOLDS.length; i++) {
            if (honor >= THRESHOLDS[i]) {
                rank = i;
            }
        }
        return rank;
    }

    public static String title(Race race, int rank) {
        String[] titles = TITLES.get(race);
        return titles[Math.max(0, Math.min(rank, titles.length - 1))];
    }

    public static String[] titles(Race race) {
        return TITLES.get(race);
    }

    /** Honra da próxima patente, ou -1 se já está na maior. */
    public static int nextThreshold(int rank) {
        return rank >= THRESHOLDS.length - 1 ? -1 : THRESHOLDS[rank + 1];
    }
}
