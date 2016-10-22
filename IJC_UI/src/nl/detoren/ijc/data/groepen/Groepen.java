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

import nl.detoren.ijc.ui.control.IJCController;

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
    	for (int i =0; i < groepen.size(); i++) {
    		if (groepen.get(i).getNiveau() == id)
    			groepen.set(i, groep);
    		
    	}
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
        for (int i = 0; i < groepen.size(); ++i) {
        	Groep groep = groepen.get(i);
        	//Stand na 3e ronde , 1e periode               Keizergroep (16)	
            //pos naam                           ini   zw rating  gespeeld tegen  punt
            //------------------------------------------------------------------------
            result += "Stand na " + ronde + "e ronde, " + periode;
            result += "e periode                " + groep.getNaam() + " (" + groep.getSpelers().size() + ")\n";
            result += "    Naam                           ini   zw rating  gespeeld tegen  pnt\n";
            result += "-----------------------------------------------------------------------\n";

            result += groep.toPrintableString(lang) + "\n";
            
			if (IJCController.c().exportDoorschuivers) {
				int ndoor = IJCController.c().bepaalAantalDoorschuiversVolgendeRonde(periode, ronde);
				if (i + 1 < groepen.size()) {
					result += IJCController.c().exportDoorschuiversStart + "\n";
					Groep lager = groepen.get(i + 1);
					if (ndoor > 1) {
						for (int j = 0; j < ndoor; j++) {
							Speler s = lager.getSpelerByID(j + 1);
							result += s.toPrintableString(lang) + "\n";
						}
						result += IJCController.c().exportDoorschuiversStop + "\n" + "\n";
					} else {
						// Bij één doorschuiver, alleen doorschuiVen als kampioen
						Speler s1 = lager.getSpelerByID(1);
						Speler s2 = lager.getSpelerByID(2);
						if ((s2 != null) && ((s1.getPunten() - s2.getPunten()) > 4)) {
							result += s1.toPrintableString(lang) + "\n";
						}
					}
				}
            }
            result += "\n";
        }
        return result;
    }
    
    /**
     * Hernummer alle groepen
     */
    public void hernummerGroepen() {
        for (Groep g : groepen) {
            g.renumber();
        }
    }
    /**
     * Sorteer alle groepen op punten
     */
    public void sorteerGroepen() {
        for (Groep g : groepen) {
        	g.sorteerPunten();
            g.renumber();
        }
        
    }
    
    /**
     * Zoek speler in alle groepen
     * 
     */
    public Speler getSpelerByKNSB(int knsb) {
    	for (Groep g: groepen) {
    		for (Speler s: g.getSpelers()) {
    			if (s.getKNSBnummer() == knsb) return s;
    		}
    	}
    	return null;
    }
    
    public void resetPunten() {
    	for (Groep g : groepen) {
    		g.resetPunten();
    	}
    }
    
}
