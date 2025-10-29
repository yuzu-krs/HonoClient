package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSkeletonRenderer<T extends AbstractSkeleton, S extends SkeletonRenderState> extends HumanoidMobRenderer<T, S, SkeletonModel<S>> {
    public AbstractSkeletonRenderer(
        EntityRendererProvider.Context p_362142_, ModelLayerLocation p_369786_, ModelLayerLocation p_368806_, ModelLayerLocation p_364022_
    ) {
        this(p_362142_, p_368806_, p_364022_, new SkeletonModel<>(p_362142_.bakeLayer(p_369786_)));
    }

    public AbstractSkeletonRenderer(
        EntityRendererProvider.Context p_363000_, ModelLayerLocation p_363059_, ModelLayerLocation p_360859_, SkeletonModel<S> p_360744_
    ) {
        super(p_363000_, p_360744_, 0.5F);
        this.addLayer(
            new HumanoidArmorLayer<>(
                this, new SkeletonModel(p_363000_.bakeLayer(p_363059_)), new SkeletonModel(p_363000_.bakeLayer(p_360859_)), p_363000_.getEquipmentRenderer()
            )
        );
    }

    public void extractRenderState(T p_369278_, S p_363603_, float p_362928_) {
        super.extractRenderState(p_369278_, p_363603_, p_362928_);
        p_363603_.isAggressive = p_369278_.isAggressive();
        p_363603_.isShaking = p_369278_.isShaking();
    }

    protected boolean isShaking(S p_366804_) {
        return p_366804_.isShaking;
    }
}