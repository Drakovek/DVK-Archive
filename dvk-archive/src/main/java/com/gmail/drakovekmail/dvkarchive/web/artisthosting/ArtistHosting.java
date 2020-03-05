package com.gmail.drakovekmail.dvkarchive.web.artisthosting;

import java.io.File;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DButton;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DDialog;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DLabel;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DPasswordField;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DTextField;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;

/**
 * Contains methods for getting info from artist hosting websites.
 * 
 * @author Drakovek
 */
public abstract class ArtistHosting implements DActionEvent {
	
	/**
	 * Dialog for getting user data
	 */
	private DDialog dialog;
	
	/**
	 * Returns a list of Dvks of different artist's work from a given domain.
	 * Used for determining which artists to download from and to which directories.
	 * 
	 * @param handler DvkHandler containing loaded Dvks
	 * @param domain Domain in which to check for artists
	 * @return List of Dvks from different artists of a domain
	 */
	public static ArrayList<Dvk> get_artists(DvkHandler handler, String domain) {
		if(handler == null) {
			return new ArrayList<>();
		}
		ArrayList<Dvk> dvks = new ArrayList<>();
		ArrayList<String> artists = new ArrayList<>();
		int size = handler.get_size();
		for(int i = 0; i < size; i++) {
			Dvk dvk = handler.get_dvk(i);
			String artist = dvk.get_artists()[0];
			String url = dvk.get_page_url();
			if(url.contains(domain) && !artists.contains(artist)) {
				artists.add(artist);
				dvks.add(dvk);
			}
		}
		return dvks;
	}
	
	/**
	 * Gets user info.
	 * 
	 * @param title Title of the dialog
	 * @param captcha File for captcha. If null, no CAPTCHA
	 * @return [0] - Username, [1] - Password, [2] - CAPTCHA
	 */
	public String[] get_user_info(String title, File captcha) {
		BaseGUI base_gui = new BaseGUI();
		//CAPTCHA
		JLabel img_lbl = new JLabel();
		img_lbl.setHorizontalAlignment(SwingConstants.HORIZONTAL);
		img_lbl.setVerticalAlignment(SwingConstants.HORIZONTAL);
		DTextField cap_txt = new DTextField(base_gui);
		DLabel cap_lbl = new DLabel(base_gui, cap_txt, "captcha");
		JPanel cap_pnl = base_gui.get_x_stack(cap_lbl, 0, cap_txt, 1);
		JPanel full_cap_pnl = base_gui.get_y_stack(img_lbl, cap_pnl);
		//TEXT PANELS
		DTextField usr_txt = new DTextField(base_gui);
		DLabel usr_lbl = new DLabel(base_gui, usr_txt, "username");
		JPanel usr_pnl = base_gui.get_x_stack(usr_lbl, 0, usr_txt, 1);
		DPasswordField pass_txt = new DPasswordField(base_gui);
		DLabel pass_lbl = new DLabel(base_gui, pass_txt, "password");
		JPanel pass_pnl = base_gui.get_x_stack(pass_lbl, 0, pass_txt, 1);
		JPanel text_pnl = base_gui.get_y_stack(usr_pnl, pass_pnl);
		//BOTTOM PANEL
		DButton log_btn = new DButton(base_gui, this, "login");
		JPanel btm_pnl = base_gui.get_y_stack(text_pnl, log_btn);
		//FULL PANEL
		JPanel full_pnl;
		if(captcha != null) {
			ImageIcon icon = new ImageIcon(captcha.getAbsolutePath());
			img_lbl.setIcon(icon);
			full_pnl = base_gui.get_spaced_panel(base_gui.get_y_stack(full_cap_pnl, btm_pnl));
		}
		else {
			full_pnl = base_gui.get_spaced_panel(btm_pnl);
		}
		this.dialog = new DDialog(null, full_pnl, title);
		this.dialog.setVisible(true);
		this.dialog = null;
		String[] info = new String[3];
		info[0] = usr_txt.getText();
		info[1] = pass_txt.get_text();
		info[2] = cap_txt.getText();
		return info;
	}
	
	@Override
	public void event(String id) {
		this.dialog.dispose();
	}
}
