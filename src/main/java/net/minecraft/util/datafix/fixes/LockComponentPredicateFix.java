package net.minecraft.util.datafix.fixes;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class LockComponentPredicateFix extends ItemStackComponentRemainderFix {
    public static final Escaper ESCAPER = Escapers.builder().addEscape('"', "\\\"").addEscape('\\', "\\\\").build();

    public LockComponentPredicateFix(Schema p_370065_) {
        super(p_370065_, "LockComponentPredicateFix", "minecraft:lock");
    }

    @Override
    protected <T> Dynamic<T> fixComponent(Dynamic<T> p_360989_) {
        return fixLock(p_360989_);
    }

    public static <T> Dynamic<T> fixLock(Dynamic<T> p_369566_) {
        Optional<String> optional = p_369566_.asString().result();
        if (optional.isPresent()) {
            Dynamic<T> dynamic = p_369566_.createString("\"" + ESCAPER.escape(optional.get()) + "\"");
            Dynamic<T> dynamic1 = p_369566_.emptyMap().set("minecraft:custom_name", dynamic);
            return p_369566_.emptyMap().set("components", dynamic1);
        } else {
            return p_369566_.emptyMap();
        }
    }
}