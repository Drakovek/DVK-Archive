package com.gmail.drakovekmail.dvkarchive.gui.swing.compound;

import java.awt.GridLayout;
import javax.swing.JPanel;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.language.LanguageHandler;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DButton;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DDialog;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DFrame;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DLabel;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DTextField;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;

/**
 * Dialog UI that allows the user to input text.
 * 
 * @author Drakovek
 */
public class DTextDialog implements DActionEvent {
	
	/**
	 * Value to return, determined by text in text
	 */
	private String value;
	
	/**
	 * Main DDialog for the object
	 */
	private DDialog dialog;
	
	/**
	 * DTextField for user input
	 */
	private DTextField text;
	
	/**
	 * Opens a text dialog which returns text.
	 * 
	 * @param base_gui BaseGUI for UI settings
	 * @param owner DFrame owner of the dialog
	 * @param title Title ID for the dialog
	 * @param messages Language IDs for messages in dialog
	 * @return Text entered in the text dialog
	 */
	public String open(
			BaseGUI base_gui,
			DFrame owner,
			String title,
			final String[] messages) {
		base_gui.set_running(true);
		//CREATE BUTTON PANEL
		JPanel btn_pnl = new JPanel();
		btn_pnl.setLayout(new GridLayout(1, 2, base_gui.get_space_size(), 1));
		DButton cancel_btn = new DButton(base_gui, this, "cancel");
		cancel_btn.always_allow_action();
		btn_pnl.add(cancel_btn);
		DButton ok_btn = new DButton(base_gui, this, "ok");
		ok_btn.always_allow_action();
		btn_pnl.add(ok_btn);
		//CREATE TEXT PANEL
		JPanel text_pnl = new JPanel();
		text_pnl.setLayout(new GridLayout(messages.length, 1, 1, base_gui.get_space_size()));
		for(int i = 0; i < messages.length; i++) {
			DLabel label = new DLabel(base_gui, null, messages[i]);
			text_pnl.add(label);
		}
		//CENTER PANEL
		this.text = new DTextField(base_gui, this, "ok");
		this.text.always_allow_action();
		JPanel center_pnl = base_gui.get_y_stack(text_pnl, this.text);
		//FULL PANEL
		JPanel full_pnl = base_gui.get_spaced_panel(base_gui.get_y_stack(center_pnl, btn_pnl));
		//OPEN DIALOG
		this.dialog = new DDialog(owner, full_pnl, LanguageHandler.get_text(base_gui.get_language_string(title)));
		this.dialog.setVisible(true);
		this.dialog = null;
		base_gui.set_running(false);
		return this.value;
	}

	@Override
	public void event(String id) {
		this.value = null;
		if(id.equals("ok")) {
			this.value = this.text.getText();
		}
		this.dialog.dispose();
	}
}
