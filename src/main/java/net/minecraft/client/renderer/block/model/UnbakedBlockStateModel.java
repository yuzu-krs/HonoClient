package net.minecraft.client.renderer.block.model;

import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface UnbakedBlockStateModel extends UnbakedModel {
    Object visualEqualityGroup(BlockState p_362808_);
}