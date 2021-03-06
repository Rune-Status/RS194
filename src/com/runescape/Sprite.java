package com.runescape;

import com.runescape.Graphics2D;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.logging.*;
import javax.imageio.*;

public class Sprite extends Graphics2D {

	private static final Logger logger = Logger.getLogger(Sprite.class.toString());

	public static final Sprite load(File f) throws IOException {
		BufferedImage image = ImageIO.read(f);
		Sprite s = new Sprite(image.getWidth(), image.getHeight());
		image.getRGB(0, 0, s.width, s.height, s.pixels, 0, s.width);
		for (int i = 0; i < s.pixels.length; i++) {
			s.pixels[i] &= ~(0xFF000000);
		}
		return s;
	}

	public int[] pixels;
	public int width;
	public int height;
	public int clipX;
	public int clipY;
	public int clipWidth;
	public int clipHeight;

	public Sprite(int w, int h) {
		pixels = new int[w * h];
		width = clipWidth = w;
		height = clipHeight = h;
		clipX = clipY = 0;
	}

	public Sprite(byte[] src, Component c) {
		try {
			Image i = Toolkit.getDefaultToolkit().createImage(src);
			MediaTracker mt = new MediaTracker(c);
			mt.addImage(i, 0);
			mt.waitForAll();
			width = i.getWidth(c);
			height = i.getHeight(c);
			clipWidth = width;
			clipHeight = height;
			clipX = 0;
			clipY = 0;
			pixels = new int[width * height];
			PixelGrabber pg = new PixelGrabber(i, 0, 0, width, height, pixels, 0, width);
			pg.grabPixels();
		} catch (Exception e) {
			System.out.println("Error converting jpg");
		}
	}

