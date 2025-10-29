package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.WorldVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.advancements.packs.VanillaAdvancementProvider;
import net.minecraft.data.advancements.packs.WinterDropAdvancementProvider;
import net.minecraft.data.info.BiomeParametersDumpReport;
import net.minecraft.data.info.BlockListReport;
import net.minecraft.data.info.CommandsReport;
import net.minecraft.data.info.DatapackStructureReport;
import net.minecraft.data.info.ItemListReport;
import net.minecraft.data.info.PacketReport;
import net.minecraft.data.info.RegistryDumpReport;
import net.minecraft.data.loot.packs.TradeRebalanceLootTableProvider;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.data.loot.packs.WinterDropLootTableProvider;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.data.models.EquipmentModelProvider;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.data.recipes.packs.WinterDropRecipeProvider;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.TradeRebalanceRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.registries.WinterDropRegistries;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.data.structures.SnbtToNbt;
import net.minecraft.data.structures.StructureUpdater;
import net.minecraft.data.tags.BannerPatternTagsProvider;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.CatVariantTagsProvider;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.FlatLevelGeneratorPresetTagsProvider;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.GameEventTagsProvider;
import net.minecraft.data.tags.InstrumentTagsProvider;
import net.minecraft.data.tags.PaintingVariantTagsProvider;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.data.tags.StructureTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.tags.TradeRebalanceEnchantmentTagsProvider;
import net.minecraft.data.tags.TradeRebalanceStructureTagsProvider;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.data.tags.VanillaEnchantmentTagsProvider;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.data.tags.WinterDropBiomeTagsProvider;
import net.minecraft.data.tags.WinterDropBlockTagsProvider;
import net.minecraft.data.tags.WinterDropEntityTypeTagsProvider;
import net.minecraft.data.tags.WinterDropItemTagsProvider;
import net.minecraft.data.tags.WorldPresetTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.levelgen.structure.Structure;

public class Main {
    @DontObfuscate
    public static void main(String[] p_129669_) throws IOException {
        SharedConstants.tryDetectVersion();
        OptionParser optionparser = new OptionParser();
        OptionSpec<Void> optionspec = optionparser.accepts("help", "Show the help menu").forHelp();
        OptionSpec<Void> optionspec1 = optionparser.accepts("server", "Include server generators");
        OptionSpec<Void> optionspec2 = optionparser.accepts("client", "Include client generators");
        OptionSpec<Void> optionspec3 = optionparser.accepts("dev", "Include development tools");
        OptionSpec<Void> optionspec4 = optionparser.accepts("reports", "Include data reports");
        OptionSpec<Void> optionspec5 = optionparser.accepts("validate", "Validate inputs");
        OptionSpec<Void> optionspec6 = optionparser.accepts("all", "Include all generators");
        OptionSpec<String> optionspec7 = optionparser.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated");
        OptionSpec<String> optionspec8 = optionparser.accepts("input", "Input folder").withRequiredArg();
        OptionSet optionset = optionparser.parse(p_129669_);
        if (!optionset.has(optionspec) && optionset.hasOptions()) {
            Path path = Paths.get(optionspec7.value(optionset));
            boolean flag = optionset.has(optionspec6);
            boolean flag1 = flag || optionset.has(optionspec2);
            boolean flag2 = flag || optionset.has(optionspec1);
            boolean flag3 = flag || optionset.has(optionspec3);
            boolean flag4 = flag || optionset.has(optionspec4);
            boolean flag5 = flag || optionset.has(optionspec5);
            DataGenerator datagenerator = createStandardGenerator(
                path,
                optionset.valuesOf(optionspec8).stream().map(p_129659_ -> Paths.get(p_129659_)).collect(Collectors.toList()),
                flag1,
                flag2,
                flag3,
                flag4,
                flag5,
                SharedConstants.getCurrentVersion(),
                true
            );
            datagenerator.run();
        } else {
            optionparser.printHelpOn(System.out);
        }
    }

    private static <T extends DataProvider> DataProvider.Factory<T> bindRegistries(
        BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> p_256618_, CompletableFuture<HolderLookup.Provider> p_256515_
    ) {
        return p_255476_ -> p_256618_.apply(p_255476_, p_256515_);
    }

