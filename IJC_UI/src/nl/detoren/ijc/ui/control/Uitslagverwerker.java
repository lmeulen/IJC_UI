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
package nl.detoren.ijc.ui.control;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.groepen.Groepen;
import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.data.wedstrijden.Groepswedstrijden;
import nl.detoren.ijc.data.wedstrijden.Serie;
import nl.detoren.ijc.data.wedstrijden.Wedstrijd;
import nl.detoren.ijc.data.wedstrijden.Wedstrijden;
import nl.detoren.ijc.io.GroepenReader;

/**
 *
 * Verwerk uitslagen in een nieuwe stand
 *
 */
public class Uitslagverwerker {

	private final static Logger logger = Logger.getLogger(GroepenReader.class.getName());

	/**
	 * Verwerk de wedstrijduitslagen en werk de standen bij
	 *
	 * @param spelersgroepen
	 *            De spelersgroepen
	 * @param wedstrijden
	 *            De gespeelde wedstrijden
	 * @return Bijgewerkte standen
	 */
	public Groepen verwerkUitslag(Groepen spelersgroepen, Wedstrijden wedstrijden, ArrayList<Speler> externGespeeld) {
		Groepen updateGroepen = new Groepen();
		updateGroepen.setPeriode(spelersgroepen.getPeriode());
		updateGroepen.setRonde(spelersgroepen.getRonde());
		for (Groep groep : spelersgroepen.getGroepen()) {
			logger.log(Level.INFO, "Verwerk uitslag voor groep " + groep.getNaam());
			Groep bijgewerkt = new Groep();
			bijgewerkt.setNiveau(groep.getNiveau());
			for (Speler speler : groep.getSpelers()) {
				boolean extern = heeftExternGespeeld(speler, externGespeeld);
				logger.log(Level.INFO, "Speler " + speler.getNaam() + ", Extern ? " + extern);
				Speler update = updateSpeler(speler, wedstrijden, extern);
				bijgewerkt.addSpeler(update);
			}
			updateGroepen.addGroep(bijgewerkt);
		}
		updateRating(updateGroepen, wedstrijden);
		return updateGroepen;
	}

	/**
	 * Bepaal of de gegeven speler extern heeft gespeeld
	 *
	 * @param speler
	 *            De betreffende speler
	 * @param hebbenExternGespeeld
	 *            Lijst van spelers die extern hebben gespeeld
	 * @return
	 */
	private boolean heeftExternGespeeld(Speler speler, ArrayList<Speler> hebbenExternGespeeld) {
		if ((speler != null) && (hebbenExternGespeeld != null) && (hebbenExternGespeeld.size() > 0)) {
			for (Speler s : hebbenExternGespeeld) {
				if (s.gelijkAan(speler))
					return true;
			}
		}
		return false;
	}

