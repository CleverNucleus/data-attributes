package com.github.clevernucleus.dataattributes.mutable;

import net.minecraft.entity.LivingEntity;

public interface MutableAttributeContainer {
	void setLivingEntity(final LivingEntity livingEntity);
	
	LivingEntity getLivingEntity();
}
