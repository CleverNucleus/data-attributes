package com.github.clevernucleus.dataattributes.mutable;

import java.util.Map;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.util.Identifier;

public interface MutableAttributeContainer {
	Map<Identifier, EntityAttributeInstance> custom();
	
	LivingEntity getLivingEntity();
	
	void setLivingEntity(final LivingEntity livingEntity);
	
	void refresh();

	void clearTracked();
}
