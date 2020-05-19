package com.gmail.drakovekmail.dvkarchive.gui;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

/**
 * Class for getting screen dimensions.
 * 
 * @author Drakovek
 */
public class DScreenDimensions {
	
	/**
	 * DisplayMode for the smallest connected screen
	 */
	private DisplayMode small_screen;
	
	/**
	 * DisplayMode for the largest connected screen
	 */
	private DisplayMode large_screen;
	
	/**
	 * Initializes the DScreenDimensions class, getting smallest and largest screen sizes.
	 */
	public DScreenDimensions() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gds = ge.getScreenDevices();
		DisplayMode dm = gds[0].getDisplayMode();
		this.small_screen = gds[0].getDisplayMode();
		this.large_screen = gds[0].getDisplayMode();
		int small = dm.getWidth() * dm.getHeight();
		int large = small;
		int size = 0;
		for(int i = 0; i < gds.length; i++) {
			dm = gds[i].getDisplayMode();
			size = dm.getWidth() * dm.getHeight();
			if(size < small) {
				this.small_screen = dm;
				small = size;
			}
			if(size > large) {
				this.large_screen = dm;
				large = size;
			}
		}
	}
	
	/**
	 * Returns the dimensions of the smallest connected screen.
	 * 
	 * @return Small screen dimensions
	 */
	public Dimension get_small_screen_size() {
		return new Dimension(this.small_screen.getWidth(), this.small_screen.getHeight());
	}
	
	/**
	 * Returns the dimensions of the largest connected screen.
	 * 
	 * @return Large screen dimensions
	 */
	public Dimension get_large_screen_size() {
		return new Dimension(this.large_screen.getWidth(), this.large_screen.getHeight());
	}
	
	/**
	 * Return dimensions of the largest size to allow window to initialize as.
	 * 
	 * @return Maximum window dimensions
	 */
	public Dimension get_maximum_size() {
		int width = this.small_screen.getWidth() - 100;
		int height = this.small_screen.getHeight() - 100;
		if(width < 1) {
			width = 1;
		}
		if(height < 1) {
			height = 1;
		}
		return new Dimension(width, height);
	}
}
