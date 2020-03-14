package com.gmail.drakovekmail.dvkarchive.gui.settings;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import javax.swing.JPanel;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DCheckBox;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DLabel;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DList;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DScrollPane;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DTextArea;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DTextField;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DCheckEvent;

//TODO SHORTEN THIS CRAP!
//TODO SELECT CURRENT FONT

/**
 * GUI for selecting the main font for DVK Archive.
 * 
 * @author Drakovek
 */
public class FontGUI extends JPanel implements DActionEvent, DCheckEvent {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -4675224451716546521L;

	/**
	 * List of installed system fonts.
	 */
	private String[] fonts;
	
	/**
	 * Saved program font
	 */
	private Font start_font;
	
	/**
	 * Saved anti-aliasing setting
	 */
	private boolean start_aa;
	
	/**
	 * Currently selected font
	 */
	private Font current_font;
	
	/**
	 * Currently selected anti-aliasing setting
	 */
	private boolean current_aa;
	
	/**
	 * BaseGUI for getting UI settings
	 */
	private BaseGUI base_gui;
	
	/**
	 * List for selecting font
	 */
	private DList font_lst;
	
	/**
	 * Used to show font preview
	 */
	private DTextArea preview_txt;
	
	/**
	 * Used for entering the font size
	 */
	private DTextField size_txt;
	
	/**
	 * Initializes the FontGUI.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 */
	public FontGUI(BaseGUI base_gui) {

		//GET CURRENT FONT SETTINGS
		this.fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		this.start_font = base_gui.get_font();
		this.start_aa = base_gui.use_aa();
		this.current_font = this.start_font;
		this.current_aa = this.start_aa;
		this.base_gui = base_gui;
		//CREATE SIZE PANEL
		this.size_txt = new DTextField(base_gui, this, "size");
		this.size_txt.setText(Integer.toString(this.start_font.getSize()));
		this.size_txt.always_allow_action();
		DLabel size_lbl = new DLabel(base_gui, this.size_txt, "size");
		JPanel size_pnl = base_gui.get_x_stack(size_lbl, 0, this.size_txt, 1);
		//CREATE BOTTOM PANEL
		DCheckBox bold_chk = new DCheckBox(base_gui, this, "bold", this.start_font.isBold());
		bold_chk.always_allow_action();
		JPanel mid_pnl = new JPanel();
		mid_pnl.setLayout(new GridLayout(1, 2, base_gui.get_space_size(), 1));
		mid_pnl.add(bold_chk);
		mid_pnl.add(size_pnl);
		DCheckBox aa_chk = new DCheckBox(base_gui, this, "aa", this.start_aa);
		aa_chk.always_allow_action();
		JPanel btm_pnl = base_gui.get_y_stack(mid_pnl, aa_chk);
		//CREATE FONT PANEL
		this.font_lst = new DList(base_gui, this, "font", false);
		this.font_lst.set_list(this.fonts, false);
		this.font_lst.always_allow_action();
		DScrollPane font_scr = new DScrollPane(this.font_lst);
		JPanel font_pnl = new JPanel();
		this.preview_txt = new DTextArea(base_gui);
		this.preview_txt.setWrapStyleWord(true);
		this.preview_txt.setLineWrap(true);
		this.preview_txt.setText(base_gui.get_language_string("preview_text"));
		DScrollPane preview_scr = new DScrollPane(this.preview_txt);
		font_pnl.setLayout(new GridLayout(1, 2, base_gui.get_space_size(), 1));
		font_pnl.add(font_scr);
		font_pnl.add(preview_scr);
		//CREATE MAIN LABEL
		DLabel lbl = new DLabel(base_gui, this.font_lst, "font");
		lbl.set_font_large();
		//CREATE MAIN PANEL
		setLayout(new BorderLayout());
		add(btm_pnl, BorderLayout.SOUTH);
		add(base_gui.get_spaced_panel(font_pnl, true, true, false, false), BorderLayout.CENTER);
		add(lbl, BorderLayout.NORTH);
	}
	
	/**
	 * Saves font settings, if changed.
	 * 
	 * @return Whether any font settings changed
	 */
	public boolean save() {
		resized();
		boolean changed = 
				!this.start_font.getFamily().equals(this.current_font.getFamily())
				|| this.start_font.getSize() != this.current_font.getSize()
				|| this.start_font.isBold() != this.current_font.isBold()
				|| this.start_aa != this.current_aa;
		if(changed) {
			this.base_gui.set_font(
					this.current_font.getFamily(),
					this.current_font.getSize(),
					this.current_font.isBold());
			this.base_gui.set_use_aa(this.current_aa);
			this.base_gui.save_preferences();
		}
		return changed;
	}

	/**
	 * Sets the current font.
	 * 
	 * @param family Font family
	 * @param size Font size
	 * @param is_bold Whether the font is bold
	 */
	private void set_font(String family, int size, boolean is_bold) {
		int font_type = Font.PLAIN;
		if(is_bold) {
			font_type = Font.BOLD;
		}
		this.current_font = new Font(family, font_type, size);
		this.preview_txt.setFont(this.current_font);
	}
	
	/**
	 * Sets the current font family.
	 * Based on font being selected from the font list.
	 */
	private void font_selected() {
		int sel = this.font_lst.getSelectedIndex();
		if(sel != -1) {
			set_font(this.fonts[sel],
					this.current_font.getSize(),
					this.current_font.isBold());
		}
	}
	
	/**
	 * Changes the size of the current font.
	 * Based on the value in the size text field.
	 */
	private void resized() {
		//GET SIZE FROM TEXT FIELD
		int size = 0;
		try {
			size = Integer.parseInt(this.size_txt.getText());
			if(size > 100) {
				size = 100;
			}
			if(size < 4) {
				size = 4;
			}
		}
		catch(NumberFormatException e) {
			size = this.current_font.getSize();
			this.size_txt.setText(Integer.toString(size));
		}
		//SET FONT
		set_font(this.current_font.getFamily(),
				size,
				this.current_font.isBold());
	}
	
	@Override
	public void check_event(String id, boolean checked) {
		switch(id) {
			case "bold":
				set_font(this.current_font.getFamily(),
						this.current_font.getSize(),
						checked);
				break;
			case "aa":
				this.current_aa = checked;
				break;
		}
	}

	@Override
	public void event(String id) {
		switch(id) {
			case "font":
				font_selected();
				break;
			case "size":
				resized();
				break;
		}
	}
}
