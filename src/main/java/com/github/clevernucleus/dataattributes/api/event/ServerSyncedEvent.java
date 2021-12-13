package com.github.clevernucleus.dataattributes.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;

/**
 * Serverside event that fires after all attribute data is initialised. This may occur before the player is initialised, 
 * and before the client has recieved it.
 * 
 * @author CleverNucleus
 *
 */
public final class ServerSyncedEvent {
	
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
		void onCompleted(final MinecraftServer server);
	}
}
