package net.minecraft.world.item.equipment;

import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public interface EquipmentModels {
    ResourceLocation LEATHER = ResourceLocation.withDefaultNamespace("leather");
    ResourceLocation CHAINMAIL = ResourceLocation.withDefaultNamespace("chainmail");
    ResourceLocation IRON = ResourceLocation.withDefaultNamespace("iron");
    ResourceLocation GOLD = ResourceLocation.withDefaultNamespace("gold");
    ResourceLocation DIAMOND = ResourceLocation.withDefaultNamespace("diamond");
    ResourceLocation TURTLE_SCUTE = ResourceLocation.withDefaultNamespace("turtle_scute");
    ResourceLocation NETHERITE = ResourceLocation.withDefaultNamespace("netherite");
    ResourceLocation ARMADILLO_SCUTE = ResourceLocation.withDefaultNamespace("armadillo_scute");
    ResourceLocation ELYTRA = ResourceLocation.withDefaultNamespace("elytra");
    Map<DyeColor, ResourceLocation> CARPETS = Util.makeEnumMap(DyeColor.class, p_367306_ -> ResourceLocation.withDefaultNamespace(p_367306_.getSerializedName() + "_carpet"));
    ResourceLocation TRADER_LLAMA = ResourceLocation.withDefaultNamespace("trader_llama");

    static void bootstrap(BiConsumer<ResourceLocation, EquipmentModel> p_360870_) {
        p_360870_.accept(
            LEATHER,
            EquipmentModel.builder()
                .addHumanoidLayers(ResourceLocation.withDefaultNamespace("leather"), true)
                .addHumanoidLayers(ResourceLocation.withDefaultNamespace("leather_overlay"), false)
                .addLayers(EquipmentModel.LayerType.HORSE_BODY, EquipmentModel.Layer.leatherDyeable(ResourceLocation.withDefaultNamespace("leather"), true))
                .build()
        );
        p_360870_.accept(CHAINMAIL, onlyHumanoid("chainmail"));
        p_360870_.accept(IRON, humanoidAndHorse("iron"));
        p_360870_.accept(GOLD, humanoidAndHorse("gold"));
        p_360870_.accept(DIAMOND, humanoidAndHorse("diamond"));
        p_360870_.accept(TURTLE_SCUTE, EquipmentModel.builder().addMainHumanoidLayer(ResourceLocation.withDefaultNamespace("turtle_scute"), false).build());
        p_360870_.accept(NETHERITE, onlyHumanoid("netherite"));
        p_360870_.accept(
            ARMADILLO_SCUTE,
            EquipmentModel.builder()
                .addLayers(EquipmentModel.LayerType.WOLF_BODY, EquipmentModel.Layer.onlyIfDyed(ResourceLocation.withDefaultNamespace("armadillo_scute"), false))
                .addLayers(EquipmentModel.LayerType.WOLF_BODY, EquipmentModel.Layer.onlyIfDyed(ResourceLocation.withDefaultNamespace("armadillo_scute_overlay"), true))
                .build()
        );
        p_360870_.accept(
            ELYTRA,
            EquipmentModel.builder()
                .addLayers(EquipmentModel.LayerType.WINGS, new EquipmentModel.Layer(ResourceLocation.withDefaultNamespace("elytra"), Optional.empty(), true))
                .build()
        );

        for (Entry<DyeColor, ResourceLocation> entry : CARPETS.entrySet()) {
            DyeColor dyecolor = entry.getKey();
            ResourceLocation resourcelocation = entry.getValue();
            p_360870_.accept(
                resourcelocation,
                EquipmentModel.builder()
                    .addLayers(EquipmentModel.LayerType.LLAMA_BODY, new EquipmentModel.Layer(ResourceLocation.withDefaultNamespace(dyecolor.getSerializedName())))
                    .build()
            );
        }

        p_360870_.accept(
            TRADER_LLAMA,
            EquipmentModel.builder()
                .addLayers(EquipmentModel.LayerType.LLAMA_BODY, new EquipmentModel.Layer(ResourceLocation.withDefaultNamespace("trader_llama")))
                .build()
        );
    }

    private static EquipmentModel onlyHumanoid(String p_364139_) {
        return EquipmentModel.builder().addHumanoidLayers(ResourceLocation.withDefaultNamespace(p_364139_)).build();
    }

    private static EquipmentModel humanoidAndHorse(String p_365853_) {
        return EquipmentModel.builder()
            .addHumanoidLayers(ResourceLocation.withDefaultNamespace(p_365853_))
            .addLayers(EquipmentModel.LayerType.HORSE_BODY, EquipmentModel.Layer.leatherDyeable(ResourceLocation.withDefaultNamespace(p_365853_), false))
            .build();
    }
}