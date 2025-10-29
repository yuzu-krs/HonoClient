package net.minecraft.data.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class WinterDropBiomes {
    public static final ResourceKey<Biome> PALE_GARDEN = createKey("pale_garden");

    public static ResourceKey<Biome> createKey(String p_363822_) {
        return ResourceKey.create(Registries.BIOME, ResourceLocation.withDefaultNamespace(p_363822_));
    }

    public static void register(BootstrapContext<Biome> p_366941_, String p_362748_, Biome p_367115_) {
        p_366941_.register(createKey(p_362748_), p_367115_);
    }

    public static void bootstrap(BootstrapContext<Biome> p_370118_) {
        HolderGetter<PlacedFeature> holdergetter = p_370118_.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> holdergetter1 = p_370118_.lookup(Registries.CONFIGURED_CARVER);
        p_370118_.register(PALE_GARDEN, OverworldBiomes.darkForest(holdergetter, holdergetter1, true));
    }
}