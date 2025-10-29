package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record MultiVariant(List<Variant> variants) implements UnbakedBlockStateModel {
    public MultiVariant(List<Variant> variants) {
        if (variants.isEmpty()) {
            throw new IllegalArgumentException("Variant list must contain at least one element");
        } else {
            this.variants = variants;
        }
    }

    @Override
    public Object visualEqualityGroup(BlockState p_364633_) {
        return this;
    }

    @Override
    public void resolveDependencies(UnbakedModel.Resolver p_369727_) {
        this.variants.forEach(p_357929_ -> p_369727_.resolve(p_357929_.getModelLocation()));
    }

    @Override
    public BakedModel bake(ModelBaker p_249016_, Function<Material, TextureAtlasSprite> p_111851_, ModelState p_111852_) {
        if (this.variants.size() == 1) {
            Variant variant1 = this.variants.getFirst();
            return p_249016_.bake(variant1.getModelLocation(), variant1);
        } else {
            SimpleWeightedRandomList.Builder<BakedModel> builder = SimpleWeightedRandomList.builder();

            for (Variant variant : this.variants) {
                BakedModel bakedmodel = p_249016_.bake(variant.getModelLocation(), variant);
                builder.add(bakedmodel, variant.getWeight());
            }

            return new WeightedBakedModel(builder.build());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Deserializer implements JsonDeserializer<MultiVariant> {
        public MultiVariant deserialize(JsonElement p_111867_, Type p_111868_, JsonDeserializationContext p_111869_) throws JsonParseException {
            List<Variant> list = Lists.newArrayList();
            if (p_111867_.isJsonArray()) {
                JsonArray jsonarray = p_111867_.getAsJsonArray();
                if (jsonarray.isEmpty()) {
                    throw new JsonParseException("Empty variant array");
                }

                for (JsonElement jsonelement : jsonarray) {
                    list.add(p_111869_.deserialize(jsonelement, Variant.class));
                }
            } else {
                list.add(p_111869_.deserialize(p_111867_, Variant.class));
            }

            return new MultiVariant(list);
        }
    }
}