	/**
	 * Return een bijgewerkte versie van een speler. Bijgewerkt zijn aantal
	 * punten en zijn rating
	 *
	 * @param speler
	 *            Speler om bij te werken
	 * @param wedstrijden
	 *            Alle wedstrijden
	 * @return Bijgewerkte speler
	 */
	private Speler updateSpeler(Speler speler, Wedstrijden wedstrijden, boolean extern) {
		ArrayList<Wedstrijd> spelerWedstrijden = getWedstrijdenVoorSpeler(speler, wedstrijden);
		Speler updateSpeler = new Speler(speler);
		int aantalgewonnen = 0;
		int aantalremise = 0;
		int wedstrijdenHoger = 0;
		// Standaardpunt
		int puntenbij = 1;
		logger.log(Level.INFO, "      Aanwezigheidspunt :" + puntenbij);
		if (!speler.isAanwezig()) {
			// Speler heeft mogelijk wel reglementair verloren door niet aanwezig te zijn.
			// Tegenstander moet punten behouden en niet nadelig geraakt worden door afwezigheid
			// van deze speler.
			// Deze betreffende speler wel als afwezig behandelen
			spelerWedstrijden = new ArrayList<>();
		}
		for (Wedstrijd w : spelerWedstrijden) {
			logger.log(Level.INFO, "    Wedstrijd :" + w.toString());
			int resultaat = 0; // TOTO style -> 0 = onbekend
			Speler tegenstander = w.getWit().gelijkAan(speler) ? w.getZwart() : w.getWit();
			if (updateSpeler.getGroep() < tegenstander.getGroep())
				wedstrijdenHoger++;
			// PUNTEN
			if (w.getUitslag() == Wedstrijd.ONBEKEND) {
				// doe niets
				resultaat = 3;
			} else if (w.getUitslag() == Wedstrijd.GELIJKSPEL) {
				puntenbij += 1;
				aantalremise++;
				resultaat = 3;
				logger.log(Level.INFO, "      Remise         : " + puntenbij);
			} else if ((w.getUitslag() == Wedstrijd.WIT_WINT) && (w.getWit().gelijkAan(speler))) {
				puntenbij += 2;
				aantalgewonnen++;
				resultaat = 1;
				logger.log(Level.INFO, "      Winst met wit  : " + puntenbij);
			} else if ((w.getUitslag() == Wedstrijd.ZWART_WINT) && (w.getZwart().gelijkAan(speler))) {
				puntenbij += 2;
				aantalgewonnen++;
				resultaat = 1;
				logger.log(Level.INFO, "      Winst met zwart :" + puntenbij);
			} else {
				// verlies
				resultaat = 2;
				logger.log(Level.INFO, "      Verlies        :" + puntenbij);
			}
			// WITVOORKEUR
			if (w.getWit().gelijkAan(speler)) {
				updateSpeler.setWitvoorkeur(updateSpeler.getWitvoorkeur() - 1);
				logger.log(Level.INFO, "      Witvoorkeur -1 :" + updateSpeler.getWitvoorkeur());
			} else {
				updateSpeler.setWitvoorkeur(updateSpeler.getWitvoorkeur() + 1);
				logger.log(Level.INFO, "      Witvoorkeur +1 :" + updateSpeler.getWitvoorkeur());
			}
			// TEGENSTANDERS
			String res = resultaat == 1 ? "+" : (resultaat == 2 ? "-" : (resultaat == 3 ? "=" : "?"));
			updateSpeler.addTegenstander(tegenstander.getInitialen() + res);
			logger.log(Level.INFO, "      Tegenstanders  :" + updateSpeler.getTegenstandersString());
		}
		if (spelerWedstrijden.size() == 1) {
			// bij 1 wedstrijd dubbele punten
			logger.log(Level.INFO, "Enkele wedstrijd gepeeld dus verdubbelaar");
			if (puntenbij == 3)
				puntenbij = 5;
			if (puntenbij == 2)
				puntenbij = 3;
		}
		// Spelen in een hogere groep levert punten op
		if (heeftHogerGespeeld(speler, wedstrijden)) {
			logger.log(Level.INFO, "      Hoger gespeeld :" + puntenbij);
			puntenbij++;
		}
		puntenbij = Math.min(puntenbij, 5); // niet meer dan 5 punten er bij
		// Geen wedstrijden dus speler was afwezig
		if ((spelerWedstrijden.size() == 0) && (!extern)) {
			if (!updateSpeler.isAfwezigheidspunt()) {
				puntenbij += 2;
				updateSpeler.setAfwezigheidspunt(true);
				updateSpeler.addTegenstander("## ");
				logger.log(Level.INFO, "      Eerste keer afw : 2 punten");
			} else if (!extern) {
				updateSpeler.addTegenstander("-- ");
			}
		}
		// Externe resultaten verwerken, niet als afwezig laten gelden
		if (extern) {
			if (spelerWedstrijden.size() > 0) {
				logger.log(Level.WARNING,
						"Speler " + updateSpeler.getNaam() + " zowel extern als intern. Extern telt niet mee");
			} else {
				puntenbij = 3;
				updateSpeler.addTegenstander("X3 ");
				logger.log(Level.WARNING, "Speler " + updateSpeler.getNaam() + " extern gespeeld, dus 3 punten");
			}
		}

		// KEI punten bepalen
		if ((wedstrijdenHoger > 0)) {
			// Alle wedstrijden tegen speler hoger dus kant op punten
			String lr = "KEI punten bepalen, aantal gewonnen = " + aantalgewonnen + " aantal remise = " + aantalremise;
			logger.log(Level.INFO, lr);
			int keipunten_bij = 0;
			if (aantalgewonnen == 1 && aantalremise == 1)
				keipunten_bij = 1;
			if (aantalgewonnen == spelerWedstrijden.size())
				keipunten_bij = 2;
			logger.log(Level.INFO, "Speler " + updateSpeler.getNaam() + " verdient aantal keipunten: " + keipunten_bij);
			updateSpeler.setKeikansen(updateSpeler.getKeikansen() + 1);
			updateSpeler.setKeipunten(updateSpeler.getKeipunten() + keipunten_bij);
		}

		logger.log(Level.INFO, "      Punten bij tot :" + puntenbij);
		updateSpeler.setPunten(updateSpeler.getPunten() + puntenbij);
		return updateSpeler;
	}

