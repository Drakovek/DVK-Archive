package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import com.gmail.drakovekmail.dvkarchive.processing.ArrayProcessing;
import com.gmail.drakovekmail.dvkarchive.processing.StringProcessing;

/**
 * Class containing methods for handling many Dvk objects.
 * 
 * @author Drakovek
 */
public class DvkHandler implements AutoCloseable {

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
	 * Label for the Dvk title formatted to sort alpha-numerically
	 */
	public static final String SORT_TITLE = "sort_title";
	
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
	 * Config class for getting directory for the database file.
	 */
	private Config config;
	
	/**
	 * ArrayList of directories to limit database searches to.
	 */
	private ArrayList<File> directories;
	
	/**
	 * Last modified time of the SQLite database holding Dvk info
	 */
	private long modified;
	
	/**
	 * File holding the SQLite database
	 */
	private File database;
	
	/**
	 * Initializes DvkHandler with empty Dvk list.
	 *
	 * @param config Config object for getting directory in which to store SQLite database
	 * @exception DvkException DvkException
	 */
	public DvkHandler(Config config) throws DvkException {
		this.config = config;
		this.connection = null;
		this.directories = new ArrayList<>();
		initialize_connection();
	}
	
	/**
	 * Initializes connection to SQLite database.
	 *
	 * @exception DvkException DvkException
	 */
	public void initialize_connection() throws DvkException {
		//CLOSE DATABASE IF CURRENTLY OPEN
		close();
		//SET THE LAST MODIFIED VALUE OF THE DATABASE FILE
		this.database = new File(this.config.get_config_directory(), "dvk_archive.db");
		this.modified = 0L;
		if(this.database.exists()) {
			this.modified = this.database.lastModified();
		}
		//OPEN SQLITE DATABASE FILE
		String url = "jdbc:sqlite:" + this.database.getAbsolutePath();
		try {
			this.connection = DriverManager.getConnection(url);
			this.connection.setAutoCommit(true);
		}
		catch(SQLException e) {
			close();
		}
		//ADD TABLE IF IT DOESN'T CURRENTLY EXIST
		try(Statement statement = this.connection.createStatement()) {
			StringBuilder sql = new StringBuilder();
			sql.append("CREATE TABLE IF NOT EXISTS ");
			sql.append(DVKS);
			sql.append("(");
			sql.append(SQL_ID);
			sql.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
			sql.append(DVK_ID);
			sql.append(" TEXT NOT NULL, ");
			sql.append(TITLE);
			sql.append(" TEXT NOT NULL, ");
			sql.append(SORT_TITLE);
			sql.append(" TEXT NOT NULL, ");
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
			statement.execute(sql.toString());
		}
		catch(SQLException e) {
			close();
			delete_database();
		}
	}
	
