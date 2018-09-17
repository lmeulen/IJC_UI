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
import nl.detoren.ijc.data.groepen.Groepen;
import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.data.wedstrijden.Groepswedstrijden;
import nl.detoren.ijc.data.wedstrijden.Serie;
import nl.detoren.ijc.data.wedstrijden.Wedstrijd;
import nl.detoren.ijc.data.wedstrijden.Wedstrijden;

/**
 * Deelt de groepen in op basis van aanwezigheid en methode. Mogelijke methoden zijn
 * 1. Zonder doorschuiven
 * 2. 3 of 4 spelers schuiven een groep omhoog.
 * Eindresultaat is altijd een groep met even spelers
 *
 */
public class GroepenIndeler implements GroepenIndelerInterface {

    private final static Logger logger = Logger.getLogger(GroepenIndeler.class.getName());

    /* (non-Javadoc)
	 * @see nl.detoren.ijc.ui.control.GroepenIndelerInterface#maakGroepsindeling(nl.detoren.ijc.data.groepen.Groepen)
	 */
    @Override
	public Groepen maakGroepsindeling(Groepen aanwezigheidsGroepen) {
    	logger.log(Level.INFO, "Maken groepsindeling voor alle groepen");
        // Er wordt een nieuwe groepen gemaakt, welke stapsgewijs gevuld gaat worden.
        int ronde = aanwezigheidsGroepen.getRonde();
        int periode = aanwezigheidsGroepen.getPeriode();
    	logger.log(Level.INFO, "Groepsindeling voor periode " + periode + ", ronde " + ronde);
        Groepen wedstrijdGroepen = new Groepen();
        wedstrijdGroepen.setPeriode(periode);
        wedstrijdGroepen.setRonde(ronde);
        // Eerst bepalen we de aanwezige spelers
        // Groepen worden gekopieerd maar zonder de afwezige spelers
        for (Groep groep : aanwezigheidsGroepen.getGroepen()) {
        	logger.log(Level.INFO, "Indeling voor groep " + groep.getNaam());
            Groep wedstrijdGroep = new Groep();
            wedstrijdGroep.setNiveau(groep.getNiveau());
            for (Speler speler : groep.getSpelers()) {
                if (speler.isAanwezig()) {
                	logger.log(Level.FINE, "Toevoegen aan wedstrijdgroep van speler" + speler.getNaam());
                    wedstrijdGroep.addSpeler(new Speler(speler));
                }
            }
            wedstrijdGroepen.addGroep(wedstrijdGroep);
        }
        // indien van toepassing, schuif maximaal 4 spelers door
        if (bepaalDoorschuiven(0, periode, ronde)) {
        	logger.log(Level.INFO, "Er wordt doorgeschoven, schuif door");
            doorschuiven(wedstrijdGroepen, aanwezigheidsGroepen);

        }
        // Hernummer alle groepen om overzicht te behouden
        // en dubbele nummers in een groep te voorkomen
    	logger.log(Level.INFO, "Hernummeren van spelers");
        wedstrijdGroepen.hernummerGroepen();
        return wedstrijdGroepen;
    }