    public static DataGenerator createStandardGenerator(
        Path p_236680_,
        Collection<Path> p_236681_,
        boolean p_236682_,
        boolean p_236683_,
        boolean p_236684_,
        boolean p_236685_,
        boolean p_236686_,
        WorldVersion p_236687_,
        boolean p_236688_
    ) {
        DataGenerator datagenerator = new DataGenerator(p_236680_, p_236687_, p_236688_);
        DataGenerator.PackGenerator datagenerator$packgenerator = datagenerator.getVanillaPack(p_236682_ || p_236683_);
        datagenerator$packgenerator.addProvider(p_253388_ -> new SnbtToNbt(p_253388_, p_236681_).addFilter(new StructureUpdater()));
        CompletableFuture<HolderLookup.Provider> completablefuture1 = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
        DataGenerator.PackGenerator datagenerator$packgenerator1 = datagenerator.getVanillaPack(p_236682_);
        datagenerator$packgenerator1.addProvider(ModelProvider::new);
        datagenerator$packgenerator1.addProvider(EquipmentModelProvider::new);
        DataGenerator.PackGenerator datagenerator$packgenerator2 = datagenerator.getVanillaPack(p_236683_);
        datagenerator$packgenerator2.addProvider(bindRegistries(RegistriesDatapackGenerator::new, completablefuture1));
        datagenerator$packgenerator2.addProvider(bindRegistries(VanillaAdvancementProvider::create, completablefuture1));
        datagenerator$packgenerator2.addProvider(bindRegistries(VanillaLootTableProvider::create, completablefuture1));
        datagenerator$packgenerator2.addProvider(bindRegistries(VanillaRecipeProvider.Runner::new, completablefuture1));
        TagsProvider<Block> tagsprovider5 = datagenerator$packgenerator2.addProvider(bindRegistries(VanillaBlockTagsProvider::new, completablefuture1));
        TagsProvider<Item> tagsprovider = datagenerator$packgenerator2.addProvider(
            p_274753_ -> new VanillaItemTagsProvider(p_274753_, completablefuture1, tagsprovider5.contentsGetter())
        );
        TagsProvider<Biome> tagsprovider1 = datagenerator$packgenerator2.addProvider(bindRegistries(BiomeTagsProvider::new, completablefuture1));
        TagsProvider<BannerPattern> tagsprovider2 = datagenerator$packgenerator2.addProvider(bindRegistries(BannerPatternTagsProvider::new, completablefuture1));
        TagsProvider<Structure> tagsprovider3 = datagenerator$packgenerator2.addProvider(bindRegistries(StructureTagsProvider::new, completablefuture1));
        datagenerator$packgenerator2.addProvider(bindRegistries(CatVariantTagsProvider::new, completablefuture1));
        datagenerator$packgenerator2.addProvider(bindRegistries(DamageTypeTagsProvider::new, completablefuture1));
        datagenerator$packgenerator2.addProvider(bindRegistries(EntityTypeTagsProvider::new, completablefuture1));
        datagenerator$packgenerator2.addProvider(bindRegistries(FlatLevelGeneratorPresetTagsProvider::new, completablefuture1));
        datagenerator$packgenerator2.addProvider(bindRegistries(FluidTagsProvider::new, completablefuture1));
        datagenerator$packgenerator2.addProvider(bindRegistries(GameEventTagsProvider::new, completablefuture1));
        datagenerator$packgenerator2.addProvider(bindRegistries(InstrumentTagsProvider::new, completablefuture1));
        datagenerator$packgenerator2.addProvider(bindRegistries(PaintingVariantTagsProvider::new, completablefuture1));
        datagenerator$packgenerator2.addProvider(bindRegistries(PoiTypeTagsProvider::new, completablefuture1));
        datagenerator$packgenerator2.addProvider(bindRegistries(WorldPresetTagsProvider::new, completablefuture1));
        datagenerator$packgenerator2.addProvider(bindRegistries(VanillaEnchantmentTagsProvider::new, completablefuture1));
        datagenerator$packgenerator2 = datagenerator.getVanillaPack(p_236684_);
        datagenerator$packgenerator2.addProvider(p_253386_ -> new NbtToSnbt(p_253386_, p_236681_));
        datagenerator$packgenerator2 = datagenerator.getVanillaPack(p_236685_);
        datagenerator$packgenerator2.addProvider(bindRegistries(BiomeParametersDumpReport::new, completablefuture1));
        datagenerator$packgenerator2.addProvider(bindRegistries(ItemListReport::new, completablefuture1));
        datagenerator$packgenerator2.addProvider(bindRegistries(BlockListReport::new, completablefuture1));
        datagenerator$packgenerator2.addProvider(bindRegistries(CommandsReport::new, completablefuture1));
        datagenerator$packgenerator2.addProvider(RegistryDumpReport::new);
        datagenerator$packgenerator2.addProvider(PacketReport::new);
        datagenerator$packgenerator2.addProvider(DatapackStructureReport::new);
        CompletableFuture<RegistrySetBuilder.PatchedRegistries> completablefuture2 = TradeRebalanceRegistries.createLookup(completablefuture1);
        CompletableFuture<HolderLookup.Provider> completablefuture_provider = completablefuture2.thenApply(RegistrySetBuilder.PatchedRegistries::patches);
        DataGenerator.PackGenerator datagenerator$packgenerator3 = datagenerator.getBuiltinDatapack(p_236683_, "trade_rebalance");
        datagenerator$packgenerator3.addProvider(bindRegistries(RegistriesDatapackGenerator::new, completablefuture_provider));
        datagenerator$packgenerator3.addProvider(
            p_296336_ -> PackMetadataGenerator.forFeaturePack(
                    p_296336_, Component.translatable("dataPack.trade_rebalance.description"), FeatureFlagSet.of(FeatureFlags.TRADE_REBALANCE)
                )
        );
        datagenerator$packgenerator3.addProvider(bindRegistries(TradeRebalanceLootTableProvider::create, completablefuture1));
        datagenerator$packgenerator3.addProvider(bindRegistries(TradeRebalanceStructureTagsProvider::new, completablefuture1));
        datagenerator$packgenerator3.addProvider(bindRegistries(TradeRebalanceEnchantmentTagsProvider::new, completablefuture1));
        datagenerator$packgenerator2 = datagenerator.getBuiltinDatapack(p_236683_, "redstone_experiments");
        datagenerator$packgenerator2.addProvider(
            p_358165_ -> PackMetadataGenerator.forFeaturePack(
                    p_358165_, Component.translatable("dataPack.redstone_experiments.description"), FeatureFlagSet.of(FeatureFlags.REDSTONE_EXPERIMENTS)
                )
        );
        datagenerator$packgenerator2 = datagenerator.getBuiltinDatapack(p_236683_, "minecart_improvements");
        datagenerator$packgenerator2.addProvider(
            p_358177_ -> PackMetadataGenerator.forFeaturePack(
                    p_358177_, Component.translatable("dataPack.minecart_improvements.description"), FeatureFlagSet.of(FeatureFlags.MINECART_IMPROVEMENTS)
                )
        );
        CompletableFuture<RegistrySetBuilder.PatchedRegistries> completablefuture3 = WinterDropRegistries.createLookup(completablefuture1);
        var completablefuture = completablefuture3.thenApply(RegistrySetBuilder.PatchedRegistries::full);
        datagenerator$packgenerator3 = datagenerator.getBuiltinDatapack(p_236683_, "winter_drop");
        datagenerator$packgenerator3.addProvider(
            bindRegistries(RegistriesDatapackGenerator::new, completablefuture3.thenApply(RegistrySetBuilder.PatchedRegistries::patches))
        );
        datagenerator$packgenerator3.addProvider(bindRegistries(WinterDropRecipeProvider.Runner::new, completablefuture));
        TagsProvider<Block> tagsprovider4 = datagenerator$packgenerator3.addProvider(
            p_358176_ -> new WinterDropBlockTagsProvider(p_358176_, completablefuture, tagsprovider5.contentsGetter())
        );
        datagenerator$packgenerator3.addProvider(
            p_358172_ -> new WinterDropItemTagsProvider(p_358172_, completablefuture, tagsprovider.contentsGetter(), tagsprovider4.contentsGetter())
        );
        datagenerator$packgenerator3.addProvider(p_358168_ -> new WinterDropBiomeTagsProvider(p_358168_, completablefuture, tagsprovider1.contentsGetter()));
        datagenerator$packgenerator3.addProvider(bindRegistries(WinterDropLootTableProvider::create, completablefuture));
        datagenerator$packgenerator3.addProvider(
            p_358173_ -> PackMetadataGenerator.forFeaturePack(
                    p_358173_, Component.translatable("dataPack.winter_drop.description"), FeatureFlagSet.of(FeatureFlags.WINTER_DROP)
                )
        );
        datagenerator$packgenerator3.addProvider(bindRegistries(WinterDropEntityTypeTagsProvider::new, completablefuture));
        datagenerator$packgenerator3.addProvider(bindRegistries(WinterDropAdvancementProvider::create, completablefuture));
        return datagenerator;
    }
}
