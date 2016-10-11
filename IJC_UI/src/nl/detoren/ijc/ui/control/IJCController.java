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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.groepen.Groepen;
import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.data.wedstrijden.Groepswedstrijden;
import nl.detoren.ijc.data.wedstrijden.Serie;
import nl.detoren.ijc.data.wedstrijden.Wedstrijd;
import nl.detoren.ijc.data.wedstrijden.Wedstrijden;
import nl.detoren.ijc.io.GroepenReader;
import nl.detoren.ijc.io.OutputExcel;
import nl.detoren.ijc.io.OutputKEI;
import nl.detoren.ijc.io.OutputKNSB;
import nl.detoren.ijc.io.OutputTekst;

/**
 * Main controller class voor afhandeling van de groepen en wedstrijden
 * @author Leo van der Meulen
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


    protected IJCController() {
    	status = new Status();
    	status.groepen = null;
    	status.wedstrijden = null;
    	status.wedstrijdgroepen = null;
    	status.externGespeeld = null;

    }

    public static IJCController getInstance() {
        if (instance == null) {
        	logger.log(Level.INFO, "message to log");
            instance = new IJCController();
        }
        return instance;
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

    /**
     * Lees groepen bestand in
     * @param bestandsnaam
     */
	public void leesGroepen(String bestandsnaam) {
		synchronized (this) {
			status.groepen = null;
			if (bestandsnaam.endsWith(".txt")) {
				logger.log(Level.INFO, "Lees groepen in TXT formaat uit " + bestandsnaam);
				status.groepen = new GroepenReader().leesGroepen(bestandsnaam);
			} else if (bestandsnaam.endsWith(".json")) {
				logger.log(Level.INFO, "Lees groepen in JSON uit bestand " + bestandsnaam);
				status.groepen = new GroepenReader().leesGroepenJSON(bestandsnaam);
			}
			status.wedstrijdgroepen = null;
			status.wedstrijden = null;
			status.resultaatVerwerkt = null;
			status.externGespeeld = null;
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
			if ((status == null) || (status.groepen == null)) {
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

    public int getAantalWedstrijdGroepen() {
        return status.wedstrijdgroepen.getAantalGroepen();
    }

    public Wedstrijden getWedstrijden() {
        return status.wedstrijden;
    }

    public void setWedstrijden(Wedstrijden w) {
    	status.wedstrijden = w;
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
        	status.wedstrijdgroepen = new GroepenIndeler().maakGroepsindeling(status.groepen);
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
    	synchronized (this) {
        	logger.log(Level.INFO, "Maak groepsindeling voor groep " + groepID);
    		status.wedstrijdgroepen = new GroepenIndeler().maakGroepsindeling(status.groepen, status.wedstrijdgroepen, groepID);
		}
    }
    /**
     * Bepaal de te spelen wedstrijden op basis van de wedstrijdgroepen.
     */
    public void maakWedstrijden() {
        synchronized (this) {
        	logger.log(Level.INFO, "Maak wedstrijden voor alle groepen");
        	status.wedstrijden = new GroepenIndeler().maakWedstrijdschema(status.wedstrijdgroepen);
            printWedstrijden();
        }
    }

    /**
     * Bepaal de te spelen wedstrijden op basis van de wedstrijdgroepen.
     */
    public void maakWedstrijden(int groepID) {
        synchronized (this) {
        	logger.log(Level.INFO, "Maak wedstrijden voor groep " + groepID);
        	status.wedstrijden = new GroepenIndeler().updateWedstrijdschema(status.wedstrijden, status.wedstrijdgroepen, groepID);
            printWedstrijden();
        }
    }

    /**
     * Print wedstrijden op het scherm, opgedeeld per serie
     */
    public void printWedstrijden() {
    	logger.log(Level.INFO, "Print wedstrijden");
        System.out.print("\nWedstrijden Periode " + status.wedstrijden.getPeriode());
        System.out.println(" Ronde " + status.wedstrijden.getRonde() + "\n-----------");
        for (Groepswedstrijden gw : status.wedstrijden.getGroepswedstrijden()) {
            System.out.println("  " + Groep.geefNaam(gw.getNiveau()));
            int i = 1;
            for (Serie serie : gw.getSeries()) {
                System.out.println("    Serie " + i);
                for (Wedstrijd w : serie.getWedstrijden()) {
                    System.out.println("      " + w.toString());
                }
                ++i;
            }
            if (!gw.getTriowedstrijden().isEmpty()) {
                System.out.println("    Trio");
                for (Wedstrijd w : gw.getTriowedstrijden()) {
                    System.out.println("      " + w.toString());
                }

            }
        }
    }

    /**
     * Meld een speler aan/afwezig 
     * @param groep Betreffende groep
     * @param index Betreffende speler
     * @param waarde true, als aanwezig melden
     */
    public void setSpelerAanwezigheid(Groep groep, int index, boolean waarde) {
        synchronized (this) {
        	logger.log(Level.INFO, "Speler " + index + " in groep " + groep.getNaam() + " is " + (waarde ? "niet aanwezig" : "aanwezig"));
            if (groep != null) {
                Speler s = groep.getSpelers().get(index);
                if (s != null) {
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
     * @param s Gedefinieerde speler die toegevoegd moet worden
     * @param locatie Locatie in de tabel waar toe te voegen
     */
    public void addSpeler(int groepID, Speler s, int locatie) {
    	logger.log(Level.INFO, "Voeg speler " + s.getInitialen() + " toe aan groep " + groepID + ", locatie " + locatie);
        Groep gr = status.groepen.getGroepById(groepID);
        gr.addSpeler(s, locatie);
        if (status.automatisch) {
            maakGroepsindeling();
        }
    }
    
    public void verwijderSpeler(int groepID, Speler s, int locatie) {
    	logger.log(Level.INFO, "Verwijder speler " + s.getInitialen() + " uit groep " + groepID + ", locatie " + locatie);
        Groep gr = status.groepen.getGroepById(groepID);
        gr.removeSpeler(s, locatie);
        if (status.automatisch) {
            maakGroepsindeling();
        }
    }

    public void verwijderWedstrijdSpeler(int groepID, Speler s, int locatie) {
    	logger.log(Level.INFO, "Verwijder speler " + s.getInitialen() + " uit wedstrijdgroep " + groepID + ", locatie " + locatie);
        Groep gr = status.wedstrijdgroepen.getGroepById(groepID);
        gr.removeSpeler(s, locatie);
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
    	new OutputTekst().saveUitslag(status.resultaatVerwerkt);
    	new OutputKNSB().saveUitslag(status.wedstrijden);
    	new OutputKEI().exportKEIlijst(status.resultaatVerwerkt);
    	saveState(true, "uitslag");
    }
    
    /**
     * Save state of the application to disk
     * @param  unique if true, a unique file is created with timestamp in filename
     * @param post-fix, post fix of filename, before extension. Only used in combination with unique = true
     */
	public void saveState(boolean unique, String postfix) {
		try {
			String bestandsnaam = "status.json";
			logger.log(Level.INFO, "Sla status op in bestand " + bestandsnaam);
			Gson gson = new Gson();
			String jsonString = gson.toJson(status);
			// write converted json data to a file
			FileWriter writer = new FileWriter(bestandsnaam);
			writer.write(jsonString);
			writer.close();

			if (unique) {
				String s = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
				bestandsnaam = "status" + s + "-" + postfix + ".json";
				logger.log(Level.INFO, "Sla status op in bestand " + bestandsnaam);
				// write converted json data to a file
				writer = new FileWriter(bestandsnaam);
				writer.write(jsonString);
				writer.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void leesStatus() {
		try {
			String bestandsnaam = "status.json";
	    	logger.log(Level.INFO, "Lees status uit bestand " + bestandsnaam);
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(bestandsnaam));
			status = gson.fromJson(br, Status.class);
		} catch (IOException e) {
			// Could not read status
		}
	}
    
    public void exportToExcel() {
    	logger.log(Level.INFO, "Creeer Excel bestand met wedstrijden");
    	OutputExcel oe = new OutputExcel();
    	oe.updateExcel(status.wedstrijden);
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
}
