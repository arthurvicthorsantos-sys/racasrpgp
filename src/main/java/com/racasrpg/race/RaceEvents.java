package com.racasrpg.race;

import com.racasrpg.RacasRpg;
import com.racasrpg.entity.BossInfo;
import com.racasrpg.entity.IRaceBoss;
import com.racasrpg.item.SetBonuses;
import com.racasrpg.net.ModNetwork;
import com.racasrpg.quest.QuestManager;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = RacasRpg.MODID)
public class RaceEvents {

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RaceManager.applyEffects(player);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RaceManager.applyEffects(player);
        }
    }

    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RaceManager.applyEffects(player);
        }
    }

    /** Abre o menu ~3 segundos depois de entrar no mundo, se o jogador ainda não escolheu raça ou classe. */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (player.tickCount == 60 && (!RaceManager.get(player).hasRace() || !RaceManager.get(player).hasClass())) {
            ModNetwork.openMenu(player);
        }
        if (player.tickCount % 40 == 0) {
            SetBonuses.tick(player);
        }
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }

        QuestManager.onKill(player, event.getEntity());

        if (event.getEntity() instanceof IRaceBoss boss) {
            RaceManager.addProgress(player, Race.MissionType.KILL_BOSS);
            BossInfo mine = BossInfo.forRace(RaceManager.get(player).race());
            if (mine != null && mine.id().equals(boss.bossId())) {
                RaceManager.addProgress(player, Race.MissionType.KILL_RACE_BOSS);
            }
            HonorManager.add(player, 40);
        }

        if (event.getEntity() instanceof Enemy) {
            RaceManager.addProgress(player, Race.MissionType.KILL_HOSTILE);
            HonorManager.add(player, 1);

            Entity direct = event.getSource().getDirectEntity();
            if (direct instanceof Projectile) {
                RaceManager.addProgress(player, Race.MissionType.KILL_RANGED);
            } else if (direct == player) {
                RaceManager.addProgress(player, Race.MissionType.KILL_MELEE);
            }
        }
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }
        BlockState state = event.getState();
        if (state.is(Tags.Blocks.ORES)) {
            RaceManager.addProgress(player, Race.MissionType.MINE_ORE);
        }
        if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
            RaceManager.addProgress(player, Race.MissionType.HARVEST_CROP);
        }
    }

    /** Mostra a patente antes do nome do jogador no chat. */
    @SubscribeEvent
    public static void onNameFormat(PlayerEvent.NameFormat event) {
        Player player = event.getEntity();
        Race race = Race.byId(player.getData(ModAttachments.RACE_DATA).race());
        if (race == null) {
            return;
        }
        String title = Ranks.title(race, Ranks.rankIndex(HonorManager.get(player)));
        event.setDisplayname(Component.literal("[" + title + "] ").withStyle(ChatFormatting.GOLD)
                .append(event.getDisplayname()));
    }
}
