package com.github.clevernucleus.dataattributes.mutable;

import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;

public interface MutableSimpleRegistry<T> {
	default void removeCachedIds(Registry<T> registry) {}
	default void cacheId(Identifier id) {}
}
