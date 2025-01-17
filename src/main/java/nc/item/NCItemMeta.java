package nc.item;

import java.util.List;

import javax.annotation.Nullable;

import nc.enumm.IItemMeta;
import nc.init.NCItems;
import nc.util.InfoHelper;
import nc.util.ItemStackHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NCItemMeta<T extends Enum<T> & IStringSerializable & IItemMeta> extends Item implements IInfoItem {
	
	private final Class<T> enumm;
	public final T[] values;
	private final String[][] tooltips;
	public String[][] info;
	
	public NCItemMeta(Class<T> enumm, String[]... tooltips) {
		setHasSubtypes(true);
		this.enumm = enumm;
		values = enumm.getEnumConstants();
		this.tooltips = tooltips;
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (isInCreativeTab(tab)) for (int i = 0; i < values.length; i++) {
			items.add(new ItemStack(this, 1, i));
		}
	}
	
	@Override
	public String getTranslationKey(ItemStack stack) {
		for (int i = 0; i < values.length; i++) {
			if (ItemStackHelper.getMetadata(stack) == i) return getTranslationKey() + "." + values[i].getName();
			else continue;
		}
		return getTranslationKey() + "." + values[0].getName();
	}
	
	@Override
	public void setInfo() {
		info = InfoHelper.buildInfo(getTranslationKey(), enumm, tooltips);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		super.addInformation(itemStack, world, tooltip, flag);
		int meta = itemStack.getMetadata();
		if (info.length != 0 && info.length > meta && info[meta].length > 0) {
			InfoHelper.infoFull(tooltip, info[meta]);
		}
	}
	
	protected ActionResult<ItemStack> actionResult(boolean success, ItemStack stack) {
		return new ActionResult<ItemStack>(success ? EnumActionResult.SUCCESS : EnumActionResult.FAIL, stack);
	}
	
	// Allow upgrades to be right-clicked into machines
	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
		return stack.getItem() == NCItems.upgrade;
	}
}
