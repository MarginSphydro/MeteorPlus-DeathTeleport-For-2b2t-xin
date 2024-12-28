package nekiplay.main.events.hud;

import nekiplay.main.events.Cancellable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;

public class RenderArmorBarEvent extends Cancellable {

	private static final RenderArmorBarEvent INSTANCE = new RenderArmorBarEvent();


	private DrawContext context;
	private PlayerEntity player;
	private int i;
	private int j;
	private int k;
	private int x;

	public static RenderArmorBarEvent get(DrawContext context, PlayerEntity player, int i, int j, int k, int x) {
		INSTANCE.context = context;
		INSTANCE.player = player;
		INSTANCE.i = i;
		INSTANCE.j = j;
		INSTANCE.k = k;
		INSTANCE.x = x;
		return INSTANCE;
	}
}
