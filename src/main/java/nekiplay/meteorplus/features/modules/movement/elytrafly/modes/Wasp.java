package nekiplay.meteorplus.features.modules.movement.elytrafly.modes;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import nekiplay.meteorplus.features.modules.movement.elytrafly.ElytraFlyMode;
import nekiplay.meteorplus.features.modules.movement.elytrafly.ElytraFlyModes;

public class Wasp extends ElytraFlyMode {
	public Wasp() {
		super(ElytraFlyModes.Wasp);
	}

	private boolean moving;
	private float yaw;
	private float pitch;

	@Override
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!mc.player.isGliding()) {return;}

		updateWaspMovement();
		pitch = mc.player.getPitch();

		double cos = Math.cos(Math.toRadians(yaw + 90));
		double sin = Math.sin(Math.toRadians(yaw + 90));

		double x = moving ? cos * elytraFly.horizontal_wasp.get() : 0;
		double y = -elytraFly.fallSpeed_wasp.get();
		double z = moving ? sin * elytraFly.horizontal_wasp.get() : 0;

		if (elytraFly.smartFall_wasp.get()) {
			y *= Math.abs(Math.sin(Math.toRadians(pitch)));
		}

		if (mc.options.sneakKey.isPressed() && !mc.options.jumpKey.isPressed()) {
			y = -elytraFly.down_wasp.get();
		}
		if (!mc.options.sneakKey.isPressed() && mc.options.jumpKey.isPressed()) {
			y = elytraFly.up_wasp.get();
		}

		((IVec3d) event.movement).meteor$set(x, y, z);

		if (elytraFly.resetSpeed.get()) {
			mc.player.setVelocity(0, 0, 0);
		}
	}

	private void updateWaspMovement() {
		float yaw = mc.player.getYaw();

		float forward = mc.player.input.getMovementInput().x;
		float sideways = mc.player.input.getMovementInput().y;

		if (forward > 0) {
			moving = true;
			yaw += sideways > 0 ? -45 : sideways < 0 ? 45 : 0;
		} else if (forward < 0) {
			moving = true;
			yaw += sideways > 0 ? -135 : sideways < 0 ? 135 : 180;
		} else {
			moving = sideways != 0;
			yaw += sideways > 0 ? -90 : sideways < 0 ? 90 : 0;
		}

		this.yaw = yaw;
	}
}
