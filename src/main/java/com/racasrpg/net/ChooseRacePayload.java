package com.racasrpg.net;

import com.racasrpg.RacasRpg;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Cliente -> servidor: o jogador clicou em "Escolher" no menu. */
public record ChooseRacePayload(String raceId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChooseRacePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(RacasRpg.MODID, "choose_race"));

    public static final StreamCodec<ByteBuf, ChooseRacePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ChooseRacePayload::raceId,
            ChooseRacePayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
