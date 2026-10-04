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
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** Chefe: Autômato Gnômico. Dispara descargas elétricas no alvo e libera uma sobrecarga em área. */
public class GnomeAutomaton extends IronGolem implements IRaceBoss {
    private final BossCore core = new BossCore("entity.racasrpg.gnome_automaton", BossEvent.BossBarColor.WHITE,
            "music_disc.cat", 3500);
    private int zapCooldown = 100;
    private int overloadCooldown = 200;

    public GnomeAutomaton(EntityType<? extends IronGolem> type, Level level) {
        super(type, level);
        this.xpReward = 150;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return IronGolem.createAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.ATTACK_DAMAGE, 12.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.SCALE, 1.3);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
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
            BossSkills.announce(this, "O Autômato Gnômico entrou em fúria!");
        }

        if (target == null) {
            return;
        }

        if (--this.zapCooldown <= 0) {
            if (this.distanceTo(target) < 24.0) {
                this.zapCooldown = this.core.enraged() ? 70 : 120;
                BossSkills.lightningAt(this, target, 9.0F);
            } else {
                this.zapCooldown = 20;
            }
        }
        if (--this.overloadCooldown <= 0) {
            if (this.distanceTo(target) < 8.0) {
                this.overloadCooldown = this.core.enraged() ? 130 : 210;
                BossSkills.shockwave(this, 6.0, 9.0F, 1.4);
            } else {
                this.overloadCooldown = 20;
            }
        }

    }
}
