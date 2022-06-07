package com.github.clevernucleus.dataattributes.api;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.clevernucleus.dataattributes.api.util.CacheableValue;
import com.github.clevernucleus.dataattributes.api.util.OfflinePlayerCache;
import com.github.clevernucleus.dataattributes.impl.OfflinePlayerCacheImpl;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * 
 * The core API access - provides access to the modid and safe static attribute instantiation.
 * @author CleverNucleus
 *
 */
public final class DataAttributesAPI {
	
	/**
	 * The modid for Data Attributes.
	 */
	public static final String MODID = "dataattributes";
	
	/**
	 * @param attributeKey Attribute registry key.
	 * @return A supplier getting the registered attribute assigned to the input key. 
	 * Uses a supplier because attributes added using json are null until datapacks are loaded/synced to the client,
	 * so static initialisation would not work. Using this you can safely access an attribute through a static reference.
	 */
	public static Supplier<EntityAttribute> getAttribute(final Identifier attributeKey) {
		return () -> Registry.ATTRIBUTE.get(attributeKey);
	}
	
	/**
	 * Allows for an Optional-like use of attributes that may or may not exist all the time. This is the correct way of getting and using
	 * values from attributes loaded by datapacks.
	 * @param <T>
	 * @param livingEntity
	 * @param entityAttribute
	 * @param fallback
	 * @param function
	 * @return If the input attribute is both registered to the game and present on the input entity, returns the returning value of the input function.
	 * Else returns the fallback input.
	 */
	public static <T> T ifPresent(final LivingEntity livingEntity, Supplier<EntityAttribute> entityAttribute, final T fallback, Function<Double, T> function) {
		AttributeContainer container = livingEntity.getAttributes();
		EntityAttribute attribute = entityAttribute.get();
		
		if(attribute != null && container.hasAttribute(attribute)) {
			return function.apply(container.getValue(attribute));
		}
		
		return fallback;
	}
	
	/**
	 * Registers a cacheable value to the server: these are keys that instruct the server to cache some data from players when they disconnect.
	 * @param <V>
	 * @param key
	 * @return
	 */
	public static <V> CacheableValue<V> registerCacheableValue(final CacheableValue<V> key) {
		return com.github.clevernucleus.dataattributes.impl.OfflinePlayerCacheImpl.register(key);
	}
	
	/**
	 * Get access to the offline player cache object. This should only be used on the logical server.
	 * @param server
	 * @return
	 */
	public static Optional<OfflinePlayerCache> getOfflinePlayerCache(final net.minecraft.server.MinecraftServer server) {
		OfflinePlayerCacheImpl.Provider offlinePlayerCache = new OfflinePlayerCacheImpl.Provider(server);
		return offlinePlayerCache.isEmpty() ? Optional.empty() : Optional.of(offlinePlayerCache);
	}
}
