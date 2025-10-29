package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.WitchModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.WitchItemLayer;
import net.minecraft.client.renderer.entity.state.WitchRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Witch;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitchRenderer extends MobRenderer<Witch, WitchRenderState, WitchModel> {
    private static final ResourceLocation WITCH_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/witch.png");

    public WitchRenderer(EntityRendererProvider.Context p_174443_) {
        super(p_174443_, new WitchModel(p_174443_.bakeLayer(ModelLayers.WITCH)), 0.5F);
        this.addLayer(new WitchItemLayer(this, p_174443_.getItemRenderer()));
    }

    public ResourceLocation getTextureLocation(WitchRenderState p_370135_) {
        return WITCH_LOCATION;
    }

    public WitchRenderState createRenderState() {
        return new WitchRenderState();
    }

    public void extractRenderState(Witch p_363206_, WitchRenderState p_362711_, float p_363215_) {
        super.extractRenderState(p_363206_, p_362711_, p_363215_);
        p_362711_.entityId = p_363206_.getId();
        p_362711_.isHoldingItem = !p_363206_.getMainHandItem().isEmpty();
    }
}