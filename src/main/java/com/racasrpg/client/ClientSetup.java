package com.racasrpg.client;

import com.racasrpg.entity.ModEntities;
import com.racasrpg.entity.Basilisk;
import com.racasrpg.entity.PactMaster;
import com.racasrpg.entity.SpiderQueen;

import net.minecraft.client.renderer.entity.BlazeRenderer;
import net.minecraft.client.renderer.entity.DrownedRenderer;
import net.minecraft.client.renderer.entity.EvokerRenderer;
import net.minecraft.client.renderer.entity.HuskRenderer;
import net.minecraft.client.renderer.entity.PillagerRenderer;
import net.minecraft.client.renderer.entity.RavagerRenderer;
import net.minecraft.client.renderer.entity.VexRenderer;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.VindicatorRenderer;
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
        event.registerEntityRenderer(ModEntities.STONE_COLOSSUS.get(), IronGolemRenderer::new);
        event.registerEntityRenderer(ModEntities.ORC_WARLORD.get(), VindicatorRenderer::new);
        event.registerEntityRenderer(ModEntities.SPIDER_QUEEN.get(), context -> new SpiderRenderer<SpiderQueen>(context));
        event.registerEntityRenderer(ModEntities.FLAME_WYRM.get(), BlazeRenderer::new);
        event.registerEntityRenderer(ModEntities.ROT_LICH.get(), SkeletonRenderer::new);
        event.registerEntityRenderer(ModEntities.HARVEST_SCARECROW.get(), HuskRenderer::new);
        event.registerEntityRenderer(ModEntities.LABYRINTH_GUARDIAN.get(), RavagerRenderer::new);
        event.registerEntityRenderer(ModEntities.DROWNED_KING.get(), DrownedRenderer::new);
        event.registerEntityRenderer(ModEntities.GNOME_AUTOMATON.get(), IronGolemRenderer::new);
        event.registerEntityRenderer(ModEntities.ARCHER_COLOSSUS.get(), PillagerRenderer::new);
        event.registerEntityRenderer(ModEntities.BASILISK.get(), context -> new SpiderRenderer<Basilisk>(context));
        event.registerEntityRenderer(ModEntities.FALLEN_ANGEL.get(), VexRenderer::new);
        event.registerEntityRenderer(ModEntities.PACT_MASTER.get(), context -> new EvokerRenderer<PactMaster>(context));
        event.registerEntityRenderer(ModEntities.RACE_MASTER.get(), VillagerRenderer::new);
    }
}
