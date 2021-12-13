package com.github.clevernucleus.dataattributes.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Offers a hook into ItemStack constructor, allowing nbt data to be attached to items on creation.
 * 
 * @author CleverNucleus
 *
 */
public final class ItemStackCreatedEvent {
	
	/**
	 * 
	 */
	public static final Event<Created> EVENT = EventFactory.createArrayBacked(Created.class, callbacks -> (item, itemStack, count) -> {
		for(Created callback : callbacks) {
			callback.onCreated(item, itemStack, count);
		}
	});
	
	@FunctionalInterface
	public interface Created {
		void onCreated(final Item item, final ItemStack itemStack, final int count);
	}
}
