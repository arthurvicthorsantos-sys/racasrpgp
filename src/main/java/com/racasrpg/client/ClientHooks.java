package com.racasrpg.client;

import com.racasrpg.net.OpenMenuPayload;
import com.racasrpg.race.RaceData;

import net.minecraft.client.Minecraft;

/** Código que só existe no cliente. Só é carregado quando o servidor manda abrir o menu. */
public final class ClientHooks {
    private ClientHooks() {
    }

    public static void openMenu(OpenMenuPayload payload) {
        RaceData data = new RaceData(payload.race(), payload.stage(), payload.progress());
        Minecraft.getInstance().setScreen(new RaceScreen(data));
    }
}
