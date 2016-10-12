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

/**
 * Deze klasse bevat alle gegevens van een groep met een specifiek niveau
 * Vastgelegd is het niveau van de groep en de spelers waaruit de groep bestaat
 *
 * @author Leo van der Meulen
 */
public class Groep {

    public static int PIONNENGROEP = 0;
    public static int PAARDENGROEP = 1;
    public static int LOPERGROEP = 2;
    public static int TORENGROEP = 3;
    public static int DAMEGROEP = 4;
    public static int KONINGSGROEP = 5;
    public static int KEIZERGROEP = 6;

    private static final String[] namen = {
        "Pionnengroep", "Paardengroep", "Lopergroep","Torengroep", "Damegroep", "Koningsgroep", "Keizergroep"
    };

    private int niveau;
    private ArrayList<Speler> spelers;

    public Groep() {
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
        return namen[niveau];
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
        return id < namen.length ? namen[id] : "";
    } 
    
    /**
     * Geef het totaal aantal groepen
     * @return
     */
    public static int getAantalGroepen() {
       return namen.length;
    }
    
    public Speler getSpelerByID(int id) {
        for (Speler s : spelers) {
            if (s.getId() == id) return s;
        }
        return null;
    }
    
    public ArrayList<Speler> getSpelersMetAnderNiveau() {
    	ArrayList<Speler> result = new ArrayList<>();
    	for (Speler s : spelers) {
    		if (s.getGroep() != this.niveau) result.add(s);
    	}
    	return result;
    }
    
    public void sorteerPunten() {
    	Collections.sort(spelers, new Comparator<Speler>() {
    	    @Override
    	    public int compare(Speler o1, Speler o2) {
    	        return o2.getPunten() - (o1.getPunten());
    	    }
    	});
    }

    public void sorteerRating() {
    	Collections.sort(spelers, new Comparator<Speler>() {
    	    @Override
    	    public int compare(Speler o1, Speler o2) {
    	        return o2.getRating() - (o1.getRating());
    	    }
    	});
    }
}
