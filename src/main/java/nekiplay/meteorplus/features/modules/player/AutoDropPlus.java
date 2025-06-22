package nekiplay.meteorplus.features.modules.player;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.SlotUtils;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.MeteorPlusAddon;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

import java.util.List;

public class AutoDropPlus extends Module  {
	public AutoDropPlus() {
		super(Categories.Player, "auto-drop", "Auto drop items in inventory.");
	}

	private final SettingGroup defaultGroup = settings.getDefaultGroup();

	private final Setting<List<Item>> items;
	private final Setting<Integer> delay;
	private final Setting<Boolean> workInstant;
	private final Setting<Boolean> removeContainersItems;
	private final Setting<Boolean> autoDropExcludeHotbar;
	private final Setting<Boolean> removeItems;

	{
		// Initialize settings in proper order
		items = defaultGroup.add(new ItemListSetting.Builder()
			.name("drop-items")
			.description("Items to dropping.")
			.build()
		);

		// First declare workInstant without initialization
		final Setting<Boolean>[] workInstantRef = new Setting[1];

		// Initialize delay with onChanged callback
		delay = defaultGroup.add(new IntSetting.Builder()
			.name("delay")
			.description("drop delay.")
			.defaultValue(5)
			.min(0)
			.onChanged(value -> {
				if (value != 0 && workInstantRef[0] != null && workInstantRef[0].get()) {
					workInstantRef[0].set(false);
				}
			})
			.build()
		);

		// Then initialize workInstant
		workInstantRef[0] = defaultGroup.add(new BoolSetting.Builder()
			.name("instant-work")
			.description("Drop or remove items instant.")
			.defaultValue(false)
			.visible(() -> delay.get() == 0)
			.build()
		);
		workInstant = workInstantRef[0];

		// Initialize other settings
		removeContainersItems = defaultGroup.add(new BoolSetting.Builder()
			.name("work-in-containers")
			.description("Work in chests?.")
			.defaultValue(true)
			.build()
		);

		autoDropExcludeHotbar = defaultGroup.add(new BoolSetting.Builder()
			.name("work-in-hotbar")
			.description("Work in hotbar?.")
			.defaultValue(true)
			.build()
		);

		removeItems = defaultGroup.add(new BoolSetting.Builder()
			.name("remover")
			.description("Remove items?.")
			.defaultValue(false)
			.build()
		);
	}

	private int tick = 0;

	@EventHandler
	public void onTickPost(TickEvent.Pre event) {
		int sync = mc.player.currentScreenHandler.syncId;


		for (int i = autoDropExcludeHotbar.get() ? 0 : 9; i < mc.player.getInventory().size(); i++) {
			ItemStack itemStack = mc.player.getInventory().getStack(i);

			if (items.get().contains(itemStack.getItem().asItem())) {
				if (tick == 0) {
					if (removeItems.get() && sync != -1) {
						mc.interactionManager.clickSlot(sync, invIndexToSlotId(i), 300, SlotActionType.SWAP, mc.player);
					}
					else if (!removeItems.get()) { InvUtils.drop().slot(i); }
					if (!workInstant.get()) {
						tick = delay.get();
					}
				}
				else {
					tick--;
				}
				if (!workInstant.get()) {
					break;
				}
			}
		}
		if (removeContainersItems.get()) {
			for (int i = 0; i < SlotUtils.indexToId(SlotUtils.MAIN_START); i++) {
				ScreenHandler handler = mc.player.currentScreenHandler;
				if (!handler.getSlot(i).hasStack()) continue;

				Item item = handler.getSlot(i).getStack().getItem();
				if (items.get().contains(item.asItem())) {
					if (tick == 0) {
						if (removeItems.get()) {
							mc.interactionManager.clickSlot(handler.syncId, getIndexToSlotId(handler, i), 300, SlotActionType.SWAP, mc.player);
						}
						else { InvUtils.drop().slotId(i); }
						if (!workInstant.get()) {
							tick = delay.get();
						}
					} else {
						tick--;
					}
					if (!workInstant.get()) {
						break;
					}
				}
			}
		}
	}
	public static int invIndexToSlotId(int invIndex) {
		return invIndex < 9 && invIndex != -1 ? 44 - (8 - invIndex) : invIndex;
	}

	public static int getIndexToSlotId(ScreenHandler handler, int invIndex) {
		if (handler instanceof GenericContainerScreenHandler genericContainerScreenHandler) {
			int count = genericContainerScreenHandler.slots.size();
			return invIndex < 0 && invIndex != -1 ? count - (-1 - invIndex) : invIndex;
		}
		else if (handler instanceof ShulkerBoxScreenHandler genericContainerScreenHandler) {
			int count = genericContainerScreenHandler.slots.size();
			return invIndex < 0 && invIndex != -1 ? count - (-1 - invIndex) : invIndex;
		}
		return invIndex < 9 && invIndex != -1 ? 44 - (8 - invIndex) : invIndex;
	}
}
