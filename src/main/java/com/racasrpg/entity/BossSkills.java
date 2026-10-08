package com.racasrpg.entity;

import java.util.List;

import com.racasrpg.ability.Fx;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Habilidades que os chefes usam. Todas só funcionam no servidor. */
public final class BossSkills {
    private BossSkills() {
    }

    private static List<Player> playersNear(Mob boss, double radius) {
        return boss.level().getEntitiesOfClass(Player.class, boss.getBoundingBox().inflate(radius),
                p -> p.isAlive() && !p.isSpectator() && !p.isCreative());
    }

    private static void burst(Mob boss, ParticleOptions type, int count, double spread) {
        if (boss.level() instanceof ServerLevel level) {
            level.sendParticles(type, boss.getX(), boss.getY() + 0.3, boss.getZ(), count, spread, 0.2, spread, 0.05);
        }
    }

    private static void ring(Mob boss, ParticleOptions type, int points, double speed) {
        if (boss.level() instanceof ServerLevel level) {
            Fx.ring(level, type, boss.getX(), boss.getY() + 0.3, boss.getZ(), points, speed);
        }
    }

    private static void sound(Mob boss, SoundEvent event, float volume, float pitch) {
        boss.level().playSound(null, boss.blockPosition(), event, SoundSource.HOSTILE, volume, pitch);
    }

    /** Onda de choque: dano e empurrão em jogadores ao redor. */
    public static void shockwave(Mob boss, double radius, float damage, double knockback) {
        for (Player p : playersNear(boss, radius)) {
            p.hurt(boss.damageSources().mobAttack(boss), damage);
            p.knockback(knockback, boss.getX() - p.getX(), boss.getZ() - p.getZ());
            p.hurtMarked = true;
        }
        sound(boss, SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.5F, 0.7F);
        burst(boss, ParticleTypes.EXPLOSION, 8, radius * 0.4);
        burst(boss, ParticleTypes.CLOUD, 40, radius * 0.5);
        ring(boss, ParticleTypes.CLOUD, 44, radius * 0.1);
        ring(boss, ParticleTypes.CRIT, 36, radius * 0.14);
    }

    /** Pulo na direção do alvo. */
    public static void leap(Mob boss, LivingEntity target, double power) {
        Vec3 dir = target.position().subtract(boss.position());
        Vec3 flat = new Vec3(dir.x, 0, dir.z).normalize();
        boss.setDeltaMovement(flat.x * power, 0.65, flat.z * power);
        boss.hasImpulse = true;
        sound(boss, SoundEvents.RAVAGER_ROAR, 1.2F, 1.0F);
    }

