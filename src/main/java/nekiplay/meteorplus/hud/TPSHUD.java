package nekiplay.meteorplus.hud;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import nekiplay.meteorplus.MeteorPlusAddon;

import java.util.ArrayDeque;
import java.util.Deque;

public class TPSHUD extends HudElement {
	public static final HudElementInfo<TPSHUD> INFO = new HudElementInfo<>(
		MeteorPlusAddon.HUD_GROUP,
		"tps-hud",
		"显示服务器真实 TPS（基于 WorldTimeUpdate 包），前缀颜色可自定义，数值颜色动态",
		TPSHUD::new
	);

	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final Setting<SettingColor> prefixColor = sgGeneral.add(new ColorSetting.Builder()
		.name("prefix-color").description("TPS 前缀颜色").defaultValue(new SettingColor(255,255,255)).build());
	private final Setting<SettingColor> goodColor = sgGeneral.add(new ColorSetting.Builder()
		.name("good-color").description("TPS >= 18 的颜色").defaultValue(new SettingColor(0,255,0)).build());
	private final Setting<SettingColor> warnColor = sgGeneral.add(new ColorSetting.Builder()
		.name("warn-color").description("TPS 介于 12~18 的颜色").defaultValue(new SettingColor(255,255,0)).build());
	private final Setting<SettingColor> badColor = sgGeneral.add(new ColorSetting.Builder()
		.name("bad-color").description("TPS < 12 的颜色").defaultValue(new SettingColor(255,0,0)).build());
	private final Setting<Boolean> shadow = sgGeneral.add(new BoolSetting.Builder()
		.name("shadow").description("文字阴影").defaultValue(true).build());
	private final Setting<Integer> precision = sgGeneral.add(new IntSetting.Builder()
		.name("precision").description("小数位数").defaultValue(2).min(0).max(4).build());

	private static final int WINDOW_SIZE = 20;
	private final Deque<Double> samples = new ArrayDeque<>();
	private long lastPacketTime = -1;
	private double tps = 20.0;

	public TPSHUD() { super(INFO); }

	@EventHandler
	private void onPacketReceive(PacketEvent.Receive event) {
		if (!(event.packet instanceof WorldTimeUpdateS2CPacket)) return;
		long now = System.currentTimeMillis();
		if (lastPacketTime > 0) {
			long delta = now - lastPacketTime;
			double instant = 20.0 * (1000.0 / delta);
			// clamp
			instant = Math.max(0.0, Math.min(20.0, instant));
			samples.addLast(instant);
			if (samples.size() > WINDOW_SIZE) samples.removeFirst();
			// average
			double sum = 0;
			for (double v : samples) sum += v;
			tps = sum / samples.size();
		}
		lastPacketTime = now;
	}

	@Override
	public void render(HudRenderer renderer) {
		double x = this.x, y = this.y;
		String fmt = String.format("%%.%df", precision.get());
		String prefix = "TPS:";
		String value = String.format(fmt, tps);

		// 绘制前缀
		renderer.text(prefix, x, y, prefixColor.get(), shadow.get(), -1);
		double prefixW = renderer.textWidth(prefix, shadow.get(), -1);
		double spaceW = renderer.textWidth(" ", shadow.get(), -1);

		// 绘制数值部分
		Color valColor = tps >= 18 ? goodColor.get() : (tps >= 12 ? warnColor.get() : badColor.get());
		renderer.text(" ", x + prefixW, y, prefixColor.get(), shadow.get(), -1);
		renderer.text(value, x + prefixW + spaceW, y, valColor, shadow.get(), -1);

		// 设置大小
		double totalW = prefixW + spaceW + renderer.textWidth(value, shadow.get(), -1);
		setSize(totalW, renderer.textHeight(shadow.get(), -1));
	}
}
