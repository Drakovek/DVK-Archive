package com.gmail.drakovekmail.dvkarchive.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.JPanel;

import com.gmail.drakovekmail.dvkarchive.gui.language.LanguageHandler;

/**
 * Class containing methods for use by the program's GUI elements.
 * 
 * @author Drakovek
 */
public class BaseGUI {
	
	/**
	 * Key for font size in preferences
	 */
	private static final String FONT_SIZE = "font_size";
	
	/**
	 * Key for font family in preferences
	 */
	private static final String FONT_FAMILY = "font_family";
	
	/**
	 * Key for whether the font is bold in preferences
	 */
	private static final String FONT_BOLD = "font_bold";
	
	/**
	 * Key for whether fonts should be anti-aliased
	 */
	private static final String AA = "aa";
	
	/**
	 * Key for the look and feel(theme) of the GUI
	 */
	private static final String THEME = "theme";
	
	/**
	 * Default font for the program
	 */
	private Font font;
	
	/**
	 * Whether fonts should be anti-aliased
	 */
	private boolean aa;
	
	/**
	 * Size of spaces between components in GUI
	 */
	private int space_size;
	
	/**
	 * Look and feel(theme) for the GUI
	 */
	private String theme;
	
	/**
	 * Language handler for the BaseGUI
	 */
	private LanguageHandler lang_handler;
	
	/**
	 * Initializes the BaseGUI by loading preferences.
	 */
	public BaseGUI() {
		load_preferences();
		this.lang_handler = new LanguageHandler();
	}
	
	/**
	 * Saves GUI preferences.
	 */
	public void save_preferences() {
		Preferences prefs = Preferences.userNodeForPackage(BaseGUI.class);
		//SET FONT
		prefs.put(FONT_FAMILY, get_font().getFamily());
		prefs.putBoolean(FONT_BOLD, get_font().isBold());
		prefs.putInt(FONT_SIZE, get_font().getSize());
		//SET THEME
		prefs.put(THEME, get_theme());
		//SET ANTI-ALIASING
		prefs.putBoolean(AA, use_aa());
	}
	
	/**
	 * Loads GUI preferences.
	 */
	public void load_preferences() {
		Preferences prefs = Preferences.userNodeForPackage(BaseGUI.class);
		//SET FONT
		String family = prefs.get(FONT_FAMILY, "Dialog").toString();
		boolean bold = prefs.getBoolean(FONT_BOLD, false);
		int size = prefs.getInt(FONT_SIZE, 12);
		set_font(family, size, bold);
		//SET THEME
		set_theme(prefs.get(THEME, "Metal"));
		//SET ANTIALIASING
		set_use_aa(prefs.getBoolean(AA, true));
	}
	
	/**
	 * Sets the default font for the program.
	 * 
	 * @param family Font family
	 * @param size Font size
	 * @param is_bold Wheter the font is bold
	 */
	public void set_font(String family, int size, boolean is_bold) {
		int font_type = Font.PLAIN;
		if(is_bold) {
			font_type = Font.BOLD;
		}
		this.font = new Font(family, font_type, size);
		this.space_size = size / 2;
	}
	
	/**
	 * Returns the default font for the program.
	 * 
	 * @return Default font
	 */
	public Font get_font() {
		return this.font;
	}
	
	/**
	 * Sets whether fonts should be anti-aliased.
	 * 
	 * @param aa Whether to use anti-aliasing
	 */
	public void set_use_aa(boolean aa) {
		this.aa = aa;
	}
	
	/**
	 * Returns whether fonts should be anti-aliased.
	 * 
	 * @return Whether to use anti-aliasing.
	 */
	public boolean use_aa() {
		return this.aa;
	}
	
	/**
	 * Sets the look and feel(theme) for the GUI.
	 * 
	 * @param theme Theme of the GUI
	 */
	public void set_theme(String theme) {
		if(theme == null) {
			this.theme = new String();
		}
		else {
			this.theme = theme;
		}
	}
	
