package nc.worldgen.biome;

import nc.Global;
import nc.config.NCConfig;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder(Global.MOD_ID)
public class NCBiomes {
	
	public final static BiomeNuclearWasteland NUCLEAR_WASTELAND = new BiomeNuclearWasteland();
	
	@Mod.EventBusSubscriber(modid = Global.MOD_ID)
	public static class RegistrationHandler {
		@SubscribeEvent
		public static void onEvent(final RegistryEvent.Register<Biome> event) {
			final IForgeRegistry<Biome> registry = event.getRegistry();
			registry.register(NUCLEAR_WASTELAND.setRegistryName(Global.MOD_ID, "nuclear_wasteland"));
		}
	}
	
	public static void initBiomeManagerAndDictionary() {
		if (NCConfig.wasteland_biome) {
			BiomeManager.addBiome(BiomeType.DESERT, new BiomeEntry(NUCLEAR_WASTELAND, NCConfig.wasteland_biome_weight));
			BiomeManager.addSpawnBiome(NUCLEAR_WASTELAND);
			BiomeManager.addStrongholdBiome(NUCLEAR_WASTELAND);
			BiomeDictionary.addTypes(NUCLEAR_WASTELAND, BiomeDictionary.Type.DEAD, BiomeDictionary.Type.DRY, BiomeDictionary.Type.HOT, BiomeDictionary.Type.SAVANNA, BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.WASTELAND);
		}
	}
}
