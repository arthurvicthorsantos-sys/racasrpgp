package com.racasrpg.race;

import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.Nullable;

import com.racasrpg.entity.BossInfo;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * As raças jogáveis. Cada raça tem estágios; cada estágio tem bônus/penalidades (atributos)
 * e uma missão que precisa ser cumprida para evoluir para o próximo estágio.
 * O último estágio de cada raça não tem missão (forma máxima).
 */
public enum Race {

    HUMANO("humano", "Humano", Items.IRON_SWORD,
            "Versátil e determinado. Não tem fraquezas, mas ganha poder mais devagar que as outras raças.",
            "Sem presas, sem chifres e sem magia no sangue, os humanos construíram reinos pela pura teimosia. Cada geração aprende com as perdas da anterior, e é isso que os torna perigosos. Os velhos mestres dizem que o herói nasce no dia em que escolhe não fugir.",
            "Corredor de longa distância: correr gasta bem menos fome.",
            List.of(
                    new Stage("Humano", new Mission(MissionType.KILL_HOSTILE, 90), List.of()),
                    new Stage("Humano Veterano", new Mission(MissionType.KILL_RACE_BOSS, 1), List.of(
                            Bonuses.maxHealth(4), Bonuses.speedPct(5))),
                    new Stage("Herói", null, List.of(
                            Bonuses.maxHealth(8), Bonuses.speedPct(8), Bonuses.attackDamage(1)))
            )),

    ELFO("elfo", "Elfo", Items.BOW,
            "Ágil, sortudo e mestre do arco. Frágil no começo, mas ganha alcance e sorte ao evoluir.",
            "Os elfos vivem em florestas antigas, onde as árvores guardam memórias de milênios. Cada flecha é cantada antes de ser lançada. Desde que os goblins começaram a queimar as clareiras, os elfos descobriram que a paciência também tem limites.",
            "Planar: segure Shift no ar para deslizar suavemente pelo ar e cair sem dano.",
            List.of(
                    new Stage("Elfo", new Mission(MissionType.KILL_RANGED, 75), List.of(
                            Bonuses.jumpPct(15), Bonuses.safeFall(2), Bonuses.speedPct(10), Bonuses.maxHealth(-4))),
                    new Stage("Elfo da Floresta", new Mission(MissionType.KILL_RACE_BOSS, 1), List.of(
                            Bonuses.jumpPct(25), Bonuses.safeFall(4), Bonuses.speedPct(15), Bonuses.maxHealth(-2), Bonuses.luck(1))),
                    new Stage("Alto Elfo", null, List.of(
                            Bonuses.jumpPct(40), Bonuses.safeFall(8), Bonuses.speedPct(20), Bonuses.luck(2), Bonuses.reach(1)))
            )),

    ANAO("anao", "Anão", Items.IRON_PICKAXE,
            "Mineiro robusto e teimoso. Minera mais rápido e tem armadura natural, mas é lento e frágil no começo.",
            "Os anões cavaram as montanhas até ouvir o coração de pedra do mundo. Para eles, cada veio de minério é um capítulo de história. Dizem que o Colosso de Pedra dorme sob as montanhas, guardião da primeira forja.",
            "Escalador de pedra: segure Shift contra paredes de pedra, minério ou deepslate para subir.",
            List.of(
                    new Stage("Anão", new Mission(MissionType.MINE_ORE, 120), List.of(
                            Bonuses.breakSpeedPct(20), Bonuses.armor(2), Bonuses.speedPct(-8), Bonuses.maxHealth(-2))),
                    new Stage("Anão de Ferro", new Mission(MissionType.KILL_RACE_BOSS, 1), List.of(
                            Bonuses.breakSpeedPct(40), Bonuses.armor(4), Bonuses.speedPct(-5))),
                    new Stage("Rei da Montanha", null, List.of(
                            Bonuses.breakSpeedPct(60), Bonuses.armor(6), Bonuses.toughness(2), Bonuses.speedPct(-5)))
            )),

