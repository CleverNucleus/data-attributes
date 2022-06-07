package com.github.clevernucleus.dataattributes.mixin.item;

import org.spongepowered.asm.mixin.Mixin;

import com.github.clevernucleus.dataattributes.api.item.ItemHelper;

import net.minecraft.item.Item;

@Mixin(Item.class)
abstract class ItemMixin implements ItemHelper {}
