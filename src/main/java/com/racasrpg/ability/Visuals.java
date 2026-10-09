package com.racasrpg.ability;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;

/** Efeitos visuais reutilizáveis (anéis e colunas de partículas). Só funcionam no servidor. */
public final class Visuals {
    private Visuals() {
    }

    /** Anel de partículas que se expande a partir do ponto (a velocidade controla até onde ele chega). */
    public static void ring(ServerLevel level, ParticleOptions type, double x, double y, double z, int points,
                            double speed) {
        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0 * i / points;
            // com count = 0, dx/dy/dz viram a direção da partícula e speed a multiplica
            level.sendParticles(type, x, y, z, 0, Math.cos(angle), 0.06, Math.sin(angle), speed);
        }
    }

    /** Coluna de partículas subindo do ponto. */
    public static void column(ServerLevel level, ParticleOptions type, double x, double y, double z, int count,
                              double height) {
        level.sendParticles(type, x, y + height / 2.0, z, count, 0.12, height / 2.0, 0.12, 0.02);
    }
}
