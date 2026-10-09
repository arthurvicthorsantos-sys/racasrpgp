package com.racasrpg.ability;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Blocos de construção das habilidades lançáveis das raças. */
public final class AbilityEffects {
    private AbilityEffects() {
    }

    /** Um efeito de poção com duração em segundos. */
    public record Fx(Holder<MobEffect> effect, int seconds, int amplifier) {
    }

    public static Fx fx(Holder<MobEffect> effect, int seconds, int amplifier) {
        return new Fx(effect, seconds, amplifier);
    }

    private static void particles(ServerPlayer p, ParticleOptions type, int count, double spread) {
        p.serverLevel().sendParticles(type, p.getX(), p.getY() + 1.0, p.getZ(), count, spread, spread, spread, 0.05);
    }

    /** Aplica efeitos em si mesmo. */
    public static void buff(ServerPlayer p, ParticleOptions particle, Fx... effects) {
        for (Fx f : effects) {
            p.addEffect(new MobEffectInstance(f.effect(), f.seconds() * 20, f.amplifier()));
        }
        p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.PLAYERS, 0.8F, 1.2F);
        particles(p, particle, 25, 0.6);
    }

    /** Cura vida e fome, com absorção opcional. */
    public static void heal(ServerPlayer p, float health, int food, int absorptionSeconds) {
        p.heal(health);
        if (food > 0) {
            p.getFoodData().eat(food, 0.8F);
        }
        if (absorptionSeconds > 0) {
            p.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.ABSORPTION, absorptionSeconds * 20, 1));
        }
        p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8F, 1.5F);
        particles(p, ParticleTypes.HAPPY_VILLAGER, 20, 0.6);
    }

    /** Impulso para a frente. */
    public static void dash(ServerPlayer p, double power) {
        Vec3 look = p.getLookAngle();
        p.setDeltaMovement(look.x * power, Math.max(look.y * power * 0.5, 0.15), look.z * power);
        p.hurtMarked = true;
        p.fallDistance = 0.0F;
        p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 0.7F);
        particles(p, ParticleTypes.CLOUD, 15, 0.3);
    }

    /** Teletransporte curto na direção do olhar. */
    public static void blink(ServerPlayer p, double distance) {
        ServerLevel level = p.serverLevel();
        Vec3 eye = p.getEyePosition();
        Vec3 look = p.getLookAngle();
        Vec3 end = eye.add(look.scale(distance));
        BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, p));
        Vec3 target = hit.getType() == HitResult.Type.MISS ? end : hit.getLocation().subtract(look.scale(0.6));
        particles(p, ParticleTypes.PORTAL, 30, 0.5);
        p.teleportTo(target.x, target.y - p.getEyeHeight(), target.z);
        p.fallDistance = 0.0F;
        level.playSound(null, p.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.8F, 1.3F);
        particles(p, ParticleTypes.PORTAL, 30, 0.5);
    }

    /** Pancada em área: dano e empurrão em monstros ao redor. */
    public static void slam(ServerPlayer p, double radius, float damage, double knockback) {
        ServerLevel level = p.serverLevel();
        AABB box = p.getBoundingBox().inflate(radius);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, box,
                e -> e != p && e.isAlive() && e instanceof Enemy);
        for (LivingEntity e : targets) {
            e.hurt(p.damageSources().playerAttack(p), damage);
            e.knockback(knockback, p.getX() - e.getX(), p.getZ() - e.getZ());
        }
        level.playSound(null, p.blockPosition(), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.PLAYERS, 0.8F, 1.1F);
        particles(p, ParticleTypes.EXPLOSION, 6, radius * 0.4);
        particles(p, ParticleTypes.CLOUD, 30, radius * 0.5);
        Visuals.ring(level, ParticleTypes.CLOUD, p.getX(), p.getY() + 0.2, p.getZ(), 40, radius * 0.1);
        Visuals.ring(level, ParticleTypes.CRIT, p.getX(), p.getY() + 0.2, p.getZ(), 32, radius * 0.14);
    }

    /** Rugido: enfraquece e atrasa monstros ao redor, com um pouco de dano. */
    public static void roar(ServerPlayer p, double radius, float damage, int debuffSeconds) {
        ServerLevel level = p.serverLevel();
        AABB box = p.getBoundingBox().inflate(radius);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, box,
                e -> e != p && e.isAlive() && e instanceof Enemy);
        for (LivingEntity e : targets) {
            e.hurt(p.damageSources().playerAttack(p), damage);
            e.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, debuffSeconds * 20, 1));
            e.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, debuffSeconds * 20, 1));
            e.knockback(0.6, p.getX() - e.getX(), p.getZ() - e.getZ());
        }
        level.playSound(null, p.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 0.7F, 1.3F);
        particles(p, ParticleTypes.SMOKE, 40, radius * 0.4);
        Visuals.ring(level, ParticleTypes.POOF, p.getX(), p.getY() + 0.3, p.getZ(), 36, radius * 0.08);
        Visuals.ring(level, ParticleTypes.SMOKE, p.getX(), p.getY() + 0.3, p.getZ(), 28, radius * 0.05);
    }

    /** Fumaça: cega e atrasa monstros ao redor e acelera quem lançou. */
    public static void smoke(ServerPlayer p, double radius, int seconds) {
        ServerLevel level = p.serverLevel();
        AABB box = p.getBoundingBox().inflate(radius);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, box,
                e -> e != p && e.isAlive() && e instanceof Enemy);
        for (LivingEntity e : targets) {
            e.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.BLINDNESS, seconds * 20, 0));
            e.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, seconds * 20, 1));
        }
        p.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, seconds * 20, 1));
        level.playSound(null, p.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, 0.8F);
        particles(p, ParticleTypes.LARGE_SMOKE, 60, radius * 0.4);
    }

    /** Leque de flechas mágicas (podem pegar fogo). */
    public static void volley(ServerPlayer p, int count, double spreadDegrees, double damage, int fireSeconds) {
        ServerLevel level = p.serverLevel();
        Vec3 look = p.getLookAngle();
        for (int i = 0; i < count; i++) {
            Arrow arrow = EntityType.ARROW.create(level);
            if (arrow == null) {
                continue;
            }
            double offset = (i - (count - 1) / 2.0) * Math.toRadians(spreadDegrees);
            Vec3 dir = look.yRot((float) offset);
            arrow.setOwner(p);
            arrow.setPos(p.getX(), p.getEyeY() - 0.1, p.getZ());
            arrow.shoot(dir.x, dir.y, dir.z, 2.8F, 1.0F);
            arrow.setBaseDamage(damage);
            arrow.setCritArrow(true);
            arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
            if (fireSeconds > 0) {
                arrow.setRemainingFireTicks(fireSeconds * 20);
            }
            level.addFreshEntity(arrow);
        }
        level.playSound(null, p.blockPosition(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, 0.9F);
    }

    private static List<LivingEntity> enemiesAround(ServerPlayer p, double radius) {
        return p.serverLevel().getEntitiesOfClass(LivingEntity.class, p.getBoundingBox().inflate(radius),
                e -> e != p && e.isAlive() && e instanceof Enemy);
    }

    /** Salto: sobe e avança, com queda suave. */
    public static void leap(ServerPlayer p, double up, double forward) {
        Vec3 look = p.getLookAngle();
        Vec3 flat = new Vec3(look.x, 0, look.z);
        flat = flat.lengthSqr() < 1.0E-4 ? Vec3.ZERO : flat.normalize();
        p.setDeltaMovement(flat.x * forward, up, flat.z * forward);
        p.hurtMarked = true;
        p.fallDistance = 0.0F;
        p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.RABBIT_JUMP, SoundSource.PLAYERS, 1.0F, 0.6F);
        particles(p, ParticleTypes.CLOUD, 20, 0.4);
    }

    /** Esquiva: impulso para trás. */
    public static void dodge(ServerPlayer p, double power) {
        Vec3 look = p.getLookAngle();
        Vec3 flat = new Vec3(look.x, 0, look.z);
        flat = flat.lengthSqr() < 1.0E-4 ? Vec3.ZERO : flat.normalize();
        p.setDeltaMovement(-flat.x * power, 0.25, -flat.z * power);
        p.hurtMarked = true;
        p.fallDistance = 0.0F;
        p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.8F, 1.4F);
        particles(p, ParticleTypes.CLOUD, 12, 0.3);
    }

    /** Veneno e fraqueza em monstros ao redor. */
    public static void poison(ServerPlayer p, double radius, int seconds) {
        for (LivingEntity e : enemiesAround(p, radius)) {
            e.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.POISON, seconds * 20, 1));
            e.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, seconds * 20, 0));
        }
        p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.SPIDER_AMBIENT, SoundSource.PLAYERS, 1.0F, 0.6F);
        particles(p, ParticleTypes.ITEM_SLIME, 40, radius * 0.4);
        Visuals.ring(p.serverLevel(), ParticleTypes.ITEM_SLIME, p.getX(), p.getY() + 0.3, p.getZ(), 32, radius * 0.1);
    }

    /** Explosão de fogo: dano e fogo em monstros ao redor. */
    public static void fireBurst(ServerPlayer p, double radius, float damage, int fireSeconds) {
        for (LivingEntity e : enemiesAround(p, radius)) {
            e.hurt(p.damageSources().playerAttack(p), damage);
            e.setRemainingFireTicks(fireSeconds * 20);
        }
        p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.2F, 0.7F);
        particles(p, ParticleTypes.FLAME, 80, radius * 0.4);
        particles(p, ParticleTypes.LAVA, 15, radius * 0.3);
        Visuals.ring(p.serverLevel(), ParticleTypes.FLAME, p.getX(), p.getY() + 0.3, p.getZ(), 48, radius * 0.12);
        Visuals.ring(p.serverLevel(), ParticleTypes.SMOKE, p.getX(), p.getY() + 0.3, p.getZ(), 36, radius * 0.07);
        for (LivingEntity e : enemiesAround(p, radius)) {
            Visuals.column(p.serverLevel(), ParticleTypes.FLAME, e.getX(), e.getY(), e.getZ(), 30, 2.0);
        }
    }

    /** Drena vida de monstros ao redor e cura quem lançou. */
    public static void lifeDrain(ServerPlayer p, double radius, float damage, float maxHeal) {
        float healed = 0.0F;
        for (LivingEntity e : enemiesAround(p, radius)) {
            e.hurt(p.damageSources().playerAttack(p), damage);
            healed += damage * 0.5F;
            for (int i = 1; i <= 10; i++) {
                double t = i / 10.0;
                p.serverLevel().sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        e.getX() + (p.getX() - e.getX()) * t, e.getY() + 1.0 + (p.getY() - e.getY()) * t * 0.5,
                        e.getZ() + (p.getZ() - e.getZ()) * t, 1, 0.05, 0.05, 0.05, 0.0);
            }
        }
        p.heal(Math.min(healed, maxHeal));
        p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 0.5F, 1.8F);
        particles(p, ParticleTypes.SOUL, 40, radius * 0.4);
    }

    /** Luz sagrada: fere monstros (o dobro em mortos-vivos) e os destaca. */
    public static void smite(ServerPlayer p, double radius, float damage) {
        for (LivingEntity e : enemiesAround(p, radius)) {
            float total = e.getType().is(EntityTypeTags.UNDEAD) ? damage * 2.0F : damage;
            e.hurt(p.damageSources().playerAttack(p), total);
            e.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.GLOWING, 100, 0));
            Visuals.column(p.serverLevel(), ParticleTypes.END_ROD, e.getX(), e.getY(), e.getZ(), 28, 2.5);
        }
        Visuals.ring(p.serverLevel(), ParticleTypes.END_ROD, p.getX(), p.getY() + 0.3, p.getZ(), 36, radius * 0.1);
        p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.6F);
        particles(p, ParticleTypes.END_ROD, 60, radius * 0.4);
    }

    /** Maldição sombria: dano e definhar em monstros ao redor. */
    public static void curse(ServerPlayer p, double radius, float damage, int witherSeconds) {
        for (LivingEntity e : enemiesAround(p, radius)) {
            e.hurt(p.damageSources().playerAttack(p), damage);
            e.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.WITHER, witherSeconds * 20, 0));
        }
        p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 0.8F, 1.2F);
        particles(p, ParticleTypes.SOUL, 60, radius * 0.4);
        Visuals.ring(p.serverLevel(), ParticleTypes.SOUL, p.getX(), p.getY() + 0.3, p.getZ(), 40, radius * 0.09);
    }

    /** Nova gélida: dano, lentidão forte e congelamento em monstros ao redor. */
    public static void frostNova(ServerPlayer p, double radius, float damage, int seconds) {
        for (LivingEntity e : enemiesAround(p, radius)) {
            e.hurt(p.damageSources().playerAttack(p), damage);
            e.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, seconds * 20, 2));
            e.setTicksFrozen(200);
        }
        p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.2F, 0.6F);
        particles(p, ParticleTypes.SNOWFLAKE, 80, radius * 0.4);
    }

    /** Raio no ponto que você está olhando. */
    public static void lightning(ServerPlayer p, double range) {
        ServerLevel level = p.serverLevel();
        Vec3 eye = p.getEyePosition();
        Vec3 end = eye.add(p.getLookAngle().scale(range));
        BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, p));
        Vec3 target = hit.getType() == HitResult.Type.MISS ? end : hit.getLocation();
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.moveTo(target.x, target.y, target.z);
            bolt.setCause(p);
            level.addFreshEntity(bolt);
        }
    }

    /** Aura: aplica efeitos em quem lançou e nos jogadores aliados por perto. */
    public static void aura(ServerPlayer p, double radius, ParticleOptions particle, Fx... effects) {
        ServerLevel level = p.serverLevel();
        List<Player> allies = level.getEntitiesOfClass(Player.class, p.getBoundingBox().inflate(radius),
                o -> o.isAlive() && !o.isSpectator());
        for (Player ally : allies) {
            for (Fx f : effects) {
                ally.addEffect(new MobEffectInstance(f.effect(), f.seconds() * 20, f.amplifier()));
            }
        }
        level.playSound(null, p.blockPosition(), SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.PLAYERS, 1.2F, 1.0F);
        particles(p, particle, 50, radius * 0.3);
    }

    /** Marca monstros ao redor: ficam brilhando (visíveis através de paredes) e mais fracos. */
    public static void markEnemies(ServerPlayer p, double radius, int seconds) {
        for (LivingEntity e : enemiesAround(p, radius)) {
            e.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.GLOWING, seconds * 20, 0));
            e.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, seconds * 20, 0));
        }
        p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.4F);
        particles(p, ParticleTypes.ENCHANT, 50, radius * 0.2);
    }

    /** Provocar: monstros ao redor passam a atacar quem lançou. */
    public static void taunt(ServerPlayer p, double radius) {
        for (LivingEntity e : enemiesAround(p, radius)) {
            if (e instanceof Mob mob) {
                mob.setTarget(p);
            }
        }
        p.serverLevel().playSound(null, p.blockPosition(), SoundEvents.RAVAGER_ROAR, SoundSource.PLAYERS, 1.0F, 1.2F);
        particles(p, ParticleTypes.ANGRY_VILLAGER, 20, 0.8);
    }

    /** Armadilha de teias no ponto que você olha. */
    public static void trap(ServerPlayer p, double range) {
        ServerLevel level = p.serverLevel();
        Vec3 eye = p.getEyePosition();
        Vec3 end = eye.add(p.getLookAngle().scale(range));
        BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, p));
        Vec3 target = hit.getType() == HitResult.Type.MISS ? end : hit.getLocation();
        BlockPos center = BlockPos.containing(target);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos pos = center.offset(dx, 0, dz);
                if (level.getBlockState(pos).isAir()) {
                    level.setBlock(pos, Blocks.COBWEB.defaultBlockState(), 3);
                }
            }
        }
        level.playSound(null, p.blockPosition(), SoundEvents.SPIDER_STEP, SoundSource.PLAYERS, 1.0F, 0.8F);
    }

    /** Bola de fogo de verdade: voa, deixa rastro e explode ao atingir, queimando monstros em área. */
    public static void fireball(ServerPlayer p, float damage, double radius, int fireSeconds) {
        Vec3 dir = p.getLookAngle();
        Vec3 start = p.getEyePosition().add(dir.scale(1.2)).subtract(0.0, 0.25, 0.0);
        ProjectileEngine.fire(p.serverLevel(), p, start, dir.scale(1.15), 70, damage, radius, fireSeconds);
    }
}