	public Sprite(Archive archive, String name, int index) {
		Buffer dat = new Buffer(archive.get(name + ".dat", null));
		Buffer idx = new Buffer(archive.get("index.dat", null));
		idx.position = dat.readUShort();

		clipWidth = idx.readUShort();
		clipHeight = idx.readUShort();

		int[] palette = new int[idx.read()];

		for (int i = 0; i < palette.length - 1; i++) {
			palette[i + 1] = idx.readInt24();
			if (palette[i + 1] == 0) {
				palette[i + 1] = 1;
			}
		}

		for (int i = 0; i < index; i++) {
			idx.position += 2;
			dat.position += (idx.readUShort() * idx.readUShort());
			idx.position++;
		}

		clipX = idx.read();
		clipY = idx.read();
		width = idx.readUShort();
		height = idx.readUShort();

		int type = idx.read();
		int len = width * height;
		pixels = new int[len];

		if (type == 0) {
			for (int i = 0; i < len; i++) {
				pixels[i] = palette[dat.read()];
			}
		} else if (type == 1) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					pixels[x + y * width] = palette[dat.read()];
				}
			}
		}
	}

	public void prepare() {
		Graphics2D.prepare(pixels, width, height);
	}

	public void replace(int rgbA, int rgbB) {
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] == rgbA) {
				pixels[i] = rgbB;
			}
		}
	}

	public void drawOpaque(int x, int y) {
		x += clipX;
		y += clipY;

		int dstOff = x + y * Graphics2D.targetWidth;
		int srcOff = 0;
		int h = height;
		int w = width;
		int dstStep = Graphics2D.targetWidth - w;
		int srcStep = 0;

		if (y < Graphics2D.top) {
			int cutoff = Graphics2D.top - y;
			h -= cutoff;
			y = Graphics2D.top;
			srcOff += cutoff * w;
			dstOff += cutoff * Graphics2D.targetWidth;
		}

		if (y + h > Graphics2D.bottom) {
			h -= y + h - Graphics2D.bottom;
		}

		if (x < Graphics2D.left) {
			int cutoff = Graphics2D.left - x;
			w -= cutoff;
			x = Graphics2D.left;
			srcOff += cutoff;
			dstOff += cutoff;
			srcStep += cutoff;
			dstStep += cutoff;
		}
		if (x + w > Graphics2D.right) {
			int i_22_ = x + w - Graphics2D.right;
			w -= i_22_;
			srcStep += i_22_;
			dstStep += i_22_;
		}

		if (w > 0 && h > 0) {
			copyImage(w, h, pixels, srcOff, srcStep, Graphics2D.target, dstOff, dstStep);
		}
	}

	private void copyImage(int w, int h, int[] src, int srcOff, int srcStep, int[] dst, int dstOff, int dstStep) {
		int hw = -(w >> 2);
		w = -(w & 0x3);

		for (int y = -h; y < 0; y++) {
			for (int x = hw; x < 0; x++) {
				dst[dstOff++] = src[srcOff++];
				dst[dstOff++] = src[srcOff++];
				dst[dstOff++] = src[srcOff++];
				dst[dstOff++] = src[srcOff++];
			}

			for (int x = w; x < 0; x++) {
				dst[dstOff++] = src[srcOff++];
			}

			dstOff += dstStep;
			srcOff += srcStep;
		}
	}

	public void draw(int x, int y) {
		x += clipX;
		y += clipY;

		int dstOff = x + y * Graphics2D.targetWidth;
		int srcOff = 0;
		int w = width;
		int h = height;
		int dstStep = Graphics2D.targetWidth - w;
		int srcStep = 0;

		if (y < Graphics2D.top) {
			int cutoff = Graphics2D.top - y;
			h -= cutoff;
			y = Graphics2D.top;
			srcOff += cutoff * w;
			dstOff += cutoff * Graphics2D.targetWidth;
		}

		if (y + h > Graphics2D.bottom) {
			h -= y + h - Graphics2D.bottom;
		}

		if (x < Graphics2D.left) {
			int cutoff = Graphics2D.left - x;
			w -= cutoff;
			x = Graphics2D.left;
			srcOff += cutoff;
			dstOff += cutoff;
			srcStep += cutoff;
			dstStep += cutoff;
		}

		if (x + w > Graphics2D.right) {
			int cutoff = x + w - Graphics2D.right;
			w -= cutoff;
			srcStep += cutoff;
			dstStep += cutoff;
		}

		if (w > 0 && h > 0) {
			copyImage(h, w, pixels, srcOff, srcStep, Graphics2D.target, dstOff, dstStep, 0);
		}
	}

	public void copyImage(int h, int w, int[] src, int srcOff, int srcStep, int[] dst, int dstOff, int dstStep, int rgb) {
		int hw = -(w >> 2);
		w = -(w & 0x3);
		for (int x = -h; x < 0; x++) {
			for (int y = hw; y < 0; y++) {
				rgb = src[srcOff++];

				if (rgb != 0) {
					dst[dstOff++] = rgb;
				} else {
					dstOff++;
				}

				rgb = src[srcOff++];

				if (rgb != 0) {
					dst[dstOff++] = rgb;
				} else {
					dstOff++;
				}

				rgb = src[srcOff++];

				if (rgb != 0) {
					dst[dstOff++] = rgb;
				} else {
					dstOff++;
				}

				rgb = src[srcOff++];

				if (rgb != 0) {
					dst[dstOff++] = rgb;
				} else {
					dstOff++;
				}
			}

			for (int y = w; y < 0; y++) {
				rgb = src[srcOff++];

				if (rgb != 0) {
					dst[dstOff++] = rgb;
				} else {
					dstOff++;
				}
			}

			dstOff += dstStep;
			srcOff += srcStep;
		}
	}

	public void draw(int x, int y, int alpha) {
		x += clipX;
		y += clipY;

		int dstOff = x + y * Graphics2D.targetWidth;
		int srcOff = 0;
		int w = width;
		int h = height;
		int dstStep = Graphics2D.targetWidth - w;
		int srcStep = 0;

		if (y < Graphics2D.top) {
			int cutoff = Graphics2D.top - y;
			h -= cutoff;
			y = Graphics2D.top;
			srcOff += cutoff * w;
			dstOff += cutoff * Graphics2D.targetWidth;
		}

		if (y + h > Graphics2D.bottom) {
			h -= y + h - Graphics2D.bottom;
		}

		if (x < Graphics2D.left) {
			int cutoff = Graphics2D.left - x;
			w -= cutoff;
			x = Graphics2D.left;
			srcOff += cutoff;
			dstOff += cutoff;
			srcStep += cutoff;
			dstStep += cutoff;
		}

		if (x + w > Graphics2D.right) {
			int cutoff = x + w - Graphics2D.right;
			w -= cutoff;
			srcStep += cutoff;
			dstStep += cutoff;
		}

		if (w > 0 && h > 0) {
			copyImage(w, h, pixels, srcOff, srcStep, Graphics2D.target, dstOff, dstStep, alpha, 0);
		}
	}

	private void copyImage(int w, int h, int[] src, int srcOff, int srcStep, int[] dst, int dstOff, int dstStep, int alpha, int rgb) {
		int opacity = 256 - alpha;
		for (int y = -h; y < 0; y++) {
			for (int x = -w; x < 0; x++) {
				rgb = src[srcOff++];
				if (rgb != 0) {
					int dstRGB = dst[dstOff];
					dst[dstOff++] = ((((rgb & 0xff00ff) * alpha + (dstRGB & 0xff00ff) * opacity) & ~0xff00ff) + (((rgb & 0xff00) * alpha + (dstRGB & 0xff00) * opacity) & 0xff0000)) >> 8;
				} else {
					dstOff++;
				}
			}
			dstOff += dstStep;
			srcOff += srcStep;
		}
	}

	public void draw(int x, int y, int w, int h, int pivotX, int pivotY, int theta, int[] lineStart, int[] lineWidth) {
		try {
			int cx = -w / 2;
			int cy = -h / 2;

			int sin = (int) (Math.sin((double) theta / 326.11) * 65536.0);
			int cos = (int) (Math.cos((double) theta / 326.11) * 65536.0);

			int offX = (pivotX << 16) + (cy * sin + cx * cos);
			int offY = (pivotY << 16) + (cy * cos - cx * sin);
			int baseOffset = x + (y * Graphics2D.targetWidth);

			for (y = 0; y < h; y++) {
				int start = lineStart[y];
				int off = baseOffset + start;
				int srcX = offX + cos * start;
				int srcY = offY - sin * start;
				for (x = 0; x < lineWidth[y]; x++) {
					Graphics2D.target[off++] = pixels[(srcX >> 16) + (srcY >> 16) * width];
					srcX += cos;
					srcY -= sin;
				}
				offX += sin;
				offY += cos;
				baseOffset += Graphics2D.targetWidth;
			}
		} catch (Exception e) {
		}
	}

	public void draw(int x, int y, int w, int h, int pivotX, int pivotY, int theta) {
		try {
			int cx = -w / 2;
			int cy = -h / 2;

			int sin = (int) (Math.sin((double) theta / 326.11) * 65536.0);
			int cos = (int) (Math.cos((double) theta / 326.11) * 65536.0);

			int offX = (pivotX << 16) + (cy * sin + cx * cos);
			int offY = (pivotY << 16) + (cy * cos - cx * sin);
			int baseOffset = x + (y * Graphics2D.targetWidth);

			for (y = 0; y < h; y++) {
				int off = baseOffset;
				int dstX = offX + cos;
				int dstY = offY - sin;
				for (x = 0; x < w; x++) {
					int rgb = pixels[(dstX >> 16) + (dstY >> 16) * width];

					if (rgb != 0) {
						Graphics2D.target[off++] = rgb;
					} else {
						off++;
					}
					dstX += cos;
					dstY -= sin;
				}
				offX += sin;
				offY += cos;
				baseOffset += Graphics2D.targetWidth;
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error drawing rotated bitmap", e);
		}
	}

	public void draw(IndexedSprite mask, int x, int y) {
		x += clipX;
		y += clipY;

		int dstOff = x + y * Graphics2D.targetWidth;
		int srcOff = 0;

		int h = height;
		int w = width;

		int dstStep = Graphics2D.targetWidth - w;
		int srcStep = 0;

		if (y < Graphics2D.top) {
			int i = Graphics2D.top - y;
			h -= i;
			y = Graphics2D.top;
			srcOff += i * w;
			dstOff += i * Graphics2D.targetWidth;
		}

		if (y + h > Graphics2D.bottom) {
			h -= y + h - Graphics2D.bottom;
		}

		if (x < Graphics2D.left) {
			int i = Graphics2D.left - x;
			w -= i;
			x = Graphics2D.left;
			srcOff += i;
			dstOff += i;
			srcStep += i;
			dstStep += i;
		}
		if (x + w > Graphics2D.right) {
			int i = x + w - Graphics2D.right;
			w -= i;
			srcStep += i;
			dstStep += i;
		}

		if (w > 0 && h > 0) {
			copyImage(Graphics2D.target, srcOff, 0, h, srcStep, dstOff, dstStep, pixels, mask.data, w);
		}
	}

	private void copyImage(int[] is, int i, int i_111_, int i_112_, int i_113_, int i_114_, int i_115_, int[] is_116_, byte[] is_117_, int i_118_) {
		int i_119_ = -(i_118_ >> 2);
		i_118_ = -(i_118_ & 0x3);
		for (int i_120_ = -i_112_; i_120_ < 0; i_120_++) {
			for (int i_121_ = i_119_; i_121_ < 0; i_121_++) {
				i_111_ = is_116_[i++];
				if (i_111_ != 0 && is_117_[i_114_] == 0) {
					is[i_114_++] = i_111_;
				} else {
					i_114_++;
				}
				i_111_ = is_116_[i++];
				if (i_111_ != 0 && is_117_[i_114_] == 0) {
					is[i_114_++] = i_111_;
				} else {
					i_114_++;
				}
				i_111_ = is_116_[i++];
				if (i_111_ != 0 && is_117_[i_114_] == 0) {
					is[i_114_++] = i_111_;
				} else {
					i_114_++;
				}
				i_111_ = is_116_[i++];
				if (i_111_ != 0 && is_117_[i_114_] == 0) {
					is[i_114_++] = i_111_;
				} else {
					i_114_++;
				}
			}
			for (int i_122_ = i_118_; i_122_ < 0; i_122_++) {
				i_111_ = is_116_[i++];
				if (i_111_ != 0 && is_117_[i_114_] == 0) {
					is[i_114_++] = i_111_;
				} else {
					i_114_++;
				}
			}
			i_114_ += i_115_;
			i += i_113_;
		}
	}

	public void flipHorizontally() {
		int[] flipped = new int[width * height];
		int off = 0;
		for (int y = 0; y < height; y++) {
			for (int x = width - 1; x >= 0; x--) {
				flipped[off++] = pixels[x + (y * width)];
			}
		}
		pixels = flipped;
		clipX = clipWidth - width - clipX;
	}

	public void flipVertically() {
		int[] flipped = new int[width * height];
		int off = 0;
		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {
				flipped[off++] = pixels[x + (y * width)];
			}
		}
		pixels = flipped;
		clipY = clipHeight - height - clipY;
	}

}
