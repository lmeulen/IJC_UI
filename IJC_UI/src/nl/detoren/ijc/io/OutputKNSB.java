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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.detoren.ijc.data.wedstrijden.Groepswedstrijden;
import nl.detoren.ijc.data.wedstrijden.Wedstrijd;
import nl.detoren.ijc.data.wedstrijden.Wedstrijden;
import nl.detoren.ijc.ui.control.IJCController;

public class OutputKNSB implements WedstrijdenExportInterface {

	private final static Logger logger = Logger.getLogger(OutputKNSB.class.getName());

	/**
	 * Sla de nieuwe stand op in een R?-?KNSB.csv bestand en in een json versie
	 * van resultaatVerwerkt
	 */
	public boolean export(Wedstrijden wedstrijden) {
		try {
			String bestandsnaam = "R" + wedstrijden.getPeriode() + "-" + wedstrijden.getRonde() + "KNSB";
			logger.log(Level.INFO, "Sla uitslag op in bestand " + bestandsnaam);
			FileWriter writer = new FileWriter(bestandsnaam + ".csv");
			writer.write(getHeader(wedstrijden.getPeriode(), wedstrijden.getRonde()));
			int i = 1;
			for (Groepswedstrijden gws : wedstrijden.getGroepswedstrijden()) {
				for (Wedstrijd w : gws.getWedstrijden()) {
					String result = verwerkWedstrijd(w, i++);
					if (result != null) {
						writer.write(result + "\n");
					}
				}
			}
			writer.close();
			return true;
		} catch (IOException e) {
			logger.log(Level.WARNING, "Export mislukt : " + e.getMessage());
			return false;
		}
	}

	/**
	 * Zet ��n wedstrijd om naar KNSB file formaat
	 *
	 * @param w
	 *            Wedstrijd
	 * @return regel voor KNSB bestand
	 */
	private String verwerkWedstrijd(Wedstrijd w, int volgnummer) {
		String result = "";
		if (w.getWit().isKNSBLid() && w.getZwart().isKNSBLid()) {
			// 900;1;2016-10-10;8000000;8000001;1;Piet Puk;Jan Janssen;
			result += "900;" + volgnummer + ";";
			result += new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
			result += ";" + w.getWit().getKNSBnummer() + ";" + w.getZwart().getKNSBnummer() + ";";
			result += w.getUitslag() + ";" + w.getWit().getNaam() + ";" + w.getZwart().getNaam() + ";";
			logger.log(Level.INFO, result);
			return result;
		} else {
			logger.log(Level.INFO, "Niet opgeslagen: " + w.toString());
			return null;
		}
	}

	private String getHeader(int periode, int ronde) {
		String result = "";
		result += "12;" + IJCController.c().competitieNaam + ", ronde " + ronde + ", periode " + periode + "\n";
		result += "22;" + IJCController.c().competitieLocatie + "\n" + "32;NED\n";
		String datum = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
		result += "42;" + datum + "\n" + "52;" + datum + "\n";
		result += "102;" + IJCController.c().contactPersoonNaam + " " + IJCController.c().contactPersoonEmail + "\n";
		result += "122;15 tot 60 min pppp\n\n";
		result += ";ronde_nr;ronde_dat;relnr_w;relnr_z;score;naam_w;naam_z\n";
		return result;
	}
}