    ORC("orc", "Orc", Items.IRON_AXE,
            "Guerreiro brutamontes. Mais dano e mais vida, mas é mais lento. Evolui lutando corpo a corpo.",
            "Os orcs não conhecem a palavra paz, só a palavra honra. Quem vence um combate sagrado ganha o direito de liderar o clã. O Senhor da Guerra vive em acampamentos sombrios e desafia quem diz que orcs são só força bruta.",
            "Ímpeto: depois de correr por 2 segundos, o dano dos seus golpes aumenta.",
            List.of(
                    new Stage("Orc", new Mission(MissionType.KILL_MELEE, 90), List.of(
                            Bonuses.attackDamage(1), Bonuses.maxHealth(4), Bonuses.speedPct(-5))),
                    new Stage("Orc Guerreiro", new Mission(MissionType.KILL_RACE_BOSS, 1), List.of(
                            Bonuses.attackDamage(2), Bonuses.maxHealth(6), Bonuses.speedPct(-3))),
                    new Stage("Senhor da Guerra", null, List.of(
                            Bonuses.attackDamage(3), Bonuses.maxHealth(10), Bonuses.knockbackResistPct(30)))
            )),

    GOBLIN("goblin", "Goblin", Items.GOLD_NUGGET,
            "Pequeno, veloz e sortudo. Vive de pilhagem: pouca vida, mas muita velocidade e sorte. "
                    + "Para a forma final, precisa derrotar um chefe.",
            "Pequenos e rápidos, os goblins aprenderam que quem não é forte precisa ser esperto. Roubam o que precisam e escondem o resto em covis fedorentos. Seu rei jurou que um dia os grandes pagarão pedágio.",
            "Deslize: aperte Shift enquanto corre para deslizar rápido para a frente. Anda agachado muito rápido.",
            List.of(
                    new Stage("Goblin", new Mission(MissionType.KILL_HOSTILE, 120), List.of(
                            Bonuses.sneakSpeedPct(150), Bonuses.scalePct(-15), Bonuses.speedPct(15), Bonuses.maxHealth(-6), Bonuses.luck(1))),
                    new Stage("Goblin Saqueador", new Mission(MissionType.KILL_RACE_BOSS, 1), List.of(
                            Bonuses.sneakSpeedPct(200), Bonuses.scalePct(-15), Bonuses.speedPct(18), Bonuses.maxHealth(-4), Bonuses.luck(2),
                            Bonuses.breakSpeedPct(10))),
                    new Stage("Rei Goblin", null, List.of(
                            Bonuses.sneakSpeedPct(250), Bonuses.scalePct(-15), Bonuses.speedPct(20), Bonuses.maxHealth(-2), Bonuses.luck(3),
                            Bonuses.breakSpeedPct(20), Bonuses.attackDamage(1)))
            )),

    DRACONATO("draconato", "Draconato", Items.DRAGON_HEAD,
            "Descendente de dragões: couraça de escamas e golpes poderosos, mas movimentos pesados. "
                    + "Para a forma final, precisa derrotar um chefe.",
            "Nascidos do sangue de dragões antigos, os draconatos carregam escamas e fogo no peito. Cada um busca provar que o sangue é herança, e não maldição. Os mais velhos dizem que um dragão ancião ainda dorme sob as brasas.",
            "Pouso flamejante: cair de alto não machuca e causa uma explosão de fogo ao redor.",
            List.of(
                    new Stage("Draconato", new Mission(MissionType.KILL_HOSTILE, 150), List.of(
                            Bonuses.scalePct(10), Bonuses.attackDamage(1.5), Bonuses.armor(2),
                            Bonuses.maxHealth(2), Bonuses.speedPct(-4))),
                    new Stage("Draconato Escamado", new Mission(MissionType.KILL_RACE_BOSS, 1), List.of(
                            Bonuses.scalePct(10), Bonuses.attackDamage(2), Bonuses.armor(3),
                            Bonuses.maxHealth(6), Bonuses.speedPct(-3))),
                    new Stage("Dragão Ancião", null, List.of(
                            Bonuses.scalePct(10), Bonuses.attackDamage(3), Bonuses.armor(5), Bonuses.toughness(2),
                            Bonuses.maxHealth(10), Bonuses.knockbackResistPct(20)))
            )),

