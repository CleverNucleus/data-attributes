package com.github.clevernucleus.dataattributes.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.clevernucleus.dataattributes.mutable.MutableIntFlag;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
abstract class ClientPlayNetworkHandlerMixin {
	
	@Shadow
	private ClientWorld world;
	
	@Inject(method = "onGameJoin", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;joinWorld(Lnet/minecraft/client/world/ClientWorld;)V", shift = At.Shift.AFTER))
	private void data_onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
		ClientWorld.Properties properties = this.world.getLevelProperties();
		
		if(properties instanceof MutableIntFlag) {
			int updateFlag = ((MutableIntFlag)(Object)packet).getUpdateFlag();
			((MutableIntFlag)properties).setUpdateFlag(updateFlag);
		}
	}
	
	@Inject(method = "onPlayerRespawn", at = @At("TAIL"))
	private void data_onPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo info) {
		ClientWorld.Properties properties = this.world.getLevelProperties();
		
		if(properties instanceof MutableIntFlag) {
			int updateFlag = ((MutableIntFlag)(Object)packet).getUpdateFlag();
			((MutableIntFlag)properties).setUpdateFlag(updateFlag);
		}
	}
}
