package nc.tile.dummy;

import nc.config.NCConfig;
import nc.tile.energy.ITileEnergy;
import nc.tile.energyFluid.IBufferable;
import nc.tile.fluid.ITileFluid;
import nc.tile.internal.energy.EnergyConnection;
import nc.tile.internal.fluid.TankSorption;
import nc.tile.internal.inventory.ItemSorption;
import nc.tile.inventory.ITileInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileMachineInterface extends TileDummy<IInterfaceable> implements IBufferable {
	
	public TileMachineInterface() {
		super(IInterfaceable.class, "machine_interface", ITileInventory.inventoryConnectionAll(ItemSorption.BOTH), ITileEnergy.energyConnectionAll(EnergyConnection.BOTH), NCConfig.machine_update_rate, null, ITileFluid.fluidConnectionAll(TankSorption.BOTH));
	}
	
	@Override
	public void update() {
		super.update();
		if(!world.isRemote) {
			pushEnergy();
		}
	}
	
	// Find Master
	
	@Override
	public void findMaster() {
		for (EnumFacing side : EnumFacing.VALUES) {
			TileEntity tile = world.getTileEntity(getPos().offset(side));
			if (tile != null) {
				if (isMaster(getPos().offset(side))) {
					masterPosition = getPos().offset(side);
					return;
				}
			}
		}
		masterPosition = null;
	}
}
