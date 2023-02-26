package com.github.clevernucleus.dataattributes.mutable;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public interface MutableSimpleRegistry<T> {
	default void removeCachedIds(Registry<T> registry) {}
	default void cacheId(Identifier id) {}
}
