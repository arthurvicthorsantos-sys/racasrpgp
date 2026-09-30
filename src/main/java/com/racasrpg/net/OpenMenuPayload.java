package com.racasrpg.net;

import com.racasrpg.RacasRpg;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Servidor -> cliente: abre o menu de raça com o estado atual do jogador. race vazio = escolher raça. */
public record OpenMenuPayload(String race, int stage, int progress) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenMenuPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(RacasRpg.MODID, "open_menu"));

    public static final StreamCodec<ByteBuf, OpenMenuPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, OpenMenuPayload::race,
            ByteBufCodecs.VAR_INT, OpenMenuPayload::stage,
            ByteBufCodecs.VAR_INT, OpenMenuPayload::progress,
            OpenMenuPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
