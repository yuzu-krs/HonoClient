package net.minecraft.recipebook;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;

public class ServerPlaceRecipe<R extends Recipe<?>> {
    private static final int ITEM_NOT_FOUND = -1;
    private final Inventory inventory;
    private final ServerPlaceRecipe.CraftingMenuAccess<R> menu;
    private final boolean useMaxItems;
    private final int gridWidth;
    private final int gridHeight;
    private final List<Slot> inputGridSlots;
    private final List<Slot> slotsToClear;

    public static <I extends RecipeInput, R extends Recipe<I>> RecipeBookMenu.PostPlaceAction placeRecipe(
        ServerPlaceRecipe.CraftingMenuAccess<R> p_361168_,
        int p_364309_,
        int p_363223_,
        List<Slot> p_362609_,
        List<Slot> p_366501_,
        Inventory p_367037_,
        RecipeHolder<R> p_365886_,
        boolean p_366586_,
        boolean p_362700_
    ) {
        ServerPlaceRecipe<R> serverplacerecipe = new ServerPlaceRecipe<>(p_361168_, p_367037_, p_366586_, p_364309_, p_363223_, p_362609_, p_366501_);
        if (!p_362700_ && !serverplacerecipe.testClearGrid()) {
            return RecipeBookMenu.PostPlaceAction.NOTHING;
        } else {
            StackedItemContents stackeditemcontents = new StackedItemContents();
            p_367037_.fillStackedContents(stackeditemcontents);
            p_361168_.fillCraftSlotsStackedContents(stackeditemcontents);
            return serverplacerecipe.tryPlaceRecipe(p_365886_, stackeditemcontents);
        }
    }

    private ServerPlaceRecipe(
        ServerPlaceRecipe.CraftingMenuAccess<R> p_364245_,
        Inventory p_366205_,
        boolean p_362556_,
        int p_368821_,
        int p_369958_,
        List<Slot> p_364688_,
        List<Slot> p_367687_
    ) {
        this.menu = p_364245_;
        this.inventory = p_366205_;
        this.useMaxItems = p_362556_;
        this.gridWidth = p_368821_;
        this.gridHeight = p_369958_;
        this.inputGridSlots = p_364688_;
        this.slotsToClear = p_367687_;
    }

    private RecipeBookMenu.PostPlaceAction tryPlaceRecipe(RecipeHolder<R> p_365851_, StackedItemContents p_368441_) {
        if (p_368441_.canCraft(p_365851_.value(), null)) {
            this.placeRecipe(p_365851_, p_368441_);
            this.inventory.setChanged();
            return RecipeBookMenu.PostPlaceAction.NOTHING;
        } else {
            this.clearGrid();
            this.inventory.setChanged();
            return RecipeBookMenu.PostPlaceAction.PLACE_GHOST_RECIPE;
        }
    }

    private void clearGrid() {
        for (Slot slot : this.slotsToClear) {
            ItemStack itemstack = slot.getItem().copy();
            this.inventory.placeItemBackInInventory(itemstack, false);
            slot.set(itemstack);
        }

        this.menu.clearCraftingContent();
    }