    /* (non-Javadoc)
	 * @see nl.detoren.ijc.ui.control.GroepenIndelerInterface#maakGroepsindeling(nl.detoren.ijc.data.groepen.Groepen, nl.detoren.ijc.data.groepen.Groepen, int)
	 */
    @Override
	public Groepen maakGroepsindeling(Groepen aanwezigheidsGroepen, Groepen wedstrijdGroepen, int groepID) {
    	logger.log(Level.INFO, "Maken groepsindeling voor groep" + aanwezigheidsGroepen.getGroepById(groepID).getNaam());
    	Groep aanwezigheidsGroep = aanwezigheidsGroepen.getGroepById(groepID);
    	Groep origineleWedstrijdGroep = wedstrijdGroepen.getGroepById(groepID);
    	// Zoek spelers uit deze groep die doorgeschoven zijn naar een hogere groep
    	ArrayList<Speler> doorgeschoven = new ArrayList<>();
    	Groep groepHoger = wedstrijdGroepen.getGroepById(groepID+1);
    	if (groepHoger != null) {
        	logger.log(Level.FINE, "Bepalen doorgeschoven spelers in deze groep");
    		doorgeschoven = groepHoger.getSpelersMetAnderNiveau();
        	logger.log(Level.INFO, "Aantal doorgeschoven spelers : " + doorgeschoven.size());
    	}
    	// Creeer nieuwe groep
    	// Neem alle aanwezige spelers hier in op, behalve degene die al doorgeschoven
    	// zijn naaar een hogere groep
    	Groep nieuweWedstrijdGroep = new Groep();
        nieuweWedstrijdGroep.setNiveau(aanwezigheidsGroep.getNiveau());
        for (Speler speler : aanwezigheidsGroep.getSpelers()) {
            if (speler.isAanwezig() && !groepBevat(doorgeschoven, speler)) {
            	logger.log(Level.INFO, "Toevoegen aan wedstrijdgroep van speler               : " + speler.getNaam());
                nieuweWedstrijdGroep.addSpeler(new Speler(speler));
            }
        }
        // Kopieer doorgescheven spelers uit oude lijst
        for (Speler speler : origineleWedstrijdGroep.getSpelers()) {
        	if (speler.getGroep() != origineleWedstrijdGroep.getNiveau()) {
            	logger.log(Level.INFO, "Toevoegen aan wedstrijdgroep van doorgeschoven speler : " + speler.getNaam());
        		nieuweWedstrijdGroep.addSpelerHoudNiveau(speler);
        	}
        }
    	logger.log(Level.INFO, "Aantal spelers in wedstrijdgroep: " + nieuweWedstrijdGroep.getAantalSpelers());
        nieuweWedstrijdGroep.renumber();
    	wedstrijdGroepen.updateGroep(nieuweWedstrijdGroep, groepID);
    	return wedstrijdGroepen;
    }

    protected boolean groepBevat(ArrayList<Speler> doorgeschoven, Speler speler) {
    	for (Speler s : doorgeschoven) {
    		if (s.gelijkAan(speler)) return true;
    	}
    	return false;
    }
    /**
     * Schuif spelers door. Laatste speler wordt alleen doorgeschoven indien dit tot een even
     * aantal spelers in de nieuwe groep leidt.
     * @param wedstrijdGroepen
     * @param aanwezigheidsGroepen
     */
    protected void doorschuiven(Groepen wedstrijdGroepen, Groepen aanwezigheidsGroepen) {
        int aantal = bepaalAantalDoorschuiven(0, aanwezigheidsGroepen.getPeriode(), aanwezigheidsGroepen.getRonde());
    	logger.log(Level.INFO, "Aantal door te schuiven spelers "  + aantal);
        // Doorloop hoogste groep tot ��n na laagste groep. In de laagste groep
        // kunnen geen spelers inschuiven
    	// Let op: iterator gaat op array index en NIET op groepID
        ArrayList<Groep> groepen = wedstrijdGroepen.getGroepen();
//        for (int i = 0; i < groepen.size() - 1; ++i) {
        for (int i = 0; i < wedstrijdGroepen.getAantalGroepen() - 1; ++i) {
            aantal = bepaalAantalDoorschuiven(groepen.get(i).getNiveau(), aanwezigheidsGroepen.getPeriode(), aanwezigheidsGroepen.getRonde());
        	logger.log(Level.INFO, "Doorschuiven van groep "  + groepen.get(i+1).getNaam() + " naar " + groepen.get(i).getNaam() + " n=" + aantal);
            ArrayList<Speler> naarGroep = groepen.get(i).getSpelers();
            if (naarGroep == null) naarGroep = new ArrayList<>();
            ArrayList<Speler> vanGroep = groepen.get(i + 1).getSpelers();
            // Als laatste speler niet aanwezig, dan ��n minder doorschuiven
            Speler laatste = groepen.get(i + 1).getSpelerByID(aantal);
            if (aantal > 2 && laatste == null) aantal--;

            for (int j = 1; j <= aantal; ++j) {
                Speler s = groepen.get(i + 1).getSpelerByID(j);
            	logger.log(Level.FINE, "Speler : " + (s != null ? s.getNaam() : "null"));
                if ((s != null) && s.isAanwezig()) {
                    if ((j == aantal) && (aantal == 1)) {
                        // Alleen doorschuiven als speler 1 niet meer ingehaald kan worden
                        Speler s2 = groepen.get(i + 1).getSpelerByID(2);
						if (!IJCController.c().laasteRondeDoorschuivenAltijd) {
							if ((s2 != null) && (s.getPunten() > (s2.getPunten() + 4))) {
								logger.log(Level.FINE, "Speler doorgeschoven, niet meer in te halen ");
								naarGroep.add(new Speler(s));
								vanGroep.remove(s);

							}
						}
                    } else if (j == aantal) {
                        if (naarGroep.size() % 2 != 0) {
                        	logger.log(Level.FINE, "Speler doorgeschoven, laatste doorschuiver maar door om even aantal ");
                            naarGroep.add(new Speler(s));
                            vanGroep.remove(s);
                        }
                    } else {
                    	logger.log(Level.FINE, "Speler doorgeschoven, niet laatste dus altijd");
                        naarGroep.add(new Speler(s));
                        vanGroep.remove(s);

                    }
                }

            }
        }
    }

