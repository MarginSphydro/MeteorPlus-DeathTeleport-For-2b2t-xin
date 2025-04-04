package nekiplay.meteorplus.mixin.meteorclient.gui;

import meteordevelopment.meteorclient.utils.player.TitleScreenCredits;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Calendar;

@Mixin(TitleScreenCredits.class)
public class TitleScreenCreditsMixin {
	@ModifyArgs(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/MutableText;append(Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"))
	private static void modifyAddText(Args args) {
		Text text = args.get(0);
		if (text != null) {
			Calendar calendar = Calendar.getInstance();
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int month = calendar.get(Calendar.MONTH) + 1;

			if (day <= 5 && month == 4) {
				MutableText newText = Text.literal(text.getString().replaceAll("Meteor", "Motor"))
					.setStyle(text.getStyle());
				if (text instanceof MutableText mutableText) {
					newText = newText.styled(style -> {
						Style original = mutableText.getStyle();
						if (original.getHoverEvent() != null) {
							style = style.withHoverEvent(original.getHoverEvent());
						}
						if (original.getClickEvent() != null) {
							style = style.withClickEvent(original.getClickEvent());
						}
						if (original.getInsertion() != null) {
							style = style.withInsertion(original.getInsertion());
						}
						return style.withColor(original.getColor())
							.withBold(original.isBold())
							.withItalic(original.isItalic())
							.withStrikethrough(original.isStrikethrough())
							.withObfuscated(original.isObfuscated());
					});
				}

				args.set(0, newText);
			}
		}
	}
}
