package nekiplay.meteorplus.features.modules.render;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.Renderer3D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class GodHoleESP extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final SettingGroup sgRender  = settings.createGroup("Render");

	private final Setting<Integer> horizontalRadius = sgGeneral.add(new IntSetting.Builder()
		.name("horizontal-radius")
		.description("Horizontal search radius for God Holes.")
		.defaultValue(8)
		.min(0)
		.sliderMax(32)
		.build()
	);

	private final Setting<Integer> verticalRadius = sgGeneral.add(new IntSetting.Builder()
		.name("vertical-radius")
		.description("Vertical search radius for God Holes.")
		.defaultValue(4)
		.min(0)
		.sliderMax(16)
		.build()
	);

	private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
		.name("shape-mode")
		.description("How the shapes are rendered.")
		.defaultValue(ShapeMode.Both)
		.build()
	);

	private final Setting<Double> boxHeight = sgRender.add(new DoubleSetting.Builder()
		.name("height")
		.description("Height of the rendering box.")
		.defaultValue(2.0)
		.min(0)
		.build()
	);

	private final Setting<SettingColor> color = sgRender.add(new ColorSetting.Builder()
		.name("color")
		.description("Color of the God Hole highlight.")
		.defaultValue(new SettingColor(0, 255, 255, 150))
		.build()
	);

	private final Pool<Pos> pool = new Pool<>(Pos::new);
	private final List<Pos> holes = new ArrayList<>();

	public GodHoleESP() {
		super(Categories.Render, "god-hole-esp", "Highlights 2-block air pockets surrounded by bedrock above and around.");
	}

	@EventHandler
	private void onTick(TickEvent.Pre event) {
		holes.forEach(pool::free);
		holes.clear();

		BlockPos playerPos = mc.player.getBlockPos();

		for (int dx = -horizontalRadius.get(); dx <= horizontalRadius.get(); dx++) {
			for (int dy = -verticalRadius.get(); dy <= verticalRadius.get(); dy++) {
				for (int dz = -horizontalRadius.get(); dz <= horizontalRadius.get(); dz++) {
					BlockPos pos = playerPos.add(dx, dy, dz);
					if (!mc.world.isAir(pos) || !mc.world.isAir(pos.up())) continue;
					if (mc.world.getBlockState(pos.up(2)).getBlock() != Blocks.BEDROCK) continue;
					boolean surround = true;
					for (Direction dir : List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)) {
						if (mc.world.getBlockState(pos.offset(dir)).getBlock() != Blocks.BEDROCK) {
							surround = false;
							break;
						}
					}
					if (!surround) continue;
					holes.add(pool.get().set(pos));
				}
			}
		}
	}

	@EventHandler
	private void onRender(Render3DEvent event) {
		Renderer3D r = event.renderer;
		SettingColor sc = color.get();
		double h = boxHeight.get();
		int mask = 63;

		for (Pos p : holes) {
			BlockPos pos = p.pos;
			Box box = new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + h, pos.getZ() + 1);
			r.box(box, sc, sc, shapeMode.get(), mask);
		}
	}

	private static class Pos {
		private BlockPos pos;
		public Pos set(BlockPos p) {
			this.pos = p;
			return this;
		}
	}
}
