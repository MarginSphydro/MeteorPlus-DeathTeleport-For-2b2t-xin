package nekiplay.main.events.hud;

import nekiplay.main.events.Cancellable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;

public class RenderHealthBarEvent extends Cancellable {

	private static final RenderHealthBarEvent INSTANCE = new RenderHealthBarEvent();


	private DrawContext context;
	private PlayerEntity player;
	private int x;
	private int y;
	private int lines;
	private int regeneratingHeartIndex;
	private float maxHealth;
	private int lastHealth;
	private int health;
	private int absorption;
	private boolean blinking;

	public static RenderHealthBarEvent get(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking) {
		INSTANCE.context = context;
		INSTANCE.player = player;
		INSTANCE.x = x;
		INSTANCE.y = y;
		INSTANCE.lines = lines;
		INSTANCE.regeneratingHeartIndex = regeneratingHeartIndex;
		INSTANCE.maxHealth = maxHealth;
		INSTANCE.lastHealth = lastHealth;
		INSTANCE.health = health;
		INSTANCE.absorption = absorption;
		INSTANCE.blinking = blinking;
		return INSTANCE;
	}
}
