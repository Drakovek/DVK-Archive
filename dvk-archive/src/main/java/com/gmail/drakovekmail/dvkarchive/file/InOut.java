package com.gmail.drakovekmail.dvkarchive.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
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
	public static void write_file(
			final File file, 
			final ArrayList<String> contents) {
		boolean write = true;
		if(file != null && file.exists()) {
			write = file.delete();
		}
		if(write && file != null && contents != null) {
			BufferedWriter bw = null;
			try {
				FileOutputStream fos; 
				OutputStreamWriter osw;
				Charset utf = StandardCharsets.UTF_8;
				fos = new FileOutputStream(file);
				osw = new OutputStreamWriter(fos, utf);
				bw = new BufferedWriter(osw);
				for(int i = 0; i < contents.size(); i++) {
					if(i > 0) {
						bw.append("\r\n");
					}
					bw.append(contents.get(i));
				}
			}
			catch(IOException e) {}
			finally {
				if(bw != null) {
					try {
						bw.flush();
						bw.close();
					}
					catch(IOException f) {}
				}
			}
		}
	}
	
	/**
	 * Writes text to a given file in UTF-8 text format.
	 * 
	 * @param file Given file in which to save text
	 * @param contents Contents of text file
	 */
	public static void write_file(
			final File file, 
			final String contents) {
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
	public static ArrayList<String> read_file_list(final File file) {
		if(file == null || !file.exists()) {
			return new ArrayList<>();
		}
		ArrayList<String> contents = new ArrayList<>();
		@SuppressWarnings("resource")
		BufferedReader br = null;
		try {
			String line;
			FileInputStream fis;
			InputStreamReader isr;
			Charset utf = StandardCharsets.UTF_8;
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, utf);
			br = new BufferedReader(isr);
			while((line = br.readLine()) != null) {
				contents.add(line);
			}
		}
		catch(IOException e) {}
		finally {
			if(br != null) {
				try {
					br.close();
				}
				catch(IOException f) {}
			}
		}
		return contents;
	}
	
	/**
	 * Returns the text contents of a given UTF-8 formatted text file.
	 * 
	 * @param file Given UTF-8 file to read.
	 * @return Text contents of file
	 */
	public static String read_file(final File file) {
		ArrayList<String> contents;
		contents = read_file_list(file);
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < contents.size(); i++) {
			if(i > 0) {
				builder.append("\r\n");
			}
			builder.append(contents.get(i));
		}
		return builder.toString();
	}
}
