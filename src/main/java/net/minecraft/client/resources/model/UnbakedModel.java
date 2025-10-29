package net.minecraft.client.resources.model;

import java.util.function.Function;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface UnbakedModel {
    void resolveDependencies(UnbakedModel.Resolver p_369253_);

    BakedModel bake(ModelBaker p_250133_, Function<Material, TextureAtlasSprite> p_119535_, ModelState p_119536_);

    @OnlyIn(Dist.CLIENT)
    public interface Resolver {
        UnbakedModel resolve(ResourceLocation p_368602_);
    }
}