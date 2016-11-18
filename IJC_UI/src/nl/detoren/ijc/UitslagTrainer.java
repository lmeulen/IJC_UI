package nl.detoren.ijc;

import nl.detoren.ijc.neural.Voorspeller;

/**
 * Creeer een getraind neural network op basis van historische wedstrijddata
 * DEze data is opgeslagen in het bestand traindata.arff
 * @author Leo.vanderMeulen
 *
 */
public class UitslagTrainer {

	/**
	 * Main app
	 * @param args
	 */
	public static void main(String[] args) {
		Voorspeller v = new Voorspeller();
		v.initialiseerStandaard();
		v.train();
		v.saveNetwork();
	}

}
