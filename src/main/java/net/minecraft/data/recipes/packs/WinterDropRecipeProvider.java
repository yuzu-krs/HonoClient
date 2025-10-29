package net.minecraft.data.recipes.packs;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class WinterDropRecipeProvider extends RecipeProvider {
    public WinterDropRecipeProvider(HolderLookup.Provider p_362093_, RecipeOutput p_367519_) {
        super(p_362093_, p_367519_);
    }

    @Override
    protected void buildRecipes() {
        this.generateForEnabledBlockFamilies(FeatureFlagSet.of(FeatureFlags.WINTER_DROP));
        this.hangingSign(Items.PALE_OAK_HANGING_SIGN, Blocks.STRIPPED_PALE_OAK_LOG);
        this.planksFromLog(Blocks.PALE_OAK_PLANKS, ItemTags.PALE_OAK_LOGS, 4);
        this.woodFromLogs(Blocks.PALE_OAK_WOOD, Blocks.PALE_OAK_LOG);
        this.woodFromLogs(Blocks.STRIPPED_PALE_OAK_WOOD, Blocks.STRIPPED_PALE_OAK_LOG);
        this.woodenBoat(Items.PALE_OAK_BOAT, Blocks.PALE_OAK_PLANKS);
        this.chestBoat(Items.PALE_OAK_CHEST_BOAT, Items.PALE_OAK_BOAT);
        this.carpet(Blocks.PALE_MOSS_CARPET, Blocks.PALE_MOSS_BLOCK);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput p_368733_, CompletableFuture<HolderLookup.Provider> p_365882_) {
            super(p_368733_, p_365882_);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider p_362565_, RecipeOutput p_366491_) {
            return new WinterDropRecipeProvider(p_362565_, p_366491_);
        }

        @Override
        public String getName() {
            return "Winter Drop Recipes";
        }
    }
}