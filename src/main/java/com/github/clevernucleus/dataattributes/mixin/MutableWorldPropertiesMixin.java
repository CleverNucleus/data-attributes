package com.github.clevernucleus.dataattributes.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.github.clevernucleus.dataattributes.mutable.MutableIntFlag;

import net.minecraft.world.MutableWorldProperties;

@Mixin(MutableWorldProperties.class)
public interface MutableWorldPropertiesMixin extends MutableIntFlag {}
