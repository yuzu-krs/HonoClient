package net.minecraft.client.resources.model;

import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpecialModels {
    public static final ResourceLocation BUILTIN_GENERATED = builtinModelId("generated");
    public static final ResourceLocation BUILTIN_BLOCK_ENTITY = builtinModelId("entity");
    public static final UnbakedModel GENERATED_MARKER = createMarker("generation marker", BlockModel.GuiLight.FRONT);
    public static final UnbakedModel BLOCK_ENTITY_MARKER = createMarker("block entity marker", BlockModel.GuiLight.SIDE);

    public static ResourceLocation builtinModelId(String p_365122_) {
        return ResourceLocation.withDefaultNamespace("builtin/" + p_365122_);
    }

    private static UnbakedModel createMarker(String p_369288_, BlockModel.GuiLight p_367009_) {
        BlockModel blockmodel = new BlockModel(null, List.of(), Map.of(), null, p_367009_, ItemTransforms.NO_TRANSFORMS, List.of());
        blockmodel.name = p_369288_;
        return blockmodel;
    }
}