/**
 * Copyright (C) 2016 Leo van der Meulen, Lars Dam
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
package nl.detoren.ijc.ui.control;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.data.wedstrijden.Groepswedstrijden;
import nl.detoren.ijc.data.wedstrijden.Serie;
import nl.detoren.ijc.data.wedstrijden.Wedstrijd;
import nl.detoren.ijc.ui.util.Utils;
import nl.detoren.ijc.ui.util.minimizetriagonal;

/**
 * Fuzzy implementatie voor bepalen wedstrijdschema
 * Groepsindeling fucntionaliteit wordt overgenomen van GroepenIndeler.
 * @author Leo.vanderMeulen
 * @author Lars.Dam
 *
 */
public class GroepenIndelerFuzzy extends GroepenIndeler implements GroepenIndelerInterface {

	private final static Logger logger = Logger.getLogger(GroepenIndelerFuzzy.class.getName());

	private int fuzzymatrix[][];

	int[] vijf1 = {0,0,0,0};
	int[] vijf2 = {0};
	
	ArrayList<Integer> oneven1 = new ArrayList<>();
	ArrayList<Integer> oneven2 = new ArrayList<>();
	
	int wedstrijdnr = 1;

	/**
	 * Bepaal voor een groep de te spelen wedstrijden
	 *
	 * @param periode
	 * @param ronde
	 * @param wedstrijdgroep
	 * @return
	 */
	@Override
	public  Groepswedstrijden maakWedstrijdenVoorGroep(int periode, int ronde, Groep wedstrijdgroep) {
		//
		int doorschuivers = IJCController.c().bepaalAantalDoorschuivers(periode, ronde);		// Aantal doorschuivers
		vijf1=null;
		vijf2=null;
		oneven1.clear();
		oneven2.clear();
		// ZW is absoluut aantal in onbalans zwart/wit
		wedstrijdgroep.setZWbalansvoor();
		// 
		// Maak clone van de Groep om ongewenste updates te voorkomen
		Groep groep = new Groep();
		groep.setNiveau(wedstrijdgroep.getNiveau());
		for (Speler s : wedstrijdgroep.getSpelers()) {
			logger.log(Level.FINE, "Toevoegen van speler " + s.getNaam());
			groep.addSpeler(new Speler(s));
		}
		
		if ((groep.getNiveau() == (IJCController.c().aantalGroepen-1)) && (ronde < 7) && (ronde > 1)) {
			// Sorteer keizergroep op rating voor indeling indien ronde =
			// 2,3,4,5 of 6
			groep.sorteerRating();
		}
		logger.log(Level.INFO, "Bepalen wedstrijden voor groep " + wedstrijdgroep.getNaam() + " periode " + periode
				+ " ronde " + ronde);
		// Maak wedstrijden
		Groepswedstrijden gws = new Groepswedstrijden();
		gws.setNiveau(groep.getNiveau());
		int speelrondes = bepaalAantalSeries(groep.getNiveau(), periode, ronde);
		logger.log(Level.INFO, "Aantal speelrondes " + speelrondes);

		// Trucje voor 5 speler in een wedstrijdgroep:
		// Oud trucje voor 5 spelers in traditionele indeling werkt niet bij Fuzzy.
		// Nieuwe routine moet bestaan uit:
		// Serie 1 is beste twee wedstrijden.
		// Serie 2 is beste wedstrijd voor 5e speler
		// Serie 3 is de resterende 2 wedtrijden.
		// 
		
		if (IJCController.c().fuzzyOneven && (!((groep.getAantalSpelers() & 1) == 0)) && speelrondes == 2) {
			speelrondes = 3;
			logger.log(Level.INFO, "Oneven spelers met 2 rondes dus maak 3 series");
		} 
		if (groep.getAantalSpelers() == 5 && speelrondes == 2) {
			speelrondes = 3;
			logger.log(Level.INFO, "Vijf spelers met 2 rondes dus maak 3 series");
		} 
		// Introductie Fuzzy Logic
		//
		int trioloc = 0;
		int[] trio = {0,1,2};
		int indexrow = 1;
		for (int i = 0; i < speelrondes; i++) {
			System.out.print(
					"Creating serie " + Integer.toString(i + 1) + " voor groep " + groep.getNaam() + "\n");
			if (i > 0) {
				if (!(trioloc == 0)) {
					Groep reducedgroep = new Groep();
					reducedgroep.setNiveau(groep.getNiveau());
					for (Speler s : groep.getSpelers()) {
						if (!Utils.containing(trio, s.getId())) {
							reducedgroep.addSpeler(new Speler(s));	
							logger.log(Level.FINE, "Toevoegen van speler " + s.getNaam() + " aan groep zonder trio");
						} else {
							logger.log(Level.FINE, "Speler " + s.getNaam() + " niet toevoegen aan groep (zit in trio)");
						}
						
					}
					fuzzymatrix = MaakFuzzyMatrix(reducedgroep, i, speelrondes, doorschuivers);
					//
					// System.out.print("Reduced Matrix\n");
					Utils.printMatrix(fuzzymatrix);
				} else {
					fuzzymatrix = MaakFuzzyMatrix(groep, i, speelrondes, doorschuivers);
					Utils.printMatrix(fuzzymatrix);
				}
			} else {
				fuzzymatrix = MaakFuzzyMatrix(groep, i, speelrondes, doorschuivers);
				Utils.printMatrix(fuzzymatrix);
			}
			if (IJCController.c().fuzzyOneven && (!((groep.getAantalSpelers() & 1) == 0))) {
				switch (i) { 
				case 2:
					fuzzymatrix = Utils.removerowandcolumnfrom2D(fuzzymatrix, oneven2, indexrow);
					break;
				}				
			}
			if (!IJCController.c().fuzzyOneven && groep.getAantalSpelers() == 5) {
				// Als gebruik gemaakt wordt van trio bij oneven aantallen, dan niet voor 5 omdat dit niet kan.
				// Je houdt dat 2 spelers over die twee series tegen elkaar spelen.
				switch (i) { 
				case 2:
					fuzzymatrix = Utils.removerowandcolumnfrom2D(fuzzymatrix, vijf2, indexrow);
					break;
				}
			}
			int[][] fmatrix = fuzzymatrix;

			logger.log(Level.INFO, "FuzzyMatrix created.");
			System.out.print("Triagonalization of Matrix\n");
			minimizetriagonal triagonal = new minimizetriagonal();
			triagonal.setA(fuzzymatrix);
			triagonal.setIterations(groep.getAantalSpelers());
			triagonal.Iterminimizetriagonal();
			int[][] tri = triagonal.getA();
			Utils.printMatrix(tri);
			if (groep.getNaam().equals("Pionnengroep")) {
				SpelersNamenopvolgorde(tri,indexrow, groep.getSpelers());
				//groep.SpelersNamenopvolgorde(ordertest);
			}
			System.out.print("Deze groep " + groep.getNaam() + " heeft " + tri.length + " spelers.\n");
//			if (speelrondes >1) {
			if (!IJCController.c().fuzzyOneven && !(groep.getAantalSpelers() == 5)) {
				trioloc = minimizetriagonal.gettrio(tri,1);
			} else {
				trioloc = 0;
			}
			if (!(trioloc == 0)) {
				trio[0]=tri[trioloc-1][0];
				trio[1]=tri[trioloc][0];
				trio[2]=tri[trioloc+1][0];
			}
			// System.out.print("Minimize matrix is\n");
			// Utils.printMatrix(tri);
			// System.out.print("Order vector is\n");
			// Utils.printMatrix(order);
			Serie s = new Serie();
			if (IJCController.c().fuzzyOneven && (!((groep.getAantalSpelers() & 1) == 0))) {
				s = this.IndelingOnevenSpelers(s, groep, tri, i);
			} else 
			if (!IJCController.c().fuzzyOneven && groep.getAantalSpelers() == 5) {
				s = this.Indeling5Spelers(s, groep, tri, i);
			} else 
			if ((trioloc == 0) || (i > 0)) {
				for (int k = 0; k <= fmatrix.length - 1; k += 2) {
					Speler s1 = groep.getSpelerByID(tri[k][0]); // Speler
																			// wit
					Speler s2 = groep.getSpelerByID(tri[k + 1][0]); // Speler
																				// zwart
					Wedstrijd w = new Wedstrijd(wedstrijdnr, s1, s2, 0);
					s.addWedstrijd(w, true);
					wedstrijdnr++;
					System.out.printf("Wedstrijd tussen " + w.getWit().getNaam()
							+ " (wit) en " + w.getZwart().getNaam() + " (zwart)"
									+ "\n");
									}
			} else {
				for (int k = 0; k < trioloc - 2; k += 2) {
					Speler s1 = groep.getSpelerByID(tri[k][0]); // Speler
																			// wit
					Speler s2 = groep.getSpelerByID(tri[k + 1][0]); // Speler
																				// zwart
					Wedstrijd w = new Wedstrijd(wedstrijdnr, s1, s2, 0);
					s.addWedstrijd(w, true);
					wedstrijdnr++;
					System.out.printf("Wedstrijd tussen " + w.getWit().getNaam()
							+ " (wit) en " + w.getZwart().getNaam() + " (zwart)"
									+ "\n");				}
				for (int k = trioloc + 2; k <= fmatrix.length - 1; k += 2) {
					Speler s1 = groep.getSpelerByID(tri[k][0]); // Speler
																			// wit
					Speler s2 = groep.getSpelerByID(tri[k + 1][0]); // Speler
																				// zwart
					Wedstrijd w = new Wedstrijd(wedstrijdnr, s1, s2, 0);
					s.addWedstrijd(w, true);
					System.out.printf("Wedstrijd tussen " + w.getWit().getNaam()
							+ " (wit) en " + w.getZwart().getNaam() + " (zwart)"
									+ "\n");
					wedstrijdnr++;
				}
				// trio
				if (i == 0) {
					Wedstrijd w = new Wedstrijd(2 * (wedstrijdnr - 1) + 1,
							groep.getSpelerByID(trio[0]),
							groep.getSpelerByID(trio[1]), 0);
					gws.addTrioWedstrijd(w);
					System.out.printf("Triowedstrijd tussen " + w.getWit().getNaam()
							+ " (wit) en " + w.getZwart().getNaam() + " (zwart)"
									+ "\n");
					w = new Wedstrijd(2 * (wedstrijdnr - 1) + 2, groep.getSpelerByID(trio[1]),
									groep.getSpelerByID(trio[2]), 0);
					gws.addTrioWedstrijd(w);
					System.out.printf("Triowedstrijd tussen " + w.getWit().getNaam()
							+ " (wit) en " + w.getZwart().getNaam() + " (zwart)"
									+ "\n");
					w = new Wedstrijd(2 * (wedstrijdnr - 1) + 3,
							groep.getSpelerByID(trio[2]),
							groep.getSpelerByID(trio[0]), 0);
					gws.addTrioWedstrijd(w);
					System.out.printf("Triowedstrijd tussen " + w.getWit().getNaam()
							+ " (wit) en " + w.getZwart().getNaam() + " (zwart)"
									+ "\n");
					groep = updateSpelers(groep, gws.getTriowedstrijden());
					logger.log(Level.INFO, "Update Spelers triowedstrijden");
					// update gegevens tegenstanders en witvoorkeur
				}
				// Einde trio
			}
			if (s != null) {
				s.renumber(i); // Hernummer wedstrijden.
				gws.addSerie(s);
				logger.log(Level.INFO, "Voeg Serie toe");
				groep = updateSpelers(groep, s);
				logger.log(Level.INFO, "Update Spelers gewone wedstrijden");
				// update gegevens tegenstanders en witvoorkeur
			}
		}
		// Samenvoegen 3 series naar 1 voor nieuwe oneven indeling
		if (IJCController.c().fuzzyOneven && (!((groep.getAantalSpelers() & 1) == 0))) {
			gws=Samenvoegenseries(gws);
		} else 
		// Samenvoegen 3 series naar 1 voor originele trioindeling en aantalspelers is 5
		if (!IJCController.c().fuzzyOneven && groep.getAantalSpelers() == 5) {
			gws=Samenvoegenseries(gws);
		} else 

		logger.log(Level.INFO, "ZW balans voor groep " + wedstrijdgroep.getNaam() + " voor deze ronde is " +wedstrijdgroep.getZWbalansvoor());
		groep.setZWbalansna();
		// Overdragen tijdelijke data naar reguliere data voor deze waarde.
		wedstrijdgroep.setZWbalansna(groep.getZWbalansna());
		logger.log(Level.INFO, "ZW balans voor groep " + wedstrijdgroep.getNaam() + " na deze ronde is " + wedstrijdgroep.getZWbalansna());
		return gws;
	}

