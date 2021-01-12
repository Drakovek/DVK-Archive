package com.gmail.drakovekmail.dvkarchive.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Class containing methods for reading and writing files.
 * 
 * @author Drakovek
 */
public class InOut {
	
	/**
	 * Writes text to a given file in UTF-8 text format.
	 * Items in contents are separated by line breaks.
	 * 
	 * @param file Given file in which to save text
	 * @param contents Contents of text file
	 */
	public static void write_file(File file, ArrayList<String> contents) {
		//DELETE FILE IF GIVEN FILE ALREADY EXISTS
		boolean should_write = true;
		if(file != null && file.exists()) {
			should_write = file.delete();
		}
		//WRITE FILE
		if(should_write && file != null && contents != null) {
			try(FileOutputStream fos = new FileOutputStream(file);
					OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
					BufferedWriter bw = new BufferedWriter(osw)) {
				//WRITE EACH LINE OF CONTENTS
				for(int i = 0; i < contents.size(); i++) {
					if(i > 0) {
						//ADDS LINE BREAK BETWEEN LINES
						bw.append("\r\n");
					}
					bw.append(contents.get(i));
				}
			}
			catch(IOException e) {}
		}
	}
	
	/**
	 * Writes text to a given file in UTF-8 text format.
	 * 
	 * @param file Given file in which to save text
	 * @param contents Contents of text file
	 */
	public static void write_file(File file, String contents) {
		if(contents != null) {
			ArrayList<String> list = new ArrayList<>();
			list.add(contents);
			write_file(file, list);
		}
	}
	
	/**
	 * Returns the text contents of a given UTF-8 formatted text file.
	 * Lines of text are separated into different items in ArrayList<String>.
	 * 
	 * @param file Given UTF-8 file to read.
	 * @return Text contents of file
	 */
	public static ArrayList<String> read_file_list(File file) {
		//RETURN EMPTY LIST IF FILE IS INVALID
		if(file == null || !file.exists()) {
			return new ArrayList<>();
		}
		//READ FILE
		ArrayList<String> contents = new ArrayList<>();
		try(FileInputStream fis = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
				BufferedReader br = new BufferedReader(isr)) {
			//READ EACH LINE
			String line;
			while((line = br.readLine()) != null) {
				contents.add(line);
			}
		}
		catch(IOException e) {}
		return contents;
	}
	
	/**
	 * Returns the text contents of a given UTF-8 formatted text file.
	 * 
	 * @param file Given UTF-8 file to read.
	 * @return Text contents of file
	 */
	public static String read_file(final File file) {
		ArrayList<String> contents = read_file_list(file);
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < contents.size(); i++) {
			if(i > 0) {
				//ADDS LINE BREAK BETWEEN LINES
				builder.append("\r\n");
			}
			builder.append(contents.get(i));
		}
		return builder.toString();
	}
}
