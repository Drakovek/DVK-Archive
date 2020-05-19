package com.gmail.drakovekmail.dvkarchive.gui.swing.compound;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.language.LanguageHandler;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DButton;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DDialog;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DEditorPane;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DFrame;
import com.gmail.drakovekmail.dvkarchive.gui.swing.components.DLabel;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;

/**
 * Dialog that allows the user to give input via buttons.
 * 
 * @author Drakovek
 */
public class DButtonDialog implements DActionEvent {
	
	/**
	 * Value to return, determined by button pressed in dialog
	 */
	private String value;
	
	/**
	 * Main button dialog
	 */
	private DDialog dialog;
	
	/**
	 * Opens the button dialog, returns id of button pressed.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 * @param owner DFrame owner of the dialog
	 * @param title Title ID of the dialog
	 * @param labels IDs for the dialog labels
	 * @param buttons IDs for the dialog buttons
	 * @return ID of the button pressed
	 */
	public String open(
			BaseGUI base_gui,
			DFrame owner,
			String title,
			String[] labels,
			String[] buttons) {
		base_gui.set_running(true);
		//CREATE BUTTON PANEL
		JPanel button_pnl = new JPanel();
		button_pnl.setLayout(new GridLayout(1, buttons.length, base_gui.get_space_size(), 1));
		for(int i = 0; i < buttons.length; i++) {
			DButton btn = new DButton(base_gui, this, buttons[i]);
			btn.always_allow_action();
			button_pnl.add(btn);
		}
		//CREATE MESSAGE PANEL
		JPanel msg_pnl = new JPanel();
		msg_pnl.setLayout(new GridLayout(labels.length, 1, 1, base_gui.get_space_size()));
		for(int i = 0; i < labels.length; i++) {
			DLabel lbl = new DLabel(base_gui, null, labels[i]);
			lbl.setHorizontalAlignment(SwingConstants.CENTER);
			msg_pnl.add(lbl);
		}
		JPanel stack_pnl = base_gui.get_y_stack(msg_pnl, button_pnl);
		JPanel full_pnl = base_gui.get_spaced_panel(stack_pnl);
		//OPEN DIALOG
		this.dialog = new DDialog(owner, full_pnl, LanguageHandler.get_text(base_gui.get_language_string(title)));
		this.dialog.setVisible(true);
		this.dialog = null;
		base_gui.set_running(false);
		return this.value;
	}
	
	/**
	 * Opens the button dialog, returns id of button pressed.
	 * Uses a DEditorPane to show HTML message.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 * @param owner DFrame owner of the dialog
	 * @param title Title ID of the dialog
	 * @param html HTML formatted dialog message
	 * @param buttons IDs for the dialog buttons
	 * @return ID of the button pressed
	 */
	public String open_html(
			BaseGUI base_gui,
			DFrame owner,
			String title,
			String html,
			String[] buttons) {
		base_gui.set_running(true);
		//CREATE BUTTON PANEL
		JPanel button_pnl = new JPanel();
		button_pnl.setLayout(new GridLayout(1, buttons.length, base_gui.get_space_size(), 1));
		for(int i = 0; i < buttons.length; i++) {
			DButton btn = new DButton(base_gui, this, buttons[i]);
			btn.always_allow_action();
			button_pnl.add(btn);
		}
		//CREATE MESSAGE PANEL
		DLabel color_ref = new DLabel(base_gui, null, "ok");
		DEditorPane epane = new DEditorPane(base_gui, color_ref);
		String html_str = base_gui.get_language_string(html);
		epane.set_text_html(html_str);
		JPanel stack_pnl = base_gui.get_y_stack(epane, button_pnl);
		JPanel full_pnl = base_gui.get_spaced_panel(stack_pnl);
		//OPEN DIALOG
		this.dialog = new DDialog(owner, full_pnl, LanguageHandler.get_text(base_gui.get_language_string(title)));
		this.dialog.setVisible(true);
		this.dialog = null;
		base_gui.set_running(false);
		return this.value;
	}

	@Override
	public void event(String id) {
		this.value = id;
		this.dialog.dispose();
	}
}