    HALFLING("halfling", "Halfling", Items.WHEAT,
            "Pequeno, sortudo e ligado à terra. Ágil e de sorte alta, mas frágil. Evolui colhendo plantações.",
            "Gente pequena, de pés peludos, que prefere um bom jantar a uma boa batalha. Mas quando as plantações queimam, os halflings descobrem uma coragem teimosa. Sorte? Dizem que só colhe bem quem planta bem.",
            "Pulo de lebre: pular correndo dá um impulso extra para a frente. Sobe blocos sem pular.",
            List.of(
                    new Stage("Halfling", new Mission(MissionType.HARVEST_CROP, 180), List.of(
                            Bonuses.stepHeight(0.4), Bonuses.scalePct(-25), Bonuses.speedPct(8), Bonuses.luck(1), Bonuses.maxHealth(-4))),
                    new Stage("Halfling Andarilho", new Mission(MissionType.KILL_RACE_BOSS, 1), List.of(
                            Bonuses.stepHeight(0.4), Bonuses.scalePct(-25), Bonuses.speedPct(10), Bonuses.luck(2), Bonuses.maxHealth(-2))),
                    new Stage("Herói do Condado", null, List.of(
                            Bonuses.stepHeight(0.4), Bonuses.scalePct(-25), Bonuses.speedPct(12), Bonuses.luck(3), Bonuses.maxHealth(2)))
            )),

    MINOTAURO("minotauro", "Minotauro", Items.GOLDEN_AXE,
            "Gigante de força bruta, difícil de empurrar. Evolui no corpo a corpo e, para a forma final, "
                    + "precisa derrotar um chefe.",
            "Guardiões de labirintos que ninguém mais consegue atravessar. Respeitam a força e desprezam a covardia. Contam que o Rei do Labirinto só se revela a quem encontra o caminho sem pedir ajuda.",
            "Investida de touro: correndo por 1,5 segundo, você atropela e empurra monstros no caminho.",
            List.of(
                    new Stage("Minotauro", new Mission(MissionType.KILL_MELEE, 120), List.of(
                            Bonuses.scalePct(15), Bonuses.attackDamage(2), Bonuses.maxHealth(6),
                            Bonuses.knockbackResistPct(20), Bonuses.speedPct(-3))),
                    new Stage("Minotauro Bravo", new Mission(MissionType.KILL_RACE_BOSS, 1), List.of(
                            Bonuses.scalePct(15), Bonuses.attackDamage(3), Bonuses.maxHealth(10),
                            Bonuses.knockbackResistPct(30), Bonuses.speedPct(-2))),
                    new Stage("Rei do Labirinto", null, List.of(
                            Bonuses.scalePct(15), Bonuses.attackDamage(4), Bonuses.maxHealth(14),
                            Bonuses.knockbackResistPct(40), Bonuses.armor(2)))
            )),

    TRITAO("tritao", "Tritão", Items.TRIDENT,
            "Povo do mar. Respira por mais tempo e nada muito bem, mas é menos resistente em terra. "
                    + "Evolui abatendo monstros à distância (tridente, arco).",
            "O povo do mar vive onde as marés encontram a terra, entre recifes e tempestades. Guardam tridentes herdados e cantam para chamar a calmaria. Quando o mar se revolta, é um tritão quem responde.",
            "Jato d'água: correndo na água, você dispara como um golfinho.",
            List.of(
                    new Stage("Tritão", new Mission(MissionType.KILL_RANGED, 90), List.of(
                            Bonuses.oxygen(3), Bonuses.waterEffPct(50), Bonuses.maxHealth(-2), Bonuses.speedPct(-5))),
                    new Stage("Tritão das Marés", new Mission(MissionType.KILL_RACE_BOSS, 1), List.of(
                            Bonuses.oxygen(5), Bonuses.waterEffPct(75), Bonuses.luck(1), Bonuses.speedPct(-3))),
                    new Stage("Senhor dos Mares", null, List.of(
                            Bonuses.oxygen(8), Bonuses.waterEffPct(100), Bonuses.luck(2), Bonuses.attackDamage(1)))
            )),

