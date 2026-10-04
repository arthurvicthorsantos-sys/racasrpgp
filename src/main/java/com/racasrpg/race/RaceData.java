package com.racasrpg.race;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Dados de raça e classe de um jogador: id da raça ("" = sem raça), estágio, progresso da missão
 * do estágio e id da classe ("" = sem classe).
 */
public record RaceData(String race, int stage, int progress, String clazz) {
    public static final RaceData EMPTY = new RaceData("", 0, 0, "");

    public static final Codec<RaceData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("race", "").forGetter(RaceData::race),
            Codec.INT.optionalFieldOf("stage", 0).forGetter(RaceData::stage),
            Codec.INT.optionalFieldOf("progress", 0).forGetter(RaceData::progress),
            Codec.STRING.optionalFieldOf("class", "").forGetter(RaceData::clazz)
    ).apply(instance, RaceData::new));

    public boolean hasRace() {
        return !race.isEmpty();
    }

    public boolean hasClass() {
        return com.racasrpg.classes.ClassDef.byId(clazz) != null;
    }
}
