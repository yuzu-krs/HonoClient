package net.minecraft.client.resources.model;

import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ModelBakery {
    public static final Material FIRE_0 = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/fire_0"));
    public static final Material FIRE_1 = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/fire_1"));
    public static final Material LAVA_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/lava_flow"));
    public static final Material WATER_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/water_flow"));
    public static final Material WATER_OVERLAY = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/water_overlay"));
    public static final Material BANNER_BASE = new Material(Sheets.BANNER_SHEET, ResourceLocation.withDefaultNamespace("entity/banner_base"));
    public static final Material SHIELD_BASE = new Material(Sheets.SHIELD_SHEET, ResourceLocation.withDefaultNamespace("entity/shield_base"));
    public static final Material NO_PATTERN_SHIELD = new Material(Sheets.SHIELD_SHEET, ResourceLocation.withDefaultNamespace("entity/shield_base_nopattern"));
    public static final int DESTROY_STAGE_COUNT = 10;
    public static final List<ResourceLocation> DESTROY_STAGES = IntStream.range(0, 10)
        .mapToObj(p_340955_ -> ResourceLocation.withDefaultNamespace("block/destroy_stage_" + p_340955_))
        .collect(Collectors.toList());
    public static final List<ResourceLocation> BREAKING_LOCATIONS = DESTROY_STAGES.stream()
        .map(p_340960_ -> p_340960_.withPath(p_340956_ -> "textures/" + p_340956_ + ".png"))
        .collect(Collectors.toList());
    public static final List<RenderType> DESTROY_TYPES = BREAKING_LOCATIONS.stream().map(RenderType::crumbling).collect(Collectors.toList());
    static final Logger LOGGER = LogUtils.getLogger();
    static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
    final Map<ModelBakery.BakedCacheKey, BakedModel> bakedCache = new HashMap<>();
    private final Map<ModelResourceLocation, BakedModel> bakedTopLevelModels = new HashMap<>();
    private final Map<ModelResourceLocation, UnbakedModel> topModels;
    final Map<ResourceLocation, UnbakedModel> unbakedModels;
    final UnbakedModel missingModel;

    public ModelBakery(Map<ModelResourceLocation, UnbakedModel> p_251087_, Map<ResourceLocation, UnbakedModel> p_250416_, UnbakedModel p_361482_) {
        this.topModels = p_251087_;
        this.unbakedModels = p_250416_;
        this.missingModel = p_361482_;
    }

    public void bakeModels(ModelBakery.TextureGetter p_343407_) {
        this.topModels.forEach((p_340958_, p_340959_) -> {
            BakedModel bakedmodel = null;

            try {
                bakedmodel = new ModelBakery.ModelBakerImpl(p_343407_, p_340958_).bakeUncached(p_340959_, BlockModelRotation.X0_Y0);
            } catch (Exception exception) {
                LOGGER.warn("Unable to bake model: '{}': {}", p_340958_, exception);
            }

            if (bakedmodel != null) {
                this.bakedTopLevelModels.put(p_340958_, bakedmodel);
            }
        });
    }

    public Map<ModelResourceLocation, BakedModel> getBakedTopLevelModels() {
        return this.bakedTopLevelModels;
    }

    @OnlyIn(Dist.CLIENT)
    static record BakedCacheKey(ResourceLocation id, Transformation transformation, boolean isUvLocked) {
    }

    @OnlyIn(Dist.CLIENT)
    class ModelBakerImpl implements ModelBaker {
        private final Function<Material, TextureAtlasSprite> modelTextureGetter;

        ModelBakerImpl(final ModelBakery.TextureGetter p_342310_, final ModelResourceLocation p_344289_) {
            this.modelTextureGetter = p_340963_ -> p_342310_.get(p_344289_, p_340963_);
        }

        private UnbakedModel getModel(ResourceLocation p_248568_) {
            UnbakedModel unbakedmodel = ModelBakery.this.unbakedModels.get(p_248568_);
            if (unbakedmodel == null) {
                ModelBakery.LOGGER.warn("Requested a model that was not discovered previously: {}", p_248568_);
                return ModelBakery.this.missingModel;
            } else {
                return unbakedmodel;
            }
        }

        @Override
        public BakedModel bake(ResourceLocation p_252176_, ModelState p_249765_) {
            ModelBakery.BakedCacheKey modelbakery$bakedcachekey = new ModelBakery.BakedCacheKey(p_252176_, p_249765_.getRotation(), p_249765_.isUvLocked());
            BakedModel bakedmodel = ModelBakery.this.bakedCache.get(modelbakery$bakedcachekey);
            if (bakedmodel != null) {
                return bakedmodel;
            } else {
                UnbakedModel unbakedmodel = this.getModel(p_252176_);
                BakedModel bakedmodel1 = this.bakeUncached(unbakedmodel, p_249765_);
                ModelBakery.this.bakedCache.put(modelbakery$bakedcachekey, bakedmodel1);
                return bakedmodel1;
            }
        }

        BakedModel bakeUncached(UnbakedModel p_343761_, ModelState p_342939_) {
            if (p_343761_ instanceof BlockModel blockmodel && blockmodel.getRootModel() == SpecialModels.GENERATED_MARKER) {
                return ModelBakery.ITEM_MODEL_GENERATOR.generateBlockModel(this.modelTextureGetter, blockmodel).bake(this.modelTextureGetter, p_342939_, false);
            }

            return p_343761_.bake(this, this.modelTextureGetter, p_342939_);
        }
    }

    @FunctionalInterface
    @OnlyIn(Dist.CLIENT)
    public interface TextureGetter {
        TextureAtlasSprite get(ModelResourceLocation p_343839_, Material p_345409_);
    }
}