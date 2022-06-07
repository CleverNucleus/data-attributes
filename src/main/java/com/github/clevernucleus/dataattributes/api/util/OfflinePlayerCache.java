package com.github.clevernucleus.dataattributes.api.util;

import java.util.Collection;
import java.util.UUID;

/**
 * Interface to use methods from the offline player cache.
 * 
 * @author CleverNucleus
 *
 */
public interface OfflinePlayerCache {
	
	/**
	 * If the Player is offline and exists in the cache, retrieves the last cached value. If the player is online, retrieves the player's 
	 * current value.
	 * @param <V>
	 * @param uuid Player UUID
	 * @param key
	 * @return
	 */
	<V> V get(final UUID uuid, final CacheableValue<V> key);
	
	/**
	 * If the Player is offline and exists in the cache, retrieves the last cached value. If the player is online, retrieves the player's 
	 * current value.
	 * @param <V>
	 * @param uuid Player Name
	 * @param key
	 * @return
	 */
	<V> V get(final String name, final CacheableValue<V> key);
	
	/**
	 * @return Returns all offline/cached and online players' UUIDs.
	 */
	Collection<UUID> playerIds();
	
	/**
	 * @return Returns all offline/cached and online players' names.
	 */
	Collection<String> playerNames();
	
	/**
	 * Tests if the player with the input UUID exists in the cache.
	 * @param uuid
	 * @return
	 */
	boolean isPlayerCached(final UUID uuid);
	
	/**
	 * Tests if the player with the input name exists in the cache.
	 * @param uuid
	 * @return
	 */
	boolean isPlayerCached(final String name);
}
