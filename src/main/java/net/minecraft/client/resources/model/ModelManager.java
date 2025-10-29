package net.minecraft.client.resources.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ModelManager implements PreparableReloadListener, AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter BLOCKSTATE_LISTER = FileToIdConverter.json("blockstates");
    private static final FileToIdConverter MODEL_LISTER = FileToIdConverter.json("models");
    private static final Map<ResourceLocation, ResourceLocation> VANILLA_ATLASES = Map.of(
        Sheets.BANNER_SHEET,
        ResourceLocation.withDefaultNamespace("banner_patterns"),
        Sheets.BED_SHEET,
        ResourceLocation.withDefaultNamespace("beds"),
        Sheets.CHEST_SHEET,
        ResourceLocation.withDefaultNamespace("chests"),
        Sheets.SHIELD_SHEET,
        ResourceLocation.withDefaultNamespace("shield_patterns"),
        Sheets.SIGN_SHEET,
        ResourceLocation.withDefaultNamespace("signs"),
        Sheets.SHULKER_SHEET,
        ResourceLocation.withDefaultNamespace("shulker_boxes"),
        Sheets.ARMOR_TRIMS_SHEET,
        ResourceLocation.withDefaultNamespace("armor_trims"),
        Sheets.DECORATED_POT_SHEET,
        ResourceLocation.withDefaultNamespace("decorated_pot"),
        TextureAtlas.LOCATION_BLOCKS,
        ResourceLocation.withDefaultNamespace("blocks")
    );
    private Map<ModelResourceLocation, BakedModel> bakedRegistry;
    private final AtlasSet atlases;
    private final BlockModelShaper blockModelShaper;
    private final BlockColors blockColors;
    private int maxMipmapLevels;
    private BakedModel missingModel;
    private Object2IntMap<BlockState> modelGroups;

    public ModelManager(TextureManager p_119406_, BlockColors p_119407_, int p_119408_) {
        this.blockColors = p_119407_;
        this.maxMipmapLevels = p_119408_;
        this.blockModelShaper = new BlockModelShaper(this);
        this.atlases = new AtlasSet(VANILLA_ATLASES, p_119406_);
    }

    public BakedModel getModel(ModelResourceLocation p_119423_) {
        return this.bakedRegistry.getOrDefault(p_119423_, this.missingModel);
    }

    public BakedModel getMissingModel() {
        return this.missingModel;
    }

    public BlockModelShaper getBlockModelShaper() {
        return this.blockModelShaper;
    }

    @Override
    public final CompletableFuture<Void> reload(
        PreparableReloadListener.PreparationBarrier p_249079_, ResourceManager p_251134_, Executor p_250550_, Executor p_249221_
    ) {
        UnbakedModel unbakedmodel = MissingBlockModel.missingModel();
        BlockStateModelLoader blockstatemodelloader = new BlockStateModelLoader(unbakedmodel);
        CompletableFuture<Map<ResourceLocation, UnbakedModel>> completablefuture = loadBlockModels(p_251134_, p_250550_);
        CompletableFuture<BlockStateModelLoader.LoadedModels> completablefuture1 = loadBlockStates(blockstatemodelloader, p_251134_, p_250550_);
        CompletableFuture<ModelDiscovery> completablefuture2 = completablefuture1.thenCombineAsync(
            completablefuture, (p_358036_, p_358037_) -> this.discoverModelDependencies(unbakedmodel, (Map<ResourceLocation, UnbakedModel>)p_358037_, p_358036_), p_250550_
        );
        CompletableFuture<Object2IntMap<BlockState>> completablefuture3 = completablefuture1.thenApplyAsync(
            p_358038_ -> buildModelGroups(this.blockColors, p_358038_), p_250550_
        );
        Map<ResourceLocation, CompletableFuture<AtlasSet.StitchResult>> map = this.atlases.scheduleLoad(p_251134_, this.maxMipmapLevels, p_250550_);
        return CompletableFuture.allOf(
                Stream.concat(map.values().stream(), Stream.of(completablefuture2, completablefuture3)).toArray(CompletableFuture[]::new)
            )
            .thenApplyAsync(
                p_358046_ -> {
                    Map<ResourceLocation, AtlasSet.StitchResult> map1 = map.entrySet()
                        .stream()
                        .collect(Collectors.toMap(Entry::getKey, p_248988_ -> p_248988_.getValue().join()));
                    ModelDiscovery modeldiscovery = completablefuture2.join();
                    Object2IntMap<BlockState> object2intmap = completablefuture3.join();
                    return this.loadModels(
                        Profiler.get(), map1, new ModelBakery(modeldiscovery.getTopModels(), modeldiscovery.getReferencedModels(), unbakedmodel), object2intmap
                    );
                },
                p_250550_
            )
            .thenCompose(p_252255_ -> p_252255_.readyForUpload.thenApply(p_251581_ -> (ModelManager.ReloadState)p_252255_))
            .thenCompose(p_249079_::wait)
            .thenAcceptAsync(p_358039_ -> this.apply(p_358039_, Profiler.get()), p_249221_);
    }

    private static CompletableFuture<Map<ResourceLocation, UnbakedModel>> loadBlockModels(ResourceManager p_251361_, Executor p_252189_) {
        return CompletableFuture.<Map<ResourceLocation, Resource>>supplyAsync(() -> MODEL_LISTER.listMatchingResources(p_251361_), p_252189_)
            .thenCompose(
                p_250597_ -> {
                    List<CompletableFuture<Pair<ResourceLocation, BlockModel>>> list = new ArrayList<>(p_250597_.size());

                    for (Entry<ResourceLocation, Resource> entry : p_250597_.entrySet()) {
                        list.add(CompletableFuture.supplyAsync(() -> {
                            ResourceLocation resourcelocation = MODEL_LISTER.fileToId(entry.getKey());

                            try {
                                Pair pair;
                                try (Reader reader = entry.getValue().openAsReader()) {
                                    BlockModel blockmodel = BlockModel.fromStream(reader);
                                    blockmodel.name = resourcelocation.toString();
                                    pair = Pair.of(resourcelocation, blockmodel);
                                }

                                return pair;
                            } catch (Exception exception) {
                                LOGGER.error("Failed to load model {}", entry.getKey(), exception);
                                return null;
                            }
                        }, p_252189_));
                    }

                    return Util.sequence(list)
                        .thenApply(
                            p_250813_ -> p_250813_.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond))
                        );
                }
            );
    }

    private ModelDiscovery discoverModelDependencies(UnbakedModel p_364387_, Map<ResourceLocation, UnbakedModel> p_360749_, BlockStateModelLoader.LoadedModels p_366446_) {
        ModelDiscovery modeldiscovery = new ModelDiscovery(p_360749_, p_364387_);
        modeldiscovery.registerStandardModels(p_366446_);
        modeldiscovery.discoverDependencies();
        return modeldiscovery;
    }

    private static CompletableFuture<BlockStateModelLoader.LoadedModels> loadBlockStates(
        BlockStateModelLoader p_367882_, ResourceManager p_252084_, Executor p_249943_
    ) {
        Function<ResourceLocation, StateDefinition<Block, BlockState>> function = BlockStateModelLoader.definitionLocationToBlockMapper();
        return CompletableFuture.<Map<ResourceLocation, List<Resource>>>supplyAsync(() -> BLOCKSTATE_LISTER.listMatchingResourceStacks(p_252084_), p_249943_).thenCompose(p_358054_ -> {
            List<CompletableFuture<BlockStateModelLoader.LoadedModels>> list = new ArrayList<>(p_358054_.size());

            for (Entry<ResourceLocation, List<Resource>> entry : p_358054_.entrySet()) {
                list.add(CompletableFuture.supplyAsync(() -> {
                    ResourceLocation resourcelocation = BLOCKSTATE_LISTER.fileToId(entry.getKey());
                    StateDefinition<Block, BlockState> statedefinition = function.apply(resourcelocation);
                    if (statedefinition == null) {
                        LOGGER.debug("Discovered unknown block state definition {}, ignoring", resourcelocation);
                        return null;
                    } else {
                        List<Resource> list1 = entry.getValue();
                        List<BlockStateModelLoader.LoadedBlockModelDefinition> list2 = new ArrayList<>(list1.size());

                        for (Resource resource : list1) {
                            try (Reader reader = resource.openAsReader()) {
                                JsonObject jsonobject = GsonHelper.parse(reader);
                                BlockModelDefinition blockmodeldefinition = BlockModelDefinition.fromJsonElement(jsonobject);
                                list2.add(new BlockStateModelLoader.LoadedBlockModelDefinition(resource.sourcePackId(), blockmodeldefinition));
                            } catch (Exception exception1) {
                                LOGGER.error("Failed to load blockstate definition {} from pack {}", resourcelocation, resource.sourcePackId(), exception1);
                            }
                        }

                        try {
                            return p_367882_.loadBlockStateDefinitionStack(resourcelocation, statedefinition, list2);
                        } catch (Exception exception) {
                            LOGGER.error("Failed to load blockstate definition {}", resourcelocation, exception);
                            return null;
                        }
                    }
                }, p_249943_));
            }

            return Util.sequence(list).thenApply(p_358041_ -> {
                Map<ModelResourceLocation, BlockStateModelLoader.LoadedModel> map = new HashMap<>();

                for (BlockStateModelLoader.LoadedModels blockstatemodelloader$loadedmodels : p_358041_) {
                    if (blockstatemodelloader$loadedmodels != null) {
                        map.putAll(blockstatemodelloader$loadedmodels.models());
                    }
                }

                return new BlockStateModelLoader.LoadedModels(map);
            });
        });
    }

    private ModelManager.ReloadState loadModels(
        ProfilerFiller p_252136_, Map<ResourceLocation, AtlasSet.StitchResult> p_250646_, ModelBakery p_248945_, Object2IntMap<BlockState> p_361513_
    ) {
        p_252136_.push("baking");
        Multimap<ModelResourceLocation, Material> multimap = HashMultimap.create();
        p_248945_.bakeModels((p_343412_, p_251262_) -> {
            AtlasSet.StitchResult atlasset$stitchresult = p_250646_.get(p_251262_.atlasLocation());
            TextureAtlasSprite textureatlassprite = atlasset$stitchresult.getSprite(p_251262_.texture());
            if (textureatlassprite != null) {
                return textureatlassprite;
            } else {
                multimap.put(p_343412_, p_251262_);
                return atlasset$stitchresult.missing();
            }
        });
        multimap.asMap()
            .forEach(
                (p_344983_, p_252017_) -> LOGGER.warn(
                        "Missing textures in model {}:\n{}",
                        p_344983_,
                        p_252017_.stream()
                            .sorted(Material.COMPARATOR)
                            .map(p_325574_ -> "    " + p_325574_.atlasLocation() + ":" + p_325574_.texture())
                            .collect(Collectors.joining("\n"))
                    )
            );
        p_252136_.popPush("dispatch");
        Map<ModelResourceLocation, BakedModel> map = p_248945_.getBakedTopLevelModels();
        BakedModel bakedmodel = map.get(MissingBlockModel.VARIANT);
        Map<BlockState, BakedModel> map1 = new IdentityHashMap<>();

        for (Block block : BuiltInRegistries.BLOCK) {
            block.getStateDefinition().getPossibleStates().forEach(p_250633_ -> {
                ResourceLocation resourcelocation = p_250633_.getBlock().builtInRegistryHolder().key().location();
                BakedModel bakedmodel1 = map.getOrDefault(BlockModelShaper.stateToModelLocation(resourcelocation, p_250633_), bakedmodel);
                map1.put(p_250633_, bakedmodel1);
            });
        }

        CompletableFuture<Void> completablefuture = CompletableFuture.allOf(
            p_250646_.values().stream().map(AtlasSet.StitchResult::readyForUpload).toArray(CompletableFuture[]::new)
        );
        p_252136_.pop();
        return new ModelManager.ReloadState(p_248945_, p_361513_, bakedmodel, map1, p_250646_, completablefuture);
    }

    private static Object2IntMap<BlockState> buildModelGroups(BlockColors p_369941_, BlockStateModelLoader.LoadedModels p_360724_) {
        return ModelGroupCollector.build(p_369941_, p_360724_);
    }

    private void apply(ModelManager.ReloadState p_248996_, ProfilerFiller p_251960_) {
        p_251960_.push("upload");
        p_248996_.atlasPreparations.values().forEach(AtlasSet.StitchResult::upload);
        ModelBakery modelbakery = p_248996_.modelBakery;
        this.bakedRegistry = modelbakery.getBakedTopLevelModels();
        this.modelGroups = p_248996_.modelGroups;
        this.missingModel = p_248996_.missingModel;
        p_251960_.popPush("cache");
        this.blockModelShaper.replaceCache(p_248996_.modelCache);
        p_251960_.pop();
    }

    public boolean requiresRender(BlockState p_119416_, BlockState p_119417_) {
        if (p_119416_ == p_119417_) {
            return false;
        } else {
            int i = this.modelGroups.getInt(p_119416_);
            if (i != -1) {
                int j = this.modelGroups.getInt(p_119417_);
                if (i == j) {
                    FluidState fluidstate = p_119416_.getFluidState();
                    FluidState fluidstate1 = p_119417_.getFluidState();
                    return fluidstate != fluidstate1;
                }
            }

            return true;
        }
    }

    public TextureAtlas getAtlas(ResourceLocation p_119429_) {
        return this.atlases.getAtlas(p_119429_);
    }

    @Override
    public void close() {
        this.atlases.close();
    }

    public void updateMaxMipLevel(int p_119411_) {
        this.maxMipmapLevels = p_119411_;
    }

    @OnlyIn(Dist.CLIENT)
    static record ReloadState(
        ModelBakery modelBakery,
        Object2IntMap<BlockState> modelGroups,
        BakedModel missingModel,
        Map<BlockState, BakedModel> modelCache,
        Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations,
        CompletableFuture<Void> readyForUpload
    ) {
    }
}