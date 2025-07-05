package nekiplay.meteorplus.features.modules.player;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.network.ClientPlayerEntity;

public class ExplosionPhase extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Double> insideDepth = sgGeneral.add(new DoubleSetting.Builder()
		.name("inside-depth")
		.description("进入方块内部的深度 (0.0 - 0.9)，调整直到只入部分身体或更多。")
		.defaultValue(0.5)
		.min(0.0)
		.max(0.9)
		.sliderMin(0.0)
		.sliderMax(0.9)
		.build()
	);

	private final Setting<Integer> packetRepeats = sgGeneral.add(new IntSetting.Builder()
		.name("packet-repeats")
		.description("发送伪造位置包次数，确保同步。")
		.defaultValue(4)
		.min(1)
		.max(10)
		.sliderMin(1)
		.sliderMax(10)
		.build()
	);

	private final Setting<Boolean> cancelVel = sgGeneral.add(new BoolSetting.Builder()
		.name("cancel-velocity")
		.description("在相位前后拦截爆炸击退包，减少被震出方块。")
		.defaultValue(true)
		.build()
	);

	private boolean triggered;
	private BlockPos targetBlock;

	public ExplosionPhase() {
		super(Categories.Player, "Explosion-Phase", "在爆炸瞬间开启无碰撞相位，将身体部分或全部卡进方块以削减伤害。");
	}

	@EventHandler
	private void onExplosionPacket(PacketEvent.Receive event) {
		if (!(event.packet instanceof ExplosionS2CPacket)) return;
		// 选择当前脚下方块作为目标
		Vec3d ppos = mc.player.getPos();
		int bx = (int) Math.floor(ppos.x);
		int by = (int) Math.floor(ppos.y) - 1;
		int bz = (int) Math.floor(ppos.z);
		targetBlock = new BlockPos(bx, by, bz);
		triggered = true;
		event.cancel();
	}

	@EventHandler
	private void onTick(TickEvent.Post event) {
		if (!triggered || targetBlock == null) return;
		triggered = false;

		ClientPlayerEntity p = mc.player;
		// 启用无碰撞
		p.noClip = true;

		double x = targetBlock.getX() + 0.5;
		double y = targetBlock.getY() + insideDepth.get();
		double z = targetBlock.getZ() + 0.5;

		// 多次发送位置包，卡入方块
		for (int i = 0; i < packetRepeats.get(); i++) {
			mc.getNetworkHandler().sendPacket(
				new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true)
			);
		}

		// 拦截击退包（本地清除速度）
		if (cancelVel.get()) {
			mc.player.setVelocity(0, 0, 0);
		}

		// 恢复碰撞
		p.noClip = false;
	}
}
