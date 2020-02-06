package com.gmail.drakovekmail.dvkarchive.gui;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DButton;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DLabel;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;

/**
 * Framework for a simple ServiceGUI with only one runnable task.
 * 
 * @author Drakovek
 */
public class SimpleServiceGUI extends ServiceGUI implements DActionEvent {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 7717006779177292600L;

	/**
	 * Initializes the SimpleServiceGUI object.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 * @param title Title ID for the service GUI
	 * @param desc Description of the task in the service GUI
	 */
	public SimpleServiceGUI(BaseGUI base_gui, String title, String desc) {
		super(base_gui);
		//CREATE TITLE PANEL
		DLabel title_lbl = new DLabel(this.base_gui, null, title);
		title_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		JPanel title_pnl = this.base_gui.get_y_stack(title_lbl, sep);
		//CREATE DESCRIPTION PANEL
		DLabel desc_lbl = new DLabel(this.base_gui, null, desc);
		desc_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		desc_lbl.wrap_text(true);
		JPanel desc_pnl = this.base_gui.get_y_stack(title_pnl, desc_lbl);
		//CREATE BUTTON PANEL
		DButton run_btn = new DButton(this.base_gui, this, "run");
		JPanel button_pnl = this.base_gui.get_y_stack(desc_pnl, run_btn);
		//CREATE SERVICE PANEL
		JPanel service_pnl = base_gui.get_spaced_panel(
				button_pnl, 0, 0, false, false, false, false);
		this.setLayout(new GridLayout(1, 1));
		this.add(service_pnl);
	}

	@Override
	public void event(String id) {
	}
}
