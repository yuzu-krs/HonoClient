package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemInHandLayer<S extends LivingEntityRenderState, M extends EntityModel<S> & ArmedModel> extends RenderLayer<S, M> {
    private final ItemRenderer itemRenderer;

    public ItemInHandLayer(RenderLayerParent<S, M> p_234846_, ItemRenderer p_367058_) {
        super(p_234846_);
        this.itemRenderer = p_367058_;
    }

    public void render(PoseStack p_117204_, MultiBufferSource p_117205_, int p_117206_, S p_363689_, float p_117208_, float p_117209_) {
        this.renderArmWithItem(
            p_363689_, p_363689_.rightHandItemModel, p_363689_.rightHandItem, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, p_117204_, p_117205_, p_117206_
        );
        this.renderArmWithItem(
            p_363689_, p_363689_.leftHandItemModel, p_363689_.leftHandItem, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT, p_117204_, p_117205_, p_117206_
        );
    }

    protected void renderArmWithItem(
        S p_361040_,
        @Nullable BakedModel p_361149_,
        ItemStack p_117186_,
        ItemDisplayContext p_270970_,
        HumanoidArm p_117188_,
        PoseStack p_117189_,
        MultiBufferSource p_117190_,
        int p_117191_
    ) {
        if (p_361149_ != null && !p_117186_.isEmpty()) {
            p_117189_.pushPose();
            this.getParentModel().translateToHand(p_117188_, p_117189_);
            p_117189_.mulPose(Axis.XP.rotationDegrees(-90.0F));
            p_117189_.mulPose(Axis.YP.rotationDegrees(180.0F));
            boolean flag = p_117188_ == HumanoidArm.LEFT;
            p_117189_.translate((float)(flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);
            this.itemRenderer.render(p_117186_, p_270970_, flag, p_117189_, p_117190_, p_117191_, OverlayTexture.NO_OVERLAY, p_361149_);
            p_117189_.popPose();
        }
    }
}