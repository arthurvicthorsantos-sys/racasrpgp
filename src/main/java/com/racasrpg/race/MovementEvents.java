package com.racasrpg.race;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.racasrpg.RacasRpg;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingJumpEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Movimentação única de cada raça. Tudo roda no servidor. */
@EventBusSubscriber(modid = RacasRpg.MODID)
public class MovementEvents {
    private static final Map<UUID, Integer> SPRINT_TICKS = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> PREV_SNEAK = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> PREV_SPRINT = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> COOLDOWN = new ConcurrentHashMap<>();

    private static boolean ready(UUID id, long now, int cooldownTicks) {
        long until = COOLDOWN.getOrDefault(id, 0L);
        if (now < until) {
            return false;
        }
        COOLDOWN.put(id, now + cooldownTicks);
        return true;
    }

    private static Vec3 flatLook(ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        Vec3 flat = new Vec3(look.x, 0, look.z);
        return flat.lengthSqr() < 1.0E-4 ? Vec3.ZERO : flat.normalize();
    }

    private static void effect(ServerPlayer player, net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect,
                               int ticks, int amplifier) {
        player.addEffect(new MobEffectInstance(effect, ticks, amplifier, true, false, false));
    }

    private static boolean stoneAhead(ServerPlayer player) {
        BlockPos feet = player.blockPosition();
        Direction dir = player.getDirection();
        for (BlockPos pos : new BlockPos[]{feet.relative(dir), feet.above().relative(dir)}) {
            BlockState state = player.level().getBlockState(pos);
            if (state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(Tags.Blocks.ORES)
                    || state.is(BlockTags.STONE_BRICKS) || state.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        UUID id = player.getUUID();
        boolean sprinting = player.isSprinting();
        boolean sneaking = player.isShiftKeyDown();
        int sprintTicks = 0;
        if (sprinting) {
            sprintTicks = SPRINT_TICKS.merge(id, 1, Integer::sum);
        } else {
            SPRINT_TICKS.remove(id);
        }
        boolean prevSneak = PREV_SNEAK.getOrDefault(id, false);
        boolean prevSprint = PREV_SPRINT.getOrDefault(id, false);
        PREV_SNEAK.put(id, sneaking);
        PREV_SPRINT.put(id, sprinting);

        Race race = Race.byId(RaceManager.get(player).race());
        if (race == null) {
            return;
        }
        long now = player.level().getGameTime();
        Vec3 motion = player.getDeltaMovement();

        switch (race) {
            case HUMANO -> {
                // corredor de longa distância: correr gasta menos fome
                if (sprinting && player.getFoodData().getExhaustionLevel() > 0.05F) {
                    player.getFoodData().addExhaustion(-0.015F);
                }
            }
            case ELFO -> {
                // planar: Shift no ar para deslizar
                if (!player.onGround() && !player.isInWater() && sneaking && motion.y < -0.08
                        && !player.getAbilities().flying) {
                    Vec3 look = player.getLookAngle();
                    player.setDeltaMovement(motion.x + look.x * 0.02, Math.max(motion.y, -0.06), motion.z + look.z * 0.02);
                    player.hurtMarked = true;
                    player.fallDistance = 0.0F;
                }
            }
            case ANAO -> {
                // escalador de pedra: Shift contra parede de pedra
                if (player.horizontalCollision && sneaking && stoneAhead(player)) {
                    player.setDeltaMovement(motion.x, 0.15, motion.z);
                    player.hurtMarked = true;
                    player.fallDistance = 0.0F;
                }
            }
            case ORC -> {
                // ímpeto: depois de correr um tempo, ganha força
                if (sprintTicks >= 40 && player.tickCount % 20 == 0) {
                    effect(player, MobEffects.DAMAGE_BOOST, 60, 0);
                }
            }
            case GOBLIN -> {
                // deslize: Shift enquanto corre
                if (sneaking && !prevSneak && prevSprint && player.onGround() && ready(id, now, 30)) {
                    Vec3 flat = flatLook(player);
                    player.setDeltaMovement(flat.x * 0.95, 0.05, flat.z * 0.95);
                    player.hurtMarked = true;
                    player.serverLevel().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP,
                            SoundSource.PLAYERS, 0.6F, 1.5F);
                }
            }
            case MINOTAURO -> {
                // investida de touro: atropela monstros no caminho
                if (sprintTicks >= 30 && ready(id, now, 12)) {
                    Vec3 flat = flatLook(player);
                    AABB box = player.getBoundingBox().expandTowards(flat.x * 1.3, 0, flat.z * 1.3).inflate(0.2);
                    List<LivingEntity> hit = player.serverLevel().getEntitiesOfClass(LivingEntity.class, box,
                            e -> e != player && e.isAlive() && e instanceof Enemy);
                    for (LivingEntity e : hit) {
                        e.hurt(player.damageSources().playerAttack(player), 6.0F);
                        e.knockback(1.2, player.getX() - e.getX(), player.getZ() - e.getZ());
                    }
                    if (!hit.isEmpty()) {
                        player.serverLevel().playSound(null, player.blockPosition(), SoundEvents.RAVAGER_ATTACK,
                                SoundSource.PLAYERS, 1.0F, 0.9F);
                    }
                }
            }
            case TRITAO -> {
                // jato d'água: correr na água
                if (player.isInWater() && sprinting && motion.lengthSqr() < 1.0) {
                    player.setDeltaMovement(motion.add(player.getLookAngle().scale(0.06)));
                    player.hurtMarked = true;
                }
            }
            case DROW -> {
                // passo sombrio: no escuro fica rápido, enxerga e, agachado, some
                if (player.tickCount % 10 == 0 && player.level().getMaxLocalRawBrightness(player.blockPosition()) <= 6) {
                    effect(player, MobEffects.MOVEMENT_SPEED, 40, 1);
                    effect(player, MobEffects.NIGHT_VISION, 260, 0);
                    if (sneaking) {
                        effect(player, MobEffects.INVISIBILITY, 30, 0);
                    }
                }
            }
            case CENTAURO -> {
                // galope: quanto mais corre, mais rápido
                if (sprintTicks >= 20 && player.tickCount % 5 == 0) {
                    int amplifier = sprintTicks >= 120 ? 2 : (sprintTicks >= 60 ? 1 : 0);
                    effect(player, MobEffects.MOVEMENT_SPEED, 20, amplifier);
                }
            }
            case REPTILIANO -> {
                // aderência: sobe qualquer parede ao correr contra ela
                if (player.horizontalCollision && player.zza > 0) {
                    player.setDeltaMovement(motion.x, 0.2, motion.z);
                    player.hurtMarked = true;
                    player.fallDistance = 0.0F;
                }
            }
            case INFERNAL -> {
                // passo de chamas: imune a fogo e veloz em chamas
                if (player.tickCount % 10 == 0) {
                    effect(player, MobEffects.FIRE_RESISTANCE, 60, 0);
                    if (player.isOnFire() || player.isInLava()) {
                        effect(player, MobEffects.MOVEMENT_SPEED, 40, 1);
                    }
                }
            }
            default -> {
            }
        }
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        Race race = Race.byId(RaceManager.get(player).race());
        if (race == null) {
            return;
        }
        double distance = event.getDistance();
        ServerLevel level = player.serverLevel();

        switch (race) {
            case DRACONATO -> {
                // pouso flamejante
                if (distance > 3.0) {
                    event.setDamageMultiplier(0.0F);
                    float damage = (float) Math.min(12.0, distance * 0.8);
                    List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                            player.getBoundingBox().inflate(4.0), e -> e != player && e.isAlive() && e instanceof Enemy);
                    for (LivingEntity e : targets) {
                        e.hurt(player.damageSources().playerAttack(player), damage);
                        e.setRemainingFireTicks(80);
                    }
                    level.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 0.2, player.getZ(),
                            60, 1.5, 0.2, 1.5, 0.05);
                    level.playSound(null, player.blockPosition(), SoundEvents.DRAGON_FIREBALL_EXPLODE,
                            SoundSource.PLAYERS, 1.0F, 0.9F);
                }
            }
            case GNOMO -> {
                // pouso de mola: não machuca e quica
                if (distance > 2.5) {
                    event.setDamageMultiplier(0.0F);
                    Vec3 m = player.getDeltaMovement();
                    player.setDeltaMovement(m.x, Math.min(0.9, 0.35 + distance * 0.06), m.z);
                    player.hurtMarked = true;
                    level.playSound(null, player.blockPosition(), SoundEvents.SLIME_BLOCK_FALL,
                            SoundSource.PLAYERS, 1.0F, 1.2F);
                }
            }
            case ANJO -> event.setDamageMultiplier(0.3F);
            default -> {
            }
        }
    }

    @SubscribeEvent
    public static void onJump(LivingJumpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        Race race = Race.byId(RaceManager.get(player).race());
        if (race == null) {
            return;
        }
        Vec3 m = player.getDeltaMovement();
        switch (race) {
            case HALFLING -> {
                // pulo de lebre: pular correndo dá impulso extra
                if (player.isSprinting()) {
                    Vec3 flat = flatLook(player);
                    player.setDeltaMovement(m.x + flat.x * 0.22, m.y, m.z + flat.z * 0.22);
                    player.hurtMarked = true;
                }
            }
            case ANJO -> {
                // pular agachado dá um salto enorme
                if (player.isShiftKeyDown()) {
                    player.setDeltaMovement(m.x, m.y + 0.3, m.z);
                    player.hurtMarked = true;
                }
            }
            default -> {
            }
        }
    }
}
