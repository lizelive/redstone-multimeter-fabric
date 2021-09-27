package rsmm.fabric.mixin.meterable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import rsmm.fabric.block.MeterableBlock;
import rsmm.fabric.block.PowerSource;
import rsmm.fabric.interfaces.mixin.IBlock;

@Mixin(RedstoneTorchBlock.class)
public abstract class RedstoneTorchBlockMixin implements IBlock, MeterableBlock, PowerSource {
	
	@Shadow protected abstract boolean shouldUnpower(World world, BlockPos pos, BlockState state);
	
	@Inject(
			method = "shouldUnpower",
			at = @At(
					value = "RETURN"
			)
	)
	private void onShouldUnpowerInjectAtReturn(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		logPowered(world, pos, cir.getReturnValue()); // floor redstone torches only
	}
	
	@Override
	public boolean standardIsPowered() {
		return false;
	}
	
	@Override
	public boolean isPowered(World world, BlockPos pos, BlockState state) {
		return shouldUnpower(world, pos, state);
	}
	
	@Override
	public boolean isActive(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.LIT);
	}
	
	@Override
	public int getPowerLevel(World world, BlockPos pos, BlockState state) {
		return state.get(Properties.LIT) ? MAX_POWER : 0;
	}
}
