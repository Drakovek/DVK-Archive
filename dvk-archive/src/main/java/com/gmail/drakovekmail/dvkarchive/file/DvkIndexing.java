package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class for creating and reading index files for faster reading of DvkDirectory objects.
 * 
 * @author Drakovek
 */
public class DvkIndexing {
	
	/**
	 * Name of the index list JSON
	 */
	public final static String INDEX_LIST = "indexes.json";
	
	/**
	 * Directory in which to store index files and the index list JSON.
	 */
	private File index_directory;
	
	/**
	 * List containing pairs of index files and their associated directory
	 * For each File[]:
	 * [0] - Index file
	 * [1] - Directory index file references
	 */
	private ArrayList<File[]> index_list;
	
	/**
	 * Initializes the DvkIndexing object.
	 * 
	 * @param directory Main index directory
	 */
	public DvkIndexing(File directory) {
		set_index_directory(directory);
	}
	
	/**
	 * Returns a DvkDirectory for a given directory.
	 * Loads from an index file if specified.
	 * 
	 * @param dir Directory to load from
	 * @param use_index Whether to use index file to load
	 * @param check_new Whether to check for file updates if loading from index
	 * @param save_index Whether to save the DvkDirectory to an index file
	 * @return DvkDirectory object for dir
	 */
	public DvkDirectory get_dvk_directory(
			File dir,
			boolean use_index,
			boolean check_new,
			boolean save_index) {
		boolean load = !use_index;
		//GET INDEX FILE
		File file = new File("");
		if(use_index || save_index) {
			int index = index_of_directory(dir);
			file = get_index_list().get(index)[0];
		}
		//LOAD DVK DIRECTORY FROM INDEX
		DvkDirectory dvk_directory = new DvkDirectory();
		if(use_index) {
			@SuppressWarnings("resource")
			FileInputStream fis = null;
			@SuppressWarnings("resource")
			ObjectInputStream ois = null;
			try {
				fis = new FileInputStream(file);
				ois = new ObjectInputStream(fis);
				dvk_directory = (DvkDirectory)ois.readObject();
				//UPDATE DIRECTORY
				if(check_new) {
					dvk_directory.update_directory(file.lastModified());
				}
			}
			catch(Exception e) {
				load = true;
			}
			finally {
				if(ois != null) {
					try {
						ois.close();
					}
					catch (IOException e) {}
				}
				if(fis != null) {
					try {
						fis.close();
					}
					catch (IOException e) {}
				}
			}
		}
		//LOAD NORMALLY IF LOADING INDEX FAILED
		if(load) {
			dvk_directory = new DvkDirectory();
			dvk_directory.read_dvks(dir);
		}
		//SAVE DVK DIRECTORY INDEX
		if(save_index) {
			save_index(file, dvk_directory);
		}
		return dvk_directory;
	}
	
