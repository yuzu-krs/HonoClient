package net.minecraft.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public interface DataProvider {
    ToIntFunction<String> FIXED_ORDER_FIELDS = Util.make(new Object2IntOpenHashMap<>(), p_236070_ -> {
        p_236070_.put("type", 0);
        p_236070_.put("parent", 1);
        p_236070_.defaultReturnValue(2);
    });
    Comparator<String> KEY_COMPARATOR = Comparator.comparingInt(FIXED_ORDER_FIELDS).thenComparing(p_236077_ -> (String)p_236077_);
    Logger LOGGER = LogUtils.getLogger();

    CompletableFuture<?> run(CachedOutput p_236071_);

    String getName();

    static <T> CompletableFuture<?> saveAll(CachedOutput p_369703_, Codec<T> p_369384_, PackOutput.PathProvider p_366772_, Map<ResourceLocation, T> p_364004_) {
        return CompletableFuture.allOf(
            p_364004_.entrySet()
                .stream()
                .map(p_358164_ -> saveStable(p_369703_, p_369384_, p_358164_.getValue(), p_366772_.json(p_358164_.getKey())))
                .toArray(CompletableFuture[]::new)
        );
    }

    static <T> CompletableFuture<?> saveStable(CachedOutput p_300299_, HolderLookup.Provider p_330662_, Codec<T> p_297797_, T p_300766_, Path p_299101_) {
        RegistryOps<JsonElement> registryops = p_330662_.createSerializationContext(JsonOps.INSTANCE);
        return saveStable(p_300299_, registryops, p_297797_, p_300766_, p_299101_);
    }

    static <T> CompletableFuture<?> saveStable(CachedOutput p_364924_, Codec<T> p_367616_, T p_369344_, Path p_369481_) {
        return saveStable(p_364924_, JsonOps.INSTANCE, p_367616_, p_369344_, p_369481_);
    }

    private static <T> CompletableFuture<?> saveStable(
        CachedOutput p_366662_, DynamicOps<JsonElement> p_369056_, Codec<T> p_365700_, T p_360791_, Path p_368062_
    ) {
        JsonElement jsonelement = p_365700_.encodeStart(p_369056_, p_360791_).getOrThrow();
        return saveStable(p_366662_, jsonelement, p_368062_);
    }

    static CompletableFuture<?> saveStable(CachedOutput p_253653_, JsonElement p_254542_, Path p_254467_) {
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);

                try (JsonWriter jsonwriter = new JsonWriter(new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8))) {
                    jsonwriter.setSerializeNulls(false);
                    jsonwriter.setIndent("  ");
                    GsonHelper.writeValue(jsonwriter, p_254542_, KEY_COMPARATOR);
                }

                p_253653_.writeIfNeeded(p_254467_, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
            } catch (IOException ioexception) {
                LOGGER.error("Failed to save file to {}", p_254467_, ioexception);
            }
        }, Util.backgroundExecutor().forName("saveStable"));
    }

    @FunctionalInterface
    public interface Factory<T extends DataProvider> {
        T create(PackOutput p_253851_);
    }
}