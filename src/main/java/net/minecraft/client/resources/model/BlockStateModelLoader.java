package net.minecraft.client.resources.model;

import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.UnbakedBlockStateModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class BlockStateModelLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String FRAME_MAP_PROPERTY = "map";
    private static final String FRAME_MAP_PROPERTY_TRUE = "map=true";
    private static final String FRAME_MAP_PROPERTY_FALSE = "map=false";
    private static final StateDefinition<Block, BlockState> ITEM_FRAME_FAKE_DEFINITION = new StateDefinition.Builder<Block, BlockState>(Blocks.AIR)
        .add(BooleanProperty.create("map"))
        .create(Block::defaultBlockState, BlockState::new);
    private static final ResourceLocation GLOW_ITEM_FRAME_LOCATION = ResourceLocation.withDefaultNamespace("glow_item_frame");
    private static final ResourceLocation ITEM_FRAME_LOCATION = ResourceLocation.withDefaultNamespace("item_frame");
    private static final Map<ResourceLocation, StateDefinition<Block, BlockState>> STATIC_DEFINITIONS = Map.of(ITEM_FRAME_LOCATION, ITEM_FRAME_FAKE_DEFINITION, GLOW_ITEM_FRAME_LOCATION, ITEM_FRAME_FAKE_DEFINITION);
    public static final ModelResourceLocation GLOW_MAP_FRAME_LOCATION = new ModelResourceLocation(GLOW_ITEM_FRAME_LOCATION, "map=true");
    public static final ModelResourceLocation GLOW_FRAME_LOCATION = new ModelResourceLocation(GLOW_ITEM_FRAME_LOCATION, "map=false");
    public static final ModelResourceLocation MAP_FRAME_LOCATION = new ModelResourceLocation(ITEM_FRAME_LOCATION, "map=true");
    public static final ModelResourceLocation FRAME_LOCATION = new ModelResourceLocation(ITEM_FRAME_LOCATION, "map=false");
    private final UnbakedModel missingModel;

    public BlockStateModelLoader(UnbakedModel p_344187_) {
        this.missingModel = p_344187_;
    }

    public static Function<ResourceLocation, StateDefinition<Block, BlockState>> definitionLocationToBlockMapper() {
        Map<ResourceLocation, StateDefinition<Block, BlockState>> map = new HashMap<>(STATIC_DEFINITIONS);

        for (Block block : BuiltInRegistries.BLOCK) {
            map.put(block.builtInRegistryHolder().key().location(), block.getStateDefinition());
        }

        return map::get;
    }

    public BlockStateModelLoader.LoadedModels loadBlockStateDefinitionStack(
        ResourceLocation p_367866_, StateDefinition<Block, BlockState> p_361140_, List<BlockStateModelLoader.LoadedBlockModelDefinition> p_367255_
    ) {
        List<BlockState> list = p_361140_.getPossibleStates();
        Map<BlockState, BlockStateModelLoader.LoadedModel> map = new HashMap<>();
        Map<ModelResourceLocation, BlockStateModelLoader.LoadedModel> map1 = new HashMap<>();

        try {
            for (BlockStateModelLoader.LoadedBlockModelDefinition blockstatemodelloader$loadedblockmodeldefinition : p_367255_) {
                blockstatemodelloader$loadedblockmodeldefinition.contents
                    .instantiate(p_361140_, p_367866_ + "/" + blockstatemodelloader$loadedblockmodeldefinition.source)
                    .forEach((p_358032_, p_358033_) -> map.put(p_358032_, new BlockStateModelLoader.LoadedModel(p_358032_, p_358033_)));
            }
        } finally {
        	for (BlockState blockstate : list) {
                    ModelResourceLocation modelresourcelocation = BlockModelShaper.stateToModelLocation(p_367866_, blockstate);
                    BlockStateModelLoader.LoadedModel blockstatemodelloader$loadedmodel = map.get(blockstate);
                    if (blockstatemodelloader$loadedmodel == null) {
                        LOGGER.warn("Missing blockstate definition: '{}' missing model for variant: '{}'", p_367866_, modelresourcelocation);
                        blockstatemodelloader$loadedmodel = new BlockStateModelLoader.LoadedModel(blockstate, this.missingModel);
                    }

                    map1.put(modelresourcelocation, blockstatemodelloader$loadedmodel);
                }
            }

        return new BlockStateModelLoader.LoadedModels(map1);
    }

    @OnlyIn(Dist.CLIENT)
    public static record LoadedBlockModelDefinition(String source, BlockModelDefinition contents) {
    }

    @OnlyIn(Dist.CLIENT)
    public static record LoadedModel(BlockState state, UnbakedModel model) {
    }

    @OnlyIn(Dist.CLIENT)
    public static record LoadedModels(Map<ModelResourceLocation, BlockStateModelLoader.LoadedModel> models) {
    }
}
