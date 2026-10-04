package com.racasrpg.net;

import com.racasrpg.RacasRpg;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Cliente -> servidor: o jogador clicou em "Escolher classe" no menu. */
public record ChooseClassPayload(String classId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChooseClassPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(RacasRpg.MODID, "choose_class"));

    public static final StreamCodec<ByteBuf, ChooseClassPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ChooseClassPayload::classId,
            ChooseClassPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
