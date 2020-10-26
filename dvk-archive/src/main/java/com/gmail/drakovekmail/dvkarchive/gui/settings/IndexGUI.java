package com.gmail.drakovekmail.dvkarchive.gui.settings;

import java.awt.GridLayout;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.gmail.drakovekmail.dvkarchive.file.FilePrefs;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DButton;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DLabel;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DTextField;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;

/**
 * GUI for handling settings regarding Dvk indexing files.
 * 
 * @author Drakovek
 */
public class IndexGUI extends JPanel implements DActionEvent {

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -4968644996950316678L;

	/**
	 * Directory in which to store Dvk index files
	 */
	private File directory;
	
	/**
	 * FilePrefs object to use when saving index settings
	 */
	private FilePrefs file_prefs;
	
	/**
	 * Text field object for showing/entering index file directory
	 */
	private DTextField path_txt;
	
	/**
	 * Initializes the IndexGUI.
	 * 
	 * @param base_gui BaseGUI for getting UI settings.
	 * @param file_prefs FilePrefs object to use when saving index settings
	 */
	public IndexGUI(BaseGUI base_gui, FilePrefs file_prefs) {
		this.file_prefs = file_prefs;
		this.directory = file_prefs.get_index_dir();
		this.path_txt = new DTextField(base_gui, this, "path");
		this.path_txt.always_allow_action();
		DLabel index_lbl = new DLabel(base_gui, this.path_txt, "index");
		index_lbl.set_font_large();
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		JPanel title_pnl = base_gui.get_y_stack(index_lbl, sep);
		DButton open_btn = new DButton(base_gui, this, "browse");
		open_btn.always_allow_action();
		JPanel open_pnl = base_gui.get_x_stack(this.path_txt, 1, open_btn, 0);
		JPanel full_pnl = base_gui.get_y_stack(title_pnl, 0, open_pnl, 0);
		setLayout(new GridLayout(1, 1));
		set_directory(this.directory);
		add(full_pnl);
	}
	
	/**
	 * Opens dialog for setting index file directory.
	 */
	private void browse() {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(this.directory);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int value = fc.showOpenDialog(this);
		if(value == JFileChooser.APPROVE_OPTION) {
			set_directory(fc.getSelectedFile());
		}
	}
	
	/**
	 * Sets the index file directory based on a given path string.
	 * 
	 * @param path Absolute path of index directory
	 */
	private void set_directory(String path) {
		File file = new File(path);
		if(file.exists()) {
			set_directory(file);
		}
		else {
			set_directory(this.directory);
		}
	}
	
	/**
	 * Sets the index file directory to a given directory.
	 * 
	 * @param directory Given directory
	 */
	private void set_directory(File directory) {
		this.directory = directory;
		this.path_txt.setText(directory.getAbsolutePath());
	}
	
	/**
	 * Saves index file settings.
	 */
	public void save() {
		this.file_prefs.set_index_dir(this.directory);
		this.file_prefs.save_preferences();
	}
	
	@Override
	public void event(String id) {
		switch(id) {
			case "browse":
				browse();
				break;
			case "path":
				set_directory(this.path_txt.getText());
				break;
		}
	}
}