    /**
     * Op basis van periode en ronde gegevens wordt bepaald of er wel of niet wordt doorgeschoven
     *
     * @param periode Huidige periode
     * @param ronde Huidige ronde
     * @return true als er met doorschuiven wordt gespeeld
     */
    protected boolean bepaalDoorschuiven(int groep, int periode, int ronde) {
    	return (bepaalAantalDoorschuiven(groep, periode, ronde) > 0);
    }

    protected int bepaalAantalDoorschuiven(int groep, int periode, int ronde) {
    	return IJCController.c().bepaalAantalDoorschuivers(groep, periode, ronde);
    }

    /**
     * Bepaal het minimale verschil tussen twee spelers die tegen elkaar spelen
     * @param groep De groep
     * @param periode Periode
     * @param ronde Ronde in de periode
     * @param serie serie wedstrijden binnen de ronde
     * @return
     */
    protected int bepaalMinimaalVerschil(Groep groep, int periode, int ronde, int serie) {
        int aantal = groep.getSpelers().size();
    	logger.log(Level.INFO, "Periode " + periode + " ronde " + ronde + " serie " + serie);
    	logger.log(Level.INFO, "groep " + groep.getNaam() + " met grootte " + aantal);
    	int resultaat;

    	if ((IJCController.c().specialeIndelingEersteRonde) && (ronde == 1)) {
            resultaat = (serie == 1 ? (aantal / 2) : (serie == 2 ? 1 : 2));
    	} else {
            resultaat = serie;
    	}
        String log = groep.getNaam() + " in periode "+ periode + ", ronde " + ronde;
        log += ", serie " + serie + "-> minimaal verschil = " + resultaat;
    	logger.log(Level.INFO, log);
        return resultaat;
    }

    /**
     * Bepaal het het aantal series dat tijdens een ronde wordt gespeeld. Dit is afhankelijk van de groep,
     * de periode en
     * de ronde.
     *
     * @param groep Niveau van de groep
     * @param periode Periode
     * @param ronde Ronde in de periode
     * @return
     */
    protected int bepaalAantalSeries(int groep, int periode, int ronde) {
    	logger.log(Level.INFO, "Vaststellen aantal te spelen series");
    	int aantalSeries = IJCController.c().bepaalAantalSeries(groep, periode, ronde);
    	logger.log(Level.INFO, "Vastgesteld aan series : " + aantalSeries);    	
    	return aantalSeries;
    }

