package net.hyper_pigeon.image2map.renderer;

import net.hyper_pigeon.image2map.Image2Map;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Arrays;
import java.util.Objects;

public class MapRenderer {

    private static final double shadeCoeffs[] = { 0.71, 0.86, 1.0, 0.53 };
    private static final MapColor mapColors[];
    // Precomputed normalized (0-1) shaded RGB vector for every palette entry,
    // Index = mapColorIndex * shadeCoeffs.length + shadeIndex, matching the map colour byte.
    private static final double[][] shadedPalette;

    static {
        mapColors = getColors();
        shadedPalette = new double[mapColors.length * shadeCoeffs.length][];
        for (int k = 0; k < mapColors.length; k++) {
            int col = mapColors[k].col;
            double r = ((col >> 16) & 0xFF) / 255.0;
            double g = ((col >> 8) & 0xFF) / 255.0;
            double b = (col & 0xFF) / 255.0;
            for (int s = 0; s < shadeCoeffs.length; s++) {
                double coeff = shadeCoeffs[s];
                shadedPalette[k * shadeCoeffs.length + s] = new double[] { r * coeff, g * coeff, b * coeff };
            }
        }
    }

    public static MapColor[] getColors(){
        MapColor[] colors = new MapColor[64];
        for (int i = 0; i<= 63; i++){
            colors[i] = MapColor.byId(i);
        }
        colors = Arrays.stream(colors).filter(Objects::nonNull).toArray(MapColor[]::new);
        return colors;
    }

//    public static ItemStack render(BufferedImage image, Image2Map.DitherMode mode, ServerWorld world, double x, double z,
//                                   PlayerEntity player) {
//        // mojang removed the ability to set a map as locked via the "locked" field in
//        // 1.17, so we create and apply our own MapState instead
//        ItemStack stack = new ItemStack(Items.FILLED_MAP);
//        MapIdComponent id = world.increaseAndGetMapId();
//        NbtCompound nbt = new NbtCompound();
//
//        nbt.putString("dimension", world.getRegistryKey().getValue().toString());
//        nbt.putInt("xCenter", (int) x);
//        nbt.putInt("zCenter", (int) z);
//        nbt.putBoolean("locked", true);
//        nbt.putBoolean("unlimitedTracking", false);
//        nbt.putBoolean("trackingPosition", false);
//        nbt.putByte("scale", (byte) 3);
//        MapState state = MapState.fromNbt(nbt);
//        world.putMapState(FilledMapItem.getMapName(id), state);
//        stack.getOrCreateNbt().putInt("map", id);
//
//        Image resizedImage = image.getScaledInstance(128, 128, Image.SCALE_DEFAULT);
//        BufferedImage resized = convertToBufferedImage(resizedImage);
//        int width = resized.getWidth();
//        int height = resized.getHeight();
//        int[][] pixels = convertPixelArray(resized);
//        MapColor[] mapColors = getColors();
//        Color imageColor;
//        mapColors = Arrays.stream(mapColors).filter(Objects::nonNull).toArray(MapColor[]::new);
//
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//                imageColor = new Color(pixels[j][i], true);
//                if (mode.equals(Image2Map.DitherMode.FLOYD))
//                    state.colors[i + j * width] = (byte) floydDither(mapColors, pixels, i, j, imageColor);
//                else
//                    state.colors[i + j * width] = (byte) nearestColor(mapColors, imageColor);
//            }
//        }
//        return stack;
//    }

