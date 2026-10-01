package com.racasrpg.entity;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** Brutamontes Orc: grande, resistente e de golpes pesados. Não queima no sol. */
public class OrcBrute extends Zombie {
    public OrcBrute(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4)
                .add(Attributes.SCALE, 1.2)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0);
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public boolean convertsInWater() {
        return false;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
    }
}
