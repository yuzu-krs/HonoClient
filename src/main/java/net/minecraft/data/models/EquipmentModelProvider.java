package net.minecraft.data.models;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.EquipmentModel;
import net.minecraft.world.item.equipment.EquipmentModels;

public class EquipmentModelProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public EquipmentModelProvider(PackOutput p_363709_) {
        this.pathProvider = p_363709_.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/equipment");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput p_361266_) {
        Map<ResourceLocation, EquipmentModel> map = new HashMap<>();
        EquipmentModels.bootstrap((p_369842_, p_361854_) -> {
            if (map.putIfAbsent(p_369842_, p_361854_) != null) {
                throw new IllegalStateException("Tried to register equipment model twice for id: " + p_369842_);
            }
        });
        return DataProvider.saveAll(p_361266_, EquipmentModel.CODEC, this.pathProvider, map);
    }

    @Override
    public String getName() {
        return "Equipment Model Definitions";
    }
}