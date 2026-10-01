package com.racasrpg.race;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** Progresso das missões dos NPCs: índice da missão atual e quantos alvos já foram abatidos. */
public record QuestData(int index, int progress) {
    public static final QuestData EMPTY = new QuestData(0, 0);

    public static final Codec<QuestData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("index", 0).forGetter(QuestData::index),
            Codec.INT.optionalFieldOf("progress", 0).forGetter(QuestData::progress)
    ).apply(instance, QuestData::new));
}
