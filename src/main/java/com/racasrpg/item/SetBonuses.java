package com.racasrpg.item;

import com.racasrpg.race.Race;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/** Bônus por usar as 4 peças da armadura de uma mesma raça. */
public final class SetBonuses {
    private SetBonuses() {
    }

    private static Race fullSet(ServerPlayer player) {
        Race race = null;
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!(stack.getItem() instanceof RaceArmorItem armor)) {
                return null;
            }
            if (race == null) {
                race = armor.race();
            } else if (race != armor.race()) {
                return null;
            }
        }
        return race;
    }

    private static void give(ServerPlayer player, Holder<MobEffect> effect, int amplifier) {
        player.addEffect(new MobEffectInstance(effect, 100, amplifier, true, false, true));
    }

    /** Chamar a cada 40 ticks (2 segundos). */
    public static void tick(ServerPlayer player) {
        Race race = fullSet(player);
        if (race == null) {
            return;
        }
        switch (race) {
            case HUMANO -> give(player, MobEffects.DAMAGE_RESISTANCE, 0);
            case ELFO -> {
                give(player, MobEffects.MOVEMENT_SPEED, 0);
                give(player, MobEffects.JUMP, 0);
            }
            case ANAO -> give(player, MobEffects.DIG_SPEED, 0);
            case ORC -> give(player, MobEffects.DAMAGE_BOOST, 0);
            case GOBLIN -> {
                give(player, MobEffects.MOVEMENT_SPEED, 1);
                give(player, MobEffects.LUCK, 0);
            }
            case DRACONATO -> give(player, MobEffects.FIRE_RESISTANCE, 0);
            case HALFLING -> give(player, MobEffects.LUCK, 1);
            case MINOTAURO -> {
                give(player, MobEffects.DAMAGE_RESISTANCE, 0);
                give(player, MobEffects.DAMAGE_BOOST, 0);
            }
            case TRITAO -> {
                give(player, MobEffects.WATER_BREATHING, 0);
                give(player, MobEffects.DOLPHINS_GRACE, 0);
            }
            case GNOMO -> give(player, MobEffects.DIG_SPEED, 0);
            case DROW -> give(player, MobEffects.NIGHT_VISION, 0);
            case CENTAURO -> give(player, MobEffects.MOVEMENT_SPEED, 0);
            case REPTILIANO -> give(player, MobEffects.DAMAGE_RESISTANCE, 0);
            case ANJO -> {
                give(player, MobEffects.REGENERATION, 0);
                give(player, MobEffects.SLOW_FALLING, 0);
            }
            case INFERNAL -> give(player, MobEffects.FIRE_RESISTANCE, 0);
        }
    }
}
