package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import java.awt.Dimension;
import javax.swing.JFrame;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.DScreenDimensions;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DCloseListener;

/**
 * Frame UI object for DVK Archive.
 * 
 * @author Drakovek
 */
public class DFrame extends JFrame implements DActionEvent {

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 5679649795269805200L;
	
	/**
	 * DActionEvent to call when attempting to close frame
	 */
	private DActionEvent event;

	/**
	 * Initializes the DFrame object.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 * @param event DActionEvent to call when attempting to close frame
	 * @param title Frame title
	 */
	public DFrame(BaseGUI base_gui, DActionEvent event, String title) {
		super(base_gui.get_language_string(title));
		this.event = event;
		this.setMinimumSize(new Dimension(200, 200));
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new DCloseListener(base_gui, this, "close_frame"));
	}

	/**
	 * Packs the frame while ensuring frame fits on screen.
	 */
	public void pack_restricted() {
		pack();
		int width = getWidth();
		int height = getHeight();
		DScreenDimensions sd = new DScreenDimensions();
		Dimension max = sd.get_maximum_size();
		int max_width = (int)max.getWidth();
		int max_height = (int)max.getHeight();
		if(width > max_width) {
			width = max_width;
		}
		if(height > max_height) {
			height = max_height;
		}
		Dimension size = new Dimension(width, height);
		setPreferredSize(size);
		setMinimumSize(size);
		pack();
	}
	
	/**
	 * Closes the frame.
	 */
	public void close() {
		this.dispose();
	}
	
	@Override
	public void event(String id) {
		this.event.event("close_frame");
	}
}