	/**
	 * Returns the look and feel(theme) for the GUI.
	 * 
	 * @return Theme of the GUI.
	 */
	public String get_theme() {
		return this.theme;
	}
	
	/**
	 * Returns a horizontal rigid area.
	 * Space is the default UI space size.
	 * 
	 * @return Horizontal space
	 */
	public Component get_x_space() {
		Dimension space = new Dimension(this.space_size, 1);
		return Box.createRigidArea(space);
	}
	
	/**
	 * Returns a vertical rigid area.
	 * Space is the default UI space size.
	 * 
	 * @return Vertical space
	 */
	public Component get_y_space() {
		Dimension space = new Dimension(1, this.space_size);
		return Box.createRigidArea(space);
	}
	
	/**
	 * Returns a JPanel with two components stacked vertically.
	 * Sets the vertical weights of components to 0.
	 * Places a default space between components.
	 * 
	 * @param top Top component
	 * @param bottom Bottom component
	 * @return JPanel with components stacked
	 */
	public JPanel get_y_stack(Component top, Component bottom) {
		return(get_y_stack(top, 0, bottom, 0));
	}
	
	/**
	 * Returns a JPanel with two components stacked vertically.
	 * Places a default space between components.
	 * 
	 * @param top Top component
	 * @param top_weight Weight Y of the top component
	 * @param bottom Bottom component
	 * @param bottom_weight Weight Y of the bottom component
	 * @return JPanel with components stacked
	 */
	public JPanel get_y_stack(Component top, int top_weight, Component bottom, int bottom_weight) {
		JPanel stack = new JPanel();
		//ADD SPACE
		stack.setLayout(new GridBagLayout());
		GridBagConstraints cst = new GridBagConstraints();
		cst.gridx = 1;
		cst.gridy = 1;
		cst.gridwidth = 1;
		cst.gridheight = 1;
		cst.weightx = 0;
		cst.weighty = 0;
		cst.fill = GridBagConstraints.BOTH;
		stack.add(get_y_space(), cst);
		//ADD TOP COMPONENT
		cst.gridx = 0;
		cst.gridy = 0;
		cst.gridwidth = 3;
		cst.weightx = 1;
		cst.weighty = top_weight;
		stack.add(top, cst);
		//ADD BOTTOM COMPONENT
		cst.gridy = 2;
		cst.weighty = bottom_weight;
		stack.add(bottom, cst);
		return stack;
	}
	
	/**
	 * Returns a JPanel with two components stacked horizontal.
	 * Sets the horizontal weights of components to 0.
	 * Places a default space between components.
	 * 
	 * @param left Left component
	 * @param right Right component
	 * @return JPanel with components stacked
	 */
	public JPanel get_x_stack(Component left, Component right) {
		return get_x_stack(left, 0, right, 0);
	}
	
	/**
	 * Returns a JPanel with two components stacked horizontal.
	 * Places a default space between components.
	 * 
	 * @param left Left component
	 * @param left_weight Weight X of the left component
	 * @param right Right component
	 * @param right_weight Weight X of the right component
	 * @return JPanel with components stacked
	 */
	public JPanel get_x_stack(Component left, int left_weight, Component right, int right_weight) {
		JPanel stack = new JPanel();
		//ADD SPACE
		stack.setLayout(new GridBagLayout());
		GridBagConstraints cst = new GridBagConstraints();
		cst.gridx = 1;
		cst.gridy = 1;
		cst.gridwidth = 1;
		cst.gridheight = 1;
		cst.weightx = 0;
		cst.weighty = 0;
		cst.fill = GridBagConstraints.BOTH;
		stack.add(get_x_space(), cst);
		//ADD LEFT COMPONENT
		cst.gridx = 0;
		cst.gridy = 0;
		cst.gridheight = 3;
		cst.weightx = left_weight;
		cst.weighty = 1;
		stack.add(left, cst);
		//ADD RIGHT COMPONENT
		cst.gridx = 2;
		cst.weightx = right_weight;
		stack.add(right, cst);
		return stack;
	}
	
