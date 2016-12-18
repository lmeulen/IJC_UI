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
package nl.detoren.ijc.data.groepen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nl.detoren.ijc.ui.control.IJCController;

/**
 * Deze klasse bevat alle gegevens van een groep met een specifiek niveau
 * Vastgelegd is het niveau van de groep en de spelers waaruit de groep bestaat
 *
 * @author Leo van der Meulen
 */
public class Groep {

	enum Sortering {RATING_ASC, RATING_DESC, PUNTEN_ASC, PUNTEN_DESC};

    private int niveau;
    private double ZWbalansvoor;
    private double ZWbalansna;
    private ArrayList<Speler> spelers;
    private Sortering sortering;

    /**
     * Default constructor.
     * Creer lege spelerslijst
     */
    public Groep() {
    	spelers = new ArrayList<>();
    }

    /**
     * Creeer nieuwe groep van gespecificeerd niveau
     * Spelerslijst is leeg
     * @param niveau
     */
    public Groep(int niveau) {
    	this();
        this.niveau = niveau;
    }

    /**
     * Retourneert niveau van deze groep
     * @return
     */
    public int getNiveau() {
        return niveau;
    }

    /**
     * Geen deze groep gespecificeerd niveau
     * @param niveau
     */
    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }

    /**
     * Retourneert ZWbalans van deze groep
     * @return
     */
    public double getZWbalansvoor() {
        return this.ZWbalansvoor;
    }

    /**
     * Bepaal ZWbalans van deze groep
     */
    public void setZWbalansvoor() {
    	double ZW = 0;
		for (Speler s : getSpelers()) {
			ZW += Math.abs(s.getWitvoorkeur());
		}
		ZWbalansvoor = ZW;

    }

    /**
     * Retourneert ZWbalans van deze groep op basis van ingeplande wedstrijden
     * @return
     */
    public double getZWbalansna() {
        return this.ZWbalansna;
    }

    /**
     * Bepaal ZWbalans van deze groep op basis van ingeplande wedstrijden
     */
    public void setZWbalansna() {
    	double ZW = 0;
		for (Speler s : getSpelers()) {
			ZW += Math.abs(s.getWitvoorkeur());
		}
		ZWbalansna = ZW;

    }

    /**
     * Bepaal ZWbalans van deze groep op basis van waarde
     */
    public void setZWbalansna(double ZW) {
		this.ZWbalansna = ZW;
    }

    /**
     * Retourneert lijst van spelers in deze groeo
     * @return
     */
    public ArrayList<Speler> getSpelers() {
        return spelers;
    }

    /**
     * Retourneert hoeveel spelers er in deze groep zitten
     * @return
     */
    public int getAantalSpelers() {
        return spelers.size();
    }

    /**
     * Vervang de huidige spelerslijst door de meegegeven
     * spelerslijst
     * @param spelers
     */
    public void setSpelers(ArrayList<Speler> spelers) {
        this.spelers = spelers;
    }

    /**
     * Voeg een speler toe aan de spelerslijst
     * @param speler Toe te voegen speler
     */
    public void addSpeler(Speler speler) {
        if (spelers == null) {
            spelers = new ArrayList<>();
        }
        speler.setGroep(niveau);
        spelers.add(speler);
    }

    /**
     * Voeg een speler toe aan de spelerslijst
     * @param speler Toe te voegen speler
     */
    public void addSpelerHoudNiveau(Speler speler) {
        if (spelers == null) {
            spelers = new ArrayList<>();
        }
        spelers.add(speler);
    }
	/**
	 * Voeg een speler toe aan de lijst op de aangegeven locatie
	 * @param speler Toe te voegen speler
	 * @param loc Locatie in de lijst waar speler te plaatsen
	 */
    public void addSpeler(Speler speler, int loc) {
        spelers.add(loc, speler);
        renumber();
    }

    /**
     * Verwijder speler op de opgegeven locatie
     * @param loc Locatie van de te verwijderen speler
     */
    public void removeSpeler(Speler speler, int loc) {
        spelers.remove(loc);
        renumber();
    }

    /**
     * Geef naam van deze groep
     * @return GRoepsnaam
     */
    public String getNaam() {
    	return IJCController.c().groepsnamen[niveau];
    }

    /**
     * Retourneer een lijst van spelers als printvare string
     * @return String met alle spelers
     */
    public String toPrintableString() {
    	return toPrintableString(false);
    }

    /**
     * Retourneer een lijst van spelers. Vorm:
     *
     * @param lang Als waar, lange notatoe
     * @return
     */
    public String toPrintableString(boolean lang) {
        String result = "";
        for (Speler s : getSpelers()) {
            result += s.toPrintableString(lang);
            result += System.lineSeparator();
        }
        return result;
    }

    /**
     * Hernummer alle spelers in de groep als 1,2,3...
     */
    public void renumber() {
    	if (spelers == null) return;
        int nummer = 1;
        for (Speler s : spelers) {
            s.setId(nummer++);
        }
    }

    /**
     * Retourneert de naam van een specifieke groep
     * @param id ID van de groep welke naam wordt gezocht
     * @return
     */
    public static String geefNaam(int id) {
    	String[] namen = IJCController.c().groepsnamen;
    	return id < namen.length ? namen[id] : "";
    }

    /**
     * Geef het totaal aantal groepen
     * @return
     */
    public static int getAantalGroepen() {
    	return IJCController.c().aantalGroepen;
    }

    /**
     * Geef speler met specifiek id (positie in de lijst)
     * @param id ID
     * @return betreffende speler
     */
    public Speler getSpelerByID(int id) {
        for (Speler s : spelers) {
            if (s.getId() == id) return s;
        }
        return null;
    }

    /**
     * Geef speler met specifieke initialen
     * @param afk afkorting
     * @return betreffende speler
     */
    public Speler getSpelerByInitialen(String afk) {
        for (Speler s : spelers) {
            if (s.getInitialen().equals(afk)) return s;
        }
        return null;
    }

    /**
     * Retourneer de spelers in deze groep die een andere groep als niveau
     * hebben staan
     * @return
     */
    public ArrayList<Speler> getSpelersMetAnderNiveau() {
    	ArrayList<Speler> result = new ArrayList<>();
    	for (Speler s : spelers) {
    		if (s.getGroep() != this.niveau) result.add(s);
    	}
    	return result;
    }

    /**
     * Sorteer de spelers in deze groep op punten. Bij hetzelfde aantal
     * punten wordt gesorteerd op rating
     */
    public void sorteerPunten() {
    	sortering = sortering != Sortering.PUNTEN_ASC? Sortering.PUNTEN_ASC : Sortering.PUNTEN_DESC;
    	Collections.sort(spelers, new Comparator<Speler>() {
    	    @Override
    	    public int compare(Speler o1, Speler o2) {
    	    	int result = o2.getPunten() - o1.getPunten();
    	    	if (result == 0) {
    	    		int r1 = o1.getRating();
    	    		int r2 = o2.getRating();
    	    		if (niveau == (IJCController.c().aantalGroepen-1)) {
    	    			r1 = o1.isKNSBLid() ? o1.getRating() * 10 : o1.getRating();
    	    			r2 = o2.isKNSBLid() ? o2.getRating() * 10 : o2.getRating();
    	    		}
					result = r2 - r1;
    	    	}
    	    	return (sortering == Sortering.PUNTEN_ASC) ? result : -result;
    	    }
    	});
    }

    /**
     * Sorteer de spelers in deze groep op rating
     */
    public void sorteerRating() {
    	sortering = sortering != Sortering.RATING_ASC ? Sortering.RATING_ASC : Sortering.RATING_DESC;
    	Collections.sort(spelers, new Comparator<Speler>() {
    	    @Override
    	    public int compare(Speler o1, Speler o2) {
    	    	if (sortering == Sortering.RATING_ASC) {
    	    		return o2.getRating() - o1.getRating();
    	    	} else {
    	    		return o1.getRating() - o2.getRating();
    	    	}
    	    }
    	});
    }

    /**
     * Sorteer op rating en specifeer of dit ASC of DESC moet zijn
     * @param s
     */
    public void sorteerRating(boolean asc) {
    	// inverteer rating en roep standaard sorteer routine aan
    	// inverteren is nodig omdat de algemene sorteer routine dit ook doet.
    	sortering = asc ? Sortering.RATING_DESC : Sortering.RATING_ASC;
    }

    /**
     * Reset de punten en afwezigheidspunten van alle spelers in deze groep
     */
	public void resetPunten() {
		int punten = IJCController.c().startPunten[niveau];
		for (Speler s : spelers) {
			s.setPunten(punten);
			s.setAfwezigheidspunt(false);
		}
	}
}
