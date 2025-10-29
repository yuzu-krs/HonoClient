package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemFrameRenderer<T extends ItemFrame> extends EntityRenderer<T, ItemFrameRenderState> {
    public static final int GLOW_FRAME_BRIGHTNESS = 5;
    public static final int BRIGHT_MAP_LIGHT_ADJUSTMENT = 30;
    private final ItemRenderer itemRenderer;
    private final MapRenderer mapRenderer;
    private final BlockRenderDispatcher blockRenderer;

    public ItemFrameRenderer(EntityRendererProvider.Context p_174204_) {
        super(p_174204_);
        this.itemRenderer = p_174204_.getItemRenderer();
        this.mapRenderer = p_174204_.getMapRenderer();
        this.blockRenderer = p_174204_.getBlockRenderDispatcher();
    }

    protected int getBlockLightLevel(T p_174216_, BlockPos p_174217_) {
        return p_174216_.getType() == EntityType.GLOW_ITEM_FRAME ? Math.max(5, super.getBlockLightLevel(p_174216_, p_174217_)) : super.getBlockLightLevel(p_174216_, p_174217_);
    }

    public void render(ItemFrameRenderState p_361692_, PoseStack p_115061_, MultiBufferSource p_115062_, int p_115063_) {
        super.render(p_361692_, p_115061_, p_115062_, p_115063_);
        p_115061_.pushPose();
        Direction direction = p_361692_.direction;
        Vec3 vec3 = this.getRenderOffset(p_361692_);
        p_115061_.translate(-vec3.x(), -vec3.y(), -vec3.z());
        double d0 = 0.46875;
        p_115061_.translate((double)direction.getStepX() * 0.46875, (double)direction.getStepY() * 0.46875, (double)direction.getStepZ() * 0.46875);
        float f;
        float f1;
        if (direction.getAxis().isHorizontal()) {
            f = 0.0F;
            f1 = 180.0F - direction.toYRot();
        } else {
            f = (float)(-90 * direction.getAxisDirection().getStep());
            f1 = 180.0F;
        }

        p_115061_.mulPose(Axis.XP.rotationDegrees(f));
        p_115061_.mulPose(Axis.YP.rotationDegrees(f1));
        ItemStack itemstack = p_361692_.itemStack;
        if (!p_361692_.isInvisible) {
            ModelManager modelmanager = this.blockRenderer.getBlockModelShaper().getModelManager();
            ModelResourceLocation modelresourcelocation = this.getFrameModelResourceLoc(p_361692_.isGlowFrame, itemstack);
            p_115061_.pushPose();
            p_115061_.translate(-0.5F, -0.5F, -0.5F);
            this.blockRenderer
                .getModelRenderer()
                .renderModel(
                    p_115061_.last(),
                    p_115062_.getBuffer(RenderType.entitySolidZOffsetForward(TextureAtlas.LOCATION_BLOCKS)),
                    null,
                    modelmanager.getModel(modelresourcelocation),
                    1.0F,
                    1.0F,
                    1.0F,
                    p_115063_,
                    OverlayTexture.NO_OVERLAY
                );
            p_115061_.popPose();
        }

        if (!itemstack.isEmpty()) {
            MapId mapid = p_361692_.mapId;
            if (p_361692_.isInvisible) {
                p_115061_.translate(0.0F, 0.0F, 0.5F);
            } else {
                p_115061_.translate(0.0F, 0.0F, 0.4375F);
            }

            int j = mapid != null ? p_361692_.rotation % 4 * 2 : p_361692_.rotation;
            p_115061_.mulPose(Axis.ZP.rotationDegrees((float)j * 360.0F / 8.0F));
            if (mapid != null) {
                p_115061_.mulPose(Axis.ZP.rotationDegrees(180.0F));
                float f2 = 0.0078125F;
                p_115061_.scale(0.0078125F, 0.0078125F, 0.0078125F);
                p_115061_.translate(-64.0F, -64.0F, 0.0F);
                p_115061_.translate(0.0F, 0.0F, -1.0F);
                int i = this.getLightVal(p_361692_.isGlowFrame, 15728850, p_115063_);
                this.mapRenderer.render(p_361692_.mapRenderState, p_115061_, p_115062_, true, i);
            } else if (p_361692_.itemModel != null) {
                int k = this.getLightVal(p_361692_.isGlowFrame, 15728880, p_115063_);
                p_115061_.scale(0.5F, 0.5F, 0.5F);
                this.itemRenderer.render(itemstack, ItemDisplayContext.FIXED, false, p_115061_, p_115062_, k, OverlayTexture.NO_OVERLAY, p_361692_.itemModel);
            }
        }

        p_115061_.popPose();
    }

    private int getLightVal(boolean p_368253_, int p_174210_, int p_174211_) {
        return p_368253_ ? p_174210_ : p_174211_;
    }

    private ModelResourceLocation getFrameModelResourceLoc(boolean p_366996_, ItemStack p_174214_) {
        if (p_174214_.has(DataComponents.MAP_ID)) {
            return p_366996_ ? BlockStateModelLoader.GLOW_MAP_FRAME_LOCATION : BlockStateModelLoader.MAP_FRAME_LOCATION;
        } else {
            return p_366996_ ? BlockStateModelLoader.GLOW_FRAME_LOCATION : BlockStateModelLoader.FRAME_LOCATION;
        }
    }

    public Vec3 getRenderOffset(ItemFrameRenderState p_368370_) {
        return new Vec3((double)((float)p_368370_.direction.getStepX() * 0.3F), -0.25, (double)((float)p_368370_.direction.getStepZ() * 0.3F));
    }

    protected boolean shouldShowName(T p_115091_, double p_366137_) {
        return Minecraft.renderNames()
            && !p_115091_.getItem().isEmpty()
            && p_115091_.getItem().has(DataComponents.CUSTOM_NAME)
            && this.entityRenderDispatcher.crosshairPickEntity == p_115091_;
    }

    protected Component getNameTag(T p_364863_) {
        return p_364863_.getItem().getHoverName();
    }

    public ItemFrameRenderState createRenderState() {
        return new ItemFrameRenderState();
    }

    public void extractRenderState(T p_369136_, ItemFrameRenderState p_364469_, float p_366511_) {
        super.extractRenderState(p_369136_, p_364469_, p_366511_);
        p_364469_.direction = p_369136_.getDirection();
        ItemStack itemstack = p_369136_.getItem();
        p_364469_.itemStack = itemstack.copy();
        p_364469_.rotation = p_369136_.getRotation();
        p_364469_.isGlowFrame = p_369136_.getType() == EntityType.GLOW_ITEM_FRAME;
        p_364469_.itemModel = null;
        p_364469_.mapId = null;
        if (!p_364469_.itemStack.isEmpty()) {
            MapId mapid = p_369136_.getFramedMapId(itemstack);
            if (mapid != null) {
                MapItemSavedData mapitemsaveddata = p_369136_.level().getMapData(mapid);
                if (mapitemsaveddata != null) {
                    this.mapRenderer.extractRenderState(mapid, mapitemsaveddata, p_364469_.mapRenderState);
                    p_364469_.mapId = mapid;
                }
            } else {
                p_364469_.itemModel = this.itemRenderer.getModel(itemstack, p_369136_.level(), null, p_369136_.getId());
            }
        }
    }
}