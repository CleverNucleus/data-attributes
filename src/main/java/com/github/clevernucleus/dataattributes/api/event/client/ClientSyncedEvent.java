package com.github.clevernucleus.dataattributes.api.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;

/**
 * Clientside event that fires after all attribute data is synced from the server. This may occur before the player is initialised.
 * 
 * @author CleverNucleus
 *
 */
public final class ClientSyncedEvent {
	
	/**
	 * 
	 */
	public static final Event<Synced> EVENT = EventFactory.createArrayBacked(Synced.class, callbacks -> server -> {
		for(Synced callback : callbacks) {
			callback.onCompleted(server);
		}
	});
	
	@FunctionalInterface
	public interface Synced {
		void onCompleted(final MinecraftClient server);
	}
}
