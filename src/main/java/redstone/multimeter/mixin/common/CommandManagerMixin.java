package redstone.multimeter.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import redstone.multimeter.command.MeterGroupCommand;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
	
	@Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;
	
	@Inject(
			method="<init>",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lcom/mojang/brigadier/CommandDispatcher;findAmbiguities(Lcom/mojang/brigadier/AmbiguityConsumer;)V"
			)
	)
	private void registerCommands(CommandManager.RegistrationEnvironment environment, CallbackInfo ci) {
		MeterGroupCommand.register(dispatcher);
	}
}
