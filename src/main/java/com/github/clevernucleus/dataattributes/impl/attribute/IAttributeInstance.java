package com.github.clevernucleus.dataattributes.impl.attribute;

import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;

public interface IAttributeInstance {
	void setContainer(AttributeContainer container);
	
	void addTemporaryModifierToInstance(EntityAttributeModifier modifierIn);
	
	void addPersistentModifierToInstance(EntityAttributeModifier modifierIn);
}
