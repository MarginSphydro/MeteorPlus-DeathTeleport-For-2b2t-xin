package nekiplay.meteorplus.features.modules.combat;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;

public class AntiMissca extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Integer> tickDelay = sgGeneral.add(new IntSetting.Builder()
		.name("delay")
		.description("How often to force totem into offhand in ticks.")
		.defaultValue(1)
		.min(1)
		.sliderMax(10)
		.build()
	);

	private int ticks;

	public AntiMissca() {
		super(Categories.Combat, "anti-missca", "Tries to always keep a fresh totem in offhand to prevent miss crystal.");
	}

	@EventHandler
	private void onTick(TickEvent.Pre event) {
		ticks++;
		if (ticks >= tickDelay.get()) {
			FindItemResult result = InvUtils.find(Items.TOTEM_OF_UNDYING);
			if (result.found()) {
				InvUtils.move().from(result.slot()).toOffhand();
			}
			ticks = 0;
		}
	}
}
