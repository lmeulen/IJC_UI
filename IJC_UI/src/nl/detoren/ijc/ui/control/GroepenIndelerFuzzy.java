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
 *
 */
public class GroepenIndelerFuzzy extends GroepenIndeler implements GroepenIndelerInterface {
	
	private final static Logger logger = Logger.getLogger(GroepenIndelerFuzzy.class.getName());

	private int fuzzymatrix[][];
	
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
		// ALS 5 spelers in 2 ronden, dupliceer spelers naar 10 en plan
		// maar 1 ronde in. Dit heeft het juiste aantal wedstrijden tot gevolg
		if (groep.getAantalSpelers() == 5 && speelrondes == 2) {
			speelrondes = 1;
			logger.log(Level.INFO, "Vijf spelers met 2 rondes dus spelers verdubbelen en maar één serie");
			for (Speler s : wedstrijdgroep.getSpelers()) {
				groep.addSpeler(new Speler(s));
			}
			// plan 1 round and duplicate players
		}
		// Introductie Fuzzy Logic
		//
		int wedstrijdnr = 1;
		int trioloc = 0;
		int[] trio = {0,1,2};
		int indexrow = 1;
		int order[] = new int[groep.getAantalSpelers()];
		for (int i = 0; i < speelrondes; i++) {
			System.out.print(
					"Creating serie " + Integer.toString(i + 1) + " voor groep " + groep.getNaam() + "\n");
			int[][] matrix = MaakFuzzyMatrix(groep, i);
			Utils.printMatrix(matrix);
			if (i > 0) {
				if (!(trioloc == 0)) {
					// Stap 2 Trio spelers verwijderen uit matrix
					//
					// int[] triolocarr = { order[trioloc - 1], order[trioloc], order[trioloc + 1] };
					// BUG onderdstaande klopt niet. triolocarr bevat niet de
					// juiste indices
					int[][] matrix2 = Utils.removerowandcolumnfrom2D(matrix, trio, indexrow);
					//
					// System.out.print("Reduced Matrix\n");
					Utils.printMatrix(matrix2);
					fuzzymatrix = matrix2;
				} else {
					fuzzymatrix = matrix;
				}
			} else {
				fuzzymatrix = matrix;
			}
			int[][] fmatrix = fuzzymatrix;

			logger.log(Level.INFO, "FuzzyMatrix created.");
			System.out.print("Trigonalization of Matrix\n");
			minimizetriagonal triagonal = new minimizetriagonal();
			triagonal.setA(fuzzymatrix);
			triagonal.setOrder(order);
			triagonal.setIterations(groep.getAantalSpelers());
			triagonal.Iterminimizetriagonal();
			order = minimizetriagonal.getOrder();
			int[][] tri = minimizetriagonal.getA();
			Utils.printMatrix(tri);
			if (groep.getNaam().equals("Pionnengroep")) {
				SpelersNamenopvolgorde(tri,indexrow, groep.getSpelers());
				//groep.SpelersNamenopvolgorde(ordertest);
			}
			System.out.print("Deze groep " + groep.getNaam() + " heeft " + tri.length + " spelers.\n");
			trioloc = minimizetriagonal.gettrio(tri,1);
			if (i==0) {
				System.out.print("Geen trio in deze groep.\n");
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
			if ((trioloc == 0) || (i > 0)) {
				for (int k = 0; k <= fmatrix.length - 1; k += 2) {
					Speler s1 = groep.getSpelerByID(tri[k][0]); // Speler
																			// wit
					Speler s2 = groep.getSpelerByID(tri[k + 1][0]); // Speler
																				// zwart
					Wedstrijd w = new Wedstrijd(wedstrijdnr, s1, s2, 0);
					s.addWestrijd(w, true);
					wedstrijdnr++;
					System.out.printf("Wedstrijd tussen " + groep.getSpelerByID(tri[k][0]).getNaam()
							+ " (wit) met index " + tri[k][0] + " en " + groep.getSpelerByID(tri[k + 1][0]).getNaam() + " (zwart)"
									+ " met index " + tri[k+1][0] + "\n");
				}
			} else {
				for (int k = 0; k < trioloc - 2; k += 2) {
					Speler s1 = groep.getSpelerByID(tri[k][0]); // Speler
																			// wit
					Speler s2 = groep.getSpelerByID(tri[k + 1][0]); // Speler
																				// zwart
					Wedstrijd w = new Wedstrijd(wedstrijdnr, s1, s2, 0);
					s.addWestrijd(w, true);
					wedstrijdnr++;
					System.out.printf("Wedstrijd tussen " + groep.getSpelerByID(tri[k][0]).getNaam()
							+ " (wit) met index " + tri[k][0] + " en " + groep.getSpelerByID(tri[k + 1][0]).getNaam() + " (zwart)"
									+ " met index " + tri[k+1][0] +  " \n");
				}
				for (int k = trioloc + 2; k <= fmatrix.length - 1; k += 2) {
					Speler s1 = groep.getSpelerByID(tri[k][0]); // Speler
																			// wit
					Speler s2 = groep.getSpelerByID(tri[k + 1][0]); // Speler
																				// zwart
					Wedstrijd w = new Wedstrijd(wedstrijdnr, s1, s2, 0);
					s.addWestrijd(w, true);
					System.out.printf("Wedstrijd tussen " + groep.getSpelerByID(tri[k][0]).getNaam()
							+ " (wit) met index " +  tri[k][0] +  " en " + groep.getSpelerByID(tri[k + 1][0]).getNaam() + " (zwart)"
									+ " met index " + tri[k+1][0] + "\n");
					wedstrijdnr++;
				}
				// trio
				if (i == 0) {
					gws.addTrioWedstrijd(new Wedstrijd(2 * (wedstrijdnr - 1) + 1,
							groep.getSpelerByID(tri[trioloc - 1][0]),
							groep.getSpelerByID(tri[trioloc][0]), 0));
					System.out.printf("Wedstrijd uit trio tussen"
							+ groep.getSpelerByID(tri[trioloc - 1][0]).getNaam() + " (wit) met index " + tri[i][0]
									+ " en "	+ groep.getSpelerByID(tri[trioloc][0]).getNaam() + " (zwart) met index "
									+ tri[i+1][0] + "\n");
					gws.addTrioWedstrijd(
							new Wedstrijd(2 * (wedstrijdnr - 1) + 2, groep.getSpelerByID(tri[trioloc][0]),
									groep.getSpelerByID(tri[trioloc + 1][0]), 0));
					System.out.printf("Wedstrijd uit trio tussen"
							+ groep.getSpelerByID(tri[trioloc][0]).getNaam() + " (wit) met index " + tri[i][0]
									+ " en " + groep.getSpelerByID(tri[trioloc + 1][0]).getNaam() + " (zwart) met index "
									+  tri[i+1][0] + "\n");
					gws.addTrioWedstrijd(new Wedstrijd(2 * (wedstrijdnr - 1) + 3,
							groep.getSpelerByID(tri[trioloc - 1][0]),
							groep.getSpelerByID(tri[trioloc + 1][0]), 0));
					System.out.printf("Wedstrijd uit trio tussen"
							+ groep.getSpelerByID(tri[trioloc + 1][0]).getNaam() + " (wit) met index " + tri[i][0]
									+ " en " + groep.getSpelerByID(tri[trioloc - 1][0]).getNaam() + " (zwart) met index"
									+ tri[i+1][0] + "\n");
				}
				// Einde trio
			}
			if (s != null) {
				s.renumber(i); // Hernummer wedstrijden.
				gws.addSerie(s);
				logger.log(Level.INFO, "Voeg Serie toe");
				groep = updateSpelers(groep, s);
				logger.log(Level.INFO, "Update Spelers");
				// update gegevens tegenstanders en witvoorkeur
			}
		}

		return gws;
	}

