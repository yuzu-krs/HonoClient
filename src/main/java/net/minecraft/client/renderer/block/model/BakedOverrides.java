package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BakedOverrides {
    public static final BakedOverrides EMPTY = new BakedOverrides();
    public static final float NO_OVERRIDE = Float.NEGATIVE_INFINITY;
    private final BakedOverrides.BakedOverride[] overrides;
    private final ResourceLocation[] properties;

    private BakedOverrides() {
        this.overrides = new BakedOverrides.BakedOverride[0];
        this.properties = new ResourceLocation[0];
    }

    public BakedOverrides(ModelBaker p_369157_, List<ItemOverride> p_368857_) {
        this.properties = p_368857_.stream()
            .flatMap(p_361461_ -> p_361461_.predicates().stream())
            .map(ItemOverride.Predicate::property)
            .distinct()
            .toArray(ResourceLocation[]::new);
        Object2IntMap<ResourceLocation> object2intmap = new Object2IntOpenHashMap<>();

        for (int i = 0; i < this.properties.length; i++) {
            object2intmap.put(this.properties[i], i);
        }

        List<BakedOverrides.BakedOverride> list = Lists.newArrayList();

        for (int j = p_368857_.size() - 1; j >= 0; j--) {
            ItemOverride itemoverride = p_368857_.get(j);
            BakedModel bakedmodel = p_369157_.bake(itemoverride.model(), BlockModelRotation.X0_Y0);
            BakedOverrides.PropertyMatcher[] abakedoverrides$propertymatcher = itemoverride.predicates().stream().map(p_367339_ -> {
                int k = object2intmap.getInt(p_367339_.property());
                return new BakedOverrides.PropertyMatcher(k, p_367339_.value());
            }).toArray(BakedOverrides.PropertyMatcher[]::new);
            list.add(new BakedOverrides.BakedOverride(abakedoverrides$propertymatcher, bakedmodel));
        }

        this.overrides = list.toArray(new BakedOverrides.BakedOverride[0]);
    }

    @Nullable
    public BakedModel findOverride(ItemStack p_368585_, @Nullable ClientLevel p_364326_, @Nullable LivingEntity p_363063_, int p_363414_) {
        int i = this.properties.length;
        if (i != 0) {
            float[] afloat = new float[i];

            for (int j = 0; j < i; j++) {
                ResourceLocation resourcelocation = this.properties[j];
                ItemPropertyFunction itempropertyfunction = ItemProperties.getProperty(p_368585_, resourcelocation);
                if (itempropertyfunction != null) {
                    afloat[j] = itempropertyfunction.call(p_368585_, p_364326_, p_363063_, p_363414_);
                } else {
                    afloat[j] = Float.NEGATIVE_INFINITY;
                }
            }

            for (BakedOverrides.BakedOverride bakedoverrides$bakedoverride : this.overrides) {
                if (bakedoverrides$bakedoverride.test(afloat)) {
                    return bakedoverrides$bakedoverride.model;
                }
            }
        }

        return null;
    }

    @OnlyIn(Dist.CLIENT)
    static record BakedOverride(BakedOverrides.PropertyMatcher[] matchers, @Nullable BakedModel model) {
        boolean test(float[] p_366284_) {
            for (BakedOverrides.PropertyMatcher bakedoverrides$propertymatcher : this.matchers) {
                float f = p_366284_[bakedoverrides$propertymatcher.index];
                if (f < bakedoverrides$propertymatcher.value) {
                    return false;
                }
            }

            return true;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static record PropertyMatcher(int index, float value) {
    }
}