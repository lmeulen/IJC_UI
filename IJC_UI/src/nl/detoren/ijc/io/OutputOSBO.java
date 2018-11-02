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
 * - Complexe wijze voor formatteren getallen, overgaan op String.format(...)
 */
package nl.detoren.ijc.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.data.wedstrijden.Wedstrijd;
import nl.detoren.ijc.data.wedstrijden.Wedstrijden;
import nl.detoren.ijc.ui.control.IJCController;
import nl.detoren.ijc.ui.util.Utils;

public class OutputOSBO implements WedstrijdenExportInterface {

	private final static Logger logger = Logger.getLogger(OutputOSBO.class.getName());
    private static String ls = System.lineSeparator();

	DecimalFormat int3p;

	public OutputOSBO() {
		Locale loc = new Locale("de", "DE");
		int3p = (DecimalFormat) NumberFormat.getNumberInstance(loc);
		int3p.applyPattern("000");

	}

	/**
	 * Sla de nieuwe stand op in een R?-?OSBO.txt bestand en in een json versie
	 * van resultaatVerwerkt.
	 * Sla alleen reglementaire uitslagen op
	 */
	public boolean export(Wedstrijden wedstrijden) {
		try {
			String vereniging = IJCController.c().verenigingNaam;
			if (vereniging.startsWith("SV " )) {
				vereniging = vereniging.substring(3);
			}
			String bestandsnaam = "OSBO " + vereniging + " P" + wedstrijden.getPeriode() + "R" + wedstrijden.getRonde() + ".txt";
			logger.log(Level.INFO, "Sla uitslag op in bestand " + bestandsnaam);

			OSBOResultaat resultaat = maakOSBOData(wedstrijden);
			String dirName = "R" + wedstrijden.getPeriode() + "-" + wedstrijden.getRonde();
			new File(dirName).mkdirs();

			FileWriter writer = new FileWriter( dirName + File.separator + bestandsnaam);
			writer.write(getHeader(wedstrijden.getPeriode(), wedstrijden.getRonde(), resultaat.getAantalSpelers()));
			for (OSBOSpeler speler : resultaat.spelers) {
				String result = verwerkSpeler(speler);
				if (result != null) {
					writer.write(result);
				}
			}
			writer.close();
			return true;
		} catch (IOException ex) {
			logger.log(Level.WARNING, "Export mislukt : " + ex.getMessage());
            Utils.stacktrace(ex);

			return false;
		}
	}

	private String getHeader(int periode, int ronde, int regels) {
		String result = "";
		result += "012 OSBO " + IJCController.c().competitieNaam + ", ronde " + ronde + ", periode " + periode + ls;
		result += "022 " + IJCController.c().competitieLocatie + ls;
		result += "032 NED" + ls + "033 OSBO" + ls + "034 " + ls;
		String datum = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
		result += "042 " + datum + ls + "052 " + datum + ls + "062 " + regels + ls;
		result += "102 " + IJCController.c().contactPersoonNaam + " " + IJCController.c().contactPersoonEmail + ls;
		result += "122 15 tot 60 min pppp" + ls + ls;
		result += "132                                                                                        ";
		String datum2 = new SimpleDateFormat("yy.MM.dd").format(Calendar.getInstance().getTime());
		result += datum2 + "  " + datum2 + ls + ls + ls;
		result += "DDD-SSSS sTTT NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN RRRR FFF IIIIIIIIIII yyyy/mm/dd PPPP RRRR  1111 1 1  2222 2 2  3333 3 3  4444 4 4  5555 5 5  6666 6 6  7777 7 7  8888 8 8  9999 9 9" + ls;
		return result;
	}

