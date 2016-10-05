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
 * - FIXME KNSB lidmaatschapnummers
 */
package nl.detoren.ijc.data.groepen;

import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Dit object bevat de gegevens van ��n speler
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
    
    private static final DecimalFormat decimalFormat = new DecimalFormat("#");

    public Speler() {
        this(0, "", "", 0, 0, 0, new String[4], 0, false, true);
    }

    public Speler(Speler s) {
        this.id = s.id;
        this.naam = s.naam;
        this.initialen = s.initialen;
        this.witvoorkeur = s.witvoorkeur;
        this.groep = s.groep;
        this.rating = s.rating;
        this.tegenstanders = s.tegenstanders.clone();
        this.punten = s.punten;
        this.afwezigheidspunt = s.afwezigheidspunt;
        this.aanwezig = s.aanwezig;
    }

    public Speler(int id, String naam, String initialen, int witvk, int groep, int rating, String[] tgs, int punten,
            boolean ap, boolean aanw) {
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

    public void setInitialen(String s) {
        initialen = s;
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
     * @param s
     * @return 
     */
    public boolean gelijkAan(Speler s) {
        return (this.getNaam().equals(s.getNaam())
                && this.getInitialen().equals(s.getInitialen())
                && this.getGroep() == s.getGroep());
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

    private String verwijderAccenten(String s) {
        String norm = Normalizer.normalize(s, Normalizer.Form.NFD);
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
     * @return True als er in één van de laatste vier wedstrijden tegen deze speler gespeeld is
     */
    public boolean isGespeeldTegen(Speler speler2) {
        return isGespeeldTegen(speler2, 0);
    }

    /**
     * Is er eerder tegen meegegeven speler gespeeld?
     *
     * @param speler2 Speler die gecontroleerd word "param negeerNspelers Hoeveel van de oudste spelers worden niet
     * meetgeteld?
     * @return True als er in één van de laatste vier wedstrijden tegen deze speler gespeeld is
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
        tegenstanders[0] = tegenstanders[1];
        tegenstanders[1] = tegenstanders[2];
        tegenstanders[2] = tegenstanders[3];
        tegenstanders[3] = tgn;
    }

    public static Speler dummySpeler(int groepID) {
        return new Speler(99, "Dummy", "--", 0, groepID, (groepID + 1) * 100, new String[]{"--", "--", "--", "--"}, 0, false, true);

    }
}
