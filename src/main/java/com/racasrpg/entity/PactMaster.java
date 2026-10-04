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
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** Chefe: Mestre do Pacto. Além dos feitiços de presas e vexes, solta anéis de fogo e lança maldições. */
public class PactMaster extends Evoker implements IRaceBoss {
    private final BossCore core = new BossCore("entity.racasrpg.pact_master", BossEvent.BossBarColor.RED,
            "music_disc.stal", 3000);
    private int ringCooldown = 140;
    private int curseCooldown = 240;

    public PactMaster(EntityType<? extends Evoker> type, Level level) {
        super(type, level);
        this.xpReward = 160;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Evoker.createAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.SCALE, 1.8);
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
            BossSkills.announce(this, "O Mestre do Pacto entrou em fúria!");
        }

        if (target == null) {
            return;
        }

        if (--this.ringCooldown <= 0) {
            if (this.distanceTo(target) < 12.0) {
                this.ringCooldown = this.core.enraged() ? 110 : 170;
                BossSkills.fireRing(this, this.core.enraged() ? 11.0 : 9.0, 7.0F, 5);
            } else {
                this.ringCooldown = 20;
            }
        }
        if (--this.curseCooldown <= 0) {
            if (this.distanceTo(target) < 12.0) {
                this.curseCooldown = this.core.enraged() ? 180 : 280;
                BossSkills.curse(this, 8.0, 6.0F, 6);
            } else {
                this.curseCooldown = 20;
            }
        }

    }
}
