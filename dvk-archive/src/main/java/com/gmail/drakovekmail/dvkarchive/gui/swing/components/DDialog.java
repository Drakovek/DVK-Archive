package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * Modal dialog UI object for DVK Archive.
 * 
 * @author Drakovek
 */
public class DDialog extends JDialog {
	
	//TODO ADD MAXIMUM SIZE
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
		Dimension size = new Dimension(frame_width, frame_height);
		setPreferredSize(size);
		setMinimumSize(size);
		pack();
	}
}
