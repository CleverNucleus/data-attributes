package com.github.clevernucleus.dataattributes.api.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * This is the base abstract CacheableValue key to be implemented. Serves as a key to get specific data, as well as an 
 * instruction set to tell the server how to read/write this data and how to get the data from a player initially when 
 * cached as well as when the player is online.
 * 
 * @author CleverNucleus
 *
 * @param <V> The value type to be cached: can be anything (primitives, objects) as long as you have a valid read/write from/to 
 * nbt implementation.
 */
public abstract class CacheableValue<V> {
	private final Identifier key;
	
	/**
	 * Should be in the form <code>modid:value_name</code>; example: <code>dataattributes:current_health</code>.
	 * @param key
	 */
	public CacheableValue(final Identifier key) {this.key = key; }
	
	/**
	 * When a player is online, gets the value from the player. When the player disconnects, this is used to get the value 
	 * to store in the server cache.
	 * @param player
	 * @return
	 */
	public abstract V get(final ServerPlayerEntity player);
	
	/** 
	 * Reads the value from nbt.
	 * @param tag
	 * @return
	 */
	public abstract V readFromNbt(final NbtCompound tag);
	
	/**
	 * Writes the value to nbt.
	 * @param tag
	 * @param value
	 */
	public abstract void writeToNbt(final NbtCompound tag, Object value);
	
	@Override
	public int hashCode() {
		return this.key.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(obj instanceof CacheableValue) return false;
		
		CacheableValue<?> cacheableValue = (CacheableValue<?>)obj;
		return this.key.equals(cacheableValue.key);
	}
	
	@Override
	public String toString() {
		return this.key.toString();
	}
}
