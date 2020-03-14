package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DFrame;

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
		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
		setMinimumSize(getSize());
		setLocationRelativeTo(owner);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
}
