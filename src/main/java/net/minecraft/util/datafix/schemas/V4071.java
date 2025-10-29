package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V4071 extends NamespacedSchema {
    public V4071(int p_361008_, Schema p_362992_) {
        super(p_361008_, p_362992_);
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_367045_) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_367045_);
        p_367045_.register(map, "minecraft:creaking", () -> V100.equipment(p_367045_));
        p_367045_.register(map, "minecraft:creaking_transient", () -> V100.equipment(p_367045_));
        return map;
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_366043_) {
        Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(p_366043_);
        p_366043_.register(map, "minecraft:creaking_heart", () -> DSL.optionalFields());
        return map;
    }
}