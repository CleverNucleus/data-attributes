package com.github.clevernucleus.dataattributes.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.github.clevernucleus.dataattributes.mutable.MutableIntFlag;

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
	
	@ModifyArg(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0))
	private Packet<?> data_onPlayerConnect(Packet<?> arg) {
		WorldProperties worldProperties = this.server.getOverworld().getLevelProperties();
		
		if(arg instanceof MutableIntFlag && worldProperties instanceof MutableIntFlag) {
			int updateFlag = ((MutableIntFlag)worldProperties).getUpdateFlag();
			((MutableIntFlag)arg).setUpdateFlag(updateFlag);
		}
		
		return arg;
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
}
