package nc.block.property;

import nc.tile.energy.ITileEnergy;
import nc.tile.internal.energy.EnergyConnection;
import net.minecraft.block.Block;
import net.minecraft.block.state.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;

public interface ISidedEnergy {
	
	PropertySidedEnum<EnergyConnection> ENERGY_DOWN = energySide("down", EnumFacing.DOWN);
	PropertySidedEnum<EnergyConnection> ENERGY_UP = energySide("up", EnumFacing.UP);
	PropertySidedEnum<EnergyConnection> ENERGY_NORTH = energySide("north", EnumFacing.NORTH);
	PropertySidedEnum<EnergyConnection> ENERGY_SOUTH = energySide("south", EnumFacing.SOUTH);
	PropertySidedEnum<EnergyConnection> ENERGY_WEST = energySide("west", EnumFacing.WEST);
	PropertySidedEnum<EnergyConnection> ENERGY_EAST = energySide("east", EnumFacing.EAST);
	
	static PropertySidedEnum<EnergyConnection> energySide(String name, EnumFacing facing) {
		return PropertySidedEnum.create(name, EnergyConnection.class, new EnergyConnection[] {EnergyConnection.IN, EnergyConnection.OUT, EnergyConnection.NON}, facing);
	}
	
	default IBlockState getActualEnergyState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.withProperty(ENERGY_DOWN, getEnergyConnection(world, pos, EnumFacing.DOWN)).withProperty(ENERGY_UP, getEnergyConnection(world, pos, EnumFacing.UP)).withProperty(ENERGY_NORTH, getEnergyConnection(world, pos, EnumFacing.NORTH)).withProperty(ENERGY_SOUTH, getEnergyConnection(world, pos, EnumFacing.SOUTH)).withProperty(ENERGY_WEST, getEnergyConnection(world, pos, EnumFacing.WEST)).withProperty(ENERGY_EAST, getEnergyConnection(world, pos, EnumFacing.EAST));
	}
	
	default EnergyConnection getEnergyConnection(IBlockAccess world, BlockPos pos, @Nonnull EnumFacing facing) {
		TileEntity tile = world.getTileEntity(pos);
		return tile instanceof ITileEnergy ? ((ITileEnergy) tile).getEnergyConnection(facing) : EnergyConnection.NON;
	}
	
	default BlockStateContainer createEnergyBlockState(Block block) {
		return new BlockStateContainer(block, ENERGY_DOWN, ENERGY_UP, ENERGY_NORTH, ENERGY_SOUTH, ENERGY_WEST, ENERGY_EAST);
	}
}
