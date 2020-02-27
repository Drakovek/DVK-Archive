package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;

/**
 * Extension of JPanel that can be used in JScrollPane.
 * 
 * @author Drakovek
 */
public class DScrollablePanel extends JPanel implements Scrollable {

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 3369146791600449991L;

	/**
	 * Whether to limit the width to the container's width
	 */
	private boolean fit_width;
	
	/**
	 * Whether to limit the height to the container's height
	 */
	private boolean fit_height;
	
	/**
	 * Preferred size of the scroll pane viewport
	 */
	private Dimension pvps;
	
	/**
	 * Initializes the DScrollablePanel
	 */
	public DScrollablePanel() {
		this.fit_width = false;
		this.fit_height = false;
		this.pvps = new Dimension(1, 1);
		this.setLayout(new GridLayout(1, 1));
	}
	
	/**
	 * Adds a panel to the scrollable panel.
	 * 
	 * @param panel Given panel to show
	 * @param fit_width Whether to limit the width to the container's width.
	 * @param fit_height Whether to limit the height to the container's height.
	 */
	public void set_panel(JPanel panel, boolean fit_width, boolean fit_height) {
		this.fit_width = fit_width;
		this.fit_height = fit_height;
		this.pvps = panel.getPreferredSize();
		this.removeAll();
		this.add(panel);
		this.revalidate();
		this.repaint();
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return this.pvps;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		return 0;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return this.fit_height;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return this.fit_width;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		return 0;
	}
}
