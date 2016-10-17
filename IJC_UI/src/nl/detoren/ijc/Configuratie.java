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
package nl.detoren.ijc;

/**
 * Configuratie van het indelingsprogramma. Doel is dat hier geen code in staat
 * maar alleen data. Hiermee kan deze geexporteerd worden naar een json bestand
 * waarna code editing overbodig is geworden om het programma te configureren.
 * 
 * @author Leo.vanderMeulen
 *
 */
public class Configuratie {

	/**
	 * Aantal periodes in een seizoen
	 * MINOR Nog niet in gebruik
	 */
	public int perioden = 4;
	/**
	 * Aantal rondes per periode. Alle perioden in een seizoen hebben hetzelfde
	 * aantal rondes
	 * MINOR Nog niet in gebruik
	 */
	public int rondes = 8;

	/**
	 * Aantal groepen waarin wordt gespeeld
	 * MINOR Nog niet in gebruik
	 */
	public int aantalGroepen = 7;

	/**
	 * Identiefier van de hoogste groep. Is gelijk aan aantal groepen minus 1,
	 * maar is voor leesbaarheid code apart opgenomen
	 * MINOR Nog niet in gebruik
	 */
	public int hoogsteGroep = 6;

	/**
	 * Geef hier voor iedere groep de naam op, lopende van de laagst groep tot
	 * de hoogste groep
	 * MINOR Nog niet in gebruik
	 */
	public String[] groepsnamen = { "Pionnengroep", "Paardengroep", "Lopergroep", "Torengroep",
			"Damegroep", "Koningsgroep", "Keizergroep" };

	/**
	 * Groovy functie die bepaalt hoeveel series er gespeeld moeten. 
	 * Input is
	 * X=groepnummer, Y=periodenummer, Z=rondenummer 
	 * Gebruik: 
	 * int groep,periode,ronde; 
	 * int series = Eval.xyz(groep, periode, ronde, Configuratie.grAantalSeries);
	 * MINOR Nog niet in gebruik
	 */
	private String grAantalSeries =
        "if (x == 6) { if ((y == 1) && (z == 1)) { return 2 } else { return 1 } }" +
        " else if ((y == 1) && (z == 1)) { return 3 } else { return 2 } ";
	
	/**
	 * Retour aantal series
	 * @param groep
	 * @param periode
	 * @param ronde
	 * @return aantal series
	 * MINOR Nog niet in gebruik
	 */
	public int bepaalAantalSeries(int groep, int periode, int ronde) {
		return (Integer)groovy.util.Eval.xyz(groep, periode, ronde, grAantalSeries);
	}

	/**
	 * Groovy functie die bepaalt hoeveel doorschuivers er zijn. 
	 * Input is
	 * X=groepnummer, Y=periodenummer, Z=rondenummer 
	 * Gebruik: 
	 * int groep,periode,ronde; 
	 * int doorschuivers = Eval.xyz(groep, periode, ronde, Configuratie.grAantalDoorschuivers);
	 * MINOR Nog niet in gebruik
	 */
	private String grAantalDoorschuivers = "if (z >= 4) { if (z < 8) { return 4 } else {  return 1 } } else { return 0 }";

	/**
	 * Retourneer aantal doorschuivers
	 * @param groep
	 * @param periode
	 * @param ronde
	 * @return aantal doorschuivers
	 * MINOR Nog niet in gebruik
	 */
	public int bepaalAantalDoorschuivers(int groep, int periode, int ronde) {
		return (Integer)groovy.util.Eval.xyz(groep, periode, ronde, grAantalDoorschuivers);
	}
	/**
	 * Groovy functie die bepaalt of op rating wordt gesorteerd. 
	 * Input is
	 * X=groepnummer, Y=periodenummer, Z=rondenummer 
	 * Gebruik: 
	 * int groep; 
	 * int periode; 
	 * int ronde; 
	 * boolean sort = Eval.xyz(groep, periode, ronde, Configuratie.grSorteerOpRating);
	 * MINOR Nog niet in gebruik
	 */
	private String grSorteerOpRating = "if ((x == 6) && (z > 1) && (z < 7)) { true } else { false }";
	