    /* (non-Javadoc)
	 * @see nl.detoren.ijc.ui.control.GroepenIndelerInterface#maakWedstrijdschema(nl.detoren.ijc.data.groepen.Groepen)
	 */
    @Override
	public Wedstrijden maakWedstrijdschema(Groepen groepen) {
    	int periode = groepen.getPeriode();
    	int ronde = groepen.getRonde();
    	logger.log(Level.INFO, "Maken wedstrijden voor periode " + periode + " ronde " + ronde);
        Wedstrijden wedstrijden = new Wedstrijden();
        for (Groep groepOrg : groepen.getGroepen()) {
        	if (groepOrg.getAantalSpelers() == 1) {
    			logger.log(Level.WARNING, "Maar een speler. Kan geen wedstrijden maken. ");
        	} else {
        		logger.log(Level.INFO, "Maken wedstrijden voor groep " + groepOrg.getNaam());
        		Groepswedstrijden gws = maakWedstrijdenVoorGroep(periode, ronde, groepOrg);
        		wedstrijden.addGroepswedstrijden(gws);
        		logger.log(Level.INFO, "Aantal wedstrijden " + gws.getWedstrijden().size());
        	}
        }
        wedstrijden.setPeriode(periode);
        wedstrijden.setRonde(ronde);
        return wedstrijden;
    }

    /* (non-Javadoc)
	 * @see nl.detoren.ijc.ui.control.GroepenIndelerInterface#updateWedstrijdschema(nl.detoren.ijc.data.wedstrijden.Wedstrijden, nl.detoren.ijc.data.groepen.Groepen, int)
	 */
    @Override
	public Wedstrijden updateWedstrijdschema(Wedstrijden wedstrijden, Groepen wedstrijdgroepen, int groepID) {
    	int periode = wedstrijdgroepen.getPeriode();
    	int ronde = wedstrijdgroepen.getRonde();
    	logger.log(Level.INFO, "Update wedstrijden voor groep " + groepID + " periode " + periode + " ronde " + ronde);
        Wedstrijden wedstrijdenNieuw = new Wedstrijden();
        wedstrijdenNieuw.setPeriode(periode);
        wedstrijdenNieuw.setRonde(ronde);
        for (Groepswedstrijden gw : wedstrijden.getGroepswedstrijden()) {
        	if (gw.getNiveau() == groepID) {
        		Groep wsGroep = wedstrijdgroepen.getGroepById(groepID);
        		Groepswedstrijden nieuw = maakWedstrijdenVoorGroep(periode, ronde, wsGroep);
        		wedstrijdenNieuw.addGroepswedstrijden(nieuw);
        	} else {
        		wedstrijdenNieuw.addGroepswedstrijden(gw);
        	}
        }
        return wedstrijdenNieuw;
    }

