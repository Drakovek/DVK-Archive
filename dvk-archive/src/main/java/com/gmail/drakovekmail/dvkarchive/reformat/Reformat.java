package com.gmail.drakovekmail.dvkarchive.reformat;


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
	 * @param start_gui Used for showing progress, if not null
	 */
	//TODO REINSTATE
	/*
	public static void reformat_dvks(
			DvkHandler dvk_handler,
			StartGUI start_gui) {
		ArrayList<Dvk> dvks = dvk_handler.get_dvks(0, -1, 'n', false, false);
		int size = dvks.size();
		for(int i = 0; i < size; i++) {
			//UPDATE PROGRESS
			if(start_gui != null) {
				start_gui.get_main_pbar().set_progress(false, true, i, size);
				//BREAK IF CANCELED
				if(start_gui.get_base_gui().is_canceled()) {
					break;
				}
			}
			//REFORMAT DVKS
			Dvk dvk = dvks.get(i);
			String desc = dvk.get_description();
			if(desc != null) {
				desc = DConnect.clean_element(desc, false);
				dvk.set_description(desc);
			}
			dvk.write_dvk();
			dvk.update_extensions();
		}
	}
	*/
	
	/**
	 * Renames DVKs and associated media to their default names.
	 * 
	 * @param dvk_handler Contains DVKs to be renamed
	 * @param start_gui Used for showing progress, if not null
	 */
	//TODO REINSTATE
	/*
	public static void rename_files(
			DvkHandler dvk_handler,
			StartGUI start_gui) {
		ArrayList<Dvk> dvks = dvk_handler.get_dvks(0, -1, 'n', false, false);
		int size = dvks.size();
		for(int i = 0; i < size; i++) {
			//UPDATE PROGRESS
			if(start_gui != null) {
				start_gui.get_main_pbar().set_progress(false, true, i, size);
				//BREAK IF CANCELED
				if(start_gui.get_base_gui().is_canceled()) {
					break;
				}
			}
			//RENAME FILES
			Dvk dvk = dvks.get(i);
			dvk.rename_files(dvk.get_filename(false), dvk.get_filename(true));
			dvk.update_extensions();
			dvk_handler.set_dvk(dvk, dvk.get_sql_id());
		}
	}
	*/
}
