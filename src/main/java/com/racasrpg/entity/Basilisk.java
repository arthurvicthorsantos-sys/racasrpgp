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
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** Chefe: Basilisco. Salta sobre o alvo, espalha uma praga venenosa e envenena com a mordida. */
public class Basilisk extends Spider implements IRaceBoss {
    private final BossCore core = new BossCore("entity.racasrpg.basilisk", BossEvent.BossBarColor.GREEN,
            "music_disc.far", 3400);
    private int leapCooldown = 120;
    private int landingGrace = 0;
    private boolean leaping = false;
    private int blightCooldown = 170;

    public Basilisk(EntityType<? extends Spider> type, Level level) {
        super(type, level);
        this.xpReward = 150;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Spider.createAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0)
                .add(Attributes.ATTACK_DAMAGE, 9.0)
                .add(Attributes.ARMOR, 5.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.SCALE, 2.6);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 1));
        }
        return hit;
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
            BossSkills.announce(this, "O Basilisco entrou em fúria!");
        }

        if (target == null) {
            return;
        }

        // pulo com onda de choque ao cair
        if (this.leaping) {
            if (--this.landingGrace <= 0 && this.onGround()) {
                this.leaping = false;
                BossSkills.shockwave(this, 4.0, 8.0F, 1.1);
            }
        } else if (--this.leapCooldown <= 0) {
            double distance = this.distanceTo(target);
            if (distance > 4.0 && distance < 16.0 && this.onGround()) {
                this.leapCooldown = this.core.enraged() ? 90 : 150;
                this.leaping = true;
                this.landingGrace = 8;
                BossSkills.leap(this, target, 1.2);
            } else {
                this.leapCooldown = 20;
            }
        }

        if (--this.blightCooldown <= 0) {
            if (this.distanceTo(target) < 10.0) {
                this.blightCooldown = this.core.enraged() ? 130 : 200;
                BossSkills.blight(this, 8.0, 8);
            } else {
                this.blightCooldown = 20;
            }
        }

    }
}
