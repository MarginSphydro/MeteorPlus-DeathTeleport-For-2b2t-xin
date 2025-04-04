package nekiplay.meteorplus.mixin.meteorclient.modules;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import meteordevelopment.meteorclient.events.Cancellable;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Blink;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.utils.RaycastUtils;
import net.minecraft.block.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.misc.input.Input.isPressed;
import static nekiplay.meteorplus.MeteorPlusAddon.HUD_TITLE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

@Mixin(Freecam.class)
public class FreecamMixin {
	@Unique
	private final Freecam freecam = (Freecam) (Object) this;
	@Unique
	private final SettingGroup freecamMeteorPlusSetting = freecam.settings.createGroup(HUD_TITLE);
	@Unique
	private final Setting<Boolean> moveBaritoneControl = freecamMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("baritone-move-control")
		.description("click bind to set the destination on the selected block. Right mouse click to cancel.")
		.build()
	);

	@Unique
	private final Setting<Keybind> baritoneMoveKey = freecamMeteorPlusSetting.add(new KeybindSetting.Builder()
		.name("baritone-move-keybind")
		.description("The bind for move.")
		.visible(moveBaritoneControl::get)
		.defaultValue(Keybind.fromKey(GLFW_MOUSE_BUTTON_LEFT))
		.build()
	);

	@Unique
	private final Setting<Boolean> blinkBaritoneControl = freecamMeteorPlusSetting.add(new BoolSetting.Builder()
		.name("baritone-blink-move-control")
		.description("Click bind to move to point in blink.")
		.build()
	);

	@Unique
	private final Setting<Keybind> baritoneMoveBlinkKey = freecamMeteorPlusSetting.add(new KeybindSetting.Builder()
		.name("baritone-blink-move-keybind")
		.description("The bind for move in blink.")
		.visible(blinkBaritoneControl::get)
		.build()
	);

	@Unique
	private final Setting<Keybind> baritoneStopKey = freecamMeteorPlusSetting.add(new KeybindSetting.Builder()
		.name("baritone-stop-keybind")
		.description("The bind for stop baritone actions.")
		.visible(() -> blinkBaritoneControl.get() || moveBaritoneControl.get())
		.defaultValue(Keybind.fromKey(GLFW_MOUSE_BUTTON_RIGHT))
		.build()
	);

	@Unique
	private final Blink blink = Modules.get().get(Blink.class);
	@Unique
	private boolean isBlinkMoving = false;

	@Unique
	private BlockPos tryGetValidPos(BlockPos pos) {
        assert mc.world != null;
        BlockState state = mc.world.getBlockState(pos);
		Block block = state.getBlock();
		if (block == Blocks.FERN ||
			block == Blocks.SHORT_GRASS ||
			block == Blocks.TALL_GRASS ||
			block == Blocks.GLOW_LICHEN ||
			block == Blocks.DEAD_BUSH ||
			block == Blocks.SNOW ||
			block == Blocks.MOSS_CARPET ||
			// Torchs
			block instanceof TorchBlock ||
			block instanceof WallTorchBlock ||
			// Signs
			block instanceof WallSignBlock ||
			block instanceof SignBlock ||
			// Mushroms
			block instanceof MushroomPlantBlock ||
			// Small flowers
			block instanceof FlowerBlock ||
			// Crops
			block instanceof CropBlock ||
			// Saplings
			block instanceof SaplingBlock ||
			// Rails
			block instanceof RailBlock ||
			// Carpets
			block instanceof CarpetBlock

		) {
			return pos;
		}
		else {
			return pos.up();
		}
	}

	@Unique @Nullable
	private BlockPos rayCastClicked() {
		BlockPos blockPos = null;
		Vec3d rotationVector = RaycastUtils.getRotationVector((float) freecam.getPitch(mc.getRenderTickCounter().getTickDelta(true)), (float) freecam.getYaw(mc.getRenderTickCounter().getTickDelta(true)));
		Vec3d pos = new Vec3d(freecam.pos.x, freecam.pos.y, freecam.pos.z);
		HitResult result = RaycastUtils.raycast(pos, rotationVector, 64 * 4, true);
		if (result.getType() == HitResult.Type.BLOCK) {
			BlockHitResult blockHitResult = (BlockHitResult) result;
			blockPos = blockHitResult.getBlockPos();
		}
		return blockPos;
	}

	@Unique
	private void Work(Cancellable event) {
		if (baritoneMoveBlinkKey.get().isPressed() && mc.currentScreen == null) {
			BlockPos clicked = rayCastClicked();

			if (blinkBaritoneControl.get()) {

				if (clicked == null) return;

				if (mc.world == null) return;

				BlockState state = mc.world.getBlockState(clicked);

				if (state.isAir()) return;
				isBlinkMoving = true;
				GoalBlock goal = new GoalBlock(tryGetValidPos(clicked));
				BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("stop");
				BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("goto " + goal.x + " " + goal.y + " " + goal.z);

				event.cancel();
			}
		}
		if (baritoneMoveKey.get().isPressed() && mc.currentScreen == null) {
			BlockPos clicked = rayCastClicked();
			if (clicked == null) return;

			if (mc.world == null) return;

			BlockState state = mc.world.getBlockState(clicked);

			if (state.isAir()) return;

			if (BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing())
				BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().forceCancel();


			GoalBlock goal = new GoalBlock(tryGetValidPos(clicked));
			BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("stop");
			BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("goto " + goal.x + " " + goal.y + " " + goal.z);

			event.cancel();
		}

		if (baritoneStopKey.get().isPressed() && mc.currentScreen == null) {
			BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().forceCancel();
			if (blink != null) {
				if (blink.isActive()) {
					blink.toggle();
					isBlinkMoving = false;
				}
			}
			event.cancel();
		}
	}

	@Unique
	@EventHandler
	private void onKeyEvent(KeyEvent event)
	{
		if (mc.world != null && event.action == KeyAction.Press) {
			Work(event);
		}
	}
	@Unique
	@EventHandler
	private void onMouseButtonEvent(MouseButtonEvent event) {
		if (mc.world != null && event.action == KeyAction.Press) {
			Work(event);
		}
	}

	@Unique
	@EventHandler
	private void onTickEvent(TickEvent.Pre event) {
		if (mc.world != null && blinkBaritoneControl.get() && blink != null) {
			if (isBlinkMoving && (BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().hasPath() || BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing() ) ) {
				if (!blink.isActive()) {
					blink.toggle();
				}
			}
			if (isBlinkMoving && (!BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().hasPath() || !BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing() ) ) {
				if (blink.isActive()) {
					blink.toggle();
					isBlinkMoving = false;
				}
			}
		}
	}
}
