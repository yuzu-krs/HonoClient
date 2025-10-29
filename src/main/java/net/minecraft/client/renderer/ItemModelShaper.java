package net.minecraft.client.renderer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemModelShaper {
    private final Map<ResourceLocation, BakedModel> modelToBakedModel = new HashMap<>();
    private final Supplier<BakedModel> missingModel;
    private final Function<ResourceLocation, BakedModel> modelGetter;

    public ItemModelShaper(ModelManager p_109392_) {
        this.missingModel = p_109392_::getMissingModel;
        this.modelGetter = p_364613_ -> p_109392_.getModel(ModelResourceLocation.inventory(p_364613_));
    }

    public BakedModel getItemModel(ItemStack p_109407_) {
        ResourceLocation resourcelocation = p_109407_.get(DataComponents.ITEM_MODEL);
        return resourcelocation == null ? this.missingModel.get() : this.getItemModel(resourcelocation);
    }

    public BakedModel getItemModel(ResourceLocation p_361977_) {
        return this.modelToBakedModel.computeIfAbsent(p_361977_, this.modelGetter);
    }

    public void invalidateCache() {
        this.modelToBakedModel.clear();
    }
}