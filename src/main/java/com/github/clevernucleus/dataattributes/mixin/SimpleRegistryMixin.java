package com.github.clevernucleus.dataattributes.mixin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.clevernucleus.dataattributes.mutable.MutableSimpleRegistry;
import com.mojang.serialization.Lifecycle;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

@Mixin(SimpleRegistry.class)
abstract class SimpleRegistryMixin<T> implements MutableSimpleRegistry<T> {
	
	@Final
	@Shadow
	private ObjectList<T> rawIdToEntry;
	
	@Final
	@Shadow
	private Object2IntMap<T> entryToRawId;
	
	@Final
	@Shadow
	private Map<Identifier, T> idToEntry;
	
	@Final
	@Shadow
	private Map<RegistryKey<T>, T> keyToEntry;
	
	@Final
	@Shadow
	private Map<T, Lifecycle> entryToLifecycle;
	
	@Shadow
	private Lifecycle lifecycle;
	
	@Shadow
	private int nextId;
	
	@Unique
	private Collection<Identifier> idCache;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(CallbackInfo info) {
		this.idCache = new HashSet<Identifier>();
	}
	
	@SuppressWarnings("deprecation")
	private <V extends T> void remove(RegistryKey<T> key, Lifecycle lifecycle) {
		Validate.notNull(key);
		
		T entry = this.keyToEntry.get(key);
		final int rawId = this.entryToRawId.getInt(entry);
		
		this.nextId--;
		this.lifecycle = this.lifecycle.add(lifecycle);
		this.entryToLifecycle.remove(entry);
		this.keyToEntry.remove(key);
		this.idToEntry.remove(key.getValue());
		this.entryToRawId.remove(entry);
		
		for(T t : this.entryToRawId.keySet()) {
			int i = this.entryToRawId.get(t);
			
			if(i > rawId) {
				this.entryToRawId.replace(t, i - 1);
			}
		}
		
		this.rawIdToEntry.remove(rawId);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void removeCachedIds(Registry<T> registry) {
		for(Iterator<Identifier> iterator = this.idCache.iterator(); iterator.hasNext();) {
			Identifier id = iterator.next();
			
			this.remove(RegistryKey.of(((RegistryAccessor<T>)registry).getRegistryKey(), id), Lifecycle.stable());
			iterator.remove();
		}
	}
	
	@Override
	public void cacheId(Identifier id) {
		this.idCache.add(id);
	}
}
