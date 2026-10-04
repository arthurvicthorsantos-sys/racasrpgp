package com.racasrpg.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;

/**
 * Música de luta de chefe, usando faixas do jogo base (discos de música). Toca para quem está perto
 * enquanto o chefe tem um jogador como alvo, e para quando a luta acaba.
 */
public final class BossMusic {
    private final ResourceLocation id;
    private final SoundEvent sound;
    private final int intervalTicks;
    private final Set<UUID> heard = new HashSet<>();
    private int timer = 0;
    private boolean playing = false;

    public BossMusic(String vanillaSoundPath, int intervalTicks) {
        this.id = ResourceLocation.withDefaultNamespace(vanillaSoundPath);
        this.sound = SoundEvent.createVariableRangeEvent(this.id);
        this.intervalTicks = intervalTicks;
    }

    /** Chamar todo tick de IA do servidor. */
    public void tick(Mob boss, boolean fighting) {
        if (!(boss.level() instanceof ServerLevel level)) {
            return;
        }
        if (!fighting) {
            if (playing) {
                stop(level);
            }
            return;
        }

        boolean restart = !playing || --timer <= 0;
        if (restart) {
            stopFor(level);
            heard.clear();
            timer = intervalTicks;
            playing = true;
        }
        if (restart || boss.tickCount % 40 == 0) {
            for (ServerPlayer player : level.players()) {
                if (player.distanceToSqr(boss) <= 48.0 * 48.0 && heard.add(player.getUUID())) {
                    player.playNotifySound(sound, SoundSource.RECORDS, 3.0F, 1.0F);
                }
            }
        }
    }

    /** Para a música (chefe derrotado ou removido). */
    public void stop(ServerLevel level) {
        stopFor(level);
        heard.clear();
        playing = false;
        timer = 0;
    }

    private void stopFor(ServerLevel level) {
        ClientboundStopSoundPacket packet = new ClientboundStopSoundPacket(id, SoundSource.RECORDS);
        for (ServerPlayer player : level.players()) {
            player.connection.send(packet);
        }
    }
}
