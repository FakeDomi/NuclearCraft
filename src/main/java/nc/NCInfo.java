package nc;

import nc.config.NCConfig;
import nc.enumm.IFissionStats;
import nc.enumm.IItemMeta;
import nc.enumm.MetaEnums;
import nc.enumm.MetaEnums.CoolerType;
import nc.enumm.MetaEnums.IngotType;
import nc.enumm.MetaEnums.RadShieldingType;
import nc.enumm.MetaEnums.UpgradeType;
import nc.multiblock.turbine.TurbineDynamoCoilType;
import nc.radiation.RadiationHelper;
import nc.util.CollectionHelper;
import nc.util.InfoHelper;
import nc.util.Lang;
import nc.util.NCMath;
import net.minecraft.util.IStringSerializable;

public class NCInfo {
	
	// Coolers
	
	public static String[][] coolerInfo() {
		String[][] info = new String[CoolerType.values().length][];
		info[0] = new String[] {};
		for (int i = 1; i < CoolerType.values().length; i++) {
			info[i] = CollectionHelper.concatenate(new String[] {coolingRateString(i)}, InfoHelper.formattedInfo(coolerInfoString(i)));
		}
		return info;
	}
	
	private static String coolingRateString(int meta) {
		return Lang.localise("tile." + Global.MOD_ID + ".cooler.cooling_rate") + " " + NCMath.decimalPlaces(CoolerType.values()[meta].getCooling(), 2) + " H/t";
	}
	
	private static String coolerInfoString(int meta) {
		return Lang.localise("tile." + Global.MOD_ID + ".cooler." + CoolerType.values()[meta].name().toLowerCase() + ".desc");
	}
	
	// Fuel Rods
	
	public static <T extends Enum<T> & IStringSerializable & IItemMeta & IFissionStats> String[][] fuelRodInfo(T[] values) {
		String[][] info = new String[values.length][];
		for (int i = 0; i < values.length; i++) {
			info[i] = new String[] {Lang.localise("item." + Global.MOD_ID + ".fission_fuel.base_time.desc", NCMath.decimalPlaces(values[i].getBaseTime()/(1200D*NCConfig.fission_fuel_use), 2)), Lang.localise("item." + Global.MOD_ID + ".fission_fuel.base_power.desc", NCMath.decimalPlaces(values[i].getBasePower()*NCConfig.fission_power, 2)), Lang.localise("item." + Global.MOD_ID + ".fission_fuel.base_heat.desc", NCMath.decimalPlaces(values[i].getBaseHeat()*NCConfig.fission_heat_generation, 2))};
		}
		return info;
	}
	
	// Ingot Blocks
	
	public static String moderatorPowerInfo = Lang.localise("info.moderator.power", NCMath.decimalPlaces(6D/NCConfig.fission_moderator_extra_power, 2));
	public static String moderatorHeatInfo = Lang.localise("info.moderator.heat", NCMath.decimalPlaces(6D/NCConfig.fission_moderator_extra_heat, 2));
	
	public static String[][] ingotBlockInfo() {
		String[][] info = new String[IngotType.values().length][];
		for (int i = 0; i < IngotType.values().length; i++) {
			info[i] = InfoHelper.EMPTY_ARRAY;
		}
		info[8] = InfoHelper.formattedInfo(Lang.localise(ingotBlockInfoString(8), moderatorPowerInfo, moderatorHeatInfo));
		info[9] = InfoHelper.formattedInfo(Lang.localise(ingotBlockInfoString(9), moderatorPowerInfo, moderatorHeatInfo));
		return info;
	}
	
	private static String ingotBlockInfoString(int meta) {
		return "tile." + Global.MOD_ID + ".ingot_block." + IngotType.values()[meta].name().toLowerCase() + ".desc";
	}
	
	public static String[][] ingotBlockFixedInfo() {
		String[][] info = new String[IngotType.values().length][];
		for (int i = 0; i < IngotType.values().length; i++) {
			info[i] = InfoHelper.EMPTY_ARRAY;
		}
		info[8] = new String[] {Lang.localise("info.moderator.desc")};
		info[9] = new String[] {Lang.localise("info.moderator.desc")};
		return info;
	}
	
	// Dynamo Coils
	
