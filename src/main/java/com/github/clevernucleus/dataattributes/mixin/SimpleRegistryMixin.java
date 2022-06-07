package com.github.clevernucleus.dataattributes.mixin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

@Mixin(SimpleRegistry.class)
abstract class SimpleRegistryMixin<T> implements MutableSimpleRegistry<T> {
	
	@Unique
	private Collection<Identifier> data_idCache;
	
	@Final
	@Shadow
	private ObjectList<RegistryEntry.Reference<T>> rawIdToEntry;
	
	@Final
	@Shadow
	private Object2IntMap<T> entryToRawId;
	
	@Final
	@Shadow
	private Map<Identifier, RegistryEntry.Reference<T>> idToEntry;
	
	@Final
	@Shadow
	private Map<RegistryKey<T>, RegistryEntry.Reference<T>> keyToEntry;
	
	@Final
	@Shadow
	private Map<T, RegistryEntry.Reference<T>> valueToEntry;
	
	@Final
	@Shadow
	private Function<T, RegistryEntry.Reference<T>> valueToEntryFunction;
	
	@Final
	@Shadow
	private Map<T, Lifecycle> entryToLifecycle;
	
	@Shadow
	private Lifecycle lifecycle;
	
	@Shadow
	private List<RegistryEntry.Reference<T>> cachedEntries;
	
	@Shadow
	private int nextId;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void data_init(CallbackInfo info) {
		this.data_idCache = new HashSet<Identifier>();
	}
	
	@SuppressWarnings("unchecked")
	@Inject(method = "assertNotFrozen", at = @At("HEAD"), cancellable = true)
	private void data_assertNotFrozen(CallbackInfo info) {
		if((SimpleRegistry<T>)(Object)this == Registry.ATTRIBUTE) {
			info.cancel();
		}
	}
	
	@SuppressWarnings("deprecation")
	private <V extends T> void remove(RegistryKey<T> key, Lifecycle lifecycle) {
		Validate.notNull(key);
		RegistryEntry.Reference<T> reference = this.keyToEntry.get(key);
		T value = reference.value();
		final int rawId = this.entryToRawId.getInt(value);
		
		this.rawIdToEntry.remove(rawId);
		this.valueToEntry.remove(value);
		this.idToEntry.remove(key.getValue());
		this.keyToEntry.remove(key);
		this.nextId--;
		this.lifecycle = this.lifecycle.add(lifecycle);
		this.entryToLifecycle.remove(value);
		this.cachedEntries = null;
		this.entryToRawId.remove(value);
		
		for(T t : this.entryToRawId.keySet()) {
			int i = this.entryToRawId.get(t);
			
			if(i > rawId) {
				this.entryToRawId.replace(t, i - 1);
			}
		}
	}
	
	@Override
	public void removeCachedIds(Registry<T> registry) {
		for(Iterator<Identifier> iterator = this.data_idCache.iterator(); iterator.hasNext();) {
			Identifier id = iterator.next();
			
			this.remove(RegistryKey.of(registry.getKey(), id), Lifecycle.stable());
			iterator.remove();
		}
	}
	
	@Override
	public void cacheId(Identifier id) {
		this.data_idCache.add(id);
	}
}
