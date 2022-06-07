package com.github.clevernucleus.dataattributes.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.github.clevernucleus.dataattributes.mutable.MutableIntFlag;

import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.WorldProperties;

@Mixin(ServerPlayerEntity.class)
abstract class ServerPlayerEntityMixin {
	
	@Final
	@Shadow
	private MinecraftServer server;
	
	@ModifyArg(method = "moveToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 1))
	private Packet<?> data_moveToWorld(Packet<?> arg) {
		WorldProperties worldProperties = this.server.getOverworld().getLevelProperties();
		
		if(arg instanceof MutableIntFlag && worldProperties instanceof MutableIntFlag) {
			int updateFlag = ((MutableIntFlag)worldProperties).getUpdateFlag();
			((MutableIntFlag)arg).setUpdateFlag(updateFlag);
		}
		
		return arg;
	}
	
	@ModifyArg(method = "teleport", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0))
	private Packet<?> data_teleport(Packet<?> arg) {
		WorldProperties worldProperties = this.server.getOverworld().getLevelProperties();
		
		if(arg instanceof MutableIntFlag && worldProperties instanceof MutableIntFlag) {
			int updateFlag = ((MutableIntFlag)worldProperties).getUpdateFlag();
			((MutableIntFlag)arg).setUpdateFlag(updateFlag);
		}
		
		return arg;
	}
}
