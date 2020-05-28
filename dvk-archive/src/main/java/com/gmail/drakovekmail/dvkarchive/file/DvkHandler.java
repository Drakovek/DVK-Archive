package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import com.gmail.drakovekmail.dvkarchive.gui.StartGUI;
import com.gmail.drakovekmail.dvkarchive.processing.ArrayProcessing;

/**
 * Class containing methods for handling many Dvk objects.
 * 
 * @author Drakovek
 */
public class DvkHandler {
	
	/**
	 * Label for the SQLite table used for holding Dvk information
	 */
	public static final String DVKS = "dvks";
	
	/**
	 * Label for unique SQLite id
	 */
	public static final String SQL_ID = "sql_id";
	
	/**
	 * Label for the Dvk ID
	 */
	public static final String DVK_ID = "dvk_id";
	
	/**
	 * Label for the Dvk title
	 */
	public static final String TITLE = "title";
	
	/**
	 * Label for the Dvk contributing artists
	 */
	public static final String ARTISTS = "artists";
	
	/**
	 * Label for the Dvk's time published
	 */
	public static final String TIME = "time";
	
	/**
	 * Label for the Dvk's web tags
	 */
	public static final String WEB_TAGS = "web_tags";
	
	/**
	 * Label for the Dvk description
	 */
	public static final String DESCRIPTION = "description";
	
	/**
	 * Label for the Dvk page URL
	 */
	public static final String PAGE_URL = "page_url";
	
	/**
	 * Label for the Dvk direct URL
	 */
	public static final String DIRECT_URL = "direct_url";
	
	/**
	 * Label for the Dvk secondary media URL
	 */
	public static final String SECONDARY_URL = "secondary_url";
	
	/**
	 * Label for the directory in which the referenced Dvk's files reside
	 */
	public static final String DIRECTORY = "directory";
	
	/**
	 * Label for the Dvk's main filename
	 */
	public static final String DVK_FILE = "dvk_file";
	
	/**
	 * Label for the Dvk's referenced media filename
	 */
	public static final String MEDIA_FILE = "media_file";
	
	/**
	 * Label for the Dvk's secondary media filename
	 */
	public static final String SECONDARY_FILE = "secondary_file";
	
	/**
	 * Connection object for connecting to SQLite database
	 */
	private Connection connection;
	
	/**
	 * FilePrefs for getting directory in which to store SQLite database
	 */
	private FilePrefs prefs;
	
	/**
	 * List of directories opened when reading DVK files
	 */
	private File[] directories;
	
	/**
	 * Last modified time of the SQLite database holding Dvk info
	 */
	private long modified;
	
	/**
	 * Initializes DvkHandler with empty Dvk list.
	 * 
	 * @param prefs FilePrefs for getting directory in which to store SQLite database
	 */
	public DvkHandler(FilePrefs prefs) {
		this.prefs = prefs;
		this.connection = null;
		initialize_connection();
	}
	
	/**
	 * Initializes connection to SQLite database.
	 */
	public void initialize_connection() {
		close_connection();
		File db = new File(this.prefs.get_index_dir(), "dvk_archive.db");
		this.modified = 0L;
		if(db.exists()) {
			this.modified = db.lastModified();
		}
		String url = "jdbc:sqlite:" + db.getAbsolutePath();
		@SuppressWarnings("resource")
		Statement smt = null;
		try {
			//CREATE SQL STATEMENT
			this.connection = DriverManager.getConnection(url);
			smt = this.connection.createStatement();
			StringBuilder sql = new StringBuilder();
			sql.append("CREATE TABLE IF NOT EXISTS ");
			sql.append(DVKS);
			sql.append("(");
			sql.append(SQL_ID);
			sql.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
			sql.append(DVK_ID);
			sql.append(" TEXT NOT NULL, ");
			sql.append(TITLE);
			sql.append(" TEXT NOT NULL COLLATE NOCASE, ");
			sql.append(ARTISTS);
			sql.append(" TEXT NOT NULL, ");
			sql.append(TIME);
			sql.append(" TEXT NOT NULL DEFAULT '0000/00/00|00:00', ");
			sql.append(WEB_TAGS);
			sql.append(" TEXT, ");
			sql.append(DESCRIPTION);
			sql.append(" TEXT, ");
			sql.append(PAGE_URL);
			sql.append(" TEXT NOT NULL, ");
			sql.append(DIRECT_URL);
			sql.append(" TEXT, ");
			sql.append(SECONDARY_URL);
			sql.append(" TEXT, ");
			sql.append(DIRECTORY);
			sql.append(" TEXT, ");
			sql.append(DVK_FILE);
			sql.append(" TEXT NOT NULL, ");
			sql.append(MEDIA_FILE);
			sql.append(" TEXT, ");
			sql.append(SECONDARY_FILE);
			sql.append(" TEXT);");
			smt.execute(sql.toString());
		}
		catch(SQLException e) {
			e.printStackTrace();
			close_connection();
		}
		finally {
			if(smt != null) {
				try {
					smt.close();
				}
				catch(SQLException f) {}
			}
		}
	}
	
