package com.gmail.drakovekmail.dvkarchive.gui.settings;

import java.awt.GridLayout;
import java.io.File;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.Disabler;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DButton;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DLabel;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;

/**
 * Settings bar GUI object for accessing program settings.
 * 
 * @author Drakovek
 */
public class SettingsBarGUI extends JPanel implements DActionEvent, Disabler {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 720135488118796515L;

	/**
	 * Directory label for the settings bar.
	 */
	private DLabel lbl;
	
	/**
	 * Settings button for the settings bar.
	 */
	private DButton btn;
	
	/**
	 * Creates the settings bar GUI object.
	 * 
	 * @param base_gui BaseGUI for UI settings
	 */
	public SettingsBarGUI(BaseGUI base_gui) {
		//CREATE BUTTON AND LABEL
		this.btn = new DButton(base_gui, this, "settings");
		this.lbl = new DLabel(base_gui, null, "no_dir_select");
		//CREATE INTERNAL BAR
		JPanel internal = base_gui.get_x_stack(
				this.lbl, 1, this.btn, 0);
		//ADD SEPARATOR
		JSeparator sep;
		sep = new JSeparator(SwingConstants.HORIZONTAL);
		JPanel bar = base_gui.get_y_stack(sep, internal);
		//ADD SPACES
		this.setLayout(new GridLayout(1, 1));
		this.add(base_gui.get_spaced_panel(bar, 1, 0, false, true, true, true));
	}
	
	/**
	 * Sets the directory shown in the settings bar.
	 * 
	 * @param dir Directory to show.
	 */
	public void set_directory(File dir) {
		if(dir == null) {
			this.lbl.set_text_id("no_dir_select");
		}
		else {
			this.lbl.setText(dir.getAbsolutePath());
		}
	}

	@Override
	public void event(String id) {
		System.out.println("Something happened.");
	}

	@Override
	public void enable_all() {
		this.btn.setEnabled(true);
	}

	@Override
	public void disable_all() {
		this.btn.setEnabled(false);
	}
}
