package com.github.clevernucleus.dataattributes.mutable;

import net.minecraft.entity.attribute.DefaultAttributeContainer;

public interface MutableDefaultAttributeContainer {
	void copy(DefaultAttributeContainer.Builder builder);
}
