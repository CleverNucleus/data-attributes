package com.github.clevernucleus.dataattributes.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.clevernucleus.dataattributes.impl.OfflinePlayerCacheImpl;
import com.github.clevernucleus.dataattributes.mutable.MutableIntFlag;
import com.github.clevernucleus.dataattributes.mutable.MutableOfflinePlayerCache;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.WorldProperties;

@Mixin(PlayerManager.class)
abstract class PlayerManagerMixin {
	
	@Final
	@Shadow
	private MinecraftServer server;
	
	@Final
	@Shadow
	List<ServerPlayerEntity> players;
	
	@Inject(method = "onPlayerConnect", at = @At("TAIL"))
	private void data_onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
		WorldProperties worldProperties = this.server.getOverworld().getLevelProperties();
		
		if(worldProperties instanceof MutableOfflinePlayerCache) {
			((MutableOfflinePlayerCache)worldProperties).offlinePlayerCache().uncache(player);
		}
	}
	
	@ModifyArg(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0))
	private Packet<?> data_onPlayerConnect(Packet<?> arg) {
		WorldProperties worldProperties = this.server.getOverworld().getLevelProperties();
		
		if(arg instanceof MutableIntFlag && worldProperties instanceof MutableIntFlag) {
			int updateFlag = ((MutableIntFlag)worldProperties).getUpdateFlag();
			((MutableIntFlag)arg).setUpdateFlag(updateFlag);
		}
		
		return arg;
	}
	
	@Inject(method = "remove", at = @At("HEAD"))
	private void data_remove(ServerPlayerEntity player, CallbackInfo info) {
		WorldProperties worldProperties = this.server.getOverworld().getLevelProperties();
		
		if(worldProperties instanceof MutableOfflinePlayerCache) {
			((MutableOfflinePlayerCache)worldProperties).offlinePlayerCache().cache(player);
		}
	}
	
	@ModifyArg(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 1))
	private Packet<?> data_respawnPlayer(Packet<?> arg) {
		WorldProperties worldProperties = this.server.getOverworld().getLevelProperties();
		
		if(arg instanceof MutableIntFlag && worldProperties instanceof MutableIntFlag) {
			int updateFlag = ((MutableIntFlag)worldProperties).getUpdateFlag();
			((MutableIntFlag)arg).setUpdateFlag(updateFlag);
		}
		
		return arg;
	}
	
	/*
	 * We could also do the following:
	 * 
	 * 
	 * @Inject(method = "disconnectAllPlayers", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILHARD)
	 * private void data_disconnectAllPlayers(CallbackInfo info, int i) {
	 *     ServerPlayerEntity player = this.players.get(i);
	 * }
	 * 
	 * This may seem faster at first since we're not iterating over PlayerManager#players twice, but it is actually slower due to getting the WorldProperties 
	 * and checking for correct instanceof, and casting for each player. Instead, we move this out of the loop, which ends up being faster. Why do we check 
	 * for instanceof MutableOfflinePlayerCache in the first place when we used Mixins? Because otherwise it occasionally gets unhappy for unknown reason. Also, if 
	 * for some reason this gets called on a client or an integrated server (maybe from some other poorly coded mod) it will still be safe.
	 * 
	 */
	@Inject(method = "disconnectAllPlayers", at = @At("HEAD"))
	private void data_disconnectAllPlayers(CallbackInfo info) {
		WorldProperties worldProperties = this.server.getOverworld().getLevelProperties();
		
		if(worldProperties instanceof MutableOfflinePlayerCache) {
			OfflinePlayerCacheImpl offlinePlayerCache = ((MutableOfflinePlayerCache)worldProperties).offlinePlayerCache();
			
			for(int i = 0; i < this.players.size(); i++) {
				offlinePlayerCache.cache(this.players.get(i));
			}
		}
	}
}