	/**
	 * Stel vast of de meegegeven speler in een hogere groep heeft gespeeld
	 *
	 * @param speler
	 *            Speler
	 * @param wedstrijden
	 *            Alle weedstrijden
	 * @return true, als hoger gespeeld
	 */
	private boolean heeftHogerGespeeld(Speler speler, Wedstrijden wedstrijden) {
		for (Groepswedstrijden gws : wedstrijden.getGroepswedstrijden()) {
			if (gws.getNiveau() > speler.getGroep()) {
				for (Wedstrijd w : gws.getWedstrijden()) {
					if (w.getWit().gelijkAan(speler) || (w.getZwart().gelijkAan(speler)))
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Geef voor een specifieke speler alle wedstrijden die hij gespeeld heeft
	 *
	 * @param speler
	 *            De speler
	 * @param wedstrijden
	 *            Alle wedstrijden van een speelavond
	 * @return wedstrijden gespeeld door speler
	 */
	private ArrayList<Wedstrijd> getWedstrijdenVoorSpeler(Speler speler, Wedstrijden wedstrijden) {
		logger.log(Level.INFO, "Vind wedstrijden voor speler :" + speler.toString());
		ArrayList<Wedstrijd> result = new ArrayList<>();
		for (Groepswedstrijden gws : wedstrijden.getGroepswedstrijden()) {
			for (Serie serie : gws.getSeries()) {
				for (Wedstrijd w : serie.getWedstrijden()) {
					if (w.getWit().gelijkAan(speler) || w.getZwart().gelijkAan(speler)) {
						result.add(w);
					}
				}
			}
			for (Wedstrijd w : gws.getTriowedstrijden()) {
				if (w.getWit().gelijkAan(speler) || w.getZwart().gelijkAan(speler)) {
					result.add(w);
				}
			}
		}
		logger.log(Level.INFO, "" + result.size() + " wedstrijden gevonden voor " + speler.toString());
		return result;
	}

	/**
	 * UPdate rating van alle spelers. Itereer hiervoor door alle wedstrijden en
	 * pas per wedstrijd de rating van iedere speler aan.
	 *
	 * @param groepen
	 * @param wedstrijden
	 */
	private void updateRating(Groepen groepen, Wedstrijden wedstrijden) {
		for (Groepswedstrijden gws : wedstrijden.getGroepswedstrijden()) {
			for (Wedstrijd wedstrijd : gws.getWedstrijden()) {
				
				logger.log(Level.INFO, "Vind speler (W) bij KNSBnummer :" + wedstrijd.getWit().getKNSBnummer());
				logger.log(Level.INFO, "Vind speler (Z) bij KNSBnummer :" + wedstrijd.getZwart().getKNSBnummer());
				
				Speler wit = groepen.getSpelerByKNSB(wedstrijd.getWit().getKNSBnummer());
				Speler zwart = groepen.getSpelerByKNSB(wedstrijd.getZwart().getKNSBnummer());

				int ratingWit = wit.getRating();
				int ratingZwart = zwart.getRating();

				// w.getUitslag 1=wit wint 2=zwart wint 3=remise
				int uitslagWit = wedstrijd.getUitslag(); // uitslag vanuit
															// perspectief wit
				int uitslagZwart = (wedstrijd.getUitslag() == 1) ? 2 : ((wedstrijd.getUitslag() == 2) ? 1 : 3);

				int nieuwWit = nieuweRatingOSBO(ratingWit, ratingZwart, uitslagWit);
				int nieuwZwart = nieuweRatingOSBO(ratingZwart, ratingWit, uitslagZwart);

				wit.setRating(Math.max(nieuwWit, 100));
				zwart.setRating(Math.max(nieuwZwart, 100));

				logger.log(Level.INFO, wedstrijd.toString());
				logger.log(Level.INFO, "Wit: " + wit.getNaam() + " van " + ratingWit + " naar " + nieuwWit);
				logger.log(Level.INFO, "Zwart: " + zwart.getNaam() + " van " + ratingZwart + " naar " + nieuwZwart);
			}
		}
	}

	/**
	 * Bereken nieuwe rating conform de regels van de OSBO en zoals gebruikt bij
	 * de interne competitie
	 *
	 * @param beginRating
	 * @param tegenstanderRating
	 * @param uitslag
	 *            1 = winst, 2 = verlies, 3 = remise
	 * @return
	 */
	public int nieuweRatingOSBO(int beginRating, int tegenstanderRating, int uitslag) {

		int[] ratingTabel = { 0, 16, 31, 51, 71, 91, 116, 141, 166, 201, 236, 281, 371, 9999 };
		int ratingVerschil = Math.abs(beginRating - tegenstanderRating);
		boolean ratingHogerDanTegenstander = beginRating > tegenstanderRating;
		int index = 0;
		while (ratingVerschil >= ratingTabel[index]) {
			index++;
		}
		index--; // iterator goes one to far.
		if (index == -1)
			index = 0;
		// Update rating wit
		// Dit gebeurd aan de hand van de volgende OSBO tabel
		// Hierin is: W> = winnaar heeft de hoogste rating
		// W< = winnaar heeft de laagste rating
		// V> = verliezer heeft de hoogste rating
		// V< = verliezer heeft de laagste rating
		// R> = remise met de hoogste rating
		// R< = remise met de laagste rating
		//
		// In de volgende tabel wordt de aanpassing van de rating weergegeven
		// rating
		// verschil W> V< W< V> R> R<
		// 0- 15 +12 -12 +12 -12 0 0
		// 16- 30 +11 -11 +13 -13 - 1 + 1
		// 31- 50 +10 -10 +14 -14 - 2 + 2
		// 51- 70 + 9 - 9 +15 -15 - 3 + 3
		// 71- 90 + 8 - 8 +16 -16 - 4 + 4
		// 91-115 + 7 - 7 +17 -17 - 5 + 5
		// 116-140 + 6 - 6 +18 -18 - 6 + 6
		// 141-165 + 5 - 5 +19 -19 - 7 + 7
		// 166-200 + 4 - 4 +20 -20 - 8 + 8
		// 201-235 + 3 - 3 +21 -21 - 9 + 9
		// 236-280 + 2 - 2 +22 -22 -10 +10
		// 281-370 + 1 - 1 +23 -23 -11 +11
		// >371 + 0 - 0 +24 -24 -12 +12
		int deltaRating;
		switch (uitslag) {
		case 1: // Winst
			deltaRating = 12 + (ratingHogerDanTegenstander ? (-1 * index) : (+1 * index));
			return beginRating + deltaRating;
		case 2: // Verlies
			deltaRating = 12 + (ratingHogerDanTegenstander ? (+1 * index) : (-1 * index));
			return beginRating - deltaRating;
		case 3: // Remise
			deltaRating = (ratingHogerDanTegenstander ? (-1 * index) : (+1 * index));
			return beginRating + deltaRating;
		default: // Geen uitstal
			logger.log(Level.SEVERE , "Rating update: Uitslag is geen winst, geen verlies en geen remise.");
			return beginRating;
		}
	}
}
