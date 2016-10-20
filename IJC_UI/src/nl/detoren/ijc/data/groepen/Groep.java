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

    private int niveau;
    private ArrayList<Speler> spelers;

    public Groep() {
    	spelers = new ArrayList<>();
    }

    public Groep(int niveau) {
        this.niveau = niveau;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }

    public ArrayList<Speler> getSpelers() {
        return spelers;
    }
    
    public int getAantalSpelers() {
        return spelers.size();
    }

    public void setSpelers(ArrayList<Speler> spelers) {
        this.spelers = spelers;
    }

    public void addSpeler(Speler speler) {
        if (spelers == null) {
            spelers = new ArrayList<>();
        }
        spelers.add(speler);
    }

    public void addSpeler(Speler speler, int loc) {
        spelers.add(loc, speler);
        renumber();
    }
    
    public void removeSpeler(Speler speler, int loc) {
        spelers.remove(loc);
        renumber();
    }

    /** 
     * Geen naam van deze groep
     * @return GRoepsnaam
     */
    public String getNaam() {
    	return IJCController.c().groepsnamen[niveau];
    }

    /**
     * Retourneer een lijst van spelers. 
     * @return String met alle spelers
     */
    public String toPrintableString() {
    	return toPrintableString(false);
    }
    
    /**
     * Retourneer een lijst van spelers. 
     * @param lang Als waar, lange notatoe
     * @return
     */
    public String toPrintableString(boolean lang) {
        String result = "";
        for (Speler s : getSpelers()) {
            result += s.toPrintableString(lang);
            result += "\n";
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
     * Hernummer alle spelers, met uitzondering van de
     * doorgeschoven spelers
     */
    public void renumber2() {
        for (int i = 0; i < spelers.size(); ++i) {
            Speler s = spelers.get(i);
            if (s.getGroep() != this.niveau) {
                s.setId(spelers.get(i-1).getId()+1);
            }
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
    	Collections.sort(spelers, new Comparator<Speler>() {
    	    @Override
    	    public int compare(Speler o1, Speler o2) {
    	    	int result = o2.getPunten() - o1.getPunten();
    	    	if (result == 0) {
    	    		result = o2.getRating() - o1.getRating(); 
    	    	}
    	    	return result;
    	    }
    	});
    }

    /**
     * Sorteer de spelers in deze groep op rating
     */
    public void sorteerRating() {
    	Collections.sort(spelers, new Comparator<Speler>() {
    	    @Override
    	    public int compare(Speler o1, Speler o2) {
    	        return o2.getRating() - o1.getRating();
    	    }
    	});
    }

    /**
     * Reset de punten van alle spelers in deze groep
     */
	public void resetPunten() {
		int punten = IJCController.c().startPunten[niveau];
		for (Speler s : spelers) {
			s.setPunten(punten);
		}
	}
}
