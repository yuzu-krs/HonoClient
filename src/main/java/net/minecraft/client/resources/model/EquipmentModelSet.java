package net.minecraft.client.resources.model;

import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.equipment.EquipmentModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EquipmentModelSet extends SimpleJsonResourceReloadListener<EquipmentModel> {
    public static final EquipmentModel MISSING_MODEL = new EquipmentModel(Map.of());
    private Map<ResourceLocation, EquipmentModel> models = Map.of();

    public EquipmentModelSet() {
        super(EquipmentModel.CODEC, "models/equipment");
    }

    protected void apply(Map<ResourceLocation, EquipmentModel> p_367691_, ResourceManager p_366183_, ProfilerFiller p_366026_) {
        this.models = Map.copyOf(p_367691_);
    }

    public EquipmentModel get(ResourceLocation p_368918_) {
        return this.models.getOrDefault(p_368918_, MISSING_MODEL);
    }
}