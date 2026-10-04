package com.racasrpg.entity;

import com.racasrpg.RacasRpg;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/** Faz os chefes aguentarem muito mais: todo dano recebido é dividido por {@link BossCore#HEALTH_SCALE}. */
@EventBusSubscriber(modid = RacasRpg.MODID)
public class BossDamageEvents {

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof IRaceBoss) {
            event.setAmount(event.getAmount() / BossCore.HEALTH_SCALE);
        }
    }
}
