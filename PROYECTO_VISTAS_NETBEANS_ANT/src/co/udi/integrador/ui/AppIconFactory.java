package co.udi.integrador.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public final class AppIconFactory {
    public static final String APP_NAME = "SIGPRA";

    private static final String LOGO_MAIN_PATH = "/branding/sigpra-logo-main.jpeg";
    private static final String LOGO_LOGIN_PATH = "/branding/sigpra-logo.png";
    private static BufferedImage cachedWordmark;
    private static BufferedImage cachedLoginWordmark;
    private static BufferedImage cachedSymbol;
    private static Image cachedIcon;

    private AppIconFactory() {
    }

    public static String windowTitle(String viewTitle) {
        return APP_NAME + " - " + viewTitle;
    }

    public static Image appIcon() {
        if (cachedIcon != null) {
            return cachedIcon;
        }
        cachedIcon = scaledSymbol(64, 64);
        return cachedIcon;
    }

    public static ImageIcon wordmarkIcon(int width, int height) {
        return new ImageIcon(scaledWordmark(width, height));
    }

    public static ImageIcon loginWordmarkIcon(int width, int height) {
        return new ImageIcon(scaledLoginWordmark(width, height));
    }

    public static ImageIcon symbolIcon(int width, int height) {
        return new ImageIcon(scaledSymbol(width, height));
    }

    // Legacy alias kept so old callers keep compiling.
    public static Image moonIcon() {
        return appIcon();
    }

    private static Image scaledWordmark(int width, int height) {
        BufferedImage source = loadWordmark();
        return scaleHighQuality(source, width, height);
    }

    private static Image scaledLoginWordmark(int width, int height) {
        BufferedImage source = loadLoginWordmark();
        return scaleHighQuality(source, width, height);
    }

    private static Image scaledSymbol(int width, int height) {
        BufferedImage source = loadSymbol();
        return scaleHighQuality(source, width, height);
    }

    private static BufferedImage loadWordmark() {
        if (cachedWordmark != null) {
            return cachedWordmark;
        }
        BufferedImage raw = readLogoFromResources(LOGO_MAIN_PATH);
        if (raw == null) {
            cachedWordmark = fallbackWordmark();
            return cachedWordmark;
        }

        BufferedImage transparent = transparentBackground(raw);
        cachedWordmark = extractBottomWordmark(transparent);
        if (cachedWordmark == null) {
            cachedWordmark = fallbackWordmark();
        }
        return cachedWordmark;
    }

    private static BufferedImage loadLoginWordmark() {
        if (cachedLoginWordmark != null) {
            return cachedLoginWordmark;
        }

        BufferedImage raw = readLogoFromResources(LOGO_LOGIN_PATH);
        if (raw == null) {
            cachedLoginWordmark = loadWordmark();
            return cachedLoginWordmark;
        }

        BufferedImage transparent = transparentBackground(raw);
        cachedLoginWordmark = extractBottomWordmark(transparent);
        if (cachedLoginWordmark == null) {
            cachedLoginWordmark = loadWordmark();
        }
        return cachedLoginWordmark;
    }

    private static BufferedImage loadSymbol() {
        if (cachedSymbol != null) {
            return cachedSymbol;
        }

        BufferedImage wordmark = loadWordmark();
        int size = Math.min(wordmark.getHeight(), wordmark.getWidth());
        if (size <= 0) {
            cachedSymbol = fallbackSymbol();
            return cachedSymbol;
        }

        cachedSymbol = wordmark.getSubimage(0, 0, size, size);
        return cachedSymbol;
    }

    private static BufferedImage readLogoFromResources(String path) {
        try (InputStream in = AppIconFactory.class.getResourceAsStream(path)) {
            if (in == null) {
                return null;
            }
            return ImageIO.read(in);
        } catch (IOException ex) {
            return null;
        }
    }

    private static BufferedImage transparentBackground(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();

        if (source.getColorModel().hasAlpha()) {
            BufferedImage direct = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = direct.createGraphics();
            g.drawImage(source, 0, 0, null);
            g.dispose();
            return direct;
        }

        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Color background = new Color(source.getRGB(0, 0));
        int threshold = 26;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixel = new Color(source.getRGB(x, y));
                int diff = Math.abs(pixel.getRed() - background.getRed())
                        + Math.abs(pixel.getGreen() - background.getGreen())
                        + Math.abs(pixel.getBlue() - background.getBlue());
                if (diff <= threshold) {
                    out.setRGB(x, y, 0x00000000);
                } else {
                    out.setRGB(x, y, (0xFF << 24) | (pixel.getRGB() & 0x00FFFFFF));
                }
            }
        }
        return out;
    }

    private static Image scaleHighQuality(BufferedImage source, int width, int height) {
        int srcW = source.getWidth();
        int srcH = source.getHeight();
        if (srcW <= 0 || srcH <= 0 || width <= 0 || height <= 0) {
            return source;
        }

        double scale = Math.min((double) width / srcW, (double) height / srcH);
        int drawW = Math.max(1, (int) Math.round(srcW * scale));
        int drawH = Math.max(1, (int) Math.round(srcH * scale));
        int x = (width - drawW) / 2;
        int y = (height - drawH) / 2;

        BufferedImage scaled = scaleProgressive(source, drawW, drawH);

        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(scaled, x, y, null);
        g.dispose();
        return out;
    }

    private static BufferedImage scaleProgressive(BufferedImage source, int targetW, int targetH) {
        int currentW = source.getWidth();
        int currentH = source.getHeight();
        BufferedImage current = source;

        while (currentW / 2 >= targetW && currentH / 2 >= targetH) {
            currentW = Math.max(targetW, currentW / 2);
            currentH = Math.max(targetH, currentH / 2);
            current = scaleOnce(current, currentW, currentH, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }
        return scaleOnce(current, targetW, targetH, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }

    private static BufferedImage scaleOnce(BufferedImage source, int targetW, int targetH, Object interpolation) {
        BufferedImage out = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolation);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(source, 0, 0, targetW, targetH, null);
        g.dispose();
        return out;
    }

    private static BufferedImage extractBottomWordmark(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();

        int rowStart = -1;
        int rowEnd = -1;
        boolean inContent = false;

        for (int y = 0; y < height; y++) {
            boolean hasInk = rowHasInk(source, y);
            if (hasInk && !inContent) {
                rowStart = y;
                inContent = true;
            } else if (!hasInk && inContent) {
                rowEnd = y - 1;
                inContent = false;
            }
        }
        if (inContent) {
            rowEnd = height - 1;
        }
        if (rowStart < 0 || rowEnd < rowStart) {
            return null;
        }

        int wordmarkTop = rowStart;
        int wordmarkBottom = rowEnd;

        for (int y = rowEnd; y >= 0; y--) {
            if (!rowHasInk(source, y)) {
                wordmarkTop = y + 1;
                break;
            }
        }

        int minX = width - 1;
        int maxX = 0;
        for (int y = wordmarkTop; y <= wordmarkBottom; y++) {
            for (int x = 0; x < width; x++) {
                if (((source.getRGB(x, y) >>> 24) & 0xFF) > 10) {
                    minX = Math.min(minX, x);
                    maxX = Math.max(maxX, x);
                }
            }
        }
        if (maxX <= minX) {
            return null;
        }
        return source.getSubimage(minX, wordmarkTop, maxX - minX + 1, wordmarkBottom - wordmarkTop + 1);
    }

    private static boolean rowHasInk(BufferedImage image, int y) {
        for (int x = 0; x < image.getWidth(); x++) {
            if (((image.getRGB(x, y) >>> 24) & 0xFF) > 10) {
                return true;
            }
        }
        return false;
    }

    private static BufferedImage fallbackWordmark() {
        int w = 380;
        int h = 120;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(19, 70, 170));
        g.fillRoundRect(12, 16, 86, 86, 28, 28);
        g.setColor(Color.WHITE);
        g.fillOval(24, 28, 25, 25);
        g.fillOval(62, 65, 18, 18);
        g.setColor(new Color(19, 54, 126));
        g.setFont(UITheme.TITLE_FONT.deriveFont(56f));
        g.drawString(APP_NAME, 120, 83);
        g.dispose();
        return image;
    }

    private static BufferedImage fallbackSymbol() {
        BufferedImage wordmark = fallbackWordmark();
        return wordmark.getSubimage(0, 0, 120, 120);
    }
}
