package redstone.multimeter.mixin.common;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import redstone.multimeter.interfaces.mixin.IServerWorld;

@Mixin(AbstractBlockState.class)
public class AbstractBlockStateMixin {
	
	@Inject(
			method = "randomTick",
			at = @At(
					value = "HEAD"
			)
	)
	private void onRandomTick(ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		((IServerWorld)world).getMultimeter().logRandomTick(world, pos);
	}
	
	@Inject(
			method = "onUse",
			at = @At(
					value = "HEAD"
			)
	)
	private void onInteractBlock(World world, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if (!world.isClient()) {
			((IServerWorld)world).getMultimeter().logInteractBlock(world, hit.getBlockPos());
		}
	}
	
	@Inject(
			method = "getStateForNeighborUpdate",
			at = @At(
					value = "HEAD"
			)
	)
	private void onShapeUpdate(Direction direction, BlockState neighborState, WorldAccess worldAccess, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> ci) {
		if (worldAccess instanceof ServerWorld) {
			ServerWorld world = (ServerWorld)worldAccess;
			((IServerWorld)world).getMultimeter().logShapeUpdate(world, pos, direction);
		}
	}
}