	private Groepswedstrijden Samenvoegenseries(Groepswedstrijden gws) {
		int i=0;
		for (Serie s : gws.getSeries()){
			if (i > 0) {
				for (Wedstrijd w : s.getWedstrijden()) {
					w.setId(gws.getSerie(0).getWedstrijden().size()+1);
					gws.getSerie(0).addWedstrijd(w);	
				}
			}
			i++;
		}
		i=0;
		ArrayList<Serie> ss = new ArrayList<Serie>();
		for (Serie s : gws.getSeries()){
			if (i > 0) {
				ss.add(s);
			}
			i++;
		}
		for (Serie s : ss) {
			gws.removeSerie(s);
		}
		return gws;
	}
	
	private Serie Indeling5Spelers(Serie s, Groep groep, int[][] tri, int i) {
	// Bepaal de twee wedstrijden voor serie 1.
	Speler s1 = new Speler();
	Speler s2 = new Speler();
	Wedstrijd w;
	switch (i) {
	case 0:
		for (int k = 0; k <= 3; k += 2) {
			s1 = groep.getSpelerByID(tri[k][0]); // Speler
																	// wit
			s2 = groep.getSpelerByID(tri[k + 1][0]); // Speler
																		// zwart
			// Onthoud welke spelers in serie 1 hebben gespeeld.
			vijf1[k] = tri[k][0];
			vijf1[k+1] = tri[k+1][0];
			w = new Wedstrijd(wedstrijdnr, s1, s2, 0);
			s.addWedstrijd(w, true);
			wedstrijdnr++;
			System.out.printf("5 Spelers- Serie 1. Wedstrijd tussen " + w.getWit().getNaam()
					+ " (wit) en " + w.getZwart().getNaam() + " (zwart)"
							+ "\n");
							}
		break;
	case 1:
		boolean found = false;
		for (int k = 0; k <= 3; k += 2) {
			// Als één van beide spelers de nog niet gespeelde 
			if (!(Utils.containing(vijf1,(tri[k][0]))) || (!(Utils.containing(vijf1,(tri[k + 1][0]))))) {
				s1 = groep.getSpelerByID(tri[k][0]); // Speler
															// wit
				s2 = groep.getSpelerByID(tri[k + 1][0]); // Speler
															// zwart
				found = true;
			}
		}
		if (!found) {
			s1 = groep.getSpelerByID(tri[3][0]); // Speler
														// wit
			s2 = groep.getSpelerByID(tri[4][0]); // Speler
														// zwart
			
		}
		// Onthoud welke speler nu al 2 wedstrijden heeft gespeeld.
			
			if (Utils.containing(vijf1,s1.getId())) {
				vijf2[0] = s1.getId();	
			} else {
				if (Utils.containing(vijf1,s2.getId())) {
					vijf2[0] = s2.getId();		
				} else {
					logger.log(Level.SEVERE, "Geen speler voor twee westrijden ingedeeld gevonden. Probleem met indeling voor 5 spelers.");
				}
			}
			w = new Wedstrijd(wedstrijdnr, s1, s2, 0);
			s.addWedstrijd(w, true);
			wedstrijdnr++;
			System.out.printf("5 Spelers- Serie 2. Wedstrijd tussen " + w.getWit().getNaam()
					+ " (wit) en " + w.getZwart().getNaam() + " (zwart)"
							+ "\n");
		break;
	case 2:
		for (int k = 0; k <= 3; k += 2) {
			s1 = groep.getSpelerByID(tri[k][0]); // Speler
																	// wit
			s2 = groep.getSpelerByID(tri[k + 1][0]); // Speler
																		// zwart
			
			w = new Wedstrijd(wedstrijdnr, s1, s2, 0);
			s.addWedstrijd(w, true);
			wedstrijdnr++;
			System.out.printf("5 Spelers- Serie 3. Wedstrijd tussen " + w.getWit().getNaam()
					+ " (wit) en " + w.getZwart().getNaam() + " (zwart)"
							+ "\n");
							}
		break;
	}
	return s;
}
	
