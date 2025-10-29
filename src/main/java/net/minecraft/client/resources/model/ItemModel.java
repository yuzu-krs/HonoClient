package net.minecraft.client.resources.model;

import java.util.List;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.BakedOverrides;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemModel implements UnbakedModel {
    private final ResourceLocation id;
    private List<ItemOverride> overrides = List.of();

    public ItemModel(ResourceLocation p_366876_) {
        this.id = p_366876_;
    }

    @Override
    public void resolveDependencies(UnbakedModel.Resolver p_369598_) {
        if (p_369598_.resolve(this.id) instanceof BlockModel blockmodel) {
            this.overrides = blockmodel.getOverrides();
            this.overrides.forEach(p_366982_ -> p_369598_.resolve(p_366982_.model()));
        }
    }

    @Override
    public BakedModel bake(ModelBaker p_369488_, Function<Material, TextureAtlasSprite> p_363575_, ModelState p_361246_) {
        BakedModel bakedmodel = p_369488_.bake(this.id, p_361246_);
        if (this.overrides.isEmpty()) {
            return bakedmodel;
        } else {
            BakedOverrides bakedoverrides = new BakedOverrides(p_369488_, this.overrides);
            return new ItemModel.BakedModelWithOverrides(bakedmodel, bakedoverrides);
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class BakedModelWithOverrides extends DelegateBakedModel {
        private final BakedOverrides overrides;

        public BakedModelWithOverrides(BakedModel p_370182_, BakedOverrides p_361578_) {
            super(p_370182_);
            this.overrides = p_361578_;
        }

        @Override
        public BakedOverrides overrides() {
            return this.overrides;
        }
    }
}