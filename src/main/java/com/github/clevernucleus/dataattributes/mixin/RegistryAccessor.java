package com.github.clevernucleus.dataattributes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

@Mixin(Registry.class)
public interface RegistryAccessor<T> {
	@Accessor() RegistryKey<Registry<T>> getRegistryKey();
}
