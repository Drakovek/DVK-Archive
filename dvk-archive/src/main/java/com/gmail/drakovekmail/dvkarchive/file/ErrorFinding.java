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
	 * @param dvk_handler DvkHandler with loaded Dvk objects
	 * @param directories Directories in which to search for files
	 * @param start_gui Used for displaying progress and results
	 * @return List of unlinked files
	 */
	public static ArrayList<File> get_unlinked_media(
			DvkHandler dvk_handler,
			File[] directories,
			StartGUI start_gui) {
		int index;
		File file;
		File[] dirs = new File[0];
		if(start_gui == null || !start_gui.get_base_gui().is_canceled()) {
			dirs = DvkHandler.get_directories(directories, true);
		}
		ArrayList<File> missing = new ArrayList<>();
		//CREATE MAIN SQL COMMAND
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(DvkHandler.MEDIA_FILE);
		sql.append(", ");
		sql.append(DvkHandler.SECONDARY_FILE);
		sql.append(" FROM ");
		sql.append(DvkHandler.DVKS);
		sql.append(" WHERE ");
		sql.append(DvkHandler.DIRECTORY);
		sql.append(" = ?;");
		String[] params = new String[1];
		//RUN THROUGH DIRECTORIES
		for(int i = 0; i < dirs.length; i++) {
			//BREAK IF CANCELLED
			if(start_gui != null && start_gui.get_base_gui().is_canceled()) {
				break;
			}
			//UPDATE PROGRESS
			if(start_gui != null) {
				start_gui.get_main_pbar().set_progress(false, true, i, dirs.length);
			}
			//GET NON-DVKS IN DIRECTORY
			File[] files = {dirs[i]};
			dvk_handler.read_dvks(files, null);
			files = dirs[i].listFiles(new ExtensionFilter(".dvk", false));
			Arrays.parallelSort(files);
			ArrayList<File> non_dvks = new ArrayList<>();
			for(File non_dvk: files) {
				non_dvks.add(non_dvk);
			}
			//REMOVE FILES
			params[0] = dirs[i].getAbsolutePath();
			try(ResultSet rs = dvk_handler.get_sql_set(sql.toString(), params)) {
				while(rs.next()) {
					try {
						file = new File(dirs[i], rs.getString(DvkHandler.MEDIA_FILE));
						index = non_dvks.indexOf(file);
						if(index != -1) {
							non_dvks.remove(index);
						}
					}
					catch(NullPointerException f) {}
					try {
						file = new File(dirs[i], rs.getString(DvkHandler.SECONDARY_FILE));
						index = non_dvks.indexOf(file);
						if(index != -1) {
							non_dvks.remove(index);
						}
					}
					catch(NullPointerException f) {}
				}
			}
			catch(SQLException e) {}
			//ADD MISSING FILES TO MISSING LIST
			missing.addAll(non_dvks);
			if(start_gui != null) {
				for(int missingno = 0; missingno < non_dvks.size(); missingno++) {
					start_gui.append_console(non_dvks.get(missingno).getAbsolutePath(), false);
				}
			}
		}
		return missing;
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
		//GET LIST OF DUPLICATED DVK IDS
		ArrayList<String> ids = new ArrayList<>();
		try (ResultSet rs = dvk_handler.get_sql_set(sql.toString(), new String[0])) {
			while(rs.next()) {
				//BREAK IF CANCELLED
				if(start_gui != null && start_gui.get_base_gui().is_canceled()) {
					break;
				}
				ids.add(rs.getString(DvkHandler.DVK_ID));
			}
		}
		catch(SQLException e) {}
		File file;
		boolean first;
		ArrayList<File> files = new ArrayList<>();
		sql = new StringBuilder("SELECT ");
		sql.append(DvkHandler.DIRECTORY);
		sql.append(", ");
		sql.append(DvkHandler.DVK_FILE);
		sql.append(" FROM ");
		sql.append(DvkHandler.DVKS);
		sql.append(" WHERE ");
		sql.append(DvkHandler.DVK_ID);
		sql.append(" = ? COLLATE NOCASE ORDER BY ");
		sql.append(DvkHandler.TITLE);
		sql.append(';');
		String[] params = new String[1];
		int size = ids.size();
		for(int i = 0; i < size; i++) {
			params[0] = ids.get(i);
			try(ResultSet rs = dvk_handler.get_sql_set(sql.toString(), params)) {
				//BREAK IF CANCELLED
				if(start_gui != null) {
					start_gui.get_main_pbar().set_progress(false, true, i, size);
					if(start_gui.get_base_gui().is_canceled()) {
						break;
					}
				}
				first = true;
				while(rs.next()) {
					file = new File(rs.getString(DvkHandler.DIRECTORY),
							rs.getString(DvkHandler.DVK_FILE));
					//PRINT FILE
					if(start_gui != null) {
						if(first) {
							start_gui.append_console(file.getAbsolutePath(), false);
						}
						else {
							start_gui.append_console("    " + file.getAbsolutePath(), false);
						}
					}
					first = false;
					files.add(file);
				}
			}
			catch(SQLException f) {
				return new ArrayList<>();
			}
		}
		return files;
	}
}