	/**
	 * Reads all the DVK files in a given directory and loads them in the SQLite database.
	 * Updates any DVK entries in the database for which the matching DVK file has been modified.
	 * Skips over DVK entries already in the database that don't need to be updated.
	 * 
	 * @param dir Directory from which to load DVK files
	 */
	public void read_dvks(File dir) {
		try {
			//INITIALIZE THE DATABASE
			initialize_connection();
			remove_duplicates();
			//GET AND UPDATE SUBDIRECTORIES WITH DVK FILES
			this.directories = get_directories(dir, true);
			int size = this.directories.size();
			for(int dir_num = 0; dir_num < size; dir_num++) {
				update_directory(this.directories.get(dir_num));
			}
			//CREATE STATEMENT TO GROUP DVKS BY DIRECTORY
			StringBuilder statement = new StringBuilder();
			statement.append("SELECT ");
			statement.append(DIRECTORY);
			statement.append(" FROM ");
			statement.append(DVKS);
			statement.append(" GROUP BY ");
			statement.append(DIRECTORY);
			statement.append(";");
			//GET LIST OF DIRECTORIES IN THE DATABASE THAT NO LONGER EXIST
			ArrayList<String> nix = new ArrayList<>();
			try(PreparedStatement ps = this.connection.prepareStatement(statement.toString())) {
				try(ResultSet rs = ps.executeQuery()) {
					String filename;
					while(rs.next()) {
						filename = rs.getString(DIRECTORY);
						//IF DIRECTORY DOESN'T EXIST
						if(!new File(filename).exists()) {
							nix.add(filename);
						}
					}
				}
				catch(SQLException sql_exc_int) {
					throw new SQLException();
				}
			}
			catch(SQLException sql_exc) {
				throw new DvkException();
			}
			//CREATE STATEMENT FOR GETTING ALL DVK ITEMS FOR A GIVEN DIRECTORY IN THE DATABASE
			statement = new StringBuilder();
			statement.append("SELECT ");
			statement.append(SQL_ID);
			statement.append(" FROM ");
			statement.append(DVKS);
			statement.append(" WHERE ");
			statement.append(DIRECTORY);
			statement.append(" = ?;");
			//DELETE ALL ENTRIES FROM NON-EXISTANT DIRECTORIES FROM THE DATABASE
			size = nix.size();
			for(int dir_num = 0; dir_num < size; dir_num++) {
				try(PreparedStatement ps = this.connection.prepareStatement(statement.toString())) {
					//TURN OFF AUTO-COMMIT TO DELETE ENTRIES QUICKLY
					this.connection.setAutoCommit(false);
					//DELETE EVERY ENTRY WITH A NON-EXISTANT DIRECTORY
					ps.setString(1, nix.get(dir_num));
					try(ResultSet rs = ps.executeQuery()) {
						while(rs.next()) {
							delete_dvk(rs.getInt(SQL_ID));
						}
					}
					catch(SQLException sql_int) {
						throw new SQLException();
					}
					//COMMIT TO DATABASE, THEN TURN AUTO-COMMIT BACK ON
					this.connection.commit();
					this.connection.setAutoCommit(true);
				}
				catch(SQLException sql_exc) {
					try {
						//ROLLBACK ANY DELETION COMMITS, THEN SET AUTO-COMMIT BACK ON
						this.connection.rollback();
						this.connection.setAutoCommit(true);
					}
					catch(SQLException sql_exc_int) {}
					throw new DvkException();
				}
			}
		}
		catch(DvkException dvk_exc) {
			//CLOSE AND DELETE DATABASE ON EXCEPTION
			try {
				close();
				delete_database();
			}
			catch(DvkException dvk_exc_2) {}
		}
	}

