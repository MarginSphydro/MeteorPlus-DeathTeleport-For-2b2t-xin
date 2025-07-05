package nekiplay.meteorplus.features.modules.player;

import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

import java.security.SecureRandom;
import java.util.List;
import java.util.Collections;

public class AntiSpam extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	// 是否启用自定义前缀绕过
	private final Setting<Boolean> bypassEnabled = sgGeneral.add(new BoolSetting.Builder()
		.name("bypass-enabled")
		.description("Bypass anti-spam for messages starting with specified prefixes.")
		.defaultValue(false)
		.build()
	);

	// 自定义绕过的前缀列表
	private final Setting<List<String>> bypassPrefixes = sgGeneral.add(new StringListSetting.Builder()
		.name("bypass-prefixes")
		.description("List of message prefixes to bypass anti-spam.")
		.defaultValue(Collections.singletonList(";"))
		.build()
	);

	// 后缀随机字符长度
	private final Setting<Integer> length = sgGeneral.add(new IntSetting.Builder()
		.name("length")
		.description("Add random characters to messages.")
		.defaultValue(3)
		.min(1)
		.max(16)
		.sliderMin(1)
		.sliderMax(16)
		.build()
	);

	private static final SecureRandom RANDOM = new SecureRandom();
	private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	public AntiSpam() {
		super(Categories.Player, "anti-spam", "Add random characters to messages");
	}

	@EventHandler
	private void onSend(SendMessageEvent event) {
		String msg = event.message;

		// 如果启用绕过并且消息以任一指定前缀开头，则直接跳过
		if (bypassEnabled.get()) {
			for (String prefix : bypassPrefixes.get()) {
				if (msg.startsWith(prefix)) {
					return;
				}
			}
		}

		// 添加随机后缀
		StringBuilder sb = new StringBuilder(msg);
		sb.append(" <");
		for (int i = 0; i < length.get(); i++) {
			sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
		}
		sb.append(">");
		event.message = sb.toString();
	}
}
