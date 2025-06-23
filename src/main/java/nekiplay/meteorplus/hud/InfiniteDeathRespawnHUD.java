package nekiplay.meteorplus.hud;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import nekiplay.meteorplus.MeteorPlusAddon;
import nekiplay.meteorplus.features.modules.player.InfiniteDeathRespawn;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.util.math.Vec3d;

public class InfiniteDeathRespawnHUD extends HudElement {
	public static final HudElementInfo<InfiniteDeathRespawnHUD> INFO = new HudElementInfo<>(
		MeteorPlusAddon.HUD_GROUP,
		"infinite-death-respawn",
		"显示 InfiniteDeathRespawn 模块状态。",
		InfiniteDeathRespawnHUD::new
	);

	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final SettingGroup sgBackground = settings.createGroup("Background");
	private final SettingGroup sgScale = settings.createGroup("Scale");

	private final Setting<Boolean> shadow = sgGeneral.add(new BoolSetting.Builder()
		.name("shadow")
		.description("是否启用文字阴影。")
		.defaultValue(true)
		.build()
	);

	private final Setting<SettingColor> activeColor = sgGeneral.add(new ColorSetting.Builder()
		.name("active-color")
		.description("模块启用时的颜色。")
		.defaultValue(new SettingColor(0, 255, 0))
		.build()
	);

	private final Setting<SettingColor> inactiveColor = sgGeneral.add(new ColorSetting.Builder()
		.name("inactive-color")
		.description("模块关闭时的颜色。")
		.defaultValue(new SettingColor(255, 0, 0))
		.build()
	);

	private final Setting<SettingColor> deathPosColor = sgGeneral.add(new ColorSetting.Builder()
		.name("deathpos-color")
		.description("死亡点坐标颜色。")
		.defaultValue(new SettingColor(255, 255, 255))
		.build()
	);

	private final Setting<Boolean> background = sgBackground.add(new BoolSetting.Builder()
		.name("background")
		.description("是否显示背景。")
		.defaultValue(true)
		.build()
	);

	private final Setting<SettingColor> backgroundColor = sgBackground.add(new ColorSetting.Builder()
		.name("background-color")
		.description("背景颜色。")
		.visible(background::get)
		.defaultValue(new SettingColor(0, 0, 0, 100))
		.build()
	);

	private final Setting<Boolean> customScale = sgScale.add(new BoolSetting.Builder()
		.name("custom-scale")
		.description("是否使用自定义缩放。")
		.defaultValue(false)
		.build()
	);

	private final Setting<Double> scale = sgScale.add(new DoubleSetting.Builder()
		.name("scale")
		.description("文本缩放。")
		.visible(customScale::get)
		.defaultValue(1.0)
		.min(0.5)
		.sliderRange(0.5, 3.0)
		.build()
	);

	public InfiniteDeathRespawnHUD() {
		super(INFO);
	}

	@Override
	public void render(HudRenderer renderer) {
		if (background.get()) {
			renderer.quad(x, y, getWidth(), getHeight(), backgroundColor.get());
		}

		InfiniteDeathRespawn mod = Modules.get().get(InfiniteDeathRespawn.class);

		String statusText = "InfiniteRespawn: " + (mod.isActive() ? "ON" : "OFF");
		Color statusColor = mod.isActive() ? activeColor.get() : inactiveColor.get();

		double xPos = x;
		double yPos = y;

		double line1Width = renderer.text(statusText, xPos, yPos, statusColor, shadow.get(), getScale());
		double totalHeight = renderer.textHeight(shadow.get(), getScale());

		Vec3d deathPos = mod.getDeathPos();
		if (mod.isActive() && deathPos != null) {
			String deathText = String.format("DeathPos: %.1f, %.1f, %.1f",
				deathPos.x, deathPos.y, deathPos.z
			);
			renderer.text(deathText, xPos, yPos + totalHeight + 2, deathPosColor.get(), shadow.get(), getScale());
			line1Width = Math.max(line1Width, renderer.textWidth(deathText, shadow.get(), getScale()));
			totalHeight += renderer.textHeight(shadow.get(), getScale()) + 2;
		}

		setSize(line1Width, totalHeight);
	}

	private double getScale() {
		return customScale.get() ? scale.get() : -1;
	}
}
