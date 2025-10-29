package net.minecraft.world.item.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.Item;

public class PlacementInfo {
    public static final PlacementInfo NOT_PLACEABLE = new PlacementInfo(List.of(), List.of(), List.of());
    private final List<Ingredient> ingredients;
    private final List<StackedContents.IngredientInfo<Holder<Item>>> unpackedIngredients;
    private final List<Optional<PlacementInfo.SlotInfo>> slotInfo;

    private PlacementInfo(
        List<Ingredient> p_365245_, List<StackedContents.IngredientInfo<Holder<Item>>> p_369660_, List<Optional<PlacementInfo.SlotInfo>> p_360891_
    ) {
        this.ingredients = p_365245_;
        this.unpackedIngredients = p_369660_;
        this.slotInfo = p_360891_;
    }

    public static StackedContents.IngredientInfo<Holder<Item>> ingredientToContents(Ingredient p_362616_) {
        return StackedItemContents.convertIngredientContents(p_362616_.items().stream());
    }

    public static PlacementInfo create(Ingredient p_361591_) {
        if (p_361591_.items().isEmpty()) {
            return NOT_PLACEABLE;
        } else {
            StackedContents.IngredientInfo<Holder<Item>> ingredientinfo = ingredientToContents(p_361591_);
            PlacementInfo.SlotInfo placementinfo$slotinfo = new PlacementInfo.SlotInfo(0);
            return new PlacementInfo(List.of(p_361591_), List.of(ingredientinfo), List.of(Optional.of(placementinfo$slotinfo)));
        }
    }

    public static PlacementInfo createFromOptionals(List<Optional<Ingredient>> p_362899_) {
        int i = p_362899_.size();
        List<Ingredient> list = new ArrayList<>(i);
        List<StackedContents.IngredientInfo<Holder<Item>>> list1 = new ArrayList<>(i);
        List<Optional<PlacementInfo.SlotInfo>> list2 = new ArrayList<>(i);
        int j = 0;

        for (Optional<Ingredient> optional : p_362899_) {
            if (optional.isPresent()) {
                Ingredient ingredient = optional.get();
                if (ingredient.items().isEmpty()) {
                    return NOT_PLACEABLE;
                }

                list.add(ingredient);
                list1.add(ingredientToContents(ingredient));
                list2.add(Optional.of(new PlacementInfo.SlotInfo(j++)));
            } else {
                list2.add(Optional.empty());
            }
        }

        return new PlacementInfo(list, list1, list2);
    }

    public static PlacementInfo create(List<Ingredient> p_366350_) {
        int i = p_366350_.size();
        List<StackedContents.IngredientInfo<Holder<Item>>> list = new ArrayList<>(i);
        List<Optional<PlacementInfo.SlotInfo>> list1 = new ArrayList<>(i);

        for (int j = 0; j < i; j++) {
            Ingredient ingredient = p_366350_.get(j);
            if (ingredient.items().isEmpty()) {
                return NOT_PLACEABLE;
            }

            list.add(ingredientToContents(ingredient));
            list1.add(Optional.of(new PlacementInfo.SlotInfo(j)));
        }

        return new PlacementInfo(p_366350_, list, list1);
    }

    public List<Optional<PlacementInfo.SlotInfo>> slotInfo() {
        return this.slotInfo;
    }

    public List<Ingredient> ingredients() {
        return this.ingredients;
    }

    public List<StackedContents.IngredientInfo<Holder<Item>>> unpackedIngredients() {
        return this.unpackedIngredients;
    }

    public boolean isImpossibleToPlace() {
        return this.slotInfo.isEmpty();
    }

    public static record SlotInfo(int placerOutputPosition) {
    }
}