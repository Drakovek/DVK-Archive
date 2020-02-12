package com.gmail.drakovekmail.dvkarchive.gui.artist;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.ServiceGUI;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.gui.language.LanguageHandler;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DButton;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DLabel;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DTextField;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;

/**
 * GUI for downloading files from artist-hosting websites.
 * 
 * @author Drakovek
 */
public abstract class ArtistHostingGUI extends ServiceGUI implements DActionEvent {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -2549451539358120744L;
	
	/**
	 * Name of the current artist-hosting service.
	 */
	private String name;
	
	/**
	 * Initializes the ArtistHostingGUI object.
	 * 
	 * @param start_gui Parent of ArtistHostingGUI
	 * @param name_id Language ID for the name of current service.
	 */
	public ArtistHostingGUI(StartGUI start_gui, String name_id) {
		super(start_gui);
		this.name = start_gui.get_base_gui()
				.get_language_string(name_id);
		this.setLayout(new GridLayout(1, 1));
	}
	
	/**
	 * Creates and displays a login GUI.
	 */
	protected void create_login_gui() {
		BaseGUI base_gui = this.start_gui.get_base_gui();
		DTextField u_txt = new DTextField(base_gui);
		DTextField p_txt = new DTextField(base_gui);
		DLabel u_lbl = new DLabel(base_gui, u_txt, "username");
		DLabel p_lbl = new DLabel(base_gui, p_txt, "password");
		//CREATE INPUT PANEL
		JPanel usr_pnl = base_gui.get_x_stack(u_lbl, 0, u_txt, 1);
		JPanel pass_pnl = base_gui.get_x_stack(p_lbl, 0, p_txt, 1);
		JPanel in_pnl = base_gui.get_y_stack(usr_pnl, pass_pnl);
		//CREATE BOTTOM PANEL
		DButton login_btn = new DButton(base_gui, this, "login");
		DButton skip_btn = new DButton(base_gui, this, "skip_login");
		JPanel btn_pnl = base_gui.get_y_stack(login_btn, skip_btn);
		JPanel btm_pnl = base_gui.get_y_stack(in_pnl, btn_pnl);
		//CREATE TOP LABEL
		String label = base_gui.get_language_string("login");
		label = LanguageHandler.get_text(label);
		label = this.name + " - " + label;
		DLabel login_lbl = new DLabel(base_gui, null, "login");
		login_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		login_lbl.set_font_large();
		login_lbl.setText(label);
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		JPanel top_pnl = base_gui.get_y_stack(login_lbl, sep);
		//CREATE FULL PANEL
		JPanel full_pnl = base_gui.get_y_stack(top_pnl, btm_pnl);
		//CREATE CENTER PANEL
		JPanel center_pnl = base_gui.get_spaced_panel(full_pnl, 0, 0, false, false, false, false);
		//UPDATE MAIN PANEL
		this.removeAll();
		this.add(center_pnl);
		this.revalidate();
		this.repaint();
	}
	
	@Override
	public void enable_all() {
	}

	@Override
	public void disable_all() {
	}
	
	@Override
	public void event(String id) {
		switch(id) {
			case "login":
				System.out.println("login");
				break;
			case "skip_login":
				System.out.println("skip");
				break;
		}
	}
}
