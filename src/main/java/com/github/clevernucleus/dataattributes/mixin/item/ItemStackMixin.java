package com.github.clevernucleus.dataattributes.mixin.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.clevernucleus.dataattributes.api.item.ItemHelper;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;

@Mixin(ItemStack.class)
abstract class ItemStackMixin {
	
	@Inject(method = "<init>(Lnet/minecraft/item/ItemConvertible;I)V", at = @At("TAIL"))
	private void data_init(ItemConvertible item, int count, CallbackInfo info) {
		ItemStack stack = (ItemStack)(Object)this;
		
		if(item != null) {
			((ItemHelper)item.asItem()).onStackCreated(stack, count);
		}
	}

	@Inject(method = "getEquipSound", at = @At("HEAD"), cancellable = true)
	private void data_getEquipSound(CallbackInfoReturnable<SoundEvent> ci) {
		ItemStack stack = (ItemStack)(Object)this;
		ItemHelper item = (ItemHelper)stack.getItem();
		ci.setReturnValue(item.getEquipSound(stack));
	}
}
