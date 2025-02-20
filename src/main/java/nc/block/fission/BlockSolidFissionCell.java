package nc.block.fission;

import nc.multiblock.fission.FissionReactor;
import nc.tile.fission.TileSolidFissionCell;
import nc.util.Lang;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

public class BlockSolidFissionCell extends BlockFissionPart {
	
	public BlockSolidFissionCell() {
		super();
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileSolidFissionCell();
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (hand != EnumHand.MAIN_HAND || player.isSneaking()) {
			return false;
		}
		
		if (!world.isRemote) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof TileSolidFissionCell cell) {
				FissionReactor reactor = cell.getMultiblock();
				if (reactor != null) {
					ItemStack heldStack = player.getHeldItem(hand);
					if (cell.canModifyFilter(0) && cell.getInventoryStacks().get(0).isEmpty() && !heldStack.isItemEqual(cell.getFilterStacks().get(0)) && cell.isItemValidForSlotInternal(0, heldStack)) {
						player.sendMessage(new TextComponentString(Lang.localize("message.nuclearcraft.filter") + " " + TextFormatting.BOLD + heldStack.getDisplayName()));
						ItemStack filter = heldStack.copy();
						filter.setCount(1);
						cell.getFilterStacks().set(0, filter);
						cell.onFilterChanged(0);
					}
					else {
						cell.openGui(world, pos, player);
					}
					return true;
				}
			}
		}
		return rightClickOnPart(world, pos, player, hand, facing, true);
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (!keepInventory) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof TileSolidFissionCell cell) {
				dropItems(world, pos, cell.getInventoryStacksInternal());
				/* world.updateComparatorOutputLevel(pos, this);
				FissionReactor reactor = cell.getMultiblock();
				world.removeTileEntity(pos);
				if (reactor != null) {
					reactor.getLogic().refreshPorts();
				}*/
			}
		}
		// super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}
	
}
