package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.FireworkRocketRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FireworkEntityRenderer extends EntityRenderer<FireworkRocketEntity, FireworkRocketRenderState> {
    private final ItemRenderer itemRenderer;

    public FireworkEntityRenderer(EntityRendererProvider.Context p_174114_) {
        super(p_174114_);
        this.itemRenderer = p_174114_.getItemRenderer();
    }

    public void render(FireworkRocketRenderState p_364825_, PoseStack p_114659_, MultiBufferSource p_114660_, int p_114661_) {
        p_114659_.pushPose();
        p_114659_.mulPose(this.entityRenderDispatcher.cameraOrientation());
        if (p_364825_.isShotAtAngle) {
            p_114659_.mulPose(Axis.ZP.rotationDegrees(180.0F));
            p_114659_.mulPose(Axis.YP.rotationDegrees(180.0F));
            p_114659_.mulPose(Axis.XP.rotationDegrees(90.0F));
        }

        this.itemRenderer
            .render(p_364825_.item, ItemDisplayContext.GROUND, false, p_114659_, p_114660_, p_114661_, OverlayTexture.NO_OVERLAY, p_364825_.itemModel);
        p_114659_.popPose();
        super.render(p_364825_, p_114659_, p_114660_, p_114661_);
    }

    public FireworkRocketRenderState createRenderState() {
        return new FireworkRocketRenderState();
    }

    public void extractRenderState(FireworkRocketEntity p_362725_, FireworkRocketRenderState p_362243_, float p_362924_) {
        super.extractRenderState(p_362725_, p_362243_, p_362924_);
        p_362243_.isShotAtAngle = p_362725_.isShotAtAngle();
        ItemStack itemstack = p_362725_.getItem();
        p_362243_.item = itemstack.copy();
        p_362243_.itemModel = !itemstack.isEmpty() ? this.itemRenderer.getModel(itemstack, p_362725_.level(), null, p_362725_.getId()) : null;
    }
}