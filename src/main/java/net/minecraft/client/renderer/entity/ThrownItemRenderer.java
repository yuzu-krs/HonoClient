package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ThrownItemRenderer<T extends Entity & ItemSupplier> extends EntityRenderer<T, ThrownItemRenderState> {
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean fullBright;

    public ThrownItemRenderer(EntityRendererProvider.Context p_174416_, float p_174417_, boolean p_174418_) {
        super(p_174416_);
        this.itemRenderer = p_174416_.getItemRenderer();
        this.scale = p_174417_;
        this.fullBright = p_174418_;
    }

    public ThrownItemRenderer(EntityRendererProvider.Context p_174414_) {
        this(p_174414_, 1.0F, false);
    }

    @Override
    protected int getBlockLightLevel(T p_116092_, BlockPos p_116093_) {
        return this.fullBright ? 15 : super.getBlockLightLevel(p_116092_, p_116093_);
    }

    public void render(ThrownItemRenderState p_362153_, PoseStack p_367133_, MultiBufferSource p_369201_, int p_366531_) {
        p_367133_.pushPose();
        p_367133_.scale(this.scale, this.scale, this.scale);
        p_367133_.mulPose(this.entityRenderDispatcher.cameraOrientation());
        if (p_362153_.itemModel != null) {
            this.itemRenderer
                .render(
                    p_362153_.item, ItemDisplayContext.GROUND, false, p_367133_, p_369201_, p_366531_, OverlayTexture.NO_OVERLAY, p_362153_.itemModel
                );
        }

        p_367133_.popPose();
        super.render(p_362153_, p_367133_, p_369201_, p_366531_);
    }

    public ThrownItemRenderState createRenderState() {
        return new ThrownItemRenderState();
    }

    public void extractRenderState(T p_367843_, ThrownItemRenderState p_362566_, float p_361133_) {
        super.extractRenderState(p_367843_, p_362566_, p_361133_);
        ItemStack itemstack = p_367843_.getItem();
        p_362566_.itemModel = !itemstack.isEmpty() ? this.itemRenderer.getModel(itemstack, p_367843_.level(), null, p_367843_.getId()) : null;
        p_362566_.item = itemstack.copy();
    }
}