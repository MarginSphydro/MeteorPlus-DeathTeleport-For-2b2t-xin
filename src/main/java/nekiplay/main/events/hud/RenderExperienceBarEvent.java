package nekiplay.main.events.hud;

import nekiplay.main.events.Cancellable;
import net.minecraft.client.gui.DrawContext;

public class RenderExperienceBarEvent extends Cancellable {

	private static final RenderExperienceBarEvent INSTANCE = new RenderExperienceBarEvent();


	private DrawContext context;
	private int x;

	public static RenderExperienceBarEvent get(DrawContext context, int x) {
		INSTANCE.context = context;
		INSTANCE.x = x;
		return INSTANCE;
	}
}
