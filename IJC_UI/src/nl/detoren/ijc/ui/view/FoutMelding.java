/**
 * Copyright (C) 2016 Leo van der Meulen
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

import javax.swing.JOptionPane;

/**
 * Genereer popup met foutmelding. Deze moet geaccepteerd worden voordat
 * de applicatie verder kan.
 * Gebruik:
 *         FoutMelding.melding("Melding");
 * @author sv
 *
 */
public class FoutMelding {

	public static void melding(String infoMessage)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "Foutmelding", JOptionPane.INFORMATION_MESSAGE);
    }
}
