package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.FoxRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoxHeldItemLayer extends RenderLayer<FoxRenderState, FoxModel> {
    private final ItemRenderer itemRenderer;

    public FoxHeldItemLayer(RenderLayerParent<FoxRenderState, FoxModel> p_234838_, ItemRenderer p_368674_) {
        super(p_234838_);
        this.itemRenderer = p_368674_;
    }

    public void render(PoseStack p_116996_, MultiBufferSource p_116997_, int p_116998_, FoxRenderState p_360829_, float p_117000_, float p_117001_) {
        BakedModel bakedmodel = p_360829_.getMainHandItemModel();
        ItemStack itemstack = p_360829_.getMainHandItem();
        if (bakedmodel != null && !itemstack.isEmpty()) {
            boolean flag = p_360829_.isSleeping;
            boolean flag1 = p_360829_.isBaby;
            p_116996_.pushPose();
            p_116996_.translate(
                this.getParentModel().head.x / 16.0F, this.getParentModel().head.y / 16.0F, this.getParentModel().head.z / 16.0F
            );
            if (flag1) {
                float f = 0.75F;
                p_116996_.scale(0.75F, 0.75F, 0.75F);
            }

            p_116996_.mulPose(Axis.ZP.rotation(p_360829_.headRollAngle));
            p_116996_.mulPose(Axis.YP.rotationDegrees(p_117000_));
            p_116996_.mulPose(Axis.XP.rotationDegrees(p_117001_));
            if (p_360829_.isBaby) {
                if (flag) {
                    p_116996_.translate(0.4F, 0.26F, 0.15F);
                } else {
                    p_116996_.translate(0.06F, 0.26F, -0.5F);
                }
            } else if (flag) {
                p_116996_.translate(0.46F, 0.26F, 0.22F);
            } else {
                p_116996_.translate(0.06F, 0.27F, -0.5F);
            }

            p_116996_.mulPose(Axis.XP.rotationDegrees(90.0F));
            if (flag) {
                p_116996_.mulPose(Axis.ZP.rotationDegrees(90.0F));
            }

            this.itemRenderer.render(itemstack, ItemDisplayContext.GROUND, false, p_116996_, p_116997_, p_116998_, OverlayTexture.NO_OVERLAY, bakedmodel);
            p_116996_.popPose();
        }
    }
}