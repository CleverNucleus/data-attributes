package com.github.clevernucleus.dataattributes.mutable;

import com.github.clevernucleus.dataattributes.api.util.VoidConsumer;

import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;

public interface MutableAttributeInstance {
	Identifier getId();
	
	void actionModifier(final VoidConsumer consumerIn, final EntityAttributeInstance instanceIn, final EntityAttributeModifier modifierIn, final boolean isWasAdded);
	
	void setContainerCallback(final AttributeContainer containerIn);
}
