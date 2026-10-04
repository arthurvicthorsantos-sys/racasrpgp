package com.racasrpg.item;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.racasrpg.RacasRpg;
import com.racasrpg.entity.BossInfo;
import com.racasrpg.entity.ModEntities;
import com.racasrpg.race.Race;

import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private ModItems() {
    }

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RacasRpg.MODID);
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, RacasRpg.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RacasRpg.MODID);

    /** Dropada pelos chefes; usada para criar as armas e armaduras das raças. */
    public static final DeferredItem<Item> BOSS_ESSENCE = ITEMS.register("boss_essence",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC).fireResistant()));

    /** Bússolas de busca: uma para cada chefe, com poucos usos. */
    public static final Map<BossInfo, DeferredItem<BossLocatorItem>> LOCATORS = new EnumMap<>(BossInfo.class);

    static {
        for (BossInfo boss : BossInfo.values()) {
            LOCATORS.put(boss, ITEMS.register("locator_" + boss.id(),
                    () -> new BossLocatorItem(boss, new Item.Properties().durability(3).rarity(Rarity.RARE))));
        }
    }

    public static final Map<Race, DeferredItem<RaceWeaponItem>> WEAPONS = new EnumMap<>(Race.class);
    public static final Map<Race, Map<ArmorItem.Type, DeferredItem<RaceArmorItem>>> ARMOR = new EnumMap<>(Race.class);

    // ---- ovos de geração (para testar criaturas, chefes e NPC) ----
    public static final List<DeferredItem<DeferredSpawnEggItem>> EGGS = new java.util.ArrayList<>();

    private static void egg(String name, Supplier<? extends EntityType<? extends Mob>> type, int c1, int c2) {
        EGGS.add(ITEMS.register(name + "_spawn_egg", () -> new DeferredSpawnEggItem(type, c1, c2, new Item.Properties())));
    }

    // dados: {botas, calças, peitoral, capacete}, resistência, resistência a empurrão
    private record ArmorProfile(int[] defense, float toughness, float knockback) {
    }

    private static ArmorProfile profile(Race race) {
        return switch (race) {
            case HUMANO -> new ArmorProfile(new int[]{3, 6, 8, 3}, 1.0F, 0.0F);
            case ELFO -> new ArmorProfile(new int[]{2, 5, 6, 2}, 0.0F, 0.0F);
            case ANAO -> new ArmorProfile(new int[]{3, 7, 9, 3}, 2.0F, 0.05F);
            case ORC -> new ArmorProfile(new int[]{3, 6, 8, 3}, 1.0F, 0.05F);
            case GOBLIN -> new ArmorProfile(new int[]{2, 4, 6, 2}, 0.0F, 0.0F);
            case DRACONATO -> new ArmorProfile(new int[]{3, 6, 8, 3}, 2.0F, 0.1F);
            case HALFLING -> new ArmorProfile(new int[]{2, 4, 5, 2}, 0.0F, 0.0F);
            case MINOTAURO -> new ArmorProfile(new int[]{3, 7, 9, 3}, 2.0F, 0.1F);
            case TRITAO -> new ArmorProfile(new int[]{2, 5, 7, 3}, 1.0F, 0.0F);
            case GNOMO -> new ArmorProfile(new int[]{2, 4, 5, 2}, 0.0F, 0.0F);
            case DROW -> new ArmorProfile(new int[]{2, 5, 6, 2}, 1.0F, 0.0F);
            case CENTAURO -> new ArmorProfile(new int[]{2, 5, 7, 3}, 0.0F, 0.0F);
            case REPTILIANO -> new ArmorProfile(new int[]{3, 5, 7, 3}, 1.0F, 0.0F);
            case ANJO -> new ArmorProfile(new int[]{2, 5, 6, 3}, 1.0F, 0.0F);
            case INFERNAL -> new ArmorProfile(new int[]{3, 6, 8, 3}, 2.0F, 0.05F);
        };
    }

    // dano base (somado ao do jogador), velocidade de ataque e efeito ao acertar (alvo, atacante)
    private static int weaponDamage(Race race) {
        return switch (race) {
            case HUMANO -> 5;
            case ELFO -> 4;
            case ANAO -> 8;
            case ORC -> 8;
            case GOBLIN -> 2;
            case DRACONATO -> 6;
            case HALFLING -> 3;
            case MINOTAURO -> 9;
            case TRITAO -> 6;
            case GNOMO -> 3;
            case DROW -> 4;
            case CENTAURO -> 5;
            case REPTILIANO -> 5;
            case ANJO -> 5;
            case INFERNAL -> 7;
        };
    }

    private static float weaponSpeed(Race race) {
        return switch (race) {
            case HUMANO -> -2.4F;
            case ELFO -> -1.8F;
            case ANAO -> -3.0F;
            case ORC -> -3.2F;
            case GOBLIN -> -1.0F;
            case DRACONATO -> -2.6F;
            case HALFLING -> -1.6F;
            case MINOTAURO -> -3.3F;
            case TRITAO -> -2.4F;
            case GNOMO -> -1.6F;
            case DROW -> -1.4F;
            case CENTAURO -> -2.2F;
            case REPTILIANO -> -2.0F;
            case ANJO -> -2.2F;
            case INFERNAL -> -2.6F;
        };
    }

    private static BiConsumer<LivingEntity, LivingEntity> weaponEffect(Race race) {
        return switch (race) {
            case HUMANO -> (target, attacker) -> attacker.heal(1.0F);
            case ELFO -> (target, attacker) ->
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0));
            case ANAO -> (target, attacker) -> {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0));
                target.knockback(0.6, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
            };
            case ORC -> (target, attacker) ->
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0));
            case GOBLIN -> (target, attacker) ->
                    attacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1));
            case DRACONATO -> (target, attacker) -> target.setRemainingFireTicks(80);
            case HALFLING -> (target, attacker) ->
                    target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
            case MINOTAURO -> (target, attacker) ->
                    target.knockback(1.1, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
            case TRITAO -> (target, attacker) -> {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0));
                attacker.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 60, 0));
            };
            case GNOMO -> (target, attacker) ->
                    target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 25, 0));
            case DROW -> (target, attacker) ->
                    target.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0));
            case CENTAURO -> (target, attacker) ->
                    attacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 1));
            case REPTILIANO -> (target, attacker) ->
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0));
            case ANJO -> (target, attacker) -> attacker.heal(2.0F);
            case INFERNAL -> (target, attacker) -> target.setRemainingFireTicks(120);
        };
    }

    static {
        for (Race race : Race.values()) {
            final ArmorProfile prof = profile(race);

            DeferredHolder<ArmorMaterial, ArmorMaterial> material = ARMOR_MATERIALS.register(race.id(),
                    () -> new ArmorMaterial(
                            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                                map.put(ArmorItem.Type.BOOTS, prof.defense()[0]);
                                map.put(ArmorItem.Type.LEGGINGS, prof.defense()[1]);
                                map.put(ArmorItem.Type.CHESTPLATE, prof.defense()[2]);
                                map.put(ArmorItem.Type.HELMET, prof.defense()[3]);
                                map.put(ArmorItem.Type.BODY, prof.defense()[2]);
                            }),
                            18,
                            SoundEvents.ARMOR_EQUIP_DIAMOND,
                            () -> Ingredient.of(Items.DIAMOND),
                            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(RacasRpg.MODID, race.id()))),
                            prof.toughness(),
                            prof.knockback()));

            Map<ArmorItem.Type, DeferredItem<RaceArmorItem>> pieces = new EnumMap<>(ArmorItem.Type.class);
            for (ArmorItem.Type type : new ArmorItem.Type[]{ArmorItem.Type.HELMET, ArmorItem.Type.CHESTPLATE,
                    ArmorItem.Type.LEGGINGS, ArmorItem.Type.BOOTS}) {
                String name = race.id() + "_" + type.getName();
                pieces.put(type, ITEMS.register(name, () -> new RaceArmorItem(race, material, type,
                        new Item.Properties().durability(type.getDurability(33)))));
            }
            ARMOR.put(race, pieces);

            final int damage = weaponDamage(race);
            final float speed = weaponSpeed(race);
            final BiConsumer<LivingEntity, LivingEntity> effect = weaponEffect(race);
            WEAPONS.put(race, ITEMS.register(race.id() + "_weapon", () -> {
                SimpleTier tier = new SimpleTier(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1561, 8.0F, 0.0F, 15,
                        () -> Ingredient.of(Items.DIAMOND));
                return new RaceWeaponItem(tier,
                        new Item.Properties().attributes(SwordItem.createAttributes(tier, damage, speed)), effect);
            }));
        }

        egg("goblin_raider", ModEntities.GOBLIN_RAIDER, 0x5A7A2A, 0xC8B040);
        egg("orc_brute", ModEntities.ORC_BRUTE, 0x6B4A3A, 0xB02020);
        egg("dark_elf_archer", ModEntities.DARK_ELF_ARCHER, 0x2A2A3A, 0x7050A0);
        egg("goblin_king", ModEntities.GOBLIN_KING, 0x3E5A1A, 0xFFD040);
        egg("shadow_champion", ModEntities.SHADOW_CHAMPION, 0x1A1020, 0x9040C0);
        egg("stone_colossus", ModEntities.STONE_COLOSSUS, 0x707070, 0xC0A060);
        egg("orc_warlord", ModEntities.ORC_WARLORD, 0x502020, 0xD04040);
        egg("spider_queen", ModEntities.SPIDER_QUEEN, 0x201020, 0xC040A0);
        egg("flame_wyrm", ModEntities.FLAME_WYRM, 0xB03010, 0xFFC020);
        egg("rot_lich", ModEntities.ROT_LICH, 0x203040, 0x80C0FF);
        egg("harvest_scarecrow", ModEntities.HARVEST_SCARECROW, 0xB09040, 0x405020);
        egg("labyrinth_guardian", ModEntities.LABYRINTH_GUARDIAN, 0x5A4A3A, 0xC03030);
        egg("drowned_king", ModEntities.DROWNED_KING, 0x206070, 0x60E0D0);
        egg("gnome_automaton", ModEntities.GNOME_AUTOMATON, 0xA06030, 0x60E0E0);
        egg("archer_colossus", ModEntities.ARCHER_COLOSSUS, 0x406030, 0xD0C070);
        egg("basilisk", ModEntities.BASILISK, 0x305020, 0xD0E040);
        egg("fallen_angel", ModEntities.FALLEN_ANGEL, 0xF0E0A0, 0x704090);
        egg("pact_master", ModEntities.PACT_MASTER, 0x601020, 0xFF8030);
        egg("race_master", ModEntities.RACE_MASTER, 0x806040, 0xFFD060);
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("racasrpg",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.racasrpg"))
                    .icon(() -> new ItemStack(BOSS_ESSENCE.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(BOSS_ESSENCE.get());
                        for (DeferredItem<BossLocatorItem> locator : LOCATORS.values()) {
                            output.accept(locator.get());
                        }
                        for (Race race : Race.values()) {
                            output.accept(WEAPONS.get(race).get());
                            for (DeferredItem<RaceArmorItem> piece : ARMOR.get(race).values()) {
                                output.accept(piece.get());
                            }
                        }
                        for (DeferredItem<DeferredSpawnEggItem> egg : EGGS) {
                            output.accept(egg.get());
                        }
                    })
                    .build());
}
