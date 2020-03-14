package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.language.LanguageHandler;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DActionEvent;
import com.gmail.drakovekmail.dvkarchive.gui.swing.listeners.DListSelectionListener;

/**
 * List UI object for DVK Archive.
 * 
 * @author Drakovek
 */
public class DList extends JList<String> {
	
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 516214302380203354L;

	/**
	 * BaseGUI for getting UI settings
	 */
	private BaseGUI base_gui;
	
	/**
	 * DActionEvent to call when list item is selected
	 */
	private DActionEvent event;
	
	/**
	 * ID for the DList
	 */
	private String id;
	
	/**
	 * Listener for when list item is selected
	 */
	private DListSelectionListener listener;

	/**
	 * Height of one cell in the DList
	 */
	int cell_height;
	
	/**
	 * Initializes the DList object.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 * @param event DActionEvent to call when list item is selected
	 * @param id ID for the DList
	 * @param multiple Whether to allow selecting multiple cells
	 */
	public DList(BaseGUI base_gui, DActionEvent event, String id, boolean multiple) {
		this.base_gui = base_gui;
		this.event = event;
		this.id = id;
		set_select_multiple(multiple);
		this.cell_height = base_gui.get_font().getSize() * 2;
		this.setFixedCellHeight(this.cell_height);
		this.setLayoutOrientation(VERTICAL);
		this.setFont(base_gui.get_font());
		//SET ACTION LISTENER
		this.listener = new DListSelectionListener(base_gui, event, id);
		this.addListSelectionListener(this.listener);
	}
	
	/**
	 * Sets whether multiple cells in DList can be selected.
	 * 
	 * @param multiple Whether to allow selecting multiple cells
	 */
	public void set_select_multiple(boolean multiple) {
		if(multiple) {
			this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		}
		else {
			this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
	}
	
	/**
	 * Sets the list to allow actions to be recorded,
	 * even while a process is running.
	 */
	public void always_allow_action() {
		this.removeListSelectionListener(this.listener);
		this.listener = new DListSelectionListener(
				null, 
				this.event,
				this.id);
		this.addListSelectionListener(this.listener);
	}
	
	/**
	 * Sets the data the list will show.
	 * 
	 * @param items Items to show in the list
	 * @param are_ids Whether items are language IDs
	 */
	public void set_list(String[] items, boolean are_ids) {
		String[] strs = new String[items.length];
		for(int i = 0; i < strs.length; i++) {
			strs[i] = items[i];
			if(are_ids) {
				strs[i] = this.base_gui.get_language_string(strs[i]);
				strs[i] = LanguageHandler.get_text(strs[i]);
			}
			strs[i] = " " + strs[i] + "    ";
		}
		setListData(strs);
	}
}
