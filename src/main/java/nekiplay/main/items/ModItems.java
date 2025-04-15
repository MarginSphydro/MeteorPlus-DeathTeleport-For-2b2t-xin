package nekiplay.main.items;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {

	public static Item register(String path, Function<Item.Settings, Item> factory, Item.Settings settings) {
		final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of("meteorplus", path));
		return Items.register(registryKey, factory, settings);
	}
	public static Item METEOR_PLUS_LOGO_ITEM = null;
	public static Item METEOR_PLUS_LOGO_MODS_ITEM = null;

	public static void initialize() {
		METEOR_PLUS_LOGO_ITEM = register("logo", Item::new, new Item.Settings());
		METEOR_PLUS_LOGO_MODS_ITEM = register("logo_mods", Item::new, new Item.Settings());
	}
}
