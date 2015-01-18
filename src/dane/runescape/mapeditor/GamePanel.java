/*
 * The MIT License
 *
 * Copyright 2015 Dane.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dane.runescape.mapeditor;

import com.runescape.event.ShellListener;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 * This component is used to override the Graphics object of the game, and to
 * draw directly into a buffered image. This is because originally the game
 * would draw directly to its applets graphics, which would cause artifacts
 * attempting to add the applet directly into a panel.
 *
 * @author Dane
 */
public class GamePanel extends JComponent implements ShellListener {

	private final GameSub game;
	private BufferedImage image;
	private Graphics graphics;

	public GamePanel(GameSub game) {
		this.game = game;
	}

	public void init() {
		final int w = this.getWidth();
		final int h = this.getHeight();

		this.game.addShellListener(this);
		this.game.initApplet(w, h);
		this.image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		this.graphics = this.image.getGraphics();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(this.image, 0, 0, this.getWidth(), this.getHeight(), this);
	}

	public GameSub getGame() {
		return this.game;
	}

	@Override
	public void onShellStartup() {
		// override graphics
		this.game.graphics = this.graphics;
	}

	@Override
	public void onShellDraw() {
		this.repaint();
	}

	@Override
	public void onShellUpdate() {

	}

	@Override
	public void onShellShutdown() {
		this.graphics.dispose(); // byebye
	}

}