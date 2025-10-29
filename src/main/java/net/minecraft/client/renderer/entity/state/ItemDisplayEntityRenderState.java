package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.Display;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemDisplayEntityRenderState extends DisplayEntityRenderState {
    @Nullable
    public Display.ItemDisplay.ItemRenderState itemRenderState;
    @Nullable
    public BakedModel itemModel;

    @Override
    public boolean hasSubState() {
        return this.itemRenderState != null && this.itemModel != null;
    }
}