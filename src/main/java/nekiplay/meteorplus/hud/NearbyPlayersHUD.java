package nekiplay.meteorplus.hud;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import nekiplay.meteorplus.MeteorPlusAddon;

import java.util.*;
import java.util.stream.Collectors;

public class NearbyPlayersHUD extends HudElement {
	public static final HudElementInfo<NearbyPlayersHUD> INFO = new HudElementInfo<>(
		MeteorPlusAddon.HUD_GROUP,
		"nearby-players-hud",
		"显示附近玩家列表并推送死亡通告（单独停留时间）。",
		NearbyPlayersHUD::new
	);

	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final SettingGroup sgColors = settings.createGroup("Colors");
	private final SettingGroup sgBG = settings.createGroup("Background");
	private final SettingGroup sgScale = settings.createGroup("Scale");
	private final SettingGroup sgTiming = settings.createGroup("Timing");

	private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
		.name("range").description("检测范围(方块)").defaultValue(50).min(1).max(500).build());
	private final Setting<Boolean> showDistance = sgGeneral.add(new BoolSetting.Builder()
		.name("show-distance").description("显示距离").defaultValue(true).build());
	private final Setting<Integer> distancePrecision = sgGeneral.add(new IntSetting.Builder()
		.name("distance-precision").description("距离小数位数").defaultValue(1).min(0).max(3).visible(showDistance::get).build());
	private final Setting<Boolean> showHealth = sgGeneral.add(new BoolSetting.Builder()
		.name("show-health").description("显示血量(含吸收)").defaultValue(true).build());
	private final Setting<Boolean> shadow = sgGeneral.add(new BoolSetting.Builder()
		.name("shadow").description("文字阴影").defaultValue(true).build());

	private final Setting<Integer> killedTime = sgTiming.add(new IntSetting.Builder()
		.name("killed-time").description("死亡通告停留时间(秒)").defaultValue(3).min(1).max(30).build());

	private final Setting<SettingColor> nameColor = sgColors.add(new ColorSetting.Builder()
		.name("name-color").defaultValue(new SettingColor(255,255,255)).build());
	private final Setting<SettingColor> bracketColor = sgColors.add(new ColorSetting.Builder()
		.name("bracket-color").defaultValue(new SettingColor(150,150,150)).visible(showDistance::get).build());
	private final Setting<SettingColor> distanceColor = sgColors.add(new ColorSetting.Builder()
		.name("distance-color").defaultValue(new SettingColor(200,200,200)).visible(showDistance::get).build());
	private final Setting<SettingColor> heartColor = sgColors.add(new ColorSetting.Builder()
		.name("heart-color").defaultValue(new SettingColor(255,0,0)).visible(showHealth::get).build());
	private final Setting<SettingColor> hpHighColor = sgColors.add(new ColorSetting.Builder()
		.name("hp-high-color").description("18~36").defaultValue(new SettingColor(0,255,0)).visible(showHealth::get).build());
	private final Setting<SettingColor> hpMidColor = sgColors.add(new ColorSetting.Builder()
		.name("hp-mid-color").description("12~18").defaultValue(new SettingColor(255,255,0)).visible(showHealth::get).build());
	private final Setting<SettingColor> hpLowColor = sgColors.add(new ColorSetting.Builder()
		.name("hp-low-color").description("0~12").defaultValue(new SettingColor(255,0,0)).visible(showHealth::get).build());
	private final Setting<SettingColor> killedColor = sgColors.add(new ColorSetting.Builder()
		.name("killed-color").description("死亡通告颜色").defaultValue(new SettingColor(255,0,0)).build());

	private final Setting<Boolean> background = sgBG.add(new BoolSetting.Builder()
		.name("background").defaultValue(true).build());
	private final Setting<SettingColor> backgroundColor = sgBG.add(new ColorSetting.Builder()
		.name("background-color").defaultValue(new SettingColor(0,0,0,100)).visible(background::get).build());

	private final Setting<Boolean> customScale = sgScale.add(new BoolSetting.Builder()
		.name("custom-scale").defaultValue(false).build());
	private final Setting<Double> scale = sgScale.add(new DoubleSetting.Builder()
		.name("scale").defaultValue(1.0).min(0.5).max(3.0).visible(customScale::get).build());

	private final Map<UUID, Long> deathTimestamps = new HashMap<>();
	private final Map<UUID, Integer> popCountMap = new HashMap<>();
	private final List<String> deathNotices = new ArrayList<>();

	public NearbyPlayersHUD() {
		super(INFO);
		MeteorClient.EVENT_BUS.subscribe(this);
	}

	@EventHandler
	private void onReceivePacket(PacketEvent.Receive event) {
		if (event.packet instanceof EntityStatusS2CPacket packet) {
			if (packet.getStatus() == 35 && packet.getEntity(MeteorClient.mc.world) instanceof PlayerEntity player) {
				UUID id = player.getUuid();
				popCountMap.put(id, popCountMap.getOrDefault(id, 0) + 1);
			}
		}
	}

	@Override
	public void render(HudRenderer renderer) {
		long now = System.currentTimeMillis();
		long keep = killedTime.get() * 1000L;
		String distFmt = "%." + distancePrecision.get() + "f";
		double txtScale = customScale.get() ? scale.get() : -1;
		double lineH = renderer.textHeight(shadow.get(), txtScale) + 2;

		List<PlayerEntity> list = MeteorClient.mc.world.getPlayers().stream()
			.filter(p -> p != MeteorClient.mc.player && MeteorClient.mc.player.distanceTo(p) <= range.get())
			.sorted(Comparator.comparingDouble(MeteorClient.mc.player::distanceTo))
			.collect(Collectors.toList());

		for (PlayerEntity p : list) {
			UUID id = p.getUuid();
			double hp = p.getHealth() + p.getAbsorptionAmount();
			if (hp <= 0 && !deathTimestamps.containsKey(id)) {
				deathTimestamps.put(id, now);
				int pops = popCountMap.getOrDefault(id, 0);
				String notice = p.getName().getString()
					+ "[" + String.format(distFmt, MeteorClient.mc.player.distanceTo(p)) + "] "
					+ "❤0.0 WAS KILLED (" + pops + " Popped)";
				deathNotices.add(notice);
			}
		}

		Iterator<String> it = deathNotices.iterator();
		for (Iterator<Map.Entry<UUID, Long>> tit = deathTimestamps.entrySet().iterator(); tit.hasNext();) {
			Map.Entry<UUID, Long> e = tit.next();
			if (now - e.getValue() > keep) {
				tit.remove();
				if (!deathNotices.isEmpty()) deathNotices.remove(0);
			}
		}

		double maxW = 0;
		int totalLines = deathNotices.size() + list.size();
		for (String notice : deathNotices) {
			maxW = Math.max(maxW, renderer.textWidth(notice, shadow.get(), txtScale));
		}
		for (PlayerEntity p : list) {
			double w = renderer.textWidth(p.getName().getString(), shadow.get(), txtScale);
			if (showDistance.get()) w += renderer.textWidth("[" + String.format(distFmt, MeteorClient.mc.player.distanceTo(p)) + "]", shadow.get(), txtScale);
			if (showHealth.get()) w += renderer.textWidth(" ❤" + String.format("%.1f", p.getHealth() + p.getAbsorptionAmount()), shadow.get(), txtScale);
			maxW = Math.max(maxW, w);
		}
		setSize(maxW, totalLines * lineH);

		if (background.get()) renderer.quad(x, y, getWidth(), getHeight(), backgroundColor.get());

		double yOff = 0;
		for (String notice : deathNotices) {
			renderer.text(notice, x, y + yOff, killedColor.get(), shadow.get(), txtScale);
			yOff += lineH;
		}

		for (PlayerEntity p : list) {
			double xOff = 0;
			String name = p.getName().getString();
			renderer.text(name, x + xOff, y + yOff, nameColor.get(), shadow.get(), txtScale);
			xOff += renderer.textWidth(name, shadow.get(), txtScale);
			if (showDistance.get()) {
				String ds = "[" + String.format(distFmt, MeteorClient.mc.player.distanceTo(p)) + "]";
				renderer.text(ds, x + xOff, y + yOff, distanceColor.get(), shadow.get(), txtScale);
				xOff += renderer.textWidth(ds, shadow.get(), txtScale);
			}
			if (showHealth.get()) {
				double hp = p.getHealth() + p.getAbsorptionAmount();
				String hpStr = String.format("%.1f", hp);
				SettingColor hpC = hp >= 18 ? hpHighColor.get() : (hp >= 12 ? hpMidColor.get() : hpLowColor.get());
				renderer.text(" ❤", x + xOff, y + yOff, heartColor.get(), shadow.get(), txtScale);
				xOff += renderer.textWidth(" ❤", shadow.get(), txtScale);
				renderer.text(hpStr, x + xOff, y + yOff, hpC, shadow.get(), txtScale);
			}
			yOff += lineH;
		}
	}
}
