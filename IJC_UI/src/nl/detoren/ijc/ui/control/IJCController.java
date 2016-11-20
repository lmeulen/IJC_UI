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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import nl.detoren.ijc.Configuratie;
import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.groepen.Groepen;
import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.data.wedstrijden.Groepswedstrijden;
import nl.detoren.ijc.data.wedstrijden.Serie;
import nl.detoren.ijc.data.wedstrijden.Wedstrijd;
import nl.detoren.ijc.data.wedstrijden.Wedstrijden;
import nl.detoren.ijc.io.GroepenReader;
import nl.detoren.ijc.io.ImportSpelers;
import nl.detoren.ijc.io.OutputExcel;
import nl.detoren.ijc.io.OutputIntekenlijst;
import nl.detoren.ijc.io.OutputKEI;
import nl.detoren.ijc.io.OutputKNSB;
import nl.detoren.ijc.io.OutputNeuralData;
import nl.detoren.ijc.io.OutputOSBO;
import nl.detoren.ijc.io.OutputSpeelschema;
import nl.detoren.ijc.io.OutputStanden;
import nl.detoren.ijc.io.OutputUitslagen;
import nl.detoren.ijc.neural.NeuralHelper;
import nl.detoren.ijc.neural.Voorspeller;

/**
 * Main controller class voor afhandeling van de groepen en wedstrijden
 */
public class IJCController {

    private static volatile IJCController instance = null;

    private final static Logger logger = Logger.getLogger(IJCController.class.getName());

    private static final String defaultInputfile = "uitslag.txt";

    private class Status {
        private boolean automatisch = true;
        private Groepen groepen;
        private Groepen wedstrijdgroepen;
        private Wedstrijden wedstrijden;
        private Groepen resultaatVerwerkt;
        private ArrayList<Speler> externGespeeld;
    }
    private Status status;
    private Configuratie c;

    private String laatsteExport;

    protected IJCController() {
    	status = new Status();
    	status.groepen = null;
    	status.wedstrijden = null;
    	status.wedstrijdgroepen = null;
    	status.externGespeeld = null;
    	c = new Configuratie();
    }

    public static IJCController getInstance() {
        if (instance == null) {
            instance = new IJCController();
        }
        return instance;
    }

    /**
     * Alias voor getInstance, korter voor beter leesbare code
     * @return
     */
    public static IJCController getI() {
    	return getInstance();
    }

    /**
     * Snelle methode om tot de configuratie te komen
     * @return
     */
    public static Configuratie c() {
    	return getInstance().c;
    }

    public boolean isAutomatisch() {
        return status.automatisch;
    }

    public void setAutomatisch(boolean automatisch) {
        this.status.automatisch = automatisch;
    }

    public void leesGroepen() {
        synchronized (this) {
            leesGroepen(defaultInputfile);
        }
    }
    
    public Status getStatus() {
    	return status;
    }

    /**
     * Lees groepen bestand in
     * @param bestandsnaam
     */
	public void leesGroepen(String bestandsnaam) {
		synchronized (this) {
			status.groepen = null;
			try {
				if (bestandsnaam.endsWith(".txt")) {
					logger.log(Level.INFO, "Lees groepen in TXT formaat uit " + bestandsnaam);
					status.groepen = new GroepenReader().leesGroepen(bestandsnaam);
				} else if (bestandsnaam.endsWith(".json")) {
					logger.log(Level.INFO, "Lees groepen in JSON uit bestand " + bestandsnaam);
					status.groepen = new GroepenReader().leesGroepenJSON(bestandsnaam);
					// check for old groepen in status.
					for (int i=c.aantalGroepen; i<11 ; i++) {
						status.groepen.removeGroep(status.groepen.getGroepById(i));
					}
				} else {
					return;
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, "Geens statusbestand gelezen");
				status.groepen = null;
			}
			if (status.groepen == null) {
				status.groepen = new Groepen();
				for (int i = 0; i < c.aantalGroepen; ++i)
					status.groepen.addGroep(new Groep(i));
				status.groepen.setPeriode(1);
				status.groepen.setRonde(1);
			}

			status.wedstrijdgroepen = new Groepen();
			status.wedstrijden = new Wedstrijden();
			status.resultaatVerwerkt = new Groepen();
			status.externGespeeld = new ArrayList<>();
			if (status.groepen.getRonde() == 1)
				resetAanwezigheidspunt();
		}
	}

