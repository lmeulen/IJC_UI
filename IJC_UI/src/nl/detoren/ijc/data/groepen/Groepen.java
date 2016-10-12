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

/**
 * Bevat een verzameling groepen, een per niveau.
 * Naast de verschillende groepen ligt hier ook de periode en de
 * ronde binnen deze periode vast.
 * 
 * @author Leo van der Meulen
 */
public class Groepen {
    
    private ArrayList<Groep> groepen;
    private int periode;
    private int ronde;
    
    public Groepen() {
        groepen = new ArrayList<>();
    }
    
    public void addGroep(Groep groep) {
        groepen.add(groep);
    }
    
    public void updateGroep(Groep groep, int id) {
    	groepen.set(id, groep);
    }
    
    public Groep getGroepById(int id) {
        for (Groep g : groepen) {
            if (g.getNiveau() == id) {
                return g;
            }
        }
        return null;
    }
    
    public ArrayList<Groep>getGroepen() {
        return groepen;
    }
    
    public int getAantalGroepen() {
        return groepen.size();
    }

    public int getPeriode() {
        return periode;
    }

    public void setPeriode(int periode) {
        this.periode = periode;
    }

    public int getRonde() {
        return ronde;
    }

    public void setRonde(int ronde) {
        this.ronde = ronde;
    }

    
	/**
	 * Return printable string met alle groepen
	 * @return
	 */
    public String toPrintableString() {
    	return toPrintableString(false);
    }
    public String toPrintableString(boolean lang) {
        String result = "";
        for (Groep groep : groepen) {
            //Stand na 3e ronde , 1e periode               Keizergroep (16)	
            //pos naam                           ini   zw rating  gespeeld tegen  punt
            //------------------------------------------------------------------------
            result += "Stand na " + ronde + "e ronde, " + periode;
            result += "e periode                " + groep.getNaam() + " (" + groep.getSpelers().size() + ")\n";
            result += "    Naam                           ini   zw rating  gespeeld tegen  pnt\n";
            result += "-----------------------------------------------------------------------\n";

            result += groep.toPrintableString(lang) + "\n";
        }
        return result;
    }
    
    public void hernummerGroepen() {
        for (Groep g : groepen) {
            g.renumber();
        }
    }
    
    public void sorteerGroepen() {
        for (Groep g : groepen) {
        	g.sorteerPunten();
            g.renumber();
        }
        
    }

}
