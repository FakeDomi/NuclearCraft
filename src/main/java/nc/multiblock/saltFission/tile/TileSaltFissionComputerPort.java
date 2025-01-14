package nc.multiblock.saltFission.tile;

import java.util.ArrayList;
import java.util.List;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import nc.Global;
import nc.multiblock.cuboidal.CuboidalPartPositionType;
import nc.multiblock.saltFission.SaltFissionReactor;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "opencomputers")
public class TileSaltFissionComputerPort extends TileSaltFissionPartBase implements SimpleComponent {
	
	public TileSaltFissionComputerPort() {
		super(CuboidalPartPositionType.WALL);
	}
	
	@Override
	public void onMachineAssembled(SaltFissionReactor controller) {
		doStandardNullControllerResponse(controller);
		super.onMachineAssembled(controller);
		if (getWorld().isRemote) return;
	}
	
	@Override
	public void onMachineBroken() {
		super.onMachineBroken();
		if (getWorld().isRemote) return;
		//getWorld().setBlockState(getPos(), getWorld().getBlockState(getPos()), 2);
	}
	
	// OpenComputers
	
	@Override
	@Optional.Method(modid = "opencomputers")
	public String getComponentName() {
		return Global.MOD_SHORT_ID + "_salt_fission_reactor";
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] isComplete(Context context, Arguments args) {
		return new Object[] {isMultiblockAssembled()};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] isReactorOn(Context context, Arguments args) {
		return new Object[] {isMultiblockAssembled() ? getMultiblock().isReactorOn : false};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] getLengthX(Context context, Arguments args) {
		return new Object[] {isMultiblockAssembled() ? getMultiblock().getInteriorLengthX() : 0};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] getLengthY(Context context, Arguments args) {
		return new Object[] {isMultiblockAssembled() ? getMultiblock().getInteriorLengthY() : 0};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] getLengthZ(Context context, Arguments args) {
		return new Object[] {isMultiblockAssembled() ? getMultiblock().getInteriorLengthZ() : 0};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] getHeatStored(Context context, Arguments args) {
		return new Object[] {isMultiblockAssembled() ? getMultiblock().heatBuffer.getHeatStored() : 0L};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] getHeatCapacity(Context context, Arguments args) {
		return new Object[] {isMultiblockAssembled() ? getMultiblock().heatBuffer.getHeatCapacity() : 0L};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] getCoolingRate(Context context, Arguments args) {
		return new Object[] {isMultiblockAssembled() ? getMultiblock().cooling : 0D};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] getRawHeatingRate(Context context, Arguments args) {
		return new Object[] {isMultiblockAssembled() ? getMultiblock().heating : 0D};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] getNetHeatingRate(Context context, Arguments args) {
		return new Object[] {isMultiblockAssembled() ? getMultiblock().getNetHeatingRate(false) : 0D};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] getRawEfficiency(Context context, Arguments args) {
		return new Object[] {isMultiblockAssembled() ? getMultiblock().rawEfficiency : 0D};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] getHeatMultiplier(Context context, Arguments args) {
		return new Object[] {isMultiblockAssembled() ? getMultiblock().heatMult : 0D};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] getCoolingEfficiency(Context context, Arguments args) {
		return new Object[] {isMultiblockAssembled() ? getMultiblock().coolingEfficiency : 0D};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] getNumberOfVessels(Context context, Arguments args) {
		return new Object[] {isMultiblockAssembled() ? getMultiblock().getVessels().size() : 0};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] getNumberOfHeaters(Context context, Arguments args) {
		return new Object[] {isMultiblockAssembled() ? getMultiblock().getHeaters().size() : 0};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] getNumberOfModerators(Context context, Arguments args) {
		return new Object[] {isMultiblockAssembled() ? getMultiblock().getModerators().size() : 0};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] getVesselStats(Context context, Arguments args) {
		if (isMultiblockAssembled()) {
			List<Object[]> stats = new ArrayList<Object[]>();
			for (TileSaltFissionVessel vessel : getMultiblock().getVessels()) {
				stats.add(new Object[] {
						new Object[] {vessel.getPos().getX(), vessel.getPos().getY(), vessel.getPos().getZ()},
						vessel.isProcessing,
						vessel.time,
						vessel.recipeInfo != null ? vessel.baseProcessTime/vessel.getSpeedMultiplier() : 0D,
						vessel.getProcessHeat(),
						vessel.getEfficiency(),
						vessel.getHeatMultiplier()
						});
			}
			return new Object[] {stats.toArray()};
		}
		else return new Object[] {};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] getHeaterStats(Context context, Arguments args) {
		if (isMultiblockAssembled()) {
			List<Object[]> stats = new ArrayList<Object[]>();
			for (TileSaltFissionHeater heater : getMultiblock().getHeaters()) {
				stats.add(new Object[] {
						new Object[] {heater.getPos().getX(), heater.getPos().getY(), heater.getPos().getZ()},
						heater.getCoolantName(),
						heater.isProcessing,
						heater.time,
						heater.recipeInfo != null ? heater.baseProcessTime/heater.getSpeedMultiplier() : 0D,
						heater.baseProcessCooling
						});
			}
			return new Object[] {stats.toArray()};
		}
		else return new Object[] {};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] getModeratorStats(Context context, Arguments args) {
		if (isMultiblockAssembled()) {
			List<Object[]> stats = new ArrayList<Object[]>();
			for (TileSaltFissionModerator moderator : getMultiblock().getModerators()) {
				stats.add(new Object[] {
						new Object[] {moderator.getPos().getX(), moderator.getPos().getY(), moderator.getPos().getZ()},
						moderator.isInValidPosition,
						moderator.isInModerationLine
						});
			}
			return new Object[] {stats.toArray()};
		}
		else return new Object[] {};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] activate(Context context, Arguments args) {
		if (isMultiblockAssembled()) {
			getMultiblock().computerActivated = true;
			getMultiblock().setIsReactorOn();
		}
		return new Object[] {};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] deactivate(Context context, Arguments args) {
		if (isMultiblockAssembled()) {
			getMultiblock().computerActivated = false;
			getMultiblock().setIsReactorOn();
		}
		return new Object[] {};
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] clearAllFluids(Context context, Arguments args) {
		if (isMultiblockAssembled()) {
			getMultiblock().clearAllFluids();
		}
		return new Object[] {};
	}
}
