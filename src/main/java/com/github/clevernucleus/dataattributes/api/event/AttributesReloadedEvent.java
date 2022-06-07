package com.github.clevernucleus.dataattributes.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Event that allows for logic reliant on datapack attributes to be ordered after they are loaded on both the server and client.
 * 
 * @author CleverNucleus
 *
 */
public final class AttributesReloadedEvent {
	
	/**
	 * Fired on Server upon joining world and on datapack reload through '/reload'. Fired on Client when selecting datapacks 
	 * and after server has synced with client.
	 */
	public static final Event<Reloaded> EVENT = EventFactory.createArrayBacked(Reloaded.class, callbacks -> () -> {
		for(Reloaded callback : callbacks) {
			callback.onCompletedReload();
		}
	});
	
	@FunctionalInterface
	public interface Reloaded {
		void onCompletedReload();
	}
}