	/**
	 * Zet voor alle spelers het aanwezigheidspunt op onwaar.
	 */
	private void resetAanwezigheidspunt() {
    	logger.log(Level.INFO, "Eerste ronde van een periode; reset aanwezigheidspunt");
		for (Groep groep : status.groepen.getGroepen()) {
			for (Speler s : groep.getSpelers()) {
				s.setAanwezig(false);
			}
		}

	}

	/**
	 * Lees het status export bestand
	 * @return true, als bestand gevonden en ingelezen
	 */
	public boolean leesStatusBestand() {
		synchronized (this) {
        	logger.log(Level.INFO, "Lees status");
        	leesStatus();
        	leesConfiguratie();
			if ((status == null) || (status.groepen == null)) {
				status = new Status();
				status.groepen = null;
				status.wedstrijdgroepen = null;
				status.wedstrijden = null;
				status.resultaatVerwerkt = null;
	        	logger.log(Level.INFO, "Status bestand niet ingelezen");
				return false;
			}
		}
    	logger.log(Level.INFO, "Statusbestand ingelezen");
		return true;
	}


    /**
     * Groepen zoals ingelezen met aanwezigheid bijgewerkt.
     *
     * @return
     */
    public Groepen getGroepen() {
        return status.groepen;
    }

    public Groep getGroepByID(int id) {
        return status.groepen.getGroepById(id);
    }

    public int getAantalGroepen() {
        return status.groepen.getAantalGroepen();
    }

    public Groepen getWedstrijdgroepen() {
        return status.wedstrijdgroepen;
    }

    public Groep getWedstrijdGroepByID(int id) {
        return status.wedstrijdgroepen.getGroepById(id);
    }

    public Groep getResultaatGroepByID(int id) {
    	if (status.resultaatVerwerkt != null) {
    		return status.resultaatVerwerkt.getGroepById(id);
    	} else {
    		return null;
    	}
    }

    public int getAantalWedstrijdGroepen() {
        return status.wedstrijdgroepen.getAantalGroepen();
    }

    public Wedstrijden getWedstrijden() {
        return status.wedstrijden;
    }

    public void setWedstrijden(Wedstrijden wedstrijden) {
    	status.wedstrijden = wedstrijden;
    }

    /**
     * Zet de groepen van aanwezige spelers om naar de wedstrijdgroepen. In de
     * wedstrijdgroepen zitten alleen de aanwezige spelers aangevuld met de
     * eventuele doorschuivers.
     * Deze wedstrijdgroepen worden vervoglens gebruikt om de wedstrijden in te delen
     */
    public void maakGroepsindeling() {
        synchronized (this) {
        	logger.log(Level.INFO, "Maak groepsindeling");
        	status.wedstrijdgroepen = getIndeler().maakGroepsindeling(status.groepen);
            if (status.automatisch) {
                maakWedstrijden();
            }
        }
    }

    /**
     * Maak de groepsindeling voor een wedstrijdgroep voor alleen de geselecteerde
     * groep. Indelingen andere groepen blijven ongewijzigd
     *
     */
    public void maakGroepsindeling(int groepID) {
    	//synchronized (this) {
        	logger.log(Level.INFO, "Maak groepsindeling voor groep " + groepID);
    		status.wedstrijdgroepen = getIndeler().maakGroepsindeling(status.groepen, status.wedstrijdgroepen, groepID);
		//}
    }
    /**
     * Bepaal de te spelen wedstrijden op basis van de wedstrijdgroepen.
     */
    public void maakWedstrijden() {
        synchronized (this) {
        	logger.log(Level.INFO, "Maak wedstrijden voor alle groepen");
        	status.wedstrijden = getIndeler().maakWedstrijdschema(status.wedstrijdgroepen);
            printWedstrijden();
        }
    }

    /**
     * Bepaal de te spelen wedstrijden op basis van de wedstrijdgroepen.
     */
    public void maakWedstrijden(int groepID) {
        synchronized (this) {
        	logger.log(Level.INFO, "Maak wedstrijden voor groep " + groepID);
        	status.wedstrijden = getIndeler().updateWedstrijdschema(status.wedstrijden, status.wedstrijdgroepen, groepID);
            printWedstrijden(groepID);
        }
    }

