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

/**
 *
 * @author Leo van der Meulen
 */
public class Groepswedstrijden {

    private int niveau;
    private ArrayList<Serie> series;
    private ArrayList<Wedstrijd> triowedstrijden;

    public Groepswedstrijden() {
        series = new ArrayList<>();
        triowedstrijden = new ArrayList<>();
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }

    public ArrayList<Serie> getSeries() {
        return series;
    }

    public void setSeries(ArrayList<Serie> series) {
        this.series = series;
    }

    public ArrayList<Wedstrijd> getTriowedstrijden() {
        return triowedstrijden;
    }

    public void setTriowedstrijden(ArrayList<Wedstrijd> triowedstrijden) {
        this.triowedstrijden = triowedstrijden;
    }

    public void addTrioWedstrijd(Wedstrijd wedstrijden) {
        triowedstrijden.add(wedstrijden);
    }

    public void addSerie(Serie serie) {
        series.add(serie);
    }
    
    public void removeSerie(Serie serie) {
    	series.remove(serie);
    }

    public Serie getSerie(int index) {
        if (index < series.size()) {
            return series.get(index);
        }
        return null;
    }

    /**
     * Retourneer lijst met alle wedstrijden, waarin zowel de serie
     * wedstrijden als de trio wedstrijden zijn opgenomen.
     * @return
     */
    public ArrayList<Wedstrijd> getWedstrijden() {
        ArrayList<Wedstrijd> lst = new ArrayList<>();
        for (Serie s : series)
            lst.addAll(s.getWedstrijden());
        lst.addAll(triowedstrijden);
        return lst;
    }

	public boolean isUitslagBekend() {
		if (getWedstrijden().isEmpty() && getTriowedstrijden().isEmpty()) {
			return true;
		}
		for (Serie s : series) {
			if (!s.isUitslagBekend())
				return false;
		}
		for (Wedstrijd w : triowedstrijden) {
			if (!w.isUitslagBekend())
				return false;
		}
		return true;
	}
}
