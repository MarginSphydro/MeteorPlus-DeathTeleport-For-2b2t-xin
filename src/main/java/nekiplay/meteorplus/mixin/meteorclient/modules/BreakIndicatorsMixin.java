package nekiplay.meteorplus.mixin.meteorclient.modules;

import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.mixin.ClientPlayerInteractionManagerAccessor;
import meteordevelopment.meteorclient.mixin.WorldRendererAccessor;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BreakIndicators;
import meteordevelopment.meteorclient.systems.modules.world.PacketMine;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.MeteorPlusAddon;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.BlockBreakingInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Map;

import static nekiplay.meteorplus.features.modules.world.timer.TimerPlus.find_percent;

@Mixin(BreakIndicators.class)
public class BreakIndicatorsMixin extends Module  {

	@Unique
	private final SettingGroup sgPercentageRenderPlus = settings.createGroup(MeteorPlusAddon.HUD_TITLE + " Percentage Render");

	@Unique
	public final Setting<Boolean> percentageRender = sgPercentageRenderPlus.add(new BoolSetting.Builder()
		.name("enable-percentage-render")
		.description("Whether or not to render blocks being packet mined.")
		.defaultValue(true)
		.build()
	);

	@Unique
	public final Setting<Boolean> packetMine = sgPercentageRenderPlus.add(new BoolSetting.Builder()
		.name("packet-mine")
		.description("Render packet mine blocks.")
		.defaultValue(true)
		.visible(percentageRender::get)
		.build()
	);

	@Unique
	private final Setting<SettingColor> percentageColor = sgPercentageRenderPlus.add(new ColorSetting.Builder()
		.name("percentage-color")
		.description("The color for the breaking block.")
		.defaultValue(new SettingColor(25, 252, 25, 150))
		.visible(percentageRender::get)
		.build()
	);


	public BreakIndicatorsMixin(Category category, String name, String description, String... aliases) {
		super(category, name, description, aliases);
	}

	@Unique
	@EventHandler
	private void on2DRender(Render2DEvent event) {

		if (packetMine.get() && !Modules.get().get(PacketMine.class).blocks.isEmpty()) {
			renderPacket(event, Modules.get().get(PacketMine.class).blocks);
		}

		Map<Integer, BlockBreakingInfo> blocks = ((WorldRendererAccessor) mc.worldRenderer).getBlockBreakingInfos();

		float ownBreakingStage = ((ClientPlayerInteractionManagerAccessor) mc.interactionManager).getBreakingProgress();
		BlockPos ownBreakingPos = ((ClientPlayerInteractionManagerAccessor) mc.interactionManager).getCurrentBreakingBlockPos();


		if (ownBreakingPos != null && ownBreakingStage > 0) {

			double shrinkFactor = 1d - ownBreakingStage;


			BlockState state = mc.world.getBlockState(ownBreakingPos);
			VoxelShape shape = state.getOutlineShape(mc.world, ownBreakingPos);

			renderBlock(event, ownBreakingPos, shrinkFactor);

		}

		blocks.values().forEach(info -> {
			BlockPos pos = info.getPos();
			int stage = info.getStage();
			if (pos.equals(ownBreakingPos)) return;

			BlockState state = mc.world.getBlockState(pos);
			VoxelShape shape = state.getOutlineShape(mc.world, pos);
			if (shape == null || shape.isEmpty()) return;

			Box orig = shape.getBoundingBox();

			double shrinkFactor = (9 - (stage + 1)) / 9d;
			double progress = 1d - shrinkFactor;

			renderBlock(event, pos, shrinkFactor);
		});
	}

	@Unique
	private void renderBlock(Render2DEvent event, BlockPos pos, double shrinkFactor) {
		Vector3d vector3d = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		if (percentageRender.get()) {
			if (NametagUtils.to2D(vector3d, 1, true)) {
				TextRenderer text = TextRenderer.get();
				NametagUtils.begin(vector3d, event.drawContext);
				text.beginBig();
				String label = String.format("%1$,.0f", shrinkFactor * 100) + "%";

				double hologramWidth = text.getWidth(label, true);
				double heightDown = text.getHeight(true);

				double widthHalf = hologramWidth / 2;


				double hX = -widthHalf;
				double hY = -heightDown;

				text.render(label, hX, hY, percentageColor.get(), true);

				text.end();
				NametagUtils.end(event.drawContext);
			}
		}
	}

	@Unique
	private void renderPacket(Render2DEvent event, List<PacketMine.MyBlock> blocks) {
		for (PacketMine.MyBlock block : blocks) {
			if (block.mining && block.progress != Double.POSITIVE_INFINITY) {
				VoxelShape shape = block.blockState.getOutlineShape(mc.world, block.blockPos);
				if (shape == null || shape.isEmpty()) return;

				Box orig = shape.getBoundingBox();

				double progressNormalised = block.progress > 1 ? 1 : block.progress;
				double shrinkFactor = 1d - progressNormalised;
				BlockPos pos = block.blockPos;

				renderBlock(event, pos, shrinkFactor);
			}
		}
	}
}
