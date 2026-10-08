package com.racasrpg.ability;

import java.util.List;
import java.util.function.Consumer;

import com.racasrpg.race.Race;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;

import static com.racasrpg.ability.AbilityEffects.fx;

/** As 3 habilidades de cada raça. A habilidade N é liberada ao chegar no estágio N da raça. */
public final class Abilities {
    private Abilities() {
    }

    public record Ability(String name, String description, int cooldownSeconds, Consumer<ServerPlayer> cast) {
    }

    public static Ability get(Race race, int slot) {
        return LIST_BY_RACE[race.ordinal()].get(Math.max(0, Math.min(slot, 2)));
    }

    private static Ability a(String name, String description, int cooldown, Consumer<ServerPlayer> cast) {
        return new Ability(name, description, cooldown, cast);
    }

    @SuppressWarnings("unchecked")
    private static final List<Ability>[] LIST_BY_RACE = new List[]{
            // HUMANO
            List.of(
                    a("Grito de Coragem", "Força e resistência por 10s.", 25,
                            p -> AbilityEffects.buff(p, ParticleTypes.CRIT,
                                    fx(MobEffects.DAMAGE_BOOST, 10, 0), fx(MobEffects.DAMAGE_RESISTANCE, 10, 0))),
                    a("Golpe Heroico", "Pancada em área que empurra monstros.", 18,
                            p -> AbilityEffects.slam(p, 4.0, 7.0F, 0.9)),
                    a("Juramento do Herói", "Cura 10 de vida, absorção e regeneração.", 60,
                            p -> {
                                AbilityEffects.heal(p, 10.0F, 4, 15);
                                AbilityEffects.buff(p, ParticleTypes.HAPPY_VILLAGER, fx(MobEffects.REGENERATION, 8, 1));
                            })),
            // ELFO
            List.of(
                    a("Salto Élfico", "Salta para a frente e plana suavemente.", 12,
                            p -> {
                                AbilityEffects.leap(p, 0.8, 1.0);
                                AbilityEffects.buff(p, ParticleTypes.CLOUD, fx(MobEffects.SLOW_FALLING, 6, 0));
                            }),
                    a("Chuva de Flechas", "Dispara um leque de 7 flechas.", 16,
                            p -> AbilityEffects.volley(p, 7, 7.0, 5.0, 0)),
                    a("Passo da Floresta", "Teletransporte de até 14 blocos e velocidade.", 20,
                            p -> {
                                AbilityEffects.blink(p, 14.0);
                                AbilityEffects.buff(p, ParticleTypes.END_ROD, fx(MobEffects.MOVEMENT_SPEED, 6, 1));
                            })),
            // ANAO
            List.of(
                    a("Pele de Pedra", "Resistência alta por 12s, mas fica mais lento.", 30,
                            p -> AbilityEffects.buff(p, ParticleTypes.CRIT,
                                    fx(MobEffects.DAMAGE_RESISTANCE, 12, 2), fx(MobEffects.MOVEMENT_SLOWDOWN, 12, 0))),
                    a("Pancada de Martelo", "Golpe sísmico forte em área.", 20,
                            p -> AbilityEffects.slam(p, 5.0, 9.0F, 1.2)),
                    a("Fúria da Forja", "Mineração e força aumentadas por 15s.", 45,
                            p -> AbilityEffects.buff(p, ParticleTypes.FLAME,
                                    fx(MobEffects.DIG_SPEED, 15, 2), fx(MobEffects.DAMAGE_BOOST, 15, 1)))),
            // ORC
            List.of(
                    a("Grito de Guerra", "Força e velocidade por 12s.", 25,
                            p -> AbilityEffects.buff(p, ParticleTypes.ANGRY_VILLAGER,
                                    fx(MobEffects.DAMAGE_BOOST, 12, 1), fx(MobEffects.MOVEMENT_SPEED, 12, 0))),
                    a("Investida", "Avança e bate em quem estiver perto.", 14,
                            p -> {
                                AbilityEffects.dash(p, 1.8);
                                AbilityEffects.slam(p, 3.0, 6.0F, 0.8);
                            }),
                    a("Fúria Orc", "Força altíssima e resistência por 15s.", 55,
                            p -> AbilityEffects.buff(p, ParticleTypes.ANGRY_VILLAGER,
                                    fx(MobEffects.DAMAGE_BOOST, 15, 2), fx(MobEffects.DAMAGE_RESISTANCE, 15, 0)))),
            // GOBLIN
            List.of(
                    a("Passo Furtivo", "Fica invisível e rápido por 6s.", 22,
                            p -> AbilityEffects.buff(p, ParticleTypes.SMOKE,
                                    fx(MobEffects.INVISIBILITY, 6, 0), fx(MobEffects.MOVEMENT_SPEED, 6, 1))),
                    a("Bomba de Fumaça", "Cega e atrasa monstros ao redor.", 20,
                            p -> AbilityEffects.smoke(p, 5.0, 6)),
                    a("Saque Real", "Mineração, velocidade e regeneração por 15s.", 45,
                            p -> AbilityEffects.buff(p, ParticleTypes.HAPPY_VILLAGER,
                                    fx(MobEffects.DIG_SPEED, 15, 1), fx(MobEffects.MOVEMENT_SPEED, 15, 1),
                                    fx(MobEffects.REGENERATION, 10, 0)))),
            // DRACONATO
            List.of(
                    a("Bola de Fogo Dracônica", "Lança uma bola de fogo que explode ao atingir e queima monstros em área.", 10,
                            p -> AbilityEffects.fireball(p, 11.0F, 4.5, 8)),
                    a("Escamas Ancestrais", "Resistência e imunidade a fogo por 12s.", 35,
                            p -> AbilityEffects.buff(p, ParticleTypes.FLAME,
                                    fx(MobEffects.DAMAGE_RESISTANCE, 12, 2), fx(MobEffects.FIRE_RESISTANCE, 12, 0))),
                    a("Rugido do Dragão", "Rugido que fere e enfraquece monstros.", 40,
                            p -> AbilityEffects.roar(p, 7.0, 6.0F, 8))),
            // HALFLING
            List.of(
                    a("Sorte de Halfling", "Sorte e velocidade por 20s.", 30,
                            p -> AbilityEffects.buff(p, ParticleTypes.HAPPY_VILLAGER,
                                    fx(MobEffects.LUCK, 20, 1), fx(MobEffects.MOVEMENT_SPEED, 20, 0))),
                    a("Colheita Farta", "Cura 8 de vida e recupera a fome.", 40,
                            p -> AbilityEffects.heal(p, 8.0F, 8, 0)),
                    a("Esconderijo", "Fica invisível e mais rápido por 6s.", 26,
                            p -> AbilityEffects.buff(p, ParticleTypes.CLOUD,
                                    fx(MobEffects.INVISIBILITY, 6, 0), fx(MobEffects.MOVEMENT_SPEED, 6, 1)))),
            // MINOTAURO
            List.of(
                    a("Chifrada", "Investida que empurra quem estiver perto.", 12,
                            p -> {
                                AbilityEffects.dash(p, 1.6);
                                AbilityEffects.slam(p, 2.5, 7.0F, 1.3);
                            }),
                    a("Rugido", "Rugido que fere e enfraquece monstros.", 28,
                            p -> AbilityEffects.roar(p, 6.0, 5.0F, 8)),
                    a("Fúria do Labirinto", "Força e resistência altas por 12s.", 55,
                            p -> AbilityEffects.buff(p, ParticleTypes.ANGRY_VILLAGER,
                                    fx(MobEffects.DAMAGE_BOOST, 12, 2), fx(MobEffects.DAMAGE_RESISTANCE, 12, 1)))),
            // TRITAO
            List.of(
                    a("Correnteza", "Impulso rápido com graça de golfinho.", 12,
                            p -> {
                                AbilityEffects.dash(p, 1.6);
                                AbilityEffects.buff(p, ParticleTypes.SPLASH, fx(MobEffects.DOLPHINS_GRACE, 10, 0));
                            }),
                    a("Bênção do Mar", "Respira na água e regenera por 30s.", 45,
                            p -> AbilityEffects.buff(p, ParticleTypes.SPLASH,
                                    fx(MobEffects.WATER_BREATHING, 60, 0), fx(MobEffects.DOLPHINS_GRACE, 30, 0),
                                    fx(MobEffects.REGENERATION, 10, 0))),
                    a("Maremoto", "Onda gigante que empurra e atrasa monstros.", 35,
                            p -> {
                                AbilityEffects.slam(p, 6.0, 7.0F, 1.6);
                                AbilityEffects.smoke(p, 6.0, 4);
                            })),
            // GNOMO
            List.of(
                    a("Foguete de Mola", "Salta muito alto e cai suavemente.", 14,
                            p -> {
                                AbilityEffects.leap(p, 1.3, 0.5);
                                AbilityEffects.buff(p, ParticleTypes.CLOUD, fx(MobEffects.SLOW_FALLING, 6, 0));
                            }),
                    a("Bomba Gnômica", "Explosão pequena que atrasa monstros.", 18,
                            p -> {
                                AbilityEffects.slam(p, 3.5, 6.0F, 0.9);
                                AbilityEffects.smoke(p, 4.0, 3);
                            }),
                    a("Engenhoca", "Mineração, velocidade e regeneração por 15s.", 45,
                            p -> AbilityEffects.buff(p, ParticleTypes.CRIT,
                                    fx(MobEffects.DIG_SPEED, 15, 2), fx(MobEffects.MOVEMENT_SPEED, 10, 1),
                                    fx(MobEffects.REGENERATION, 8, 0)))),
            // DROW
            List.of(
                    a("Véu das Sombras", "Invisível por 8s, com visão noturna.", 25,
                            p -> AbilityEffects.buff(p, ParticleTypes.SMOKE,
                                    fx(MobEffects.INVISIBILITY, 8, 0), fx(MobEffects.NIGHT_VISION, 20, 0))),
                    a("Passo Sombrio", "Teletransporte de até 12 blocos e velocidade.", 16,
                            p -> {
                                AbilityEffects.blink(p, 12.0);
                                AbilityEffects.buff(p, ParticleTypes.SMOKE, fx(MobEffects.MOVEMENT_SPEED, 4, 1));
                            }),
                    a("Chuva de Sombras", "Fere e faz definhar monstros ao redor.", 35,
                            p -> AbilityEffects.curse(p, 7.0, 6.0F, 6))),
            // CENTAURO
            List.of(
                    a("Galope", "Velocidade e pulo altos por 6s.", 20,
                            p -> AbilityEffects.buff(p, ParticleTypes.CLOUD,
                                    fx(MobEffects.MOVEMENT_SPEED, 6, 2), fx(MobEffects.JUMP, 6, 1))),
                    a("Pisada", "Pisotear que fere e empurra monstros.", 18,
                            p -> AbilityEffects.slam(p, 4.0, 8.0F, 1.2)),
                    a("Flecha Perfurante", "Uma flecha muito forte e rápida.", 12,
                            p -> AbilityEffects.volley(p, 1, 0.0, 14.0, 0))),
            // REPTILIANO
            List.of(
                    a("Bote", "Pulo baixo e longo para a frente.", 10,
                            p -> AbilityEffects.leap(p, 0.55, 1.6)),
                    a("Mordida Venenosa", "Envenena e enfraquece monstros perto.", 18,
                            p -> AbilityEffects.poison(p, 3.5, 8)),
                    a("Pele de Camaleão", "Invisível e resistente por 10s.", 40,
                            p -> AbilityEffects.buff(p, ParticleTypes.CLOUD,
                                    fx(MobEffects.INVISIBILITY, 10, 0), fx(MobEffects.DAMAGE_RESISTANCE, 10, 0)))),
            // ANJO
            List.of(
                    a("Bênção", "Cura 12 de vida, absorção e regeneração.", 40,
                            p -> {
                                AbilityEffects.heal(p, 12.0F, 4, 15);
                                AbilityEffects.buff(p, ParticleTypes.END_ROD, fx(MobEffects.REGENERATION, 8, 0));
                            }),
                    a("Luz Sagrada", "Fere monstros (o dobro em mortos-vivos).", 20,
                            p -> AbilityEffects.smite(p, 6.0, 8.0F)),
                    a("Asas de Luz", "Salta e plana por 12s, mais rápido.", 30,
                            p -> {
                                AbilityEffects.leap(p, 0.8, 0.8);
                                AbilityEffects.buff(p, ParticleTypes.END_ROD,
                                        fx(MobEffects.SLOW_FALLING, 12, 0), fx(MobEffects.MOVEMENT_SPEED, 8, 0));
                            })),
            // INFERNAL
            List.of(
                    a("Explosão Infernal", "Fogo em área que queima monstros.", 18,
                            p -> AbilityEffects.fireBurst(p, 5.0, 7.0F, 6)),
                    a("Passo de Chamas", "Avança queimando quem estiver perto.", 14,
                            p -> {
                                AbilityEffects.dash(p, 1.6);
                                AbilityEffects.fireBurst(p, 3.0, 4.0F, 4);
                            }),
                    a("Pacto de Sangue", "Drena vida de monstros ao redor e te cura.", 35,
                            p -> AbilityEffects.lifeDrain(p, 6.0, 5.0F, 10.0F)))
    };
}
