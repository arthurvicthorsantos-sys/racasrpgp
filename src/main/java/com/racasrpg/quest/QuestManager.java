package com.racasrpg.quest;

import java.util.function.Supplier;

import com.racasrpg.entity.ModEntities;
import com.racasrpg.net.ModNetwork;
import com.racasrpg.race.HonorManager;
import com.racasrpg.race.ModAttachments;
import com.racasrpg.race.QuestData;
import com.racasrpg.race.Race;
import com.racasrpg.race.RaceData;
import com.racasrpg.race.RaceManager;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Missões entregues pelos Mestres da Raça: abater certos monstros em troca de recompensas. */
public final class QuestManager {
    private QuestManager() {
    }

    public enum Quest {
        GOBLINS("Abater 10 Saqueadores Goblin", () -> ModEntities.GOBLIN_RAIDER.get(), 10,
                () -> new ItemStack(Items.EMERALD, 8)),
        ARCHERS("Abater 8 Arqueiros Sombrios", () -> ModEntities.DARK_ELF_ARCHER.get(), 8,
                () -> new ItemStack(Items.EMERALD, 14)),
        ORCS("Abater 5 Brutamontes Orcs", () -> ModEntities.ORC_BRUTE.get(), 5,
                () -> new ItemStack(Items.DIAMOND, 2)),
        GOBLIN_KING("Derrotar o Rei Goblin (ele mora em covis espalhados pelo mundo)",
                () -> ModEntities.GOBLIN_KING.get(), 1,
                () -> new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1)),
        SHADOW_CHAMPION("Derrotar o Campeão Sombrio (ele vive em ruínas sombrias pelo mundo)",
                () -> ModEntities.SHADOW_CHAMPION.get(), 1,
                () -> new ItemStack(Items.NETHERITE_INGOT, 1));

        private final String description;
        private final Supplier<EntityType<?>> target;
        private final int count;
        private final Supplier<ItemStack> reward;

        Quest(String description, Supplier<EntityType<?>> target, int count, Supplier<ItemStack> reward) {
            this.description = description;
            this.target = target;
            this.count = count;
            this.reward = reward;
        }
    }

    private static QuestData get(ServerPlayer player) {
        return player.getData(ModAttachments.QUEST_DATA);
    }

    private static void set(ServerPlayer player, QuestData data) {
        player.setData(ModAttachments.QUEST_DATA, data);
    }

    private static void say(ServerPlayer player, String text, ChatFormatting color) {
        player.sendSystemMessage(Component.literal(text).withStyle(color));
    }

    /** Conta um abate para a missão atual do jogador, se o alvo for o certo. */
    public static void onKill(ServerPlayer player, LivingEntity victim) {
        QuestData data = get(player);
        Quest[] all = Quest.values();
        if (data.index() >= all.length) {
            return;
        }
        Quest quest = all[data.index()];
        if (victim.getType() != quest.target.get()) {
            return;
        }
        if (data.progress() >= quest.count) {
            return;
        }

        int progress = data.progress() + 1;
        set(player, new QuestData(data.index(), progress));
        player.displayClientMessage(Component.literal("Missão do Mestre: " + progress + "/" + quest.count)
                .withStyle(ChatFormatting.GREEN), true);
        if (progress >= quest.count) {
            say(player, "Missão concluída! Fale com um Mestre da Raça para receber a recompensa.", ChatFormatting.GOLD);
        }
    }

    /** O jogador clicou em um Mestre da Raça. */
    public static void talk(ServerPlayer player, String npcRaceId) {
        Race npcRace = Race.byId(npcRaceId);
        String who = npcRace == null ? "Mestre" : "Mestre " + npcRace.displayName();

        RaceData raceData = RaceManager.get(player);
        if (!raceData.hasRace()) {
            say(player, who + ": Você ainda não escolheu uma raça, viajante. Veja as opções.", ChatFormatting.YELLOW);
            ModNetwork.openMenu(player);
            return;
        }

        QuestData data = get(player);
        Quest[] all = Quest.values();
        if (data.index() >= all.length) {
            say(player, who + ": Você já cumpriu todas as minhas missões, herói!", ChatFormatting.YELLOW);
            return;
        }

        Quest quest = all[data.index()];
        if (data.progress() >= quest.count) {
            ItemStack reward = quest.reward.get();
            String rewardName = reward.getCount() + "x " + reward.getHoverName().getString();
            if (!player.getInventory().add(reward)) {
                player.drop(reward, false);
            }
            set(player, new QuestData(data.index() + 1, 0));
            HonorManager.add(player, 15);
            player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP,
                    SoundSource.PLAYERS, 1.0F, 1.0F);
            say(player, who + ": Bom trabalho! Sua recompensa: " + rewardName + ".", ChatFormatting.GOLD);

            QuestData next = get(player);
            if (next.index() < all.length) {
                say(player, "Próxima missão: " + all[next.index()].description, ChatFormatting.AQUA);
            } else {
                say(player, "Não tenho mais missões para você. Você é uma lenda!", ChatFormatting.YELLOW);
            }
            return;
        }

        say(player, who + ": Missão atual: " + quest.description + " (" + data.progress() + "/" + quest.count + ")",
                ChatFormatting.AQUA);
    }
}