	/**
	 * Updates DVK entries from the SQLite database from a given directory.
	 * Adds new DVK files and deletes DVK entries for files that no longer exist.
	 * Updates DVK entries for files that have been modified.
	 * 
	 * @param dir Directory from which to update DVK file entries
	 */
	private void update_directory(File dir) {
		//GET LIST OF DVK FILES IN THE GIVEN DIRECTORY
		File[] files = dir.listFiles(new ExtensionFilter(".dvk", true));
		ArrayList<String> dvks = new ArrayList<>();
		for(File file: files) {
			dvks.add(file.getName());
		}
		//CREATE STATEMENT TO GET ALL ENTRIES FROM THE DATABASE FROM THE GIVEN DIRECTORY
		StringBuilder statement = new StringBuilder();
		statement.append("SELECT ");
		statement.append(SQL_ID);
		statement.append(", ");
		statement.append(DVK_FILE);
		statement.append(" FROM ");
		statement.append(DVKS);
		statement.append(" WHERE ");
		statement.append(DIRECTORY);
		statement.append(" = ?;");
		//EXECUTE STATEMENT TO GET ENTRIES FROM GIVEN DIRECTORY
		try(PreparedStatement ps = this.connection.prepareStatement(statement.toString())) {
			ps.setString(1, dir.getAbsolutePath());
			try(ResultSet rs = ps.executeQuery()) {
				//RUN THROUGH ENTRIES TO SEE WHICH ENTRIES NEED TO BE DELETED OR MODIFIED
				int sql_id;
				String filename;
				File dvk_file;
				ArrayList<Integer> delete = new ArrayList<>();
				this.connection.setAutoCommit(false);
				while(rs.next()) {
					sql_id = rs.getInt(SQL_ID);
					filename = rs.getString(DVK_FILE);
					dvk_file = new File(dir, filename);
					if(dvk_file.exists()) {
						if(this.modified < dvk_file.lastModified()) {
							//UPDATES ENTRY IF DVK FILE HAS BEEN MODIFIED SINCE THE LAST TIME THE DATABASE WAS ACCESSED
							Dvk dvk = new Dvk(dvk_file);
							set_dvk(dvk, sql_id);
						}
						//REMOVE DVK FROM LIST OF DVKS TO ADD, SINCE IT'S ALREADY IN THE DATABASE
						int index = dvks.indexOf(filename);
						if(index != -1) {
							dvks.remove(index);
						}
					}
					else {
						//IF DVK FILE NO LONGER EXISTS, ADD TO LIST OF ENTRIES TO BE DELETED
						delete.add(Integer.valueOf(sql_id));
					}
				}
				//REMOVE NO LONGER EXISTING DVK FILES FROM THE DATABASE
				int size = delete.size();
				for(int del_num = 0; del_num < size; del_num++) {
					delete_dvk(delete.get(del_num).intValue());
				}
				//ADD DVKS NOT ALREADY IN THE DATABASE
				size = dvks.size();
				for(int dvk_num = 0; dvk_num < size; dvk_num++) {
					Dvk dvk = new Dvk(new File(dir, dvks.get(dvk_num)));
					if(dvk.get_title() != null) {
						add_dvk(dvk);
					}
				}
				//COMMIT CHANGES
				this.connection.commit();
				this.connection.setAutoCommit(true);
			}
			catch(SQLException resultset_e) {
				throw new SQLException();
			}
		}
		catch(SQLException statement_e) {
			//CLOSE AND DELETE DATABASE ON EXCEPTION
			try {
				this.connection.rollback();
				this.connection.setAutoCommit(true);
				close();
				delete_database();
			}
			catch(SQLException e) {}
			catch(DvkException f) {}
		}
	}
	
	/**
	 * Removes DVK files from the SQLite database if they share the same DVK file.
	 */
	public void remove_duplicates() {
		//CREATE STATEMENT TO GET ENTRIES WITH IDENTICAL DVK FILE NAMES
		//NOT NECESSARILY THE SAME FILE, COULD BE IN DIFFERENT DIRECTORIES
		StringBuilder statement = new StringBuilder();
		statement.append("SELECT ");
		statement.append(DVK_FILE);
		statement.append(" FROM ");
		statement.append(DVKS);
		statement.append(" GROUP BY ");
		statement.append(DVK_FILE);
		statement.append(" HAVING COUNT(");
		statement.append(DVK_FILE);
		statement.append(") > 1;");
		//CREATE AND EXECUTE PREPARED STATEMENT
		try(PreparedStatement ps_group = this.connection.prepareStatement(statement.toString())) {
			try(ResultSet rs_group = ps_group.executeQuery()) {
				//CREATE STATEMENT TO GET ALL ENTRIES WITH A GIVEN DVK FILE
				statement = new StringBuilder();
				statement.append("SELECT * FROM ");
				statement.append(DVKS);
				statement.append(" WHERE ");
				statement.append(DVK_FILE);
				statement.append(" = ?;");
				while(rs_group.next()) {
					//UPDATE EXECUTE STATEMENT WITH THE CURRENT DVK FILE AS A PARAMETER
					try(PreparedStatement ps_same = this.connection.prepareStatement(statement.toString())) {
						ps_same.setString(1, rs_group.getString(DVK_FILE));
						try(ResultSet rs_same = ps_same.executeQuery()) {
							//GET ALL DVKS WITH THE SAME FILENAME TO CHECK IF THE FILES THEMSELVES ARE THE SAME
							ArrayList<Dvk> dvks = get_dvks(rs_same);
							while(dvks.size() > 0) {
								Dvk dvk = dvks.get(0);
								dvks.remove(0);
								for(int dvk_num = 0; dvk_num < dvks.size(); dvk_num++) {
									//IF DVK FILES ARE ITENTICAL, DELETE ENTRIES
									if(dvks.get(dvk_num).get_dvk_file().equals(dvk.get_dvk_file())) {
										delete_dvk(dvk.get_sql_id());
										delete_dvk(dvks.get(dvk_num).get_sql_id());
										dvks.remove(dvk_num);
										dvk_num--;
									}
								}
							}
						}
						catch(SQLException e_rs_same) {
							throw new SQLException();
						}
					}
					catch(SQLException e_ps_same) {
						throw new SQLException();
					}
				}
			}
			catch(SQLException e_rs_group) {
				throw new SQLException();
			}
		}
		catch(SQLException | NullPointerException e_ps_group) {
			//CLOSE AND DELETE DATABASE ON EXCEPTION
			try {
				close();
				delete_database();
			}
			catch(DvkException e) {}
		}
	}
	