	private OSBOResultaat maakOSBOData(Wedstrijden uitslag) {
		OSBOResultaat resultaat = new OSBOResultaat();
		for (Wedstrijd w : uitslag.getAlleWedstrijden()) {
			Speler wit = w.getWit();
			Speler zwart = w.getZwart();
			if (wit.isKNSBLid() && zwart.isKNSBLid() && w.isNietReglementair()) {
				// Bepaal spelers
				int witID = resultaat.getSpelerIDfromKNSB(wit.getKNSBnummer());
				witID = (witID > 0) ? witID : resultaat.addSpeler(new OSBOSpeler(wit.getKNSBnummer(), wit.getNaam()));
				int zwartID = resultaat.getSpelerIDfromKNSB(zwart.getKNSBnummer());
				zwartID = (zwartID > 0) ? zwartID : resultaat.addSpeler(new OSBOSpeler(zwart.getKNSBnummer(), zwart.getNaam()));
				// Bepaal ronde
				int ronde = Math.max(resultaat.getSpeler(witID).getLaatsteRonde(), resultaat.getSpeler(zwartID).getLaatsteRonde());
				ronde++;
				// witwedstrijd
				OSBOWedstrijd wedstrijdWit = new OSBOWedstrijd();
				wedstrijdWit.ronde = ronde;
				wedstrijdWit.tegenstander = zwartID;
				wedstrijdWit.kleur = "w";
				wedstrijdWit.dblResultaat = (w.getUitslag() == 1) ? 1.0 : ((w.getUitslag() == 2) ? 0.0 : 0.5);
				wedstrijdWit.strResultaat = (w.getUitslag() == 1) ? "1" : ((w.getUitslag() == 2) ? "0" : "=");
				resultaat.getSpeler(witID).addWedstrijd(wedstrijdWit);
				// zwart wedstrijd
				OSBOWedstrijd wedstrijdZwart = new OSBOWedstrijd();
				wedstrijdZwart.ronde = ronde;
				wedstrijdZwart.tegenstander = witID;
				wedstrijdZwart.kleur = "z";
				wedstrijdZwart.dblResultaat = (w.getUitslag() == 1) ? 0.0 : ((w.getUitslag() == 2) ? 1.0 : 0.5);
				wedstrijdZwart.strResultaat = (w.getUitslag() == 1) ? "0" : ((w.getUitslag() == 2) ? "1" : "=");
				resultaat.getSpeler(zwartID).addWedstrijd(wedstrijdZwart);
			}
		}
		return resultaat;
	}

	/**
	 * Converter speler naar een export regel voor het OSBO bestand
	 *
	 * @param speler
	 * @return
	 */
	private String verwerkSpeler(OSBOSpeler speler) {
		String result = "";
		// 001 16 Piet Pietersen NED 8588318 0,0 16 5 z 0 8 w 0
		result += "001  ";
		result += int3p.format(speler.nr).replaceAll("\\G0", " ") + "      " + speler.naam;
		while (result.length() < 53)
			result += " ";
		result += "NED     " + speler.KNSB + "             ";
		result += String.format("%3.1f", speler.getPunten()) + "  ";
		result += int3p.format(speler.nr).replaceAll("\\G0", " ");
		for (int i = 1; i <= 10; i++) {
			OSBOWedstrijd w = speler.getRonde(i);
			if (w != null) {
				result += "   " + int3p.format(w.tegenstander).replaceAll("\\G0", " ") + " " + w.kleur + " "
						+ w.strResultaat;
			} else {
				result += "          ";
			}
		}
		return result.trim() + ls;
	}

	class OSBOResultaat {
		public ArrayList<OSBOSpeler> spelers;

		OSBOResultaat() {
			spelers = new ArrayList<>();
		}

		public int addSpeler(OSBOSpeler s) {
			spelers.add(s);
			s.nr = spelers.size();
			return s.nr;
		}

		public int getSpelerIDfromKNSB(int KNSB) {
			for (OSBOSpeler s : spelers) {
				if (s.KNSB == KNSB)
					return s.nr;
			}
			return -1;
		}

		public OSBOSpeler getSpeler(int id) {
			return spelers.get(id - 1);
		}

		public int getAantalSpelers() {
			return spelers.size();
		}
	}

	class OSBOSpeler {
		public int nr;
		public int KNSB;
		public String naam;
		public ArrayList<OSBOWedstrijd> wedstrijden = new ArrayList<>();

		public OSBOSpeler(int knsb, String naam) {
			KNSB = knsb;
			this.naam = naam;
		}

		public void addWedstrijd(OSBOWedstrijd w) {
			wedstrijden.add(w);
		}

		public double getPunten() {
			double punten = 0;
			for (OSBOWedstrijd w : wedstrijden)
				punten += w.dblResultaat;
			return punten;
		}

		public int getLaatsteRonde() {
			int result = 0;
			for (OSBOWedstrijd w : wedstrijden) {
				result = (w.ronde > result) ? w.ronde : result;
			}
			return result;
		}

		public OSBOWedstrijd getRonde(int ronde) {
			for (OSBOWedstrijd w : wedstrijden) {
				if (w.ronde == ronde)
					return w;
			}
			return null;
		}
	}

	class OSBOWedstrijd {
		public int ronde;
		public int tegenstander;
		public String kleur;
		public String strResultaat; // '0'=verlies, '1'=winst, '='=gelijk
		public double dblResultaat; // 0 = verlies, 1 = winst, 0.5 = gelijk
	}
}
