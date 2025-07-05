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

import java.util.ArrayList;
import java.util.List;

public class ExplosionSpoof extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Double> xOffset = sgGeneral.add(new DoubleSetting.Builder()
		.name("x-offset")
		.description("Horizontal spoof offset on X axis (east +, west -)")
		.defaultValue(0.0)
		.min(-5.0)
		.max(5.0)
		.sliderMin(-5.0)
		.sliderMax(5.0)
		.build()
	);

	private final Setting<Double> yOffset = sgGeneral.add(new DoubleSetting.Builder()
		.name("y-offset")
		.description("Vertical spoof offset on Y axis (+ up)")
		.defaultValue(1.0)
		.min(0.0)
		.max(5.0)
		.sliderMin(0.0)
		.sliderMax(5.0)
		.build()
	);

	private final Setting<Double> zOffset = sgGeneral.add(new DoubleSetting.Builder()
		.name("z-offset")
		.description("Horizontal spoof offset on Z axis (south +, north -)")
		.defaultValue(0.0)
		.min(-5.0)
		.max(5.0)
		.sliderMin(-5.0)
		.sliderMax(5.0)
		.build()
	);

	private final Setting<Integer> packetCount = sgGeneral.add(new IntSetting.Builder()
		.name("fake-packet-count")
		.description("Number of spoof packets to send when spoofing")
		.defaultValue(5)
		.min(1)
		.max(20)
		.sliderMin(1)
		.sliderMax(20)
		.build()
	);

	private final Setting<Boolean> cancelExplosion = sgGeneral.add(new BoolSetting.Builder()
		.name("cancel-explosion-packet")
		.description("Cancel the incoming explosion packet to prevent client knockback and effects")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> cancelVelocity = sgGeneral.add(new BoolSetting.Builder()
		.name("cancel-velocity-packet")
		.description("Cancel velocity update packets to avoid knockback from server")
		.defaultValue(true)
		.build()
	);

	private final List<PlayerMoveC2SPacket> buffer = new ArrayList<>();
	private boolean spoofing;

	public ExplosionSpoof() {
		super(Categories.Player, "Explosion-Spoof", "Bypass crystal explosion damage by spoofing player position via packet buffering.");
	}

	@EventHandler
	private void onSend(PacketEvent.Send event) {
		// Only buffer movement packets during spoofing window
		if (event.packet instanceof PlayerMoveC2SPacket && spoofing) {
			buffer.add((PlayerMoveC2SPacket) event.packet);
			event.cancel();
		}
	}

	@EventHandler
	private void onReceive(PacketEvent.Receive event) {
		if (event.packet instanceof ExplosionS2CPacket) {
			if (cancelExplosion.get()) event.cancel();
			spoofing = true;
		} else if (cancelVelocity.get() && event.packet instanceof EntityVelocityUpdateS2CPacket) {
			event.cancel();
		}
	}

	@EventHandler
	private void onTick(TickEvent.Post event) {
		if (!spoofing) return;
		spoofing = false;

		// Send spoof packets
		double x = mc.player.getX() + xOffset.get();
		double y = mc.player.getY() + yOffset.get();
		double z = mc.player.getZ() + zOffset.get();
		for (int i = 0; i < packetCount.get(); i++) {
			mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true));
		}

		// Send buffered packets safely
		List<PlayerMoveC2SPacket> toSend = new ArrayList<>(buffer);
		buffer.clear();
		for (PlayerMoveC2SPacket pkt : toSend) {
			mc.getNetworkHandler().sendPacket(pkt);
		}
	}
}
