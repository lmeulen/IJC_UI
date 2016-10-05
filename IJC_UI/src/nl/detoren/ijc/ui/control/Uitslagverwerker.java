/**
 * Copyright (C) 2016 Leo van der Meulen
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.0
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * See: http://www.gnu.org/licenses/gpl-2.0.html
 *  
 * Problemen in deze code:
 * - TODO Het verwerken van uitslagen in de nieuwe rating van spelers gaat niet per wedstrijd maar per speler
 * - TODO Generenen KNSB rating verwerkingsbestanden
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
 * @author Leo van der Meulen
 *
 */
public class Uitslagverwerker {

	private final static Logger logger = Logger.getLogger(GroepenReader.class.getName());

	/**
	 * Verwerk de wedstrijduitslagen en werk de standen bij
	 * @param spelersgroepen De spelersgroepen
	 * @param wedstrijden De gespeelde wedstrijden
	 * @return Bijgewerkte standen
	 */
	public Groepen verwerkUitslag(Groepen spelersgroepen, Wedstrijden wedstrijden) {
		Groepen updateGroepen = new Groepen();
		updateGroepen.setPeriode(spelersgroepen.getPeriode());
		updateGroepen.setRonde(spelersgroepen.getRonde());
		for (Groep groep : spelersgroepen.getGroepen()) {
			logger.log(Level.INFO, "Verwerk uitslag voor groep " + groep.getNaam());
			Groep bijgewerkt = new Groep();
			bijgewerkt.setNiveau(groep.getNiveau());
			for (Speler speler : groep.getSpelers()) {
				logger.log(Level.INFO, "Speler " + speler.getNaam());
				Speler update = updateSpeler(speler, wedstrijden);
				bijgewerkt.addSpeler(update);
			}
			updateGroepen.addGroep(bijgewerkt);
		}
		return updateGroepen;
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
	private Speler updateSpeler(Speler speler, Wedstrijden wedstrijden) {
		ArrayList<Wedstrijd> spelerWedstrijden = getWedstrijdenVoorSpeler(speler, wedstrijden);
		Speler nieuw = new Speler(speler);
		// Standaardpunt
		int puntenbij = 1;
		System.out.println("      Aanwezigheidspunt :" + puntenbij);
		for (Wedstrijd w : spelerWedstrijden) {
			System.out.println("    Wedstrijd :" + w.toString());
			int resultaat = 0; // TOTO style -> 0 = onbekend
			Speler tegenstander = w.getWit().gelijkAan(speler) ? w.getZwart() : w.getWit();
			// RATING EN PUNTEN
			if (w.getUitslag() == Wedstrijd.ONBEKEND) {
				// doe niets
				resultaat = 3;
			} else if (w.getUitslag() == Wedstrijd.GELIJKSPEL) {
				puntenbij += 1;
				resultaat = 3;
				System.out.println("      Gelijkspel     : " + puntenbij);
				int ratingoud = nieuw.getRating();
				nieuw.setRating(nieuweRatingOSBO(nieuw.getRating(), tegenstander.getRating(), 3));
				System.out.println("      Rating         : " + (nieuw.getRating() - ratingoud));
			} else if ((w.getUitslag() == Wedstrijd.WIT_WINT) && (w.getWit().gelijkAan(speler))) {
				puntenbij += 2;
				resultaat = 1;
				System.out.println("      Winst met wit  : " + puntenbij);
				int ratingoud = nieuw.getRating();
				nieuw.setRating(nieuweRatingOSBO(nieuw.getRating(), tegenstander.getRating(), 1));
				System.out.println("      Rating         :" + (nieuw.getRating() - ratingoud));
			} else if ((w.getUitslag() == Wedstrijd.ZWART_WINT) && (w.getZwart().gelijkAan(speler))) {
				puntenbij += 2;
				resultaat = 1;
				System.out.println("      Winst met zwart :" + puntenbij);
				int ratingoud = nieuw.getRating();
				nieuw.setRating(nieuweRatingOSBO(nieuw.getRating(), tegenstander.getRating(), 1));
				System.out.println("      Rating :" + (nieuw.getRating() - ratingoud));
			} else {
				// verlies
				resultaat = 2;
				System.out.println("      Verlies        :" + puntenbij);
				int ratingoud = nieuw.getRating();
				nieuw.setRating(nieuweRatingOSBO(nieuw.getRating(), tegenstander.getRating(), 2));
				System.out.println("      Rating         :" + (nieuw.getRating() - ratingoud));
			}
			// WITVOORKEUR
			if (w.getWit().gelijkAan(speler)) {
				nieuw.setWitvoorkeur(nieuw.getWitvoorkeur()-1);
				System.out.println("      Witvoorkeur -1 :" + nieuw.getWitvoorkeur());
			} else {
				nieuw.setWitvoorkeur(nieuw.getWitvoorkeur()+1);
				System.out.println("      Witvoorkeur +1 :" + nieuw.getWitvoorkeur());
			}
			// TEGENSTANDERS
			String res = resultaat == 1 ? "+" : (resultaat == 2 ? "-" : (resultaat == 3 ? "=" : "?"));
			nieuw.addTegenstander(tegenstander.getInitialen()+res);			
			System.out.println("      Tegenstanders  :" + nieuw.getTegenstandersString());
		}
		puntenbij = Math.min(puntenbij, 5); // niet meer dan 5 punten er bij
		if (spelerWedstrijden.size() == 1) {
			// bij 1 wedstrijd dubbele punten
			if (puntenbij == 3)	puntenbij = 5;
			if (puntenbij == 2)	puntenbij = 3;
		}
		// Geen wedstrijden dus speler was afwezig
		if (spelerWedstrijden.size() == 0) {
			nieuw.addTegenstander("-- ");
			if (!nieuw.isAfwezigheidspunt()) {
				puntenbij += 2;
				nieuw.setAfwezigheidspunt(true);
				System.out.println("      Eerste keer afw : 2");
			}
		}
		System.out.println("      Punten bij tot :" + puntenbij);
		nieuw.setPunten(nieuw.getPunten() + puntenbij);
		if (nieuw.getRating() < 100) nieuw.setRating(100);
		// Tegenstanders
		// witvoorkeur
		return nieuw;
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
		return result;
	}

    /**
    Bereken nieuwe rating conform de regels van de OSBO en zoals gebruikt
   bij de interne competitie
    @param beginRating
    @param tegenstanderRating
    @param uitslag 1 = winst, 2 = verlies, 3 = remise
    * @return  
    */
   public int nieuweRatingOSBO(int beginRating, int tegenstanderRating, int uitslag) {
       
       int[] ratingTabel = {0, 16, 31, 51, 71, 91, 116, 141, 166, 201, 236, 281, 371, 9999};
       int ratingVerschil = Math.abs(beginRating - tegenstanderRating);
       boolean witHoger = beginRating > tegenstanderRating;
       int index = 0;
       while (ratingVerschil >= ratingTabel[index]) {
           index++;
       }
       // Update rating wit
       // Dit gebeurd aan de hand van de volgende OSBO tabel
       // Hierin is: W> = winnaar heeft de hoogste rating
       //            W< = winnaar heeft de laagste rating
       //            V> = verliezer heeft de hoogste rating
       //            V< = verliezer heeft de laagste rating
       //            R> = remise met de hoogste rating
       //            R< = remise met de laagste rating
       //
       //In de volgende tabel wordt de aanpassing van de rating weergegeven
       //rating   
       //verschil  W>    V<    W<    V>    R>    R<
       //  0- 15   +12   -12   +12   -12     0     0
       // 16- 30   +11   -11   +13   -13   - 1   + 1
       // 31- 50   +10   -10   +14   -14   - 2   + 2
       // 51- 70   + 9   - 9   +15   -15   - 3   + 3
       // 71- 90   + 8   - 8   +16   -16   - 4   + 4
       // 91-115   + 7   - 7   +17   -17   - 5   + 5
       //116-140   + 6   - 6   +18   -18   - 6   + 6
       //141-165   + 5   - 5   +19   -19   - 7   + 7
       //166-200   + 4   - 4   +20   -20   - 8   + 8
       //201-235   + 3   - 3   +21   -21   - 9   + 9
       //236-280   + 2   - 2   +22   -22   -10   +10
       //281-370   + 1   - 1   +23   -23   -11   +11
       // >371     + 0   - 0   +24   -24   -12   +12        
       int deltaRating;
       switch (uitslag) {
           case 1:
               deltaRating = 12 + (witHoger ? (-1 * index) : (+1 * index));
               return beginRating + deltaRating;
           case 2:
               deltaRating = 12 + (witHoger ? (+1 * index) : (-1 * index));
               return beginRating - deltaRating;
           case 3:
               deltaRating = (witHoger ? (-1 * index) : (+1 * index));
               return beginRating + deltaRating;
           default:
               return beginRating;
       }
   }
}
