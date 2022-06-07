package com.github.clevernucleus.dataattributes.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.clevernucleus.dataattributes.impl.OfflinePlayerCacheImpl;
import com.github.clevernucleus.dataattributes.mutable.MutableIntFlag;
import com.github.clevernucleus.dataattributes.mutable.MutableOfflinePlayerCache;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.SaveVersionInfo;

@Mixin(LevelProperties.class)
abstract class LevelPropertiesMixin implements MutableIntFlag, MutableOfflinePlayerCache {
	
	@Unique
	private int data_updateFlag;
	
	@Unique
	private OfflinePlayerCacheImpl data_cache = new OfflinePlayerCacheImpl();
	
	@Inject(method = "updateProperties", at = @At("HEAD"))
	private void data_updateProperties(DynamicRegistryManager registryManager, NbtCompound levelNbt, @Nullable NbtCompound playerNbt, CallbackInfo info) {
		levelNbt.putInt("AttributeUpdateFlag", this.data_updateFlag);
		levelNbt.put("OfflinePlayerCache", this.data_cache.writeToNbt());
	}
	
	@Inject(method = "readProperties", at = @At("RETURN"))
	private static void data_readProperties(Dynamic<NbtElement> dynamic2, DataFixer dataFixer, int dataVersion, @Nullable NbtCompound playerData, LevelInfo levelInfo, SaveVersionInfo saveVersionInfo, GeneratorOptions generatorOptions, Lifecycle lifecycle, CallbackInfoReturnable<LevelProperties> info) {
		LevelProperties levelProperties = info.getReturnValue();
		((MutableIntFlag)levelProperties).setUpdateFlag(dynamic2.get("AttributeUpdateFlag").asInt(0));
		dynamic2.get("OfflinePlayerCache").result().map(Dynamic::getValue).ifPresent(element -> ((MutableOfflinePlayerCache)levelProperties).offlinePlayerCache().readFromNbt((NbtList)element));
	}
	
	@Override
	public void setUpdateFlag(int flag) {
		this.data_updateFlag = flag;
	}
	
	@Override
	public int getUpdateFlag() {
		return this.data_updateFlag;
	}
	
	@Override
	public OfflinePlayerCacheImpl offlinePlayerCache() {
		return this.data_cache;
	}
}
