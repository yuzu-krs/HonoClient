package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.EquipmentModelSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.EquipmentModel;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EquipmentLayerRenderer {
    private static final int NO_LAYER_COLOR = 0;
    private final EquipmentModelSet equipmentModels;
    private final Function<EquipmentLayerRenderer.LayerTextureKey, ResourceLocation> layerTextureLookup;
    private final Function<EquipmentLayerRenderer.TrimSpriteKey, TextureAtlasSprite> trimSpriteLookup;

    public EquipmentLayerRenderer(EquipmentModelSet p_365849_, TextureAtlas p_363154_) {
        this.equipmentModels = p_365849_;
        this.layerTextureLookup = Util.memoize(p_365709_ -> p_365709_.layer.getTextureLocation(p_365709_.layerType));
        this.trimSpriteLookup = Util.memoize(p_361661_ -> {
            ResourceLocation resourcelocation = p_361661_.trim.getTexture(p_361661_.layerType, p_361661_.equipmentModelId);
            return p_363154_.getSprite(resourcelocation);
        });
    }

    public void renderLayers(
        EquipmentModel.LayerType p_361529_,
        ResourceLocation p_367833_,
        Model p_366052_,
        ItemStack p_368999_,
        PoseStack p_366797_,
        MultiBufferSource p_367071_,
        int p_365571_
    ) {
        this.renderLayers(p_361529_, p_367833_, p_366052_, p_368999_, p_366797_, p_367071_, p_365571_, null);
    }

    public void renderLayers(
        EquipmentModel.LayerType p_361602_,
        ResourceLocation p_363056_,
        Model p_366813_,
        ItemStack p_363462_,
        PoseStack p_361892_,
        MultiBufferSource p_369133_,
        int p_367241_,
        @Nullable ResourceLocation p_366172_
    ) {
        List<EquipmentModel.Layer> list = this.equipmentModels.get(p_363056_).getLayers(p_361602_);
        if (!list.isEmpty()) {
            int i = p_363462_.is(ItemTags.DYEABLE) ? DyedItemColor.getOrDefault(p_363462_, 0) : 0;
            boolean flag = p_363462_.hasFoil();

            for (EquipmentModel.Layer equipmentmodel$layer : list) {
                int j = getColorForLayer(equipmentmodel$layer, i);
                if (j != 0) {
                    ResourceLocation resourcelocation = equipmentmodel$layer.usePlayerTexture() && p_366172_ != null
                        ? p_366172_
                        : this.layerTextureLookup.apply(new EquipmentLayerRenderer.LayerTextureKey(p_361602_, equipmentmodel$layer));
                    VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(p_369133_, RenderType.armorCutoutNoCull(resourcelocation), flag);
                    p_366813_.renderToBuffer(p_361892_, vertexconsumer, p_367241_, OverlayTexture.NO_OVERLAY, j);
                    flag = false;
                }
            }

            ArmorTrim armortrim = p_363462_.get(DataComponents.TRIM);
            if (armortrim != null) {
                TextureAtlasSprite textureatlassprite = this.trimSpriteLookup.apply(new EquipmentLayerRenderer.TrimSpriteKey(armortrim, p_361602_, p_363056_));
                VertexConsumer vertexconsumer1 = textureatlassprite.wrap(
                    p_369133_.getBuffer(Sheets.armorTrimsSheet(armortrim.pattern().value().decal()))
                );
                p_366813_.renderToBuffer(p_361892_, vertexconsumer1, p_367241_, OverlayTexture.NO_OVERLAY);
            }
        }
    }

    private static int getColorForLayer(EquipmentModel.Layer p_361320_, int p_365160_) {
        Optional<EquipmentModel.Dyeable> optional = p_361320_.dyeable();
        if (optional.isPresent()) {
            int i = optional.get().colorWhenUndyed().map(ARGB::opaque).orElse(0);
            return p_365160_ != 0 ? p_365160_ : i;
        } else {
            return -1;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static record LayerTextureKey(EquipmentModel.LayerType layerType, EquipmentModel.Layer layer) {
    }

    @OnlyIn(Dist.CLIENT)
    static record TrimSpriteKey(ArmorTrim trim, EquipmentModel.LayerType layerType, ResourceLocation equipmentModelId) {
    }
}