	public int[][] MaakFuzzyMatrix(Groep wedstrijdgroep, int serie) {
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
		double mf1 = 1;
		double mf2 = 1;
		double mf3 = 1;
		double mf4 = 1;
		int i, j, weging = 0;
		int tegenstanders[] = new int[4];
		// matrix1 : Niet tegen dezelfde tegenstander
		System.out.print("Initializing Matrix1\n");
		for (i = 1; i <= wedstrijdgroep.getAantalSpelers(); i++) {
			matrix1[i-1][0] = wedstrijdgroep.getSpelerByID(i).getId();
			for (j = 1; j <= wedstrijdgroep.getAantalSpelers(); j++) {
				// logger.log(Level.INFO, "Speler 1 ID : " +
				// wedstrijdgroep.getSpelerByID(i));
				// logger.log(Level.INFO, "Speler 2 ID : " +
				// wedstrijdgroep.getSpelerByID(j));
				weging = 0;
				if (i == j) {
					weging = 0;
				} else {
					if (serie == 0) {
						tegenstanders = wedstrijdgroep.getSpelerByID(i).getGespeeldTegen(wedstrijdgroep.getSpelerByID(j));
					} else {
						tegenstanders = wedstrijdgroep.getSpelerByID(i).getGespeeldTegen(wedstrijdgroep.getSpelerByID(j));
					}
					for (int k = 0; k < 4; k++) {
						if (tegenstanders[k] == 1) {
							weging += 140;
							System.out.print(wedstrijdgroep.getSpelerByID(i).getNaam() + " heeft " + tegenstanders[k]
									+ " ronden eerder al gespeeld tegen " + wedstrijdgroep.getSpelerByID(j).getNaam()
									+ "\n");
						}
						if (tegenstanders[k] == 2) {
							weging += 90;
							System.out.print(wedstrijdgroep.getSpelerByID(i).getNaam() + " heeft " + tegenstanders[k]
									+ " ronden eerder al gespeeld tegen " + wedstrijdgroep.getSpelerByID(j).getNaam()
									+ "\n");
						}
						if ((tegenstanders[k] > 2) && (tegenstanders[k] < 5)) {
							weging += 20;
							System.out.print(wedstrijdgroep.getSpelerByID(i).getNaam() + " heeft " + tegenstanders[k]
									+ " ronden eerder al gespeeld tegen " + wedstrijdgroep.getSpelerByID(j).getNaam()
									+ "\n");
						}
					}
				}
				matrix1[i - 1][j] = weging;
			}
		}
		//Utils.printMatrix(matrix1);
		// matrix2 : Geen speler die een veel hogere of lagere ranking heeft.
		System.out.print("Initializing Matrix2\n");
		for (i = 1; i <= wedstrijdgroep.getAantalSpelers(); i++) {
			matrix2[i-1][0] = 0;
			for (j = 1; j <= wedstrijdgroep.getAantalSpelers(); j++) {
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
		//Utils.printMatrix(matrix2);
		// matrix 3 : Iedere tegenstander moet zoveel mogelijk evenveel met wit
		// als zwart spelen
		System.out.print("Initializing Matrix3\n");
		for (i = 1; i <= wedstrijdgroep.getAantalSpelers(); i++) {
			matrix3[i-1][0] = 0;
			int witv1 = (int) wedstrijdgroep.getSpelerByID(i).getWitvoorkeur();
			for (j = 1; j <= wedstrijdgroep.getAantalSpelers(); j++) {
				int witv2 = (int) wedstrijdgroep.getSpelerByID(j).getWitvoorkeur();
				if (/*i == j*/ true) {
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
					;
				}
			}
		}
		//Utils.printMatrix(matrix3);
		// matrix 4 : De doorschuivende speler speelt bij voorkeur tegen iemand
		// van zijn eigen groep.
		System.out.print("Initializing Matrix4\n");
		for (i = 1; i <= wedstrijdgroep.getAantalSpelers(); i++) {
			matrix4[i-1][0] = 0;
			for (j = 1; j <= wedstrijdgroep.getAantalSpelers(); j++) {
				if (i == j) {
					matrix4[i - 1][j] = 0;
				} else {
					switch (serie) {
					case 0:
						if (wedstrijdgroep.getSpelersMetAnderNiveau().contains(wedstrijdgroep.getSpelerByID(i))
								&& wedstrijdgroep.getSpelersMetAnderNiveau()
										.contains(wedstrijdgroep.getSpelerByID(j))) {
							matrix4[i - 1][j] = 100;
						} else {
							if (wedstrijdgroep.getSpelersMetAnderNiveau().contains(wedstrijdgroep.getSpelerByID(i))
									&& !(wedstrijdgroep.getSpelersMetAnderNiveau()
											.contains(wedstrijdgroep.getSpelerByID(j)))) {
								matrix4[i - 1][j] = 0;
							}
						}
						if (!wedstrijdgroep.getSpelersMetAnderNiveau().contains(wedstrijdgroep.getSpelerByID(i))
								&& wedstrijdgroep.getSpelersMetAnderNiveau()
										.contains(wedstrijdgroep.getSpelerByID(j))) {
							matrix4[i - 1][j] = 0;
						} else {
							if (!wedstrijdgroep.getSpelersMetAnderNiveau().contains(wedstrijdgroep.getSpelerByID(i))
									&& !wedstrijdgroep.getSpelersMetAnderNiveau()
											.contains(wedstrijdgroep.getSpelerByID(j))) {
								matrix4[i - 1][j] = 10;
							}
						}
						break;
					case 1:
						if (wedstrijdgroep.getSpelersMetAnderNiveau().contains(wedstrijdgroep.getSpelerByID(i))
								&& wedstrijdgroep.getSpelersMetAnderNiveau()
										.contains(wedstrijdgroep.getSpelerByID(j))) {
							matrix4[i - 1][j] = 0;
						} else {
							if (wedstrijdgroep.getSpelersMetAnderNiveau().contains(wedstrijdgroep.getSpelerByID(i))
									&& !wedstrijdgroep.getSpelersMetAnderNiveau()
											.contains(wedstrijdgroep.getSpelerByID(j))) {
								matrix4[i - 1][j] = 80;
							}
						}
						if (!wedstrijdgroep.getSpelersMetAnderNiveau().contains(wedstrijdgroep.getSpelerByID(i))
								&& wedstrijdgroep.getSpelersMetAnderNiveau()
										.contains(wedstrijdgroep.getSpelerByID(j))) {
							matrix4[i - 1][j] = 80;
						} else {
							if (!wedstrijdgroep.getSpelersMetAnderNiveau().contains(wedstrijdgroep.getSpelerByID(i))
									&& !wedstrijdgroep.getSpelersMetAnderNiveau()
											.contains(wedstrijdgroep.getSpelerByID(j))) {
								matrix4[i - 1][j] = 0;
							}
						}
						break;
					}
				}
			}
		}
		//Utils.printMatrix(matrix4);
		matrix = Utils.add2DArrays(mf1, matrix1, mf2, matrix2);
		matrix = Utils.add2DArrays(1, matrix, mf3, matrix3);
		matrix = Utils.add2DArrays(1, matrix, mf4, matrix4);
		//System.out.print("Output Matrix\n");
		//Utils.printMatrix(matrix);
		return matrix;
	}
	
	public void SpelersNamenopvolgorde(int[][] tri, int indexrow, ArrayList<Speler> spelers) {    	
    	for (int i=0;i<tri.length;i++) {
    		System.out.print("Speler ID " + spelers.get(tri[i][indexrow-1]-1).getId() + " met naam " + spelers.get(tri[i][indexrow-1]-1).getNaam() + " staat op plaats " + i + ".\n");
    	}
    }}

