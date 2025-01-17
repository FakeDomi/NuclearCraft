package nc.block.fluid;

import nc.fluid.FluidBase;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class NCBlockFluid extends BlockFluidClassic {
	
	protected String name;
	public final Fluid fluid;
	
	public NCBlockFluid(Fluid fluid, Material material) {
		super(fluid, material);
		this.name = "fluid_" + fluid.getName();
		//NuclearCraft.proxy.registerFluidBlockRendering(this, name);
		this.fluid = fluid;
	}
	
	public NCBlockFluid(FluidBase fluid, Material material) {
		super(fluid, material);
		this.name = "fluid_" + fluid.getName();
		//NuclearCraft.proxy.registerFluidBlockRendering(this, name);
		this.fluid = fluid;
	}
	
	@Override
	public boolean canDisplace(IBlockAccess world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock().getMaterial(world.getBlockState(pos)).isLiquid()) return false;
		return super.canDisplace(world, pos);
	}
	
	@Override
	public boolean displaceIfPossible(World world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock().getMaterial(world.getBlockState(pos)).isLiquid()) return false;
		return super.displaceIfPossible(world, pos);
	}
	
	public String getBlockName() {
		return name;
	}
}
