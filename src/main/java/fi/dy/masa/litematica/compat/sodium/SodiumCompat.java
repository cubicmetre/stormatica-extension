package fi.dy.masa.litematica.compat.sodium;

import net.fabricmc.loader.api.FabricLoader;

public class SodiumCompat
{
	public static final String SODIUM = "sodium";
    private static boolean hasSodium;
	private static String version = "";

    public static void checkForSodium()
    {
        hasSodium = FabricLoader.getInstance().isModLoaded(SODIUM);

		if (FabricLoader.getInstance().getModContainer(SODIUM).isPresent())
		{
			version = FabricLoader.getInstance().getModContainer(SODIUM).get().getMetadata().getVersion().getFriendlyString();
		}
    }

    public static boolean hasSodium() { return hasSodium; }

	public static String getVersion() { return version; }

    static
    {
        checkForSodium();
    }
}
