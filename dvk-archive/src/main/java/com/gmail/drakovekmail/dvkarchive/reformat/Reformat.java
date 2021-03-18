package com.gmail.drakovekmail.dvkarchive.reformat;

import java.util.ArrayList;
import com.gmail.drakovekmail.dvkarchive.file.Dvk;
import com.gmail.drakovekmail.dvkarchive.file.DvkHandler;
import com.gmail.drakovekmail.dvkarchive.processing.HtmlProcessing;

/**
 * Methods for reformatting Dvks and associated media.
 * 
 * @author Drakovek
 */
public class Reformat {
	
	/**
	 * Reformats DVKs to fit the current formatting standard.
	 * 
	 * @param dvk_handler Contains DVKs to be formatted
	 */
	public static void reformat_dvks(DvkHandler dvk_handler) {
		//GET LIST OF DVKS
		ArrayList<Dvk> dvks = dvk_handler.get_dvks('a', false, false);
		//REFORMAT EACH DVK FILE
		int size = dvks.size();
		for(int dvk_num = 0; dvk_num < size; dvk_num++) {
			Dvk dvk = dvks.get(dvk_num);
			//CLEAN UP HTML IN DVK DESCRIPTIONS
			String desc = dvk.get_description();
			if(desc != null) {
				desc = HtmlProcessing.clean_element(desc, false);
				dvk.set_description(desc);
			}
			//WRITE DVK
			dvk.write_dvk();
			//ENSURE EXTENSIONS ARE CORRECT
			dvk.update_extensions();
			//UPDATE DVK INFO IN THE DVK HANDLER DATABASE
			dvk_handler.set_dvk(dvk, dvk.get_sql_id());
		}
	}
	
	/**
	 * Renames DVKs and associated media to their default names.
	 * 
	 * @param dvk_handler Contains DVKs to be renamed
	 */
	public static void rename_files(DvkHandler dvk_handler) {
		//GET LIST OF DVKS
		ArrayList<Dvk> dvks = dvk_handler.get_dvks('a', false, false);
		//RENAME EACH DVK FILE
		int size = dvks.size();
		for(int dvk_num = 0; dvk_num < size; dvk_num++) {
			//RENAME DVK FILE AND ASSOCIATED MEDIA
			Dvk dvk = dvks.get(dvk_num);
			dvk.rename_files(dvk.get_filename(false, null), dvk.get_filename(true, null));
			//ENSURE EXTENSIONS ARE CORRECT
			dvk.update_extensions();
			//UPDATE DVK INFO IN THE DVK HANDLER DATABASE
			dvk_handler.set_dvk(dvk, dvk.get_sql_id());
		}
	}
}
