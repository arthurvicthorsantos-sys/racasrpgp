package com.racasrpg.race;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Dados de raca de um jogador: id da raca ("" = sem raca), estagio atual e progresso da missao do estagio.
 */
public record RaceData(String race, int stage, int progress) {
    public static final RaceData EMPTY = new RaceData("", 0, 0);

    public static final Codec<RaceData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("race", "").forGetter(RaceData::race),
            Codec.INT.optionalFieldOf("stage", 0).forGetter(RaceData::stage),
            Codec.INT.optionalFieldOf("progress", 0).forGetter(RaceData::progress)
    ).apply(instance, RaceData::new));

    public boolean hasRace() {
        return !race.isEmpty();
    }
}
