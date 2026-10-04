package com.racasrpg.race;

import com.mojang.serialization.Codec;
import com.racasrpg.RacasRpg;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, RacasRpg.MODID);

    /** Dados da raça do jogador. Salvos com o jogador e mantidos ao morrer. */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<RaceData>> RACE_DATA =
            ATTACHMENT_TYPES.register("race_data", () -> AttachmentType.builder(() -> RaceData.EMPTY)
                    .serialize(RaceData.CODEC)
                    .copyOnDeath()
                    .build());

    /** Progresso das missões dos NPCs. */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<QuestData>> QUEST_DATA =
            ATTACHMENT_TYPES.register("quest_data", () -> AttachmentType.builder(() -> QuestData.EMPTY)
                    .serialize(QuestData.CODEC)
                    .copyOnDeath()
                    .build());

    /** Honra acumulada (define a patente na hierarquia da raça). */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> HONOR =
            ATTACHMENT_TYPES.register("honor", () -> AttachmentType.builder(() -> 0)
                    .serialize(Codec.INT)
                    .copyOnDeath()
                    .build());
}