    /**
     * Bepaal voor een groep de te spelen wedstrijden
     * @param periode
     * @param ronde
     * @param wedstrijdgroep
     * @return
     */
	public Groepswedstrijden maakWedstrijdenVoorGroep(int periode, int ronde, Groep wedstrijdgroep) {
    	logger.log(Level.INFO, "Bepalen wedstrijden voor groep " + wedstrijdgroep.getNaam() + " periode " + periode + " ronde " + ronde);
		wedstrijdgroep.setZWbalansvoor();
    	// Maak clone van de Groep om ongewenste updates te voorkomen
		Groep groep = new Groep();
		groep.setNiveau(wedstrijdgroep.getNiveau());
		for (Speler s : wedstrijdgroep.getSpelers()) {
	    	logger.log(Level.FINE, "Toevoegen van speler " + s.getNaam());
		    groep.addSpeler(new Speler(s));
		}
		if (IJCController.c().sorteerOpRating(groep.getNiveau(), periode, ronde)) {
			// Sorteer keizergroep op rating voor indeling indien ronde = 2,3,4,5 of 6
			groep.sorteerRating();
		}

		// Maak wedstrijden
		Groepswedstrijden gws = new Groepswedstrijden();
		gws.setNiveau(groep.getNiveau());
		int speelrondes = bepaalAantalSeries(groep.getNiveau(), periode, ronde);
    	logger.log(Level.INFO, "Aantal speelrondes " + speelrondes);

		// Trucje voor 5 speler in een wedstrijdgroep:
		// ALS 5 spelers in 2 ronden, dupliceer spelers naar 10 en plan
		// maar 1 ronde in. Dit heeft het juiste aantal wedstrijden tot gevolg
		if (groep.getAantalSpelers() == 5 && speelrondes == 2 ) {
			speelrondes = 1;
	    	logger.log(Level.INFO, "Vijf spelers met 2 rondes dus spelers verdubbelen en maar ��n serie");
		    for (Speler s : wedstrijdgroep.getSpelers()) {
		        groep.addSpeler(new Speler(s));
		    }
		    // plan 1 round and duplicate players
		}

		boolean[] gepland = new boolean[groep.getSpelers().size()];
		int aantalSpelers = groep.getSpelers().size();
		ArrayList<Integer> trio = new ArrayList<>();
		if ((groep.getAantalSpelers() % 2 != 0) && groep.getAantalSpelers() >= 3) {
	    	logger.log(Level.INFO, "Maken van een trio vanwege oneven aantal spelers");
		    // Bij oneven aantal spelers wordt een trio gemaakt.
		    trio = bepaalTrioSpelers(groep);
		    aantalSpelers -= 3;
		    Speler sid1 = groep.getSpelerByID(trio.get(0).intValue());
		    Speler sid2 = groep.getSpelerByID(trio.get(1).intValue());
		    Speler sid3 = groep.getSpelerByID(trio.get(2).intValue());
	    	logger.log(Level.INFO, "Spelers in trio " + sid1.getInitialen() + " " + sid2.getInitialen() + " " + sid3.getInitialen());
	    	if (speelrondes % 2 == 0) {
	    		// Even aantal rondes, dus volledig trio
		    	gws.addTrioWedstrijd(new Wedstrijd(sid1.getId()*100 + sid2.getId(), sid1, sid2, 0));
			    gws.addTrioWedstrijd(new Wedstrijd(sid2.getId()*100 + sid3.getId(), sid2, sid3, 0));
			    gws.addTrioWedstrijd(new Wedstrijd(sid1.getId()*100 + sid3.getId(), sid1, sid3, 0));
	    	} else {
	    		Speler sid4 = new Speler();
	    		sid4.setNaam("Eerste beschikbaar");
		    	gws.addTrioWedstrijd(new Wedstrijd(sid1.getId()*100 + sid2.getId(), sid1, sid2, 0));
		    	gws.addTrioWedstrijd(new Wedstrijd(0, sid1, sid4, 0));
	    	}
		}
		for (int i = 0; i < speelrondes; ++i) {
		    int minverschil = bepaalMinimaalVerschil(groep, periode, ronde, i + 1);
		    for (int j = 0; j < gepland.length; ++j) {
		        gepland[j] = false;
		    }
		    for (Integer sid : trio) {
		        // -1 omdat speler ID een versprongen is tov array nummer
		        gepland[sid.intValue() - 1] = true;
		    }

		    Serie serie = null;
		    int ignoreTgns = 0;
		    int maxverschil = minverschil + IJCController.c().indelingMaximumVerschil;
	        //maxverschil = Math.min(minverschil + 3, groep.getAantalSpelers());
			while ((serie == null) && (maxverschil <= groep.getAantalSpelers())) {
				while ((serie == null) && (ignoreTgns <= 5)) {
					serie = maakSerie(groep, gepland, aantalSpelers, minverschil, maxverschil, ignoreTgns, i);
					ignoreTgns++;
				}
				maxverschil++;
				if (minverschil > 1) minverschil--;
				ignoreTgns = 0;
			}

			if (serie != null) {
				gws.addSerie(serie);
				groep = updateSpelers(groep, serie);
				// update gegevens tegenstanders en witvoorkeur
			}
		}
		logger.log(Level.INFO, "ZW balans voor groep " + wedstrijdgroep.getNaam() + " voor deze ronde is " +wedstrijdgroep.getZWbalansvoor());
		groep.setZWbalansna();
		// Overdragen tijdelijke data naar regliere data voor deze waarde.
		wedstrijdgroep.setZWbalansna(groep.getZWbalansna());
		logger.log(Level.INFO, "ZW balans voor groep " + wedstrijdgroep.getNaam() + " na deze ronde is " + wedstrijdgroep.getZWbalansna());
		return gws;
	}

