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
 * Configuratie van het indelingsprogramma.
 * Doel is dat hier geen code in staat maar alleen data.
 * Hiermee kan deze geexporteerd worden naar een json bestand waarna code editing overbodig
 * is geworden om het programma te configureren. 
 * 
 * @author Leo.vanderMeulen
 *
 */
public class Configuratie {

	/**
	 * Aantal periodes in een seizoen
	 */
	public static final int perioden = 4;
	/**
	 * Aantal rondes per periode. Alle perioden in een seizoen hebben hetzelfde aantal rondes 
	 */
	public static final int rondes = 8;

	/**
	 * Aantal groepen waarin wordt gespeeld
	 */
	public static final int aantalGroepen = 7;
	
	/**
	 * Identiefier van de hoogste groep.
	 * Is gelijk aan aantal groepen minus 1, maar is voor 
	 * leesbaarheid code apart opgenomen
	 */
	public static final int hoogsteGroep = 7;

	/**
	 * Geef hier voor iedere groep de naam op, lopende van de laagst groep tot de hoogste groep
	 */
	public static final String[] groepsnamen = { "Pionnengroep", "Paardengroep", "Lopergroep", "Torengroep",
			"Damegroep", "Koningsgroep", "Keizergroep" };

	/**
	 * Voor iedere groep en hiervoor voor iedere ronde is het aantal series vastgelegd.
	 * Bv aantal series voor pionnengroep in de zevende ronde = aantalseries[0][6]
	 * NB aangezien arrays lopen van 0..max is index voor ronde gelijk aan rondenummer -1
	 * 
	 * TODO Hier moet ook nog het periodenummer in mee
	 */
	public static int[][] aantalSeries = { 
			{ 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2 },
			{ 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2 }, 
			{ 2, 2, 2, 2, 2, 2, 2, 2 }, { 2, 2, 2, 2, 2, 2, 2, 2 }, 
			{ 2, 1, 1, 1, 1, 1, 1, 1 } };
	
	/**
	 * Geef per periode aan of het aantal series verhoogd moet worden in 
	 * de eerste ronde
	 */
	public static int[] serieverhoger = { 1, 0, 0, 0, 0, 0, 0, 0 }; 

	/** 
	 * Geef per groep voor iedere ronde op hoeveel spelers er maximal
	 * doorschuiven. Dit is voor iedere periode gelijk
	 */
	public static int[][] aantaldoorschuivers = {
			{ 0, 0, 0, 4, 4, 4, 4, 1 }, { 0, 0, 0, 4, 4, 4, 4, 1 },
			{ 0, 0, 0, 4, 4, 4, 4, 1 }, { 0, 0, 0, 4, 4, 4, 4, 1 },
			{ 0, 0, 0, 4, 4, 4, 4, 1 }, { 0, 0, 0, 4, 4, 4, 4, 1 },
			{ 0, 0, 0, 4, 4, 4, 4, 1 } };

	/**
	 * Leg vast of voor het indelen er wordt gesorteerd op rating
	 */
	public static boolean[][] sorteerOpRating = {
			{ false, false, false, false, false, false, false, false }, 
			{ false, false, false, false, false, false, false, false }, 
			{ false, false, false, false, false, false, false, false }, 
			{ false, false, false, false, false, false, false, false }, 
			{ false, false, false, false, false, false, false, false }, 
			{ false, false, false, false, false, false, false, false }, 
			{ false, true , true , true , true , true , false, false }};
	/**
	 * Standaard wordt er gecontroleerd of de doorschuiver in de laatste
	 * ronde gegarandeerd kampioen is en niet te achterhalen door nummer 2.
	 * Door deze op false te true te zetten, wordt deze controle niet uitgevoerd
	 */
	public static boolean laasteRondeDoorschuivenAltijd = false;
	
	/**
	 * Zet op true als de eerste ronde van iedere periode anders ingedeeld moet 
	 * worden waarbij de bovenste helft tegen de onderste helft speelt. Door dit
	 * te doen, wordt er snel een scheiding gemaakt tussen de goede en minder
	 * goede spelers in een groep. Hierna wordt dus sneller tegen spelers van het 
	 * eigen niveau gespeeld. 
	 */
	public static boolean specialeIndelingEersteRonde = true;

	/**
	 * Bepaald hoe groot het verschil standaard mag zijn tussen twee tegenstanders 
	 * in het klassement. Hiermee krijgt spelen tegen eigen niveau een hogere prioriteit
	 * dan spelen tegen een nieuwe tegenstander
	 */
	public static int indelingMaximumVerschil = 3;
	
	/**
	 * Standaard rating voor nieuwe speler. Bij het toevoegen van een nieuwe speler 
	 * is dit de standaard rating, afhankelijk van de groep waarin hij begint.
	 */
	public static int[] startRating = {100, 200, 300, 400, 500, 600, 800};
	
	/**
	 * Geef aan of er een bestand gegenereerd moet worden dat door de KNSB
	 * gebruikt kan worden voor verwerking resultaten in de KNSB rating
	 */
	public static boolean exportKNSBRating = true;
	
	/**
	 * Geef aan of het simpele tekst bestand geexporteerd moet worden, kan
	 * o.a. worden gebruikt voor publicatie op websites. 
	 */
	public static boolean exportTextShort = true;
	
	/**
	 * Geef aan of het complexe tekstbestand geexporteerd moet worden. DIt
	 * formaat is compatible met oudere indelingsoftware.
	 */
	public static boolean exportTextLong = true;
	
	/**
	 * Geef aan of de KEI stand geexporteerd moet worden.
	 */
	public static boolean exportKEIlijst = true;
	
	/**
	 * Geef aan of er speciale status bestanden opgeslagen moeten worden, bijvoorbeeld
	 * bij exporteren wedstrijden en een expliciete save in het menu.
	 */
	public static boolean saveAdditionalStates = true;
 }
