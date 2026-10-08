package com.racasrpg.ability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.racasrpg.RacasRpg;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Bolas de fogo: projéteis feitos só de partículas (não precisam de modelo nem de entidade nova).
 * Voam em linha reta, deixam rastro de fogo e fumaça e explodem ao atingir um monstro ou uma parede,
 * sem destruir blocos. Se o dono é um jogador, ferem monstros; se é um monstro, ferem jogadores.
 */
@EventBusSubscriber(modid = RacasRpg.MODID)
public final class ProjectileEngine {
    private ProjectileEngine() {
    }

    private static final class Shot {
        ResourceKey<Level> dimension;
        UUID owner;
        boolean ownerIsPlayer;
        Vec3 pos;
        Vec3 velocity;
        int life;
        int age;
        float damage;
        double radius;
        int fireSeconds;
    }

    private static final List<Shot> SHOTS = new ArrayList<>();

    /** Lança uma bola de fogo. */
    public static void fire(ServerLevel level, LivingEntity owner, Vec3 start, Vec3 velocity, int life,
                            float damage, double radius, int fireSeconds) {
        Shot shot = new Shot();
        shot.dimension = level.dimension();
        shot.owner = owner.getUUID();
        shot.ownerIsPlayer = owner instanceof Player;
        shot.pos = start;
        shot.velocity = velocity;
        shot.life = life;
        shot.damage = damage;
        shot.radius = radius;
        shot.fireSeconds = fireSeconds;
        synchronized (SHOTS) {
            SHOTS.add(shot);
        }
        level.sendParticles(ParticleTypes.FLAME, start.x, start.y, start.z, 25, 0.3, 0.3, 0.3, 0.06);
        level.sendParticles(ParticleTypes.LAVA, start.x, start.y, start.z, 6, 0.3, 0.3, 0.3, 0.0);
        level.playSound(null, BlockPos.containing(start), SoundEvents.GHAST_SHOOT, SoundSource.PLAYERS, 1.4F, 0.8F);
    }

    private static boolean isTarget(LivingEntity e, Shot shot) {
        if (!e.isAlive() || e.getUUID().equals(shot.owner)) {
            return false;
        }
        if (shot.ownerIsPlayer) {
            return e instanceof Enemy;
        }
        return e instanceof Player player && !player.isCreative() && !player.isSpectator();
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        synchronized (SHOTS) {
            if (SHOTS.isEmpty()) {
                return;
            }
            MinecraftServer server = event.getServer();
            Iterator<Shot> iterator = SHOTS.iterator();
            while (iterator.hasNext()) {
                Shot shot = iterator.next();
                ServerLevel level = server.getLevel(shot.dimension);
                if (level == null) {
                    iterator.remove();
                    continue;
                }
                if (step(level, shot)) {
                    iterator.remove();
                }
            }
        }
    }

    /** Move a bola de fogo e desenha o rastro. Devolve true quando ela explodiu. */
    private static boolean step(ServerLevel level, Shot shot) {
        boolean explode = false;
        Vec3 hitPos = shot.pos;

        for (int i = 0; i < 2 && !explode; i++) {
            Vec3 next = shot.pos.add(shot.velocity.scale(0.5));
            BlockPos blockPos = BlockPos.containing(next);
            BlockState state = level.getBlockState(blockPos);
            if (!state.getCollisionShape(level, blockPos).isEmpty()) {
                explode = true;
                hitPos = shot.pos;
                break;
            }
            List<LivingEntity> near = level.getEntitiesOfClass(LivingEntity.class, new AABB(next, next).inflate(0.8),
                    e -> isTarget(e, shot));
            if (!near.isEmpty()) {
                explode = true;
                hitPos = next;
                break;
            }
            shot.pos = next;
        }

        shot.age++;
        double x = shot.pos.x;
        double y = shot.pos.y;
        double z = shot.pos.z;
        level.sendParticles(ParticleTypes.FLAME, x, y, z, 14, 0.25, 0.25, 0.25, 0.02);
        level.sendParticles(ParticleTypes.LAVA, x, y, z, 1, 0.15, 0.15, 0.15, 0.0);
        Vec3 back = shot.pos.subtract(shot.velocity.scale(0.8));
        level.sendParticles(ParticleTypes.LARGE_SMOKE, back.x, back.y, back.z, 3, 0.12, 0.12, 0.12, 0.01);
        level.sendParticles(ParticleTypes.SMALL_FLAME, back.x, back.y, back.z, 6, 0.2, 0.2, 0.2, 0.01);
        if (shot.age % 5 == 0) {
            level.playSound(null, BlockPos.containing(shot.pos), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS,
                    0.6F, 1.2F);
        }

        if (--shot.life <= 0) {
            explode = true;
            hitPos = shot.pos;
        }
        if (explode) {
            explode(level, shot, hitPos);
        }
        return explode;
    }

    private static void explode(ServerLevel level, Shot shot, Vec3 at) {
        double r = shot.radius;
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, at.x, at.y, at.z, 1, 0.0, 0.0, 0.0, 0.0);
        level.sendParticles(ParticleTypes.FLAME, at.x, at.y, at.z, 160, r * 0.35, r * 0.25, r * 0.35, 0.12);
        level.sendParticles(ParticleTypes.LAVA, at.x, at.y, at.z, 40, r * 0.3, r * 0.2, r * 0.3, 0.0);
        level.sendParticles(ParticleTypes.LARGE_SMOKE, at.x, at.y, at.z, 40, r * 0.3, r * 0.25, r * 0.3, 0.05);
        Fx.ring(level, ParticleTypes.FLAME, at.x, at.y + 0.3, at.z, 48, r * 0.11);
        Fx.ring(level, ParticleTypes.SMOKE, at.x, at.y + 0.3, at.z, 36, r * 0.07);
        level.playSound(null, BlockPos.containing(at), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.PLAYERS, 1.6F, 0.9F);

        LivingEntity owner = null;
        Player ownerPlayer = level.getPlayerByUUID(shot.owner);
        if (ownerPlayer != null) {
            owner = ownerPlayer;
        }
        AABB box = new AABB(at, at).inflate(r);
        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, box, e -> isTarget(e, shot));
        for (LivingEntity victim : victims) {
            DamageSource source = owner instanceof Player p ? p.damageSources().playerAttack(p)
                    : level.damageSources().magic();
            victim.hurt(source, shot.damage);
            victim.setRemainingFireTicks(shot.fireSeconds * 20);
            victim.knockback(0.8, at.x - victim.getX(), at.z - victim.getZ());
        }
    }
}
