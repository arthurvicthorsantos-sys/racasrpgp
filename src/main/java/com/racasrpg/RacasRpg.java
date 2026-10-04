package com.racasrpg;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.racasrpg.client.ClientKeys;
import com.racasrpg.client.ClientSetup;
import com.racasrpg.entity.ModEntities;
import com.racasrpg.item.ModItems;
import com.racasrpg.net.ModNetwork;
import com.racasrpg.race.ModAttachments;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(RacasRpg.MODID)
public class RacasRpg {
    public static final String MODID = "racasrpg";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RacasRpg(IEventBus modEventBus) {
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModItems.ARMOR_MATERIALS.register(modEventBus);
        ModItems.TABS.register(modEventBus);

        modEventBus.addListener(ModNetwork::register);
        modEventBus.addListener(ModEntities::registerAttributes);
        modEventBus.addListener(ModEntities::registerSpawnPlacements);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(ClientSetup::registerRenderers);
            modEventBus.addListener(ClientKeys::register);
            NeoForge.EVENT_BUS.addListener(ClientKeys::onTick);
        }

        LOGGER.info("Raças RPG carregado.");
    }
}
