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

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import nl.detoren.ijc.data.groepen.Groepen;
import nl.detoren.ijc.ui.control.IJCController;

public class OutputTekst implements GroepenExportInterface {

	private final static Logger logger = Logger.getLogger(OutputTekst.class.getName());
    private static String ls = System.lineSeparator();

	/**
     * Sla de nieuwe stand op in een uitslag?-?.txt bestand en
     * in een json versie van resultaatVerwerkt
     */
    public boolean export(Groepen uitslag) {
		try {
			String bestandsnaam = "R" + uitslag.getPeriode() + "-" + uitslag.getRonde() + "Uitslag";
			logger.log(Level.INFO, "Sla uitslag op in bestand " + bestandsnaam);

			// Short variant
			if (IJCController.c().exportTextLong) {
				FileWriter writer = new FileWriter(bestandsnaam + ".txt");
				writer.write(uitslag.toPrintableString(false));
				writer.write(ls + "Stand aangemaakt met " + IJCController.c().appTitle + " voor " + IJCController.c().verenigingNaam + ls);
				writer.close();
				IJCController.getInstance().setLaatsteExport(bestandsnaam + ".txt");
			}

			// Long variant
			if (IJCController.c().exportTextLong) {
				FileWriter writer = new FileWriter(bestandsnaam + "-long.txt");
				writer.write(uitslag.toPrintableString(true));
				writer.write(ls + "Stand aangemaakt met " + IJCController.c().appTitle + " voor " + IJCController.c().verenigingNaam + ls);
				writer.close();
				IJCController.getInstance().setLaatsteExport(bestandsnaam + "-long.txt");
			}
			// GSON variant
			Gson gson = new Gson();
			FileWriter writer = new FileWriter(bestandsnaam + ".json");
			writer.write(gson.toJson(uitslag));
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

    }


}