	public static String[][] dynamoCoilInfo() {
		String[][] info = new String[TurbineDynamoCoilType.values().length][];
		info[0] = new String[] {};
		for (int i = 0; i < TurbineDynamoCoilType.values().length; i++) {
			info[i] = CollectionHelper.concatenate(new String[] {coilConductivityString(i)}, InfoHelper.formattedInfo(coiInfoString(i)));
		}
		return info;
	}
	
	private static String coilConductivityString(int meta) {
		return Lang.localise("tile." + Global.MOD_ID + ".turbine_dynamo_coil.conductivity") + " " + NCMath.decimalPlaces(100D*TurbineDynamoCoilType.values()[meta].getConductivity(), 1) + "%";
	}
	
	private static String coiInfoString(int meta) {
		return Lang.localise("tile." + Global.MOD_ID + ".turbine_dynamo_coil." + TurbineDynamoCoilType.values()[meta].name().toLowerCase() + ".desc");
	}
	
	// Speed Upgrade
	
	public static final String[] POLY_POWER = new String[] {"linearly", "quadratically", "cubicly", "quarticly", "quinticly", "sexticly", "septicly", "octicly", "nonicly", "decicly", "undecicly", "duodecicly", "tredecicly", "quattuordecicly", "quindecicly"};
	
	public static String[][] upgradeInfo() {
		String[][] info = new String[UpgradeType.values().length][];
		for (int i = 0; i < UpgradeType.values().length; i++) {
			info[i] = InfoHelper.EMPTY_ARRAY;
		}
		info[0] = InfoHelper.formattedInfo(Lang.localise("item.nuclearcraft.upgrade.speed_desc", powerString(NCConfig.speed_upgrade_power_laws[0]), powerString(NCConfig.speed_upgrade_power_laws[1])));
		info[1] = InfoHelper.formattedInfo(Lang.localise("item.nuclearcraft.upgrade.energy_desc", powerString(NCConfig.energy_upgrade_power_laws[0])));
		return info;
	}
	
	private static String powerString(double power) {
		return (power == (int)power ? "" : Lang.localise("info.nuclearcraft.approximately" + " ")) + POLY_POWER[(int)Math.round(power) - 1];
	}
	
	// Extra Ore Drops
	
	public static <T extends Enum<T> & IStringSerializable & IItemMeta> String[][] oreDropInfo(String type, T[] values, int[] configIds, int[] metas) {
		String[][] info = new String[values.length][];
		for (int i = 0; i < values.length; i++) {
			info[i] = null;
		}
		for (int i = 0; i < configIds.length; i++) {
			String unloc = "item." + Global.MOD_ID + "." + type + "." + values[metas[i]].getName() + ".desc";
			if (Lang.canLocalise(unloc) && NCConfig.ore_drops[configIds[i]]) info[metas[i]] = InfoHelper.formattedInfo(Lang.localise(unloc));
		}
		
		return info;
	}
	
	public static <T extends Enum<T> & IStringSerializable & IItemMeta> String[][] dustOreDropInfo() {
		return oreDropInfo("dust", MetaEnums.DustType.values(), new int[] {1, 2}, new int[] {9, 10});
	}
	
	public static <T extends Enum<T> & IStringSerializable & IItemMeta> String[][] gemOreDropInfo() {
		return oreDropInfo("gem", MetaEnums.GemType.values(), new int[] {0, 3, 5, 6}, new int[] {0, 2, 3, 4});
	}
	
	public static <T extends Enum<T> & IStringSerializable & IItemMeta> String[][] gemDustOreDropInfo() {
		return oreDropInfo("gem_dust", MetaEnums.GemDustType.values(), new int[] {4}, new int[] {6});
	}
	
	// Rad Shielding
	
	public static String[][] radShieldingInfo() {
		String[][] info = new String[RadShieldingType.values().length][];
		for (int i = 0; i < RadShieldingType.values().length; i++) {
			info[i] = InfoHelper.formattedInfo(Lang.localise("item.nuclearcraft.rad_shielding.desc" + (NCConfig.radiation_hardcore_containers > 0D ? "_hardcore" : ""), RadiationHelper.resistanceSigFigs(NCConfig.radiation_shielding_level[i])));
		}
		return info;
	}
}