    GNOMO("gnomo", "Gnomo", Items.COPPER_INGOT,
            "Pequenino, engenhoso e cheio de engenhocas. Frágil, mas rápido e de mãos habilidosas. Evolui minerando.",
            "Os gnomos vivem entre bosques de bétulas, em oficinas cheias de engrenagens e molas. Dizem que um gnomo nunca faz algo simples quando dá para fazer algo que explode. Seus inventos já salvaram aldeias inteiras, e já destruíram algumas.",
            "Pouso de mola: cair de mais de 2 blocos não causa dano e faz você quicar de volta para cima.",
            List.of(
                    new Stage("Gnomo", new Mission(MissionType.MINE_ORE, 90), List.of(
                            Bonuses.scalePct(-30), Bonuses.speedPct(10), Bonuses.breakSpeedPct(15), Bonuses.maxHealth(-6))),
                    new Stage("Gnomo Inventor", new Mission(MissionType.KILL_RACE_BOSS, 1), List.of(
                            Bonuses.scalePct(-30), Bonuses.speedPct(12), Bonuses.breakSpeedPct(30), Bonuses.maxHealth(-4))),
                    new Stage("Mestre Artífice", null, List.of(
                            Bonuses.scalePct(-30), Bonuses.speedPct(15), Bonuses.breakSpeedPct(50), Bonuses.luck(2)))
            )),

    DROW("drow", "Drow", Items.AMETHYST_SHARD,
            "Elfo das sombras. Letal no escuro e silencioso, mas desconfortável sob a luz. Para a forma final, precisa derrotar um chefe.",
            "Exilados das florestas, os drow aprenderam a enxergar onde nem a lua alcança. Não confiam em ninguém que viva sob o sol, e quase sempre têm motivo. Quem encontra um drow no escuro raramente o vê antes de ser visto.",
            "Passo sombrio: no escuro você ganha velocidade e visão noturna, e fica invisível ao se agachar.",
            List.of(
                    new Stage("Drow", new Mission(MissionType.KILL_HOSTILE, 120), List.of(
                            Bonuses.speedPct(6), Bonuses.attackDamage(1), Bonuses.maxHealth(-4))),
                    new Stage("Drow Sombrio", new Mission(MissionType.KILL_RACE_BOSS, 1), List.of(
                            Bonuses.speedPct(8), Bonuses.attackDamage(2), Bonuses.maxHealth(-2), Bonuses.luck(1))),
                    new Stage("Senhor das Sombras", null, List.of(
                            Bonuses.speedPct(10), Bonuses.attackDamage(3), Bonuses.luck(2), Bonuses.sneakSpeedPct(100)))
            )),

    CENTAURO("centauro", "Centauro", Items.SADDLE,
            "Metade homem, metade cavalo. Corredor incansável e arqueiro nato. Evolui abatendo monstros à distância.",
            "Nas planícies abertas, os centauros cavalgam ao lado do vento. Nunca param para esperar o inimigo, só para mirar. Contam que um bom centauro acerta o alvo antes de o som do galope chegar.",
            "Galope: quanto mais tempo você corre, mais rápido fica, até ficar bem veloz.",
            List.of(
                    new Stage("Centauro", new Mission(MissionType.KILL_RANGED, 90), List.of(
                            Bonuses.scalePct(10), Bonuses.speedPct(12), Bonuses.jumpPct(25), Bonuses.maxHealth(2))),
                    new Stage("Centauro Caçador", new Mission(MissionType.KILL_RACE_BOSS, 1), List.of(
                            Bonuses.scalePct(10), Bonuses.speedPct(15), Bonuses.jumpPct(35), Bonuses.maxHealth(4))),
                    new Stage("Senhor das Planícies", null, List.of(
                            Bonuses.scalePct(10), Bonuses.speedPct(20), Bonuses.jumpPct(50), Bonuses.maxHealth(8), Bonuses.luck(1)))
            )),

