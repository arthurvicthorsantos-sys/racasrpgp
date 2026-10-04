package com.racasrpg.race;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/** Honra do jogador e promoções de patente. */
public final class HonorManager {
    private HonorManager() {
    }

    public static int get(Player player) {
        return player.getData(ModAttachments.HONOR);
    }

    public static void add(ServerPlayer player, int amount) {
        Race race = Race.byId(RaceManager.get(player).race());
        if (race == null || amount <= 0) {
            return;
        }
        int before = get(player);
        int after = before + amount;
        player.setData(ModAttachments.HONOR, after);

        int oldRank = Ranks.rankIndex(before);
        int newRank = Ranks.rankIndex(after);
        if (newRank > oldRank) {
            RaceManager.applyEffects(player);
            player.level().playSound(null, player.blockPosition(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                    SoundSource.PLAYERS, 1.0F, 1.0F);
            player.sendSystemMessage(Component.literal("Promoção! Sua nova patente: " + Ranks.title(race, newRank))
                    .withStyle(ChatFormatting.GOLD));
        }
    }
}
