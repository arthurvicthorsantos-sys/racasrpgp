package com.racasrpg.net;

import com.racasrpg.RacasRpg;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Cliente -> servidor: o jogador apertou a tecla de uma habilidade (0 a 2 = raça, 3 a 5 = classe). */
public record CastAbilityPayload(int slot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CastAbilityPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(RacasRpg.MODID, "cast_ability"));

    public static final StreamCodec<ByteBuf, CastAbilityPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, CastAbilityPayload::slot,
            CastAbilityPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
