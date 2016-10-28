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

public class OutputOSBO implements WedstrijdenExportInterface {

	private final static Logger logger = Logger.getLogger(OutputOSBO.class.getName());

	DecimalFormat int3p;

	public OutputOSBO() {
		Locale loc = new Locale("de", "DE");
		int3p = (DecimalFormat) NumberFormat.getNumberInstance(loc);
		int3p.applyPattern("000");

	}

	/**
	 * Sla de nieuwe stand op in een R?-?OSBO.txt bestand en in een json versie
	 * van resultaatVerwerkt
	 */
	public boolean export(Wedstrijden wedstrijden) {
		try {
			String bestandsnaam = "R" + wedstrijden.getPeriode() + "-" + wedstrijden.getRonde() + "OSBO.txt";
			logger.log(Level.INFO, "Sla uitslag op in bestand " + bestandsnaam);

			OSBOResultaat resultaat = maakOSBOData(wedstrijden);

			FileWriter writer = new FileWriter(bestandsnaam);
			writer.write(getHeader(wedstrijden.getPeriode(), wedstrijden.getRonde(), resultaat.getAantalSpelers()));
			for (OSBOSpeler speler : resultaat.spelers) {
				String result = verwerkSpeler(speler);
				if (result != null) {
					writer.write(result);
				}
			}
			writer.close();
			return true;
		} catch (IOException e) {
			logger.log(Level.WARNING, "Export mislukt : " + e.getMessage());
			return false;
		}
	}

	private String getHeader(int periode, int ronde, int regels) {
		String result = "";
		result += "012 OSBO " + IJCController.c().competitieNaam + ", ronde " + ronde + ", periode " + periode + "\n";
		result += "022 " + IJCController.c().competitieLocatie + "\n";
		result += "032 NED\n" + "033 OSBO\n" + "034 \n";
		String datum = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
		result += "042 " + datum + "\n" + "052 " + datum + "\n" + "062 " + regels + "\n";
		result += "102 " + IJCController.c().contactPersoonNaam + " " + IJCController.c().contactPersoonEmail + "\n";
		result += "122 15 tot 60 min pppp\n" + "\n";
		result += "132                                                                                        ";
		String datum2 = new SimpleDateFormat("yy.MM.dd").format(Calendar.getInstance().getTime());
		result += datum2 + "  " + datum2 + "\n\n\n";
		result += "DDD-SSSS sTTT NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN RRRR FFF IIIIIIIIIII yyyy/mm/dd PPPP RRRR  1111 1 1  2222 2 2  3333 3 3  4444 4 4  5555 5 5  6666 6 6  7777 7 7  8888 8 8  9999 9 9\n";
		return result;
	}

	private OSBOResultaat maakOSBOData(Wedstrijden uitslag) {
		OSBOResultaat resultaat = new OSBOResultaat();
		for (Wedstrijd w : uitslag.getAlleWedstrijden()) {
			Speler wit = w.getWit();
			Speler zwart = w.getZwart();
			if (wit.isKNSBLid() && zwart.isKNSBLid()) {
				int witID = resultaat.getSpelerIDfromKNSB(wit.getKNSBnummer());
				witID = (witID > 0) ? witID : resultaat.addSpeler(new OSBOSpeler(wit.getKNSBnummer(), wit.getNaam()));
				int zwartID = resultaat.getSpelerIDfromKNSB(zwart.getKNSBnummer());
				zwartID = (zwartID > 0) ? zwartID
						: resultaat.addSpeler(new OSBOSpeler(zwart.getKNSBnummer(), zwart.getNaam()));
				// witwedstrijd
				OSBOWedstrijd wedstrijdWit = new OSBOWedstrijd();
				wedstrijdWit.tegenstander = zwartID;
				wedstrijdWit.kleur = "w";
				wedstrijdWit.dblResultaat = (w.getUitslag() == 1) ? 1.0 : ((w.getUitslag() == 2) ? 0.0 : 0.5);
				wedstrijdWit.strResultaat = (w.getUitslag() == 1) ? "1" : ((w.getUitslag() == 2) ? "0" : "=");
				resultaat.getSpeler(witID).addWedstrijd(wedstrijdWit);
				// zwart wedstrijd
				OSBOWedstrijd wedstrijdZwart = new OSBOWedstrijd();
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
		for (OSBOWedstrijd w : speler.wedstrijden) {
			if (w != null)
				result += "   " + int3p.format(w.tegenstander).replaceAll("\\G0", " ") + " " + w.kleur + " "
						+ w.strResultaat;
		}
		return result + "\n";
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
		public OSBOWedstrijd[] wedstrijden = new OSBOWedstrijd[10];

		public OSBOSpeler(int knsb, String naam) {
			KNSB = knsb;
			this.naam = naam;
		}

		public void addWedstrijd(OSBOWedstrijd w) {
			for (int i = 0; i < 10; i++) {
				if (wedstrijden[i] == null) {
					wedstrijden[i] = w;
					return;
				}
			}
		}

		public double getPunten() {
			double punten = 0;
			for (int i = 0; i < 10; i++) {
				if (wedstrijden[i] != null) {
					punten += wedstrijden[i].dblResultaat;
				}
			}
			return punten;
		}
	}

	class OSBOWedstrijd {
		public int tegenstander;
		public String kleur;
		public String strResultaat; // '0'=verlies, '1'=winst, '='=gelijk
		public double dblResultaat; // 0 = veriies, 1 = winst, 0.5 = gelijk
	}
}
