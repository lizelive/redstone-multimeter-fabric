package redstone.multimeter.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class NBTUtils {
	
	public static final byte TYPE_NULL       =  0;
	public static final byte TYPE_BYTE       =  1;
	public static final byte TYPE_SHORT      =  2;
	public static final byte TYPE_INT        =  3;
	public static final byte TYPE_LONG       =  4;
	public static final byte TYPE_FLOAT      =  5;
	public static final byte TYPE_DOUBLE     =  6;
	public static final byte TYPE_BYTE_ARRAY =  7;
	public static final byte TYPE_STRING     =  8;
	public static final byte TYPE_LIST       =  9;
	public static final byte TYPE_COMPOUND   = 10;
	public static final byte TYPE_INT_ARRAY  = 11;
	public static final byte TYPE_LONG_ARRAY = 12;
	
	public static CompoundTag identifierToNBT(Identifier id) {
		CompoundTag nbt = new CompoundTag();
		
		nbt.putString("namespace", id.getNamespace());
		nbt.putString("path", id.getPath());
		
		return nbt;
	}
	
	public static Identifier NBTToIdentifier(CompoundTag nbt) {
		String namespace = nbt.getString("namespace");
		String path = nbt.getString("path");
		
		return new Identifier(namespace, path);
	}
}
