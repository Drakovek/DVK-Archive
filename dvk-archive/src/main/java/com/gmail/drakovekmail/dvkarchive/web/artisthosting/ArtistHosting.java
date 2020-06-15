package com.gmail.drakovekmail.dvkarchive.web.artisthosting;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.gmail.drakovekmail.dvkarchive.processing.ArrayProcessing;
import com.google.common.io.Files;

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
	 * @param dvk_handler DvkHandler containing loaded Dvks
	 * @param domain Domain in which to check for artists
	 * @return List of Dvks from different artists of a domain
	 */
	public static ArrayList<Dvk> get_artists(
			DvkHandler dvk_handler,
			String domain) {
		if(dvk_handler == null) {
			return new ArrayList<>();
		}
		StringBuilder sql = new StringBuilder("SELECT * FROM ");
		sql.append(DvkHandler.DVKS);
		sql.append(" WHERE ");
		sql.append(DvkHandler.PAGE_URL);
		sql.append(" COLLATE NOCASE LIKE ? AND (");
		sql.append(DvkHandler.WEB_TAGS);
		sql.append(" IS NULL OR ");
		sql.append(DvkHandler.WEB_TAGS);
		sql.append(" COLLATE NOCASE NOT LIKE ?)");
		String[] params = {"%" + domain + "%", "%dvk&#58;single%"};
		//LIMIT TO OPENED DIRECTORIES
		sql.append(" GROUP BY ");
		sql.append(DvkHandler.ARTISTS);
		sql.append(" ORDER BY ");
		sql.append(DvkHandler.ARTISTS);
		sql.append(" COLLATE NOCASE ASC;");
		try(ResultSet rs = dvk_handler.sql_select(sql.toString(), params, true)) {
			ArrayList<Dvk> dvks = DvkHandler.get_dvks(rs);
			for(int i = 0; i < dvks.size(); i++) {
				dvks.get(i).set_dvk_file(dvks.get(i).get_dvk_file().getParentFile());
			}
			return dvks;
		}
		catch(SQLException e) {}
		return new ArrayList<>();
	}
	
	/**
	 * Gets the common directory of two given directories.
	 * Directory of which both given directories are subdirectories.
	 * 
	 * @param dir1 Directory 1
	 * @param dir2 Directory 2
	 * @return Common directory
	 */
	public static File get_common_directory(
			File dir1,
			File dir2) {
		if(dir1.equals(dir2)) {
			return dir1;
		}
		//GET FIRST FILE PATH
		File path = dir1;
		ArrayList<File> path1 = new ArrayList<>();
		while(path != null) {
			path1.add(path);
			path = path.getParentFile();
		};
		//GET SECOND FILE PATH
		path = dir2;
		ArrayList<File> path2 = new ArrayList<>();
		while(path != null) {
			path2.add(path);
			path = path.getParentFile();
		};
		//GET COMMON FILE
		for(int i = 0; i < path1.size(); i++) {
			for(int k = 0; k < path2.size(); k++) {
				if(path1.get(i).equals(path2.get(k))) {
					return path1.get(i);
				}
			}
		}
		return dir1;
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
		DTextField cap_txt = new DTextField(base_gui, this, "nothing");
		DLabel cap_lbl = new DLabel(base_gui, cap_txt, "captcha");
		JPanel cap_pnl = base_gui.get_x_stack(cap_lbl, 0, cap_txt, 1);
		JPanel full_cap_pnl = base_gui.get_y_stack(img_lbl, cap_pnl);
		//TEXT PANELS
		DTextField usr_txt = new DTextField(base_gui, this, "nothing");
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
			ImageIcon icon = new ImageIcon(
					captcha.getAbsolutePath());
			img_lbl.setIcon(icon);
			full_pnl = base_gui.get_spaced_panel(
					base_gui.get_y_stack(full_cap_pnl, btm_pnl));
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
	
	/**
	 * Moves a DVK file to a given directory if Dvk is a single download.
	 * 
	 * @param dvk DVK file to move
	 * @param directory Directory in which to move Dvk
	 * @return Dvk of moved DVK file
	 */
	public static Dvk move_dvk(Dvk dvk, File directory) {
		Dvk new_dvk = dvk;
		//MOVE FILES IF NECESSARY
		if(directory != null && 
				ArrayProcessing.contains(dvk.get_web_tags(), "dvk:single", false) && 
				!dvk.get_dvk_file().getParentFile().equals(directory)) {
			//SET DVK FILE
			File file = new File(directory, dvk.get_dvk_file().getName());
			dvk.get_dvk_file().delete();
			new_dvk.set_dvk_file(file);
			//MOVE MEDIA FILE
			file = new File(directory, dvk.get_media_file().getName());
			try {
				Files.move(dvk.get_media_file(), file);
			} catch (IOException e) {}
			new_dvk.set_media_file(file.getName());
			//MOVE SECONDARY FILE
			if(dvk.get_secondary_file() != null) {
				file = new File(directory, dvk.get_secondary_file().getName());
				try {
					Files.move(dvk.get_secondary_file(), file);
				} catch (IOException e) {}
				new_dvk.set_secondary_file(file.getName());
			}
		}
		new_dvk.write_dvk();
		return new_dvk;
	}
	
	/**
	 * Updates Dvk object of given DVK ID to include a given favorite tag if available.
	 * Returns updated Dvk object.
	 * 
	 * @param dvk_handler Used to search for Dvk with given ID
	 * @param artist Artist to use when adding favorite tag
	 * @param dvk_id Given DVK ID of Dvk to update
	 * @return Updated Dvk object, null if Dvk with given ID does not exist
	 */
	public static Dvk update_favorite(DvkHandler dvk_handler, String artist, String dvk_id) {
		StringBuilder sql = new StringBuilder("SELECT * FROM ");
		sql.append(DvkHandler.DVKS);
		sql.append(" WHERE ");
		sql.append(DvkHandler.DVK_ID);
		sql.append(" = ?;");
		String[] params = {dvk_id};
		try(ResultSet rs = dvk_handler.sql_select(sql.toString(), params, true)) {
			ArrayList<Dvk> dvks = DvkHandler.get_dvks(rs);
			if(dvks.size() > 0) {
				Dvk dvk = dvks.get(0);
				String[] tags = dvk.get_web_tags();
				if(!ArrayProcessing.contains(tags, "favorite:" + artist, false)) {
					String[] new_tags;
					if(tags == null) {
						new_tags = new String[1];
					}
					else {
						new_tags = new String[tags.length + 1];
						for(int k = 0; k < tags.length; k++) {
							new_tags[k] = tags[k];
						}
					}
					new_tags[new_tags.length - 1] = "Favorite:" + artist;
					dvk.set_web_tags(new_tags);
				}
				dvk.write_dvk();
				dvk_handler.set_dvk(dvk, dvk.get_sql_id());
				return dvk;
			}
		}
		catch(SQLException e) {}
		return null;
	}
	
	/**
	 * Returns Dvk objects that match one of a given list of DVK IDs.
	 * 
	 * @param dvk_handler DvkHandler with loaded Dvk objects
	 * @param ids IDs of Dvk objects to return
	 * @return Dvks matching one of the IDs
	 */
	public static ArrayList<Dvk> get_dvks_from_ids(DvkHandler dvk_handler, ArrayList<String> ids) {
		ArrayList<String> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder("SELECT * FROM ");
		sql.append(DvkHandler.DVKS);
		sql.append(" WHERE ");
		//ADD IDS
		int size = ids.size();
		if(size > 0) {
			sql.append("(");
			for(int id_num = 0; id_num < ids.size(); id_num++) {
				if(id_num > 0) {
					sql.append(" OR ");
				}
				sql.append(DvkHandler.DVK_ID);
				sql.append(" = ?");
				params.add(ids.get(id_num));
			}
			sql.append(")");
		}
		//GET DVKS
		ArrayList<Dvk> dvks;
		try(ResultSet rs = dvk_handler.sql_select(sql.toString(), params, true)) {
			dvks = DvkHandler.get_dvks(rs);
			return dvks;
		}
		catch(SQLException e) {
			return new ArrayList<>();
		}
	}
	
	@Override
	public void event(String id) {
		this.dialog.dispose();
	}
}