    /** Rugido: enfraquece e atrasa jogadores ao redor. */
    public static void roar(Mob boss, double radius, int seconds) {
        for (Player p : playersNear(boss, radius)) {
            p.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, seconds * 20, 0));
            p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, seconds * 20, 0));
        }
        boss.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, seconds * 20, 1));
        sound(boss, SoundEvents.ENDER_DRAGON_GROWL, 1.5F, 0.9F);
        burst(boss, ParticleTypes.ANGRY_VILLAGER, 20, radius * 0.3);
        ring(boss, ParticleTypes.POOF, 40, radius * 0.08);
    }

    /** Chuva sombria: dano e efeito de definhar em jogadores ao redor. */
    public static void curse(Mob boss, double radius, float damage, int witherSeconds) {
        for (Player p : playersNear(boss, radius)) {
            p.hurt(boss.damageSources().magic(), damage);
            p.addEffect(new MobEffectInstance(MobEffects.WITHER, witherSeconds * 20, 0));
        }
        sound(boss, SoundEvents.WITHER_SHOOT, 1.2F, 0.8F);
        burst(boss, ParticleTypes.SOUL, 60, radius * 0.4);
        ring(boss, ParticleTypes.SOUL, 44, radius * 0.1);
    }

    /** Anel de fogo: dano e fogo em jogadores ao redor. */
    public static void fireRing(Mob boss, double radius, float damage, int fireSeconds) {
        for (Player p : playersNear(boss, radius)) {
            p.hurt(boss.damageSources().mobAttack(boss), damage);
            p.setRemainingFireTicks(fireSeconds * 20);
        }
        sound(boss, SoundEvents.BLAZE_SHOOT, 1.5F, 0.6F);
        burst(boss, ParticleTypes.FLAME, 80, radius * 0.4);
        burst(boss, ParticleTypes.LAVA, 20, radius * 0.3);
        ring(boss, ParticleTypes.FLAME, 56, radius * 0.12);
        ring(boss, ParticleTypes.SMOKE, 40, radius * 0.07);
    }

    /** Teletransporta o chefe para as costas do alvo. */
    public static void teleportBehind(Mob boss, LivingEntity target) {
        Vec3 back = target.getLookAngle().scale(-2.0);
        burst(boss, ParticleTypes.PORTAL, 40, 0.6);
        boss.teleportTo(target.getX() + back.x, target.getY(), target.getZ() + back.z);
        sound(boss, SoundEvents.ENDERMAN_TELEPORT, 1.2F, 0.7F);
        burst(boss, ParticleTypes.PORTAL, 40, 0.6);
    }

    /** Avisa os jogadores por perto (início de fúria, etc.). */
    public static void announce(Mob boss, String text) {
        for (Player p : playersNear(boss, 60.0)) {
            p.displayClientMessage(Component.literal(text), true);
        }
    }

    public static AABB area(Mob boss, double radius) {
        return boss.getBoundingBox().inflate(radius);
    }

    /** Nuvem de praga: veneno e fome em jogadores ao redor. */
    public static void blight(Mob boss, double radius, int seconds) {
        for (Player p : playersNear(boss, radius)) {
            p.addEffect(new MobEffectInstance(MobEffects.POISON, seconds * 20, 1));
            p.addEffect(new MobEffectInstance(MobEffects.HUNGER, seconds * 20, 2));
        }
        sound(boss, SoundEvents.SPIDER_AMBIENT, 1.5F, 0.5F);
        burst(boss, ParticleTypes.ITEM_SLIME, 60, radius * 0.4);
    }

    /** Raio sobre o alvo (dano direto, sem fogo ao redor). */
    public static void lightningAt(Mob boss, LivingEntity target, float damage) {
        if (!(boss.level() instanceof ServerLevel level)) {
            return;
        }
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.moveTo(target.getX(), target.getY(), target.getZ());
            bolt.setVisualOnly(true);
            level.addFreshEntity(bolt);
        }
        target.hurt(boss.damageSources().lightningBolt(), damage);
    }

    /** Pulo para longe do alvo. */
    public static void leapAway(Mob boss, LivingEntity target, double power) {
        Vec3 dir = boss.position().subtract(target.position());
        Vec3 flat = new Vec3(dir.x, 0, dir.z);
        flat = flat.lengthSqr() < 1.0E-4 ? Vec3.ZERO : flat.normalize();
        boss.setDeltaMovement(flat.x * power, 0.5, flat.z * power);
        boss.hasImpulse = true;
    }

    /** Leque de flechas na direção do alvo. */
    public static void volley(Mob boss, LivingEntity target, int count, double spreadDegrees, double damage) {
        if (!(boss.level() instanceof ServerLevel level)) {
            return;
        }
        Vec3 dir = new Vec3(target.getX() - boss.getX(), target.getEyeY() - boss.getEyeY(), target.getZ() - boss.getZ());
        dir = dir.normalize();
        for (int i = 0; i < count; i++) {
            Arrow arrow = EntityType.ARROW.create(level);
            if (arrow == null) {
                continue;
            }
            double offset = (i - (count - 1) / 2.0) * Math.toRadians(spreadDegrees);
            Vec3 d = dir.yRot((float) offset);
            arrow.setOwner(boss);
            arrow.setPos(boss.getX(), boss.getEyeY() - 0.1, boss.getZ());
            arrow.shoot(d.x, d.y, d.z, 2.4F, 2.0F);
            arrow.setBaseDamage(damage);
            arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
            level.addFreshEntity(arrow);
        }
        sound(boss, SoundEvents.CROSSBOW_SHOOT, 1.5F, 0.8F);
    }

    /** Chama ajudantes perto do chefe, respeitando um limite de ajudantes por perto. */
    public static <T extends Mob> void summon(Mob boss, EntityType<T> type, Class<T> helperClass, int count, int maxNearby) {
        if (!(boss.level() instanceof ServerLevel level)) {
            return;
        }
        int nearby = level.getEntitiesOfClass(helperClass, boss.getBoundingBox().inflate(24.0)).size();
        for (int i = 0; i < count && nearby + i < maxNearby; i++) {
            type.spawn(level, boss.blockPosition().offset(i * 2 - 1, 0, 2), MobSpawnType.MOB_SUMMONED);
        }
    }

    /** Bola de fogo do chefe na direção do alvo (fere jogadores, não destrói blocos). */
    public static void fireball(Mob boss, LivingEntity target, float damage, double radius) {
        if (!(boss.level() instanceof ServerLevel level)) {
            return;
        }
        Vec3 start = new Vec3(boss.getX(), boss.getEyeY(), boss.getZ());
        Vec3 dir = new Vec3(target.getX() - start.x, target.getEyeY() - start.y, target.getZ() - start.z).normalize();
        com.racasrpg.ability.ProjectileEngine.fire(level, boss, start.add(dir.scale(1.5)), dir.scale(0.9), 80, damage,
                radius, 5);
    }
}
