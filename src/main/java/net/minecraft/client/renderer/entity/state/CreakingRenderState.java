package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.AnimationState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreakingRenderState extends LivingEntityRenderState {
    public AnimationState invulnerabilityAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public boolean isActive;
    public boolean canMove;
}