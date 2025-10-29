package net.minecraft.server.packs.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult.Error;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public abstract class SimpleJsonResourceReloadListener<T> extends SimplePreparableReloadListener<Map<ResourceLocation, T>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final DynamicOps<JsonElement> ops;
    private final Codec<T> codec;
    private final String directory;

    protected SimpleJsonResourceReloadListener(HolderLookup.Provider p_368426_, Codec<T> p_362926_, String p_364031_) {
        this(p_368426_.createSerializationContext(JsonOps.INSTANCE), p_362926_, p_364031_);
    }

    protected SimpleJsonResourceReloadListener(Codec<T> p_370137_, String p_10769_) {
        this(JsonOps.INSTANCE, p_370137_, p_10769_);
    }

    private SimpleJsonResourceReloadListener(DynamicOps<JsonElement> p_362832_, Codec<T> p_361980_, String p_361491_) {
        this.ops = p_362832_;
        this.codec = p_361980_;
        this.directory = p_361491_;
    }

    protected Map<ResourceLocation, T> prepare(ResourceManager p_10771_, ProfilerFiller p_10772_) {
        Map<ResourceLocation, T> map = new HashMap<>();
        scanDirectory(p_10771_, this.directory, this.ops, this.codec, map);
        return map;
    }

    public static <T> void scanDirectory(
        ResourceManager p_279308_, String p_279131_, DynamicOps<JsonElement> p_369854_, Codec<T> p_368755_, Map<ResourceLocation, T> p_279404_
    ) {
        FileToIdConverter filetoidconverter = FileToIdConverter.json(p_279131_);

        for (Entry<ResourceLocation, Resource> entry : filetoidconverter.listMatchingResources(p_279308_).entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            ResourceLocation resourcelocation1 = filetoidconverter.fileToId(resourcelocation);

            try (Reader reader = entry.getValue().openAsReader()) {
                p_368755_.parse(p_369854_, JsonParser.parseReader(reader)).ifSuccess(p_370131_ -> {
                    if (p_279404_.putIfAbsent(resourcelocation1, (T)p_370131_) != null) {
                        throw new IllegalStateException("Duplicate data file ignored with ID " + resourcelocation1);
                    }
                }).ifError(p_362245_ -> LOGGER.error("Couldn't parse data file '{}' from '{}': {}", resourcelocation1, resourcelocation, p_362245_));
            } catch (IllegalArgumentException | IOException | JsonParseException jsonparseexception) {
                LOGGER.error("Couldn't parse data file '{}' from '{}'", resourcelocation1, resourcelocation, jsonparseexception);
            }
        }
    }
}