    /**
     * Print wedstrijden op het scherm, opgedeeld per serie
     */
    public void printWedstrijden() {
        System.out.print("\nWedstrijden Periode " + status.wedstrijden.getPeriode());
        System.out.println(" Ronde " + status.wedstrijden.getRonde() + "\n-----------");
        for (Groepswedstrijden gw : status.wedstrijden.getGroepswedstrijden()) {
        	printGroepsWedstrijden(gw);
        }
    }

    /**
     * Print wedstrijden voor ��n groep op het scherm, opgedeeld per serie
     */
	public void printWedstrijden(int groep) {
		logger.log(Level.INFO, "Print wedstrijden");
		System.out.print("\nWedstrijden Periode " + status.wedstrijden.getPeriode());
		System.out.println(" Ronde " + status.wedstrijden.getRonde() + "\n-----------");
		Groepswedstrijden gw = status.wedstrijden.getGroepswedstrijdenNiveau(groep);
		printGroepsWedstrijden(gw);
	}

	/**
	 * Print wedstrijden voor gespecificeerde Groepswedstrijden
	 * @param gw
	 */
	public void printGroepsWedstrijden(Groepswedstrijden gw) {
		System.out.println("  " + Groep.geefNaam(gw.getNiveau()));
		int i = 1;
		int distance = 0;
		for (Serie serie : gw.getSeries()) {
			System.out.println("    Serie " + i);
			for (Wedstrijd w : serie.getWedstrijden()) {
				System.out.println("      " + w.toString());
				distance += w.getDistance();
			}
			++i;
		}
		if (!gw.getTriowedstrijden().isEmpty()) {
			System.out.println("    Trio");
			for (Wedstrijd w : gw.getTriowedstrijden()) {
				System.out.println("      " + w.toString());
				distance += w.getDistance();
			}

		}
		System.out.println("    Totale afstand : " + distance);
	}

    /**
     * Meld een speler aan/afwezig
     * @param groep Betreffende groep
     * @param index Betreffende speler
     * @param waarde true, als aanwezig melden
     */
    public void setSpelerAanwezigheid(Groep groep, int index, final boolean waarde) {
        synchronized (this) {
            if (groep != null) {
                Speler s = groep.getSpelers().get(index);
                if (s != null) {
                	logger.log(Level.INFO, "Speler " + index + " in groep " + groep.getNaam() + " is " + (waarde ? "niet aanwezig" : "aanwezig"));
                    s.setAanwezig(waarde);
                    if (status.automatisch) {
                        maakGroepsindeling();
                    }
                }
            }
        }

    }

    /**
     * Voeg een speler toe aan een groep
     * @param groepID Groep waaraan toe te voegen
     * @param sp Gedefinieerde speler die toegevoegd moet worden
     * @param locatie Locatie in de tabel waar toe te voegen
     */
    public void addSpeler(int groepID, Speler sp, int locatie) {
    	logger.log(Level.INFO, "Voeg speler " + sp.getInitialen() + " toe aan groep " + groepID + ", locatie " + locatie);
        Groep gr = status.groepen.getGroepById(groepID);
        if (locatie > 0) {
        	gr.addSpeler(sp, locatie);
        } else {
        	gr.addSpeler(sp);
        }
        if (status.automatisch) {
            maakGroepsindeling();
        }
    }

    public void verwijderSpeler(int groepID, Speler sp, int locatie) {
    	logger.log(Level.INFO, "Verwijder speler " + sp.getInitialen() + " uit groep " + groepID + ", locatie " + locatie);
        Groep gr = status.groepen.getGroepById(groepID);
        gr.removeSpeler(sp, locatie);
        if (status.automatisch) {
            maakGroepsindeling();
        }
    }

