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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.groepen.Groepen;
import nl.detoren.ijc.data.groepen.Speler;

/**
 * Schrijf het bestand met KEI stand. Alleen spelers die daadwerkelijk punten
 * hebben behaald, worden getoond.
 * @author Leo.vanderMeulen
 *
 */
public class OutputKEI {

	private final static Logger logger = Logger.getLogger(OutputKNSB.class.getName());

	/**
	 * Exporteer de KEI stand naar bestand Rp-rKEIpuntenS.txt
	 * Alleen spelers met behaalde punten worden getoond
	 * 
	 * @param groepen bevat de stand die geexporteerd moet worden
	 */
	public void exportKEIlijst(Groepen groepen) {
		try {
			String bestandsnaam = "R" + groepen.getPeriode() + "-" + groepen.getRonde() + "KEIpuntenS"; 
			logger.log(Level.INFO, "Sla uitslag op in bestand " + bestandsnaam);

			FileWriter writer = new FileWriter(bestandsnaam + ".txt");
			writer.write(getHeader(groepen.getPeriode(), groepen.getRonde()));
			
			// Vind spelers met KEI punten
			ArrayList<Speler> keispelers = new ArrayList<>();
			for (Groep groep : groepen.getGroepen()) {
				for (Speler speler : groep.getSpelers()) {
					if ((speler.getKeikansen() > 0)) {
						keispelers.add(speler);
					}
				}
			}

			// Sorteer deze eerst op punten, dan op kansen
	    	Collections.sort(keispelers, new Comparator<Speler>() {
	    	    @Override
	    	    public int compare(Speler o1, Speler o2) {
	    	        //return o2.getRating() - (o1.getRating());
	    	        return (o2.getKeipunten()*100 + o2.getKeikansen()) - (o1.getKeipunten()*100 + o1.getKeikansen());
	    	    }
	    	});

	    	// Exporteer de gesorteerde lijst
	    	int pos = 1;
	    	for (Speler s : keispelers) {
	    		String res = Integer.toString(pos++);
	    		if (res.length() < 2) res = " " + res;
	    		res += ". ";
	    		res += s.getNaam();
	    		while (res.length() < 34) {
	    			res += " ";
	    		}
	    		String p = Integer.toString(s.getKeipunten());
	    		if (p.length() < 2) p = " " + p;
	    		res += p + "/"; 
	    		p = Integer.toString(s.getKeikansen());
	    		if (p.length() < 2) p = " " + p;
	    		res += p;
				writer.write(res + "\n");
	    		
	    	}
			writer.close();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Export mislukt : " + e.getMessage());
			e.printStackTrace();
		}
	}

	private String getHeader(int periode, int ronde) {
		String res = "";
		res += "Klassement KEI-punten: ronde: " + ronde + "  periode: " + periode + "\n";
		res += "\nPos Naam               KEIpunten/kansen\n";
		res += "---------------------------------------\n";
		return res;
	}
}
