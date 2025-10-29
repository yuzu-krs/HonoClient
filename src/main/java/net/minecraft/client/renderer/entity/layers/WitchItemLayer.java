package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.WitchModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.WitchRenderState;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitchItemLayer extends CrossedArmsItemLayer<WitchRenderState, WitchModel> {
    public WitchItemLayer(RenderLayerParent<WitchRenderState, WitchModel> p_234926_, ItemRenderer p_364299_) {
        super(p_234926_, p_364299_);
    }

    public void render(PoseStack p_362411_, MultiBufferSource p_368708_, int p_368191_, WitchRenderState p_367958_, float p_367022_, float p_362108_) {
        p_362411_.pushPose();
        if (p_367958_.rightHandItem.is(Items.POTION)) {
            this.getParentModel().root().translateAndRotate(p_362411_);
            this.getParentModel().getHead().translateAndRotate(p_362411_);
            this.getParentModel().getNose().translateAndRotate(p_362411_);
            p_362411_.translate(0.0625F, 0.25F, 0.0F);
            p_362411_.mulPose(Axis.ZP.rotationDegrees(180.0F));
            p_362411_.mulPose(Axis.XP.rotationDegrees(140.0F));
            p_362411_.mulPose(Axis.ZP.rotationDegrees(10.0F));
            p_362411_.translate(0.0F, -0.4F, 0.4F);
        }

        super.render(p_362411_, p_368708_, p_368191_, p_367958_, p_367022_, p_362108_);
        p_362411_.popPose();
    }
}