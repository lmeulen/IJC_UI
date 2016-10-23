package nl.detoren.ijc.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.ui.control.IJCController;

/**
 * Importeer spelers uit een CSV bestand. De volgende items staan in dit bestand:
 * - groepnummer
 * - naam speler
 * - afkorting (2 karakters)
 * - KNSBnummer (1234567 indien onbekend)
 * 
 * @author Leo.vanderMeulen
 *
 */
public class ImportSpelers {

	private final static Logger logger = Logger.getLogger(ImportSpelers.class.getName());
    
    /**
     * Lees groepen uit het gespecificeerde textbestand
     * @param bestandsnaam Naam van het bestand dat ingelezen moet worden
     * @return De ingelezen spelers verdeeld over de groepen
     */
    public void importeerSpelers(String bestandsnaam) {
		logger.log(Level.INFO, "Lezen spelers in CSV formaat uit : " + bestandsnaam);

        // Lees het volledige bestand in naar een String array
        String[] stringArr = leesBestand(bestandsnaam);
        for (String regel : stringArr) {
    		logger.log(Level.INFO, "Speler : " + bestandsnaam);
        	List<String> items = Arrays.asList(regel.split("\\s*,\\s*"));
        	int groepID = Integer.parseInt(items.get(0));
        	String naam = items.get(1);
        	String afk = items.get(2).substring(0, 1);
        	int knsb = Integer.parseInt(items.get(3));
        	Speler s = new Speler();
        	s.setGroep(groepID);
        	s.setNaam(naam);
        	s.setInitialen(afk);
        	s.setKNSBnummer(knsb);
            s.setTegenstanders(new String[]{"-- ","-- ","-- ","-- "});
            s.setSpeelgeschiedenis("-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- ");
        	Groep groep = IJCController.getInstance().getGroepByID(groepID);
        	groep.addSpeler(s);
    		logger.log(Level.INFO, "toegevoegd aan : " + groep.getNaam());
        }
    }

    /**
     * Lees een bestand in en retourneer dit als Strings.
     * @param bestandsnaam
     * @return array of strings met bestandsinhoud
     */
    private String[] leesBestand(String bestandsnaam) {
        List<String> list = new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(bestandsnaam));
            String str;
            while ((str = in.readLine()) != null) {
                list.add(str);
            }
            in.close();
            return list.toArray(new String[0]);
        } catch (IOException e) {
        }
        return null;
    }

}