    public void verwijderWedstrijdSpeler(int groepID, Speler sp, int locatie) {
    	logger.log(Level.INFO, "Verwijder speler " + sp.getInitialen() + " uit wedstrijdgroep " + groepID + ", locatie " + locatie);
        Groep gr = status.wedstrijdgroepen.getGroepById(groepID);
        gr.removeSpeler(sp, locatie);
        gr.renumber();
        if (status.automatisch) {
            maakGroepsindeling();
        }
    }
    /**
     * Verwerk uitslagen tot een nieuwe stand en sla deze op
     * in de verschillende bestanden
     */
    public void verwerkUitslagen() {
    	logger.log(Level.INFO, "Verwerk uitslagen");
    	Uitslagverwerker uv = new Uitslagverwerker();
    	status.resultaatVerwerkt =  uv.verwerkUitslag(status.groepen, status.wedstrijden, status.externGespeeld);
    	status.resultaatVerwerkt.sorteerGroepen();
    	System.out.println(status.resultaatVerwerkt.toPrintableString());
    	logger.log(Level.INFO, "en sla uitslagen en status op");
    	new OutputStanden().export(status.resultaatVerwerkt);
    	new OutputUitslagen().export(status.wedstrijden);
    	if (c.exportKNSBRating) new OutputKNSB().export(status.wedstrijden);
    	if (c.exportOSBORating) new OutputOSBO().export(status.wedstrijden);
    	if (c.exportKEIlijst) new OutputKEI().export(status.resultaatVerwerkt);
    	if (c.exportIntekenlijst) new OutputIntekenlijst().export(status.resultaatVerwerkt);

    	new OutputNeuralData().export(status.wedstrijden);

    	saveState(true, "uitslag");
    }

	/**
     * Save state of the application to disk
	 * @param unique if true, a unique file is created with timestamp in filename
	 * @param postfix post fix of filename, before extension. Only used in combination with unique = true
	 */
    public void saveState(boolean unique, String postfix) {
		try {
			String bestandsnaam = c.statusBestand + ".json";
			logger.log(Level.INFO, "Sla status op in bestand " + bestandsnaam);
			Gson gson = new Gson();
			String jsonString = gson.toJson(status);
			// write converted json data to a file
			FileWriter writer = new FileWriter(bestandsnaam);
			writer.write(jsonString);
			writer.close();

			if (c.saveAdditionalStates && unique) {
				String s = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
				bestandsnaam = c.statusBestand + s + "-" + postfix + ".json";
				logger.log(Level.INFO, "Sla status op in bestand " + bestandsnaam);
				// write converted json data to a file
				writer = new FileWriter(bestandsnaam);
				writer.write(jsonString);
				writer.close();
			}
			bestandsnaam = c.configuratieBestand + ".json";
			logger.log(Level.INFO, "Sla configuratie op in bestand " + bestandsnaam);
			// write converted json data to a file
			writer = new FileWriter(bestandsnaam);
			jsonString = gson.toJson(c);
			writer.write(jsonString);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean leesStatus() {
		return leesStatus(c.statusBestand + ".json");
	}

	public boolean leesStatus(String bestandsnaam) {
		try {
	    	logger.log(Level.INFO, "Lees status uit bestand " + bestandsnaam);
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(bestandsnaam));
			Status nieuw = gson.fromJson(br, Status.class);
			status = nieuw;	// assure excpetion is thrown when things go wrong
			return true;
		} catch (Exception e) {
			// Could not read status
			return false;
		}
	}


	public void leesBestand(String bestandsnaam) {
		if (!leesStatus(bestandsnaam))
			leesGroepen(bestandsnaam);

	}


	public void leesConfiguratie() {
		try {
			String bestandsnaam = c.configuratieBestand + ".json";
	    	logger.log(Level.INFO, "Lees configuratie uit bestand " + bestandsnaam);
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(bestandsnaam));
			c = gson.fromJson(br, Configuratie.class);
			if (c == null) c = new Configuratie();
		} catch (IOException e) {
			// Could not read status
		}
	}

    public void exportWedstrijdschema() {
    	logger.log(Level.INFO, "Creeer bestanden met wedstrijden");
    	new OutputExcel().export(status.wedstrijden);
    	new OutputSpeelschema().export(status.wedstrijden);
        voorspelUitslagen();
    }

    public ArrayList<Speler> getExterneSpelers() {
    	return status.externGespeeld;
    }

    /**
     * Voeg externe speler toe
     * @param naam Naam of initialen
     * @return De toegeveogde speler
     */
    public Speler addExterneSpeler(String naam) {
    	if (status.externGespeeld == null) status.externGespeeld = new ArrayList<>();
    	if (naam != null && naam.length() == 2) {
    		Speler s = getSpelerOpInitialen(naam);
    		if (s != null) status.externGespeeld.add(s);
    		return s;
    	} else if (naam != null && naam.length() > 2) {
    		Speler s = getSpelerOpNaam(naam);
    		if (s != null) status.externGespeeld.add(s);
    		return s;
    	} else {
    		// ongeldige naam
    		return null;
    	}
    }

