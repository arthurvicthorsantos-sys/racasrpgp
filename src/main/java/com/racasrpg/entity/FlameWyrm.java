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
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Chefe: Dragão Flamejante. Voa, lança bolas de fogo e solta um anel de fogo ao redor. */
public class FlameWyrm extends Blaze implements IRaceBoss {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.racasrpg.flame_wyrm"),
            BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.PROGRESS);
    private final BossMusic music = new BossMusic("music_disc.5", 3500);

    private int ringCooldown = 140;
    private boolean enraged = false;

    public FlameWyrm(EntityType<? extends Blaze> type, Level level) {
        super(type, level);
        this.xpReward = 160;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Blaze.createAttributes()
                .add(Attributes.MAX_HEALTH, 240.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.SCALE, 2.4);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
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
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, MobEffectInstance.INFINITE_DURATION, 0));
            BossSkills.fireRing(this, 12.0, 8.0F, 6);
            BossSkills.announce(this, "O Dragão Flamejante arde em fúria!");
        }

        if (target != null && --this.ringCooldown <= 0) {
            if (this.distanceTo(target) < 12.0) {
                this.ringCooldown = this.enraged ? 100 : 160;
                BossSkills.fireRing(this, 9.0, 7.0F, 5);
            } else {
                this.ringCooldown = 20;
            }
        }
    }
}
