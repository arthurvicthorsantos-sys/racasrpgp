package com.racasrpg.classes;

import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.Nullable;

import com.racasrpg.ability.Abilities.Ability;
import com.racasrpg.ability.AbilityEffects;
import com.racasrpg.race.Race.Bonus;
import com.racasrpg.race.Race.Bonuses;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import static com.racasrpg.ability.AbilityEffects.fx;

/**
 * Classes: escolhidas depois da raça. Dão bônus próprios e 3 habilidades (teclas Z, X e C),
 * liberadas conforme o estágio da raça (1º, 2º e 3º estágio).
 */
public enum ClassDef {

    GUERREIRO("guerreiro", "Guerreiro", Items.SHIELD,
            "Linha de frente. Resistente, forte e firme no combate corpo a corpo.",
            "O guerreiro treina até o corpo esquecer o medo. Não busca glória, busca ficar de pé quando todos caírem.",
            List.of(Bonuses.maxHealth(4), Bonuses.armor(1), Bonuses.attackDamage(1)),
            List.of(
                    new Ability("Escudo Erguido", "Resistência muito alta por 6s, mas fica mais lento.", 25,
                            p -> AbilityEffects.buff(p, ParticleTypes.CRIT,
                                    fx(MobEffects.DAMAGE_RESISTANCE, 6, 2), fx(MobEffects.MOVEMENT_SLOWDOWN, 6, 0))),
                    new Ability("Golpe Giratório", "Golpe em área que empurra monstros.", 15,
                            p -> AbilityEffects.slam(p, 4.5, 9.0F, 1.0)),
                    new Ability("Brado de Batalha", "Força e velocidade por 12s.", 40,
                            p -> AbilityEffects.buff(p, ParticleTypes.ANGRY_VILLAGER,
                                    fx(MobEffects.DAMAGE_BOOST, 12, 1), fx(MobEffects.MOVEMENT_SPEED, 12, 0))))),

    ARQUEIRO("arqueiro", "Arqueiro", Items.CROSSBOW,
            "Mira e mobilidade. Fere de longe e foge de perto.",
            "O arqueiro respira antes de soltar a corda. Quem vê a flecha chegar já está atrasado.",
            List.of(Bonuses.speedPct(5), Bonuses.luck(1), Bonuses.reach(0.5)),
            List.of(
                    new Ability("Tiro Certeiro", "Uma flecha muito forte e rápida.", 10,
                            p -> AbilityEffects.volley(p, 1, 0.0, 16.0, 0)),
                    new Ability("Flechas Flamejantes", "Leque de 5 flechas em chamas.", 18,
                            p -> AbilityEffects.volley(p, 5, 5.0, 6.0, 6)),
                    new Ability("Rolamento", "Pula para trás e ganha velocidade.", 8,
                            p -> {
                                AbilityEffects.dodge(p, 1.4);
                                AbilityEffects.buff(p, ParticleTypes.CLOUD, fx(MobEffects.MOVEMENT_SPEED, 3, 1));
                            }))),

    LADINO("ladino", "Ladino", Items.SHEARS,
            "Sombras e veneno. Rápido, silencioso e letal para quem não espera.",
            "O ladino só existe para quem ele quer que exista. O resto do mundo é cenário e oportunidade.",
            List.of(Bonuses.speedPct(8), Bonuses.sneakSpeedPct(100), Bonuses.maxHealth(-2)),
            List.of(
                    new Ability("Furtividade", "Invisível e rápido por 8s.", 24,
                            p -> AbilityEffects.buff(p, ParticleTypes.SMOKE,
                                    fx(MobEffects.INVISIBILITY, 8, 0), fx(MobEffects.MOVEMENT_SPEED, 8, 1))),
                    new Ability("Esquiva Rápida", "Teletransporte curto de até 8 blocos.", 12,
                            p -> AbilityEffects.blink(p, 8.0)),
                    new Ability("Veneno", "Envenena e enfraquece monstros perto.", 20,
                            p -> AbilityEffects.poison(p, 3.5, 8)))),

    CLERIGO("clerigo", "Clérigo", Items.GOLDEN_APPLE,
            "Cura e luz. Mantém o grupo de pé e fere os mortos-vivos.",
            "O clérigo carrega uma fé que não precisa de explicações, só de resultados. Sua luz fere o que odeia a vida.",
            List.of(Bonuses.maxHealth(2), Bonuses.luck(1)),
            List.of(
                    new Ability("Cura Maior", "Cura 14 de vida e recupera a fome.", 35,
                            p -> AbilityEffects.heal(p, 14.0F, 6, 20)),
                    new Ability("Luz Sagrada", "Fere monstros (o dobro em mortos-vivos).", 18,
                            p -> AbilityEffects.smite(p, 6.0, 7.0F)),
                    new Ability("Santuário", "Resistência e regeneração por 10s.", 45,
                            p -> AbilityEffects.buff(p, ParticleTypes.END_ROD,
                                    fx(MobEffects.DAMAGE_RESISTANCE, 10, 1), fx(MobEffects.REGENERATION, 10, 1))))),

