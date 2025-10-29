package net.minecraft.world.item.equipment.trim;

import java.util.Map;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.EquipmentModels;

public class TrimMaterials {
    public static final ResourceKey<TrimMaterial> QUARTZ = registryKey("quartz");
    public static final ResourceKey<TrimMaterial> IRON = registryKey("iron");
    public static final ResourceKey<TrimMaterial> NETHERITE = registryKey("netherite");
    public static final ResourceKey<TrimMaterial> REDSTONE = registryKey("redstone");
    public static final ResourceKey<TrimMaterial> COPPER = registryKey("copper");
    public static final ResourceKey<TrimMaterial> GOLD = registryKey("gold");
    public static final ResourceKey<TrimMaterial> EMERALD = registryKey("emerald");
    public static final ResourceKey<TrimMaterial> DIAMOND = registryKey("diamond");
    public static final ResourceKey<TrimMaterial> LAPIS = registryKey("lapis");
    public static final ResourceKey<TrimMaterial> AMETHYST = registryKey("amethyst");

    public static void bootstrap(BootstrapContext<TrimMaterial> p_368813_) {
        register(p_368813_, QUARTZ, Items.QUARTZ, Style.EMPTY.withColor(14931140), 0.1F);
        register(p_368813_, IRON, Items.IRON_INGOT, Style.EMPTY.withColor(15527148), 0.2F, Map.of(EquipmentModels.IRON, "iron_darker"));
        register(p_368813_, NETHERITE, Items.NETHERITE_INGOT, Style.EMPTY.withColor(6445145), 0.3F, Map.of(EquipmentModels.NETHERITE, "netherite_darker"));
        register(p_368813_, REDSTONE, Items.REDSTONE, Style.EMPTY.withColor(9901575), 0.4F);
        register(p_368813_, COPPER, Items.COPPER_INGOT, Style.EMPTY.withColor(11823181), 0.5F);
        register(p_368813_, GOLD, Items.GOLD_INGOT, Style.EMPTY.withColor(14594349), 0.6F, Map.of(EquipmentModels.GOLD, "gold_darker"));
        register(p_368813_, EMERALD, Items.EMERALD, Style.EMPTY.withColor(1155126), 0.7F);
        register(p_368813_, DIAMOND, Items.DIAMOND, Style.EMPTY.withColor(7269586), 0.8F, Map.of(EquipmentModels.DIAMOND, "diamond_darker"));
        register(p_368813_, LAPIS, Items.LAPIS_LAZULI, Style.EMPTY.withColor(4288151), 0.9F);
        register(p_368813_, AMETHYST, Items.AMETHYST_SHARD, Style.EMPTY.withColor(10116294), 1.0F);
    }

    public static Optional<Holder.Reference<TrimMaterial>> getFromIngredient(HolderLookup.Provider p_363557_, ItemStack p_369735_) {
        return p_363557_.lookupOrThrow(Registries.TRIM_MATERIAL).listElements().filter(p_361384_ -> p_369735_.is(p_361384_.value().ingredient())).findFirst();
    }

    private static void register(
        BootstrapContext<TrimMaterial> p_367478_, ResourceKey<TrimMaterial> p_366748_, Item p_365449_, Style p_363590_, float p_366126_
    ) {
        register(p_367478_, p_366748_, p_365449_, p_363590_, p_366126_, Map.of());
    }

    private static void register(
        BootstrapContext<TrimMaterial> p_369807_,
        ResourceKey<TrimMaterial> p_365636_,
        Item p_360710_,
        Style p_361695_,
        float p_368149_,
        Map<ResourceLocation, String> p_366096_
    ) {
        TrimMaterial trimmaterial = TrimMaterial.create(
            p_365636_.location().getPath(),
            p_360710_,
            p_368149_,
            Component.translatable(Util.makeDescriptionId("trim_material", p_365636_.location())).withStyle(p_361695_),
            p_366096_
        );
        p_369807_.register(p_365636_, trimmaterial);
    }

    private static ResourceKey<TrimMaterial> registryKey(String p_360778_) {
        return ResourceKey.create(Registries.TRIM_MATERIAL, ResourceLocation.withDefaultNamespace(p_360778_));
    }
}