package com.racasrpg.entity;

import com.racasrpg.RacasRpg;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    private ModEntities() {
    }

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, RacasRpg.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<GoblinRaider>> GOBLIN_RAIDER =
            ENTITY_TYPES.register("goblin_raider", () -> EntityType.Builder.of(GoblinRaider::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(8).build("goblin_raider"));

    public static final DeferredHolder<EntityType<?>, EntityType<OrcBrute>> ORC_BRUTE =
            ENTITY_TYPES.register("orc_brute", () -> EntityType.Builder.of(OrcBrute::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(8).build("orc_brute"));

    public static final DeferredHolder<EntityType<?>, EntityType<DarkElfArcher>> DARK_ELF_ARCHER =
            ENTITY_TYPES.register("dark_elf_archer", () -> EntityType.Builder.of(DarkElfArcher::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F).clientTrackingRange(8).build("dark_elf_archer"));

    public static final DeferredHolder<EntityType<?>, EntityType<GoblinKing>> GOBLIN_KING =
            ENTITY_TYPES.register("goblin_king", () -> EntityType.Builder.of(GoblinKing::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(10).build("goblin_king"));

    public static final DeferredHolder<EntityType<?>, EntityType<ShadowChampion>> SHADOW_CHAMPION =
            ENTITY_TYPES.register("shadow_champion", () -> EntityType.Builder.of(ShadowChampion::new, MobCategory.MONSTER)
                    .sized(0.7F, 2.4F).fireImmune().clientTrackingRange(10).build("shadow_champion"));

    public static final DeferredHolder<EntityType<?>, EntityType<RaceMaster>> RACE_MASTER =
            ENTITY_TYPES.register("race_master", () -> EntityType.Builder.of(RaceMaster::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F).clientTrackingRange(10).build("race_master"));

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(GOBLIN_RAIDER.get(), GoblinRaider.createAttributes().build());
        event.put(ORC_BRUTE.get(), OrcBrute.createAttributes().build());
        event.put(DARK_ELF_ARCHER.get(), DarkElfArcher.createAttributes().build());
        event.put(GOBLIN_KING.get(), GoblinKing.createAttributes().build());
        event.put(SHADOW_CHAMPION.get(), ShadowChampion.createAttributes().build());
        event.put(RACE_MASTER.get(), Villager.createAttributes().build());
    }

    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(GOBLIN_RAIDER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) -> Monster.checkMonsterSpawnRules(type, level, reason, pos, random),
                RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(ORC_BRUTE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) -> Monster.checkMonsterSpawnRules(type, level, reason, pos, random),
                RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(DARK_ELF_ARCHER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) -> Monster.checkMonsterSpawnRules(type, level, reason, pos, random),
                RegisterSpawnPlacementsEvent.Operation.OR);
    }
}
