package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.OminousItemSpawnerRenderState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.OminousItemSpawner;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OminousItemSpawnerRenderer extends EntityRenderer<OminousItemSpawner, OminousItemSpawnerRenderState> {
    private static final float ROTATION_SPEED = 40.0F;
    private static final int TICKS_SCALING = 50;
    private final ItemRenderer itemRenderer;

    protected OminousItemSpawnerRenderer(EntityRendererProvider.Context p_332134_) {
        super(p_332134_);
        this.itemRenderer = p_332134_.getItemRenderer();
    }

    public OminousItemSpawnerRenderState createRenderState() {
        return new OminousItemSpawnerRenderState();
    }

    public void extractRenderState(OminousItemSpawner p_370185_, OminousItemSpawnerRenderState p_364291_, float p_369603_) {
        super.extractRenderState(p_370185_, p_364291_, p_369603_);
        ItemStack itemstack = p_370185_.getItem();
        p_364291_.item = itemstack.copy();
        p_364291_.itemModel = !itemstack.isEmpty() ? this.itemRenderer.getModel(itemstack, p_370185_.level(), null, 0) : null;
    }

    public void render(OminousItemSpawnerRenderState p_365179_, PoseStack p_330642_, MultiBufferSource p_333628_, int p_334934_) {
        BakedModel bakedmodel = p_365179_.itemModel;
        if (bakedmodel != null) {
            p_330642_.pushPose();
            if (p_365179_.ageInTicks <= 50.0F) {
                float f = Math.min(p_365179_.ageInTicks, 50.0F) / 50.0F;
                p_330642_.scale(f, f, f);
            }

            float f1 = Mth.wrapDegrees(p_365179_.ageInTicks * 40.0F);
            p_330642_.mulPose(Axis.YP.rotationDegrees(f1));
            ItemEntityRenderer.renderMultipleFromCount(
                this.itemRenderer, p_330642_, p_333628_, 15728880, p_365179_.item, bakedmodel, bakedmodel.isGui3d(), RandomSource.create()
            );
            p_330642_.popPose();
        }
    }
}