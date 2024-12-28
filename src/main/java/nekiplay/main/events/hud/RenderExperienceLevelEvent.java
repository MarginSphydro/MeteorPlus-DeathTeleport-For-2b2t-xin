package nekiplay.main.events.hud;

import nekiplay.main.events.Cancellable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;

public class RenderExperienceLevelEvent extends Cancellable {

	private static final RenderExperienceLevelEvent INSTANCE = new RenderExperienceLevelEvent();


	private DrawContext context;
	private RenderTickCounter tickCounter;

	public static RenderExperienceLevelEvent get(DrawContext context, RenderTickCounter tickCounter) {
		INSTANCE.context = context;
		INSTANCE.tickCounter = tickCounter;
		return INSTANCE;
	}
}