    protected Serie maakSerie(Groep groep, boolean[] gepland, int aantalSpelers, int minverschil, int maxverschil, int ignoreTgn, int serienr) {
        Serie serie = new Serie();
        int mv = minverschil;
        while (mv >= 0) {
            Serie s = planSerie(serie, groep.getSpelers(), gepland, aantalSpelers, minverschil, maxverschil, ignoreTgn, groep.getNiveau(), 1, serienr);
            if (s != null) {
                return s;
            }
            mv--;
        }
        return null;
    }

    protected Serie planSerie(Serie serie, ArrayList<Speler> spelers, boolean[] gepland,
            int teplannen, int minverschil, int maxverschil, int ignoreTgn, int niveau, int diepte, int serienr) {
        for (int i = 0; i < diepte; ++i) {
            System.out.print("  ");
        }
        System.out.print("vanaf:" + eersteOngeplandeSpeler(gepland, 0) + "#" + teplannen + "minv:" + minverschil);
        System.out.print(",maxv:" + maxverschil + ",ignore:" + ignoreTgn + ",niv:" + niveau + "\n");

        // Laatste ronde?
        if (teplannen < 2) {
            return new Serie();
        }
        // Eerst doorgeschoven spelers inplannen
        // Maar deze speciale behandeling geldt alleen de eerste ronde
        int doorgeschovenID = laatsteOngeplandeDoorgeschovenspeler(spelers, gepland, niveau);
        if ((doorgeschovenID >= 0) && (serienr == 1)) {
            int zoekId = doorgeschovenID - 1;
            while ((zoekId != -1) && ((Math.abs(zoekId - doorgeschovenID) <= maxverschil))) {
                int partner = laatsteOngeplandeSpeler(gepland, zoekId);
                if (partner == -1) {
                    return null;
                }
                Speler s1 = spelers.get(doorgeschovenID);
                Speler s2 = spelers.get(partner);
                if (!s1.isGespeeldTegen(s2, ignoreTgn) && (s2.getGroep() != s1.getGroep())) {
                    gepland[doorgeschovenID] = true;
                    gepland[partner] = true;
                    Serie s = planSerie(serie, spelers, gepland, teplannen - 2, minverschil, maxverschil, ignoreTgn, niveau, diepte + 1, serienr);
                    if (s != null) {
                        Wedstrijd w = new Wedstrijd(s1.getId() * 100 + s2.getId(), s1, s2, 0);
                        s.addWedstrijd(w, true);
                        return s;
                    }
                    gepland[doorgeschovenID] = false;
                    gepland[partner] = false;
                }
                zoekId = partner - 1;
            }
        } else {
            // Inplannen 'gewone' speler
            int plannenID = eersteOngeplandeSpeler(gepland, 0);
            int zoekID = plannenID + minverschil;
            while ((zoekID < gepland.length) && (Math.abs(zoekID - plannenID) <= maxverschil)) {
                int partner = eersteOngeplandeSpeler(gepland, zoekID);
                if (partner == -1) {
                    return null;
                }
                Speler s1 = spelers.get(plannenID);
                Speler s2 = spelers.get(partner);
                if (!s1.isGespeeldTegen(s2, ignoreTgn) && (Math.abs(s2.getId() - s1.getId()) <= maxverschil)) {
                    gepland[plannenID] = true;
                    gepland[partner] = true;
                    Serie s = planSerie(serie, spelers, gepland, teplannen - 2, minverschil, maxverschil, ignoreTgn, niveau, diepte + 1, serienr);
                    if (s != null) {
                        Wedstrijd w = new Wedstrijd(s1.getId() * 100 + s2.getId(), s1, s2, 0);
                        s.addWedstrijd(w, true);
                        return s;
                    }
                    gepland[plannenID] = false;
                    gepland[partner] = false;
                }
                zoekID = partner + 1;
            }
        }
        return null;
    }

