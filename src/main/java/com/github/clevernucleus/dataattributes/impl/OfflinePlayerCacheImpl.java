package com.github.clevernucleus.dataattributes.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

import com.github.clevernucleus.dataattributes.api.util.CacheableValue;
import com.github.clevernucleus.dataattributes.api.util.OfflinePlayerCache;
import com.github.clevernucleus.dataattributes.mutable.MutableOfflinePlayerCache;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.authlib.GameProfile;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.WorldProperties;

public final class OfflinePlayerCacheImpl {
	private static final Map<Identifier, CacheableValue<?>> TYPES = new HashMap<>();
	private final Map<UUID, Map<CacheableValue<?>, ?>> cache;
	private final BiMap<String, UUID> nameToId;
	
	public OfflinePlayerCacheImpl() {
		this.nameToId = HashBiMap.create();
		this.cache = new HashMap<>();
	}
	
	@SuppressWarnings("unchecked")
	private <V> V fromCache(final UUID uuid, final CacheableValue<V> key) {
		Map<CacheableValue<?>, ?> value = this.cache.get(uuid);
		
		if(value == null) return (V)null;
		return (V)value.get(key);
	}
	
	private boolean ifValidForCache(final ServerPlayerEntity playerIn, final BiFunction<UUID, String, Boolean> action) {
		if(playerIn == null || TYPES.isEmpty()) return false;
		GameProfile profile = playerIn.getGameProfile();
		
		if(profile == null) return false;
		UUID uuid = profile.getId();
		String name = profile.getName();
		
		if(uuid == null || name == null || name.equals("")) return false;
		return action.apply(uuid, name);
	}
	
	protected <V> V get(final MinecraftServer server, final UUID uuid, final CacheableValue<V> key) {
		if(uuid == null) return (V)null;
		ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
		
		if(player == null) {
			return this.fromCache(uuid, key);
		}
		
		return key.get(player);
	}
	
	protected <V> V get(final MinecraftServer server, final String name, final CacheableValue<V> key) {
		if(name == null || name.equals("")) return (V)null;
		ServerPlayerEntity player = server.getPlayerManager().getPlayer(name);
		
		if(player == null) {
			UUID uuid = this.nameToId.get(name);
			
			if(uuid == null) return (V)null;
			return this.fromCache(uuid, key);
		}
		
		return key.get(player);
	}
	
	protected Collection<UUID> playerIds(final MinecraftServer server) {
		Set<UUID> set = new HashSet<UUID>();
		this.nameToId.values().forEach(set::add);
		
		for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			GameProfile profile = player.getGameProfile();
			
			if(profile == null) continue;
			UUID uuid = profile.getId();
			
			if(uuid == null) continue;
			set.add(uuid);
		}
		
