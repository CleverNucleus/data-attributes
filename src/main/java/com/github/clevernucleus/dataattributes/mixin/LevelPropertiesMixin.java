package com.github.clevernucleus.dataattributes.mixin;

import net.minecraft.registry.DynamicRegistryManager;
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
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.SaveVersionInfo;

@Mixin(LevelProperties.class)
abstract class LevelPropertiesMixin implements MutableWorldPropertiesMixin {

	@Unique
	private int data_updateFlag;

	@Inject(method = "updateProperties", at = @At("HEAD"))
	private void data_updateProperties(DynamicRegistryManager registryManager, NbtCompound levelNbt, @Nullable NbtCompound playerNbt, CallbackInfo ci) {
		levelNbt.putInt("AttributeUpdateFlag", this.data_updateFlag);
	}

	@Inject(method = "readProperties", at = @At("RETURN"))
	private static void data_readProperties(Dynamic<NbtElement> dynamic, DataFixer dataFixer, int dataVersion, NbtCompound playerData, LevelInfo levelInfo, SaveVersionInfo saveVersionInfo, LevelProperties.SpecialProperty specialProperty, GeneratorOptions generatorOptions, Lifecycle lifecycle, CallbackInfoReturnable<LevelProperties> cir) {
		LevelProperties levelProperties = cir.getReturnValue();
		((MutableIntFlag)levelProperties).setUpdateFlag(dynamic.get("AttributeUpdateFlag").asInt(0));
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