    private void placeRecipe(RecipeHolder<R> p_369245_, StackedItemContents p_365814_) {
        boolean flag = this.menu.recipeMatches(p_369245_);
        int i = p_365814_.getBiggestCraftableStack(p_369245_.value(), null);
        if (flag) {
            for (Slot slot : this.inputGridSlots) {
                ItemStack itemstack = slot.getItem();
                if (!itemstack.isEmpty() && Math.min(i, itemstack.getMaxStackSize()) < itemstack.getCount() + 1) {
                    return;
                }
            }
        }

        int k = this.calculateAmountToCraft(i, flag);
        List<Holder<Item>> list = new ArrayList<>();
        if (p_365814_.canCraft(p_369245_.value(), k, list::add)) {
            OptionalInt optionalint = list.stream().mapToInt(p_367938_ -> p_367938_.value().getDefaultMaxStackSize()).min();
            if (optionalint.isPresent()) {
                k = Math.min(k, optionalint.getAsInt());
            }

            list.clear();
            if (p_365814_.canCraft(p_369245_.value(), k, list::add)) {
                this.clearGrid();
                int j = k;
                PlaceRecipeHelper.placeRecipe(
                    this.gridWidth,
                    this.gridHeight,
                    p_369245_.value(),
                    p_369245_.value().placementInfo().slotInfo(),
                    (p_365768_, p_361490_, p_361353_, p_369562_) -> {
                        if (!p_365768_.isEmpty()) {
                            Slot slot1 = this.inputGridSlots.get(p_361490_);
                            int l = p_365768_.get().placerOutputPosition();
                            int i1 = j;

                            while (i1 > 0) {
                                Holder<Item> holder = list.get(l);
                                i1 = this.moveItemToGrid(slot1, holder, i1);
                                if (i1 == -1) {
                                    return;
                                }
                            }
                        }
                    }
                );
            }
        }
    }

    private int calculateAmountToCraft(int p_362229_, boolean p_364254_) {
        if (this.useMaxItems) {
            return p_362229_;
        } else if (p_364254_) {
            int i = Integer.MAX_VALUE;

            for (Slot slot : this.inputGridSlots) {
                ItemStack itemstack = slot.getItem();
                if (!itemstack.isEmpty() && i > itemstack.getCount()) {
                    i = itemstack.getCount();
                }
            }

            if (i != Integer.MAX_VALUE) {
                i++;
            }

            return i;
        } else {
            return 1;
        }
    }

    private int moveItemToGrid(Slot p_135439_, Holder<Item> p_363932_, int p_342870_) {
        int i = this.inventory.findSlotMatchingCraftingIngredient(p_363932_);
        if (i == -1) {
            return -1;
        } else {
            ItemStack itemstack = this.inventory.getItem(i);
            int j;
            if (p_342870_ < itemstack.getCount()) {
                this.inventory.removeItem(i, p_342870_);
                j = p_342870_;
            } else {
                this.inventory.removeItemNoUpdate(i);
                j = itemstack.getCount();
            }

            if (p_135439_.getItem().isEmpty()) {
                p_135439_.set(itemstack.copyWithCount(j));
            } else {
                p_135439_.getItem().grow(j);
            }

            return p_342870_ - j;
        }
    }

    private boolean testClearGrid() {
        List<ItemStack> list = Lists.newArrayList();
        int i = this.getAmountOfFreeSlotsInInventory();

        for (Slot slot : this.inputGridSlots) {
            ItemStack itemstack = slot.getItem().copy();
            if (!itemstack.isEmpty()) {
                int j = this.inventory.getSlotWithRemainingSpace(itemstack);
                if (j == -1 && list.size() <= i) {
                    for (ItemStack itemstack1 : list) {
                        if (ItemStack.isSameItem(itemstack1, itemstack)
                            && itemstack1.getCount() != itemstack1.getMaxStackSize()
                            && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) {
                            itemstack1.grow(itemstack.getCount());
                            itemstack.setCount(0);
                            break;
                        }
                    }

                    if (!itemstack.isEmpty()) {
                        if (list.size() >= i) {
                            return false;
                        }

                        list.add(itemstack);
                    }
                } else if (j == -1) {
                    return false;
                }
            }
        }

        return true;
    }

    private int getAmountOfFreeSlotsInInventory() {
        int i = 0;

        for (ItemStack itemstack : this.inventory.items) {
            if (itemstack.isEmpty()) {
                i++;
            }
        }

        return i;
    }

    public interface CraftingMenuAccess<T extends Recipe<?>> {
        void fillCraftSlotsStackedContents(StackedItemContents p_362886_);

        void clearCraftingContent();

        boolean recipeMatches(RecipeHolder<T> p_361326_);
    }
}