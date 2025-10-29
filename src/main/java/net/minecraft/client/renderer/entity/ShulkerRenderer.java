package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.ShulkerRenderState;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerRenderer extends MobRenderer<Shulker, ShulkerRenderState, ShulkerModel> {
    private static final ResourceLocation DEFAULT_TEXTURE_LOCATION = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture().withPath(p_340944_ -> "textures/" + p_340944_ + ".png");
    private static final ResourceLocation[] TEXTURE_LOCATION = Sheets.SHULKER_TEXTURE_LOCATION
        .stream()
        .map(p_340943_ -> p_340943_.texture().withPath(p_340942_ -> "textures/" + p_340942_ + ".png"))
        .toArray(ResourceLocation[]::new);

    public ShulkerRenderer(EntityRendererProvider.Context p_174370_) {
        super(p_174370_, new ShulkerModel(p_174370_.bakeLayer(ModelLayers.SHULKER)), 0.0F);
    }

    public Vec3 getRenderOffset(ShulkerRenderState p_368997_) {
        return p_368997_.renderOffset;
    }

    public boolean shouldRender(Shulker p_115913_, Frustum p_115914_, double p_115915_, double p_115916_, double p_115917_) {
        if (super.shouldRender(p_115913_, p_115914_, p_115915_, p_115916_, p_115917_)) {
            return true;
        } else {
            Vec3 vec3 = p_115913_.getRenderPosition(0.0F);
            if (vec3 == null) {
                return false;
            } else {
                EntityType<?> entitytype = p_115913_.getType();
                float f = entitytype.getHeight() / 2.0F;
                float f1 = entitytype.getWidth() / 2.0F;
                Vec3 vec31 = Vec3.atBottomCenterOf(p_115913_.blockPosition());
                return p_115914_.isVisible(
                    new AABB(vec3.x, vec3.y + (double)f, vec3.z, vec31.x, vec31.y + (double)f, vec31.z)
                        .inflate((double)f1, (double)f, (double)f1)
                );
            }
        }
    }

    public ResourceLocation getTextureLocation(ShulkerRenderState p_366878_) {
        return getTextureLocation(p_366878_.color);
    }

    public ShulkerRenderState createRenderState() {
        return new ShulkerRenderState();
    }

    public void extractRenderState(Shulker p_360848_, ShulkerRenderState p_368032_, float p_368000_) {
        super.extractRenderState(p_360848_, p_368032_, p_368000_);
        p_368032_.renderOffset = Objects.requireNonNullElse(p_360848_.getRenderPosition(p_368000_), Vec3.ZERO);
        p_368032_.color = p_360848_.getColor();
        p_368032_.peekAmount = p_360848_.getClientPeekAmount(p_368000_);
        p_368032_.yHeadRot = p_360848_.yHeadRot;
        p_368032_.yBodyRot = p_360848_.yBodyRot;
        p_368032_.attachFace = p_360848_.getAttachFace();
    }

    public static ResourceLocation getTextureLocation(@Nullable DyeColor p_174376_) {
        return p_174376_ == null ? DEFAULT_TEXTURE_LOCATION : TEXTURE_LOCATION[p_174376_.getId()];
    }

    protected void setupRotations(ShulkerRenderState p_363517_, PoseStack p_115908_, float p_115909_, float p_115910_) {
        super.setupRotations(p_363517_, p_115908_, p_115909_ + 180.0F, p_115910_);
        p_115908_.rotateAround(p_363517_.attachFace.getOpposite().getRotation(), 0.0F, 0.5F, 0.0F);
    }
}