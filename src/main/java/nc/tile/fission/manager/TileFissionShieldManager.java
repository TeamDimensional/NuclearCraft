package nc.tile.fission.manager;

import nc.tile.fission.TileFissionShield;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileFissionShieldManager extends TileFissionManager<TileFissionShieldManager, TileFissionShield> {
	
	public TileFissionShieldManager() {
		super(TileFissionShieldManager.class);
	}
	
	@Override
	public int[] weakSidesToCheck(World worldIn, BlockPos posIn) {
		return new int[] {2, 3, 4, 5};
	}
	
	@Override
	public String getManagerType() {
		return "fissionShieldManager";
	}
	
	@Override
	public Class<TileFissionShield> getListenerClass() {
		return TileFissionShield.class;
	}
	
	@Override
	public boolean isManagerActive() {
		return getIsRedstonePowered();
	}
}
