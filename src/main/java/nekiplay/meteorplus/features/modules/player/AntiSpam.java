package nekiplay.meteorplus.features.modules.player;

import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;      // 新增：正确导入 Categories
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

import java.security.SecureRandom;

public class AntiSpam extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	// 后缀长度可配置
	private final Setting<Integer> length = sgGeneral.add(new IntSetting.Builder()
		.name("length")
		.description("Add random characters")
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
		super(Categories.Player, "Anti-Spam", "Add random characters");  // 保留你的名称和描述，只改分类
	}

	@EventHandler
	private void onSend(SendMessageEvent event) {
		StringBuilder sb = new StringBuilder(event.message);
		sb.append(" <");
		for (int i = 0; i < length.get(); i++) {
			sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
		}
		sb.append(">");
		event.message = sb.toString();
	}
}