    /**
     * Wis lijst met exterene spelers
     */
    public void wisExterneSpelers() {
    	status.externGespeeld = new ArrayList<>();
    }

    /**
     * Vind speler in alle groepen op naam
     * @param naam
     * @return
     */
    public Speler getSpelerOpNaam(String naam) {
    	if (status.groepen != null) {
    		for (Groep groep : status.groepen.getGroepen()) {
    			for (Speler speler : groep.getSpelers()) {
    				if (speler.getNaam().equals(naam)) {
    					return speler;
    				}
    			}
    		}
    	}
    	return null;
    }

    /**
     * Vind speler in alle groepen op initialen
     * @param naam
     * @return
     */
    public Speler getSpelerOpInitialen(String naam) {
    	if (status.groepen != null) {
    		for (Groep groep : status.groepen.getGroepen()) {
    			for (Speler speler : groep.getSpelers()) {
    				if (speler.getInitialen().equals(naam)) {
    					return speler;
    				}
    			}
    		}
    	}
    	return null;
    }

    /**
     * Ga over naar de volgende ronde.
     * Uitslag van deze ronde wordt de start van de volgende
     */
    public void volgendeRonde() {
    	if (status.resultaatVerwerkt != null) {
    		status.groepen = status.resultaatVerwerkt;
			status.wedstrijdgroepen = null;
			status.wedstrijden = null;
			status.resultaatVerwerkt = null;
			status.externGespeeld = null;

			int ronde = status.groepen.getRonde();
			int periode = status.groepen.getPeriode();
			ronde += 1;
	        if (ronde > c.rondes) {
	        	ronde = 1;
	        	periode++;
	        	if (periode > c.perioden) periode = 1;
	        	resetPunten();
	        }
	        status.groepen.setRonde(ronde);
	        status.groepen.setPeriode(periode);
			if (ronde == 1)
				resetAanwezigheidspunt();
			// Iedereen aanwezig zetten
			for (Groep groep : status.groepen.getGroepen()) {
				for (Speler s : groep.getSpelers()) {
					s.setAanwezig(true);
				}
			}
			setAutomatisch(true);
			maakGroepsindeling();
    	}
    }

    /**
     * Alle spelers in betreffende groep aan/afwezig.
     * Groep wordt inverse status van max
     */
    public void setAlleSpelersAanwezigheid(int groepID) {
    	Groep groep = getGroepByID(groepID);
    	if (groep != null) {
    		int aan = 0;
    		int af = 0;
    		for (Speler s : groep.getSpelers()) {
    			if (s.isAanwezig()) aan++; else af++;
    		}
    		boolean newstatus = (aan >= af) ? false : true;
    		for (Speler s : groep.getSpelers()) {
    			s.setAanwezig(newstatus);
    		}
    	}
    }

    /**
     * Speler naar hogere groep verplaatsen
     * @param groepID Huidige groep
     * @param speler Speler
     * @param locatie huidige locatie in huidige groep
     */
	public void spelerNaarHogereGroep(int groepID, Speler speler, int locatie) {
		Groep huidigeGroep = status.wedstrijdgroepen.getGroepById(groepID);
		Groep hogereGroep = status.wedstrijdgroepen.getGroepById(groepID+1);
		if (huidigeGroep == null || hogereGroep == null) return;
		logger.log(Level.INFO, "Verplaats " + speler.getNaam() + " van " + huidigeGroep.getNaam() + " naar " + hogereGroep.getNaam());
		hogereGroep.addSpeler(speler);
		hogereGroep.renumber();
		huidigeGroep.removeSpeler(speler, locatie);
		huidigeGroep.renumber();
	}

    /**
     * Speler naar lagere groep verplaatsen in wedstrijdgroepen
     * @param groepID Huidige groep
     * @param speler Speler
     * @param locatie huidige locatie in huidige groep
     */
	public void spelerNaarLagereGroep(int groepID, Speler speler, int locatie) {
		Groep huidigeGroep = status.wedstrijdgroepen.getGroepById(groepID);
		Groep lagereGroep = status.wedstrijdgroepen.getGroepById(groepID-1);
		if (huidigeGroep == null || lagereGroep == null) return;
		logger.log(Level.INFO, "Verplaats " + speler.getNaam() + " van " + huidigeGroep.getNaam() + " naar " + lagereGroep.getNaam());
		lagereGroep.addSpeler(speler, 0); //vooraan plaatsen
		lagereGroep.renumber();
		huidigeGroep.removeSpeler(speler, locatie);
		lagereGroep.renumber();
	}

