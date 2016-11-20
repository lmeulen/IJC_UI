package nl.detoren.ijc.neural;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.data.wedstrijden.Wedstrijd;
import nl.detoren.ijc.ui.control.IJCController;

public class NeuralHelper {

	private static String intFormats[] = { "", "%01d", "%02d", "%03d", "%04d", "%05d", "%06d", "%07d", "%08d", "%09d" };
	//private static String fltFormats[] = { "", "%.1f", "%.2f", "%.3f" };
	private final static Logger logger = Logger.getLogger(NeuralHelper.class.getName());
	private final static String sep = ",";


	/**
	 * @RELATION chess_result
	 * @ATTRIBUTE wit_rating NUMERIC
	 * @ATTRIBUTE wit_punten NUMERIC
	 * @ATTRIBUTE wit_groep NUMERIC                         *
	 * @ATTRIBUTE wit_resultaat_1 NUMERIC
	 * @ATTRIBUTE wit_resultaat_2 NUMERIC
	 * @ATTRIBUTE wit_resultaat_3 NUMERIC
	 * @ATTRIBUTE wit_resultaat_4 NUMERIC
	 * @ATTRIBUTE zwart_rating NUMERIC
	 * @ATTRIBUTE zwart_punten NUMERIC
	 * @ATTRIBUTE zwart_groep NUMERIC                       *
	 * @ATTRIBUTE zwart_resultaat_1 NUMERIC
	 * @ATTRIBUTE zwart_resultaat_2 NUMERIC
	 * @ATTRIBUTE zwart_resultaat_3 NUMERIC
	 * @ATTRIBUTE zwart_resultaat_4 NUMERIC
	 * @ATTRIBUTE resultaat {wit,remise,zwart}
	 *
	 *            retourneer data voor één wedstrijd
	 * @param gw
	 */

	public static String convertWedstrijd(Wedstrijd w) {
		logger.log(Level.FINE, w.toString());
		String result = "";
		result += String.format(Locale.US, intFormats[4], w.getWit().getRating()) + sep;
		result += String.format(Locale.US, intFormats[3], w.getWit().getPunten()) + sep;
		result += String.format(Locale.US, intFormats[1], w.getWit().getGroep()) + sep;
		result += tegenstander(w.getWit().getTegenstanders()[3]) + sep;
		result += tegenstander(w.getWit().getTegenstanders()[2]) + sep;
		result += tegenstander(w.getWit().getTegenstanders()[1]) + sep;
		result += tegenstander(w.getWit().getTegenstanders()[0]) + sep;
		result += String.format(Locale.US, intFormats[4], w.getZwart().getRating()) + sep;
		result += String.format(Locale.US, intFormats[3], w.getZwart().getPunten()) + sep;
		result += String.format(Locale.US, intFormats[1], w.getZwart().getGroep()) + sep;
		result += tegenstander(w.getZwart().getTegenstanders()[3]) + sep;
		result += tegenstander(w.getZwart().getTegenstanders()[2]) + sep;
		result += tegenstander(w.getZwart().getTegenstanders()[1]) + sep;
		result += tegenstander(w.getZwart().getTegenstanders()[0]) + sep;
		result += uitslag2description(w.getUitslag());
		logger.log(Level.FINE, result);
		return result;
	}

	private static String tegenstander(String tgn) {
		return  tegenstander2result(tgn) + sep + tegenstander2rating(tgn);
	}

	private static String uitslag2description(int uitslag) {
		switch (uitslag) {
		case 1:
			return "wit";
		case 2:
			return "zwart";
		case 3:
			return "remise";
		default:
			return "?";
		}
	}

	/**
	 * Retourneer wedstrijdsresultaat, ? als onbekend
	 * @param tgn
	 * @return
	 */
	private static String tegenstander2result(String tgn) {
		if (tgn.length() == 3) {
			switch (tgn.charAt(2)) {
			case '=':
				return "0.5";
			case '+':
				return "1.0";
			case '-' :
				return "0.0";
			default:
				return "0.0";
			}

		}
		return "0.0";
	}

	private static String tegenstander2rating(String tgn) {
		Speler s = IJCController.getI().getGroepen().getSpelerByInitialen(tgn.substring(0, 2));
		return (s!=null) ? String.format(Locale.US, intFormats[4], s.getRating()) : "0";
	}


}
