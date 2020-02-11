package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
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
	private static final long serialVersionUID = -5215736291266793942L;

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
		set_select_multiple(multiple);
		this.cell_height = base_gui.get_font().getSize() * 2;
		this.setFixedCellHeight(this.cell_height);
		this.setLayoutOrientation(VERTICAL);
		this.setFont(base_gui.get_font());
		//SET ACTION LISTENER
		this.addListSelectionListener(new DListSelectionListener(base_gui, event, id));
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
}