		return set;
	}
	
	protected Collection<String> playerNames(final MinecraftServer server) {
		Set<String> set = new HashSet<String>();
		this.nameToId.inverse().values().forEach(set::add);
		
		for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			GameProfile profile = player.getGameProfile();
			
			if(profile == null) continue;
			String name = profile.getName();
			
			if(name == null || name.equals("")) continue;
			set.add(name);
		}
		
		return set;
	}
	
	protected boolean isPlayerCached(final UUID uuid) {
		return this.nameToId.containsValue(uuid);
	}
	
	protected boolean isPlayerCached(final String name) {
		return this.nameToId.containsKey(name);
	}
	
	public boolean cache(final ServerPlayerEntity playerIn) {
		return this.ifValidForCache(playerIn, (uuid, name) -> {
			Map<CacheableValue<?>, Object> value = new HashMap<>();
			TYPES.forEach((identifier, key) -> value.put(key, key.get(playerIn)));
			this.cache.put(uuid, value);
			this.nameToId.put(name, uuid);
			return true;
		});
	}
	
	public boolean uncache(final ServerPlayerEntity playerIn) {
		return this.ifValidForCache(playerIn, (uuid, name) -> {
			this.cache.remove(uuid);
			this.nameToId.remove(name);
			return true;
		});
	}
	
	public <V> boolean uncache(final UUID uuid, final CacheableValue<V> key) {
		if(uuid == null || !TYPES.containsValue(key)) return false;
		Map<CacheableValue<?>, ?> value = this.cache.get(uuid);
		
		if(value == null) return false;
		if(value.remove(key) != null) {
			if(value.isEmpty()) {
				this.cache.remove(uuid);
				this.nameToId.inverse().remove(uuid);
			}
			
			return true;
		}
		
		return false;
	}
	
	public <V> boolean uncache(final String name, final CacheableValue<V> key) {
		if(name == null || name.equals("") || !TYPES.containsValue(key)) return false;
		UUID uuid = this.nameToId.get(name);
		return this.uncache(uuid, key);
	}
	
	public boolean uncache(final UUID uuid) {
		if(uuid == null) return false;
		final boolean wasCached = this.isPlayerCached(uuid);
		this.cache.remove(uuid);
		this.nameToId.inverse().remove(uuid);
		return wasCached;
	}
	
	public boolean uncache(final String name) {
		if(name == null || name.isEmpty()) return false;
		UUID uuid = this.nameToId.get(name);
		return this.uncache(uuid);
	}
	
	public NbtList writeToNbt() {
		NbtList tag = new NbtList();
		Map<UUID, String> names = this.nameToId.inverse();
		
		for(UUID uuid : this.cache.keySet()) {
			Map<CacheableValue<?>, ?> data = this.cache.get(uuid);
			NbtCompound entry = new NbtCompound();
			entry.putUuid("uuid", uuid);
			entry.putString("name", names.getOrDefault(uuid, ""));
			
			for(CacheableValue<?> key : data.keySet()) {
				NbtCompound entry2 = new NbtCompound();
				key.writeToNbt(entry2, data.get(key));
				entry.put(key.toString(), entry2);
			}
			
			tag.add(entry);
		}
		
		return tag;
	}
	
	public void readFromNbt(final NbtList tag) {
		if(tag == null) return;
		
		this.cache.clear();
		this.nameToId.clear();
		
		for(int i = 0; i < tag.size(); i++) {
			NbtCompound entry = tag.getCompound(i);
			UUID uuid = entry.getUuid("uuid");
			String name = entry.getString("name");
			
			if(name.equals("")) continue;
			Map<CacheableValue<?>, Object> data = new HashMap<>();
			
			for(String id : entry.getKeys()) {
				CacheableValue<?> key = TYPES.getOrDefault(new Identifier(id), (CacheableValue<?>)null);
				
				if(key == null) continue;
				Object value = key.readFromNbt(entry.getCompound(id));
				data.put(key, value);
			}
			
			this.cache.put(uuid, data);
			this.nameToId.put(name, uuid);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <V> CacheableValue<V> register(final CacheableValue<V> key) {
		return (CacheableValue<V>)TYPES.computeIfAbsent(new Identifier(key.toString()), id -> key);
	}
	
	public static Collection<Identifier> keys() {
		return TYPES.keySet();
	}
	
	public static CacheableValue<?> getKey(final Identifier key) {
		return TYPES.getOrDefault(key, (CacheableValue<?>)null);
	}
	
	public static class Provider implements OfflinePlayerCache {
		private final MinecraftServer server;
		protected OfflinePlayerCacheImpl offlinePlayerCache;
		
		public Provider(final MinecraftServer server) {
			WorldProperties worldProperties = server.getOverworld().getLevelProperties();
			
			if(worldProperties instanceof MutableOfflinePlayerCache) {
				this.offlinePlayerCache = ((MutableOfflinePlayerCache)worldProperties).offlinePlayerCache();
			}
			
			this.server = server;
		}
		
		public boolean isEmpty() {
			return this.offlinePlayerCache == null;
		}
		
		@Override
		public <V> V get(final UUID uuid, final CacheableValue<V> key) {
			return this.offlinePlayerCache.get(this.server, uuid, key);
		}
		
		@Override
		public <V> V get(final String name, final CacheableValue<V> key) {
			return this.offlinePlayerCache.get(this.server, name, key);
		}
		
		@Override
		public Collection<UUID> playerIds() {
			return this.offlinePlayerCache.playerIds(this.server);
		}
		
		@Override
		public Collection<String> playerNames() {
			return this.offlinePlayerCache.playerNames(this.server);
		}
		
		@Override
		public boolean isPlayerCached(final UUID uuid) {
			return this.offlinePlayerCache.isPlayerCached(uuid);
		}
		
		@Override
		public boolean isPlayerCached(final String name) {
			return this.offlinePlayerCache.isPlayerCached(name);
		}
	}
}