    REPTILIANO("reptiliano", "Reptiliano", Items.TURTLE_SCUTE,
            "Povo-lagarto de sangue frio, ágil e venenoso. Escala qualquer parede. Para a forma final, precisa derrotar um chefe.",
            "Nos pântanos quentes e nas selvas, os reptilianos caçam com paciência de pedra. Não sentem pressa nem pena. Dizem que eles lembram do tempo em que o mundo era só lama e fogo, e que sentem saudades.",
            "Aderência: ao correr contra uma parede, você sobe por ela como uma lagartixa.",
            List.of(
                    new Stage("Reptiliano", new Mission(MissionType.KILL_MELEE, 105), List.of(
                            Bonuses.speedPct(6), Bonuses.attackDamage(1), Bonuses.armor(1), Bonuses.maxHealth(-2))),
                    new Stage("Reptiliano Caçador", new Mission(MissionType.KILL_RACE_BOSS, 1), List.of(
                            Bonuses.speedPct(8), Bonuses.attackDamage(2), Bonuses.armor(2), Bonuses.maxHealth(2))),
                    new Stage("Rei dos Pântanos", null, List.of(
                            Bonuses.speedPct(10), Bonuses.attackDamage(3), Bonuses.armor(4), Bonuses.maxHealth(6)))
            )),

    ANJO("anjo", "Celestial", Items.FEATHER,
            "Descendente de seres de luz. Gravidade mais leve e cura rápida, mas poucas defesas físicas. Evolui abatendo monstros.",
            "Dizem que os celestiais nasceram do primeiro raio de sol que atravessou a escuridão. Carregam uma luz quente que os monstros odeiam. Sua maior dor é ver quem amam sofrer, e é isso que os faz lutar.",
            "Gravidade leve: você pula mais alto, cai devagar e, ao pular agachado, dá um salto enorme.",
            List.of(
                    new Stage("Celestial", new Mission(MissionType.KILL_HOSTILE, 120), List.of(
                            Bonuses.gravityPct(-30), Bonuses.jumpPct(10), Bonuses.maxHealth(2), Bonuses.armor(-1))),
                    new Stage("Celestial Radiante", new Mission(MissionType.KILL_RACE_BOSS, 1), List.of(
                            Bonuses.gravityPct(-40), Bonuses.jumpPct(15), Bonuses.maxHealth(6), Bonuses.luck(1))),
                    new Stage("Arcanjo", null, List.of(
                            Bonuses.gravityPct(-50), Bonuses.jumpPct(20), Bonuses.maxHealth(10), Bonuses.luck(2), Bonuses.attackDamage(1)))
            )),

    INFERNAL("infernal", "Infernal", Items.FIRE_CHARGE,
            "Sangue de demônio nas veias. Imune ao fogo e veloz em chamas. Para a forma final, precisa derrotar um chefe.",
            "Os infernais carregam um pacto antigo no sangue, que ninguém lembra de ter assinado. Não temem o fogo, pois ele é família. Muitos os temem; os poucos que os conhecem sabem que o pior demônio é o que se esquece de quem é.",
            "Passo de chamas: imune a fogo e lava, e ganha velocidade quando está pegando fogo.",
            List.of(
                    new Stage("Infernal", new Mission(MissionType.KILL_HOSTILE, 135), List.of(
                            Bonuses.attackDamage(1), Bonuses.speedPct(5), Bonuses.maxHealth(-2))),
                    new Stage("Infernal Ardente", new Mission(MissionType.KILL_RACE_BOSS, 1), List.of(
                            Bonuses.attackDamage(2), Bonuses.speedPct(8), Bonuses.maxHealth(2), Bonuses.armor(1))),
                    new Stage("Senhor do Abismo", null, List.of(
                            Bonuses.attackDamage(3), Bonuses.speedPct(12), Bonuses.maxHealth(6), Bonuses.armor(3), Bonuses.luck(1)))
            ));

