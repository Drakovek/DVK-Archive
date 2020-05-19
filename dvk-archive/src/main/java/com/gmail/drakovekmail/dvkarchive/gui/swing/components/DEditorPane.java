package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DHyperlinkListener;
import com.gmail.drakovekmail.dvkarchive.processing.StringProcessing;

/**
 * EditorPane UI object for DVK Archive.
 * 
 * @author Drakovek
 */
public class DEditorPane extends JEditorPane {

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 8940050977102275052L;

	/**
	 * Label for small text class in HTML
	 */
	public static final String SMALL_TEXT_CLASS = "dvk_small_text";
	
	/**
	 * Label for large text class in HTML
	 */
	public static final String LARGE_TEXT_CLASS = "dvk_large_text";
	
	/**
	 * BaseGUI for getting GUI settings.
	 */
	private BaseGUI base_gui;
	
	/**
	 * Hex value for the main text color
	 */
	private String text_color;
	
	/**
	 * Hex value for hyperlink text color
	 */
	private String hyperlink_color;
	
	/**
	 * Size for large text
	 */
	private int large_size;
	
	/**
	 * Size for small text
	 */
	private int small_size;
	
	/**
	 * Size for boarders and separator lines
	 */
	private int border_size;
	
	/**
	 * Initializes the DEditorPane.
	 * 
	 * @param base_gui BaseGUI for getting GUI settings
	 * @param color_ref Component to use as reference for text color
	 */
	public DEditorPane(BaseGUI base_gui, JComponent color_ref) {
		//INITIALIZE PANE
		this.base_gui = base_gui;
		setEditable(false);
		setFont(base_gui.get_font());
		int space = base_gui.get_space_size();
		Insets insets = new Insets(space, space, space, space);
		setMargin(insets);
		addHyperlinkListener(new DHyperlinkListener());
		//SET MAIN TEXT COLOR
		StringBuilder color = new StringBuilder();
		color.append(StringProcessing.extend_num(
				Integer.toHexString(color_ref.getForeground().getRed()), 2));
		color.append(StringProcessing.extend_num(
				Integer.toHexString(color_ref.getForeground().getGreen()), 2));
		color.append(StringProcessing.extend_num(
				Integer.toHexString(color_ref.getForeground().getBlue()), 2));
		this.text_color = color.toString();
		//SET HYPERLINK TEXT COLOR
		color = new StringBuilder();
		color.append(StringProcessing.extend_num(
				Integer.toHexString((color_ref.getBackground().getRed()
						+ color_ref.getForeground().getRed()) / 2), 2));
		color.append(StringProcessing.extend_num(
				Integer.toHexString((color_ref.getBackground().getGreen()
						+ color_ref.getForeground().getGreen()) / 2), 2));
		color.append(StringProcessing.extend_num(
				Integer.toHexString((color_ref.getBackground().getGreen()
						+ color_ref.getForeground().getGreen()) / 2), 2));
		this.hyperlink_color = color.toString();
		//SET FONT SIZE
		this.large_size = (int)(base_gui.get_font().getSize() * ((double)4/(double)3));
		this.small_size = (int)(base_gui.get_font().getSize() * ((double)3/(double)4));
		if(this.small_size < 1) {
			this.small_size = 1;
		}
		//SET BORDER SIZE
		this.border_size = (int)(base_gui.get_font().getSize() * ((double)1/(double)10));
		if(this.border_size < 1) {
			this.border_size = 1;
		}
	}
	
	/**
	 * Returns HTML formatted text from given text to match the current GUI settings.
	 * 
	 * @param text HTML formatted text
	 * @return Given text formatted to match GUI settings
	 */
	public String get_html_text(String text) {
		if(text != null) {
			String edited = text;
			//REMOVE DOCTYPE
			int start = 0;
			int end = 0;
			while(end != -1) {
				end = -1;
				start = edited.toLowerCase().indexOf("<!doctype");
				if(start != -1) {
					end = edited.indexOf('>', start);
				}
				if(end != -1) {
					edited = StringProcessing.remove_section(edited, start, end + 1);
				}
			}
			//REMOVE HTML ELEMENTS
			end = 0;
			while(end != -1) {
				end = -1;
				start = edited.toLowerCase().indexOf("<html>");
				if(start != -1) {
					end = edited.indexOf('>', start);
				}
				if(end != -1) {
					edited = StringProcessing.remove_section(edited, start, end + 1);
				}
			}
			end = 0;
			while(end != -1) {
				end = -1;
				start = edited.toLowerCase().indexOf("</html>");
				if(start != -1) {
					end = edited.indexOf('>', start);
				}
				if(end != -1) {
					edited = StringProcessing.remove_section(edited, start, end + 1);
				}
			}
			//REMOVE BODY ELEMENTS
			end = 0;
			while(end != -1) {
				end = -1;
				start = edited.toLowerCase().indexOf("<body>");
				if(start != -1) {
					end = edited.indexOf('>', start);
				}
				if(end != -1) {
					edited = StringProcessing.remove_section(edited, start, end + 1);
				}
			}
			end = 0;
			while(end != -1) {
				end = -1;
				start = edited.toLowerCase().indexOf("</body>");
				if(start != -1) {
					end = edited.indexOf('>', start);	
				}
				if(end != -1) {
					edited = StringProcessing.remove_section(edited, start, end + 1);
				}	
			}
			//REMOVE STYLE ELEMENTS
			end = 0;
			while(end != -1) {
				end = -1;
				start = edited.toLowerCase().indexOf("<style");
				if(start != -1) {
					end = edited.indexOf("</style>", start);	
				}
				if(end != -1) {
					end = edited.indexOf('>', end);
					edited = StringProcessing.remove_section(edited, start, end + 1);
				}	
			}
			//SET STYLE
			StringBuilder builder = new StringBuilder();
			builder.append("<!DOCTYPE html><html>"); //$NON-NLS-1$
			builder.append("<style type=\"text/css\">body{text-align:left;font-family:"); //$NON-NLS-1$
			builder.append(this.base_gui.get_font().getFamily());
			builder.append(";font-size:"); //$NON-NLS-1$
			builder.append(this.base_gui.get_font().getSize());
			builder.append("pt;color:#"); //$NON-NLS-1$
			builder.append(this.text_color);
			builder.append(";}a{color:#"); //$NON-NLS-1$
			builder.append(this.hyperlink_color);
			builder.append(";}."); //$NON-NLS-1$
			builder.append(LARGE_TEXT_CLASS);
			builder.append("{font-size:"); //$NON-NLS-1$
			builder.append(this.large_size);
			builder.append("pt}."); //$NON-NLS-1$
			builder.append(SMALL_TEXT_CLASS);
			builder.append("{font-size:"); //$NON-NLS-1$
			builder.append(this.small_size);
			builder.append("pt}hr{border:"); //$NON-NLS-1$
			builder.append(this.border_size);
			builder.append("px solid #"); //$NON-NLS-1$
			builder.append(this.text_color);
			builder.append(";}</style><body>"); //$NON-NLS-1$
			//SET TEXT
			builder.append(edited);
			builder.append("</body></html>"); //$NON-NLS-1$
			return builder.toString();
		}
		return new String();
	}
	
	/**
	 * Sets the text for the editor pane, using HTML content.
	 * 
	 * @param text Text in HTML format
	 */
	public void set_text_html(String text) {
		setContentType("text/html");
		setText(get_html_text(text));
	}
}
