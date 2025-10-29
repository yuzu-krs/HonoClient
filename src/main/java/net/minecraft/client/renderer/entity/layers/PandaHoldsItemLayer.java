package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.PandaRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PandaHoldsItemLayer extends RenderLayer<PandaRenderState, PandaModel> {
    private final ItemRenderer itemRenderer;

    public PandaHoldsItemLayer(RenderLayerParent<PandaRenderState, PandaModel> p_234862_, ItemRenderer p_366287_) {
        super(p_234862_);
        this.itemRenderer = p_366287_;
    }

    public void render(PoseStack p_117269_, MultiBufferSource p_117270_, int p_117271_, PandaRenderState p_367900_, float p_117273_, float p_117274_) {
        BakedModel bakedmodel = p_367900_.getMainHandItemModel();
        if (bakedmodel != null && p_367900_.isSitting && !p_367900_.isScared) {
            float f = -0.6F;
            float f1 = 1.4F;
            if (p_367900_.isEating) {
                f -= 0.2F * Mth.sin(p_367900_.ageInTicks * 0.6F) + 0.2F;
                f1 -= 0.09F * Mth.sin(p_367900_.ageInTicks * 0.6F);
            }

            p_117269_.pushPose();
            p_117269_.translate(0.1F, f1, f);
            ItemStack itemstack = p_367900_.getMainHandItem();
            this.itemRenderer.render(itemstack, ItemDisplayContext.GROUND, false, p_117269_, p_117270_, p_117271_, OverlayTexture.NO_OVERLAY, bakedmodel);
            p_117269_.popPose();
        }
    }
}