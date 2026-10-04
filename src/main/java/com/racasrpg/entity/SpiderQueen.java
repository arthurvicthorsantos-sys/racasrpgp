package com.racasrpg.entity;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/** Chefe: Rainha Aranha. Prende o alvo em teias, chama aranhas e envenena com a mordida. */
public class SpiderQueen extends Spider implements IRaceBoss {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.racasrpg.spider_queen"),
            BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.PROGRESS);
    private final BossMusic music = new BossMusic("music_disc.ward", 5000);

    private int webCooldown = 160;
    private int summonCooldown = 260;
    private boolean enraged = false;

    public SpiderQueen(EntityType<? extends Spider> type, Level level) {
        super(type, level);
        this.xpReward = 140;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Spider.createAttributes()
                .add(Attributes.MAX_HEALTH, 240.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.SCALE, 2.2);
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
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, MobEffectInstance.INFINITE_DURATION, 1));
            BossSkills.roar(this, 12.0, 6);
            BossSkills.announce(this, "A Rainha Aranha enlouquece!");
        }

        if (target == null || !(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        // teias em volta do alvo
        if (--this.webCooldown <= 0) {
            this.webCooldown = this.enraged ? 110 : 170;
            BlockPos center = target.blockPosition();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = center.offset(dx, 0, dz);
                    if (serverLevel.getBlockState(pos).isAir()) {
                        serverLevel.setBlock(pos, Blocks.COBWEB.defaultBlockState(), 3);
                    }
                }
            }
        }

        // aranhas ajudantes
        if (--this.summonCooldown <= 0) {
            this.summonCooldown = this.enraged ? 200 : 300;
            int nearby = serverLevel.getEntitiesOfClass(net.minecraft.world.entity.monster.CaveSpider.class,
                    this.getBoundingBox().inflate(24.0)).size();
            for (int i = 0; i < 3 && nearby + i < 8; i++) {
                EntityType.CAVE_SPIDER.spawn(serverLevel, this.blockPosition().offset(i - 1, 0, 2),
                        MobSpawnType.MOB_SUMMONED);
            }
        }
    }
}
