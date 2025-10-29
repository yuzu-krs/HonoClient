package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.WinterDropBiomes;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;

public class WinterDropBiomeTagsProvider extends TagsProvider<Biome> {
    public WinterDropBiomeTagsProvider(
        PackOutput p_362199_, CompletableFuture<HolderLookup.Provider> p_363026_, CompletableFuture<TagsProvider.TagLookup<Biome>> p_364054_
    ) {
        super(p_362199_, Registries.BIOME, p_363026_, p_364054_);
    }

    @Override
    protected void addTags(HolderLookup.Provider p_362845_) {
        this.tag(BiomeTags.IS_FOREST).add(WinterDropBiomes.PALE_GARDEN);
        this.tag(BiomeTags.STRONGHOLD_BIASED_TO).add(WinterDropBiomes.PALE_GARDEN);
        this.tag(BiomeTags.IS_OVERWORLD).add(WinterDropBiomes.PALE_GARDEN);
        this.tag(BiomeTags.HAS_TRIAL_CHAMBERS).add(WinterDropBiomes.PALE_GARDEN);
    }
}