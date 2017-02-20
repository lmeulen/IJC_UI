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
 */
package nl.detoren.ijc.data.wedstrijden;

import nl.detoren.ijc.data.groepen.Speler;

/**
 *
 * @author Leo van der Meulen
 */
public class Wedstrijd {

	int id;
	Speler wit;
	Speler zwart;
	/**
	 * Toto style 1 = wit wint, 2 = zwart wint, 3 = remise
	 */
	int uitslag;

	public static int WIT_WINT = 1;
	public static int ZWART_WINT = 2;
	public static int GELIJKSPEL = 3;
	public static int ONBEKEND = 0;

	public Wedstrijd() {
		this(0, null, null, 0);
	}

	public Wedstrijd(int id, Speler s1, Speler s2, int uitslag) {
		this.id = id;
		if ((s1 != null) && (s2 != null)) {
			setSpelers(s1, s2);
		} else {
			this.wit = s1;
			this.zwart = s2;
		}
		this.uitslag = uitslag;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Speler getWit() {
		return wit;
	}

	public void setWit(Speler wit) {
		this.wit = wit;
	}

	public Speler getZwart() {
		return zwart;
	}

	public void setZwart(Speler zwart) {
		this.zwart = zwart;
	}

	public final void setSpelers(Speler s1, Speler s2) {
		if (s1.getWitvoorkeur() >= s2.getWitvoorkeur()) {
			wit = s1;
			zwart = s2;
		} else {
			wit = s2;
			zwart = s1;
		}
		if (id == 0)
			id = wit.getId() * 100 + zwart.getId();
	}

	public final void wisselSpelers() {
		Speler tmp = wit;
		wit = zwart;
		zwart = tmp;
	}

	public int getUitslag() {
		return uitslag;
	}

	/**
	 * Geef uitslag in Toto stijl, waarbij 1=winst wit, 2=winst zwart, 3=remise
	 * @param Uitslag
	 */
	public void setUitslag(int Uitslag) {
		this.uitslag = Uitslag;
	}

	/**
	 * Geef uitslag niet in Toto stijl maar voor snelle invoer in 0/1/2
	 * 0 = 0-1 => 2 (Toto)
	 * 1 = 1-0 => 1 (Toto)
	 * 2 = remise => 3 (Toto)
	 * Deze variant is handig bij invoeren van veel resultaten doordat
	 * het eerste getal van de uitslag ingevuld kan worden (met 2 voor half).
	 *
	 * @param uitslag
	 */
	public void setUitslag012(int uitslag) {
		this.uitslag = (uitslag == 0 ? 2 : (uitslag == 1 ? 1 : (uitslag == 2 ? 3 : 0)));
	}

	@Override
	public String toString() {
		String result = wit.toString() + "- " + zwart.toString() + "- ";
		switch (uitslag) {
		case 1:
			result += " 1  -  0";
			break;
		case 2:
			result += " 0  -  1";
			break;
		case 3:
			result += "1/2 - 1/2";
			break;
		default:
			result += "";
		}
		return result;
	}

	/**
	 * Geef afstand in de rankinglijst
	 *
	 * @return
	 */
	public int getDistance() {
		return Math.abs(wit.getId() - zwart.getId());
	}

	public boolean isUitslagBekend() {
		return (uitslag != ONBEKEND);
	}
	
	public int getWedstrijdnummer() {
		int result = 0;
		result = Math.min(wit.getGroep(), zwart.getGroep()) * 10000;
		result += Math.min(wit.getId(), zwart.getId()) * 100;
		result += Math.max(wit.getId(), zwart.getId()) * 1;
		return result;
	}
}
