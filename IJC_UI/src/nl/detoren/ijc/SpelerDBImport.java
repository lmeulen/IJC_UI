package nl.detoren.ijc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.groepen.Groepen;
import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.data.wedstrijden.Wedstrijd;
import nl.detoren.ijc.db.DBHistorie;
import nl.detoren.ijc.db.DBRonde;
import nl.detoren.ijc.db.DBSpeler;
import nl.detoren.ijc.db.DBWedstrijd;
import nl.detoren.ijc.db.Kleur;
import nl.detoren.ijc.db.SpelerDatabase;
import nl.detoren.ijc.io.GroepenReader;
import nl.detoren.ijc.ui.control.IJCController;
import nl.detoren.ijc.ui.control.Status;

public class SpelerDBImport {

	SpelerDatabase spelerDB;

	public SpelerDBImport() {
		spelerDB = SpelerDatabase.getInstance();
	}

	public void importStatusObject(String bestandsnaam) {
		try {
			Status status = leesStatusBestand(bestandsnaam);
			importStatusObject(status);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void importStatusObjectWithDBSession(Status status) {
		spelerDB.openDatabase();
		importStatusObject(status);
		spelerDB.cleanupDuplicatePlayers();
		spelerDB.cleanUpMultipleRoundsPerPlayer();
		spelerDB.closeDatabase();
	}

	public void importStatusObject(Status status) {
		try {
			spelerDB.startTransaction();

			// Bepaal ronde
			DBRonde ronde = new DBRonde(status.wedstrijden.getSpeeldatum(), 2016, status.wedstrijden.getPeriode(),
					status.wedstrijden.getRonde());
			// check of ronde al bestaat
			if (spelerDB.rondeExists(ronde)) {
				System.out.println("Ronde reeds ingelezen");
				spelerDB.endTransaction();
				return;
			}
			spelerDB.store(ronde);

			// Lees spelers
			int nieuweSpelers = 0;
			for (Groep groep : status.resultaatVerwerkt.getGroepen()) {
				for (Speler gs : groep.getSpelers()) {
					DBSpeler dbSpeler = spelerDB.getSpelerByName(gs.getNaam());
					if (dbSpeler == null) {
						// Speler nog niet in DB, toevoegen
						dbSpeler = new DBSpeler(gs.getNaam(), gs.getInitialen(), gs.getKNSBnummer());
						nieuweSpelers++;
						spelerDB.store(dbSpeler);
					} else {
						// Check if an improved KNSB nummer is available
						if ((gs.getKNSBnummer() > 8000000) && (dbSpeler.getKnsbnummer() < 2000000)) {
							dbSpeler.setKnsbnummer(gs.getKNSBnummer());
							spelerDB.store(dbSpeler);
						}
					}
					DBHistorie shp = new DBHistorie(ronde, gs.getGroep(), gs.getRating(), gs.getPunten());
					dbSpeler.addHistorie(shp);
					spelerDB.store(dbSpeler);
				}
			}
			System.out.println("Aantal nieuwe spelers : " + nieuweSpelers);

			spelerDB.endTransaction();
			spelerDB.startTransaction();

			// Lees wedstrijden
			ArrayList<DBWedstrijd> wedstrijden = new ArrayList<>();
			for (Wedstrijd ws : status.wedstrijden.getAlleWedstrijden()) {
				Speler wit = ws.getWit();
				Speler zwart = ws.getZwart();
				DBSpeler swit = spelerDB.getSpelerByKNSB(wit.getKNSBnummer());
				DBSpeler szwart = spelerDB.getSpelerByKNSB(zwart.getKNSBnummer());
				// Check in bouwen of speler gevonden
				DBWedstrijd wsw = new DBWedstrijd(swit, ronde, szwart, Kleur.WIT, spelerDB.res(ws.getUitslag(), true));
				DBWedstrijd wsz = new DBWedstrijd(szwart, ronde, swit, Kleur.ZWART,
						spelerDB.res(ws.getUitslag(), false));
				ronde.addWedstrijd(wsw);
				ronde.addWedstrijd(wsz);
				wedstrijden.add(wsw);
				wedstrijden.add(wsz);
			}
			System.out.println("Aantal wedstrijden : " + wedstrijden.size());
			spelerDB.store(ronde);
			spelerDB.endTransaction();
		} catch (Exception e) {
			// Could not read status
			e.printStackTrace();
		}
	}

	/**
	 * Importeer klassiek uitslag-long.txt bestand, en importeer speler
	 * historie. Er worden geen wedstrijden ingelezen.
	 *
	 * @param bestand
	 */
	public void importUitslagText(String bestand) {
		Groepen groepen = new GroepenReader().leesGroepen(bestand);
		spelerDB.startTransaction();
		// Bepaal ronde

		int r = groepen.getRonde() - 1;
		int p = groepen.getPeriode();
		if (r == 0) {
			r = IJCController.c().rondes;
			p--;
			if (p == 0) {
				p = IJCController.c().perioden;
			}
		}

		DBRonde ronde = new DBRonde(null, 2016, p, r);
		// check of ronde al bestaat
		if (spelerDB.rondeExists(ronde)) {
			System.out.println("Ronde reeds ingelezen");
			spelerDB.endTransaction();
			return;
		}
		spelerDB.store(ronde);
		// Lees spelers
		int nieuweSpelers = 0;
		for (Groep groep : groepen.getGroepen()) {
			for (Speler gs : groep.getSpelers()) {
				DBSpeler dbSpeler = spelerDB.getSpelerByKNSB(gs.getKNSBnummer());
				if (dbSpeler == null) {
					// Speler nog niet in DB, toevoegen
					dbSpeler = new DBSpeler(gs.getNaam(), gs.getInitialen(), gs.getKNSBnummer());
					nieuweSpelers++;
					spelerDB.store(dbSpeler);
				} else {
					// Check if an improved KNSB nummer is available
					if ((gs.getKNSBnummer() > 8000000) && (dbSpeler.getKnsbnummer() < 2000000)) {
						dbSpeler.setKnsbnummer(gs.getKNSBnummer());
						spelerDB.store(dbSpeler);
					}
				}
				DBHistorie shp = new DBHistorie(ronde, gs.getGroep(), gs.getRating(), gs.getPunten());
				dbSpeler.addHistorie(shp);
				spelerDB.store(dbSpeler);
			}
		}
		System.out.println("Aantal nieuwe spelers : " + nieuweSpelers);
		spelerDB.endTransaction();
	}

	/**
	 * Lees een statusbestand in en stel beschikbaar als object
	 *
	 * @param bestandsnaam
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Status leesStatusBestand(String bestandsnaam) throws FileNotFoundException, IOException {
		Status status;
		BufferedReader br = new BufferedReader(new FileReader(bestandsnaam));
		status = new Gson().fromJson(br, Status.class);
		System.out.println("Done reading");
		br.close();
		return status;
	}

	public void doSomething() {
		spelerDB.openDatabase();
		// importUitslagText("Resultaten\\R1-1\\R1-1Uitslag-long.txt");
		// importUitslagText("Resultaten\\R1-2\\R1-2Uitslag-long.txt");
		// importUitslagText("Resultaten\\R1-3\\R1-3Uitslag-long.txt");
		// importUitslagText("Resultaten\\R1-4\\R1-4Uitslag-long.txt");
		// importUitslagText("Resultaten\\R1-5\\R1-5Uitslag-long.txt");
		// importUitslagText("Resultaten\\R1-6\\R1-6Uitslag-long.txt");
		// importStatusObject("Resultaten\\R1-7\\status20161104_080315-uitslag.json");
		// importStatusObject("R1-8\\status20161121_154534-uitslag.json");
		// importStatusObject("R2-1\\status20161125_103139-uitslag.json");
		// importStatusObject("R2-2\\status20170206_152424-uitslag.json");
		// importStatusObject("R2-3\\status20170206_161353-uitslag.json");
		// importStatusObject("R2-4\\status20170206_162422-uitslag.json");
		// importStatusObject("R2-5\\status20170206_200454-uitslag.json");
		// importStatusObject("R2-6\\status20170206_202433-uitslag.json");
		// importStatusObject("R2-7\\status20170206_204013-uitslag.json");
		// importStatusObject("R2-8\\status20170206_205135-uitslag.json");
		// importStatusObject("R3-1\\status20170209_225534-uitslag.json");
		// importStatusObject("R3-2\\status20170216_195601-uitslag.json");
		// importStatusObject("R3-3\\status20170223_204142-uitslag.json");

		spelerDB.cleanupDuplicatePlayers();
		spelerDB.cleanUpMultipleRoundsPerPlayer();

		spelerDB.closeDatabase();
	}

	public static void main(String[] args) {
		SpelerDBImport db = new SpelerDBImport();
		db.doSomething();
		// db.showGraph();
	}

//	private void showGraph() {
//		XYGraph g = new XYGraph("Rating verloop", "Ronde", "Rating", true);
//		g.initialize(createXYDataset(new String[] { "FW", "Ma", "TT", "RM", "GT" }));
//		JFrame f = new JFrame();
//		f.add(g);
//		f.setVisible(true);
//
//		LineGraph g2 = new LineGraph("Rating verloop", "Ronde", "Rating", true);
//		g2.initialize(createCategoryDataset(new String[] { "FW", "Ma", "TT" }));
//		JFrame f2 = new JFrame();
//		f2.add(g2);
//		f2.setVisible(true);
//	}

//	@SuppressWarnings("rawtypes")
//	private XYDataset createXYDataset(String[] afkortingen) {
//		spelerDB.openDatabase();
//		spelerDB.startTransaction();
//		XYSeriesCollection dataset = new XYSeriesCollection();
//		for (String s : afkortingen) {
//			List result = spelerDB
//					.query("select (ronde.periode*10+ronde.ronde), h.rating from DBHistorie h where speler.afkorting = \""
//							+ s + "\"");
//			XYSeries serie = new XYSeries(s);
//			for (int i = 0; i < result.size(); ++i) {
//				Object o[] = (Object[]) result.get(i);
//				serie.add(((Integer) o[0]).intValue(), ((Integer) o[1]).intValue());
//			}
//			dataset.addSeries(serie);
//		}
//		spelerDB.closeDatabase();
//		return dataset;
//	}
//
//	@SuppressWarnings("rawtypes")
//	private CategoryDataset createCategoryDataset(String[] afkortingen) {
//		spelerDB.openDatabase();
//		spelerDB.startTransaction();
//		DefaultCategoryDataset cat = new DefaultCategoryDataset();
//		for (String s : afkortingen) {
//			List result = spelerDB
//					.query("select (ronde.periode*10+ronde.ronde), h.rating from DBHistorie h where speler.afkorting = \""
//							+ s + "\"");
//			for (int i = 0; i < result.size(); ++i) {
//				Object o[] = (Object[]) result.get(i);
//				Double val = new Double(((Integer) o[1]).intValue());
//				cat.addValue((Number) val, s, ((Integer) o[0]).intValue());
//			}
//		}
//		spelerDB.closeDatabase();
//		return cat;
//	}

}