	public String getLaatsteExport() {
		return laatsteExport;
	}

	public void setLaatsteExport(String laatsteExport) {
		this.laatsteExport = laatsteExport;
	}

	/**
	 * Sorteer in aanwezigheidsgroep op rating
	 * @param groepID
	 */
	public void sorteerGroepOpRating(int groepID) {
		Groep groep = status.groepen.getGroepById(groepID);
		if (groep != null) {
			groep.sorteerRating();
			groep.renumber();
		}
	}

	/**
	 * Sorteer in aanwezigheidsgroep op Punten
	 * @param groepID
	 */
	public void sorteerGroepOpPunten(int groepID) {
		Groep groep = status.groepen.getGroepById(groepID);
		if (groep != null) {
			groep.sorteerPunten();
			groep.renumber();
		}
	}

	/**
	 * Reset de punten in alle aanwezigheidsgroepen
	 */
	public void resetPunten() {
		status.groepen.resetPunten();
	}

	/**
	 * Schuif speler door
	 * @param groepID Groep ID van huidige groep
	 * @param spelerID speler ID
	 */
	public void doorschuiven(int groepID, int spelerID) {
		Groep huidigeGroep = status.groepen.getGroepById(groepID);
		Groep hogereGroep = status.groepen.getGroepById(groepID+1);
		if (huidigeGroep != null && hogereGroep != null) {
			Speler s = huidigeGroep.getSpelerByID(spelerID+1);
			huidigeGroep.removeSpeler(s, spelerID);
			Speler nieuweSpeler = new Speler(s);
			nieuweSpeler.setPunten(IJCController.c().startPunten[groepID+1]);
			hogereGroep.addSpeler(nieuweSpeler);
			huidigeGroep.renumber();
			hogereGroep.renumber();
			logger.log(Level.INFO, "Speler " + s.getNaam() + " doorgeschoven naar groep " + Groep.geefNaam(groepID+1));
		}
	}

	/**
	 * Schuif speler terug naar een groep lager
	 * @param groepID Groep ID van huidige groep
	 * @param spelerID speler ID
	 */
	public void terugschuiven(int groepID, int spelerID) {
		Groep huidigeGroep = status.groepen.getGroepById(groepID);
		Groep lagereGroep = status.groepen.getGroepById(groepID-1);
		if (huidigeGroep != null && lagereGroep != null) {
			Speler s = huidigeGroep.getSpelerByID(spelerID+1);
			huidigeGroep.removeSpeler(s, spelerID);
			Speler nieuweSpeler = new Speler(s);
			nieuweSpeler.setPunten(IJCController.c().startPunten[groepID-1]);
			lagereGroep.addSpeler(nieuweSpeler);
			huidigeGroep.renumber();
			lagereGroep.renumber();
			logger.log(Level.INFO, "Speler " + s.getNaam() + " teruggeschoven naar groep " + Groep.geefNaam(groepID+1));
		}
	}

	/**
	 * Geen een groepen indeler. Type is afhankelijk van de settings
	 * in Configuratie. Dynamisch wisselen tussen indelers is mogelijk.
	 * @return
	 */
	public GroepenIndelerInterface getIndeler() {
		return new GroepenIndelerFactory().getIndeler();
	}

	public void importeerSpelers(String absolutePath) {
		new ImportSpelers().importeerSpelers(absolutePath);
		status.groepen.hernummerGroepen();

	}

	public void start() {
        if (!leesStatusBestand()) leesGroepen();
        if (isAutomatisch()) maakGroepsindeling();
	}

	public void voorspelUitslagen() {
		logger.log(Level.INFO, "Voorspel uitslagen");
		Voorspeller v = new Voorspeller();
		v.initialiseer();
		String bestand =  "R" + status.wedstrijden.getPeriode() + "-" + status.wedstrijden.getRonde() + ".arff";
		new OutputNeuralData().export(status.wedstrijden, bestand);
		try {
			v.voorspel(bestand);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
