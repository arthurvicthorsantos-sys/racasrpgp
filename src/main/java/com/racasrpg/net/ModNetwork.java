package com.racasrpg.net;

import com.racasrpg.ability.AbilityManager;
import com.racasrpg.classes.ClassDef;
import com.racasrpg.client.ClientHooks;
import com.racasrpg.race.HonorManager;
import com.racasrpg.race.Race;
import com.racasrpg.race.RaceData;
import com.racasrpg.race.RaceManager;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetwork {
    private ModNetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(ChooseRacePayload.TYPE, ChooseRacePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player) {
                        Race race = Race.byId(payload.raceId());
                        if (race != null) {
                            RaceManager.choose(player, race);
                        }
                        openMenu(player);
                    }
                }));

        registrar.playToServer(EvolvePayload.TYPE, EvolvePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player) {
                        RaceManager.evolve(player);
                        openMenu(player);
                    }
                }));

        registrar.playToServer(ChooseClassPayload.TYPE, ChooseClassPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player) {
                        ClassDef clazz = ClassDef.byId(payload.classId());
                        if (clazz != null) {
                            RaceManager.chooseClass(player, clazz);
                        }
                        openMenu(player);
                    }
                }));

        registrar.playToServer(CastAbilityPayload.TYPE, CastAbilityPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player) {
                        AbilityManager.cast(player, payload.slot());
                    }
                }));

        registrar.playToClient(OpenMenuPayload.TYPE, OpenMenuPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientHooks.openMenu(payload)));
    }

    /** Manda o cliente abrir o menu de raça com o estado atual do jogador. */
    public static void openMenu(ServerPlayer player) {
        RaceData data = RaceManager.get(player);
        PacketDistributor.sendToPlayer(player, new OpenMenuPayload(data.race(), data.stage(), data.progress(), data.clazz(), HonorManager.get(player)));
    }
}
