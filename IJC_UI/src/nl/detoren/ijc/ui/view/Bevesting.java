	/**
	 * Copyright (C) 202 Lars Dam
	 * This program is free software; you can redistribute it and/or modify
	 * it under the terms of the GNU General Public License as published by
	 * the Free Software Foundation version 3.0
	 * This program is distributed in the hope that it will be useful,
	 * but WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	 * GNU General Public License for more details.
	 * See: http://www.gnu.org/licenses/gpl-3.0.html
	 *  
	 * Problemen in deze code:
	 * - ...
	 */

package nl.detoren.ijc.ui.view;

//import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Bevesting {

	public static int YesNoCancel(String infoMessage) {
		// ImageIcon icon = new ImageIcon("Bevestiging.png");
	    //JOptionPane.showConfirmDialog(null, infoMessage, "Bevestiging", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, icon);
	    return JOptionPane.showConfirmDialog(null, infoMessage, "Bevestiging", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
	}

	public static int YesNo(String infoMessage) {
		// ImageIcon icon = new ImageIcon("Bevestiging.png");
	    //JOptionPane.showConfirmDialog(null, infoMessage, "Bevestiging", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, icon);
	    return JOptionPane.showConfirmDialog(null, infoMessage, "Bevestiging", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null);
	}
}

