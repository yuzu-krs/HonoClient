package net.minecraft.client.resources.model;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BundleItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ModelDiscovery {
    static final Logger LOGGER = LogUtils.getLogger();
    public static final String INVENTORY_MODEL_PREFIX = "item/";
    private final Map<ResourceLocation, UnbakedModel> inputModels;
    final UnbakedModel missingModel;
    private final Map<ModelResourceLocation, UnbakedModel> topModels = new HashMap<>();
    private final Map<ResourceLocation, UnbakedModel> referencedModels = new HashMap<>();

    public ModelDiscovery(Map<ResourceLocation, UnbakedModel> p_362964_, UnbakedModel p_367385_) {
        this.inputModels = p_362964_;
        this.missingModel = p_367385_;
        this.registerTopModel(MissingBlockModel.VARIANT, p_367385_);
        this.referencedModels.put(MissingBlockModel.LOCATION, p_367385_);
    }

    private static Set<ModelResourceLocation> listMandatoryModels() {
        Set<ModelResourceLocation> set = new HashSet<>();
        BuiltInRegistries.ITEM.listElements().forEach(p_368638_ -> {
            ResourceLocation resourcelocation = p_368638_.value().components().get(DataComponents.ITEM_MODEL);
            if (resourcelocation != null) {
                set.add(ModelResourceLocation.inventory(resourcelocation));
            }

            if (p_368638_.value() instanceof BundleItem bundleitem) {
                set.add(ModelResourceLocation.inventory(bundleitem.openFrontModel()));
                set.add(ModelResourceLocation.inventory(bundleitem.openBackModel()));
            }
        });
        set.add(ItemRenderer.TRIDENT_MODEL);
        set.add(ItemRenderer.SPYGLASS_MODEL);
        return set;
    }

    private void registerTopModel(ModelResourceLocation p_360834_, UnbakedModel p_367173_) {
        this.topModels.put(p_360834_, p_367173_);
    }

    public void registerStandardModels(BlockStateModelLoader.LoadedModels p_361458_) {
        this.referencedModels.put(SpecialModels.BUILTIN_GENERATED, SpecialModels.GENERATED_MARKER);
        this.referencedModels.put(SpecialModels.BUILTIN_BLOCK_ENTITY, SpecialModels.BLOCK_ENTITY_MARKER);
        Set<ModelResourceLocation> set = listMandatoryModels();
        p_361458_.models().forEach((p_366538_, p_363254_) -> {
            this.registerTopModel(p_366538_, p_363254_.model());
            set.remove(p_366538_);
        });
        this.inputModels
            .keySet()
            .forEach(
                p_369814_ -> {
                    if (p_369814_.getPath().startsWith("item/")) {
                        ModelResourceLocation modelresourcelocation = ModelResourceLocation.inventory(
                            p_369814_.withPath(p_366668_ -> p_366668_.substring("item/".length()))
                        );
                        this.registerTopModel(modelresourcelocation, new ItemModel(p_369814_));
                        set.remove(modelresourcelocation);
                    }
                }
            );
        if (!set.isEmpty()) {
            LOGGER.warn("Missing mandatory models: {}", set.stream().map(p_362069_ -> "\n\t" + p_362069_).collect(Collectors.joining()));
        }
    }

    public void discoverDependencies() {
        this.topModels.values().forEach(p_367838_ -> p_367838_.resolveDependencies(new ModelDiscovery.ResolverImpl()));
    }

    public Map<ModelResourceLocation, UnbakedModel> getTopModels() {
        return this.topModels;
    }

    public Map<ResourceLocation, UnbakedModel> getReferencedModels() {
        return this.referencedModels;
    }

    UnbakedModel getBlockModel(ResourceLocation p_363667_) {
        return this.referencedModels.computeIfAbsent(p_363667_, this::loadBlockModel);
    }

    private UnbakedModel loadBlockModel(ResourceLocation p_368910_) {
        UnbakedModel unbakedmodel = this.inputModels.get(p_368910_);
        if (unbakedmodel == null) {
            LOGGER.warn("Missing block model: '{}'", p_368910_);
            return this.missingModel;
        } else {
            return unbakedmodel;
        }
    }

    @OnlyIn(Dist.CLIENT)
    class ResolverImpl implements UnbakedModel.Resolver {
        private final List<ResourceLocation> stack = new ArrayList<>();
        private final Set<ResourceLocation> resolvedModels = new HashSet<>();

        @Override
        public UnbakedModel resolve(ResourceLocation p_360973_) {
            if (this.stack.contains(p_360973_)) {
                ModelDiscovery.LOGGER.warn("Detected model loading loop: {}->{}", this.stacktraceToString(), p_360973_);
                return ModelDiscovery.this.missingModel;
            } else {
                UnbakedModel unbakedmodel = ModelDiscovery.this.getBlockModel(p_360973_);
                if (this.resolvedModels.add(p_360973_)) {
                    this.stack.add(p_360973_);
                    unbakedmodel.resolveDependencies(this);
                    this.stack.remove(p_360973_);
                }

                return unbakedmodel;
            }
        }

        private String stacktraceToString() {
            return this.stack.stream().map(ResourceLocation::toString).collect(Collectors.joining("->"));
        }
    }
}