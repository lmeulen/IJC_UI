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
package nl.detoren.ijc.data.groepen;

import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Dit object bevat de gegevens van één speler
 * 
 * @author Leo van der Meulen
 */
public class Speler implements Cloneable {

    private int id;
    private String naam;
    private String initialen;
    private double witvoorkeur;			// > 0 is witvoorkeur, kleinder dan 0 zwart
    private int groep;
    private int rating;
    private String[] tegenstanders;
    private int punten;
    private boolean afwezigheidspunt;
    private boolean aanwezig;
    private int KNSBnummer;
    private int keipunten;
    private int keikansen;
    private String speelgeschiedenis;
    
    private static final DecimalFormat decimalFormat = new DecimalFormat("#");

    public Speler() {
        this(0, "", "", 0, 0, 0, new String[4], 0, false, true, 1234567, 0, 0, "");
    }

    public Speler(Speler speler) {
        this.id = speler.id;
        this.naam = speler.naam;
        this.initialen = speler.initialen;
        this.witvoorkeur = speler.witvoorkeur;
        this.groep = speler.groep;
        this.rating = speler.rating;
        this.tegenstanders = speler.tegenstanders.clone();
        this.punten = speler.punten;
        this.afwezigheidspunt = speler.afwezigheidspunt;
        this.aanwezig = speler.aanwezig;
        this.KNSBnummer = speler.KNSBnummer;
        this.keipunten = speler.keipunten;
        this.keikansen = speler.keikansen;
        this.speelgeschiedenis = speler.speelgeschiedenis;
    }

    public Speler(int id, String naam, String initialen, int witvk, int groep, int rating, String[] tgs, int punten,
            boolean ap, boolean aanw, int knsbnr, int keipunten, int keikansen, String geschiedenis) {
        this.id = id;
        this.naam = naam.trim();
        this.initialen = initialen;
        this.witvoorkeur = witvk;
        this.groep = groep;
        this.rating = rating;
        this.tegenstanders = tgs;
        this.punten = punten;
        this.afwezigheidspunt = ap;
        this.aanwezig = aanw;
        setKNSBnummer(knsbnr);
        this.keipunten = keipunten;
        this.keikansen = keikansen;
        this.speelgeschiedenis = geschiedenis;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam.trim();
    }

    public String getInitialen() {
        return initialen;
    }

    public void setInitialen(String init) {
        initialen = init;
    }

    public double getWitvoorkeur() {
        return witvoorkeur;
    }

    public void setWitvoorkeur(double witvoorkeur) {
        this.witvoorkeur = witvoorkeur;
    }

    public int getGroep() {
        return groep;
    }

