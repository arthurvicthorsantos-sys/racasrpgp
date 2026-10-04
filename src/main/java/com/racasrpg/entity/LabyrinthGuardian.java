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
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** Chefe: Guardião do Labirinto. Investe contra o alvo com onda de choque e ruge para atrasar quem estiver por perto. */
public class LabyrinthGuardian extends Ravager implements IRaceBoss {
    private final BossCore core = new BossCore("entity.racasrpg.labyrinth_guardian", BossEvent.BossBarColor.RED,
            "music_disc.wait", 3000);
    private int leapCooldown = 120;
    private int landingGrace = 0;
    private boolean leaping = false;
    private int roarCooldown = 220;

    public LabyrinthGuardian(EntityType<? extends Ravager> type, Level level) {
        super(type, level);
        this.xpReward = 160;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Ravager.createAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0)
                .add(Attributes.ATTACK_DAMAGE, 14.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.SCALE, 1.5);
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
            BossSkills.announce(this, "O Guardião do Labirinto entrou em fúria!");
        }

        if (target == null) {
            return;
        }

        // pulo com onda de choque ao cair
        if (this.leaping) {
            if (--this.landingGrace <= 0 && this.onGround()) {
                this.leaping = false;
                BossSkills.shockwave(this, 5.0, 10.0F, 1.1);
            }
        } else if (--this.leapCooldown <= 0) {
            double distance = this.distanceTo(target);
            if (distance > 4.0 && distance < 16.0 && this.onGround()) {
                this.leapCooldown = this.core.enraged() ? 90 : 140;
                this.leaping = true;
                this.landingGrace = 8;
                BossSkills.leap(this, target, 1.4);
            } else {
                this.leapCooldown = 20;
            }
        }

        if (--this.roarCooldown <= 0) {
            this.roarCooldown = this.core.enraged() ? 200 : 300;
            BossSkills.roar(this, 10.0, 6);
        }

    }
}
