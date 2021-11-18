package redstone.multimeter.mixin.server;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;

import redstone.multimeter.common.TickTask;
import redstone.multimeter.interfaces.mixin.IMinecraftServer;
import redstone.multimeter.server.MultimeterServer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements IMinecraftServer {
	
	private MultimeterServer multimeterServer;
	
	@Inject(
			method = "<init>",
			at = @At(
					value = "RETURN"
			)
	)
	private void onInit(CallbackInfo ci) {
		this.multimeterServer = new MultimeterServer((MinecraftServer)(Object)this);
	}
	
	@Inject(
			method = "method_16208",
			at = @At(
					value = "HEAD"
			)
	)
	private void startTickTaskPackets(CallbackInfo ci) {
		multimeterServer.startTickTask(TickTask.PACKETS);
	}
	
	@Inject(
			method = "method_16208",
			at = @At(
					value = "RETURN"
			)
	)
	private void endTickTaskPacketsAndEndTick(CallbackInfo ci) {
		multimeterServer.endTickTask();
		// Ending the tick here is not ideal, but for the
		// sake of Carpet mod compatibility injecting into
		// the run loop is not an option.
		multimeterServer.tickEnd();
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "HEAD"
			)
	)
	private void onTickStart(BooleanSupplier isAheadOfTime, CallbackInfo ci) {
		multimeterServer.tickStart();
	}
	
	@Inject(
			method = "reload",
			at = @At(
					value = "HEAD"
			)
	)
	private void onReloadResources(CallbackInfo ci) {
		multimeterServer.getMultimeter().reloadOptions();
	}
	
	@Override
	public MultimeterServer getMultimeterServer() {
		return multimeterServer;
	}
	
	@Override
	public boolean isPaused() {
		return false;
	}
}
