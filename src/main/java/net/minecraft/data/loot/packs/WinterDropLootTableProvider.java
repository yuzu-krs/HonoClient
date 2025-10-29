package net.minecraft.data.loot.packs;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class WinterDropLootTableProvider {
    public static LootTableProvider create(PackOutput p_367409_, CompletableFuture<HolderLookup.Provider> p_364397_) {
        return new LootTableProvider(
            p_367409_, Set.of(), List.of(new LootTableProvider.SubProviderEntry(WinterDropBlockLoot::new, LootContextParamSets.BLOCK)), p_364397_
        );
    }
}