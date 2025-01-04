package nekiplay.meteorplus.mixin.meteorclient.modules;

import meteordevelopment.meteorclient.systems.waypoints.Waypoint;
import meteordevelopment.meteorclient.systems.waypoints.Waypoints;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Waypoints.class)
public interface WaypointsAccessor {
	@Mutable
	@Accessor("waypoints")
	List<Waypoint> getWaypoints();
}
