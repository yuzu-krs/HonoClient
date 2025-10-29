package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerBoxRenderer implements BlockEntityRenderer<ShulkerBoxBlockEntity> {
    private final ShulkerBoxRenderer.ShulkerBoxModel model;

    public ShulkerBoxRenderer(BlockEntityRendererProvider.Context p_173626_) {
        this.model = new ShulkerBoxRenderer.ShulkerBoxModel(p_173626_.bakeLayer(ModelLayers.SHULKER_BOX));
    }

    public void render(ShulkerBoxBlockEntity p_112478_, float p_112479_, PoseStack p_112480_, MultiBufferSource p_112481_, int p_112482_, int p_112483_) {
        Direction direction = Direction.UP;
        if (p_112478_.hasLevel()) {
            BlockState blockstate = p_112478_.getLevel().getBlockState(p_112478_.getBlockPos());
            if (blockstate.getBlock() instanceof ShulkerBoxBlock) {
                direction = blockstate.getValue(ShulkerBoxBlock.FACING);
            }
        }

        DyeColor dyecolor = p_112478_.getColor();
        Material material;
        if (dyecolor == null) {
            material = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
        } else {
            material = Sheets.SHULKER_TEXTURE_LOCATION.get(dyecolor.getId());
        }

        p_112480_.pushPose();
        p_112480_.translate(0.5F, 0.5F, 0.5F);
        float f = 0.9995F;
        p_112480_.scale(0.9995F, 0.9995F, 0.9995F);
        p_112480_.mulPose(direction.getRotation());
        p_112480_.scale(1.0F, -1.0F, -1.0F);
        p_112480_.translate(0.0F, -1.0F, 0.0F);
        this.model.animate(p_112478_, p_112479_);
        VertexConsumer vertexconsumer = material.buffer(p_112481_, this.model::renderType);
        this.model.renderToBuffer(p_112480_, vertexconsumer, p_112482_, p_112483_);
        p_112480_.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    static class ShulkerBoxModel extends Model {
        private final ModelPart lid;

        public ShulkerBoxModel(ModelPart p_366433_) {
            super(p_366433_, RenderType::entityCutoutNoCull);
            this.lid = p_366433_.getChild("lid");
        }

        public void animate(ShulkerBoxBlockEntity p_362661_, float p_363916_) {
            this.lid.setPos(0.0F, 24.0F - p_362661_.getProgress(p_363916_) * 0.5F * 16.0F, 0.0F);
            this.lid.yRot = 270.0F * p_362661_.getProgress(p_363916_) * (float) (Math.PI / 180.0);
        }
    }
}