	/**
	 * Loads all the DVK files within the given directories.
	 * Includes sub-directories.
	 * 
	 * @param dirs Directories in which to search for files
	 * @param prefs FilePrefs for getting index directory
	 * @param start_gui Used for displaying progress.
	 * @param use_index Whether to load from index files
	 * @param check_new Whether to update list when loading from index
	 * @param save_index Whether to save index files
	 */
	public void read_dvks(final File[] dirs, StartGUI start_gui) {
		if(start_gui != null) {
			start_gui.append_console("", false);
			start_gui.append_console("reading_dvks", true);
			start_gui.get_main_pbar().set_progress(true, false, 0, 0);
		}
		this.directories = dirs;
		initialize_connection();
		File[] dvk_dirs = get_directories(dirs);
		int max = dvk_dirs.length;
		for(int i = 0; i < max; i++) {
			//SHOW PROGRESS
			if(start_gui != null) {
				start_gui.get_main_pbar().set_progress(false, true, i, max);
				//BREAK IF CANCELLED
				if(start_gui.get_base_gui().is_canceled()) {
					break;
				}
			}
			//ADD DIRECTORY
			update_directory(dvk_dirs[i]);
		}
	}
	
	/**
	 * Updates SQLite database to contain up-to-date information about Dvks in the given directory.
	 * 
	 * @param directory Directory in which to load DVK files
	 */
	public void update_directory(File directory) {
		//GET LIST OF DVK FILES
		ArrayList<String> dvks = new ArrayList<>();
		File[] files = directory.listFiles();
		int size = files.length;
		for(File file: files) {
			String name = file.getName();
			if(name.endsWith(".dvk")) {
				dvks.add(name);
			}
		}
		//CREATE SQL STATEMENT
		String path = directory.getAbsolutePath();
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(SQL_ID);
		sql.append(", ");
		sql.append(DVK_FILE);
		sql.append(" FROM ");
		sql.append(DVKS);
		sql.append(" WHERE ");
		sql.append(DIRECTORY);
		sql.append(" = '");
		sql.append(path);
		sql.append("';");
		try (ResultSet rs = get_sql_set(sql.toString())) {
			int id;
			File file = new File(this.prefs.get_index_dir(), "dvk_archive.db");
			ArrayList<Integer> delete = new ArrayList<>();
			while(rs.next()) {
				id = rs.getInt(SQL_ID);
				path = rs.getString(DVK_FILE);
				file = new File(directory, path);
				if(file.exists()) {
					if(this.modified < file.lastModified()) {
						//MODIFIES ENTRY IF DVK HAS BEEN MODIFIED
						Dvk dvk = new Dvk(file);
						set_dvk(dvk, id);
					}
					//REMOVES ENTRY FROM LIST OF NEW FILES
					dvks.remove(dvks.indexOf(path));
				}
				else {
					//ADD TO DELETE LIST IF FILE NO LONGER EXISTS
					delete.add(Integer.valueOf(id));
				}
			}
			//DELETE NON-EXISTANT ENTRIES
			size = delete.size();
			for(int i = 0; i < size; i++) {
				sql = new StringBuilder();
				sql.append("DELETE FROM ");
				sql.append(DVKS);
				sql.append(" WHERE ");
				sql.append(SQL_ID);
				sql.append(" = ");
				sql.append(Integer.toString(delete.get(i).intValue()));
				sql.append(";");
				Statement smt = this.connection.createStatement();
				smt.execute(sql.toString());
			}
			//ADDS NEW DVKS
			size = dvks.size();
			for(int i = 0; i < size; i++) {
				Dvk dvk = new Dvk(new File(directory, dvks.get(i)));
				if(dvk.get_title() != null) {
					add_dvk(dvk);
				}
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a ResultSet from a given SQLite statement.
	 * 
	 * @param statement SQL statement
	 * @return ResultSet returned from given statement
	 * @throws SQLException Exception triggered if SQL statement is invalid
	 */
	public ResultSet get_sql_set(String statement) throws SQLException {
		Statement smt = this.connection.createStatement();
		ResultSet rs = smt.executeQuery(statement);
		return rs;
	}
	
	/**
	 * Closes the connection to the SQLite database.
	 */
	public void close_connection() {
		if(this.connection != null) {
			try {
				this.connection.close();
			}
			catch(SQLException e) {}
		}
		this.connection = null;
	}
	
	/**
	 * Adds a given Dvk object to the SQLite database.
	 * 
	 * @param dvk Given Dvk
	 */
	public void add_dvk(Dvk dvk) {
		StringBuilder sql = new StringBuilder("INSERT INTO ");
		sql.append(DVKS);
		sql.append(" (");
		sql.append(DVK_ID);
		sql.append(',');
		sql.append(TITLE);
		sql.append(',');
		sql.append(ARTISTS);
		sql.append(',');
		sql.append(TIME);
		sql.append(',');
		sql.append(WEB_TAGS);
		sql.append(',');
		sql.append(DESCRIPTION);
		sql.append(',');
		sql.append(PAGE_URL);
		sql.append(',');
		sql.append(DIRECT_URL);
		sql.append(',');
		sql.append(SECONDARY_URL);
		sql.append(',');
		sql.append(DIRECTORY);
		sql.append(',');
		sql.append(DVK_FILE);
		sql.append(',');
		sql.append(MEDIA_FILE);
		sql.append(',');
		sql.append(SECONDARY_FILE);
		sql.append(") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);");
		try (PreparedStatement psmt = this.connection.prepareStatement(sql.toString())) {
			psmt.setString(1, dvk.get_id());
			psmt.setString(2, dvk.get_title());
			psmt.setString(3, ArrayProcessing.array_to_string(dvk.get_artists(), 0, true));
			psmt.setString(4, dvk.get_time());
			psmt.setString(5, ArrayProcessing.array_to_string(dvk.get_web_tags(), 0, true));
			psmt.setString(6, dvk.get_description());
			psmt.setString(7, dvk.get_page_url());
			psmt.setString(8, dvk.get_direct_url());
			psmt.setString(9, dvk.get_secondary_url());
			psmt.setString(10, dvk.get_dvk_file().getParentFile().getAbsolutePath());
			psmt.setString(11, dvk.get_dvk_file().getName());
			if(dvk.get_media_file() != null) {
				psmt.setString(12, dvk.get_media_file().getName());
			}
			if(dvk.get_secondary_file() != null) {
				psmt.setString(13, dvk.get_secondary_file().getName());
			}
			psmt.executeUpdate();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Replaces the info for a given SQL ID with info from a given Dvk object.
	 * 
	 * @param dvk Given Dvk object
	 * @param sql_id ID for specific Dvk info in SQLite database
	 */
	public void set_dvk(Dvk dvk, int sql_id) {
		StringBuilder sql = new StringBuilder("UPDATE ");
		sql.append(DVKS);
		sql.append(" SET ");
		sql.append(DVK_ID);
		sql.append(" = ?, ");
		sql.append(TITLE);
		sql.append(" = ?, ");
		sql.append(ARTISTS);
		sql.append(" = ?, ");
		sql.append(TIME);
		sql.append(" = ?, ");
		sql.append(WEB_TAGS);
		sql.append(" = ?, ");
		sql.append(DESCRIPTION);
		sql.append(" = ?, ");
		sql.append(PAGE_URL);
		sql.append(" = ?, ");
		sql.append(DIRECT_URL);
		sql.append(" = ?, ");
		sql.append(SECONDARY_URL);
		sql.append(" = ?, ");
		sql.append(DIRECTORY);
		sql.append(" = ?, ");
		sql.append(DVK_FILE);
		sql.append(" = ?, ");
		sql.append(MEDIA_FILE);
		sql.append(" = ?, ");
		sql.append(SECONDARY_FILE);
		sql.append(" = ? WHERE ");
		sql.append(SQL_ID);
		sql.append(" = ?");
		try(PreparedStatement psmt = this.connection.prepareStatement(sql.toString())) {
			psmt.setString(1, dvk.get_id());
			psmt.setString(2, dvk.get_title());
			psmt.setString(3, ArrayProcessing.array_to_string(dvk.get_artists(), 0, true));
			psmt.setString(4, dvk.get_time());
			psmt.setString(5, ArrayProcessing.array_to_string(dvk.get_web_tags(), 0, true));
			psmt.setString(6, dvk.get_description());
			psmt.setString(7, dvk.get_page_url());
			psmt.setString(8, dvk.get_direct_url());
			psmt.setString(9, dvk.get_secondary_url());
			psmt.setString(10, dvk.get_dvk_file().getParentFile().getAbsolutePath());
			psmt.setString(11, dvk.get_dvk_file().getName());
			if(dvk.get_media_file() != null) {
				psmt.setString(12, dvk.get_media_file().getName());
			}
			if(dvk.get_secondary_file() != null) {
				psmt.setString(13, dvk.get_secondary_file().getName());
			}
			psmt.setInt(14, sql_id);
			psmt.executeUpdate();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns number of loaded DVK files from opened directories.
	 * 
	 * @return Number of loaded DVK files
	 */
	public int get_size() {
		int size = 0;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(");
		sql.append(SQL_ID);
		sql.append(") FROM ");
		sql.append(DVKS);
		//LIMIT TO OPENED DIRECTORIES
		if(this.directories != null && this.directories.length > 0) {
			sql.append(" WHERE ");
			for(int i = 0; i < this.directories.length; i++) {
				if(i > 0) {
					sql.append(" OR ");
				}
				sql.append(DIRECTORY);
				sql.append(" LIKE '");
				sql.append(this.directories[i]);
				sql.append("%'");
			}
		}
		try (PreparedStatement ps = this.connection.prepareStatement(sql.toString())) {
			ResultSet rs = ps.executeQuery();
			size = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return size;
	}
	
	/**
	 * Returns Dvk for given SQL ID.
	 * 
	 * @param sql_id Given SQL ID
	 * @return Dvk for a given SQL ID
	 */
	public Dvk get_dvk(final int sql_id) {
		StringBuilder sql = new StringBuilder("SELECT * FROM ");
		sql.append(DVKS);
		sql.append(" WHERE ");
		sql.append(SQL_ID);
		sql.append(" = ");
		sql.append(sql_id);
		sql.append(';');
		try(ResultSet rs = this.get_sql_set(sql.toString())) {
			rs.next();
			Dvk dvk = new Dvk();
			dvk.set_id(rs.getString(DVK_ID));
			dvk.set_title(rs.getString(TITLE));
			dvk.set_artists(ArrayProcessing.string_to_array(rs.getString(ARTISTS)));
			dvk.set_time(rs.getString(TIME));
			dvk.set_web_tags(ArrayProcessing.string_to_array(rs.getString(WEB_TAGS)));
			dvk.set_description(rs.getString(DESCRIPTION));
			dvk.set_page_url(rs.getString(PAGE_URL));
			dvk.set_direct_url(rs.getString(DIRECT_URL));
			dvk.set_secondary_url(rs.getString(SECONDARY_URL));
			dvk.set_dvk_file(new File(rs.getString(DIRECTORY), rs.getString(DVK_FILE)));
			dvk.set_media_file(rs.getString(MEDIA_FILE));
			dvk.set_secondary_file(rs.getString(SECONDARY_FILE));
			return dvk;
		}
		catch(SQLException e) {}
		return new Dvk();
	}
	
	/**
	 * Returns a list of Dvks from a given section of the SQLite database.
	 * 
	 * @param start Start index (inclusive, starts at zero)
	 * @param end End index (exclusive, if -1, defaults to end of database)
	 * @param sort_type How to order Dvks, 'a' - alphabetical, 't' - time published, else - unordered
	 * @param group_artists Whether to group artists together when ordering Dvks 
	 * @param reverse Whether to reverse the order of Dvks
	 * @return List of Dvk objects
	 */
	public ArrayList<Dvk> get_dvks(
			int start,
			int end,
			char sort_type,
			boolean group_artists,
			boolean reverse) {
		StringBuilder sql = new StringBuilder("SELECT * FROM ");
		sql.append(DVKS);
		//LIMIT TO OPENED DIRECTORIES
		if(this.directories != null && this.directories.length > 0) {
			sql.append(" WHERE ");
			for(int i = 0; i < this.directories.length; i++) {
				if(i > 0) {
					sql.append(" OR ");
				}
				sql.append(DIRECTORY);
				sql.append(" LIKE '");
				sql.append(this.directories[i]);
				sql.append("%'");
			}
		}
		//ADD ORDER
		String direction = "ASC";
		if(reverse) {
			direction = "DESC";
		}
		if(sort_type == 't') {
			//SORT PRIMARILY BY TIME
			sql.append(" ORDER BY ");
			if(group_artists) {
				sql.append(ARTISTS);
				sql.append(' ');
				sql.append(direction);
				sql.append(", ");
			}
			sql.append(TIME);
			sql.append(' ');
			sql.append(direction);
			sql.append(", ");
			sql.append(TITLE);
			sql.append(' ');
			sql.append(direction);
		}
		else if(sort_type == 'a') {
			//SORT PRIMARILY BY TITLE
			sql.append(" ORDER BY ");
			if(group_artists) {
				sql.append(ARTISTS);
				sql.append(' ');
				sql.append(direction);
				sql.append(", ");
			}
			sql.append(TITLE);
			sql.append(' ');
			sql.append(direction);
			sql.append(", ");
			sql.append(TIME);
			sql.append(' ');
			sql.append(direction);
		}
		//ADD LIMIT
		if(end != -1) {
			int limit = end - start;
			sql.append(" LIMIT ");
			sql.append(limit);
		}
		if(start > 0) {
			sql.append(" OFFSET ");
			sql.append(start);
		}
		sql.append(";");
		try (ResultSet rs = get_sql_set(sql.toString())) {
			ArrayList<Dvk> dvks = new ArrayList<>();
			while(rs.next()) {
				Dvk dvk = new Dvk();
				dvk.set_id(rs.getString(DVK_ID));
				dvk.set_title(rs.getString(TITLE));
				dvk.set_artists(ArrayProcessing.string_to_array(rs.getString(ARTISTS)));
				dvk.set_time(rs.getString(TIME));
				dvk.set_web_tags(ArrayProcessing.string_to_array(rs.getString(WEB_TAGS)));
				dvk.set_description(rs.getString(DESCRIPTION));
				dvk.set_page_url(rs.getString(PAGE_URL));
				dvk.set_direct_url(rs.getString(DIRECT_URL));
				dvk.set_secondary_url(rs.getString(SECONDARY_URL));
				dvk.set_dvk_file(new File(rs.getString(DIRECTORY), rs.getString(DVK_FILE)));
				dvk.set_media_file(rs.getString(MEDIA_FILE));
				dvk.set_secondary_file(rs.getString(SECONDARY_FILE));
				dvks.add(dvk);
			}
			return dvks;
		}
		catch(SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	/**
	 * Returns an array of sub-directories within a given directory.
	 * Only lists directories that contain DVK files.
	 * 
	 * @param dir Directory to search
	 * @return Sub-directories of dir containing DVK files
	 */
	public static File[] get_directories(final File dir) {
		File[] dirs = {dir};
		return get_directories(dirs);
	}
	
	/**
	 * Returns an array of sub-directories within given directories.
	 * Only lists directories that contain DVK files.
	 * 
	 * @param dirs Directories to search
	 * @return Sub-directories of dirs containing DVK files
	 */
	public static File[] get_directories(final File[] dirs) {
		if(dirs == null) {
			return new File[0];
		}
		File[] files;
		ArrayList<File> all_dirs = new ArrayList<>();
		ArrayList<String> dvk_dirs = new ArrayList<>();
		for(File file: dirs) {
			if(file != null && file.exists()) {
				all_dirs.add(file);
			}
		}
		while(all_dirs.size() > 0) {
			//TODO NULL POINTER?
			//SEARCH FOR DVKS AND NEW DIRECTORIES
			boolean add_dirs = false;
			files = all_dirs.get(0).listFiles();
			for(File file: files) {
				if(file.isDirectory()) {
					all_dirs.add(file);
				}
				else if(file.getName().toLowerCase().endsWith(".dvk")) {
					add_dirs = true;
				}
			}
			//ADD DIRECTORY TO LIST IF CONTAINS DVK
			if(add_dirs) {
				dvk_dirs.add(all_dirs.get(0).getAbsolutePath());
			}
			all_dirs.remove(0);
		}
		//REMOVE DUPLICATE DIRECTORIES AND CONVERT TO FILE
		dvk_dirs = ArrayProcessing.clean_list(dvk_dirs);
		Collections.sort(dvk_dirs);
		File[] end_dirs = new File[dvk_dirs.size()];
		for(int i = 0; i < end_dirs.length; i++) {
			end_dirs[i] = new File(dvk_dirs.get(i));
		}
		return end_dirs;
	}
	
	/**
	 * Returns whether any loaded Dvk is linked to given file.
	 * 
	 * @param file Given file
	 * @return Whether any Dvks link to the given file
	 */
	public boolean contains_file(File file) {
		int size = get_size();
		for(int i = 0; i < size; i++) {
			File media = get_dvk(i).get_media_file();
			if(file.equals(media)) {
				return true;
			}
			File second = get_dvk(i).get_secondary_file();
			if(second != null && file.equals(second)) {
				return true;
			}
		}
		return false;
	}
}
