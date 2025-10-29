package net.minecraft.client.gui.screens.recipebook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeCollection {
    private final List<RecipeDisplayEntry> entries;
    private final boolean singleResultItem;
    private final Set<RecipeDisplayId> craftable = new HashSet<>();
    private final Set<RecipeDisplayId> selected = new HashSet<>();

    public RecipeCollection(List<RecipeDisplayEntry> p_267051_) {
        this.entries = p_267051_;
        if (p_267051_.size() <= 1) {
            this.singleResultItem = true;
        } else {
            this.singleResultItem = allRecipesHaveSameResult(this.entries);
        }
    }

    private static boolean allRecipesHaveSameResult(List<RecipeDisplayEntry> p_100509_) {
        int i = p_100509_.size();
        SlotDisplay slotdisplay = p_100509_.getFirst().display().result();

        for (int j = 1; j < i; j++) {
            SlotDisplay slotdisplay1 = p_100509_.get(j).display().result();
            if (!slotdisplay1.equals(slotdisplay)) {
                return false;
            }
        }

        return true;
    }

    public void selectRecipes(StackedItemContents p_361916_, Predicate<RecipeDisplay> p_365877_) {
        for (RecipeDisplayEntry recipedisplayentry : this.entries) {
            boolean flag = p_365877_.test(recipedisplayentry.display());
            if (flag) {
                this.selected.add(recipedisplayentry.id());
            } else {
                this.selected.remove(recipedisplayentry.id());
            }

            if (flag && recipedisplayentry.canCraft(p_361916_)) {
                this.craftable.add(recipedisplayentry.id());
            } else {
                this.craftable.remove(recipedisplayentry.id());
            }
        }
    }

    public boolean isCraftable(RecipeDisplayId p_366818_) {
        return this.craftable.contains(p_366818_);
    }

    public boolean hasCraftable() {
        return !this.craftable.isEmpty();
    }

    public boolean hasAnySelected() {
        return !this.selected.isEmpty();
    }

    public List<RecipeDisplayEntry> getRecipes() {
        return this.entries;
    }

    public List<RecipeDisplayEntry> getSelectedRecipes(RecipeCollection.CraftableStatus p_369775_) {
        Predicate<RecipeDisplayId> predicate = switch (p_369775_) {
            case ANY -> this.selected::contains;
            case CRAFTABLE -> this.craftable::contains;
            case NOT_CRAFTABLE -> p_361783_ -> this.selected.contains(p_361783_) && !this.craftable.contains(p_361783_);
        };
        List<RecipeDisplayEntry> list = new ArrayList<>();

        for (RecipeDisplayEntry recipedisplayentry : this.entries) {
            if (predicate.test(recipedisplayentry.id())) {
                list.add(recipedisplayentry);
            }
        }

        return list;
    }

    public boolean hasSingleResultItem() {
        return this.singleResultItem;
    }

    @OnlyIn(Dist.CLIENT)
    public static enum CraftableStatus {
        ANY,
        CRAFTABLE,
        NOT_CRAFTABLE;
    }
}