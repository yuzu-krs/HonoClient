package net.minecraft.client.gui.screens.recipebook;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeButton extends AbstractWidget {
    private static final ResourceLocation SLOT_MANY_CRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_many_craftable");
    private static final ResourceLocation SLOT_CRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_craftable");
    private static final ResourceLocation SLOT_MANY_UNCRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_many_uncraftable");
    private static final ResourceLocation SLOT_UNCRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_uncraftable");
    private static final float ANIMATION_TIME = 15.0F;
    private static final int BACKGROUND_SIZE = 25;
    private static final Component MORE_RECIPES_TOOLTIP = Component.translatable("gui.recipebook.moreRecipes");
    private RecipeCollection collection;
    private List<RecipeButton.ResolvedEntry> selectedEntries = List.of();
    private final SlotSelectTime slotSelectTime;
    private float animationTime;

    public RecipeButton(SlotSelectTime p_361785_) {
        super(0, 0, 25, 25, CommonComponents.EMPTY);
        this.slotSelectTime = p_361785_;
    }

    public void init(RecipeCollection p_100480_, boolean p_363893_, RecipeBookPage p_100481_, ContextMap p_364354_) {
        this.collection = p_100480_;
        List<RecipeDisplayEntry> list = p_100480_.getSelectedRecipes(p_363893_ ? RecipeCollection.CraftableStatus.CRAFTABLE : RecipeCollection.CraftableStatus.ANY);
        this.selectedEntries = list.stream().map(p_367596_ -> new RecipeButton.ResolvedEntry(p_367596_.id(), p_367596_.resultItems(p_364354_))).toList();
        List<RecipeDisplayId> list1 = list.stream().map(RecipeDisplayEntry::id).filter(p_100481_.getRecipeBook()::willHighlight).toList();
        if (!list1.isEmpty()) {
            list1.forEach(p_100481_::recipeShown);
            this.animationTime = 15.0F;
        }
    }

    public RecipeCollection getCollection() {
        return this.collection;
    }

    @Override
    public void renderWidget(GuiGraphics p_281385_, int p_282779_, int p_282744_, float p_282439_) {
        ResourceLocation resourcelocation;
        if (this.collection.hasCraftable()) {
            if (this.hasMultipleRecipes()) {
                resourcelocation = SLOT_MANY_CRAFTABLE_SPRITE;
            } else {
                resourcelocation = SLOT_CRAFTABLE_SPRITE;
            }
        } else if (this.hasMultipleRecipes()) {
            resourcelocation = SLOT_MANY_UNCRAFTABLE_SPRITE;
        } else {
            resourcelocation = SLOT_UNCRAFTABLE_SPRITE;
        }

        boolean flag = this.animationTime > 0.0F;
        if (flag) {
            float f = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * (float) Math.PI));
            p_281385_.pose().pushPose();
            p_281385_.pose().translate((float)(this.getX() + 8), (float)(this.getY() + 12), 0.0F);
            p_281385_.pose().scale(f, f, 1.0F);
            p_281385_.pose().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)), 0.0F);
            this.animationTime -= p_282439_;
        }

        p_281385_.blitSprite(RenderType::guiTextured, resourcelocation, this.getX(), this.getY(), this.width, this.height);
        ItemStack itemstack = this.getDisplayStack();
        int i = 4;
        if (this.collection.hasSingleResultItem() && this.hasMultipleRecipes()) {
            p_281385_.renderItem(itemstack, this.getX() + i + 1, this.getY() + i + 1, 0, 10);
            i--;
        }

        p_281385_.renderFakeItem(itemstack, this.getX() + i, this.getY() + i);
        if (flag) {
            p_281385_.pose().popPose();
        }
    }

    private boolean hasMultipleRecipes() {
        return this.selectedEntries.size() > 1;
    }

    public boolean isOnlyOption() {
        return this.selectedEntries.size() == 1;
    }

    public RecipeDisplayId getCurrentRecipe() {
        int i = this.slotSelectTime.currentIndex() % this.selectedEntries.size();
        return this.selectedEntries.get(i).id;
    }

    public ItemStack getDisplayStack() {
        int i = this.slotSelectTime.currentIndex();
        int j = this.selectedEntries.size();
        int k = i / j;
        int l = i - j * k;
        return this.selectedEntries.get(l).selectItem(k);
    }

    public List<Component> getTooltipText(ItemStack p_363067_) {
        List<Component> list = new ArrayList<>(Screen.getTooltipFromItem(Minecraft.getInstance(), p_363067_));
        if (this.hasMultipleRecipes()) {
            list.add(MORE_RECIPES_TOOLTIP);
        }

        return list;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput p_170060_) {
        p_170060_.add(NarratedElementType.TITLE, Component.translatable("narration.recipe", this.getDisplayStack().getHoverName()));
        if (this.hasMultipleRecipes()) {
            p_170060_.add(
                NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"), Component.translatable("narration.recipe.usage.more")
            );
        } else {
            p_170060_.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
        }
    }

    @Override
    public int getWidth() {
        return 25;
    }

    @Override
    protected boolean isValidClickButton(int p_100473_) {
        return p_100473_ == 0 || p_100473_ == 1;
    }

    @OnlyIn(Dist.CLIENT)
    static record ResolvedEntry(RecipeDisplayId id, List<ItemStack> displayItems) {
        public ItemStack selectItem(int p_361103_) {
            if (this.displayItems.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                int i = p_361103_ % this.displayItems.size();
                return this.displayItems.get(i);
            }
        }
    }
}