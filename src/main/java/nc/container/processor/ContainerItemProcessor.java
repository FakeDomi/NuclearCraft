package nc.container.processor;

import nc.container.ContainerTile;
import nc.init.NCItems;
import nc.recipe.ProcessorRecipeHandler;
import nc.tile.processor.TileItemProcessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerItemProcessor extends ContainerTile {
	
	public final TileItemProcessor tile;
	public final ProcessorRecipeHandler recipeHandler;
	
	protected ItemStack speedUpgrade = new ItemStack(NCItems.upgrade, 1, 0);
	protected ItemStack energyUpgrade = new ItemStack(NCItems.upgrade, 1, 1);
	
	public ContainerItemProcessor(EntityPlayer player, TileItemProcessor tileEntity, ProcessorRecipeHandler recipeHandler) {
		super(tileEntity);
		tile = tileEntity;
		this.recipeHandler = recipeHandler;
		
		tileEntity.beginUpdatingPlayer(player);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tile.isUsableByPlayer(player);
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		tile.stopUpdatingPlayer(player);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);
		int upgrades = tile.hasUpgrades? 2 : 0;
		int invStart = tile.itemInputSize + tile.itemOutputSize + upgrades;
		int speedUpgradeSlot = tile.itemInputSize + tile.itemOutputSize;
		int otherUpgradeSlot = tile.itemInputSize + tile.itemOutputSize + 1;
		int invEnd = tile.itemInputSize + tile.itemOutputSize + 36 + upgrades;
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (index >= tile.itemInputSize && index < invStart) {
				if (!mergeItemStack(itemstack1, invStart, invEnd, false)) {
					return ItemStack.EMPTY;
				}
				slot.onSlotChange(itemstack1, itemstack);
			}
			else if (index >= invStart) {
				if (tile.isItemValidForSlot(speedUpgradeSlot, itemstack1) && tile.hasUpgrades && itemstack1.getItem() == NCItems.upgrade) {
					if (!mergeItemStack(itemstack1, speedUpgradeSlot, speedUpgradeSlot + 1, false)) {
						return ItemStack.EMPTY;
					}
				}
				else if (tile.isItemValidForSlot(otherUpgradeSlot, itemstack1) && tile.hasUpgrades && itemstack1.getItem() == NCItems.upgrade) {
					if (!mergeItemStack(itemstack1, otherUpgradeSlot, otherUpgradeSlot + 1, false)) {
						return ItemStack.EMPTY;
					}
				}
				
				else if (recipeHandler.isValidItemInput(itemstack1)) {
					if (!mergeItemStack(itemstack1, 0, tile.itemInputSize, false)) {
						return ItemStack.EMPTY;
					}
				}
				else if (index >= invStart && index < invEnd - 9) {
					if (!mergeItemStack(itemstack1, invEnd - 9, invEnd, false)) {
						return ItemStack.EMPTY;
					}
				}
				else if (index >= invEnd - 9 && index < invEnd && !mergeItemStack(itemstack1, invStart, invEnd - 9, false)) {
					return ItemStack.EMPTY;
				}
			}
			else if (!mergeItemStack(itemstack1, invStart, invEnd, false)) {
				return ItemStack.EMPTY;
			}
			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			}
			else {
				slot.onSlotChanged();
			}
			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(player, itemstack1);
		}
		return itemstack;
	}
}