    private final String id;
    private final String displayName;
    private final Item icon;
    private final String description;
    private final String lore;
    private final String movement;
    private final List<Stage> stages;

    Race(String id, String displayName, Item icon, String description, String lore, String movement, List<Stage> stages) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
        this.lore = lore;
        this.movement = movement;
        this.stages = stages;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public Item icon() {
        return icon;
    }

    public String description() {
        return description;
    }

    public String lore() {
        return lore;
    }

    /** Descrição da movimentação especial da raça. */
    public String movement() {
        return movement;
    }

    public List<Stage> stages() {
        return stages;
    }

    /** Devolve o estágio pelo índice (limitado ao último estágio). */
    public Stage stage(int index) {
        int i = Math.max(0, Math.min(index, stages.size() - 1));
        return stages.get(i);
    }

    public int lastStageIndex() {
        return stages.size() - 1;
    }

    @Nullable
    public static Race byId(String id) {
        if (id == null) return null;
        String wanted = id.toLowerCase(Locale.ROOT);
        for (Race r : values()) {
            if (r.id.equals(wanted)) return r;
        }
        return null;
    }

    /** Texto da missão, com o nome do chefe quando a missão é derrotar o chefe da raça. */
    public String describe(Mission mission) {
        if (mission.type() == MissionType.KILL_RACE_BOSS) {
            BossInfo boss = BossInfo.forRace(id);
            return "derrotar " + (boss != null ? boss.displayName() : "o chefe da sua raça")
                    + " (use a Bússola de busca para achar o covil)";
        }
        return mission.type().description();
    }

    /** Texto da missão com a quantidade (quando faz sentido). */
    public String describeFull(Mission mission) {
        return mission.type() == MissionType.KILL_RACE_BOSS ? describe(mission)
                : describe(mission) + " (" + mission.target() + ")";
    }

