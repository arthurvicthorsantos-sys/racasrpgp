package com.racasrpg.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Chefe: Rei Goblin. Habilidades: chama saqueadores, salta sobre o alvo causando onda de choque
 * e entra em fúria com metade da vida.
 */
public class GoblinKing extends Zombie implements IRaceBoss {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.racasrpg.goblin_king"),
            BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.PROGRESS);
    private final BossMusic music = new BossMusic("music_disc.pigstep", 2900);

    private int summonCooldown = 200;
    private int leapCooldown = 120;
    private int landingGrace = 0;
    private boolean leaping = false;
    private boolean enraged = false;

    public GoblinKing(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
        this.xpReward = 120;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 260.0)
                .add(Attributes.MOVEMENT_SPEED, 0.27)
                .add(Attributes.ATTACK_DAMAGE, 9.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.SCALE, 1.7)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0);
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public boolean convertsInWater() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
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
            BossSkills.roar(this, 12.0, 6);
            BossSkills.announce(this, "O Rei Goblin entrou em fúria!");
        }

        if (target == null) {
            return;
        }

        // pulo com onda de choque ao cair
        if (this.leaping) {
            if (--this.landingGrace <= 0 && this.onGround()) {
                this.leaping = false;
                BossSkills.shockwave(this, 5.0, 8.0F, 1.0);
            }
        } else if (--this.leapCooldown <= 0) {
            double distance = this.distanceTo(target);
            if (distance > 4.0 && distance < 16.0 && this.onGround()) {
                this.leapCooldown = this.enraged ? 100 : 160;
                this.leaping = true;
                this.landingGrace = 8;
                BossSkills.leap(this, target, 1.1);
            } else {
                this.leapCooldown = 20;
            }
        }

        // chama saqueadores
        if (--this.summonCooldown <= 0) {
            this.summonCooldown = this.enraged ? 220 : 320;
            if (this.level() instanceof ServerLevel serverLevel) {
                int nearby = serverLevel.getEntitiesOfClass(GoblinRaider.class, this.getBoundingBox().inflate(24.0)).size();
                for (int i = 0; i < 2 && nearby + i < 6; i++) {
                    ModEntities.GOBLIN_RAIDER.get().spawn(serverLevel,
                            this.blockPosition().offset(i * 2 - 1, 0, 1), MobSpawnType.MOB_SUMMONED);
                }
            }
        }
    }
}
