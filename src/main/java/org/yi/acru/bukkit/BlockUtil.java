package org.yi.acru.bukkit;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class BlockUtil {
	// Facings are reversed as we are attaching signs to blocks.
	public static byte faceList[] = { 5, 3, 4, 2 };     // SOUTH, WEST, NORTH, EAST
	public static byte attachList[] = { 1, 2, 0, 3 };     // SOUTH, WEST, NORTH, EAST
	//public static byte attachList[] = { 3, 1, 2, 0 };     // SOUTH, WEST, NORTH, EAST		
	static {
		if (BlockFace.NORTH.getModX() != -1) {
			// Post CraftBukkit 2502
			faceList[0] = 3; // SOUTH
			faceList[1] = 4; // WEST
			faceList[2] = 2; // NORTH
			faceList[3] = 5; // EAST

			attachList[0] = 1;
			attachList[0] = 2;
			attachList[0] = 0;
			attachList[0] = 3;
		}
	}

	public static final int[] materialList = {
		Material.CHEST.getId(),
		Material.TRAPPED_CHEST.getId(),
		Material.DISPENSER.getId(),
		Material.DROPPER.getId(),
		Material.FURNACE.getId(),
		Material.BURNING_FURNACE.getId(),
		Material.BREWING_STAND.getId(),
		Material.TRAP_DOOR.getId(),
		Material.WOODEN_DOOR.getId(),
		Material.IRON_DOOR_BLOCK.getId(),
		Material.FENCE_GATE.getId(),
		Material.ACACIA_DOOR.getId(),
		Material.ACACIA_FENCE_GATE.getId(),
		Material.BIRCH_DOOR.getId(),
		Material.BIRCH_FENCE_GATE.getId(),	  
		Material.DARK_OAK_DOOR.getId(),
		Material.DARK_OAK_FENCE_GATE.getId(),	  
		Material.JUNGLE_DOOR.getId(),
		Material.JUNGLE_FENCE_GATE.getId(),	  
		Material.SPRUCE_DOOR.getId(),
		Material.SPRUCE_FENCE_GATE.getId(),	  
		Material.WOOD_DOOR.getId(),
		Material.IRON_TRAPDOOR.getId()
	};

	public static final int[] materialListTrapDoors = {
		Material.TRAP_DOOR.getId(),
		Material.IRON_TRAPDOOR.getId()
	};
	
	public static final int[] materialListNonDoors = {
		Material.CHEST.getId(),
		Material.TRAPPED_CHEST.getId(),
		Material.DISPENSER.getId(),
		Material.DROPPER.getId(),
		Material.FURNACE.getId(),
		Material.BURNING_FURNACE.getId(),
		Material.BREWING_STAND.getId()
	};

	public static final int[] materialListTools = {
		Material.DISPENSER.getId(),
		Material.DROPPER.getId(),
		Material.FURNACE.getId(),
		Material.BURNING_FURNACE.getId(),
		Material.BREWING_STAND.getId()
	};
	
	public static final int[] materialListChests = {
		Material.CHEST.getId(),
		Material.TRAPPED_CHEST.getId(),
	};

	public static final int[] materialListFurnaces = {
		Material.FURNACE.getId(),
		Material.BURNING_FURNACE.getId()
	};

	public static final int[] materialListDoors = {
		Material.WOODEN_DOOR.getId(),
		Material.IRON_DOOR_BLOCK.getId(),
		Material.FENCE_GATE.getId(),
		Material.ACACIA_DOOR.getId(),
		Material.ACACIA_FENCE_GATE.getId(),
		Material.BIRCH_DOOR.getId(),
		Material.BIRCH_FENCE_GATE.getId(),	  
		Material.DARK_OAK_DOOR.getId(),
		Material.DARK_OAK_FENCE_GATE.getId(),	  
		Material.JUNGLE_DOOR.getId(),
		Material.JUNGLE_FENCE_GATE.getId(),	  
		Material.SPRUCE_DOOR.getId(),
		Material.SPRUCE_FENCE_GATE.getId(),	  
		Material.WOOD_DOOR.getId()
	};

	public static final int[] materialListJustDoors = {
		Material.WOODEN_DOOR.getId(),
		Material.IRON_DOOR_BLOCK.getId(),
		Material.ACACIA_DOOR.getId(),
		Material.BIRCH_DOOR.getId(),
		Material.DARK_OAK_DOOR.getId(),
		Material.JUNGLE_DOOR.getId(),
		Material.SPRUCE_DOOR.getId(),
		Material.WOOD_DOOR.getId()
	};
	
	public static final int[] materialListWoodenDoors = {
		Material.TRAP_DOOR.getId(),
		Material.WOODEN_DOOR.getId(),
		Material.FENCE_GATE.getId(),
		Material.ACACIA_DOOR.getId(),
		Material.ACACIA_FENCE_GATE.getId(),
		Material.BIRCH_DOOR.getId(),
		Material.BIRCH_FENCE_GATE.getId(),	  
		Material.DARK_OAK_DOOR.getId(),
		Material.DARK_OAK_FENCE_GATE.getId(),	  
		Material.JUNGLE_DOOR.getId(),
		Material.JUNGLE_FENCE_GATE.getId(),	  
		Material.SPRUCE_DOOR.getId(),
		Material.SPRUCE_FENCE_GATE.getId(),	  
		Material.WOOD_DOOR.getId()
	};

	public static final int[] materialListGates = {
		Material.FENCE_GATE.getId(),
		Material.ACACIA_FENCE_GATE.getId(),
		Material.BIRCH_FENCE_GATE.getId(),	  
		Material.DARK_OAK_FENCE_GATE.getId(),	  
		Material.JUNGLE_FENCE_GATE.getId(),	  
		Material.SPRUCE_FENCE_GATE.getId(),	  
	};
	
	public static final int[] materialListBad = {
		50, 63, 64, 65, 68, 71, 75, 76, 96  //, 12, 13, 18, 46 // sand, gravel, leaves, tnt
	};

	public static boolean isInList(int target, int[] list) {
		if (list == null)
			return false;
		for (int x = 0; x < list.length; x++)
			if (target == list[x])
				return true;
		return false;
	}
}
