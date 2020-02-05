package com.gmail.drakovekmail.dvkarchive.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.gmail.drakovekmail.dvkarchive.gui.settings.SettingsBarGUI;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DComboBox;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DFrame;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DLabel;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DList;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DScrollPane;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;

/**
 * Class dealing with the main GUI.
 * Starts with the DVK Archive program.
 * 
 * @author Drakovek
 *
 */
public class StartGUI implements DActionEvent{
	
	/**
	 * ComboBox for selecting service categories
	 */
	private DComboBox cat_box;
	
	/**
	 * List for selecting services.
	 */
	private DList service_list;
	
	/**
	 * BaseGUI for getting UI settings
	 */
	private BaseGUI base_gui;
	
	/**
	 * Creates the Start GUI.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 */
	public StartGUI(BaseGUI base_gui) {
		this.base_gui = base_gui;
		this.base_gui.set_font("", 14, true);
		DFrame frame = new DFrame(this.base_gui, "dvk_archive");
		//CREATE SETTINGS BAR
		frame.getContentPane().add(new SettingsBarGUI(this.base_gui),
				BorderLayout.SOUTH);
		//CREATE CATEGORY PANEL
		this.cat_box = new DComboBox(
				this.base_gui, this,
				get_categories(true),
				"category");
		DLabel cat_lbl = new DLabel(this.base_gui, this.cat_box, "category");
		JPanel cat_pnl = this.base_gui.get_y_stack(cat_lbl, this.cat_box);
		//CREATE SERVICE PANEL
		this.service_list = new DList(this.base_gui, "service", false);
		update_services();
		DScrollPane serve_scr;
		serve_scr = new DScrollPane(this.service_list);
		DLabel serve_lbl = new DLabel(this.base_gui, this.service_list, "service");
		JPanel service_pnl = this.base_gui.get_y_stack(serve_lbl, 0, serve_scr, 1);
		//CREATE SIDE PANEL
		JPanel prog_pnl = this.base_gui.get_y_stack(cat_pnl, 0, service_pnl, 1);
		JPanel side_pnl = this.base_gui.get_spaced_panel(prog_pnl, 0, 1, true, true, true, false);
		frame.getContentPane().add(side_pnl, BorderLayout.WEST);
		//PACK AND CREATE FRAME
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	/**
	 * Returns a String array of available service categories.
	 * 
	 * @param use_language Whether to use language strings instead of IDs.
	 * @return String array of service categories
	 */
	public String[] get_categories(boolean use_language) {
		String[] categories = new String[2];
		categories[0] = "artist_hosting";
		categories[1] = "error_finding";
		//CHANGE TO LANGUAGE VALUES
		if(use_language) {
			for(int i = 0; i < categories.length; i++) {
				categories[i] = this.base_gui.get_language_string(categories[i]);
			}
		}
		return categories;
	}
	
	/**
	 * Returns a String array of available services for a given category.
	 * 
	 * @param category Given category ID
	 * @param use_language Whether to use language strings instead of IDs.
	 * @return String array of available services
	 */
	public String[] get_services(String category, boolean use_language) {
		String[] services = new String[0];
		switch(category) {
			case "artist_hosting":
				services = new String[4];
				services[0] = "deviantart";
				services[1] = "fur_affinity";
				services[2] = "inkbunny";
				services[3] = "transfur";
				break;
			case "error_finding":
				services = new String[3];
				services[0] = "same_ids";
				services[1] = "missing_media";
				services[2] = "unlinked_media";
				break;
		}
		//CHANGE TO LANGUAGE VALUES
		if(use_language) {
			for(int i = 0; i < services.length; i++) {
				services[i] = this.base_gui.get_language_string(services[i]);
			}
		}
		return services;
	}
	
	/**
	 * Updates the service list to reflect the current category.
	 */
	public void update_services() {
		String cat;
		int index = this.cat_box.getSelectedIndex();
		cat = get_categories(false)[index];
		String[] services = get_services(cat, true);
		this.service_list.setListData(services);
	}

	@Override
	public void event(String id) {
		switch(id) {
			case "category":
				update_services();
				break;
		}
	}
}
