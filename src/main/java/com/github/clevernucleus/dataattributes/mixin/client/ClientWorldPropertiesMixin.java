package com.github.clevernucleus.dataattributes.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.github.clevernucleus.dataattributes.mixin.MutableWorldPropertiesMixin;

import net.minecraft.client.world.ClientWorld;

@Mixin(ClientWorld.Properties.class)
abstract class ClientWorldPropertiesMixin implements MutableWorldPropertiesMixin {
	
	@Unique
	private int data_updateFlag;
	
	@Override
	public void setUpdateFlag(int flag) {
		this.data_updateFlag = flag;
	}
	
	@Override
	public int getUpdateFlag() {
		return this.data_updateFlag;
	}
}
