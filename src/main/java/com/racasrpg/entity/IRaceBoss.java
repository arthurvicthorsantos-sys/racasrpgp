package com.racasrpg.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;

/** Marca os chefes da mod. Derrotar um deles conta para as missões de chefe. */
public interface IRaceBoss {
    /** Id do chefe (o caminho do tipo de entidade, ex.: "goblin_king"). */
    default String bossId() {
        return BuiltInRegistries.ENTITY_TYPE.getKey(((Entity) this).getType()).getPath();
    }
}
