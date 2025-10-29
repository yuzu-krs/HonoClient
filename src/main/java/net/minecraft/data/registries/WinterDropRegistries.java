package net.minecraft.data.registries;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.WinterDropBiomes;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;

public class WinterDropRegistries {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
        .add(Registries.BIOME, WinterDropBiomes::bootstrap)
        .add(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterLists::winterDrop);

    public static CompletableFuture<RegistrySetBuilder.PatchedRegistries> createLookup(CompletableFuture<HolderLookup.Provider> p_368033_) {
        return RegistryPatchGenerator.createLookup(p_368033_, BUILDER).thenApply(p_368200_ -> {
            VanillaRegistries.validateThatAllBiomeFeaturesHaveBiomeFilter(p_368200_.full().lookupOrThrow(Registries.PLACED_FEATURE), p_368200_.full().lookupOrThrow(Registries.BIOME));
            return (RegistrySetBuilder.PatchedRegistries)p_368200_;
        });
    }
}