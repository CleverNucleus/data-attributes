package com.github.clevernucleus.dataattributes.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.github.clevernucleus.dataattributes.mutable.MutableIntFlag;

import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;

@Mixin(UnmodifiableLevelProperties.class)
abstract class UnmodifiableLevelPropertiesMixin implements MutableWorldPropertiesMixin {
	
	@Final
	@Shadow
	private ServerWorldProperties worldProperties;
	
	@Override
	public void setUpdateFlag(int flag) {}
	
	@Override
	public int getUpdateFlag() {
		if(!(this.worldProperties instanceof MutableIntFlag)) return 0;
		return ((MutableIntFlag)this.worldProperties).getUpdateFlag();
	}
}
