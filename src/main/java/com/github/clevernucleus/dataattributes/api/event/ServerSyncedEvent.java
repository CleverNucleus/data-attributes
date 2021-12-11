package com.github.clevernucleus.dataattributes.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;


public final class ServerSyncedEvent {
	
	
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
