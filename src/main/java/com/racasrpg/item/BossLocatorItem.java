package com.racasrpg.item;

import com.racasrpg.RacasRpg;
import com.racasrpg.entity.BossInfo;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;

/**
 * Bússola de busca de um chefe. Ao usar, procura o covil mais próximo daquele chefe e mostra a direção,
 * a distância e as coordenadas. Tem poucos usos, e o custo de criação é alto.
 */
public class BossLocatorItem extends Item {
    private final BossInfo boss;

    public BossLocatorItem(BossInfo boss, Properties properties) {
        super(properties);
        this.boss = boss;
    }

    public BossInfo boss() {
        return this.boss;
    }

    private static String direction(double dx, double dz) {
        double angle = Math.toDegrees(Math.atan2(dz, dx));      // 0 = leste, 90 = sul
        String[] names = {"leste", "sudeste", "sul", "sudoeste", "oeste", "noroeste", "norte", "nordeste"};
        int index = (int) Math.round(((angle + 360.0) % 360.0) / 45.0) % 8;
        return names[index];
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            TagKey<Structure> tag = TagKey.create(Registries.STRUCTURE,
                    ResourceLocation.fromNamespaceAndPath(RacasRpg.MODID, boss.lairId()));
            BlockPos found = serverLevel.findNearestMapStructure(tag, player.blockPosition(), 64, false);
            if (found == null) {
                serverPlayer.sendSystemMessage(Component.literal(
                        "A bússola gira sem rumo: não há covil de " + boss.displayName() + " por perto. Explore outras regiões.")
                        .withStyle(ChatFormatting.RED));
                return InteractionResultHolder.fail(stack);
            }

            double dx = found.getX() - player.getX();
            double dz = found.getZ() - player.getZ();
            int distance = (int) Math.round(Math.sqrt(dx * dx + dz * dz));
            serverPlayer.sendSystemMessage(Component.literal("Covil de " + boss.displayName() + ": a " + distance
                    + " blocos para " + direction(dx, dz) + " (X " + found.getX() + ", Z " + found.getZ() + ").")
                    .withStyle(ChatFormatting.GOLD));
            serverLevel.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME,
                    SoundSource.PLAYERS, 1.0F, 1.0F);
            player.getCooldowns().addCooldown(this, 40);
            stack.hurtAndBreak(1, serverPlayer, hand == InteractionHand.MAIN_HAND
                    ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
