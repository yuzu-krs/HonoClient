package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrossedArmsItemLayer<S extends LivingEntityRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
    private final ItemRenderer itemRenderer;

    public CrossedArmsItemLayer(RenderLayerParent<S, M> p_234818_, ItemRenderer p_363027_) {
        super(p_234818_);
        this.itemRenderer = p_363027_;
    }

    public void render(PoseStack p_116688_, MultiBufferSource p_116689_, int p_116690_, S p_361044_, float p_116692_, float p_116693_) {
        BakedModel bakedmodel = p_361044_.getMainHandItemModel();
        if (bakedmodel != null) {
            p_116688_.pushPose();
            p_116688_.translate(0.0F, 0.4F, -0.4F);
            p_116688_.mulPose(Axis.XP.rotationDegrees(180.0F));
            ItemStack itemstack = p_361044_.getMainHandItem();
            this.itemRenderer.render(itemstack, ItemDisplayContext.GROUND, false, p_116688_, p_116689_, p_116690_, OverlayTexture.NO_OVERLAY, bakedmodel);
            p_116688_.popPose();
        }
    }
}