package com.racasrpg.ability;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.racasrpg.classes.ClassDef;
import com.racasrpg.race.Race;
import com.racasrpg.race.RaceData;
import com.racasrpg.race.RaceManager;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Controla o uso das habilidades: estágio mínimo e recarga (guardada só enquanto o servidor roda).
 * Slots 0 a 2 = habilidades da raça (R, G, V). Slots 3 a 5 = habilidades da classe (Z, X, C).
 */
public final class AbilityManager {
    private AbilityManager() {
    }

    private static final Map<UUID, long[]> READY_AT = new ConcurrentHashMap<>();

    private static void message(ServerPlayer player, String text, ChatFormatting color) {
        player.displayClientMessage(Component.literal(text).withStyle(color), true);
    }

    public static void cast(ServerPlayer player, int slot) {
        if (slot < 0 || slot > 5) {
            return;
        }
        RaceData data = RaceManager.get(player);
        Race race = Race.byId(data.race());
        if (race == null) {
            message(player, "Escolha uma raça primeiro (/raca).", ChatFormatting.RED);
            return;
        }

        int level = slot % 3;
        Abilities.Ability ability;
        if (slot < 3) {
            ability = Abilities.get(race, level);
        } else {
            ClassDef clazz = ClassDef.byId(data.clazz());
            if (clazz == null) {
                message(player, "Escolha uma classe no menu (/raca).", ChatFormatting.RED);
                return;
            }
            ability = clazz.ability(level);
        }
        if (data.stage() < level) {
            message(player, "Habilidade bloqueada: evolua para o estágio " + (level + 1) + ".", ChatFormatting.RED);
            return;
        }

        long now = player.level().getGameTime();
        long[] ready = READY_AT.computeIfAbsent(player.getUUID(), id -> new long[6]);
        if (now < ready[slot]) {
            long seconds = (ready[slot] - now + 19) / 20;
            message(player, ability.name() + " recarregando: " + seconds + "s", ChatFormatting.GRAY);
            return;
        }

        ready[slot] = now + ability.cooldownSeconds() * 20L;
        ability.cast().accept(player);
        message(player, ability.name() + "!", ChatFormatting.GOLD);
    }
}
