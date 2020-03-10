package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.gmail.drakovekmail.dvkarchive.gui.work.DRunnable;
import com.gmail.drakovekmail.dvkarchive.gui.work.DWorker;

/**
 * ScrollPane UI object for DVK Archive.
 * 
 * @author Drakovek
 */
public class DScrollPane extends JScrollPane implements DWorker {

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -9222358221795910930L;

	/**
	 * Initializes the DScrollPane object.
	 * 
	 * @param view Component to use in the scroll pane
	 */
	public DScrollPane(JComponent view) {
		super(view);
	}
	
	/**
	 * Initializes the DScrollPane object.
	 * 
	 * @param view Component to use in the scroll pane
	 * @param hsbp Horizontal ScrollBar Policy
	 * @param vsbp Vertical ScrollBar Policy
	 */
	public DScrollPane(JComponent view, int hsbp, int vsbp) {
		super(view, vsbp, hsbp);
	}
	
	/**
	 * Sets scroll pane to the top left corner.
	 */
	public void top_left() {
		SwingUtilities.invokeLater(new DRunnable(this, "top_left"));
	}
	
	/**
	 * Sets scroll pane to the top right corner.
	 */
	public void top_right() {
		SwingUtilities.invokeLater(new DRunnable(this, "top_right"));
	}
	
	/**
	 * Sets scroll pane to the bottom left corner.
	 */
	public void bottom_left() {
		SwingUtilities.invokeLater(new DRunnable(this, "bottom_left"));
	}
	
	/**
	 * Sets scroll pane to the bottom right corner.
	 */
	public void bottom_right() {
		SwingUtilities.invokeLater(new DRunnable(this, "bottom_right"));
	}

	@Override
	public void run(String id) {
		switch(id) {
			case "top_left":
				getHorizontalScrollBar().setValue(getHorizontalScrollBar().getMinimum());
				getVerticalScrollBar().setValue(getVerticalScrollBar().getMinimum());
				break;
			case "top_right":
				getHorizontalScrollBar().setValue(getHorizontalScrollBar().getMaximum());
				getVerticalScrollBar().setValue(getVerticalScrollBar().getMinimum());
				break;
			case "bottom_left":
				getHorizontalScrollBar().setValue(getHorizontalScrollBar().getMinimum());
				getVerticalScrollBar().setValue(getVerticalScrollBar().getMaximum());
				break;
			case "bottom_right":
				getHorizontalScrollBar().setValue(getHorizontalScrollBar().getMaximum());
				getVerticalScrollBar().setValue(getVerticalScrollBar().getMaximum());
				break;
		}
	}

	@Override
	public void done(String id) {}
}
