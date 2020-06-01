package com.gmail.drakovekmail.dvkarchive.file;

import java.io.File;
import java.io.FileFilter;

/**
 * FileFilter that excludes all files that are not directories.
 * 
 * @author Drakovek
 */
public class DirectoryFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		return file.isDirectory();
	}
}
