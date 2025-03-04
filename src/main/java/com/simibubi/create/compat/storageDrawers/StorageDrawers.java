//package com.simibubi.create.compat.storageDrawers;
//
//import com.simibubi.create.compat.Mods;
//import com.simibubi.create.foundation.tileEntity.behaviour.filtering.FilteringBehaviour;
//
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraftforge.items.IItemHandler;
//
//public class StorageDrawers {
//
//	public static boolean isDrawer(BlockEntity tile) {
//		return tile != null && Mods.STORAGEDRAWERS.asId()
//			.equals(tile.getType()
//				.getRegistryName()
//				.getNamespace());
//	}
//
//	public static float getTrueFillLevel(IItemHandler inv, FilteringBehaviour filtering) {
//		float occupied = 0;
//		float totalSpace = 0;
//
//		for (int slot = 1; slot < inv.getSlots(); slot++) {
//			ItemStack stackInSlot = inv.getStackInSlot(slot);
//			int space = inv.getSlotLimit(slot);
//			int count = stackInSlot.getCount();
//			if (space == 0)
//				continue;
//
//			totalSpace += 1;
//			if (filtering.test(stackInSlot))
//				occupied += count * (1f / space);
//		}
//
//		if (totalSpace == 0)
//			return 0;
//
//		return occupied / totalSpace;
//	}
//
//}
