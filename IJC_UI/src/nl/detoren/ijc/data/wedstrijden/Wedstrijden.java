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
 * - ... 
 * - ...
 */

package nl.detoren.ijc.data.wedstrijden;

import java.util.ArrayList;

/**
 *
 * @author Leo van der Meulen
 */
public class Wedstrijden {
    
    private ArrayList<Groepswedstrijden> groepswedstrijden;
    private int ronde;
    private int periode;

    public int getRonde() {
		return ronde;
	}
	public void setRonde(int ronde) {
		this.ronde = ronde;
	}
	public int getPeriode() {
		return periode;
	}
	public void setPeriode(int periode) {
		this.periode = periode;
	}
	public Wedstrijden() {
        groepswedstrijden = new ArrayList<>();
    }
    public ArrayList<Groepswedstrijden> getGroepswedstrijden() {
        return groepswedstrijden;
    }

    public void setGroepswedstrijden(ArrayList<Groepswedstrijden> groepswedstrijden) {
        this.groepswedstrijden = groepswedstrijden;
    }
    
    public void addGroepswedstrijden(Groepswedstrijden gw) {
        groepswedstrijden.add(gw);
    }
    
    public Groepswedstrijden getGroepswedstrijdenNiveau(int niveau) {
        for (Groepswedstrijden w : groepswedstrijden) {
            if (w.getNiveau() == niveau) {
                return w;
            }
        }
        return null;
    }
}
