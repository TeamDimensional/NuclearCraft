package nc.block.fission.port;

import nc.multiblock.fission.FissionReactor;
import nc.tile.ITileGui;
import nc.tile.fission.port.*;
import nc.tile.fluid.ITileFilteredFluid;
import nc.util.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public abstract class BlockFissionFluidPort<PORT extends TileFissionFluidPort<PORT, TARGET>, TARGET extends IFissionPortTarget<PORT, TARGET> & ITileFilteredFluid> extends BlockFissionPort<PORT, TARGET> {
	
	public BlockFissionFluidPort(Class<PORT> portClass) {
		super(portClass);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (hand != EnumHand.MAIN_HAND || player.isSneaking()) {
			return false;
		}
		
		if (!world.isRemote) {
			TileEntity tile = world.getTileEntity(pos);
			if (portClass.isInstance(tile)) {
				PORT port = portClass.cast(tile);
				FissionReactor reactor = port.getMultiblock();
				if (reactor != null) {
					FluidStack fluidStack = FluidStackHelper.getFluid(player.getHeldItem(hand));
					if (port.canModifyFilter(0) && port.getTanks().get(0).isEmpty() && fluidStack != null && !FluidStackHelper.stacksEqual(port.getFilterTanks().get(0).getFluid(), fluidStack) && port.isFluidValidForTank(0, fluidStack)) {
						player.sendMessage(new TextComponentString(Lang.localize("message.nuclearcraft.filter") + " " + TextFormatting.BOLD + Lang.localize(fluidStack.getUnlocalizedName())));
						FluidStack filter = fluidStack.copy();
						filter.amount = 1000;
						port.getFilterTanks().get(0).setFluid(filter);
						port.onFilterChanged(0);
					}
					else {
						if (tile instanceof ITileGui<?, ?, ?> tileGui) {
							tileGui.openGui(world, pos, player);
						}
					}
					return true;
				}
			}
		}
		return rightClickOnPart(world, pos, player, hand, facing, true);
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		// super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}
}
