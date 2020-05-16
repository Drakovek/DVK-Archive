package com.gmail.drakovekmail.dvkarchive.gui.swing.components;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import com.gmail.drakovekmail.dvkarchive.gui.BaseGUI;
import com.gmail.drakovekmail.dvkarchive.gui.work.DRunnable;
import com.gmail.drakovekmail.dvkarchive.gui.work.DWorker;

/**
 * ProgressBar UI object for DVK Archive.
 * 
 * @author Drakovek
 */
public class DProgressBar extends JProgressBar implements DWorker {

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = -9066499367931066344L;

	/**
	 * Whether progress is indeterminate
	 */
	private boolean ind;
	
	/**
	 * Whether progress bar shows text
	 */
	private boolean painted;
	
	/**
	 * Current value of determinate progress
	 */
	private int value;
	
	/**
	 * Max value of determinate progress
	 */
	private int max;
	
	/**
	 * Initializes the DProgressBar object.
	 * 
	 * @param base_gui BaseGUI for getting UI settings
	 */
	public DProgressBar(BaseGUI base_gui) {
		this.setFont(base_gui.get_font());
	}
	
	/**
	 * Adds request to set the progress bar to Swing EDT.
	 * 
	 * @param indeterminate Whether progress is indeterminate
	 * @param painted Whether progress bar shows text
	 * @param value Current value of determinate progress
	 * @param max Max value of determinate progress
	 */
	public void set_progress(
			boolean indeterminate,
			boolean painted,
			int value,
			int max) {
		this.ind = indeterminate;
		this.painted = painted;
		this.value = value;
		this.max = max;
		SwingUtilities.invokeLater(new DRunnable(this, ""));
	}
	
	/**
	 * Sets the progress bar to show the currently set parameters.
	 */
	private void set_progress() {
		//SET WHETHER INDETERMINATE
		this.setIndeterminate(this.ind);
		//SET MINIMUM AND MAXIMUM VALUES
		this.setMinimum(0);
		if(this.value > 0 && this.value <= this.max) {
			this.setMaximum(this.max);
			this.setValue(this.value);
		}
		else {
			this.setMaximum(0);
			this.setValue(0);
		}
		//SET TEXT
		if(this.painted) {
			this.setStringPainted(true);
			int pc = (int)(((double)this.value/(double)this.max) * 100);
			this.setString(Integer.toString(pc) + "%");
		}
		else
		{
			this.setStringPainted(false);
		}
	}

	@Override
	public void run(String id) {
		set_progress();
	}

	@Override
	public void done(String id) {}
}
