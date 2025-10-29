package net.minecraft.data.advancements.packs;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;

public class WinterDropAdventureAdvancements implements AdvancementSubProvider {
    @Override
    public void generate(HolderLookup.Provider p_369220_, Consumer<AdvancementHolder> p_362446_) {
        AdvancementHolder advancementholder = AdvancementSubProvider.createPlaceholder("adventure/root");
        VanillaAdventureAdvancements.createMonsterHunterAdvancement(
            advancementholder,
            p_362446_,
            p_369220_.lookupOrThrow(Registries.ENTITY_TYPE),
            Stream.concat(VanillaAdventureAdvancements.MOBS_TO_KILL.stream(), Stream.of(EntityType.CREAKING_TRANSIENT)).collect(Collectors.toList())
        );
        AdvancementHolder advancementholder1 = AdvancementSubProvider.createPlaceholder("adventure/sleep_in_bed");
        VanillaAdventureAdvancements.createAdventuringTime(p_369220_, p_362446_, advancementholder1, MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD_WINTER_DROP);
    }
}