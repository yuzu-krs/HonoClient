package net.minecraft.data.loot.packs;

import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class WinterDropBlockLoot extends BlockLootSubProvider {
    public WinterDropBlockLoot(HolderLookup.Provider p_367511_) {
        super(Set.of(), FeatureFlagSet.of(FeatureFlags.WINTER_DROP), p_367511_);
    }

    @Override
    protected void generate() {
        this.dropSelf(Blocks.PALE_OAK_PLANKS);
        this.dropSelf(Blocks.PALE_OAK_SAPLING);
        this.dropSelf(Blocks.PALE_OAK_LOG);
        this.dropSelf(Blocks.STRIPPED_PALE_OAK_LOG);
        this.dropSelf(Blocks.PALE_OAK_WOOD);
        this.dropSelf(Blocks.STRIPPED_PALE_OAK_WOOD);
        this.dropSelf(Blocks.PALE_OAK_SIGN);
        this.dropSelf(Blocks.PALE_OAK_HANGING_SIGN);
        this.dropSelf(Blocks.PALE_OAK_PRESSURE_PLATE);
        this.dropSelf(Blocks.PALE_OAK_TRAPDOOR);
        this.dropSelf(Blocks.PALE_OAK_BUTTON);
        this.dropSelf(Blocks.PALE_OAK_STAIRS);
        this.dropSelf(Blocks.PALE_OAK_FENCE_GATE);
        this.dropSelf(Blocks.PALE_OAK_FENCE);
        this.add(Blocks.PALE_MOSS_CARPET, p_368545_ -> this.createMossyCarpetBlockDrops(p_368545_));
        this.add(Blocks.PALE_HANGING_MOSS, p_361502_ -> this.createShearsOrSilkTouchOnlyDrop(p_361502_));
        this.dropSelf(Blocks.PALE_MOSS_BLOCK);
        this.dropPottedContents(Blocks.POTTED_PALE_OAK_SAPLING);
        this.add(Blocks.PALE_OAK_SLAB, p_363630_ -> this.createSlabItemTable(p_363630_));
        this.add(Blocks.PALE_OAK_DOOR, p_361249_ -> this.createDoorTable(p_361249_));
        this.add(Blocks.PALE_OAK_LEAVES, p_364256_ -> this.createLeavesDrops(p_364256_, Blocks.PALE_OAK_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES));
        this.dropWhenSilkTouch(Blocks.CREAKING_HEART);
    }
}