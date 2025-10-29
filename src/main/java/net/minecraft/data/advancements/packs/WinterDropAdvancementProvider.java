package net.minecraft.data.advancements.packs;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;

public class WinterDropAdvancementProvider {
    public static AdvancementProvider create(PackOutput p_366484_, CompletableFuture<HolderLookup.Provider> p_369981_) {
        return new AdvancementProvider(p_366484_, p_369981_, List.of(new WinterDropAdventureAdvancements()));
    }
}