    PALADINO("paladino", "Paladino", Items.GOLDEN_HELMET,
            "Guerreiro sagrado. Protege, golpeia e cura.",
            "O paladino fez um juramento que pesa mais que sua armadura. Quebrá-lo seria pior que morrer.",
            List.of(Bonuses.maxHealth(4), Bonuses.armor(2), Bonuses.knockbackResistPct(10)),
            List.of(
                    new Ability("Juramento Sagrado", "Resistência e força por 12s.", 35,
                            p -> AbilityEffects.buff(p, ParticleTypes.END_ROD,
                                    fx(MobEffects.DAMAGE_RESISTANCE, 12, 0), fx(MobEffects.DAMAGE_BOOST, 12, 0))),
                    new Ability("Martelo Sagrado", "Pancada sagrada em área.", 16,
                            p -> AbilityEffects.slam(p, 4.0, 7.0F, 1.0)),
                    new Ability("Mão Curativa", "Cura 8 de vida e recupera a fome.", 30,
                            p -> AbilityEffects.heal(p, 8.0F, 8, 10)))),

    DRUIDA("druida", "Druida", Items.OAK_SAPLING,
            "Aliado da natureza. Endurece como casca de árvore e espalha espinhos venenosos.",
            "O druida ouve o que a floresta sussurra. Quando ela grita, ele responde com raízes, espinhos e fúria selvagem.",
            List.of(Bonuses.maxHealth(2), Bonuses.luck(1), Bonuses.speedPct(3)),
            List.of(
                    new Ability("Fúria da Natureza", "Força, velocidade e regeneração por 10s.", 40,
                            p -> AbilityEffects.buff(p, ParticleTypes.HAPPY_VILLAGER,
                                    fx(MobEffects.DAMAGE_BOOST, 10, 0), fx(MobEffects.MOVEMENT_SPEED, 10, 1),
                                    fx(MobEffects.REGENERATION, 10, 0))),
                    new Ability("Casca de Árvore", "Resistência e regeneração por 12s.", 40,
                            p -> AbilityEffects.buff(p, ParticleTypes.HAPPY_VILLAGER,
                                    fx(MobEffects.DAMAGE_RESISTANCE, 12, 1), fx(MobEffects.REGENERATION, 12, 0))),
                    new Ability("Espinhos Venenosos", "Envenena monstros em um raio maior.", 25,
                            p -> AbilityEffects.poison(p, 5.0, 8)))),

    BARBARO("barbaro", "Bárbaro", Items.BONE,
            "Fúria pura. Muito dano e muita vida, mas pouca defesa.",
            "O bárbaro não planeja, avança. Dizem que sua raiva é a única coisa que ele não sabe controlar, e a única que o salva.",
            List.of(Bonuses.maxHealth(6), Bonuses.attackDamage(2), Bonuses.armor(-1)),
            List.of(
                    new Ability("Fúria Bárbara", "Força altíssima e resistência por 10s.", 45,
                            p -> AbilityEffects.buff(p, ParticleTypes.ANGRY_VILLAGER,
                                    fx(MobEffects.DAMAGE_BOOST, 10, 2), fx(MobEffects.DAMAGE_RESISTANCE, 10, 0))),
                    new Ability("Terremoto", "Pancada gigante que empurra e fere monstros.", 25,
                            p -> AbilityEffects.slam(p, 6.0, 10.0F, 1.6)),
                    new Ability("Salto Brutal", "Salta muito para a frente.", 14,
                            p -> AbilityEffects.leap(p, 0.9, 1.3)))),

    MONGE("monge", "Monge", Items.LEATHER_BOOTS,
            "Artes marciais. Muito rápido, golpes certeiros e foco interior.",
            "O monge transformou o corpo em arma e a calma em escudo. Seu golpe mais forte é o que ele escolhe não dar, até precisar.",
            List.of(Bonuses.speedPct(8), Bonuses.attackDamage(1), Bonuses.jumpPct(10)),
            List.of(
                    new Ability("Punho Trovejante", "Golpe rápido em área curta.", 10,
                            p -> AbilityEffects.slam(p, 3.0, 8.0F, 0.8)),
                    new Ability("Passo do Vento", "Salto baixo e longo para a frente.", 8,
                            p -> AbilityEffects.leap(p, 0.6, 1.8)),
                    new Ability("Meditação", "Cura 10 de vida e regenera por 6s.", 35,
                            p -> {
                                AbilityEffects.heal(p, 10.0F, 4, 8);
                                AbilityEffects.buff(p, ParticleTypes.END_ROD, fx(MobEffects.REGENERATION, 6, 1));
                            }))),

