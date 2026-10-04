package com.racasrpg.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** Chefe: Anjo Caído. Voa e chama raios sobre o alvo, e lança uma maldição em área. */
public class FallenAngel extends Vex implements IRaceBoss {
    private final BossCore core = new BossCore("entity.racasrpg.fallen_angel", BossEvent.BossBarColor.YELLOW,
            "music_disc.precipice", 3500);
    private int boltCooldown = 80;
    private int curseCooldown = 220;

    public FallenAngel(EntityType<? extends Vex> type, Level level) {
        super(type, level);
        this.xpReward = 160;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Vex.createAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.SCALE, 3.2);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.core.seen(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.core.unseen(player);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.core.stopMusic(this);
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        this.core.stopMusic(this);
        super.remove(reason);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        LivingEntity target = this.core.tick(this);

        if (this.core.enrageNow(this)) {
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, MobEffectInstance.INFINITE_DURATION, 1));
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, MobEffectInstance.INFINITE_DURATION, 0));
            BossSkills.roar(this, 12.0, 6);
            BossSkills.announce(this, "O Anjo Caído entrou em fúria!");
        }

        if (target == null) {
            return;
        }

        if (--this.boltCooldown <= 0) {
            if (this.distanceTo(target) < 32.0) {
                this.boltCooldown = this.core.enraged() ? 60 : 100;
                BossSkills.lightningAt(this, target, 10.0F);
            } else {
                this.boltCooldown = 20;
            }
        }
        if (--this.curseCooldown <= 0) {
            if (this.distanceTo(target) < 10.0) {
                this.curseCooldown = this.core.enraged() ? 160 : 260;
                BossSkills.curse(this, 8.0, 5.0F, 5);
            } else {
                this.curseCooldown = 20;
            }
        }

    }
}
