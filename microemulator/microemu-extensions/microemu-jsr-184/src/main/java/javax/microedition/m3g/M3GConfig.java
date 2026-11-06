/*
 * Replacement for freej2me's org.recompile.mobile.Mobile class, covering only
 * what the M3G (JSR-184) implementation needs. Defaults match freej2me-plus.
 *
 * Copied M3G sources are from FreeJ2ME (GPL-3.0).
 */
package javax.microedition.m3g;

import java.io.InputStream;

import org.microemu.log.Logger;

final class M3GConfig
{
	private M3GConfig() {}

	public static final byte LOG_NONE    = 0;
	public static final byte LOG_DEBUG   = 1;
	public static final byte LOG_INFO    = 2;
	public static final byte LOG_WARNING = 3;
	public static final byte LOG_ERROR   = 4;
	public static final byte LOG_FATAL   = 5;

	public static boolean halfResM3GRaster = false;
	public static int m3gAntiAliasingMode = 1;
	public static int m3gBilinearFilterMode = 1;
	public static int m3gDitheringMode = 1;
	public static int m3gPerspectiveCorrectionMode = 1;
	public static int m3gPerspCorrSubFactor = 7;
	public static boolean m3gDisableFog = false;
	public static int m3gMipmapMode = 1;

	public static boolean M3GRenderUntexturedPolygons = false;
	public static boolean M3GRenderWireframe = false;

	static void log(byte level, String message)
	{
		switch (level)
		{
			case LOG_DEBUG:   Logger.debug(message); break;
			case LOG_INFO:    Logger.info(message); break;
			case LOG_WARNING: Logger.warn(message); break;
			case LOG_ERROR:
			case LOG_FATAL:   Logger.error(message); break;
			default: break;
		}
	}

	/* TODO: confirm microemu's MIDlet classloader is the context loader here. */
	static InputStream getMIDletResourceAsStream(String name)
	{
		String n = name.startsWith("/") ? name.substring(1) : name;
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(n);
	}
}
