package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SnowGolemHeadLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SnowGolemRenderer extends MobRenderer<SnowGolem, LivingEntityRenderState, SnowGolemModel> {
    private static final ResourceLocation SNOW_GOLEM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/snow_golem.png");

    public SnowGolemRenderer(EntityRendererProvider.Context p_174393_) {
        super(p_174393_, new SnowGolemModel(p_174393_.bakeLayer(ModelLayers.SNOW_GOLEM)), 0.5F);
        this.addLayer(new SnowGolemHeadLayer(this, p_174393_.getBlockRenderDispatcher(), p_174393_.getItemRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(LivingEntityRenderState p_362671_) {
        return SNOW_GOLEM_LOCATION;
    }

    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    public void extractRenderState(SnowGolem p_361607_, LivingEntityRenderState p_367455_, float p_368353_) {
        super.extractRenderState(p_361607_, p_367455_, p_368353_);
        p_367455_.headItem = p_361607_.hasPumpkin() ? new ItemStack(Items.CARVED_PUMPKIN) : ItemStack.EMPTY;
        p_367455_.headItemModel = this.itemRenderer.resolveItemModel(p_367455_.headItem, p_361607_, ItemDisplayContext.HEAD);
    }
}