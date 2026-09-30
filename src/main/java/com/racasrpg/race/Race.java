package com.racasrpg.race;

import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.Nullable;

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
            List.of(
                    new Stage("Humano", new Mission(MissionType.KILL_HOSTILE, 30), List.of()),
                    new Stage("Humano Veterano", new Mission(MissionType.KILL_HOSTILE, 120), List.of(
                            Bonuses.maxHealth(4), Bonuses.speedPct(5))),
                    new Stage("Herói", null, List.of(
                            Bonuses.maxHealth(8), Bonuses.speedPct(8), Bonuses.attackDamage(1)))
            )),

    ELFO("elfo", "Elfo", Items.BOW,
            "Ágil, sortudo e mestre do arco. Frágil no começo, mas ganha alcance e sorte ao evoluir.",
            List.of(
                    new Stage("Elfo", new Mission(MissionType.KILL_RANGED, 25), List.of(
                            Bonuses.speedPct(10), Bonuses.maxHealth(-4))),
                    new Stage("Elfo da Floresta", new Mission(MissionType.KILL_RANGED, 100), List.of(
                            Bonuses.speedPct(15), Bonuses.maxHealth(-2), Bonuses.luck(1))),
                    new Stage("Alto Elfo", null, List.of(
                            Bonuses.speedPct(20), Bonuses.luck(2), Bonuses.reach(1)))
            )),

    ANAO("anao", "Anão", Items.IRON_PICKAXE,
            "Mineiro robusto e teimoso. Minera mais rápido e tem armadura natural, mas é lento e frágil no começo.",
            List.of(
                    new Stage("Anão", new Mission(MissionType.MINE_ORE, 40), List.of(
                            Bonuses.breakSpeedPct(20), Bonuses.armor(2), Bonuses.speedPct(-8), Bonuses.maxHealth(-2))),
                    new Stage("Anão de Ferro", new Mission(MissionType.MINE_ORE, 150), List.of(
                            Bonuses.breakSpeedPct(40), Bonuses.armor(4), Bonuses.speedPct(-5))),
                    new Stage("Rei da Montanha", null, List.of(
                            Bonuses.breakSpeedPct(60), Bonuses.armor(6), Bonuses.toughness(2), Bonuses.speedPct(-5)))
            )),

    ORC("orc", "Orc", Items.IRON_AXE,
            "Guerreiro brutamontes. Mais dano e mais vida, mas é mais lento. Evolui lutando corpo a corpo.",
            List.of(
                    new Stage("Orc", new Mission(MissionType.KILL_MELEE, 30), List.of(
                            Bonuses.attackDamage(1), Bonuses.maxHealth(4), Bonuses.speedPct(-5))),
                    new Stage("Orc Guerreiro", new Mission(MissionType.KILL_MELEE, 120), List.of(
                            Bonuses.attackDamage(2), Bonuses.maxHealth(6), Bonuses.speedPct(-3))),
                    new Stage("Senhor da Guerra", null, List.of(
                            Bonuses.attackDamage(3), Bonuses.maxHealth(10), Bonuses.knockbackResistPct(30)))
            )),

    GOBLIN("goblin", "Goblin", Items.GOLD_NUGGET,
            "Pequeno, veloz e sortudo. Vive de pilhagem: tem pouca vida, mas muita velocidade e sorte.",
            List.of(
                    new Stage("Goblin", new Mission(MissionType.KILL_HOSTILE, 40), List.of(
                            Bonuses.speedPct(15), Bonuses.maxHealth(-6), Bonuses.luck(1))),
                    new Stage("Goblin Saqueador", new Mission(MissionType.KILL_HOSTILE, 140), List.of(
                            Bonuses.speedPct(18), Bonuses.maxHealth(-4), Bonuses.luck(2), Bonuses.breakSpeedPct(10))),
                    new Stage("Rei Goblin", null, List.of(
                            Bonuses.speedPct(20), Bonuses.maxHealth(-2), Bonuses.luck(3),
                            Bonuses.breakSpeedPct(20), Bonuses.attackDamage(1)))
            )),

    DRACONATO("draconato", "Draconato", Items.DRAGON_HEAD,
            "Descendente de dragões: couraça de escamas e golpes poderosos, mas movimentos pesados.",
            List.of(
                    new Stage("Draconato", new Mission(MissionType.KILL_HOSTILE, 50), List.of(
                            Bonuses.attackDamage(1.5), Bonuses.armor(2), Bonuses.maxHealth(2), Bonuses.speedPct(-4))),
                    new Stage("Draconato Escamado", new Mission(MissionType.KILL_HOSTILE, 160), List.of(
                            Bonuses.attackDamage(2), Bonuses.armor(3), Bonuses.maxHealth(6), Bonuses.speedPct(-3))),
                    new Stage("Dragão Ancião", null, List.of(
                            Bonuses.attackDamage(3), Bonuses.armor(5), Bonuses.toughness(2),
                            Bonuses.maxHealth(10), Bonuses.knockbackResistPct(20)))
            )),

    LICANTROPO("licantropo", "Licantropo", Items.BONE,
            "Caçador selvagem. Rápido e feroz, evolui caçando animais, mas é menos resistente.",
            List.of(
                    new Stage("Licantropo", new Mission(MissionType.KILL_ANIMAL, 40), List.of(
                            Bonuses.speedPct(12), Bonuses.attackDamage(1), Bonuses.maxHealth(-2))),
                    new Stage("Lobo Caçador", new Mission(MissionType.KILL_ANIMAL, 120), List.of(
                            Bonuses.speedPct(15), Bonuses.attackDamage(2), Bonuses.maxHealth(2))),
                    new Stage("Lobo Alfa", null, List.of(
                            Bonuses.speedPct(18), Bonuses.attackDamage(3), Bonuses.maxHealth(6),
                            Bonuses.knockbackResistPct(10)))
            )),

    VAMPIRO("vampiro", "Vampiro", Items.REDSTONE,
            "Predador noturno, rápido e letal, mas de corpo frágil. Evolui abatendo monstros.",
            List.of(
                    new Stage("Vampiro", new Mission(MissionType.KILL_HOSTILE, 40), List.of(
                            Bonuses.attackDamage(1), Bonuses.speedPct(8), Bonuses.maxHealth(-6))),
                    new Stage("Vampiro Antigo", new Mission(MissionType.KILL_HOSTILE, 150), List.of(
                            Bonuses.attackDamage(2), Bonuses.speedPct(10), Bonuses.maxHealth(-2))),
                    new Stage("Senhor da Noite", null, List.of(
                            Bonuses.attackDamage(3), Bonuses.speedPct(12), Bonuses.maxHealth(4), Bonuses.luck(1)))
            ));

    private final String id;
    private final String displayName;
    private final Item icon;
    private final String description;
    private final List<Stage> stages;

    Race(String id, String displayName, Item icon, String description, List<Stage> stages) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
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
        KILL_ANIMAL("caçar animais"),
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
    static final class Bonuses {
        private Bonuses() {
        }

        private static String sign(double v) {
            return v >= 0 ? "+" : "";
        }

        private static String num(double v) {
            return v == Math.floor(v) ? String.valueOf((long) v) : String.valueOf(v);
        }

        static Bonus maxHealth(double v) {
            return new Bonus("max_health", Attributes.MAX_HEALTH, v,
                    AttributeModifier.Operation.ADD_VALUE, sign(v) + num(v) + " de vida máxima");
        }

        static Bonus speedPct(int pct) {
            return new Bonus("speed", Attributes.MOVEMENT_SPEED, pct / 100.0,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE, sign(pct) + pct + "% de velocidade");
        }

        static Bonus attackDamage(double v) {
            return new Bonus("attack_damage", Attributes.ATTACK_DAMAGE, v,
                    AttributeModifier.Operation.ADD_VALUE, sign(v) + num(v) + " de dano de ataque");
        }

        static Bonus armor(double v) {
            return new Bonus("armor", Attributes.ARMOR, v,
                    AttributeModifier.Operation.ADD_VALUE, sign(v) + num(v) + " de armadura");
        }

        static Bonus toughness(double v) {
            return new Bonus("toughness", Attributes.ARMOR_TOUGHNESS, v,
                    AttributeModifier.Operation.ADD_VALUE, sign(v) + num(v) + " de resistência da armadura");
        }

        static Bonus luck(double v) {
            return new Bonus("luck", Attributes.LUCK, v,
                    AttributeModifier.Operation.ADD_VALUE, sign(v) + num(v) + " de sorte");
        }

        static Bonus reach(double v) {
            return new Bonus("reach", Attributes.ENTITY_INTERACTION_RANGE, v,
                    AttributeModifier.Operation.ADD_VALUE, sign(v) + num(v) + " de alcance de ataque");
        }

        static Bonus breakSpeedPct(int pct) {
            return new Bonus("break_speed", Attributes.BLOCK_BREAK_SPEED, pct / 100.0,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE, sign(pct) + pct + "% de velocidade de mineração");
        }

        static Bonus knockbackResistPct(int pct) {
            return new Bonus("knockback_resist", Attributes.KNOCKBACK_RESISTANCE, pct / 100.0,
                    AttributeModifier.Operation.ADD_VALUE, sign(pct) + pct + "% de resistência a empurrão");
        }
    }
}
