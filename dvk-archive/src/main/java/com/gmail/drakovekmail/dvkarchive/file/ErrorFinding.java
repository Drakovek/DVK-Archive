package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;

/**
 * Class containing methods for finding errors in DVK files.
 * 
 * @author Drakovek
 */
public class ErrorFinding {
	
	/**
	 * Returns list of files not linked to a DVK file.
	 * Only returns files in the same directories as DVK files.
	 * 
	 * @param prefs FilePrefs for getting index settings
	 * @param directories Directories in which to search for files
	 * @param start_gui Used for displaying progress and results
	 * @return List of unlinked files
	 */
	public static ArrayList<File> get_unlinked_media(
			FilePrefs prefs,
			File[] directories,
			StartGUI start_gui) {
		try(DvkHandler handler = new DvkHandler(prefs)) {
			File[] dirs = new File[0];
			if(start_gui == null || !start_gui.get_base_gui().is_canceled()) {
				dirs = DvkHandler.get_directories(directories);
			}
			ArrayList<File> missing = new ArrayList<>();
			for(int i = 0; i < dirs.length; i++) {
				//BREAK IF CANCELLED
				if(start_gui != null && start_gui.get_base_gui().is_canceled()) {
					break;
				}
				//UPDATE PROGRESS
				if(start_gui != null) {
					start_gui.get_main_pbar().set_progress(false, true, i, dirs.length);
				}
				//LOAD DIRECTORY
				File[] files = {dirs[i]};
				handler.read_dvks(files, null);
				files = dirs[i].listFiles();
				Arrays.sort(files);
				for(File file: files) {
					//CHECK IF FILE IS MISSING
					if(!file.isDirectory()
							&& !file.getName().endsWith(".dvk")
							&& !missing.contains(file)
							&& !handler.contains_file(file)) {
						missing.add(file);
						//PRINT PATH
						if(start_gui != null) {
							start_gui.append_console(file.getAbsolutePath(), false);
						}
					}
				}
			}
			return missing;
		}
		catch(DvkException e) {}
		return new ArrayList<>();
	}
	
	/**
	 * Returns list of Dvks missing their associated media file(s).
	 * 
	 * @param dvk_handler Contains Dvk objects to check
	 * @param start_gui Used to show progress if not null
	 * @return Dvks with missing primary or secondary media
	 */
	public static ArrayList<File> get_missing_media_dvks(
			DvkHandler dvk_handler,
			StartGUI start_gui) {
		ArrayList<Dvk> dvks = dvk_handler.get_dvks(0, -1, 'a', true, false);
		int size = dvks.size();
		ArrayList<File> files = new ArrayList<>();
		for(int i = 0; i < size; i++) {
			//UPDATE PROGRESS
			if(start_gui != null) {
				start_gui.get_main_pbar().set_progress(false, true, i, size);
				//BREAK IF CANCELED
				if(start_gui.get_base_gui().is_canceled()) {
					break;
				}
			}
			Dvk dvk = dvks.get(i);
			if(!dvk.get_media_file().exists()
					|| (dvk.get_secondary_file() != null 
					&& !dvk.get_secondary_file().exists())) {
				files.add(dvk.get_dvk_file());
				//PRINT
				if(start_gui != null) {
					start_gui.append_console(dvk.get_dvk_file().getAbsolutePath(), false);
				}
			}
		}
		return files;
	}
	
	/**
	 * Returns a list of Dvks that share the same IDs.
	 * 
	 * @param dvk_handler Contains Dvk objects to check
	 * @param start_gui Used to show progress if not null
	 * @return List of Dvks that have identical IDs
	 */
	public static ArrayList<File> get_same_ids(
			DvkHandler dvk_handler,
			StartGUI start_gui) {
		//TODO SHOW PROGRESS AND ALLOW CANCELLING
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(DvkHandler.DVK_ID);
		sql.append(" FROM ");
		sql.append(DvkHandler.DVKS);
		sql.append(" GROUP BY ");
		sql.append(DvkHandler.DVK_ID);
		sql.append(" HAVING COUNT(");
		sql.append(DvkHandler.DVK_ID);
		sql.append(") > 1 ORDER BY ");
		sql.append(DvkHandler.ARTISTS);
		sql.append(", ");
		sql.append(DvkHandler.TITLE);
		sql.append(';');
		try (ResultSet rs1 = dvk_handler.get_sql_set(sql.toString())) {
			File file;
			ArrayList<File> files = new ArrayList<>();
			sql = new StringBuilder("SELECT ");
			sql.append(DvkHandler.DIRECTORY);
			sql.append(", ");
			sql.append(DvkHandler.DVK_FILE);
			sql.append(" FROM ");
			sql.append(DvkHandler.DVKS);
			sql.append(" WHERE ");
			sql.append(DvkHandler.DVK_ID);
			sql.append(" = '");
			String start = sql.toString();
			while(rs1.next()) {
				sql = new StringBuilder(start);
				sql.append(rs1.getString(DvkHandler.DVK_ID));
				sql.append("' ORDER BY ");
				sql.append(DvkHandler.TITLE);
				sql.append(";");
				try(ResultSet rs2 = dvk_handler.get_sql_set(sql.toString())) {
					while(rs2.next()) {
						file = new File(rs2.getString(DvkHandler.DIRECTORY),
								rs2.getString(DvkHandler.DVK_FILE));
						files.add(file);
					}
				}
				catch(SQLException f) {}
			}
			return files;
		}
		catch(SQLException e) {}
		return new ArrayList<>();
	}
}
