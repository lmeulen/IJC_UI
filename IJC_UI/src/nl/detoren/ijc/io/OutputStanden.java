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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import nl.detoren.ijc.data.groepen.Groepen;
import nl.detoren.ijc.ui.control.IJCController;

public class OutputStanden implements GroepenExportInterface {

	private final static Logger logger = Logger.getLogger(OutputStanden.class.getName());
    private static String ls = System.lineSeparator();

	/**
     * Sla de nieuwe stand op in een uitslag?-?.txt bestand en
     * in een json versie van resultaatVerwerkt
     */
    public boolean export(Groepen uitslag) {
		try {
			String bestandsnaam = "R" + uitslag.getPeriode() + "-" + uitslag.getRonde() + "Stand";
			logger.log(Level.INFO, "Sla uitslag op in bestand " + bestandsnaam);

			// Long variant
			if (IJCController.c().exportTextLong) {
				String dirName = "R" + uitslag.getPeriode() + "-" + uitslag.getRonde();
				new File(dirName).mkdirs();
				FileWriter writer = new FileWriter(dirName + File.separator + bestandsnaam + "-long.txt");
				writer.write(uitslag.toPrintableString(true));
				writer.write(ls + "Stand aangemaakt met " + IJCController.c().appTitle + " voor " + IJCController.c().verenigingNaam + ls);
				writer.close();
				IJCController.getInstance().setLaatsteExport(dirName + File.separator + bestandsnaam + "-long.txt");
			}

			// Short variant
			if (IJCController.c().exportTextShort) {
				String dirName = "R" + uitslag.getPeriode() + "-" + uitslag.getRonde();
				new File(dirName).mkdirs();

				FileWriter writer = new FileWriter(dirName + File.separator + bestandsnaam + ".txt");
				writer.write(uitslag.toPrintableString(false));
				writer.write(ls + "Stand aangemaakt met " + IJCController.c().appTitle + " voor " + IJCController.c().verenigingNaam + ls);
				writer.close();
				IJCController.getInstance().setLaatsteExport(dirName + File.separator + bestandsnaam + ".txt");
			}
			
			// GSON variant
			Gson gson = new Gson();
			String dirName = "R" + uitslag.getPeriode() + "-" + uitslag.getRonde();
			new File(dirName).mkdirs();
			FileWriter writer = new FileWriter( dirName + File.separator + bestandsnaam + ".json");
			writer.write(gson.toJson(IJCController.getI().getStatus()));
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

    }


}
