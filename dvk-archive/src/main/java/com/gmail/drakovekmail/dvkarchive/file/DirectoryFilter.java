package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.io.FileFilter;

/**
 * FileFilter that excludes all files that are not directories.
 * 
 * @author Drakovek
 */
public class DirectoryFilter implements FileFilter {

	/**
	 * Returns whether a given file should be accepted.
	 * Returns true if file is a directory.
	 * 
	 * @param file Given file
	 * @return Whether given file should be accepted
	 */
	@Override
	public boolean accept(File file) {
		return file.isDirectory();
	}
}
