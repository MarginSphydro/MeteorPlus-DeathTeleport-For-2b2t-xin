package nekiplay.meteorplus.features.modules.player;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.MinecraftClient;

public class AntiExplosion extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Integer> scanRange = sgGeneral.add(new IntSetting.Builder()
		.name("scan-range")
		.description("水平扫描范围 (格)。")
		.defaultValue(1)
		.min(1)
		.max(5)
		.sliderMin(1)
		.sliderMax(5)
		.build()
	);

	private final Setting<Double> insideOffset = sgGeneral.add(new DoubleSetting.Builder()
		.name("inside-offset")
		.description("站入方块时的腿部深度 (0.0-1.8)。较小值只入腿部，较大值可入体侧。")
		.defaultValue(0.1)
		.min(0.0)
		.max(1.8)
		.sliderMin(0.0)
		.sliderMax(1.8)
		.build()
	);

	private final Setting<Integer> packetCount = sgGeneral.add(new IntSetting.Builder()
		.name("packet-count")
		.description("位置包数量。")
		.defaultValue(5)
		.min(1)
		.max(10)
		.sliderMin(1)
		.sliderMax(10)
		.build()
	);

	private final Setting<Boolean> cancelVelocity = sgGeneral.add(new BoolSetting.Builder()
		.name("cancel-velocity")
		.description("拦截击退包。")
		.defaultValue(true)
		.build()
	);

	private BlockPos targetPos;
	private boolean shouldTeleport;

	public AntiExplosion() {
		super(Categories.Player, "Anti-Explosion", "爆炸时phase进入单格方块下半身，以最大化遮挡。支持多层扫描。示例: inside-offset=0.1。使用者可根据测试调整。");
	}

	@EventHandler
	private void onExplosionPacket(PacketEvent.Receive event) {
		if (!(event.packet instanceof ExplosionS2CPacket)) return;

		Vec3d pos = mc.player.getPos();
		BlockPos base = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
		MinecraftClient client = mc;
		BlockPos found = null;
		double bestDist = Double.MAX_VALUE;
		int range = scanRange.get();

		// Scanning dy offsets 0 (feet),1 (waist),2 (head) for block
		for (int dy = 0; dy <= 2; dy++) {
			for (int dx = -range; dx <= range; dx++) {
				for (int dz = -range; dz <= range; dz++) {
					BlockPos check = base.add(dx, dy, dz);
					if (client.world == null) continue;
					// 实心方块即可卡入
					if (client.world.getBlockState(check).isAir()) continue;
					// 上方必须为空气
					if (!client.world.getBlockState(check.up()).isAir()) continue;

					double dist = client.player.squaredDistanceTo(check.getX() + 0.5, client.player.getY(), check.getZ() + 0.5);
					if (dist < bestDist) {
						bestDist = dist;
						found = check;
					}
				}
			}
			if (found != null) break; // 优先使用最低 dy
		}

		if (found != null) {
			targetPos = found;
			shouldTeleport = true;
		}
		event.cancel();
	}

	@EventHandler
	private void onTick(TickEvent.Post event) {
		if (!shouldTeleport || targetPos == null) return;
		shouldTeleport = false;

		double x = targetPos.getX() + 0.5;
		double y = targetPos.getY() + insideOffset.get();
		double z = targetPos.getZ() + 0.5;

		for (int i = 0; i < packetCount.get(); i++) {
			mc.getNetworkHandler().sendPacket(
				new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true)
			);
		}
	}

	@EventHandler
	private void onVelocityPacket(PacketEvent.Receive event) {
		if (cancelVelocity.get() && event.packet instanceof EntityVelocityUpdateS2CPacket) {
			event.cancel();
		}
	}
}
