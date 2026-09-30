package com.racasrpg.net;

import com.racasrpg.RacasRpg;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Cliente -> servidor: o jogador clicou em "Evoluir" no menu. */
public record EvolvePayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<EvolvePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(RacasRpg.MODID, "evolve"));

    public static final StreamCodec<ByteBuf, EvolvePayload> STREAM_CODEC = StreamCodec.unit(new EvolvePayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
