package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LlamaRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentModel;
import net.minecraft.world.item.equipment.EquipmentModels;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LlamaDecorLayer extends RenderLayer<LlamaRenderState, LlamaModel> {
    private final LlamaModel adultModel;
    private final LlamaModel babyModel;
    private final EquipmentLayerRenderer equipmentRenderer;

    public LlamaDecorLayer(RenderLayerParent<LlamaRenderState, LlamaModel> p_174499_, EntityModelSet p_174500_, EquipmentLayerRenderer p_362060_) {
        super(p_174499_);
        this.equipmentRenderer = p_362060_;
        this.adultModel = new LlamaModel(p_174500_.bakeLayer(ModelLayers.LLAMA_DECOR));
        this.babyModel = new LlamaModel(p_174500_.bakeLayer(ModelLayers.LLAMA_BABY_DECOR));
    }

    public void render(PoseStack p_364604_, MultiBufferSource p_363218_, int p_361586_, LlamaRenderState p_367324_, float p_364047_, float p_367997_) {
        ItemStack itemstack = p_367324_.bodyItem;
        Equippable equippable = itemstack.get(DataComponents.EQUIPPABLE);
        if (equippable != null && equippable.model().isPresent()) {
            this.renderEquipment(p_364604_, p_363218_, p_367324_, itemstack, equippable.model().get(), p_361586_);
        } else if (p_367324_.isTraderLlama) {
            this.renderEquipment(p_364604_, p_363218_, p_367324_, ItemStack.EMPTY, EquipmentModels.TRADER_LLAMA, p_361586_);
        }
    }

    private void renderEquipment(
        PoseStack p_369436_, MultiBufferSource p_361687_, LlamaRenderState p_361805_, ItemStack p_360838_, ResourceLocation p_362947_, int p_361245_
    ) {
        LlamaModel llamamodel = p_361805_.isBaby ? this.babyModel : this.adultModel;
        llamamodel.setupAnim(p_361805_);
        this.equipmentRenderer.renderLayers(EquipmentModel.LayerType.LLAMA_BODY, p_362947_, llamamodel, p_360838_, p_369436_, p_361687_, p_361245_);
    }
}