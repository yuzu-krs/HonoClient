package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerItemInHandLayer<S extends PlayerRenderState, M extends EntityModel<S> & ArmedModel & HeadedModel> extends ItemInHandLayer<S, M> {
    private final ItemRenderer itemRenderer;
    private static final float X_ROT_MIN = (float) (-Math.PI / 6);
    private static final float X_ROT_MAX = (float) (Math.PI / 2);

    public PlayerItemInHandLayer(RenderLayerParent<S, M> p_234866_, ItemRenderer p_363451_) {
        super(p_234866_, p_363451_);
        this.itemRenderer = p_363451_;
    }

    protected void renderArmWithItem(
        S p_369457_,
        @Nullable BakedModel p_368658_,
        ItemStack p_366603_,
        ItemDisplayContext p_368070_,
        HumanoidArm p_364119_,
        PoseStack p_364225_,
        MultiBufferSource p_364963_,
        int p_366853_
    ) {
        if (p_368658_ != null) {
            InteractionHand interactionhand = p_364119_ == p_369457_.mainArm ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            if (p_369457_.isUsingItem && p_369457_.useItemHand == interactionhand && p_369457_.attackTime < 1.0E-5F && p_366603_.is(Items.SPYGLASS)) {
                this.renderArmWithSpyglass(p_368658_, p_366603_, p_364119_, p_364225_, p_364963_, p_366853_);
            } else {
                super.renderArmWithItem(p_369457_, p_368658_, p_366603_, p_368070_, p_364119_, p_364225_, p_364963_, p_366853_);
            }
        }
    }

    private void renderArmWithSpyglass(BakedModel p_364810_, ItemStack p_174519_, HumanoidArm p_174520_, PoseStack p_174521_, MultiBufferSource p_174522_, int p_174523_) {
        p_174521_.pushPose();
        this.getParentModel().root().translateAndRotate(p_174521_);
        ModelPart modelpart = this.getParentModel().getHead();
        float f = modelpart.xRot;
        modelpart.xRot = Mth.clamp(modelpart.xRot, (float) (-Math.PI / 6), (float) (Math.PI / 2));
        modelpart.translateAndRotate(p_174521_);
        modelpart.xRot = f;
        CustomHeadLayer.translateToHead(p_174521_, CustomHeadLayer.Transforms.DEFAULT);
        boolean flag = p_174520_ == HumanoidArm.LEFT;
        p_174521_.translate((flag ? -2.5F : 2.5F) / 16.0F, -0.0625F, 0.0F);
        this.itemRenderer.render(p_174519_, ItemDisplayContext.HEAD, false, p_174521_, p_174522_, p_174523_, OverlayTexture.NO_OVERLAY, p_364810_);
        p_174521_.popPose();
    }
}