    public void setGroep(int groep) {
        this.groep = groep;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String[] getTegenstanders() {
        return tegenstanders;
    }

    /**
     * Is er eerder tegen meegegeven speler gespeeld en hoeveel partijen geleden?
     *
     * @param speler2 Speler die gecontroleerd word
     * @return 0 als er niet in één van de laatste vier wedstrijden tegen deze speler gespeeld is,
     *         anders 1 voor vorige partij, 2 voor de partij daarvoor, etc
     */
    public int[] getGespeeldTegen(Speler speler2) {
    	int ronde[] = new int [4];
        String ini = speler2.getInitialen();
        // ff checken dat het niet de speler zelf is ;-)
        if (initialen.equals(ini)) {
        	ronde[0]=0;
        	return ronde;
        }
        for (int i = 0; i < 4; i++) {
            if (tegenstanders[i].substring(0, 2).equals(ini)) {
            	ronde[i]=4-i;
            }
        }
        return ronde;
    }
    
    public String getTegenstandersString() {
        return tegenstanders[0] + tegenstanders[1] + tegenstanders[2] + tegenstanders[3];
    }

    public void setTegenstanders(String[] tegenstanders) {
        this.tegenstanders = tegenstanders.clone();
    }

    public int getPunten() {
        return punten;
    }

    public void setPunten(int punten) {
        this.punten = punten;
    }

    public int getKNSBnummer() {
		return KNSBnummer;
	}

    /**
     * Zet het KNSBnummer. Als deze 1234567 of 0 is, oftewel
     * onbekend dat wordt deze gevuld als primitieve hash op
     * basis van de naam
     * @param nieuwKNSBnr
     */
	public void setKNSBnummer(int nieuwKNSBnr) {
		if ((nieuwKNSBnr == 1234567) && (naam.length() > 3)) {
			KNSBnummer = hashCode();
		} else {
			KNSBnummer = nieuwKNSBnr;
		}
	}

	@Override
	public int hashCode() {
		String afk = getAfkorting6();
		int hash = 1000000;
		
		int tmp = (afk.charAt(0) - 'a' + 1) + (afk.charAt(1) - 'a' + 1) + (afk.charAt(2) - 'a' + 1);  
		hash += tmp * 10000;
		tmp = (afk.charAt(3) - 'a' + 1) + (afk.charAt(4) - 'a' + 1) + (afk.charAt(5) - 'a' + 1);  
		hash += tmp * 100;
		tmp = (afk.charAt(6) - 'a' + 1) + (afk.charAt(7) - 'a' + 1) + (afk.charAt(8) - 'a' + 1);  
		hash += tmp;
		return hash;
	}

	public int getKeipunten() {
		return keipunten;
	}

	public void setKeipunten(int keipunten) {
		this.keipunten = keipunten;
	}

	public int getKeikansen() {
		return keikansen;
	}

	public void setKeikansen(int keikansen) {
		this.keikansen = keikansen;
	}

	public String getSpeelgeschiedenis() {
		return speelgeschiedenis;
	}

	public void setSpeelgeschiedenis(String speelgeschiedenis) {
		this.speelgeschiedenis = speelgeschiedenis;
	}

	/**
     * Geef de drie letterige afkorting van een spelersnaam. De afkorting bestaat uit de volgende3 karakters: 1. Eerste
     * letter voornaam 2. Eerste letter achternaam (laatste woord) 3. Tweede letter achternaam (laatste woord)
     *
     * @todo unicode de afkorting
     * @return afkorting, string 3 karakters
     */
    public String getAfkorting3() {
        String afkorting;
        if (naam.length() < 2) {
            return "";
        }
        afkorting = naam.substring(0, 1);
        String achternaam = null;
        StringTokenizer tokenizer = new StringTokenizer(naam, " ");
        while (tokenizer.hasMoreTokens()) {
            achternaam = tokenizer.nextToken();
        }
        if (achternaam != null) {
            afkorting += achternaam.substring(0, 2);
        }
        return verwijderAccenten(afkorting.toLowerCase());
    }


    public boolean isAfwezigheidspunt() {
        return afwezigheidspunt;
    }

    public void setAfwezigheidspunt(boolean afwezigheidspunt) {
        this.afwezigheidspunt = afwezigheidspunt;
    }

    public boolean isAanwezig() {
        return aanwezig;
    }

    public void setAanwezig(boolean aanwezig) {
        this.aanwezig = aanwezig;
    }

    /**
     * Wordt dezelfde speler gerepresenteerd door het andere object?
     * @param speler
     * @return 
     */
    public boolean gelijkAan(Speler speler) {
        return (this.getNaam().equals(speler.getNaam())
                && this.getInitialen().equals(speler.getInitialen())
                && this.getGroep() == speler.getGroep()
                && this.getKNSBnummer() == speler.getKNSBnummer());
    }

    @Override
    public String toString() {
        String result;
        result = Integer.toString(id);
        if (result.length() == 1) {
            result = "0" + result;
        }
        result += ". " + naam;
        while (result.length() < 35) {
            result += " ";
        }
        return result;
    }

    public String toPrintableString() {
    	return toPrintableString(false);
    }

    public String toPrintableString(boolean lang) {
    	String result = toPrintableStringShort();
    	if (lang)result += printExtensie();
    	return result;
    }

    public String toPrintableStringShort() {
        //16. Iva  Binnendijk                (Iv)* w1 ( 296)   RM Ja SW **    17
        String result;
        // ID
        result = Integer.toString(id);
        if (result.length() == 1) {
            result = "0" + result;
        }
        // Naam
        result += ". " + naam.trim();
        while (result.length() < 35) {
            result += " ";
        }
        // Initialen
        result += "(" + initialen + ")";
        // Afwezigheidspunt
        result += (afwezigheidspunt ? "#" : " ");
        // Witvoorkeur
        result += " ";
        if (witvoorkeur > 0) {
            result += "w" + decimalFormat.format(witvoorkeur);
        } else if (witvoorkeur < 0) {
            result += "z" + decimalFormat.format((-1 * witvoorkeur));
        } else {
        	result += "  ";
        }
        // Rating
        result += " (";
        String tmp = Integer.toString(rating);
        if (tmp.length() == 3) {
            tmp = " " + tmp;
        }
        result += tmp + ")";
        // Tegentanders
        result += "   " + tegenstanders[0] + tegenstanders[1] + tegenstanders[2] + tegenstanders[3];
        // Punten
        tmp = Integer.toString(punten);
        while (tmp.length() < 5) {
            tmp = " " + tmp;
        }
        result += tmp;
        return result;

    }

    public String printExtensie() {
    	//   0/ 0 8560057 Im+## -- -- LE=-- BA=-- IW+## -- jA+-- -- -- --
    	//1234567890123456789012345678901234567890123456789012345678901234567890
    	//         1         2         3         4         5         6         7
    	String result = " ";
    	// Keipunten
    	String tmp = Integer.toString(keipunten);
    	result += (tmp.length() < 2 ? " " : "") + tmp + "/";
    	// Keikansen
    	tmp = Integer.toString(keikansen);
    	result += (tmp.length() < 2 ? " " : "") + tmp + " ";
    	// KNSB
    	tmp = Integer.toString(KNSBnummer);
        while (tmp.length() < 7) {
            tmp = " " + tmp;
        }    	
        result += tmp + " ";
    	// speelgeschiedenis
        result += speelgeschiedenis;
        return result;

    }
    
    private String verwijderAccenten(String naam) {
        String norm = Normalizer.normalize(naam, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(norm).replaceAll("");
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Is er eerder tegen meegegeven speler gepseeld?
     *
     * @param speler2 Speler die gecontroleerd word
     * @return True als er in een van de laatste vier wedstrijden tegen deze speler gespeeld is
     */
    public boolean isGespeeldTegen(Speler speler2) {
        return isGespeeldTegen(speler2, 0);
    }

    /**
     * Is er eerder tegen meegegeven speler gespeeld?
     *
     * @param speler2 Speler die gecontroleerd word "param negeerNspelers Hoeveel van de oudste spelers worden niet
     * meetgeteld?
     * @return True als er in een van de laatste vier wedstrijden tegen deze speler gespeeld is
     */
    public boolean isGespeeldTegen(Speler speler2, int negeerNspelers) {
        String ini = speler2.getInitialen();
        // ff checken dat het niet de speler zelf is ;-)
        if (initialen.equals(ini)) return true;
        for (int i = 0 + negeerNspelers; i < tegenstanders.length; ++i) {
            if (tegenstanders[i].substring(0, 2).equals(ini)) {
                return true;
            }
        }
        return false;
    }

    public void addTegenstander(String tgn) {
    	speelgeschiedenis += tegenstanders[0];
    	speelgeschiedenis = speelgeschiedenis.substring(3);
        tegenstanders[0] = tegenstanders[1];
        tegenstanders[1] = tegenstanders[2];
        tegenstanders[2] = tegenstanders[3];
        tegenstanders[3] = tgn;
    }

    public static Speler dummySpeler(int groepID) {
        return new Speler(99, "Dummy", "--", 0, groepID, (groepID + 1) * 100, new String[]{"--", "--", "--", "--"}, 
        		0, false, true, 1234567,0,0, "-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --");

    }
    
    /**
     * Retourneert een 9 letterige afkorting van de naam.
     * Deze is opgebouwd uit 
     * 1. de eerste 3 van de eerste voornaam 
     * 2. de eerste 3 van de eerste voornaam
     * 3. de middelste drie letters van de volledige naam
     * Te korte naamsdelen worden aangevuld met 'z'.
     * Vooral bedoeld om semi-hash obv naam te realiseren
     * @return
     */
    public String getAfkorting6() {
        String afkorting;
        String tmpnaam = naam;
        tmpnaam = tmpnaam.replaceAll("\\s+","");
        // Hele naam korter dan 9 karakters -> hele naam aanvullen met 'z'
        if (tmpnaam.length() < 9) {
        	while (tmpnaam.length() < 9) {
        		tmpnaam += 'z';
        	}
            return tmpnaam.toLowerCase();
        }
        // 1. eerste drie letters voornaam
        afkorting = tmpnaam.substring(0, 3);
        // 2. eerste drie letters achternaam
        String achternaam = null;
        StringTokenizer tokenizer = new StringTokenizer(naam, " ");
        while (tokenizer.hasMoreTokens()) {
            achternaam = tokenizer.nextToken();
        }
        if (achternaam != null) {
        	if (achternaam.length() >= 3) {
        		afkorting += achternaam.substring(0, 3);
        	} else {
        		afkorting += achternaam;
        		while (afkorting.length() < 6) {
        			afkorting += "z";
        		}
        	}
        } else {
        	afkorting += "zzz";
        }
        // 3. middelste letters volledige naam
        afkorting += tmpnaam.substring((tmpnaam.length()/2) - 1, (tmpnaam.length()/2) + 2);
        
        return verwijderAccenten(afkorting.toLowerCase());
    }

	@Override
	public boolean equals(Object arg0) {
		if (arg0 == null) return false;
		return gelijkAan((Speler) arg0);
	}
    
    

}
