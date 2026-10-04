package com.racasrpg.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

/** Parte comum dos chefes: barra de vida, música de luta e fúria com metade da vida. */
public final class BossCore {
    private final ServerBossEvent event;
    private final BossMusic music;
    private boolean enraged = false;

    public BossCore(String nameKey, BossEvent.BossBarColor color, String vanillaSound, int musicInterval) {
        this.event = new ServerBossEvent(Component.translatable(nameKey), color, BossEvent.BossBarOverlay.PROGRESS);
        this.music = new BossMusic(vanillaSound, musicInterval);
    }

    public void seen(ServerPlayer player) {
        this.event.addPlayer(player);
    }

    public void unseen(ServerPlayer player) {
        this.event.removePlayer(player);
    }

    public void stopMusic(Mob boss) {
        if (boss.level() instanceof ServerLevel level) {
            this.music.stop(level);
        }
    }

    /** Chamar todo tick do servidor. Atualiza a barra e a música e devolve o alvo atual (ou null). */
    public LivingEntity tick(Mob boss) {
        this.event.setProgress(boss.getHealth() / boss.getMaxHealth());
        LivingEntity target = boss.getTarget();
        this.music.tick(boss, target instanceof Player);
        return target;
    }

    /** Verdadeiro uma única vez, quando a vida cai abaixo de 50%. */
    public boolean enrageNow(Mob boss) {
        if (!this.enraged && boss.getHealth() < boss.getMaxHealth() * 0.5F) {
            this.enraged = true;
            return true;
        }
        return false;
    }

    public boolean enraged() {
        return this.enraged;
    }
}
