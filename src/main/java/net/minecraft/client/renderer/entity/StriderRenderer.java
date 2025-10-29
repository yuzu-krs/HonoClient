package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.StriderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.client.renderer.entity.state.StriderRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Strider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StriderRenderer extends MobRenderer<Strider, StriderRenderState, StriderModel> {
    private static final ResourceLocation STRIDER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/strider/strider.png");
    private static final ResourceLocation COLD_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/strider/strider_cold.png");
    private static final float SHADOW_RADIUS = 0.5F;

    public StriderRenderer(EntityRendererProvider.Context p_174411_) {
        super(p_174411_, new StriderModel(p_174411_.bakeLayer(ModelLayers.STRIDER)), 0.5F);
        this.addLayer(
            new SaddleLayer<>(
                this, new StriderModel(p_174411_.bakeLayer(ModelLayers.STRIDER_SADDLE)), ResourceLocation.withDefaultNamespace("textures/entity/strider/strider_saddle.png")
            )
        );
    }

    public ResourceLocation getTextureLocation(StriderRenderState p_361677_) {
        return p_361677_.isSuffocating ? COLD_LOCATION : STRIDER_LOCATION;
    }

    protected float getShadowRadius(StriderRenderState p_364573_) {
        float f = super.getShadowRadius(p_364573_);
        return p_364573_.isBaby ? f * 0.5F : f;
    }

    public StriderRenderState createRenderState() {
        return new StriderRenderState();
    }

    public void extractRenderState(Strider p_361862_, StriderRenderState p_361393_, float p_362076_) {
        super.extractRenderState(p_361862_, p_361393_, p_362076_);
        p_361393_.isSaddled = p_361862_.isSaddled();
        p_361393_.isSuffocating = p_361862_.isSuffocating();
        p_361393_.isRidden = p_361862_.isVehicle();
    }

    protected void scale(StriderRenderState p_364959_, PoseStack p_116061_) {
        float f = p_364959_.ageScale;
        p_116061_.scale(f, f, f);
    }

    protected boolean isShaking(StriderRenderState p_370098_) {
        return super.isShaking(p_370098_) || p_370098_.isSuffocating;
    }
}