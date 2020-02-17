package com.gmail.drakovekmail.dvkarchive.gui;

import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DButton;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DLabel;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;
import com.gmail.drakovekmail.dvkarchive.gui.work.DSwingWorker;
import com.gmail.drakovekmail.dvkarchive.gui.work.DWorker;

/**
 * Framework for a simple ServiceGUI with only one runnable task.
 * 
 * @author Drakovek
 */
public abstract class SimpleServiceGUI extends ServiceGUI implements DActionEvent, DWorker {

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 7833809474544376938L;

	/**
	 * DvkHandler for reading Dvk objects
	 */
	protected DvkHandler dvk_handler;
	
	/**
	 * SwingWorker for running processes
	 */
	protected DSwingWorker sw;
	
	/**
	 * Main button for running services
	 */
	private DButton run_btn;

	/**
	 * Initializes the SimpleServiceGUI object.
	 * 
	 * @param start_gui Parent of the SimpleServiceGUI
	 * @param title Title ID for the service GUI
	 * @param desc Description of the task in the service GUI
	 */
	public SimpleServiceGUI(StartGUI start_gui, String title, String desc) {
		super(start_gui);
		BaseGUI base_gui = this.start_gui.get_base_gui();
		//CREATE TITLE PANEL
		DLabel title_lbl = new DLabel(base_gui, null, title);
		title_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		title_lbl.set_font_large();
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		JPanel title_pnl = base_gui.get_y_stack(title_lbl, sep);
		//CREATE DESCRIPTION PANEL
		DLabel desc_lbl = new DLabel(base_gui, null, desc);
		desc_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		desc_lbl.wrap_text(true);
		JPanel desc_pnl = base_gui.get_y_stack(title_pnl, desc_lbl);
		//CREATE BUTTON PANEL
		this.run_btn = new DButton(base_gui, this, "run");
		JPanel button_pnl = base_gui.get_y_stack(desc_pnl, this.run_btn);
		//CREATE SERVICE PANEL
		JPanel service_pnl = base_gui.get_spaced_panel(
				button_pnl, 0, 0, false, false, false, false);
		this.setLayout(new GridLayout(1, 1));
		this.add(service_pnl);
	}
	
	/**
	 * Starts SwingWorker to read Dvk objects.
	 */
	private void start_read_dvks() {
		this.sw = new DSwingWorker(this, "read_dvks");
		this.sw.execute();
	}
	
	@Override
	public void event(String id) {
		if(directory_loaded()) {
			this.start_gui.get_base_gui().set_running(true);
			this.start_gui.get_base_gui().set_canceled(false);
			this.start_gui.disable_all();
			disable_all();
			start_read_dvks();
		}
	}
	
	@Override
	public void enable_all() {
		this.run_btn.setEnabled(true);
	}

	@Override
	public void disable_all() {
		this.run_btn.setEnabled(false);
	}
}
