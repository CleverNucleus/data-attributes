package com.github.clevernucleus.dataattributes.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.clevernucleus.dataattributes.mutable.MutableIntFlag;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.SaveVersionInfo;

@Mixin(LevelProperties.class)
abstract class LevelPropertiesMixin implements MutableIntFlag {
	
	@Unique
	private int data_updateFlag;
	
	@Inject(method = "updateProperties", at = @At("HEAD"))
	private void data_updateProperties(DynamicRegistryManager registryManager, NbtCompound levelNbt, @Nullable NbtCompound playerNbt, CallbackInfo ci) {
		levelNbt.putInt("AttributeUpdateFlag", this.data_updateFlag);
	}
	
	@Inject(method = "readProperties", at = @At("RETURN"))
	private static void data_readProperties(Dynamic<NbtElement> dynamic2, DataFixer dataFixer, int dataVersion, @Nullable NbtCompound playerData, LevelInfo levelInfo, SaveVersionInfo saveVersionInfo, GeneratorOptions generatorOptions, Lifecycle lifecycle, CallbackInfoReturnable<LevelProperties> ci) {
		LevelProperties levelProperties = ci.getReturnValue();
		((MutableIntFlag)levelProperties).setUpdateFlag(dynamic2.get("AttributeUpdateFlag").asInt(0));
	}
	
	@Override
	public void setUpdateFlag(int flag) {
		this.data_updateFlag = flag;
	}
	
	@Override
	public int getUpdateFlag() {
		return this.data_updateFlag;
	}
}
