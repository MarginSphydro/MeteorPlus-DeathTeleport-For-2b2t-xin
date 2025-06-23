package nekiplay.meteorplus.features.modules.player;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;

import java.util.*;

public class AutoEz extends Module {
	public AutoEz() {
		super(Categories.Player, "auto-ez", "Auto Send Message when kill a player BY mark_7601 for the Shit Prism Client");
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	// 侦测敌人死亡的范围
	private final Setting<Integer> radius = sgGeneral.add(new IntSetting.Builder()
		.name("radius")
		.description("Range by mark_7601")
		.defaultValue(12)
		.min(1)
		.sliderMax(64)
		.build()
	);

	// 敌人死亡时发送的多条语句（含 {player}）
	private final Setting<List<String>> enemyMessages = sgGeneral.add(new StringListSetting.Builder()
		.name("enemy-messages")
		.description("Message Use {player} to express killed name by mark_7601")
		.defaultValue(Arrays.asList(
			"EZ {player}",
			"GG {player}",
			"Get good, {player}",
			"Trolled by Meteor+, {player}",
			"{player} got clapped"
		))
		.build()
	);

	// 自己死亡时的托词（多句）
	private final Setting<List<String>> selfMessages = sgGeneral.add(new StringListSetting.Builder()
		.name("self-messages")
		.description("Nerver Lose (Mental) by mark_7601")
		.defaultValue(Arrays.asList(
			"My dog ate my PC",
			"I lagged out",
			"My mom turned off the router",
			"I was distracted",
			"That doesn't count"
		))
		.build()
	);

	private final Random random = new Random();
	private final Set<UUID> deadPlayers = new HashSet<>();
	private boolean hasDied = false;

	@EventHandler
	private void onTick(TickEvent.Pre event) {
		if (mc.player == null || mc.world == null) return;

		// 自己死亡
		boolean isDead = mc.player.getHealth() <= 0;

		if (isDead && !hasDied) {
			List<String> selfMsgList = selfMessages.get();
			if (!selfMsgList.isEmpty()) {
				String msg = selfMsgList.get(random.nextInt(selfMsgList.size()));
				ChatUtils.sendPlayerMsg(msg);
			}
			hasDied = true;
		}

		if (!isDead && hasDied) {
			hasDied = false;
		}

		// 敌人死亡
		for (PlayerEntity p : mc.world.getPlayers()) {
			UUID id = p.getUuid();

			if (p == mc.player) {
				deadPlayers.remove(id);
				continue;
			}

			boolean targetIsDead = p.isDead() || p.getHealth() <= 0;

			if (targetIsDead && !deadPlayers.contains(id)) {
				double distSq = mc.player.squaredDistanceTo(p);
				if (distSq <= radius.get() * (double) radius.get()) {
					String name = p.getName().getString();
					List<String> messages = enemyMessages.get();
					if (!messages.isEmpty()) {
						String template = messages.get(random.nextInt(messages.size()));
						String msg = template.replace("{player}", name);
						ChatUtils.sendPlayerMsg(msg);
					}
				}
				deadPlayers.add(id);
			} else if (!targetIsDead && deadPlayers.contains(id)) {
				deadPlayers.remove(id);
			}
		}
	}
}
