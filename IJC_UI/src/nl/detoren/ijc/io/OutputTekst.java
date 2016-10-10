package nl.detoren.ijc.io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import nl.detoren.ijc.data.groepen.Groepen;

public class OutputTekst {

	private final static Logger logger = Logger.getLogger(OutputTekst.class.getName());

	/**
     * Sla de nieuwe stand op in een uitslag?-?.txt bestand en
     * in een json versie van resultaatVerwerkt
     */
    public void saveUitslag(Groepen uitslag) {
		try {
			String bestandsnaam = "Uitslag" + uitslag.getPeriode() + "-" + uitslag.getRonde(); 
			logger.log(Level.INFO, "Sla uitslag op in bestand " + bestandsnaam);

			// Short variant
			FileWriter writer = new FileWriter(bestandsnaam + ".txt");
			writer.write(uitslag.toPrintableString(false));
			writer.close();

			// Long variant
			writer = new FileWriter(bestandsnaam + "-long.txt");
			writer.write(uitslag.toPrintableString(true));
			writer.close();

			// GSON variant
			Gson gson = new Gson();
			writer = new FileWriter(bestandsnaam + ".json");
			writer.write(gson.toJson(uitslag));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }


}
