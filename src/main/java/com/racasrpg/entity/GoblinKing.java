package com.racasrpg.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** Chefe: Rei Goblin. Grande, resistente, e chama saqueadores goblin para ajudar na luta. */
public class GoblinKing extends Zombie implements IRaceBoss {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.racasrpg.goblin_king"),
            BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.PROGRESS);

    private int summonCooldown = 200;

    public GoblinKing(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
        this.xpReward = 100;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 220.0)
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
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        if (this.getTarget() != null && --this.summonCooldown <= 0) {
            this.summonCooldown = 300;
            if (this.level() instanceof ServerLevel serverLevel) {
                int nearby = serverLevel.getEntitiesOfClass(GoblinRaider.class,
                        this.getBoundingBox().inflate(24.0)).size();
                for (int i = 0; i < 2 && nearby + i < 6; i++) {
                    ModEntities.GOBLIN_RAIDER.get().spawn(serverLevel,
                            this.blockPosition().offset(i * 2 - 1, 0, 1), MobSpawnType.MOB_SUMMONED);
                }
            }
        }
    }
}