	/**
	 * Adds a given Dvk object to the SQLite database.
	 * 
	 * @param dvk Given Dvk
	 */
	public void add_dvk(Dvk dvk) {
		//CREATE SQL PREPARED STATEMENT FOR ADDING DVK INFO TO TABLE
		StringBuilder sql = new StringBuilder("INSERT INTO ");
		sql.append(DVKS);
		sql.append(" (");
		sql.append(DVK_ID);
		sql.append(',');
		sql.append(TITLE);
		sql.append(',');
		sql.append(SORT_TITLE);
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
		sql.append(") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
		//ADD DVK INFO TO PREPARED STATEMENT
		try (PreparedStatement ps = this.connection.prepareStatement(sql.toString())) {
			ps.setString(1, dvk.get_dvk_id());
			ps.setString(2, dvk.get_title());
			ps.setString(3, get_sortable_string(dvk.get_title()));
			ps.setString(4, ArrayProcessing.array_to_string(dvk.get_artists(), 0, true));
			ps.setString(5, dvk.get_time());
			ps.setString(6, ArrayProcessing.array_to_string(dvk.get_web_tags(), 0, true));
			ps.setString(7, dvk.get_description());
			ps.setString(8, dvk.get_page_url());
			ps.setString(9, dvk.get_direct_url());
			ps.setString(10, dvk.get_secondary_url());
			ps.setString(11, dvk.get_dvk_file().getParentFile().getAbsolutePath());
			ps.setString(12, dvk.get_dvk_file().getName());
			if(dvk.get_media_file() != null) {
				ps.setString(13, dvk.get_media_file().getName());
			}
			if(dvk.get_secondary_file() != null) {
				ps.setString(14, dvk.get_secondary_file().getName());
			}
			//EXECUTE PREPARED STATEMENT
			ps.executeUpdate();
			//IF PARENT DIRECTORY IS IN THE LIST OF DIRECTORIES TO INCLUDE WHEN GETTING DVK ENTRIES
			File parent = dvk.get_dvk_file().getParentFile();
			if(!this.directories.contains(parent)) {
				this.directories.add(parent);
			}
		}
		catch(SQLException | NullPointerException statement_e) {
			//CLOSE AND DELETE DATABASE ON EXCEPTION
			try {
				close();
				delete_database();
			}
			catch(DvkException e) {}
		}
	}
	
	/**
	 * Sets the DVK entry for a given SQL ID to a given Dvk object.
	 * 
	 * @param dvk Dvk object
	 * @param sql_id SQL ID of the entry to update
	 */
	public void set_dvk(Dvk dvk, int sql_id) {
		//CREATE SQL PREPARED STATEMENT TO SET THE GIVEN DVK ENTRY
		StringBuilder statement = new StringBuilder();
		statement.append("UPDATE ");
		statement.append(DVKS);
		statement.append(" SET ");
		statement.append(DVK_ID);
		statement.append("=?,");
		statement.append(TITLE);
		statement.append("=?,");
		statement.append(SORT_TITLE);
		statement.append("=?,");
		statement.append(ARTISTS);
		statement.append("=?,");
		statement.append(TIME);
		statement.append("=?,");
		statement.append(WEB_TAGS);
		statement.append("=?,");
		statement.append(DESCRIPTION);
		statement.append("=?,");
		statement.append(PAGE_URL);
		statement.append("=?,");
		statement.append(DIRECT_URL);
		statement.append("=?,");
		statement.append(SECONDARY_URL);
		statement.append("=?,");
		statement.append(DIRECTORY);
		statement.append("=?,");
		statement.append(DVK_FILE);
		statement.append("=?,");
		statement.append(MEDIA_FILE);
		statement.append("=?,");
		statement.append(SECONDARY_FILE);
		statement.append("=? WHERE ");
		statement.append(SQL_ID);
		statement.append("=?;");
		//ADD DVK INFO TO THE PREPARED STATEMENT
		try(PreparedStatement ps = this.connection.prepareStatement(statement.toString())) {
			ps.setString(1, dvk.get_dvk_id());
			ps.setString(2, dvk.get_title());
			ps.setString(3, get_sortable_string(dvk.get_title()));
			ps.setString(4, ArrayProcessing.array_to_string(dvk.get_artists(), 0, true));
			ps.setString(5, dvk.get_time());
			ps.setString(6, ArrayProcessing.array_to_string(dvk.get_web_tags(), 0, true));
			ps.setString(7, dvk.get_description());
			ps.setString(8, dvk.get_page_url());
			ps.setString(9, dvk.get_direct_url());
			ps.setString(10, dvk.get_secondary_url());
			ps.setString(11, dvk.get_dvk_file().getParentFile().getAbsolutePath());
			ps.setString(12, dvk.get_dvk_file().getName());
			if(dvk.get_media_file() != null) {
				ps.setString(13, dvk.get_media_file().getName());
			}
			if(dvk.get_secondary_file() != null) {
				ps.setString(14, dvk.get_secondary_file().getName());
			}
			ps.setInt(15, sql_id);
			//EXECUTE UPDATE
			ps.executeUpdate();
			//IF PARENT DIRECTORY IS IN THE LIST OF DIRECTORIES TO INCLUDE WHEN GETTING DVK ENTRIES
			File parent = dvk.get_dvk_file().getParentFile();
			if(!this.directories.contains(parent)) {
				this.directories.add(parent);
			}
		}
		catch(SQLException | NullPointerException e) {}
	}
	
	/**
	 * Deletes a DVK entry from the SQLite database that has a given SQL ID.
	 * 
	 * @param sql_id SQL ID of the entry to delete
	 */
	public void delete_dvk(int sql_id) {
		//CREATE SQL STATEMENT TO DELETE ENTRY OF GIVEN SQL ID
		StringBuilder statement = new StringBuilder();
		statement.append("DELETE FROM ");
		statement.append(DVKS);
		statement.append(" WHERE ");
		statement.append(SQL_ID);
		statement.append(" = ?;");
		//DELETE ENTRY OF GIVEN SQL ID FROM THE DATABASE
		try(PreparedStatement ps = this.connection.prepareStatement(statement.toString())) {
			ps.setInt(1, sql_id);
			ps.execute();
		}
		catch(SQLException | NullPointerException e) {}
	}
	
	/**
	 * Returns an ArrayList of sorted Dvk objects.
	 * Defaults to all of the Dvk entries currently loaded.
	 * 
	 * @param sort_type Order to sort the Dvk entries
	 * ('a' - Alphabetically by title, 't' - Earliest to latest published time)
	 * @param group_artists Whether to group Dvk entries by artist
	 * @param reverse Whether to reverse the order of Dvk
	 * @return List of ordered and filtered Dvk objects
	 */
	public ArrayList<Dvk> get_dvks(char sort_type, boolean group_artists, boolean reverse) {
		return get_dvks(sort_type, group_artists, reverse, null, null, null);
	}
	
	/**
	 * Returns an ArrayList of filtered Dvk objects.
	 * 
	 * @param sort_type Order to sort the Dvk entries
	 * ('a' - Alphabetically by title, 't' - Earliest to latest published time)
	 * @param group_artists Whether to group Dvk entries by artist
	 * @param reverse Whether to reverse the order of Dvk
	 * @param sql_where WHERE query to additionally filter Dvk entries (Use '?' for parameters)
	 * @param where_params Parameters of the WHERE query
	 * @return List of ordered and filtered Dvk objects
	 */
	public ArrayList<Dvk> get_dvks(
			char sort_type,
			boolean group_artists,
			boolean reverse,
			String sql_where,
			ArrayList<String> where_params) {
		return this.get_dvks(sort_type, group_artists, reverse, sql_where, where_params, null);
	}
	
	/**
	 * Returns an ArrayList of filtered Dvk objects.
	 * 
	 * @param sort_type Order to sort the Dvk entries
	 * ('a' - Alphabetically by title, 't' - Earliest to latest published time)
	 * @param group_artists Whether to group Dvk entries by artist
	 * @param reverse Whether to reverse the order of Dvk
	 * @param sql_where WHERE query to additionally filter Dvk entries (Use '?' for parameters)
	 * @param where_params Parameters of the WHERE query
	 * @param extra Any additional SQL queries to add before the WHERE statement
	 * @return List of ordered and filtered Dvk objects
	 */
	public ArrayList<Dvk> get_dvks(
			char sort_type,
			boolean group_artists,
			boolean reverse,
			String sql_where,
			ArrayList<String> where_params,
			String extra) {
		//ADD STATEMENT SECTION TO GET ALL ELEMENTS FROM THE DVK TABLE
		StringBuilder statement = new StringBuilder();
		statement.append("SELECT * FROM ");
		statement.append(DVKS);
		//ADD WHERE CLAUSE TO RESTRICT THE DVKS TO THOSE IN THE OPENED DIRECTORIES
		ArrayList<String> parameters = new ArrayList<>();
		statement.append(" WHERE (");
		for(int dir_num = 0; dir_num < this.directories.size(); dir_num++) {
			if(dir_num != 0) {
				statement.append(" OR ");
			}
			statement.append(DIRECTORY);
			statement.append("=?");
			parameters.add(this.directories.get(dir_num).getAbsolutePath());
		}
		statement.append(")");
		//ADD WHERE STATEMENT
		if(sql_where != null && sql_where.length() != 0) {
			statement.append(" AND (");
			statement.append(sql_where);
			statement.append(")");
			parameters.addAll(where_params);
		}
		//ADD EXTRA STATEMENT
		if(extra != null && extra.length() != 0) {
			statement.append(" ");
			statement.append(extra);
		}
		//DETERMINE DIRECTION TO SORT DVK FILES
		String direction = "ASC";
		if(reverse) {
			direction = "DESC";
		}
		//ADD STATEMENT SECTION TO SORT DVK FILES
		if(sort_type == 't') {
			statement.append(" ORDER BY ");
			//GROUP ARTISTS, IF SPECIFIED
			if(group_artists) {
				statement.append(ARTISTS);
				statement.append(" COLLATE NOCASE ");
				statement.append(direction);
				statement.append(", ");
			}
			//ORDER DVKS PRIMARILY BY TIME
			statement.append(TIME);
			statement.append(' ');
			statement.append(direction);
			statement.append(", ");
			statement.append(SORT_TITLE);
			statement.append(" COLLATE NOCASE ");
			statement.append(direction);
		}
		else if(sort_type == 'a') {
			statement.append(" ORDER BY ");
			//GROUP ARTISTS, IF SPECIFIED
			if(group_artists) {
				statement.append(ARTISTS);
				statement.append(" COLLATE NOCASE ");
				statement.append(direction);
				statement.append(", ");
			}
			//ORDER DVKS PRIMARILY BY TITLE
			statement.append(SORT_TITLE);
			statement.append(" COLLATE NOCASE ");
			statement.append(direction);
			statement.append(", ");
			statement.append(TIME);
			statement.append(' ');
			statement.append(direction);
		}
		statement.append(";");
		//CREATE PREPARED STATEMENT
		try(PreparedStatement ps = this.connection.prepareStatement(statement.toString())) {
			for(int param_num = 0; param_num < parameters.size(); param_num++) {
				ps.setString(param_num + 1, parameters.get(param_num));
			}
			//GET RESULT SET FROM STATEMENT
			try(ResultSet rs = ps.executeQuery()) {
				//GET DVK INFO FROM RESULT SET
				ArrayList<Dvk> dvks = get_dvks(rs);
				return dvks;
			}
			catch(SQLException f) {
				throw new SQLException();
			}
		}
		catch(SQLException | NullPointerException e) {
			return new ArrayList<>();
		}
	}
	
	/**
	 * Returns an ArrayList of Dvk objects from a ResultSet from the SQLite database.
	 * 
	 * @param rs ResultSet from the SQLite database
	 * @return ArrayList of Dvk objects from the ResultSet
	 * @throws SQLException Throws SQLException if reading from the SQLite database fails
	 */
	private static ArrayList<Dvk> get_dvks(ResultSet rs) throws SQLException {
		ArrayList<Dvk> dvks = new ArrayList<>();
		while(rs.next()) {
			Dvk dvk = new Dvk();
			dvk.set_sql_id(rs.getInt(SQL_ID));
			dvk.set_dvk_id(rs.getString(DVK_ID));
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
	
	/**
	 * Returns the DVK for the given SQL ID from the SQL database.
	 * 
	 * @param sql_id SQL ID of the DVK entry to return
	 * @return Dvk object from the given SQL ID
	 */
	public Dvk get_dvk(int sql_id) {
		//CREATE STATEMENT TO GET ELEMENT FROM DATABASE THAT MATCHES A GIVEN SQL ID
		StringBuilder statement = new StringBuilder();
		statement.append("SELECT * FROM ");
		statement.append(DVKS);
		statement.append(" WHERE ");
		statement.append(SQL_ID);
		statement.append("=?;");
		//EXECUTE STATEMENT WITH THE GIVEN SQL ID
		try(PreparedStatement ps = this.connection.prepareStatement(statement.toString())) {
			ps.setInt(1, sql_id);
			try(ResultSet rs = ps.executeQuery()) {
				//RETURN THE DVK FOR THE GIVEN SQL ID
				ArrayList<Dvk> dvks = get_dvks(rs);
				if(dvks.size() > 0) {
					return dvks.get(0);
				}
				//IF NOW DVKS WERE RETURNED FROM THE QUERY, RETURN EMPTY DVK
				return new Dvk();
			}
			catch(SQLException result_set_e) {
				throw new SQLException();
			}
		}
		catch(SQLException | NullPointerException statement_e) {
			//RETURNS EMPTY DVK IF EXECUTING THE STATEMENT FAILS
			return new Dvk();
		}
	}
	
	/**
	 * Returns a list of the directories containing DVK files currently loaded.
	 * 
	 * @return Currently loaded directories
	 */
	public ArrayList<File> get_loaded_directories() {
		if(this.directories == null) {
			return new ArrayList<>();
		}
		return this.directories;
	}
	
	/**
	 * Returns a list of sub-directories inside a given directory.
	 * If specified, only returns directories that contain DVK files.
	 * Includes the given directory.
	 * 
	 * @param dir Directory to search
	 * @param only_dvk_dirs Whether to only include directories with DVK files
	 * @return Sub-directories of the given directory
	 */
	public static ArrayList<File> get_directories(File dir, boolean only_dvk_dirs) {
		//RETURN EMPTY ARRAY LIST IF GIVEN DIRECTORY IS INVALID
		if(dir == null || !dir.isDirectory()) {
			return new ArrayList<>();
		}
		//INITIALIZE VARIABLES
		ArrayList<File> search_dirs = new ArrayList<>();
		ArrayList<File> return_dirs = new ArrayList<>();
		search_dirs.add(dir);
		return_dirs.add(dir);
		//GET ALL DIRECTORIES AND SUBDIRECTORIES
		while(search_dirs.size() > 0) {
			File[] cur_dirs = search_dirs.get(0).listFiles(new DirectoryFilter());
			for(int i = 0; i < cur_dirs.length; i++) {
				search_dirs.add(cur_dirs[i]);
				return_dirs.add(cur_dirs[i]);
			}
			search_dirs.remove(0);
		}
		//IF SET TO ONLY RETURN DIRECTORIES WITH DVK FILES,
		//FILTER OUT DIRECTORIES WITHOUT DVK FILES
		if(only_dvk_dirs) {
			ExtensionFilter ef = new ExtensionFilter(".dvk", true);
			for(int i = 0; i < return_dirs.size(); i++) {
				File[] dvk_dirs = return_dirs.get(i).listFiles(ef);
				if(dvk_dirs.length == 0) {
					return_dirs.remove(i);
					i--;
				}
				dvk_dirs = null;
			}
		}
		return return_dirs;
	}
	
	/**
	 * Takes a given string and modifies it to be easily alpha-numerically sorted.
	 * Pads out numbers with zeros so they are correctly sorted.
	 * 
	 * @param str Given string to be modified
	 * @return Alpha-numerically sortable string
	 */
	public static String get_sortable_string(String str) {
		//INITIALIZE VARIABLES
		char character;
		boolean add_num = true;
		StringBuilder modified = new StringBuilder();
		StringBuilder number = new StringBuilder();
		//RUN THROUGH EACH CHARACTER IN THE GIVEN STRING
		for(int char_num = 0; char_num < str.length(); char_num++) {
			character = str.charAt(char_num);
			if(character > 47 && character < 58) {
				//RUNS IF CURRENT CHARACTER IS A DIGIT
				if(add_num) {
					//IF ADD_NUM IS TRUE, TREAT THIS DIGIT AS PART OF A NUMBER
					//CHARACTER IS ADDED TO THE NUMBER VALUE TO BE ZERO PADDED LATER
					number.append(character);
				}
				else {
					//IF ADD_NUM IS FALSE, TREAT THIS DIGIT AS A NORMAL CHARACTER
					modified.append(character);
				}
			}
			else {
				//IF CHARACTER IS NOT A DIGIT,
				//SET ADD_NUM TO TRUE SO UPCOMING DIGITS ARE TREATED AS NUMBERS
				add_num = true;
				if(character == '.') {
					//IF THE CHARACTER IS A DECIMAL,
					//SET ADD_NUM FALSE SO UPCOMING DIGITS ARE TREATED AS CHARACTERS
					add_num = false;
				}
				//IF THE VALUE OF THE NUMBER VARIABLE IS SET,
				//ADD ZERO PADDED VALUE TO THE MODIFIED STRING
				if(number.length() > 0) {
					modified.append(StringProcessing.pad_num(number.toString(), 5));
					number = new StringBuilder();
				}
				//ADD CURRENT CHARACTER TO THE MODIFIED STRING
				modified.append(character);
			}
		}
		//IF THE VALUE OF THE NUMBER VARIABLE IS SET,
		//ADD ZERO PADDED VALUE TO THE MODIFIED STRING
		if(number.length() > 0) {
			modified.append(StringProcessing.pad_num(number.toString(), 5));
		}
		//RETURN MODIFIED STRING IN A REDUCED FORM
		return StringProcessing.get_filename(modified.toString(), -1).toLowerCase();
	}

	/**
	 * Deletes the SQLite database file.
	 * 
	 * @return Whether or not the database file was successfully deleted
	 */
	public boolean delete_database() {
		try {
			close();
		}
		catch(DvkException e) {}
		return this.database.delete();
	}

	/**
	 * Closes the SQLite database safely.
	 */
	@Override
	public void close() throws DvkException {
		if(this.connection != null) {
			try {
				this.connection.close();
			}
			catch(SQLException e) {}
		}
		this.connection = null;
		this.directories = new ArrayList<>();
	}
}