	private Serie IndelingOnevenSpelers(Serie s, Groep groep, int[][] tri, int i) {
	// Bepaal de twee wedstrijden voor serie 1.
	Speler s1 = new Speler();
	Speler s2 = new Speler();
	Wedstrijd w;
	switch (i) {
	case 0:
		for (int k = 0; k <= groep.getAantalSpelers()-2; k += 2) {
			s1 = groep.getSpelerByID(tri[k][0]); // Speler
																	// wit
			s2 = groep.getSpelerByID(tri[k + 1][0]); // Speler
																		// zwart
			// Onthoud welke spelers in serie 1 hebben gespeeld.
			oneven1.add(k, tri[k][0]);
			oneven1.add(k+1,tri[k+1][0]);
			w = new Wedstrijd(wedstrijdnr, s1, s2, 0);
			s.addWedstrijd(w, true);
			wedstrijdnr++;
			System.out.printf("Oneven Spelers- Serie 1. Wedstrijd tussen " + w.getWit().getNaam()
					+ " (wit) en " + w.getZwart().getNaam() + " (zwart)"
							+ "\n");
							}
		break;
	case 1:
		boolean found = false;
		for (int k = 0; k <= groep.getAantalSpelers()-2; k += 2) {
			// Als één van beide spelers de nog niet gespeelde 
			if (!(Utils.containing(oneven1,(tri[k][0]))) || (!(Utils.containing(oneven1,(tri[k + 1][0]))))) {
				s1 = groep.getSpelerByID(tri[k][0]); // Speler
															// wit
				s2 = groep.getSpelerByID(tri[k + 1][0]); // Speler
															// zwart
				found = true;
			}
		}
		if (!found) {
			// Als niet gevonden, dan de laatste twee spelers uit de groep.
			s1 = groep.getSpelerByID(tri[groep.getAantalSpelers()-2][0]); // Speler
														// wit
			s2 = groep.getSpelerByID(tri[groep.getAantalSpelers()-1][0]); // Speler
														// zwart
			
		}
		// Onthoud welke speler nu al 2 wedstrijden heeft gespeeld.
			
			if (Utils.containing(oneven1,s1.getId())) {
				oneven2.add(0,s1.getId());	
			} else {
				if (Utils.containing(oneven1,s2.getId())) {
					oneven2.add(0,s2.getId());		
				} else {
					logger.log(Level.SEVERE, "Geen speler voor twee westrijden ingedeeld gevonden. Probleem met indeling voor oneven spelers.");
				}
			}
			w = new Wedstrijd(wedstrijdnr, s1, s2, 0);
			s.addWedstrijd(w, true);
			wedstrijdnr++;
			System.out.printf("Oneven Spelers- Serie 2. Wedstrijd tussen " + w.getWit().getNaam()
					+ " (wit) en " + w.getZwart().getNaam() + " (zwart)"
							+ "\n");
		break;
	case 2:
 		for (int k = 0; k <= groep.getAantalSpelers()-2; k += 2) {
			s1 = groep.getSpelerByID(tri[k][0]); // Speler
																	// wit
			s2 = groep.getSpelerByID(tri[k + 1][0]); // Speler
																		// zwart
			
			w = new Wedstrijd(wedstrijdnr, s1, s2, 0);
			s.addWedstrijd(w, true);
			wedstrijdnr++;
			System.out.printf("Oneven Spelers- Serie 3. Wedstrijd tussen " + w.getWit().getNaam()
					+ " (wit) en " + w.getZwart().getNaam() + " (zwart)"
							+ "\n");
							}
		break;
	}
	return s;
}
	
