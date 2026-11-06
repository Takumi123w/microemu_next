/*
 *  MicroEmulator
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 */
package org.microemu.device;

/**
 * Optional capability of a javax.microedition.lcdui.Graphics whose pixels can be
 * written directly. Used by 3D extensions (JSR-184) that rasterize in software.
 *
 * Pixels are packed ARGB ints (0xAARRGGBB), row-major, width * height entries.
 * Writes become visible on the next repaint of the display.
 */
public interface RasterTarget {

	int[] getRasterData();

	int getRasterWidth();

	int getRasterHeight();

}
