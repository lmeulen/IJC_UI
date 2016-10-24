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
package nl.detoren.ijc.data.wedstrijden;

import java.util.ArrayList;
import nl.detoren.ijc.data.groepen.Speler;

/**
 *
 * @author Leo van der Meulen
 */
public class Serie {

    private int nummer;
    private ArrayList<Wedstrijd> wedstrijden;

    public Serie() {
        nummer = 0;
        wedstrijden = new ArrayList<>();
    }

    public int getNummer() {
        return nummer;
    }

    public void setNummer(int nummer) {
        this.nummer = nummer;
    }

    public ArrayList<Wedstrijd> getWedstrijden() {
        return wedstrijden;
    }

    public void setWedstrijden(ArrayList<Wedstrijd> wedstrijden) {
        this.wedstrijden = wedstrijden;
    }

    public void addWestrijd(Wedstrijd w) {
        wedstrijden.add(w);
    }

    public void addWestrijd(Wedstrijd w, boolean vooraan) {
        if (vooraan) {
            wedstrijden.add(0, w);
        } else {
            wedstrijden.add(w);
        }
    }

    public void addWestrijd(Wedstrijd w, int locatie) {
        wedstrijden.add(locatie, w);
    }

    public Wedstrijd getWedstrijd(int index) {
        if (index < wedstrijden.size()) {
            return wedstrijden.get(index);
        }
        return null;
    }

    public Wedstrijd getWedstrijdVoorSpeler(Speler speler) {
        for (Wedstrijd w : wedstrijden) {
            if ((w.getWit().gelijkAan(speler)) || (w.getZwart().gelijkAan(speler))) {
                return w;
            }
        }
        return null;
    }

    public Speler getTegenstanderVoorSpeler(Speler speler) {
        for (Wedstrijd w : wedstrijden) {
            if ((w.getWit() == speler)) {
                return w.getZwart();
            } else if (w.getZwart() == speler) {
                return w.getWit();
            }
        }
        return null;
    }
    
    /**
     * Hernummer alle wedstrijden in de serie als 1,2,3...
     */
	public void renumber(int serie) {
		int nummer = serie * wedstrijden.size() + 1;
		for (Wedstrijd w : wedstrijden) {
			w.setId(nummer++);
		}
	}
	
	public boolean isUitslagBekend() {
		for (Wedstrijd w : wedstrijden) {
			if (!w.isUitslagBekend())
				return false;
		}
		return true;
	}
	
}
