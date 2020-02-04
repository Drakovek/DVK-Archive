package com.gmail.drakovekmail.dvkarchive.gui.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DButton;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DLabel;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;

/**
 * Settings bar GUI object for accessing program settings.
 * 
 * @author Drakovek
 */
public class SettingsBarGUI extends JPanel implements DActionEvent{
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 720135488118796515L;

	/**
	 * Creates the settings bar GUI object.
	 * 
	 * @param base_gui BaseGUI for UI settings
	 */
	public SettingsBarGUI(BaseGUI base_gui) {
		//CREATE BUTTON AND LABEL
		DButton btn = new DButton(base_gui, this, "settings");
		DLabel lbl = new DLabel(base_gui, "no_dir_select");
		//CREATE INTERNAL BAR
		JPanel internal = new JPanel();
		internal.setLayout(new GridBagLayout());
		GridBagConstraints cst = new GridBagConstraints();
		cst.gridx = 2;
		cst.gridy = 0;
		cst.gridwidth = 1;
		cst.gridheight = 3;
		cst.weightx = 0;
		cst.weighty = 0;
		cst.fill = GridBagConstraints.BOTH;
		internal.add(btn, cst);
		cst.gridx = 1;
		internal.add(base_gui.get_x_space(), cst);
		cst.gridx = 0;
		cst.weightx = 1;
		internal.add(lbl, cst);
		//ADD SEPARATOR
		JSeparator sep;
		sep = new JSeparator(SwingConstants.HORIZONTAL);
		JPanel bar = base_gui.get_y_stack(sep, internal);
		//ADD SPACES
		this.setLayout(new GridLayout(1, 1));
		this.add(base_gui.get_spaced_panel(bar, 1, 0, false, true, true, true));
	}

	@Override
	public void event(String id) {
		System.out.println("Something happened.");
	}
}
