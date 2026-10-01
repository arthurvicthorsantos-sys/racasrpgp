package com.racasrpg.entity;

import com.racasrpg.quest.QuestManager;
import com.racasrpg.race.Race;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Mestre da Raça: NPC que vive nas vilas raciais. Não pode ser ferido e não vende nada;
 * ao conversar, ajuda a escolher a raça e entrega missões com recompensa.
 */
public class RaceMaster extends Villager {
    private String raceId = "";

    public RaceMaster(EntityType<? extends Villager> type, Level level) {
        super(type, level);
        this.setInvulnerable(true);
        this.setPersistenceRequired();
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
                QuestManager.talk(serverPlayer, this.raceId);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Raca", this.raceId);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.raceId = tag.getString("Raca");
        Race race = Race.byId(this.raceId);
        if (race != null) {
            this.setCustomName(Component.literal("Mestre " + race.displayName()));
            this.setCustomNameVisible(true);
        }
    }
}