	private int[][] MaakFuzzyMatrix(Groep wedstrijdgroep, int serie, int speelronde, int doorschuivers) {
		/**
		 * FuzzyMatrix wordt gebruik voor het snel vaststellen van beste match
		 * als tegenstander door middel van Fuzzy Logic. Hiertoe worden per
		 * voorwaarde waaraan voldaan moet worden een matrix opgesteld. Per
		 * voorwaarde wordt bepaald hoe zwaar het weegt als niet aan de
		 * voorwaarde wordt voldaan.
		 *
		 * Voorwaarde 1: Niet tegen dezelfde tegenstander speler als in de
		 * laatste 4 partijen. Hiertoe wordt vastgesteld dat een tegenstander in
		 * laatste ronde 140 weegt. Een tegenstander waar al tegen gestreden is
		 * in de een-na-laatste partij weegt 90. Een tegenstander waar al tegen
		 * gestreden is in de twee-na-laatste of drie-na-laatste partij weegt
		 * 20. Een partij langer geleden is gewenst en weegt 0.
		 *
		 * Voorwaarde 2: Geen speler die een veel hogere of lagere ranking
		 * heeft. Hiertoe wordt vastgesteld dat een tegenstander 1 ranking
		 * hoger/lager 0 weegt. Een tegenstander 2 rankings hoger/lager weegt
		 * 10. Een tegenstander 3 rankings hoger/lager weegt 20. Een
		 * tegenstander 4 rankings hoger/lager weegt 30. Een tegenstander 5
		 * rankings hoger/lager weegt 50. Een tegenstander 6 rankings
		 * hoger/lager weegt 80. Een tegenstander 7 of meer rankings hoger/lager
		 * weegt 100.
		 *
		 * Voorwaarde 3: Iedere tegenstander moet zoveel mogelijk evenveel met
		 * wit als zwart spelen Hiertoe wordt een weging vastgesteld volgens het
		 * volgens matrix.
		 *
		 * 0 z1 z2 w1 w2 0 -50 -100 50 100 0 0 20 35 50 35 50 z1 -50 35 60 75 10
		 * 25 z2 -100 50 75 100 25 0 w1 50 35 10 25 60 75 w2 100 50 25 0 75 100
		 *
		 * Indien het om doorschuiven gaat en het om de eerste serie gaat is er
		 * nog een 4e voorwaarde. De doorschuivende speler moet tegen iemand van
		 * de hogere groep zijn.
		 *
		 * Een speler van de eigen groep weegt 100 Een speler van de hogere
		 * groep weegt 0
		 *
		 * Indien het om doorschuiven gaat en het om de tweede serie gaat is er
		 * een andere 4e voorwaarde. De doorschuivende speler speelt bij
		 * voorkeur tegen iemand van zijn eigen groep.
		 *
		 * Een speler van de eigen groep weegt 0 Een speler van de hogere groep
		 * weegt 80
		 *
		 * De vierkante matrices met dimensie (aantal spelers,aantal spelers)
		 * worden bij elkaar opgesteld. Dit genereert een matrix met integers.
		 * Deze wordt hierna geoptimaliseerd door de diagonaal (is al nul) en de
		 * sub- en superdiagonaal te minimaliseren. Hiertoe wordt een iteratie
		 * uitgevoerd van algorithme minimizetrigonal
		 *
		 * Het is nog mogelijk de verhoudingen in zwaarte van de voorwaarden aan
		 * te passen met de volgende parameters. mf1 mf2 mf3 mf4
		 */
		int matrix1[][] = new int[wedstrijdgroep.getAantalSpelers()][wedstrijdgroep.getAantalSpelers()+1];
		int matrix2[][] = new int[wedstrijdgroep.getAantalSpelers()][wedstrijdgroep.getAantalSpelers()+1];
		int matrix3[][] = new int[wedstrijdgroep.getAantalSpelers()][wedstrijdgroep.getAantalSpelers()+1];
		int matrix4[][] = new int[wedstrijdgroep.getAantalSpelers()][wedstrijdgroep.getAantalSpelers()+1];
		int matrix[][] = new int[wedstrijdgroep.getAantalSpelers()][wedstrijdgroep.getAantalSpelers()+1];
		double mf1 = IJCController.c().fuzzyWegingAndereTegenstander;		// Niet tegen dezelfde tegenstander
		double mf2 = IJCController.c().fuzzyWegingAfstandRanglijst;			// Verschil in positie op de ranglijst
		double mf3 = IJCController.c().fuzzyWegingZwartWitVerdeling;		// Zwart/wit verdeling
		double mf4 = IJCController.c().fuzzyWegingDoorschuiverEigenGroep;	// Doorschuivers voorkeur voor eigen groep
		int i, j, weging = 0;
		int tegenstanders[] = new int[4];
		// matrix1 : Niet tegen dezelfde tegenstander
		System.out.print("Initializing Matrix1\n");
		i = 1;
		for (Speler s1 : wedstrijdgroep.getSpelers()) {
			matrix1[i-1][0] = s1.getId();
			j = 1;
			for (Speler s2: wedstrijdgroep.getSpelers()){
				weging = 0;
				if (i == j) {
					weging = 0;
				} else {
					if (serie == 0) {
						// Als eerste serie dan ? ...
						tegenstanders = s1.getGespeeldTegen(s2);
					} else {
						// Als niet eerste serie dan ? ...
						tegenstanders = s1.getGespeeldTegen(s2);
					}
					for (int k = 0; k < 4; k++) {
						if (tegenstanders[k] == 1) {
							weging += 140;
						}
						if (tegenstanders[k] == 2) {
							weging += 90;
						}
						if ((tegenstanders[k] > 2) && (tegenstanders[k] < 5)) {
							weging += 20;
						}
					}
				}
				matrix1[i - 1][j] = weging;
				j++;
			}
			i++;
		}		
		// Utils.printMatrix(matrix1);
		// matrix2 : Geen speler die een veel hogere of lagere ranking heeft.
		System.out.print("Initializing Matrix2\n");
		for (i = 1; i <= wedstrijdgroep.getAantalSpelers(); i++) {
			matrix2[i-1][0] = 0;
			for (j = 1; j <= wedstrijdgroep.getAantalSpelers(); j++) {
				if ((i>=wedstrijdgroep.getAantalSpelers()-(2*doorschuivers)) || (j>=wedstrijdgroep.getAantalSpelers()-(2*doorschuivers))) {
					switch (Math.max(0,Math.abs(j - i)-doorschuivers)) {
					case 0:
					case 1:
						matrix2[i - 1][j] = 0;
						break;
					case 2:
						matrix2[i - 1][j] = 15;
						break;
					case 3:
						matrix2[i - 1][j] = 40;
						break;
					case 4:
						matrix2[i - 1][j] = 60;
						break;
					case 5:
						matrix2[i - 1][j] = 90;
						break;
//					case 6:
//						matrix2[i - 1][j] = 80;
//						break;
					default:
						matrix2[i - 1][j] = 200;
///						matrix2[i - 1][j] = 100;
						break;
					}
				} else {
					switch (Math.abs(j - i)) {
					case 0:
					case 1:
						matrix2[i - 1][j] = 0;
						break;
					case 2:
						matrix2[i - 1][j] = 10;
						break;
					case 3:
						matrix2[i - 1][j] = 20;
						break;
					case 4:
						matrix2[i - 1][j] = 30;
						break;
					case 5:
						matrix2[i - 1][j] = 50;
						break;
					case 6:
						matrix2[i - 1][j] = 80;
						break;
					default:
						//matrix2[i - 1][j] = 200;
						matrix2[i - 1][j] = 100;
						break;
					}
				}	
			}
		}
		//Utils.printMatrix(matrix2);
		// matrix 3 : Iedere tegenstander moet zoveel mogelijk evenveel met wit
		// als zwart spelen
		System.out.print("Initializing Matrix3\n");
		i = 1;
		for (Speler s1 : wedstrijdgroep.getSpelers()) {
			matrix3[i-1][0] = 0;
			int witv1 = (int) s1.getWitvoorkeur();
			j = 1;
			for (Speler s2 : wedstrijdgroep.getSpelers()) {
				int witv2 = (int) s2.getWitvoorkeur();
				if (i == j) {
					matrix3[i - 1][j] = 0;
				} else {
					switch (witv1) {
					case -2:
						switch (witv2) {
						case -2:
							matrix3[i - 1][j] = 100;
							break;
						case -1:
							matrix3[i - 1][j] = 75;
							break;
						case 0:
							matrix3[i - 1][j] = 50;
							break;
						case 1:
							matrix3[i - 1][j] = 25;
							break;
						case 2:
							matrix3[i - 1][j] = 0;
							break;
						}
						break;
					case -1:
						switch (witv2) {
						case -2:
							matrix3[i - 1][j] = 75;
							break;
						case -1:
							matrix3[i - 1][j] = 60;
							break;
						case 0:
							matrix3[i - 1][j] = 35;
							break;
						case 1:
							matrix3[i - 1][j] = 10;
							break;
						case 2:
							matrix3[i - 1][j] = 25;
							break;
						}
						break;
					case 0:
						switch (witv2) {
						case -2:
							matrix3[i - 1][j] = 50;
							break;
						case -1:
							matrix3[i - 1][j] = 25;
							break;
						case 0:
							matrix3[i - 1][j] = 20;
							break;
						case 1:
							matrix3[i - 1][j] = 35;
							break;
						case 2:
							matrix3[i - 1][j] = 50;
							break;
						}
						break;
					case 1:
						switch (witv2) {
						case -2:
							matrix3[i - 1][j] = 25;
							break;
						case -1:
							matrix3[i - 1][j] = 10;
							break;
						case 0:
							matrix3[i - 1][j] = 35;
							break;
						case 1:
							matrix3[i - 1][j] = 60;
							break;
						case 2:
							matrix3[i - 1][j] = 75;
							break;
						}
						break;
					case 2:
						switch (witv2) {
						case -2:
							matrix3[i - 1][j] = 0;
							break;
						case -1:
							matrix3[i - 1][j] = 25;
							break;
						case 0:
							matrix3[i - 1][j] = 50;
							break;
						case 1:
							matrix3[i - 1][j] = 75;
							break;
						case 2:
							matrix3[i - 1][j] = 100;
							break;
						}
						break;
					}
				}
				j++;
			}
			i++;
		}
		//Utils.printMatrix(matrix3);
		// matrix 4 : De doorschuivende speler speelt bij voorkeur tegen iemand
		// van zijn de hogere groep in eerste serie en in de tweede serie juist tegen iemand van zijn eigen groep.
		System.out.print("Initializing Matrix4\n");
		i = 1;
		for (Speler s1 : wedstrijdgroep.getSpelers()) {
			matrix4[i-1][0] = 0;
			j = 1;
			for (Speler s2 : wedstrijdgroep.getSpelers()) {
				if (i == j) {
					matrix4[i - 1][j] = 0;
				} else {
					switch (serie) {
					case 0:
						if (wedstrijdgroep.getSpelersMetAnderNiveau().contains(s1)
								&& wedstrijdgroep.getSpelersMetAnderNiveau()
										.contains(s2)) {
							matrix4[i - 1][j] = 100;
						} else {
							if (wedstrijdgroep.getSpelersMetAnderNiveau().contains(s1)
									&& !(wedstrijdgroep.getSpelersMetAnderNiveau()
											.contains(s2))) {
								matrix4[i - 1][j] = 0;
							}
						}
						if (!wedstrijdgroep.getSpelersMetAnderNiveau().contains(s2)
								&& wedstrijdgroep.getSpelersMetAnderNiveau()
										.contains(s2)) {
							matrix4[i - 1][j] = 0;
						} else {
							if (!wedstrijdgroep.getSpelersMetAnderNiveau().contains(s1)
									&& !wedstrijdgroep.getSpelersMetAnderNiveau()
											.contains(s2)) {
								matrix4[i - 1][j] = 10;
							}
						}
						break;
					case 1:
						if (wedstrijdgroep.getSpelersMetAnderNiveau().contains(s1)
								&& wedstrijdgroep.getSpelersMetAnderNiveau()
										.contains(s2)) {
							matrix4[i - 1][j] = 0;
						} else {
							if (wedstrijdgroep.getSpelersMetAnderNiveau().contains(s1)
									&& !wedstrijdgroep.getSpelersMetAnderNiveau()
											.contains(s2)) {
								matrix4[i - 1][j] = 80;
							}
						}
						if (!wedstrijdgroep.getSpelersMetAnderNiveau().contains(s1)
								&& wedstrijdgroep.getSpelersMetAnderNiveau()
										.contains(s2)) {
							matrix4[i - 1][j] = 80;
						} else {
							if (!wedstrijdgroep.getSpelersMetAnderNiveau().contains(s1)
									&& !wedstrijdgroep.getSpelersMetAnderNiveau()
											.contains(s2)) {
								matrix4[i - 1][j] = 0;
							}
						}
						break;
					}
				}
				j++;
			}
			i++;
		}
		//Utils.printMatrix(matrix4);
		//

		matrix = Utils.add2DArrays(mf1, matrix1, mf2, matrix2);
		matrix = Utils.add2DArrays(1, matrix, mf3, matrix3);
		matrix = Utils.add2DArrays(1, matrix, mf4, matrix4);
		//System.out.print("Output Matrix\n");
		//Utils.printMatrix(matrix);
		return matrix;
	}

