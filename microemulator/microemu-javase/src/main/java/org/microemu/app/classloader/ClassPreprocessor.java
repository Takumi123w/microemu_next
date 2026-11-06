package org.microemu.app.classloader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ClassPreprocessor {
    public static byte[] instrument(InputStream is, Object config) { return toByteArray(is); }
    public static byte[] instrument(InputStream is) { return toByteArray(is); }
    public static byte[] instrument(String name, InputStream is) { return toByteArray(is); }
    public static byte[] instrument(InputStream is, ClassLoader cl) { return toByteArray(is); }
    public static byte[] instrument(String name, InputStream is, ClassLoader cl) { return toByteArray(is); }
    public static byte[] instrument(String name, byte[] b) { return b; }
    public static byte[] instrument(String name, byte[] b, ClassLoader cl) { return b; }
    public static byte[] instrument(String name, byte[] b, Object config) { return b; }

    private static byte[] toByteArray(InputStream is) {
        if (is == null) return null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }
}
