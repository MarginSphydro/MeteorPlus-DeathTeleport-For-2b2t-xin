package nekiplay.meteorplus.mixinclasses;

import meteordevelopment.meteorclient.systems.waypoints.Waypoint;

import java.util.Comparator;
import java.util.Map;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class WaypointsModuleModes {
	public enum SortMode {
		None,
		Distance,
		Name,
	}

	public static class DistanceComparator implements Comparator<Waypoint> {
		Map<String, Waypoint> base;
		@Override
		public int compare(Waypoint waypoint, Waypoint t1) {
			long distance1 = 0;
			long distance2 = 0;
			if (mc.player != null) {
				distance1 = Math.round(mc.player.getPos().distanceTo(waypoint.getPos().toCenterPos()));
				distance2 = Math.round(mc.player.getPos().distanceTo(t1.getPos().toCenterPos()));
			}


			if (distance1 >= distance2) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	public static class NameComparator implements Comparator<Waypoint> {
		@Override
		public int compare(Waypoint waypoint, Waypoint t1) {
			if (waypoint.name.get().length() >= t1.name.get().length()) {
				return 1;
			} else {
				return -1;
			}
		}
	}
}
