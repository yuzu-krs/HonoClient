package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

public class TransmuteRecipe implements CraftingRecipe {
    final String group;
    final CraftingBookCategory category;
    final Ingredient input;
    final Ingredient material;
    final Holder<Item> result;
    @Nullable
    private PlacementInfo placementInfo;

    public TransmuteRecipe(String p_365892_, CraftingBookCategory p_365216_, Ingredient p_361778_, Ingredient p_366676_, Holder<Item> p_368172_) {
        this.group = p_365892_;
        this.category = p_365216_;
        this.input = p_361778_;
        this.material = p_366676_;
        this.result = p_368172_;
    }

    public boolean matches(CraftingInput p_362474_, Level p_361244_) {
        if (p_362474_.ingredientCount() != 2) {
            return false;
        } else {
            boolean flag = false;
            boolean flag1 = false;

            for (int i = 0; i < p_362474_.size(); i++) {
                ItemStack itemstack = p_362474_.getItem(i);
                if (!itemstack.isEmpty()) {
                    if (!flag && this.input.test(itemstack) && itemstack.getItem() != this.result.value()) {
                        flag = true;
                    } else {
                        if (flag1 || !this.material.test(itemstack)) {
                            return false;
                        }

                        flag1 = true;
                    }
                }
            }

            return flag && flag1;
        }
    }

    public ItemStack assemble(CraftingInput p_364916_, HolderLookup.Provider p_369797_) {
        ItemStack itemstack = ItemStack.EMPTY;

        for (int i = 0; i < p_364916_.size(); i++) {
            ItemStack itemstack1 = p_364916_.getItem(i);
            if (!itemstack1.isEmpty() && this.input.test(itemstack1) && itemstack1.getItem() != this.result.value()) {
                itemstack = itemstack1;
            }
        }

        return itemstack.transmuteCopy(this.result.value(), 1);
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(
            new ShapelessCraftingRecipeDisplay(
                List.of(this.input.display(), this.material.display()),
                new SlotDisplay.ItemSlotDisplay(this.result),
                new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
            )
        );
    }

    @Override
    public RecipeSerializer<TransmuteRecipe> getSerializer() {
        return RecipeSerializer.TRANSMUTE;
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(List.of(this.input, this.material));
        }

        return this.placementInfo;
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }

    public static class Serializer implements RecipeSerializer<TransmuteRecipe> {
        private static final MapCodec<TransmuteRecipe> CODEC = RecordCodecBuilder.mapCodec(
            p_365756_ -> p_365756_.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(p_367782_ -> p_367782_.group),
                        CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(p_362283_ -> p_362283_.category),
                        Ingredient.CODEC.fieldOf("input").forGetter(p_361424_ -> p_361424_.input),
                        Ingredient.CODEC.fieldOf("material").forGetter(p_360991_ -> p_360991_.material),
                        Item.CODEC.fieldOf("result").forGetter(p_367062_ -> p_367062_.result)
                    )
                    .apply(p_365756_, TransmuteRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, TransmuteRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            p_364322_ -> p_364322_.group,
            CraftingBookCategory.STREAM_CODEC,
            p_369020_ -> p_369020_.category,
            Ingredient.CONTENTS_STREAM_CODEC,
            p_363236_ -> p_363236_.input,
            Ingredient.CONTENTS_STREAM_CODEC,
            p_365202_ -> p_365202_.material,
            ByteBufCodecs.holderRegistry(Registries.ITEM),
            p_366221_ -> p_366221_.result,
            TransmuteRecipe::new
        );

        @Override
        public MapCodec<TransmuteRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TransmuteRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}