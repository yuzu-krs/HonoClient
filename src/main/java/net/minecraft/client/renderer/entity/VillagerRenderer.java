package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VillagerRenderer extends MobRenderer<Villager, VillagerRenderState, VillagerModel> {
    private static final ResourceLocation VILLAGER_BASE_SKIN = ResourceLocation.withDefaultNamespace("textures/entity/villager/villager.png");
    public static final CustomHeadLayer.Transforms CUSTOM_HEAD_TRANSFORMS = new CustomHeadLayer.Transforms(-0.1171875F, -0.07421875F, 1.0F);

    public VillagerRenderer(EntityRendererProvider.Context p_174437_) {
        super(p_174437_, new VillagerModel(p_174437_.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
        this.addLayer(new CustomHeadLayer<>(this, p_174437_.getModelSet(), CUSTOM_HEAD_TRANSFORMS, p_174437_.getItemRenderer()));
        this.addLayer(new VillagerProfessionLayer<>(this, p_174437_.getResourceManager(), "villager"));
        this.addLayer(new CrossedArmsItemLayer<>(this, p_174437_.getItemRenderer()));
    }

    protected void scale(VillagerRenderState p_361771_, PoseStack p_116315_) {
        super.scale(p_361771_, p_116315_);
        float f = p_361771_.ageScale;
        p_116315_.scale(f, f, f);
    }

    public ResourceLocation getTextureLocation(VillagerRenderState p_362386_) {
        return VILLAGER_BASE_SKIN;
    }

    protected float getShadowRadius(VillagerRenderState p_363752_) {
        float f = super.getShadowRadius(p_363752_);
        return p_363752_.isBaby ? f * 0.5F : f;
    }

    public VillagerRenderState createRenderState() {
        return new VillagerRenderState();
    }

    public void extractRenderState(Villager p_363295_, VillagerRenderState p_364690_, float p_364344_) {
        super.extractRenderState(p_363295_, p_364690_, p_364344_);
        p_364690_.isUnhappy = p_363295_.getUnhappyCounter() > 0;
        p_364690_.villagerData = p_363295_.getVillagerData();
    }
}