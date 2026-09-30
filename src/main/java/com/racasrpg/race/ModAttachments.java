package com.racasrpg.race;

import com.racasrpg.RacasRpg;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import net.neoforged.neoforge.registries.DeferredHolder;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, RacasRpg.MODID);

    /** Dados da raca do jogador. Salvos com o jogador e mantidos ao morrer. */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<RaceData>> RACE_DATA =
            ATTACHMENT_TYPES.register("race_data", () -> AttachmentType.builder(() -> RaceData.EMPTY)
                    .serialize(RaceData.CODEC)
                    .copyOnDeath()
                    .build());
}