    public static MapItemSavedData render(BufferedImage image, Image2Map.DitherMode mode, MapItemSavedData state) {
        // mojang removed the ability to set a map as locked via the "locked" field in
        // 1.17, so we create and apply our own MapState instead

        BufferedImage resized = new BufferedImage(128, 128, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image, 0, 0, 128, 128, null); // scales the source straight into the 128x128 buffer
        g.dispose();
        int width = resized.getWidth();
        int height = resized.getHeight();
        int[][] pixels = convertPixelArray(resized);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int argb = pixels[j][i];
                if (mode.equals(Image2Map.DitherMode.FLOYD))
                    state.colors[i + j * width] = (byte) floydDither(pixels, i, j, argb);
                else
                    state.colors[i + j * width] = (byte) nearestColor(argb);
            }
        }
        return state;
    }

    // Paletted RGB (0xRRGGBB) for a map color byte (index >> 2 = colour, index & 3 = shade).
    private static int mapColorToRgb(int color) {
        int col = mapColors[color >> 2].col;
        double coeff = shadeCoeffs[color & 3];
        int r = (int) (((col >> 16) & 0xFF) * coeff);
        int g = (int) (((col >> 8) & 0xFF) * coeff);
        int b = (int) ((col & 0xFF) * coeff);
        return (r << 16) | (g << 8) | b;
    }

    private static int floydDither(int[][] pixels, int x, int y, int argb) {
        int colorIndex = nearestColor(argb);
        int paletted = mapColorToRgb(colorIndex);
        int errR = ((argb >> 16) & 0xFF) - ((paletted >> 16) & 0xFF);
        int errG = ((argb >> 8) & 0xFF) - ((paletted >> 8) & 0xFF);
        int errB = (argb & 0xFF) - (paletted & 0xFF);
        if (pixels[0].length > x + 1) {
            pixels[y][x + 1] = applyError(pixels[y][x + 1], errR, errG, errB, 7.0 / 16.0);
        }
        if (pixels.length > y + 1) {
            if (x > 0) {
                pixels[y + 1][x - 1] = applyError(pixels[y + 1][x - 1], errR, errG, errB, 3.0 / 16.0);
            }
            pixels[y + 1][x] = applyError(pixels[y + 1][x], errR, errG, errB, 5.0 / 16.0);
            if (pixels[0].length > x + 1) {
                pixels[y + 1][x + 1] = applyError(pixels[y + 1][x + 1], errR, errG, errB, 1.0 / 16.0);
            }
        }

        return colorIndex;
    }

    private static int applyError(int argb, int errR, int errG, int errB, double quantConst) {
        int a = (argb >>> 24) & 0xFF;
        int r = clamp(((argb >> 16) & 0xFF) + (int) (errR * quantConst), 0, 255);
        int g = clamp(((argb >> 8) & 0xFF) + (int) (errG * quantConst), 0, 255);
        int b = clamp((argb & 0xFF) + (int) (errB * quantConst), 0, 255);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int clamp(int i, int min, int max) {
        if (min > max)
            throw new IllegalArgumentException("max value cannot be less than min value");
        if (i < min)
            return min;
        if (i > max)
            return max;
        return i;
    }

    private static int nearestColor(int argb) {
        int alpha = (argb >>> 24) & 0xFF;
        double ir = ((argb >> 16) & 0xFF) / 255.0;
        double ig = ((argb >> 8) & 0xFF) / 255.0;
        double ib = (argb & 0xFF) / 255.0;
        int best_color = 0;
        double lowest_distance = Double.MAX_VALUE;
        for (int idx = 0; idx < shadedPalette.length; idx++) {
            double[] v = shadedPalette[idx];
            double dr = ir - v[0];
            double dg = ig - v[1];
            double db = ib - v[2];
            double dist = dr * dr + dg * dg + db * db;
            if (dist < lowest_distance) {
                lowest_distance = dist;
                // todo: handle shading with alpha values other than 255
                if (idx < shadeCoeffs.length && alpha == 255) {
                    best_color = 119;
                } else {
                    best_color = idx;
                }
            }
        }
        return best_color;
    }

    private static int[][] convertPixelArray(BufferedImage image) {

        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();

        int[][] result = new int[height][width];
        final int pixelLength = 4;
        for (int pixel = 0, row = 0, col = 0; pixel + 3 < pixels.length; pixel += pixelLength) {
            int argb = 0;
            argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
            argb += ((int) pixels[pixel + 1] & 0xff); // blue
            argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
            argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
            result[row][col] = argb;
            col++;
            if (col == width) {
                col = 0;
                row++;
            }
        }

        return result;
    }
}
