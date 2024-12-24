package nekiplay.meteorplus.mixin.whereisit;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.systems.modules.Modules;
import nekiplay.meteorplus.features.modules.integrations.WhereIsIt;
import nekiplay.meteorplus.mixinclasses.ScheduledLabel;
import nekiplay.meteorplus.utils.ColorRemover;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import red.jackf.whereisit.client.render.Rendering;
import red.jackf.whereisit.config.WhereIsItConfig;

@Mixin(Rendering.class)
public class RenderingMixin {
	@Unique
	private static WhereIsIt whereIsIt;
	@ModifyArgs(method = "renderLabel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"))
	private static void changeColor(Args args) {
		if (whereIsIt == null) {
			whereIsIt = Modules.get().get(WhereIsIt.class);
		}

		if (whereIsIt != null && whereIsIt.isActive()) {

			Text text1 = args.get(0);
			String text2 = text1.getString();
			if (whereIsIt.suport_color_symbols.get()) {
				String text3 = ColorRemover.GetVerbatim(text2);
				args.set(0, Text.of(text3));
				args.set(3, getColor(text2));
			}
			else {
				args.set(3, whereIsIt.text_color.get().getPacked());
			}
		}
	}

	@ModifyArgs(method = "renderLabel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V"))
	private static void translatePosition(Args args) {
		if (whereIsIt == null) {
			whereIsIt = Modules.get().get(WhereIsIt.class);
		}

		if (whereIsIt != null && whereIsIt.isActive()) {

			args.set(1, (double)args.get(1) + whereIsIt.y_offset.get());
		}
	}

	@Unique
	private static int getColor(String text) {
		if (text.length() >= 2) {
			char first_char = text.charAt(0);
			char color_char = text.charAt(1);

			if (first_char == '&' || first_char == '§') {
				switch (color_char) {
					case '4' -> {
						return 11141120;
					}
					case 'c' -> {
						return 16733525;
					}
					case '6' -> {
						return 16755200;
					}
					case 'e' -> {
						return 16777045;
					}
					case '2' -> {
						return 43520;
					}
					case 'a' -> {
						return 5635925;
					}
					case 'b' -> {
						return 5636095;
					}
					case '3' -> {
						return 43690;
					}
					case '1' -> {
						return 170;
					}
					case '9' -> {
						return 5592575;
					}
					case 'd' -> {
						return 16733695;
					}
					case '5' -> {
						return 11141290;
					}
					case 'f' -> {
						return 16777215;
					}
					case '7' -> {
						return 11184810;
					}
					case '8' -> {
						return 5592405;
					}
					case '0' -> {
						return 0;
					}
					default -> {
						return 0xffffff;
					}
				}
			}
		}
		return 0xffffff;
	}
}
