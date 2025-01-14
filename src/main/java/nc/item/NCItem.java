package nc.item;

import java.util.List;

import javax.annotation.Nullable;

import nc.util.InfoHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NCItem extends Item implements IInfoItem {
	
	public final TextFormatting fixedColor;
	private final String[] fixedTooltip, tooltip;
	public String[] fixedInfo, info;
	
	public NCItem(TextFormatting fixedColor, String[] fixedTooltip, String... tooltip) {
		this.fixedColor = fixedColor;
		this.fixedTooltip = fixedTooltip;
		this.tooltip = tooltip;
	}
	
	public NCItem(TextFormatting fixedColor, String... tooltip) {
		this(fixedColor, InfoHelper.EMPTY_ARRAY, tooltip);
	}
	
	public NCItem(String[] fixedTooltip, String... tooltip) {
		this(TextFormatting.AQUA, fixedTooltip, tooltip);
	}
	
	public NCItem(String... tooltip) {
		this(InfoHelper.EMPTY_ARRAY, tooltip);
	}
	
	@Override
	public void setInfo() {
		fixedInfo = InfoHelper.buildFixedInfo(getTranslationKey(), fixedTooltip);
		info = InfoHelper.buildInfo(getTranslationKey(), tooltip);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		super.addInformation(itemStack, world, tooltip, flag);
		if (info.length + fixedInfo.length > 0) InfoHelper.infoFull(tooltip, fixedColor, fixedInfo, info);
	}
	
	protected ActionResult<ItemStack> actionResult(boolean success, ItemStack stack) {
		return new ActionResult<ItemStack>(success ? EnumActionResult.SUCCESS : EnumActionResult.FAIL, stack);
	}
	
	protected boolean isStackOnHotbar(ItemStack itemStack, EntityPlayer player) {
		for (ItemStack hotbarStack : player.inventory.mainInventory.subList(0, 9)) {
			if (itemStack.isItemEqual(hotbarStack)) return true;
		}
		return false;
	}
	
	protected boolean isStackInInventory(ItemStack itemStack, EntityPlayer player) {
		for (ItemStack hotbarStack : player.inventory.mainInventory) {
			if (itemStack.isItemEqual(hotbarStack)) return true;
		}
		return false;
	}
}
