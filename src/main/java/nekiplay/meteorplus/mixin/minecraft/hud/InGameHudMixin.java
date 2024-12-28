package nekiplay.meteorplus.mixin.minecraft.hud;

import meteordevelopment.meteorclient.MeteorClient;
import nekiplay.main.events.hud.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	@Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
	private void onRenderHealthBar(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
		RenderHealthBarEvent healthBarRenderEvent = RenderHealthBarEvent.get(context, player, x, y, lines, regeneratingHeartIndex, maxHealth, lastHealth, health, absorption, blinking);
		MeteorClient.EVENT_BUS.post(healthBarRenderEvent);
		if (healthBarRenderEvent.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
	private void onRenderExperienceBar(DrawContext context, int x, CallbackInfo ci) {
		RenderExperienceBarEvent renderExperienceBarEvent = RenderExperienceBarEvent.get(context, x);
		MeteorClient.EVENT_BUS.post(renderExperienceBarEvent);
		if (renderExperienceBarEvent.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "renderExperienceLevel", at = @At("HEAD"), cancellable = true)
	private void onRenderExperienceBar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		RenderExperienceLevelEvent renderExperienceLevelEvent = RenderExperienceLevelEvent.get(context, tickCounter);
		MeteorClient.EVENT_BUS.post(renderExperienceLevelEvent);
		if (renderExperienceLevelEvent.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
	private void onRenderFoodBar(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci) {
		RenderFoodBarEvent renderFoodBarEvent = RenderFoodBarEvent.get(context, player, top, right);
		MeteorClient.EVENT_BUS.post(renderFoodBarEvent);
		if (renderFoodBarEvent.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
	private static void onRenderArmor(DrawContext context, PlayerEntity player, int i, int j, int k, int x, CallbackInfo ci) {
		RenderArmorBarEvent renderArmorBarEvent = RenderArmorBarEvent.get(context, player, i, j, k, x);
		MeteorClient.EVENT_BUS.post(renderArmorBarEvent);
		if (renderArmorBarEvent.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "renderMountHealth", at = @At("HEAD"), cancellable = true)
	private void onRenderMountHealth(DrawContext context, CallbackInfo ci) {
		RenderMountHealthBarEvent renderMountHealthBarEvent = RenderMountHealthBarEvent.get(context);
		MeteorClient.EVENT_BUS.post(renderMountHealthBarEvent);
		if (renderMountHealthBarEvent.isCancelled()) {
			ci.cancel();
		}
	}

}
