package com.racasrpg.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Chefe: Campeão Sombrio. Habilidades: teletransporte para as costas do alvo, chuva sombria
 * (dano e definhar), golpes que atrasam e fúria com metade da vida.
 */
public class ShadowChampion extends WitherSkeleton implements IRaceBoss {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.racasrpg.shadow_champion"),
            BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
    private final BossMusic music = new BossMusic("music_disc.otherside", 3800);

    private int teleportCooldown = 160;
    private int curseCooldown = 200;
    private boolean enraged = false;

    public ShadowChampion(EntityType<? extends WitherSkeleton> type, Level level) {
        super(type, level);
        this.xpReward = 150;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractSkeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.SCALE, 1.5);
    }

    @Override
    public boolean isSunBurnTick() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
        }
        return hit;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (this.level() instanceof ServerLevel serverLevel) {
            this.music.stop(serverLevel);
        }
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        if (this.level() instanceof ServerLevel serverLevel) {
            this.music.stop(serverLevel);
        }
        super.remove(reason);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        LivingEntity target = this.getTarget();
        this.music.tick(this, target instanceof Player);

        if (!this.enraged && this.getHealth() < this.getMaxHealth() * 0.5F) {
            this.enraged = true;
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, MobEffectInstance.INFINITE_DURATION, 1));
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, MobEffectInstance.INFINITE_DURATION, 0));
            BossSkills.curse(this, 10.0, 4.0F, 5);
            BossSkills.announce(this, "O Campeão Sombrio libera sua fúria!");
        }

        if (target == null) {
            return;
        }

        if (--this.teleportCooldown <= 0) {
            if (this.distanceTo(target) > 4.0) {
                this.teleportCooldown = this.enraged ? 120 : 200;
                BossSkills.teleportBehind(this, target);
            } else {
                this.teleportCooldown = 20;
            }
        }

        if (--this.curseCooldown <= 0) {
            if (this.distanceTo(target) < 9.0) {
                this.curseCooldown = this.enraged ? 160 : 240;
                BossSkills.curse(this, 7.0, 6.0F, 6);
            } else {
                this.curseCooldown = 20;
            }
        }
    }
}