	private void SpelersNamenopvolgorde(int[][] tri, int indexrow, ArrayList<Speler> spelers) {
    	for (int i=0;i<tri.length;i++) {
    		System.out.print("Speler ID " + spelers.get(tri[i][indexrow-1]-1).getId() + " met naam " + spelers.get(tri[i][indexrow-1]-1).getNaam() + " staat op plaats " + i + ".\n");
    	}
    }

    protected Groep updateSpelers(Groep groep, Serie serie) {
    	double wv;
        for (Speler speler : groep.getSpelers()) {
        	wv = speler.getWitvoorkeur();
            Wedstrijd wedstrijd = serie.getWedstrijdVoorSpeler(speler);
            if (wedstrijd != null) {
                if (wedstrijd.getWit().gelijkAan(speler)) {
                    // Speler speelde met wit
                    speler.addTegenstander(wedstrijd.getZwart().getInitialen());
                    speler.setWitvoorkeur(speler.getWitvoorkeur() - 1);
                
                } else if (wedstrijd.getZwart().gelijkAan(speler)) {
                    // Speler speelde met zwart
                    speler.addTegenstander(wedstrijd.getWit().getInitialen());

                    speler.setWitvoorkeur(speler.getWitvoorkeur() + 1);
                
                } else {
                    System.out.println("Hmmm, speler niet gevonden....");
                }
                logger.log(Level.INFO, "witvoorkeur voor speler " + speler.getNaam() + " aangepast van " + wv + " naar "+ speler.getWitvoorkeur());
            }
        }
        return groep;
    }

    protected Groep updateSpelers(Groep groep, ArrayList<Wedstrijd> triowedstrijden) {
    	double wv;
        for (Wedstrijd W: triowedstrijden) {
            if (W != null) {
            	wv = W.getWit().getWitvoorkeur();
            	groep.getSpelerByID(W.getWit().getId()).setWitvoorkeur(W.getWit().getWitvoorkeur() - 1);
            	groep.getSpelerByID(W.getWit().getId()).addTegenstander(W.getZwart().getInitialen());
                logger.log(Level.INFO, "witvoorkeur voor speler " + W.getWit().getNaam() + " aangepast van " + wv + " naar "+ W.getWit().getWitvoorkeur());
            	wv = W.getZwart().getWitvoorkeur();
            	groep.getSpelerByID(W.getZwart().getId()).setWitvoorkeur(W.getZwart().getWitvoorkeur() + 1);
            	groep.getSpelerByID(W.getZwart().getId()).addTegenstander(W.getWit().getInitialen());
                logger.log(Level.INFO, "witvoorkeur voor speler " + W.getZwart().getNaam() + " aangepast van " + wv + " naar "+ W.getZwart().getWitvoorkeur());
            }
        }
        return groep;
    }

}

