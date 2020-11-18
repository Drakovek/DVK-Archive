package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.io.FileFilter;

/**
 * FileFilter for filtering files based on file extension.
 * 
 * @author Drakovek
 */
public class ExtensionFilter implements FileFilter {

	/**
	 * Extension of files to either include or exclude
	 */
	private String extension;
	
	/**
	 * Whether to include files with extension, otherwise excludes
	 */
	private boolean include;
	
	/**
	 * Initializes the ExtensionFilter.
	 * 
	 * @param extension Extension of files to either include or exclude
	 * @param include Whether to include files with extension, otherwise excludes
	 */
	public ExtensionFilter(String extension, boolean include) {
		this.extension = extension;
		this.include = include;
	}
	
	/**
	 * Returns whether a given file should be accepted.
	 * Based on whether or not the file uses a given file extension.
	 * 
	 * @param file Given file
	 * @return Whether file should be accepted
	 */
	@Override
	public boolean accept(File file) {
		if(file.isDirectory()) {
			return false;
		}
		if(file.getAbsolutePath().endsWith(this.extension)) {
			return this.include;
		}
		return !this.include;
	}

}
