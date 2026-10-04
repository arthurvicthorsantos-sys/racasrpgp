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
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** Chefe: Colosso Arqueiro. Atira leques de flechas de longe e salta para trás quando alguém se aproxima. */
public class ArcherColossus extends Pillager implements IRaceBoss {
    private final BossCore core = new BossCore("entity.racasrpg.archer_colossus", BossEvent.BossBarColor.GREEN,
            "music_disc.chirp", 3000);
    private int volleyCooldown = 80;
    private int retreatCooldown = 100;

    public ArcherColossus(EntityType<? extends Pillager> type, Level level) {
        super(type, level);
        this.xpReward = 150;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Pillager.createAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.SCALE, 1.7);
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
            BossSkills.announce(this, "O Colosso Arqueiro entrou em fúria!");
        }

        if (target == null) {
            return;
        }

        double distance = this.distanceTo(target);
        if (--this.volleyCooldown <= 0) {
            if (distance > 5.0 && distance < 32.0) {
                this.volleyCooldown = this.core.enraged() ? 60 : 100;
                BossSkills.volley(this, target, this.core.enraged() ? 9 : 5, 6.0, 6.0);
            } else {
                this.volleyCooldown = 20;
            }
        }
        if (--this.retreatCooldown <= 0) {
            if (distance < 5.0 && this.onGround()) {
                this.retreatCooldown = 120;
                BossSkills.leapAway(this, target, 1.4);
            } else {
                this.retreatCooldown = 20;
            }
        }

    }
}
