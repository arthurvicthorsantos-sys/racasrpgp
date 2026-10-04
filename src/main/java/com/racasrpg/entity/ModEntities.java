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

    public static final DeferredHolder<EntityType<?>, EntityType<StoneColossus>> STONE_COLOSSUS =
            ENTITY_TYPES.register("stone_colossus", () -> EntityType.Builder.of(StoneColossus::new, MobCategory.MONSTER)
                    .sized(1.4F, 2.7F).clientTrackingRange(10).build("stone_colossus"));

    public static final DeferredHolder<EntityType<?>, EntityType<OrcWarlord>> ORC_WARLORD =
            ENTITY_TYPES.register("orc_warlord", () -> EntityType.Builder.of(OrcWarlord::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(10).build("orc_warlord"));

    public static final DeferredHolder<EntityType<?>, EntityType<SpiderQueen>> SPIDER_QUEEN =
            ENTITY_TYPES.register("spider_queen", () -> EntityType.Builder.of(SpiderQueen::new, MobCategory.MONSTER)
                    .sized(1.4F, 0.9F).clientTrackingRange(10).build("spider_queen"));

    public static final DeferredHolder<EntityType<?>, EntityType<FlameWyrm>> FLAME_WYRM =
            ENTITY_TYPES.register("flame_wyrm", () -> EntityType.Builder.of(FlameWyrm::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F).fireImmune().clientTrackingRange(10).build("flame_wyrm"));

    public static final DeferredHolder<EntityType<?>, EntityType<RotLich>> ROT_LICH =
            ENTITY_TYPES.register("rot_lich", () -> EntityType.Builder.of(RotLich::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F).clientTrackingRange(10).build("rot_lich"));

    public static final DeferredHolder<EntityType<?>, EntityType<HarvestScarecrow>> HARVEST_SCARECROW =
            ENTITY_TYPES.register("harvest_scarecrow", () -> EntityType.Builder.of(HarvestScarecrow::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(10).build("harvest_scarecrow"));

    public static final DeferredHolder<EntityType<?>, EntityType<LabyrinthGuardian>> LABYRINTH_GUARDIAN =
            ENTITY_TYPES.register("labyrinth_guardian", () -> EntityType.Builder.of(LabyrinthGuardian::new, MobCategory.MONSTER)
                    .sized(1.95F, 2.2F).clientTrackingRange(10).build("labyrinth_guardian"));

    public static final DeferredHolder<EntityType<?>, EntityType<DrownedKing>> DROWNED_KING =
            ENTITY_TYPES.register("drowned_king", () -> EntityType.Builder.of(DrownedKing::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(10).build("drowned_king"));

    public static final DeferredHolder<EntityType<?>, EntityType<GnomeAutomaton>> GNOME_AUTOMATON =
            ENTITY_TYPES.register("gnome_automaton", () -> EntityType.Builder.of(GnomeAutomaton::new, MobCategory.MONSTER)
                    .sized(1.4F, 2.7F).clientTrackingRange(10).build("gnome_automaton"));

    public static final DeferredHolder<EntityType<?>, EntityType<ArcherColossus>> ARCHER_COLOSSUS =
            ENTITY_TYPES.register("archer_colossus", () -> EntityType.Builder.of(ArcherColossus::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(10).build("archer_colossus"));

    public static final DeferredHolder<EntityType<?>, EntityType<Basilisk>> BASILISK =
            ENTITY_TYPES.register("basilisk", () -> EntityType.Builder.of(Basilisk::new, MobCategory.MONSTER)
                    .sized(1.4F, 0.9F).clientTrackingRange(10).build("basilisk"));

    public static final DeferredHolder<EntityType<?>, EntityType<FallenAngel>> FALLEN_ANGEL =
            ENTITY_TYPES.register("fallen_angel", () -> EntityType.Builder.of(FallenAngel::new, MobCategory.MONSTER)
                    .sized(0.4F, 0.8F).fireImmune().clientTrackingRange(10).build("fallen_angel"));

    public static final DeferredHolder<EntityType<?>, EntityType<PactMaster>> PACT_MASTER =
            ENTITY_TYPES.register("pact_master", () -> EntityType.Builder.of(PactMaster::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(10).build("pact_master"));

    public static final DeferredHolder<EntityType<?>, EntityType<RaceMaster>> RACE_MASTER =
            ENTITY_TYPES.register("race_master", () -> EntityType.Builder.of(RaceMaster::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F).clientTrackingRange(10).build("race_master"));

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(GOBLIN_RAIDER.get(), GoblinRaider.createAttributes().build());
        event.put(ORC_BRUTE.get(), OrcBrute.createAttributes().build());
        event.put(DARK_ELF_ARCHER.get(), DarkElfArcher.createAttributes().build());
        event.put(GOBLIN_KING.get(), GoblinKing.createAttributes().build());
        event.put(SHADOW_CHAMPION.get(), ShadowChampion.createAttributes().build());
        event.put(STONE_COLOSSUS.get(), StoneColossus.createAttributes().build());
        event.put(ORC_WARLORD.get(), OrcWarlord.createAttributes().build());
        event.put(SPIDER_QUEEN.get(), SpiderQueen.createAttributes().build());
        event.put(FLAME_WYRM.get(), FlameWyrm.createAttributes().build());
        event.put(ROT_LICH.get(), RotLich.createAttributes().build());
        event.put(HARVEST_SCARECROW.get(), HarvestScarecrow.createAttributes().build());
        event.put(LABYRINTH_GUARDIAN.get(), LabyrinthGuardian.createAttributes().build());
        event.put(DROWNED_KING.get(), DrownedKing.createAttributes().build());
        event.put(GNOME_AUTOMATON.get(), GnomeAutomaton.createAttributes().build());
        event.put(ARCHER_COLOSSUS.get(), ArcherColossus.createAttributes().build());
        event.put(BASILISK.get(), Basilisk.createAttributes().build());
        event.put(FALLEN_ANGEL.get(), FallenAngel.createAttributes().build());
        event.put(PACT_MASTER.get(), PactMaster.createAttributes().build());
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
