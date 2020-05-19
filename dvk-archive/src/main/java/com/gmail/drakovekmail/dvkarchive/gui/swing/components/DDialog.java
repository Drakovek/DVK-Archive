package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.gmail.drakovekmail.dvkarchive.gui.DScreenDimensions;

/**
 * Modal dialog UI object for DVK Archive.
 * 
 * @author Drakovek
 */
public class DDialog extends JDialog {

	//TODO ADD EVENT FOR CLOSING DIALOG

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 6102413813124854272L;

	/**
	 * Initializes the DDialog object.
	 * 
	 * @param owner DFrame owner of the DDialog
	 * @param panel Panel to display in the DDialog
	 * @param title Title of the DDialog
	 */
	public DDialog(DFrame owner, JPanel panel, String title) {
		super(owner, title, true);
		initialize_dialog(panel, 0, 0);
		setLocationRelativeTo(owner);
	}
	
	/**
	 * Initializes the DDialog object.
	 * 
	 * @param owner DFrame owner of the DDialog
	 * @param panel Panel to display in the DDialog
	 * @param title Title of the DDialog
	 * @param width Width of dialog. If 0, use default
	 * @param height Height of dialog. If 0, use default
	 */
	public DDialog(DFrame owner, JPanel panel, String title, int width, int height) {
		super(owner, title, true);
		initialize_dialog(panel, width, height);
		setLocationRelativeTo(owner);
	}
	
	/**
	 * Initializes dialog to contain panel and set to a given size.
	 * 
	 * @param panel Panel to display in the DDialog
	 * @param width Width of dialog. If 0, use default
	 * @param height Height of dialog. If 0, use default
	 */
	public void initialize_dialog(JPanel panel, int width, int height) {
		//PACK PANEL
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
		//SET PREFERED SIZE
		int frame_width = width;
		int frame_height = height;
		if(frame_width == 0) {
			frame_width = getWidth() + 5;
		}
		if(frame_height == 0) {
			frame_height = getHeight() + 5;
		}
		//LIMIT TO LESS THAN MAXIMUM SIZE
		DScreenDimensions sd = new DScreenDimensions();
		Dimension max = sd.get_maximum_size();
		int max_width = (int)max.getWidth();
		int max_height = (int)max.getHeight();
		if(frame_width > max_width) {
			frame_width = max_width;
		}
		if(frame_height > max_height) {
			frame_height = max_height;
		}
		Dimension size = new Dimension(frame_width, frame_height);
		setPreferredSize(size);
		setMinimumSize(size);
		pack();
	}
}
