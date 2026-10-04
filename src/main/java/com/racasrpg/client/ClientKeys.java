package com.racasrpg.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.racasrpg.net.CastAbilityPayload;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/** Teclas das habilidades (R, G e V por padrão; dá para trocar em Controles). Só existe no cliente. */
public final class ClientKeys {
    private ClientKeys() {
    }

    public static final KeyMapping ABILITY_1 = new KeyMapping("key.racasrpg.ability_1", InputConstants.KEY_R, "key.categories.racasrpg");
    public static final KeyMapping ABILITY_2 = new KeyMapping("key.racasrpg.ability_2", InputConstants.KEY_G, "key.categories.racasrpg");
    public static final KeyMapping ABILITY_3 = new KeyMapping("key.racasrpg.ability_3", InputConstants.KEY_V, "key.categories.racasrpg");

    public static final KeyMapping CLASS_1 = new KeyMapping("key.racasrpg.class_1", InputConstants.KEY_Z, "key.categories.racasrpg");
    public static final KeyMapping CLASS_2 = new KeyMapping("key.racasrpg.class_2", InputConstants.KEY_X, "key.categories.racasrpg");
    public static final KeyMapping CLASS_3 = new KeyMapping("key.racasrpg.class_3", InputConstants.KEY_C, "key.categories.racasrpg");

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(ABILITY_1);
        event.register(ABILITY_2);
        event.register(ABILITY_3);
        event.register(CLASS_1);
        event.register(CLASS_2);
        event.register(CLASS_3);
    }

    public static void onTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) {
            return;
        }
        while (ABILITY_1.consumeClick()) {
            PacketDistributor.sendToServer(new CastAbilityPayload(0));
        }
        while (ABILITY_2.consumeClick()) {
            PacketDistributor.sendToServer(new CastAbilityPayload(1));
        }
        while (ABILITY_3.consumeClick()) {
            PacketDistributor.sendToServer(new CastAbilityPayload(2));
        }
        while (CLASS_1.consumeClick()) {
            PacketDistributor.sendToServer(new CastAbilityPayload(3));
        }
        while (CLASS_2.consumeClick()) {
            PacketDistributor.sendToServer(new CastAbilityPayload(4));
        }
        while (CLASS_3.consumeClick()) {
            PacketDistributor.sendToServer(new CastAbilityPayload(5));
        }
    }
}
