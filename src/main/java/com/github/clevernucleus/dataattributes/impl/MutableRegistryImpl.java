package com.github.clevernucleus.dataattributes.impl;

import com.github.clevernucleus.dataattributes.mutable.MutableSimpleRegistry;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class MutableRegistryImpl {
	
	@SuppressWarnings("unchecked")
	public static <T extends EntityAttribute> T register(Registry<T> registry, Identifier id, T entry) {
		((MutableSimpleRegistry<T>)registry).cacheId(id);
		return Registry.register(registry, id, entry);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends EntityAttribute> void unregister(Registry<T> registry) {
		((MutableSimpleRegistry<T>)registry).removeCachedIds(registry);
	}
}
