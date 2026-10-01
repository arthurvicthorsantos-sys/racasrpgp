package com.racasrpg.client;

import com.racasrpg.entity.ModEntities;

import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.WitherSkeletonRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** Registra os renderizadores (usam os modelos e texturas do jogo base). Só carregado no cliente. */
public final class ClientSetup {
    private ClientSetup() {
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.GOBLIN_RAIDER.get(), ZombieRenderer::new);
        event.registerEntityRenderer(ModEntities.ORC_BRUTE.get(), ZombieRenderer::new);
        event.registerEntityRenderer(ModEntities.GOBLIN_KING.get(), ZombieRenderer::new);
        event.registerEntityRenderer(ModEntities.DARK_ELF_ARCHER.get(), SkeletonRenderer::new);
        event.registerEntityRenderer(ModEntities.SHADOW_CHAMPION.get(), WitherSkeletonRenderer::new);
        event.registerEntityRenderer(ModEntities.RACE_MASTER.get(), VillagerRenderer::new);
    }
}
