package nekiplay.meteorplus.features.modules.player;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.util.math.Vec3d;

public class InfiniteDeathRespawn extends Module {
	private Vec3d deathPos;

	public InfiniteDeathRespawn() {
		super(Categories.Player, "infinite-death-respawn", "Nerver Lose BY mark_7601");
	}

	@EventHandler
	private void onTick(TickEvent.Post event) {
		if (mc.player != null && mc.player.getHealth() <= 0 && deathPos == null) {
			deathPos = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());
		}
	}

	@EventHandler
	private void onReceivePacket(PacketEvent.Receive event) {
		if (deathPos != null && event.packet instanceof PlayerPositionLookS2CPacket packet) {
			event.cancel();

			mc.player.networkHandler.sendPacket(
				new TeleportConfirmC2SPacket(packet.getTeleportId())
			);

			mc.player.setPosition(deathPos.x, deathPos.y, deathPos.z);
		}

		if (event.packet instanceof DeathMessageS2CPacket) {
			event.cancel();
		}
	}

	@Override
	public void onDeactivate() {
		deathPos = null;
	}

	// 新增getter方法
	public Vec3d getDeathPos() {
		return deathPos;
	}
}
