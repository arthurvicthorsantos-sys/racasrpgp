package com.racasrpg.item;

import com.racasrpg.race.Race;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

/** Peça de armadura de uma raça. Usar o conjunto completo dá um bônus. */
public class RaceArmorItem extends ArmorItem {
    private final Race race;

    public RaceArmorItem(Race race, Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
        this.race = race;
    }

    public Race race() {
        return this.race;
    }
}
