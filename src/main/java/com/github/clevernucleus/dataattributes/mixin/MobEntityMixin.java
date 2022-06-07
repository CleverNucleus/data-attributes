package com.github.clevernucleus.dataattributes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.github.clevernucleus.dataattributes.api.item.ItemHelper;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;

@Mixin(MobEntity.class)
abstract class MobEntityMixin {
	
	@Redirect(method = "prefersNewEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/SwordItem;getAttackDamage()F", ordinal = 0))
	private float data_getAttackDamage_0(SwordItem swordItem, ItemStack newStack, ItemStack oldStack) {
		return ((ItemHelper)swordItem).getAttackDamage(newStack);
	}
	
	@Redirect(method = "prefersNewEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/SwordItem;getAttackDamage()F", ordinal = 1))
	private float data_getAttackDamage_1(SwordItem swordItem2, ItemStack newStack, ItemStack oldStack) {
		return ((ItemHelper)swordItem2).getAttackDamage(oldStack);
	}
	
	@Redirect(method = "prefersNewEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/SwordItem;getAttackDamage()F", ordinal = 2))
	private float data_getAttackDamage_2(SwordItem swordItem, ItemStack newStack, ItemStack oldStack) {
		return ((ItemHelper)swordItem).getAttackDamage(newStack);
	}
	
	@Redirect(method = "prefersNewEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/SwordItem;getAttackDamage()F", ordinal = 3))
	private float data_getAttackDamage_3(SwordItem swordItem2, ItemStack newStack, ItemStack oldStack) {
		return ((ItemHelper)swordItem2).getAttackDamage(oldStack);
	}
	
	@Redirect(method = "prefersNewEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ArmorItem;getProtection()I", ordinal = 0))
	private int data_getProtection_0(ArmorItem armorItem, ItemStack newStack, ItemStack oldStack) {
		return ((ItemHelper)armorItem).getProtection(newStack);
	}
	
	@Redirect(method = "prefersNewEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ArmorItem;getProtection()I", ordinal = 1))
	private int data_getProtection_1(ArmorItem armorItem2, ItemStack newStack, ItemStack oldStack) {
		return ((ItemHelper)armorItem2).getProtection(oldStack);
	}
	
	@Redirect(method = "prefersNewEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ArmorItem;getProtection()I", ordinal = 2))
	private int data_getProtection_2(ArmorItem armorItem, ItemStack newStack, ItemStack oldStack) {
		return ((ItemHelper)armorItem).getProtection(newStack);
	}
	
	@Redirect(method = "prefersNewEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ArmorItem;getProtection()I", ordinal = 3))
	private int data_getProtection_3(ArmorItem armorItem2, ItemStack newStack, ItemStack oldStack) {
		return ((ItemHelper)armorItem2).getProtection(oldStack);
	}
	
	@Redirect(method = "prefersNewEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ArmorItem;getToughness()F", ordinal = 0))
	private float data_getToughness_0(ArmorItem armorItem, ItemStack newStack, ItemStack oldStack) {
		return ((ItemHelper)armorItem).getToughness(newStack);
	}
	
	@Redirect(method = "prefersNewEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ArmorItem;getToughness()F", ordinal = 1))
	private float data_getToughness_1(ArmorItem armorItem2, ItemStack newStack, ItemStack oldStack) {
		return ((ItemHelper)armorItem2).getToughness(oldStack);
	}
	
	@Redirect(method = "prefersNewEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ArmorItem;getToughness()F", ordinal = 2))
	private float data_getToughness_2(ArmorItem armorItem, ItemStack newStack, ItemStack oldStack) {
		return ((ItemHelper)armorItem).getToughness(newStack);
	}
	
	@Redirect(method = "prefersNewEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ArmorItem;getToughness()F", ordinal = 3))
	private float data_getToughness_3(ArmorItem armorItem2, ItemStack newStack, ItemStack oldStack) {
		return ((ItemHelper)armorItem2).getToughness(oldStack);
	}
	
	@Redirect(method = "prefersNewEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/MiningToolItem;getAttackDamage()F", ordinal = 0))
	private float data_getAttackDamage_0(MiningToolItem miningToolItem, ItemStack newStack, ItemStack oldStack) {
		return ((ItemHelper)miningToolItem).getAttackDamage(newStack);
	}
	
	@Redirect(method = "prefersNewEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/MiningToolItem;getAttackDamage()F", ordinal = 1))
	private float data_getAttackDamage_1(MiningToolItem miningToolItem2, ItemStack newStack, ItemStack oldStack) {
		return ((ItemHelper)miningToolItem2).getAttackDamage(oldStack);
	}
	
	@Redirect(method = "prefersNewEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/MiningToolItem;getAttackDamage()F", ordinal = 2))
	private float data_getAttackDamage_2(MiningToolItem miningToolItem, ItemStack newStack, ItemStack oldStack) {
		return ((ItemHelper)miningToolItem).getAttackDamage(newStack);
	}
	
	@Redirect(method = "prefersNewEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/MiningToolItem;getAttackDamage()F", ordinal = 3))
	private float data_getAttackDamage_3(MiningToolItem miningToolItem2, ItemStack newStack, ItemStack oldStack) {
		return ((ItemHelper)miningToolItem2).getAttackDamage(oldStack);
	}
}
