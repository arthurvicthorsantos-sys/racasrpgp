package com.racasrpg.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;

/** Arqueiro Sombrio: atira de longe, mais vida que um esqueleto comum. Não queima no sol. */
public class DarkElfArcher extends Skeleton {
    public DarkElfArcher(EntityType<? extends Skeleton> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractSkeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, 26.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.SCALE, 1.05);
    }

    @Override
    public boolean isSunBurnTick() {
        return false;
    }
}
