package nc.integration.crafttweaker;

import java.util.ArrayList;
import java.util.List;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import nc.recipe.ProcessorRecipe;
import nc.recipe.ProcessorRecipeHandler;
import nc.recipe.RecipeHelper;
import nc.recipe.ingredient.ChanceFluidIngredient;
import nc.recipe.ingredient.ChanceItemIngredient;
import nc.recipe.ingredient.EmptyFluidIngredient;
import nc.recipe.ingredient.EmptyItemIngredient;
import nc.recipe.ingredient.IFluidIngredient;
import nc.recipe.ingredient.IItemIngredient;

public class AddProcessorRecipe implements IAction {
	
	public static boolean hasErrored = false;
	
	public List<IItemIngredient> itemIngredients;
	public List<IFluidIngredient> fluidIngredients;
	public List<IItemIngredient> itemProducts;
	public List<IFluidIngredient> fluidProducts;
	public List extras;
	public ProcessorRecipe recipe;
	public boolean inputsAllNull = true, ingredientError, wasNull, wrongSize;
	public final ProcessorRecipeHandler recipeHandler;

	public AddProcessorRecipe(ProcessorRecipeHandler recipeHandler, List<Object> objects) {
		this.recipeHandler = recipeHandler;
		
		int listCount = 0, ingredientCount = 0;
		List<IItemIngredient> itemIngredients = new ArrayList<IItemIngredient>();
		List<IFluidIngredient> fluidIngredients = new ArrayList<IFluidIngredient>();
		List<IItemIngredient> itemProducts = new ArrayList<IItemIngredient>();
		List<IFluidIngredient> fluidProducts = new ArrayList<IFluidIngredient>();
		List extras = new ArrayList();
		
		while (listCount < objects.size()) {
			Object object = objects.get(listCount);
			Object nextObject = listCount + 1 < objects.size() ? objects.get(listCount + 1) : null;
			Object nextNextObject = listCount + 2 < objects.size() ? objects.get(listCount + 2) : null;
			Object nextNextNextObject = listCount + 3 < objects.size() ? objects.get(listCount + 3) : null;
			if (ingredientCount < recipeHandler.itemInputSize) {
				if (object != null) {
					if (!(object instanceof IIngredient)) {
						ingredientError = true;
						return;
					}
					inputsAllNull = false;
				}
				IItemIngredient ingredient = CTHelper.buildAdditionItemIngredient(object, recipeHandler);
				if (ingredient == null) {
					ingredientError = true;
					return;
				}
				itemIngredients.add(ingredient);
			} else if (ingredientCount < recipeHandler.itemInputSize + recipeHandler.fluidInputSize) {
				if (object != null) {
					if (!(object instanceof IIngredient)) {
						ingredientError = true;
						return;
					}
					inputsAllNull = false;
				}
				IFluidIngredient ingredient = CTHelper.buildAdditionFluidIngredient(object, recipeHandler);
				if (ingredient == null) {
					ingredientError = true;
					return;
				}
				fluidIngredients.add(ingredient);
			} else if (ingredientCount < recipeHandler.itemInputSize + recipeHandler.fluidInputSize + recipeHandler.itemOutputSize) {
				if (object != null) {
					if (!(object instanceof IIngredient)) {
						ingredientError = true;
						return;
					}
				}
				IItemIngredient ingredient = CTHelper.buildAdditionItemIngredient(object, recipeHandler);
				if (ingredient == null) {
					ingredientError = true;
					return;
				}
				if (nextObject instanceof Integer && nextNextObject instanceof Integer) {
					int chancePercent = (Integer) nextObject;
					int minStackSize = (Integer) nextNextObject;
					if (chancePercent <= 0) ingredient = new EmptyItemIngredient();
					else if (chancePercent < 100) ingredient = new ChanceItemIngredient(ingredient, chancePercent, minStackSize);
					listCount += 2;
				}
				else if (nextObject instanceof Integer) {
					int chancePercent = (Integer) nextObject;
					if (chancePercent <= 0) ingredient = new EmptyItemIngredient();
					else if (chancePercent < 100) ingredient = new ChanceItemIngredient(ingredient, chancePercent);
					listCount++;
				}
				itemProducts.add(ingredient);
			} else if (ingredientCount < recipeHandler.itemInputSize + recipeHandler.fluidInputSize + recipeHandler.itemOutputSize + recipeHandler.fluidOutputSize) {
				if (object != null) {
					if (!(object instanceof IIngredient)) {
						ingredientError = true;
						return;
					}
				}
				IFluidIngredient ingredient = CTHelper.buildAdditionFluidIngredient(object, recipeHandler);
				if (ingredient == null) {
					ingredientError = true;
					return;
				}
				if (nextObject instanceof Integer && nextNextObject instanceof Integer && nextNextNextObject instanceof Integer) {
					int chancePercent = (Integer) nextObject;
					int stackDiff = (Integer) nextNextObject;
					int minStackSize = (Integer) nextNextNextObject;
					if (chancePercent <= 0) ingredient = new EmptyFluidIngredient();
					else if (chancePercent < 100) ingredient = new ChanceFluidIngredient(ingredient, chancePercent, stackDiff, minStackSize);
					listCount += 3;
				}
				else if (nextObject instanceof Integer && nextNextObject instanceof Integer) {
					int chancePercent = (Integer) nextObject;
					int stackDiff = (Integer) nextNextObject;
					if (chancePercent <= 0) ingredient = new EmptyFluidIngredient();
					else if (chancePercent < 100) ingredient = new ChanceFluidIngredient(ingredient, chancePercent, stackDiff);
					listCount += 2;
				}
				fluidProducts.add(ingredient);
			} else {
				extras.add(object);
			}
			listCount++;
			ingredientCount++;
		}
		
		if (itemIngredients.size() != recipeHandler.itemInputSize || fluidIngredients.size() != recipeHandler.fluidInputSize || itemProducts.size() != recipeHandler.itemOutputSize || fluidProducts.size() != recipeHandler.fluidOutputSize) {
			CraftTweakerAPI.logError("A " + recipeHandler.getRecipeName() + " recipe was the wrong size");
			wrongSize = true;
			return;
		}
		
		this.itemIngredients = itemIngredients;
		this.fluidIngredients = fluidIngredients;
		this.itemProducts = itemProducts;
		this.fluidProducts = fluidProducts;
		this.extras = extras;
		
		recipe = recipeHandler.buildRecipe(itemIngredients, fluidIngredients, itemProducts, fluidProducts, extras, recipeHandler.isShapeless);
		if (recipe == null) wasNull = true;
	}
	
	@Override
	public void apply() {
		if (!inputsAllNull && !ingredientError && !wasNull && !wrongSize) {
			recipeHandler.addRecipe(recipe);
		}
	}
	
	@Override
	public String describe() {
		if (inputsAllNull || ingredientError || wasNull || wrongSize) {
			if (ingredientError || wrongSize) callError();
			return String.format("Error: Failed to add %s recipe: %s", recipeHandler.getRecipeName(), RecipeHelper.getRecipeString(itemIngredients, fluidIngredients, itemProducts, fluidProducts));
		}
		return String.format("Adding %s recipe: %s", recipeHandler.getRecipeName(), RecipeHelper.getRecipeString(itemIngredients, fluidIngredients, itemProducts, fluidProducts));
	}
	
	public static void callError() {
		if (!hasErrored) {
			CraftTweakerAPI.logError("At least one NuclearCraft CraftTweaker recipe addition method has errored - check the CraftTweaker log for more details");
		}
		hasErrored = true;
	}
}
