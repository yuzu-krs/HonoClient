package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SnowGolemHeadLayer extends RenderLayer<LivingEntityRenderState, SnowGolemModel> {
    private final BlockRenderDispatcher blockRenderer;
    private final ItemRenderer itemRenderer;

    public SnowGolemHeadLayer(RenderLayerParent<LivingEntityRenderState, SnowGolemModel> p_234871_, BlockRenderDispatcher p_234872_, ItemRenderer p_234873_) {
        super(p_234871_);
        this.blockRenderer = p_234872_;
        this.itemRenderer = p_234873_;
    }

    public void render(PoseStack p_117483_, MultiBufferSource p_117484_, int p_117485_, LivingEntityRenderState p_367864_, float p_117487_, float p_117488_) {
        BakedModel bakedmodel = p_367864_.headItemModel;
        if (bakedmodel != null) {
            boolean flag = p_367864_.appearsGlowing && p_367864_.isInvisible;
            if (!p_367864_.isInvisible || flag) {
                p_117483_.pushPose();
                this.getParentModel().getHead().translateAndRotate(p_117483_);
                float f = 0.625F;
                p_117483_.translate(0.0F, -0.34375F, 0.0F);
                p_117483_.mulPose(Axis.YP.rotationDegrees(180.0F));
                p_117483_.scale(0.625F, -0.625F, -0.625F);
                ItemStack itemstack = p_367864_.headItem;
                if (flag && itemstack.getItem() instanceof BlockItem blockitem) {
                    BlockState blockstate = blockitem.getBlock().defaultBlockState();
                    BakedModel bakedmodel1 = this.blockRenderer.getBlockModel(blockstate);
                    int i = LivingEntityRenderer.getOverlayCoords(p_367864_, 0.0F);
                    p_117483_.translate(-0.5F, -0.5F, -0.5F);
                    this.blockRenderer
                        .getModelRenderer()
                        .renderModel(
                            p_117483_.last(),
                            p_117484_.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS)),
                            blockstate,
                            bakedmodel1,
                            0.0F,
                            0.0F,
                            0.0F,
                            p_117485_,
                            i
                        );
                } else {
                    this.itemRenderer
                        .render(
                            itemstack,
                            ItemDisplayContext.HEAD,
                            false,
                            p_117483_,
                            p_117484_,
                            p_117485_,
                            LivingEntityRenderer.getOverlayCoords(p_367864_, 0.0F),
                            bakedmodel
                        );
                }

                p_117483_.popPose();
            }
        }
    }
}