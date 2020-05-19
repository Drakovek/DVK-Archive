package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;

/**
 * Unit tests for the DEditorPane class.
 * 
 * @author Drakovek
 */
public class TestDEditorPane {
	
	/**
	 * Tests the get_html_text method.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void test_get_html_text() {
		BaseGUI base_gui = new BaseGUI();
		base_gui.set_font("Dialog", 12, false);
		DLabel color_ref = new DLabel(base_gui, null, "text");
		DEditorPane epane = new DEditorPane(base_gui, color_ref);
		//TEST REMOVES DOCTYPE
		String html = "Blah<!DOCTYPE html>Bleh";
		String result = epane.get_html_text(html);
		assertTrue(result.contains(">BlahBleh<"));
		//TEST REMOVE HTML TAGS
		html = "st<html>art</html>e<html>n</html>d";
		result = epane.get_html_text(html);
		assertTrue(result.contains(">startend<"));
		//TEST REMOVE BODY TAGS
		html = "Wo<body>rds</body>N<body>st</body>uff";
		result = epane.get_html_text(html);
		assertTrue(result.contains(">WordsNstuff<"));
		//TEST REMOVE STYLE ELEMENTS
		html = "Start<style>Style Stuff</style>Mid<style type=blah>stylethings</style>End";
		result = epane.get_html_text(html);
		assertTrue(result.contains(">StartMidEnd<"));
		//TEST CORRECT FORMAT
		html = "<!doctype bleh><body><html>Thing</body></html>";
		result = epane.get_html_text(html);
		html = "<!DOCTYPE html><html><style type=\"text/css\">body{text-align:left;font-family:Dialog;font-size:12pt;color:#";
		assertTrue(result.startsWith(html));
		html = ";}a{color:#";
		assertTrue(result.contains(html));
		html = ";}.dvk_large_text{font-size:16pt}.dvk_small_text{font-size:9pt}hr{border:1px solid #";
		assertTrue(result.contains(html));
		html = ";}</style><body>Thing</body></html>";
		assertTrue(result.endsWith(html));
	}
}
