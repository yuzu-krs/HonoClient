package net.minecraft.resources;

@FunctionalInterface
public interface DependantName<T, V> {
    V get(ResourceKey<T> p_368676_);

    static <T, V> DependantName<T, V> fixed(V p_363313_) {
        return p_365225_ -> p_363313_;
    }
}