    /** Lista de ids separados por " | " (para mensagens). */
    public static String allIds() {
        StringBuilder sb = new StringBuilder();
        for (Race r : values()) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append(r.id);
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------------------------------------

    /** O que o jogador precisa fazer para evoluir. */
    public enum MissionType {
        KILL_HOSTILE("abater monstros"),
        KILL_RANGED("abater monstros à distância (arco, besta, tridente...)"),
        KILL_MELEE("abater monstros no corpo a corpo"),
        KILL_BOSS("derrotar qualquer chefe"),
        KILL_RACE_BOSS("derrotar o chefe da sua raça"),
        HARVEST_CROP("colher plantações maduras"),
        MINE_ORE("minerar minérios");

        private final String description;

        MissionType(String description) {
            this.description = description;
        }

        public String description() {
            return description;
        }
    }

    public record Mission(MissionType type, int target) {
        public String text() {
            return type.description() + " (" + target + ")";
        }
    }

    /** Um bônus/penalidade de atributo. {@code key} identifica o modificador (um por atributo). */
    public record Bonus(String key, Holder<Attribute> attribute, double amount,
                        AttributeModifier.Operation operation, String text) {
    }

    /** Um estágio da raça. {@code mission} é null no último estágio. */
    public record Stage(String name, @Nullable Mission mission, List<Bonus> bonuses) {
    }

    /** Fábrica de bônus, só para deixar as definições acima legíveis. */
    public static final class Bonuses {
        private Bonuses() {
        }

        private static String sign(double v) {
            return v >= 0 ? "+" : "";
        }

        private static String num(double v) {
            return v == Math.floor(v) ? String.valueOf((long) v) : String.valueOf(v);
        }

        public static Bonus maxHealth(double v) {
            return new Bonus("max_health", Attributes.MAX_HEALTH, v,
                    AttributeModifier.Operation.ADD_VALUE, sign(v) + num(v) + " de vida máxima");
        }

        public static Bonus speedPct(int pct) {
            return new Bonus("speed", Attributes.MOVEMENT_SPEED, pct / 100.0,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE, sign(pct) + pct + "% de velocidade");
        }

        public static Bonus attackDamage(double v) {
            return new Bonus("attack_damage", Attributes.ATTACK_DAMAGE, v,
                    AttributeModifier.Operation.ADD_VALUE, sign(v) + num(v) + " de dano de ataque");
        }

        public static Bonus armor(double v) {
            return new Bonus("armor", Attributes.ARMOR, v,
                    AttributeModifier.Operation.ADD_VALUE, sign(v) + num(v) + " de armadura");
        }

        public static Bonus toughness(double v) {
            return new Bonus("toughness", Attributes.ARMOR_TOUGHNESS, v,
                    AttributeModifier.Operation.ADD_VALUE, sign(v) + num(v) + " de resistência da armadura");
        }

        public static Bonus luck(double v) {
            return new Bonus("luck", Attributes.LUCK, v,
                    AttributeModifier.Operation.ADD_VALUE, sign(v) + num(v) + " de sorte");
        }

        public static Bonus reach(double v) {
            return new Bonus("reach", Attributes.ENTITY_INTERACTION_RANGE, v,
                    AttributeModifier.Operation.ADD_VALUE, sign(v) + num(v) + " de alcance de ataque");
        }

        public static Bonus breakSpeedPct(int pct) {
            return new Bonus("break_speed", Attributes.BLOCK_BREAK_SPEED, pct / 100.0,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE, sign(pct) + pct + "% de velocidade de mineração");
        }

        public static Bonus knockbackResistPct(int pct) {
            return new Bonus("knockback_resist", Attributes.KNOCKBACK_RESISTANCE, pct / 100.0,
                    AttributeModifier.Operation.ADD_VALUE, sign(pct) + pct + "% de resistência a empurrão");
        }

        public static Bonus scalePct(int pct) {
            return new Bonus("scale", Attributes.SCALE, pct / 100.0,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE, sign(pct) + pct + "% de tamanho");
        }

        public static Bonus jumpPct(int pct) {
            return new Bonus("jump", Attributes.JUMP_STRENGTH, pct / 100.0,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE, sign(pct) + pct + "% de altura do pulo");
        }

        public static Bonus safeFall(double v) {
            return new Bonus("safe_fall", Attributes.SAFE_FALL_DISTANCE, v,
                    AttributeModifier.Operation.ADD_VALUE, sign(v) + num(v) + " blocos de queda segura");
        }

        public static Bonus stepHeight(double v) {
            return new Bonus("step_height", Attributes.STEP_HEIGHT, v,
                    AttributeModifier.Operation.ADD_VALUE, "Sobe 1 bloco sem pular");
        }

        public static Bonus sneakSpeedPct(int pct) {
            return new Bonus("sneak", Attributes.SNEAKING_SPEED, pct / 100.0,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE, sign(pct) + pct + "% de velocidade agachado");
        }

        public static Bonus gravityPct(int pct) {
            return new Bonus("gravity", Attributes.GRAVITY, pct / 100.0,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE, sign(pct) + pct + "% de gravidade (mais leve)");
        }

        public static Bonus oxygen(double v) {
            return new Bonus("oxygen", Attributes.OXYGEN_BONUS, v,
                    AttributeModifier.Operation.ADD_VALUE, sign(v) + num(v) + " de fôlego debaixo d'água");
        }

        public static Bonus waterEffPct(int pct) {
            return new Bonus("water_eff", Attributes.WATER_MOVEMENT_EFFICIENCY, pct / 100.0,
                    AttributeModifier.Operation.ADD_VALUE, sign(pct) + pct + "% de eficiência ao nadar");
        }
    }
}