    /**
     Vind de eerste ongeplande speler
     @param gepland
     @param start
     @return
     */
    protected static int eersteOngeplandeSpeler(boolean[] gepland, int start) {
        if ((start < 0) || (start >= gepland.length)) {
            return -1;
        }
        for (int i = start; i < gepland.length; ++i) {
            if (!gepland[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     Vind de laatste ongeplande speler beginnende bij start en terugzoekende naar 0
     @param gepland
     @param start
     @return
     */
    protected static int laatsteOngeplandeSpeler(boolean[] gepland, int start) {
        if ((start < 0) || (start >= gepland.length)) {
            return -1;
        }
        for (int i = start; i >= 0; --i) {
            if (!gepland[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     Vind eerste ongeplande speler zoekende vanaf onderen
     @param spelers
     @param gepland
     @param niveau
     @return
     */
    protected int laatsteOngeplandeDoorgeschovenspeler(ArrayList<Speler> spelers, boolean[] gepland, int niveau) {
        for (int i = (gepland.length - 1); i > 0; --i) {
            if (!gepland[i] && spelers.get(i).getGroep() != niveau) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Update de volgende gegevens van een speler: - Witvoorkeur - Tegenstanders
     *
     * @param groep
     * @param serie
     * @return
     */
    protected Groep updateSpelers(Groep groep, Serie serie) {

        for (Speler speler : groep.getSpelers()) {
            Wedstrijd wedstrijd = serie.getWedstrijdVoorSpeler(speler);
            if (wedstrijd != null) {
                if (wedstrijd.getWit().gelijkAan(speler)) {
                    // Speler speelde met wit
                    speler.addTegenstander(wedstrijd.getZwart().getInitialen());
                    speler.setWitvoorkeur(speler.getWitvoorkeur() - 1.1);

                } else if (wedstrijd.getZwart().gelijkAan(speler)) {
                    // Speler speelde met zwart
                    speler.addTegenstander(wedstrijd.getWit().getInitialen());

                    speler.setWitvoorkeur(speler.getWitvoorkeur() + 1.1);

                } else {
                    System.out.println("Hmmm, speler niet gevonden....");
                }
            }
        }
        return groep;
    }

    /**
     * Maak trio wedstrijden voor betreffende groep
     * @param groep Groep
     * @return
     */
    protected ArrayList<Integer> bepaalTrioSpelers(Groep groep) {
        ArrayList<Integer> trio = new ArrayList<>();
        if (groep.getSpelers().size() == 3) {
        	// 3 spelers, dus maak gelijk trio
            trio.add(groep.getSpelers().get(0).getId());
            trio.add(groep.getSpelers().get(1).getId());
            trio.add(groep.getSpelers().get(2).getId());
            return trio;
        }
        int spelerID = IJCController.c().getBeginpuntTrio(groep.getSpelers().size());
        int minDelta = 1;
        int plusDelta = 1;
        int ignore = 0;
        boolean doorzoeken = true;
        while (doorzoeken) {
            Speler s1 = groep.getSpelerByID(spelerID);
            Speler s2 = groep.getSpelerByID(spelerID - minDelta);
            Speler s3 = groep.getSpelerByID(spelerID + plusDelta);
            if (isGoedTrio(s1, s2, s3, ignore)) {
                trio.add(s1.getId());
                trio.add(s2.getId());
                trio.add(s3.getId());
                return trio;
            } else {
                if ((s2 == null) || (s3 == null)) {
                    if (ignore > 4) {
                        doorzoeken = false;
                    }
                    ignore += 1;
                    minDelta = 1;
                    plusDelta = 1;
                } else {
                    if (minDelta > plusDelta) {
                        plusDelta++;
                    } else {
                        minDelta++;
                    }
                }
            }
        }
        return trio;
    }

    /**
     * Stel vast op het meegegeven trio een goed trio is conform
     * de regels.
     * @param s1 Speler 1
     * @param s2 Speler 2
     * @param s3 Speler 3
     * @param ignore Aantal te negeren rondes in het verleden
     * @return
     */
    protected boolean isGoedTrio(Speler s1, Speler s2, Speler s3, int ignore) {
        if ((s1 != null) && (s2 != null) && (s3 != null)) {
            return !s1.isGespeeldTegen(s2, ignore) && !s1.isGespeeldTegen(s3, ignore) && !s2.isGespeeldTegen(s1, ignore)
                    && !s2.isGespeeldTegen(s3, ignore) && !s3.isGespeeldTegen(s1, ignore)
                    && !s3.isGespeeldTegen(s2, ignore);
        } else {
            return false;
        }
    }
}
