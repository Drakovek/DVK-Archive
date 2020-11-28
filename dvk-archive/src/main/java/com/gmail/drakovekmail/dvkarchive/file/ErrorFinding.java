package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
	 * @return List of unlinked files
	 */
	public static ArrayList<File> get_unlinked_media(DvkHandler dvk_handler) {
		//INITIALIZE VARIABLES
		ArrayList<Dvk> dvks;
		ArrayList<String> params;
		ArrayList<File> unlinked = new ArrayList<>();
		ExtensionFilter filter = new ExtensionFilter(".dvk", false);
		//GET LIST OF THE DIRECTORIES LOADED IN THE DVK DIRECTORY
		ArrayList<File> dirs = dvk_handler.get_loaded_directories();
		//RUN THROUGH ALL SUB-DIRECTORIES
		for(int dir_num = 0; dir_num < dirs.size(); dir_num++) {
			//GET A LIST OF ALL THE DVK OBJECTS IN THE CURRENT DIRECTORY
			params = new ArrayList<>();
			params.add(dirs.get(dir_num).getAbsolutePath());
			dvks = dvk_handler.get_dvks('a', false, false);
			//GET A LIST OF ALL THE NON-DVK FILES IN THE CURRENT DIRECTORY
			ArrayList<File> files = new ArrayList<>(Arrays.asList(dirs.get(dir_num).listFiles(filter)));
			//REMOVE FILES FROM THE FILE LIST THAT ARE IN A DVK ENTRY
			int size = dvks.size();
			for(int dvk_num = 0; dvk_num < size; dvk_num++) {
				int index;
				File media = dvks.get(dvk_num).get_media_file();
				File second = dvks.get(dvk_num).get_secondary_file();
				//REMOVE MEDIA FILE IF IT EXISTS
				if(media != null) {
					index = files.indexOf(media);
					if(index != -1) {
						files.remove(index);
					}
				}
				//REMOVE SECONDARY FILE IF IT EXISTS
				index = files.indexOf(second);
				if(index != -1) {
					files.remove(index);
				}
			}
			//ADD THE REMAINING FILES THAT WEREN'T IN A DVK ENTRY TO THE UNLINKED LIST
			unlinked.addAll(files);
		}
		//RETURN THE LIST OF UNLINKED FILES
		Collections.sort(unlinked);
		return unlinked;
	}
	
	/**
	 * Returns list of Dvks missing their associated media file(s).
	 * 
	 * @param dvk_handler Contains Dvk objects to check
	 * @return Dvks with missing primary or secondary media
	 */
	public static ArrayList<File> get_missing_media_dvks(DvkHandler dvk_handler) {
		//INITIALIZE LIST OF DVKS WITH MISSING MEDIA
		ArrayList<File> missing = new ArrayList<>();
		//GET A LIST OF ALL THE DVK FILES LOADED BY THE GIVEN DVK HANDLER
		ArrayList<Dvk> dvks = dvk_handler.get_dvks('a', true, false);
		//CHECK EACH DVK OBJECT TO SEE IF THE LINKED MEDIA FILES EXIST
		File media;
		File secondary;
		int size = dvks.size();
		for(int dvk_num = 0; dvk_num < size; dvk_num++) {
			//GET THE MEDIA AND SECONDARY MEDIA FILES FROM THE CURRENT DVK
			media = dvks.get(dvk_num).get_media_file();
			secondary = dvks.get(dvk_num).get_secondary_file();
			//IF LINKED MEDIA DOESN'T EXIST, ADD TO THE MISSING LIST
			if(media == null || !media.exists() || (secondary != null && !secondary.exists())) {
				missing.add(dvks.get(dvk_num).get_dvk_file());
			}
		}
		//RETURNS LIST OF DVK FILES THAT HAVE INVALID LINKED MEDIA FILES
		Collections.sort(missing);
		return missing;
	}
	
	/**
	 * Returns a list of Dvks that share the same IDs.
	 * Files are grouped when they share the same DVK ID.
	 * 
	 * @param dvk_handler Contains Dvk objects to check
	 * @return List of Dvks that have identical IDs
	 */
	public static ArrayList<ArrayList<File>> get_same_ids(DvkHandler dvk_handler) {
		//INITIALIZE VARIABLE
		ArrayList<ArrayList<File>> same_ids = new ArrayList<>();
		//GET A LIST OF DVKS WITH IDS THAT ARE SHARED BY MULTIPLE DVK FILES
		StringBuilder extra = new StringBuilder();
		extra.append("GROUP BY ");
		extra.append(DvkHandler.DVK_ID);
		extra.append(" HAVING COUNT(");
		extra.append(DvkHandler.DVK_ID);
		extra.append(") > 1");
		ArrayList<Dvk> id_dvks = dvk_handler.get_dvks('a', false, false, null, null, extra.toString());
		//GET GROUPS OF DVKS THAT SHARE THE SAME IDS BASED ON THE IDS FOUND
		ArrayList<String> parameters;
		String where = DvkHandler.DVK_ID + "=?";
		for(int id_num = 0; id_num < id_dvks.size(); id_num++) {
			//GET ALL THE DVKS THAT SHARE THE CURRENTLY SELECTED DVK ID
			parameters = new ArrayList<>();
			parameters.add(id_dvks.get(id_num).get_dvk_id());
			ArrayList<Dvk> same_dvks = dvk_handler.get_dvks('a', true, false, where, parameters);
			//ADD FILES TO THE MAIN SAME_IDS ARRAYLIST
			ArrayList<File> files = new ArrayList<>();
			for(int dvk_num = 0; dvk_num < same_dvks.size(); dvk_num++) {
				files.add(same_dvks.get(dvk_num).get_dvk_file());
			}
			same_ids.add(files);
		}
		//RETURN LIST OF DVK FILES GROUPED TOGETHER IF THEY HAVE THE SAME DVK ID
		return same_ids;
	}
}