	/**
	 * Saves a DvkDirectory object to a serialized index file.
	 * 
	 * @param file Index file
	 * @param dvk_directory DvkDirectory
	 */
	private static void save_index(File file, DvkDirectory dvk_directory) {
		file.delete();
		@SuppressWarnings("resource")
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(dvk_directory);
		} catch (IOException e) {
			file.delete();
		}
		finally {
			if(fos != null) {
				try {
					fos.close();
				}
				catch (IOException e) {}
			}
			if(oos != null) {
				try {
					oos.close();
				} catch (IOException e) {}
			}
		}
		fos = null;
		oos = null;	
	}
	
	/**
	 * Sets the index directory.
	 * 
	 * @param directory Index directory
	 */
	public void set_index_directory(File directory) {
		if(directory != null) {
			this.index_directory = directory;
			read_index_list();
		}
	}
	
	/**
	 * Returns the index directory.
	 * 
	 * @return Index directory
	 */
	public File get_index_directory() {
		return this.index_directory;
	}
	
	/**
	 * Reads the index list JSON file.
	 * Uses name of INDEX_LIST in the index directory.
	 */
	public void read_index_list() {
		//READ JSON TEXT
		this.index_list = new ArrayList<>();
		File json_file = new File(get_index_directory(),
				INDEX_LIST);
		String source = InOut.read_file(json_file);
		try {
			JSONObject json = new JSONObject(source);
			//CHECK IF INDEX LIST
			if(json.getString("file_type").equals("dvk-index")) {
				//ADD ALL PAIRS
				JSONArray pairs = json.getJSONArray("pairs");
				for(int i = 0; i < pairs.length(); i++) {
					JSONArray pair = pairs.getJSONArray(i);
					File index = new File(pair.getString(0));
					File dir = new File(pair.getString(1));
					add_to_index_list(index, dir);
				}
			}
		}
		catch(JSONException e) {
			this.index_list = new ArrayList<>();
		}
		clean_index_list();
		clean_index_directory();
	}
	
	/**
	 * Writes the index list to a JSON file.
	 * Uses name of INDEX_LIST in the index directory.
	 */
	public void write_index_list() {
		clean_index_list();
		File json_file = new File(get_index_directory(),
				INDEX_LIST);
		JSONObject json = new JSONObject();
		JSONArray pairs = new JSONArray();
		for(File[] item: get_index_list()) {
			String[] array = {item[0].getAbsolutePath(),
					item[1].getAbsolutePath()};
			JSONArray pair = new JSONArray(array);
			pairs.put(pair);
		}
		json.put("file_type", "dvk-index");
		json.put("pairs", pairs);
		InOut.write_file(json_file, json.toString(4));
	}
	
	/**
	 * Adds an index file -> directory pair to the index list.
	 * 
	 * @param index Index file
	 * @param directory Linked directory
	 */
	public void add_to_index_list(File index, File directory) {
		if(index != null && directory != null) {
			File[] pair = {index, directory};
			this.index_list.add(pair);
		}
	}
	
	/**
	 * Removes all non-existent index files and
	 * linked directories from the index list.
	 */
	public void clean_index_list() {
		for(int i = 0; i < get_index_list().size(); i++) {
			File index = get_index_list().get(i)[0];
			File dir = get_index_list().get(i)[1];
			if(!index.exists() || index.isDirectory() || !dir.isDirectory()) {
				this.index_list.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * Removes all files in the index directory that are not in the index list.
	 * Does not delete the index list JSON file.
	 */
	public void clean_index_directory() {
		try
		{
			File[] files = get_index_directory().listFiles();
			for(File file: files) {
				if(file.getName().endsWith(".ser")
						&& index_of_index(file) == -1) {
					file.delete();
				}
			}
		}
		catch(NullPointerException e) {}
	}
	
	/**
	 * Returns the index list.
	 * 
	 * @return Index list
	 */
	public ArrayList<File[]> get_index_list() {
		return this.index_list;
	}
	
	/**
	 * Returns the index of a given index file in the index list.
	 * If file is not in list, returns -1.
	 * 
	 * @param file Index file to search for
	 * @return Index of file
	 */
	public int index_of_index(File file) {
		for(int i = 0; i < get_index_list().size(); i++) {
			if(file.equals(get_index_list().get(i)[0])) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the index of a given linked directory in the index list.
	 * If directory is not referenced in index list, creates a new entry.
	 * 
	 * @param dir Directory to search for
	 * @return Index of dir
	 */
	public int index_of_directory(File dir) {
		for(int i = 0; i < get_index_list().size(); i++) {
			if(dir.equals(get_index_list().get(i)[1])) {
				return i;
			}
		}
		int num = 1;
		File file = null;
		do {
			file = new File(get_index_directory(), Integer.toString(num) + ".ser");
			num++;
		} while (index_of_index(file) != -1);
		add_to_index_list(file, dir);
		return get_index_list().size() - 1;
	}
}
