package nekiplay.meteorplus.features.modules.world.zones;

public class ZoneData {
	public double x_start;
	public double y_start;
	public double z_start;

	public double x_end;
	public double y_end;
	public double z_end;

	public boolean allowBaritoneBreaking = false;
	public boolean allowBaritonePlacing = false;

	public boolean allowPlayerBreaking = false;
	public boolean allowPlayerPlacing = false;

	public String name;

	public boolean showTitle;
	public String title;

	public boolean showBoundingBox;

	public String world;
	public String dimension;
}
