package com.racasrpg.race;

import com.racasrpg.RacasRpg;
import com.racasrpg.net.ModNetwork;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.Projectile;
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

    /** Abre o menu de escolha de raça ~3 segundos depois de entrar no mundo, se o jogador ainda não tem raça. */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player
                && player.tickCount == 60
                && !RaceManager.get(player).hasRace()) {
            ModNetwork.openMenu(player);
        }
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (event.getEntity() instanceof Enemy) {
            RaceManager.addProgress(player, Race.MissionType.KILL_HOSTILE);

            Entity direct = event.getSource().getDirectEntity();
            if (direct instanceof Projectile) {
                RaceManager.addProgress(player, Race.MissionType.KILL_RANGED);
            } else if (direct == player) {
                RaceManager.addProgress(player, Race.MissionType.KILL_MELEE);
            }
        } else if (event.getEntity() instanceof Animal) {
            RaceManager.addProgress(player, Race.MissionType.KILL_ANIMAL);
        }
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player && event.getState().is(Tags.Blocks.ORES)) {
            RaceManager.addProgress(player, Race.MissionType.MINE_ORE);
        }
    }
}
