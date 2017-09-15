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
 */
package nl.detoren.ijc.io;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.wedstrijden.Groepswedstrijden;
import nl.detoren.ijc.data.wedstrijden.Serie;
import nl.detoren.ijc.data.wedstrijden.Wedstrijd;
import nl.detoren.ijc.data.wedstrijden.Wedstrijden;
import nl.detoren.ijc.ui.control.IJCController;
import nl.detoren.ijc.ui.view.FoutMelding;

/**
 * Sla het wedstrijdschema op in Excel
 *
 * @author Leo van der Meulen
 *
 */
public class OutputSpeelschema implements WedstrijdenExportInterface {

	private final static Logger logger = Logger.getLogger(OutputSpeelschema.class.getName());

	/**
	 * Create the textversion of the sheet
	 * @param wedstrijden The round to store in the Excel file
	 */
	public boolean export(Wedstrijden wedstrijden) {
		try {
			logger.log(Level.INFO, "Wedstrijden wegschrijven naar Textbestand");
			int periode = wedstrijden.getPeriode();
			int ronde = wedstrijden.getRonde();
			String rpString = "Periode " + periode + ", Ronde " + ronde;
			String datum = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(Calendar.getInstance().getTime());

			String dirName = "R" + wedstrijden.getPeriode() + "-" + wedstrijden.getRonde();
			new File(dirName).mkdirs();

			String bestandsnaam = "R" + wedstrijden.getPeriode() + "-" + wedstrijden.getRonde() + "Wedstrijden.txt";
			FileWriter writer = new FileWriter( dirName + File.separator + bestandsnaam);
            writer.write("Speelschema aangemaakt met " + IJCController.c().appTitle + " voor " + IJCController.c().verenigingNaam + "\n\n");
			for (Groepswedstrijden gws: wedstrijden.getGroepswedstrijden()) {
				writer.write("\n");
				writer.write(Groep.geefNaam(gws.getNiveau()).toUpperCase());
				writer.write(" " + rpString + " (" + datum + ")\n\n");

				int i = 1;
				for (Serie s: gws.getSeries()) {
					writer.write("Serie " + i++ + ":\n\n");
					for (Wedstrijd w : s.getWedstrijden()) {
						writer.write(w.toString() + "\n\n");
					}

				}

				if (gws.getTriowedstrijden().size() > 0) {
					writer.write("\n");
					writer.write("Trio:\n\n");
					for (Wedstrijd w : gws.getTriowedstrijden()) {
						writer.write(w.toString() + "\n\n");
					}
				}
			}
			writer.close();
			return true;
	} catch (Exception e) {
		logger.log(Level.WARNING, "Export failed");
		FoutMelding.melding("Fout bij opslaan wedstrijdenbestand: " + e.getMessage());
		return false;
	}
}
}