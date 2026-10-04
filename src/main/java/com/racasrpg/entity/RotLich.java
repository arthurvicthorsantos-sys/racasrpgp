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
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** Chefe: Rei Lich. Teletransporta-se para as suas costas, lança maldições e levanta esqueletos. */
public class RotLich extends Skeleton implements IRaceBoss {
    private final BossCore core = new BossCore("entity.racasrpg.rot_lich", BossEvent.BossBarColor.BLUE,
            "music_disc.mellohi", 3900);
    private int teleportCooldown = 160;
    private int curseCooldown = 200;
    private int summonCooldown = 300;

    public RotLich(EntityType<? extends Skeleton> type, Level level) {
        super(type, level);
        this.xpReward = 150;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractSkeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 9.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.SCALE, 1.7);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean isSunBurnTick() {
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
            BossSkills.announce(this, "O Rei Lich entrou em fúria!");
        }

        if (target == null) {
            return;
        }

        if (--this.teleportCooldown <= 0) {
            if (this.distanceTo(target) > 4.0) {
                this.teleportCooldown = this.core.enraged() ? 120 : 200;
                BossSkills.teleportBehind(this, target);
            } else {
                this.teleportCooldown = 20;
            }
        }
        if (--this.curseCooldown <= 0) {
            if (this.distanceTo(target) < 10.0) {
                this.curseCooldown = this.core.enraged() ? 150 : 240;
                BossSkills.curse(this, 8.0, 6.0F, 6);
            } else {
                this.curseCooldown = 20;
            }
        }
        if (--this.summonCooldown <= 0) {
            this.summonCooldown = this.core.enraged() ? 260 : 400;
            BossSkills.summon(this, EntityType.SKELETON, Skeleton.class, 2, 6);
        }

    }
}
