package net.minecraft.world.item.equipment;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

public record EquipmentModel(Map<EquipmentModel.LayerType, List<EquipmentModel.Layer>> layers) {
    private static final Codec<List<EquipmentModel.Layer>> LAYER_LIST_CODEC = ExtraCodecs.nonEmptyList(EquipmentModel.Layer.CODEC.listOf());
    public static final Codec<EquipmentModel> CODEC = RecordCodecBuilder.create(
        p_361315_ -> p_361315_.group(
                    ExtraCodecs.nonEmptyMap(Codec.unboundedMap(EquipmentModel.LayerType.CODEC, LAYER_LIST_CODEC))
                        .fieldOf("layers")
                        .forGetter(EquipmentModel::layers)
                )
                .apply(p_361315_, EquipmentModel::new)
    );

    public static EquipmentModel.Builder builder() {
        return new EquipmentModel.Builder();
    }

    public List<EquipmentModel.Layer> getLayers(EquipmentModel.LayerType p_362280_) {
        return this.layers.getOrDefault(p_362280_, List.of());
    }

    public static class Builder {
        private final Map<EquipmentModel.LayerType, List<EquipmentModel.Layer>> layersByType = new EnumMap<>(EquipmentModel.LayerType.class);

        Builder() {
        }

        public EquipmentModel.Builder addHumanoidLayers(ResourceLocation p_360885_) {
            return this.addHumanoidLayers(p_360885_, false);
        }

        public EquipmentModel.Builder addHumanoidLayers(ResourceLocation p_361995_, boolean p_362299_) {
            this.addLayers(EquipmentModel.LayerType.HUMANOID_LEGGINGS, EquipmentModel.Layer.leatherDyeable(p_361995_, p_362299_));
            this.addMainHumanoidLayer(p_361995_, p_362299_);
            return this;
        }

        public EquipmentModel.Builder addMainHumanoidLayer(ResourceLocation p_368668_, boolean p_368120_) {
            return this.addLayers(EquipmentModel.LayerType.HUMANOID, EquipmentModel.Layer.leatherDyeable(p_368668_, p_368120_));
        }

        public EquipmentModel.Builder addLayers(EquipmentModel.LayerType p_368167_, EquipmentModel.Layer... p_367628_) {
            Collections.addAll(this.layersByType.computeIfAbsent(p_368167_, p_361693_ -> new ArrayList<>()), p_367628_);
            return this;
        }

        public EquipmentModel build() {
            return new EquipmentModel(
                this.layersByType.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, p_364450_ -> List.copyOf(p_364450_.getValue())))
            );
        }
    }

    public static record Dyeable(Optional<Integer> colorWhenUndyed) {
        public static final Codec<EquipmentModel.Dyeable> CODEC = RecordCodecBuilder.create(
            p_367134_ -> p_367134_.group(ExtraCodecs.RGB_COLOR_CODEC.optionalFieldOf("color_when_undyed").forGetter(EquipmentModel.Dyeable::colorWhenUndyed))
                    .apply(p_367134_, EquipmentModel.Dyeable::new)
        );
    }

    public static record Layer(ResourceLocation textureId, Optional<EquipmentModel.Dyeable> dyeable, boolean usePlayerTexture) {
        public static final Codec<EquipmentModel.Layer> CODEC = RecordCodecBuilder.create(
            p_368049_ -> p_368049_.group(
                        ResourceLocation.CODEC.fieldOf("texture").forGetter(EquipmentModel.Layer::textureId),
                        EquipmentModel.Dyeable.CODEC.optionalFieldOf("dyeable").forGetter(EquipmentModel.Layer::dyeable),
                        Codec.BOOL.optionalFieldOf("use_player_texture", Boolean.valueOf(false)).forGetter(EquipmentModel.Layer::usePlayerTexture)
                    )
                    .apply(p_368049_, EquipmentModel.Layer::new)
        );

        public Layer(ResourceLocation p_360819_) {
            this(p_360819_, Optional.empty(), false);
        }

        public static EquipmentModel.Layer leatherDyeable(ResourceLocation p_363878_, boolean p_362385_) {
            return new EquipmentModel.Layer(p_363878_, p_362385_ ? Optional.of(new EquipmentModel.Dyeable(Optional.of(-6265536))) : Optional.empty(), false);
        }

        public static EquipmentModel.Layer onlyIfDyed(ResourceLocation p_369738_, boolean p_364439_) {
            return new EquipmentModel.Layer(p_369738_, p_364439_ ? Optional.of(new EquipmentModel.Dyeable(Optional.empty())) : Optional.empty(), false);
        }

        public ResourceLocation getTextureLocation(EquipmentModel.LayerType p_367046_) {
            return this.textureId.withPath(p_367724_ -> "textures/entity/equipment/" + p_367046_.getSerializedName() + "/" + p_367724_ + ".png");
        }
    }

    public static enum LayerType implements StringRepresentable {
        HUMANOID("humanoid"),
        HUMANOID_LEGGINGS("humanoid_leggings"),
        WINGS("wings"),
        WOLF_BODY("wolf_body"),
        HORSE_BODY("horse_body"),
        LLAMA_BODY("llama_body");

        public static final Codec<EquipmentModel.LayerType> CODEC = StringRepresentable.fromEnum(EquipmentModel.LayerType::values);
        private final String id;

        private LayerType(final String p_362057_) {
            this.id = p_362057_;
        }

        @Override
        public String getSerializedName() {
            return this.id;
        }
    }
}