    BARDO("bardo", "Bardo", Items.NOTE_BLOCK,
            "Apoio do grupo. Canções que dão força, velocidade e cura aos aliados por perto.",
            "O bardo não carrega a espada, carrega a história. E quem ouve a história certa na hora certa luta como um herói.",
            List.of(Bonuses.luck(2), Bonuses.maxHealth(-2), Bonuses.speedPct(4)),
            List.of(
                    new Ability("Canção de Coragem", "Aliados por perto ganham força e velocidade por 12s.", 40,
                            p -> AbilityEffects.aura(p, 12.0, ParticleTypes.NOTE,
                                    fx(MobEffects.DAMAGE_BOOST, 12, 0), fx(MobEffects.MOVEMENT_SPEED, 12, 0))),
                    new Ability("Melodia de Cura", "Aliados por perto regeneram por 8s.", 35,
                            p -> AbilityEffects.aura(p, 12.0, ParticleTypes.HEART,
                                    fx(MobEffects.REGENERATION, 8, 1))),
                    new Ability("Acorde Estridente", "Som que fere e atrasa monstros.", 25,
                            p -> AbilityEffects.roar(p, 7.0, 4.0F, 6)))),

    RASTREADOR("rastreador", "Rastreador", Items.SPYGLASS,
            "Caçador de longe. Marca as presas, prende em teias e some na mata.",
            "O rastreador lê o chão como outros leem livros. Nenhuma presa escapa de quem sabe para onde ela vai fugir.",
            List.of(Bonuses.speedPct(5), Bonuses.luck(1), Bonuses.sneakSpeedPct(50)),
            List.of(
                    new Ability("Marcar Presas", "Monstros ao redor brilham e ficam mais fracos por 12s.", 30,
                            p -> AbilityEffects.markEnemies(p, 24.0, 12)),
                    new Ability("Armadilha de Teia", "Prende o ponto que você olha em teias.", 20,
                            p -> AbilityEffects.trap(p, 18.0)),
                    new Ability("Camuflagem", "Invisível e rápido por 10s.", 40,
                            p -> AbilityEffects.buff(p, ParticleTypes.CLOUD,
                                    fx(MobEffects.INVISIBILITY, 10, 0), fx(MobEffects.MOVEMENT_SPEED, 10, 0))))),

    CAVALEIRO("cavaleiro", "Cavaleiro", Items.IRON_HORSE_ARMOR,
            "Armadura pesada e investidas. Lidera o grupo com estandartes e brados.",
            "O cavaleiro foi criado para a carga. Seu estandarte é a promessa de que ninguém ficará sozinho no campo.",
            List.of(Bonuses.armor(3), Bonuses.maxHealth(4), Bonuses.speedPct(-3)),
            List.of(
                    new Ability("Carga de Lança", "Avança com força e derruba quem estiver na frente.", 14,
                            p -> {
                                AbilityEffects.dash(p, 2.0);
                                AbilityEffects.slam(p, 3.0, 9.0F, 1.4);
                            }),
                    new Ability("Estandarte de Guerra", "Aliados por perto ganham resistência e força por 10s.", 40,
                            p -> AbilityEffects.aura(p, 10.0, ParticleTypes.CRIT,
                                    fx(MobEffects.DAMAGE_RESISTANCE, 10, 0), fx(MobEffects.DAMAGE_BOOST, 10, 0))),
                    new Ability("Grito de Comando", "Provoca monstros ao redor para atacarem você.", 20,
                            p -> AbilityEffects.taunt(p, 10.0)))),

    GLADIADOR("gladiador", "Gladiador", Items.NETHERITE_AXE,
            "Lutador de arena. Combos rápidos e golpes de misericórdia.",
            "O gladiador aprendeu que a multidão ama quem sangra e vence. Cada golpe dele é pensado para o espetáculo, e para o fim.",
            List.of(Bonuses.attackDamage(2), Bonuses.maxHealth(4)),
            List.of(
                    new Ability("Saudação da Arena", "Força e velocidade por 10s.", 35,
                            p -> AbilityEffects.buff(p, ParticleTypes.CRIT,
                                    fx(MobEffects.DAMAGE_BOOST, 10, 1), fx(MobEffects.MOVEMENT_SPEED, 10, 0))),
                    new Ability("Rasteira", "Derruba e atrasa monstros ao redor.", 14,
                            p -> {
                                AbilityEffects.slam(p, 3.0, 5.0F, 0.3);
                                AbilityEffects.smoke(p, 3.0, 3);
                            }),
                    new Ability("Golpe de Misericórdia", "Golpe devastador em área curta.", 22,
                            p -> AbilityEffects.slam(p, 3.5, 14.0F, 0.4)))),

