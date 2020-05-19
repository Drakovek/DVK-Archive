package com.gmail.drakovekmail.dvkarchive.gui.swing.listeners;

import java.awt.Desktop;
import java.net.URI;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Detects a hyperlink being clicked, and opens URL in web browser.
 * 
 * @author Drakovek
 */
public class DHyperlinkListener implements HyperlinkListener {

	@Override
	public void hyperlinkUpdate(HyperlinkEvent event) {
		if(event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			//GET URL
			String url = null;
			try {
				url = event.getURL().toString();
			}
			catch(NullPointerException e) {
				url = null;
				try {
					url = event.getDescription();
				}
				catch(Exception f) {
					url = null;
				}
			}
			//OPEN URL
			if(url != null && Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().browse(new URI(url));
				}
				catch(Exception f) {}
			}
		}
	}
	
}