	/**
	 * Bepaal of er gesorteerd moet worden op rating
	 * @param groep
	 * @param periode
	 * @param ronde
	 * @return true, als er voor indelen gesorteerd moet worden op rating
	 * MINOR Nog niet in gebruik
	 */
	public boolean sorteerOpRating (int groep, int periode, int ronde) {
		return (Boolean)groovy.util.Eval.xyz(groep, periode, ronde, grSorteerOpRating);
	}
	/**
	 * Standaard wordt er gecontroleerd of de doorschuiver in de laatste ronde
	 * gegarandeerd kampioen is en niet te achterhalen door nummer 2. Door deze
	 * op false te true te zetten, wordt deze controle niet uitgevoerd
	 * MINOR Nog niet in gebruik
	 */
	public boolean laasteRondeDoorschuivenAltijd = false;

	/**
	 * Zet op true als de eerste ronde van iedere periode anders ingedeeld moet
	 * worden waarbij de bovenste helft tegen de onderste helft speelt. Door dit
	 * te doen, wordt er snel een scheiding gemaakt tussen de goede en minder
	 * goede spelers in een groep. Hierna wordt dus sneller tegen spelers van
	 * het eigen niveau gespeeld.
	 * MINOR Nog niet in gebruik
	 */
	public boolean specialeIndelingEersteRonde = true;

	/**
	 * Bepaald hoe groot het verschil standaard mag zijn tussen twee
	 * tegenstanders in het klassement. Hiermee krijgt spelen tegen eigen niveau
	 * een hogere prioriteit dan spelen tegen een nieuwe tegenstander
	 * MINOR Nog niet in gebruik
	 */
	public int indelingMaximumVerschil = 3;

	/**
	 * Standaard rating voor nieuwe speler. Bij het toevoegen van een nieuwe
	 * speler is dit de standaard rating, afhankelijk van de groep waarin hij
	 * begint.
	 * MINOR Nog niet in gebruik
	 */
	public int[] startRating = { 100, 150, 200, 300, 500, 800, 1400 };

	/**
	 * standaard punten per groep bij aanvang periode
	 */
	public int[] startPunten = {0, 10, 20, 30, 40, 50, 60 };
	
	/**
	 * Geef aan of er een bestand gegenereerd moet worden dat door de KNSB
	 * gebruikt kan worden voor verwerking resultaten in de KNSB rating
	 * MINOR Nog niet in gebruik
	 */
	public boolean exportKNSBRating = true;

	/**
	 * Geef aan of het simpele tekst bestand geexporteerd moet worden, kan o.a.
	 * worden gebruikt voor publicatie op websites.
	 */
	public boolean exportTextShort = true;

	/**
	 * Geef aan of het complexe tekstbestand geexporteerd moet worden. DIt
	 * formaat is compatible met oudere indelingsoftware.
	 */
	public boolean exportTextLong = true;
	
	/**
	 * Geef aan of in het lange bestandsformaat eventuele doorschuivers
	 * MINOR Nog niet in gebruik
	 */
	public boolean exportDoorschuivers = true;
	
	/**
	 * Geef header doorschuivers
	 */
	public String exportDoorschuiversStart = "De volgende spelers spelen deze week mee in deze groep:";

	/**
	 * Geef footer doorschuivers
	 */

	public String exportDoorschuiversStop  = "Spelers no 3 en 4 schuiven alleen door als de groep even wordt";
	

	/**
	 * Geef aan of de KEI stand geexporteerd moet worden.
	 */
	public boolean exportKEIlijst = true;

	/**
	 * Geef aan of er speciale status bestanden opgeslagen moeten worden,
	 * bijvoorbeeld bij exporteren wedstrijden en een expliciete save in het
	 * menu.
	 * MINOR Nog niet in gebruik
	 */
	public boolean saveAdditionalStates = true;
	
	/**
	 * Waar beginnen met zoeken naar een trio?
	 * param x = groepsgrootte
	 * MINOR Nog niet in gebruik
	 */
	private String grBeginRating = "x / 2";
	
	/**
	 * Bepaal beginpunt voor zoeken naar trio
	 * @param groepsgrootte
	 * @return index voor beginpunt trio
	 * MINOR Nog niet in gebruik
	 */
	public int getBeginpuntTrio (int groepsgrootte) {
		return (Integer)groovy.util.Eval.x(groepsgrootte, grBeginRating);
	}
	
	/**
	 * Bestandsnaam voor configuratie bestand
	 * prefix .json wordt automatisch toegevoegd 
	 * MINOR Nog niet in gebruik
	 */
	public String configuratieBestand = "configuratie";

	/**
	 * Bestandsnaam voor status bestand
	 * prefix .json )en evt datum postfix) wordt automatisch toegevoegd 
	 * MINOR Nog niet in gebruik
	 */
	public String statusBestand = "status";
}