    INQUISIDOR("inquisidor", "Inquisidor", Items.FLINT_AND_STEEL,
            "Caçador de monstros. Fogo purificador e interrogatórios sem piedade.",
            "O inquisidor acredita que o fogo limpa o que a lei não alcança. Ninguém discute com ele, e poucos o veem sorrir.",
            List.of(Bonuses.attackDamage(1), Bonuses.armor(1), Bonuses.luck(1)),
            List.of(
                    new Ability("Purificação", "Fogo em área que queima monstros.", 18,
                            p -> AbilityEffects.fireBurst(p, 5.0, 6.0F, 5)),
                    new Ability("Interrogatório", "Monstros ao redor ficam visíveis e mais fracos por 10s.", 28,
                            p -> AbilityEffects.markEnemies(p, 20.0, 10)),
                    new Ability("Pira Sagrada", "Fogo forte e cura parte do dano causado.", 35,
                            p -> {
                                AbilityEffects.fireBurst(p, 4.0, 8.0F, 8);
                                AbilityEffects.lifeDrain(p, 4.0, 4.0F, 8.0F);
                            }))),

    SENTINELA("sentinela", "Sentinela", Items.BELL,
            "Defensor inabalável. Provoca os inimigos e aguenta o golpe no lugar do grupo.",
            "A sentinela fica onde ninguém quer ficar: entre o perigo e quem importa. Quando o sino toca, ela já está lá.",
            List.of(Bonuses.armor(4), Bonuses.knockbackResistPct(20), Bonuses.speedPct(-5), Bonuses.maxHealth(4)),
            List.of(
                    new Ability("Provocar", "Monstros ao redor atacam você, e você ganha resistência.", 22,
                            p -> {
                                AbilityEffects.taunt(p, 10.0);
                                AbilityEffects.buff(p, ParticleTypes.CRIT, fx(MobEffects.DAMAGE_RESISTANCE, 6, 1));
                            }),
                    new Ability("Muralha", "Resistência altíssima por 5s, mas fica quase parado.", 35,
                            p -> AbilityEffects.buff(p, ParticleTypes.CRIT,
                                    fx(MobEffects.DAMAGE_RESISTANCE, 5, 3), fx(MobEffects.MOVEMENT_SLOWDOWN, 5, 2))),
                    new Ability("Alarme", "Marca monstros longe e acelera aliados por perto.", 40,
                            p -> {
                                AbilityEffects.markEnemies(p, 20.0, 10);
                                AbilityEffects.aura(p, 12.0, ParticleTypes.NOTE, fx(MobEffects.MOVEMENT_SPEED, 10, 0));
                            }))),

    MERCENARIO("mercenario", "Mercenário", Items.GOLD_INGOT,
            "Soldado de aluguel. Sorte, bom dano e fuga rápida quando o contrato aperta.",
            "O mercenário luta por quem paga melhor, mas nunca vendeu o próprio nome. Seus contratos são sagrados, o resto é negociável.",
            List.of(Bonuses.luck(2), Bonuses.attackDamage(1), Bonuses.speedPct(3)),
            List.of(
                    new Ability("Contrato", "Sorte e força por 15s.", 40,
                            p -> AbilityEffects.buff(p, ParticleTypes.HAPPY_VILLAGER,
                                    fx(MobEffects.LUCK, 15, 1), fx(MobEffects.DAMAGE_BOOST, 15, 0))),
                    new Ability("Golpe Pago", "Fere monstros ao redor e te cura.", 22,
                            p -> AbilityEffects.lifeDrain(p, 4.0, 6.0F, 8.0F)),
                    new Ability("Fuga", "Pula para trás e fica invisível por 3s.", 25,
                            p -> {
                                AbilityEffects.dodge(p, 1.8);
                                AbilityEffects.buff(p, ParticleTypes.SMOKE, fx(MobEffects.INVISIBILITY, 3, 0));
                            })));

    private final String id;
    private final String displayName;
    private final Item icon;
    private final String description;
    private final String lore;
    private final List<Bonus> bonuses;
    private final List<Ability> abilities;

    ClassDef(String id, String displayName, Item icon, String description, String lore,
             List<Bonus> bonuses, List<Ability> abilities) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
        this.lore = lore;
        this.bonuses = bonuses;
        this.abilities = abilities;
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

    public List<Bonus> bonuses() {
        return bonuses;
    }

    public Ability ability(int slot) {
        return abilities.get(Math.max(0, Math.min(slot, abilities.size() - 1)));
    }

    @Nullable
    public static ClassDef byId(String id) {
        if (id == null || id.isEmpty()) return null;
        String wanted = id.toLowerCase(Locale.ROOT);
        for (ClassDef c : values()) {
            if (c.id.equals(wanted)) return c;
        }
        return null;
    }
}
