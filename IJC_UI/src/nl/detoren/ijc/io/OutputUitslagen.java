package nl.detoren.ijc.io;

import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.wedstrijden.Groepswedstrijden;
import nl.detoren.ijc.data.wedstrijden.Serie;
import nl.detoren.ijc.data.wedstrijden.Wedstrijd;
import nl.detoren.ijc.data.wedstrijden.Wedstrijden;
import nl.detoren.ijc.ui.control.IJCController;

public class OutputUitslagen implements WedstrijdenExportInterface{

    private final static Logger logger = Logger.getLogger(IJCController.class.getName());
    private final static String ls = System.lineSeparator();
    private int periode;
    private int ronde;

	public boolean export(Wedstrijden wedstrijden) {
		try {
			periode = wedstrijden.getPeriode();
			ronde = wedstrijden.getRonde();
			String bestandsnaam = "R" + periode + "-" + ronde + "Uitslag";
			logger.log(Level.INFO, "Sla uitslag op in bestand " + bestandsnaam);
			String result = "";
			result += "Wedstrijden Periode " + periode;
			result += " Ronde " + periode + ls + "-----------" + ls;
			for (Groepswedstrijden gw : wedstrijden.getGroepswedstrijden()) {
				result += printGroepsWedstrijden(gw) + ls;
			}
			FileWriter writer = new FileWriter(bestandsnaam + ".txt");
			writer.write(result);
			writer.write(ls + "Aangemaakt met " + IJCController.c().appTitle + " voor "
					+ IJCController.c().verenigingNaam + ls);
			writer.close();
		} catch (Exception e) {
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
