package rsmm.fabric.mixin.server;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.Block;

import rsmm.fabric.interfaces.mixin.IBlock;

@Mixin(Block.class)
public abstract class BlockMixin implements IBlock {
	
}