	/**
	 * Returns a JPanel with a component surrounded by spaces.
	 * Surrounds component with spaces in all directions.
	 * Sets weight of component to 1.
	 * 
	 * @param cmp Main component of the panel.
	 * @return JPanel with cmp surrounded by spaces.
	 */
	public JPanel get_spaced_panel(Component cmp) {
		JPanel pnl = get_spaced_panel(
				cmp, 1, 1, true, true, true, true);
		return pnl;
	}
	
	/**
	 * Returns a JPanel with a component surrounded by spaces.
	 * Sets weight of component to 1.
	 * 
	 * @param cmp Main component of the panel.
	 * @param top Whether to have a top space
	 * @param bottom Whether to have a bottom space
	 * @param left Whether to have a left space
	 * @param right Whether to have a right space
	 * @return JPanel with cmp surrounded by spaces.
	 */
	public JPanel get_spaced_panel(
			Component cmp,
			boolean top,
			boolean bottom,
			boolean left,
			boolean right) {
		JPanel pnl = get_spaced_panel(
				cmp, 1, 1, top, bottom, left, right);
		return pnl;
	}
	
	/**
	 * Returns a JPanel with a component surrounded by spaces.
	 * 
	 * @param cmp Main component of the panel.
	 * @param weightx WeightX of cmp
	 * @param weighty WeightY of cmp
	 * @param top Whether to have a top space
	 * @param bottom Whether to have a bottom space
	 * @param left Whether to have a left space
	 * @param right Whether to have a right space
	 * @return JPanel with cmp surrounded by spaces.
	 */
	public JPanel get_spaced_panel(
			Component cmp,
			int weightx,
			int weighty,
			boolean top,
			boolean bottom,
			boolean left,
			boolean right) {
		//SET COMPONENT BASE LAYOUT
		GridBagConstraints comp_cst = new GridBagConstraints();
		comp_cst.gridx = 0;
		comp_cst.gridy = 0;
		comp_cst.gridwidth = 3;
		comp_cst.gridheight = 3;
		comp_cst.weightx = weightx;
		comp_cst.weighty = weighty;
		comp_cst.fill = GridBagConstraints.BOTH;
		//SET SPACE BASE LAYOUT
		GridBagConstraints space_cst = new GridBagConstraints();
		space_cst.gridwidth = 1;
		space_cst.gridheight = 1;
		space_cst.weightx = 0;
		space_cst.weighty = 0;
		//ADD SPACES TO PANEL
		JPanel pnl = new JPanel();
		pnl.setLayout(new GridBagLayout());
		if(top) {
			comp_cst.gridy = 1;
			comp_cst.gridheight = 2;
			space_cst.gridx = 1;
			space_cst.gridy = 0;
			pnl.add(get_y_space(), space_cst);
		}
		if(bottom) {
			comp_cst.gridheight--;
			space_cst.gridx = 0;
			space_cst.gridy = 2;
			pnl.add(get_y_space(), space_cst);
		}
		if(left) {
			comp_cst.gridx = 1;
			comp_cst.gridwidth = 2;
			space_cst.gridx = 0;
			space_cst.gridy = 1;
			pnl.add(get_x_space(), space_cst);
		}
		if(right) {
			comp_cst.gridwidth--;
			space_cst.gridx = 2;
			space_cst.gridy = 1;
			pnl.add(get_x_space(), space_cst);
		}
		pnl.add(cmp, comp_cst);
		return pnl;
	}
	
	/**
	 * Returns BaseGUI's LanguageHandler.
	 * 
	 * @return LanguageHandler
	 */
	public LanguageHandler get_language_handler() {
		return this.lang_handler;
	}
	
	/**
	 * Returns the language String for a given key in the UI's language.
	 * 
	 * @param key Given key
	 * @return Language String
	 */
	public String get_language_string(String key) {
		return this.lang_handler.get_language_string(key);
	}
}
