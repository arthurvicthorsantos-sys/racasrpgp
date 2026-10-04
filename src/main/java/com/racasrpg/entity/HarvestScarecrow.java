package com.racasrpg.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** Chefe: Espantalho da Colheita. Salta sobre o alvo com onda de choque e espalha uma praga de veneno e fome. */
public class HarvestScarecrow extends Husk implements IRaceBoss {
    private final BossCore core = new BossCore("entity.racasrpg.harvest_scarecrow", BossEvent.BossBarColor.YELLOW,
            "music_disc.strad", 3400);
    private int leapCooldown = 120;
    private int landingGrace = 0;
    private boolean leaping = false;
    private int blightCooldown = 200;

    public HarvestScarecrow(EntityType<? extends Husk> type, Level level) {
        super(type, level);
        this.xpReward = 150;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.MOVEMENT_SPEED, 0.26)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.SCALE, 1.8)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean convertsInWater() {
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
            BossSkills.announce(this, "O Espantalho entrou em fúria!");
        }

        if (target == null) {
            return;
        }

        // pulo com onda de choque ao cair
        if (this.leaping) {
            if (--this.landingGrace <= 0 && this.onGround()) {
                this.leaping = false;
                BossSkills.shockwave(this, 4.5, 8.0F, 1.1);
            }
        } else if (--this.leapCooldown <= 0) {
            double distance = this.distanceTo(target);
            if (distance > 4.0 && distance < 16.0 && this.onGround()) {
                this.leapCooldown = this.core.enraged() ? 100 : 160;
                this.leaping = true;
                this.landingGrace = 8;
                BossSkills.leap(this, target, 1.1);
            } else {
                this.leapCooldown = 20;
            }
        }

        if (--this.blightCooldown <= 0) {
            if (this.distanceTo(target) < 10.0) {
                this.blightCooldown = this.core.enraged() ? 160 : 240;
                BossSkills.blight(this, 8.0, 8);
            } else {
                this.blightCooldown = 20;
            }
        }

    }
}
