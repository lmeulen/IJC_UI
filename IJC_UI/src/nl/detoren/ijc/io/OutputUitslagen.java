package nl.detoren.ijc.io;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.wedstrijden.Groepswedstrijden;
import nl.detoren.ijc.data.wedstrijden.Serie;
import nl.detoren.ijc.data.wedstrijden.Wedstrijd;
import nl.detoren.ijc.data.wedstrijden.Wedstrijden;
import nl.detoren.ijc.ui.control.IJCController;
import nl.detoren.ijc.ui.util.Utils;

public class OutputUitslagen implements WedstrijdenExportInterface{

    private final static Logger logger = Logger.getLogger(IJCController.class.getName());
    private final static String ls = System.lineSeparator();
    private int periode;
    private int ronde;

	public boolean export(Wedstrijden wedstrijden) {
		try {
			periode = wedstrijden.getPeriode();
			ronde = wedstrijden.getRonde();
			String bestandsnaam = "R" + periode + "-" + ronde + "Uitslag.txt";
			logger.log(Level.INFO, "Sla uitslag op in bestand " + bestandsnaam);
			String result = "";
			result += "Wedstrijden Periode " + periode + " Ronde " + ronde;
			if (wedstrijden.getSpeeldatum() != null) {
				result += ", datum " + (new SimpleDateFormat("dd-MM-yyyy")).format(wedstrijden.getSpeeldatum());
				result += ls + "-----------------------------------------------" + ls + ls;
			} else {
				result += ls + "-----------------------------" + ls + ls;
			}
			for (Groepswedstrijden gw : wedstrijden.getGroepswedstrijden()) {
				result += printGroepsWedstrijden(gw) + ls;
			}
			String dirName = "R" + wedstrijden.getPeriode() + "-" + wedstrijden.getRonde();
			new File(dirName).mkdirs();
			FileWriter writer = new FileWriter( dirName + File.separator + bestandsnaam);
			writer.write(result);
			writer.write(ls + "Aangemaakt met " + IJCController.c().appTitle + " voor "
					+ IJCController.c().verenigingNaam + ls);
			writer.close();
		} catch (Exception ex) {
            logger.log(Level.INFO, "Exception: " +  ex.getMessage());
            Utils.stacktrace(ex);

			return false;
		}
		return true;
	}

	/**
	 * Print wedstrijden voor gespecificeerde Groepswedstrijden
	 * @param gw
	 */
	public String printGroepsWedstrijden(Groepswedstrijden gw) {
		String result = "";
		result += Groep.geefNaam(gw.getNiveau()) + ls;
		int i = 1;
		for (Serie serie : gw.getSeries()) {
			result += "    Serie " + i + ls;
			for (Wedstrijd w : serie.getWedstrijden()) {
				result += "      " + w.toString() + ls;
			}
			++i;
		}
		if (!gw.getTriowedstrijden().isEmpty()) {
			result += "    Trio" + ls;
			for (Wedstrijd w : gw.getTriowedstrijden()) {
				result += "      " + w.toString() + ls;
			}

		}
		return result;
	}
}
