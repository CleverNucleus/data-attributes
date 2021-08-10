package com.github.clevernucleus.dataattributes.impl.attribute;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;

public interface IMutableContainer {
	default void build(DefaultAttributeContainer.Builder builder) {}
	
	default void setIsClient(final boolean isClient) {}
	
	default boolean isClient() {
		return false;
	}
	
	default void setLivingEntity(LivingEntity livingEntity) {}
	
	@Nullable
	default LivingEntity livingEntity() {
		return null;
	}
}
