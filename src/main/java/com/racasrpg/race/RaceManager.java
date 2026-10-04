package com.racasrpg.race;

import java.util.HashMap;
import java.util.Map;

import com.racasrpg.RacasRpg;
import com.racasrpg.classes.ClassDef;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public final class RaceManager {
    private RaceManager() {
    }

    public static RaceData get(Player player) {
        return player.getData(ModAttachments.RACE_DATA);
    }

    private static void set(Player player, RaceData data) {
        player.setData(ModAttachments.RACE_DATA, data);
    }

    private static Component msg(String text, ChatFormatting color) {
        return Component.literal(text).withStyle(color);
    }

    private static ResourceLocation modifierId(String key) {
        return ResourceLocation.fromNamespaceAndPath(RacasRpg.MODID, "raca/" + key);
    }

    // ---------------------------------------------------------------------------------------------
    // Escolher / evoluir / resetar
    // ---------------------------------------------------------------------------------------------

    /** Escolhe a raça (só quando o jogador ainda não tem uma). Devolve false se já tinha raça. */
    public static boolean choose(ServerPlayer player, Race race) {
        if (get(player).hasRace()) {
            return false;
        }
        set(player, new RaceData(race.id(), 0, 0, ""));
        applyEffects(player);
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP,
                SoundSource.PLAYERS, 1.0F, 1.0F);
        player.sendSystemMessage(msg("Você agora é um " + race.displayName() + "!", ChatFormatting.GOLD));
        return true;
    }

    /** Escolhe a classe (só quando já tem raça e ainda não tem classe). */
    public static boolean chooseClass(ServerPlayer player, ClassDef clazz) {
        RaceData data = get(player);
        if (!data.hasRace() || data.hasClass()) {
            return false;
        }
        set(player, new RaceData(data.race(), data.stage(), data.progress(), clazz.id()));
        applyEffects(player);
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP,
                SoundSource.PLAYERS, 1.0F, 1.2F);
        player.sendSystemMessage(msg("Você agora é da classe " + clazz.displayName() + "!", ChatFormatting.GOLD));
        return true;
    }

    /** Tenta evoluir para o próximo estágio. Envia mensagens explicando o resultado. */
    public static void evolve(ServerPlayer player) {
        RaceData data = get(player);
        Race race = Race.byId(data.race());
        if (race == null) {
            player.sendSystemMessage(msg("Você ainda não escolheu uma raça.", ChatFormatting.RED));
            return;
        }
        Race.Stage stage = race.stage(data.stage());
        Race.Mission mission = stage.mission();
        if (mission == null || data.stage() >= race.lastStageIndex()) {
            player.sendSystemMessage(msg("Você já alcançou a forma máxima: " + stage.name() + ".", ChatFormatting.YELLOW));
            return;
        }
        if (data.progress() < mission.target()) {
            player.sendSystemMessage(msg("Missão incompleta: " + race.describe(mission)
                    + " - " + data.progress() + "/" + mission.target(), ChatFormatting.RED));
            return;
        }

        int next = data.stage() + 1;
        int honor = HonorManager.get(player);
        if (Ranks.rankIndex(honor) < next) {
            player.sendSystemMessage(msg("Patente insuficiente: para evoluir você precisa ser "
                    + Ranks.title(race, next) + " (honra " + Ranks.THRESHOLDS[next] + "). Sua honra: " + honor + ".",
                    ChatFormatting.RED));
            return;
        }
        set(player, new RaceData(race.id(), next, 0, data.clazz()));
        applyEffects(player);
        player.setHealth(player.getMaxHealth());
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP,
                SoundSource.PLAYERS, 1.0F, 0.8F);
        player.sendSystemMessage(msg("Você evoluiu para " + race.stage(next).name() + "!", ChatFormatting.GOLD));
        HonorManager.add(player, 30);
    }

    /** Remove a raça do jogador (uso administrativo / testes). */
    public static void reset(ServerPlayer player) {
        set(player, RaceData.EMPTY);
        applyEffects(player);
    }

    // ---------------------------------------------------------------------------------------------
    // Progresso das missões
    // ---------------------------------------------------------------------------------------------

    /** Soma 1 ao progresso se a missão atual do jogador for deste tipo. */
    public static void addProgress(ServerPlayer player, Race.MissionType type) {
        RaceData data = get(player);
        Race race = Race.byId(data.race());
        if (race == null) return;

        Race.Mission mission = race.stage(data.stage()).mission();
        if (mission == null || mission.type() != type) return;
        if (data.progress() >= mission.target()) return;

        int progress = data.progress() + 1;
        set(player, new RaceData(data.race(), data.stage(), progress, data.clazz()));

        player.displayClientMessage(msg("Missão: " + progress + "/" + mission.target()
                + " - " + type.description(), ChatFormatting.GREEN), true);

        if (progress >= mission.target()) {
            player.sendSystemMessage(msg("Missão completa! Use /raca para abrir o menu e evoluir.", ChatFormatting.GOLD));
            player.level().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP,
                    SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Efeitos (atributos)
    // ---------------------------------------------------------------------------------------------

    /** Remove todos os modificadores da mod e aplica os do estágio atual. Pode ser chamado a qualquer momento. */
    public static void applyEffects(Player player) {
        for (Map.Entry<String, Holder<Attribute>> entry : trackedAttributes().entrySet()) {
            AttributeInstance instance = player.getAttribute(entry.getValue());
            if (instance != null) {
                instance.removeModifier(modifierId(entry.getKey()));
                instance.removeModifier(modifierId("classe/" + entry.getKey()));
                instance.removeModifier(modifierId("rank/" + entry.getKey()));
            }
        }

        RaceData data = get(player);
        Race race = Race.byId(data.race());
        if (race != null) {
            for (Race.Bonus bonus : race.stage(data.stage()).bonuses()) {
                AttributeInstance instance = player.getAttribute(bonus.attribute());
                if (instance != null) {
                    instance.addOrReplacePermanentModifier(new AttributeModifier(
                            modifierId(bonus.key()), bonus.amount(), bonus.operation()));
                }
            }
        }
        int rank = Ranks.rankIndex(HonorManager.get(player));
        if (race != null && rank > 0) {
            for (Race.Bonus bonus : java.util.List.of(Race.Bonuses.maxHealth(2.0 * rank), Race.Bonuses.attackDamage(0.5 * rank))) {
                AttributeInstance instance = player.getAttribute(bonus.attribute());
                if (instance != null) {
                    instance.addOrReplacePermanentModifier(new AttributeModifier(
                            modifierId("rank/" + bonus.key()), bonus.amount(), bonus.operation()));
                }
            }
        }
        ClassDef clazz = ClassDef.byId(data.clazz());
        if (clazz != null) {
            for (Race.Bonus bonus : clazz.bonuses()) {
                AttributeInstance instance = player.getAttribute(bonus.attribute());
                if (instance != null) {
                    instance.addOrReplacePermanentModifier(new AttributeModifier(
                            modifierId("classe/" + bonus.key()), bonus.amount(), bonus.operation()));
                }
            }
        }

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    /** Todos os atributos que qualquer raça/estágio pode modificar (chave -> atributo). */
    private static Map<String, Holder<Attribute>> trackedAttributes() {
        Map<String, Holder<Attribute>> map = new HashMap<>();
        for (Race race : Race.values()) {
            for (Race.Stage stage : race.stages()) {
                for (Race.Bonus bonus : stage.bonuses()) {
                    map.put(bonus.key(), bonus.attribute());
                }
            }
        }
        for (ClassDef clazz : ClassDef.values()) {
            for (Race.Bonus bonus : clazz.bonuses()) {
                map.put(bonus.key(), bonus.attribute());
            }
        }
        return map;
    }

    // ---------------------------------------------------------------------------------------------
    // Mensagens no chat (comandos /raca status e /raca info)
    // ---------------------------------------------------------------------------------------------

    /** Estado atual do jogador em texto (comando /raca status). */
    public static void sendStatus(ServerPlayer player) {
        RaceData data = get(player);
        Race race = Race.byId(data.race());
        if (race == null) {
            player.sendSystemMessage(msg("Você ainda não tem uma raça. Use /raca para abrir o menu.", ChatFormatting.YELLOW));
            return;
        }
        Race.Stage stage = race.stage(data.stage());
        int honor = HonorManager.get(player);
        player.sendSystemMessage(msg("Raça: " + race.displayName() + " (" + stage.name() + ") - Patente: "
                + Ranks.title(race, Ranks.rankIndex(honor)) + " (honra " + honor + ")", ChatFormatting.GOLD));
        for (Race.Bonus bonus : stage.bonuses()) {
            ChatFormatting color = bonus.amount() >= 0 ? ChatFormatting.GREEN : ChatFormatting.RED;
            player.sendSystemMessage(msg("  " + bonus.text(), color));
        }
        Race.Mission mission = stage.mission();
        if (mission == null) {
            player.sendSystemMessage(msg("Forma máxima alcançada.", ChatFormatting.YELLOW));
        } else {
            player.sendSystemMessage(msg("Missão: " + race.describe(mission) + " - "
                    + data.progress() + "/" + mission.target(), ChatFormatting.YELLOW));
        }
    }

    /** Lista todas as raças e estágios em texto (comando /raca info). */
    public static void sendInfo(ServerPlayer player) {
        for (Race race : Race.values()) {
            player.sendSystemMessage(msg("== " + race.displayName() + " (" + race.id() + ") ==", ChatFormatting.GOLD));
            for (int i = 0; i < race.stages().size(); i++) {
                Race.Stage stage = race.stage(i);
                StringBuilder line = new StringBuilder("  " + (i + 1) + ". " + stage.name() + ": ");
                if (stage.bonuses().isEmpty()) {
                    line.append("sem bônus");
                } else {
                    for (int b = 0; b < stage.bonuses().size(); b++) {
                        if (b > 0) line.append(", ");
                        line.append(stage.bonuses().get(b).text());
                    }
                }
                player.sendSystemMessage(msg(line.toString(), ChatFormatting.WHITE));
                if (stage.mission() != null) {
                    player.sendSystemMessage(msg("     para evoluir: " + race.describeFull(stage.mission()), ChatFormatting.GRAY));
                }
            }
        }
    }
}
