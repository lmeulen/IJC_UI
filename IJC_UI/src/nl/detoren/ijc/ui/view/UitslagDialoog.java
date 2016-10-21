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
 * - ...
 */
package nl.detoren.ijc.ui.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeListener;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

import nl.detoren.ijc.ui.control.IJCController;

/**
 * Toont het uitslagbestand
 * @author Leo.vanderMeulen
 *
 */
public class UitslagDialoog {

	public void createDialog() {
		JDialog dialog = new JDialog();
		dialog.setLocationByPlatform(true);
		JTextArea txtArea = new JTextArea(40,150);
		txtArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		txtArea.setFont(new Font("courier new", Font.PLAIN, 12));
		txtArea.setLineWrap(false);
		JScrollPane txtAreaScroll = new JScrollPane();
		txtAreaScroll.setViewportView(txtArea);
		txtAreaScroll.setAutoscrolls(true);

		File file;
		String line = null;
		StringBuilder fileContents = new StringBuilder();
		try {
			file = new File(			IJCController.getInstance().getLaatsteExport());
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				fileContents.append(line + "\n");
			}
			reader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		txtArea.setText(fileContents.toString());
		txtArea.setLocation(0, 0);
		txtArea.setCaretPosition(0);
		dialog.add(txtAreaScroll);
		dialog.pack();
		dialog.setVisible(true);
	}
}