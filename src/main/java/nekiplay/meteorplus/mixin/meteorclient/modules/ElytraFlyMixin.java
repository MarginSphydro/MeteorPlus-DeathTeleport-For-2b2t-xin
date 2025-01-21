package nekiplay.meteorplus.mixin.meteorclient.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFlightMode;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFly;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ElytraFly.class, remap = false)
public class ElytraFlyMixin {
	@Shadow
	private ElytraFlightMode currentMode;

	@Inject(method = "onPacketReceive", at = @At("HEAD"))
	private void onPacketReceive(PacketEvent.Receive event, CallbackInfo ci) {
		if (event.packet instanceof PlayerPositionLookS2CPacket) {
			currentMode.zeroAcceleration();
		}

	}
}
