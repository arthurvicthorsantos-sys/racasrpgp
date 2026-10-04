package com.racasrpg.item;

import java.util.function.BiConsumer;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

/** Arma de raça: uma espada com um efeito especial ao acertar. */
public class RaceWeaponItem extends SwordItem {
    private final BiConsumer<LivingEntity, LivingEntity> onHit; // (alvo, atacante)

    public RaceWeaponItem(Tier tier, Properties properties, BiConsumer<LivingEntity, LivingEntity> onHit) {
        super(tier, properties);
        this.onHit = onHit;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        this.onHit.accept(target, attacker);
        return result;
    }
}
