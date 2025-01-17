package nc.tile.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import nc.config.NCConfig;
import nc.recipe.NCRecipes;
import nc.recipe.ProcessorRecipe;
import nc.recipe.ProcessorRecipeHandler;
import nc.recipe.RecipeHelper;
import nc.recipe.RecipeInfo;
import nc.tile.dummy.IInterfaceable;
import nc.tile.energy.ITileEnergy;
import nc.tile.energy.TileEnergy;
import nc.tile.internal.energy.EnergyConnection;
import nc.tile.internal.fluid.Tank;
import nc.util.EnergyHelper;
import nc.util.ItemStackHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileDecayGenerator extends TileEnergy implements IInterfaceable {
	
	Random rand = new Random();
	public int tickCount;
	
	public static final ProcessorRecipeHandler RECIPE_HANDLER = NCRecipes.decay_generator;
	protected RecipeInfo<ProcessorRecipe>[] recipes = new RecipeInfo[6];
	
	public static final double DEFAULT_LIFETIME = 1200D/NCConfig.machine_update_rate;
	
	protected int generatorCount;
	
	public TileDecayGenerator() {
		super(maxPower(), ITileEnergy.energyConnectionAll(EnergyConnection.OUT));
	}
	
	@Override
	public void onAdded() {
		for (EnumFacing side : EnumFacing.VALUES) refreshRecipe(side);
	}
	
	@Override
	public void update() {
		super.update();
		if(!world.isRemote) {
			tickGenerator();
			if (generatorCount == 0) {
				getEnergyStorage().changeEnergyStored(getGenerated());
				getRadiationSource().setRadiationLevel(getRadiation());
			}
			pushEnergy();
		}
	}
	
	public void tickGenerator() {
		generatorCount++; generatorCount %= NCConfig.machine_update_rate;
	}
	
	private static int maxPower() {
		int max = 0;
		List<ProcessorRecipe> recipes = RECIPE_HANDLER.getRecipeList();
		for (ProcessorRecipe recipe : recipes) {
			if (recipe == null) continue;
			max = Math.max(max, recipe.getDecayPower());
		}
		return 20*max;
	}
	
	public int getGenerated() {
		int power = 0;
		for (EnumFacing side : EnumFacing.VALUES) {
			power += decayGen(side);
		}
		return power;
	}
	
	public double getRadiation() {
		double radiation = 0D;
		for (EnumFacing side : EnumFacing.VALUES) {
			if (getDecayRecipeInfo(side) != null) radiation += getDecayRecipeInfo(side).getRecipe().getDecayRadiation();
		}
		return radiation;
	}
	
	public int decayGen(EnumFacing side) {
		if (getDecayRecipeInfo(side) == null) return 0;
		ItemStack stack = getOutput(side);
		if (stack == null || stack.isEmpty()) return 0;
		if (rand.nextDouble()*getRecipeLifetime(side) < 1D) {
			IBlockState block = ItemStackHelper.getBlockStateFromStack(stack);
			if (block == null) return 0;
			getWorld().setBlockState(getPos().offset(side), block);
			refreshRecipe(side);
			
		}
		return getRecipePower(side);
	}
	
	@Override
	public void onBlockNeighborChanged(IBlockState state, World world, BlockPos pos, BlockPos fromPos) {
		super.onBlockNeighborChanged(state, world, pos, fromPos);
		for (EnumFacing side : EnumFacing.VALUES) refreshRecipe(side);
	}
	
	public void refreshRecipe(EnumFacing side) {
		List<ItemStack> input = Arrays.asList(ItemStackHelper.blockStateToStack(world.getBlockState(getPos().offset(side))));
		recipes[side.getIndex()] = RECIPE_HANDLER.getRecipeInfoFromInputs(input, new ArrayList<Tank>());
	}
	
	// IC2
	
	@Override
	public int getEUSourceTier() {
		return EnergyHelper.getEUTier(maxPower());
	}
	
	@Override
	public int getEUSinkTier() {
		return 10;
	}
	
	// Recipe from BlockPos
	
	public RecipeInfo<ProcessorRecipe> getDecayRecipeInfo(EnumFacing side) {
		return recipes[side.getIndex()];
	}
	
	public double getRecipeLifetime(EnumFacing side) {
		if (getDecayRecipeInfo(side) == null) return DEFAULT_LIFETIME;
		return getDecayRecipeInfo(side).getRecipe().getDecayLifetime();
	}
	
	public int getRecipePower(EnumFacing side) {
		if (getDecayRecipeInfo(side) == null) return 0;
		return getDecayRecipeInfo(side).getRecipe().getDecayPower();
	}
	
	public ItemStack getOutput(EnumFacing side) {
		if (getDecayRecipeInfo(side) == null) return ItemStack.EMPTY;
		ItemStack output = RecipeHelper.getItemStackFromIngredientList(getDecayRecipeInfo(side).getRecipe().itemProducts(), 0);
		if (output != null) return output;
		return ItemStack.EMPTY;
	}
}
