package nl.detoren.ijc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.gson.Gson;

import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.data.wedstrijden.Wedstrijd;
import nl.detoren.ijc.db.DBHistorie;
import nl.detoren.ijc.db.DBRonde;
import nl.detoren.ijc.db.DBSpeler;
import nl.detoren.ijc.db.DBWedstrijd;
import nl.detoren.ijc.db.Kleur;
import nl.detoren.ijc.db.Resultaat;
import nl.detoren.ijc.ui.control.Status;

public class SpelerDatabase {

	private EntityManagerFactory emFactory;
	private EntityManager entityManager;

	public SpelerDatabase() {
	}

	/**
	 * Open de spelers database
	 */
	public void openDatabase() {
		emFactory = Persistence.createEntityManagerFactory("$objectdb/db/spelers.odb");
		entityManager = emFactory.createEntityManager();
	}

	/**
	 * sluit de speler database
	 */
	public void closeDatabase() {
		// Close the database connection:
		entityManager.close();
		emFactory.close();
	}

	/**
	 * Start een transaction op de database
	 */
	public void startTransaction() {
		entityManager.getTransaction().begin();
	}

	/**
	 * Commit een transaction op de database
	 */
	public void commit() {
		entityManager.getTransaction().commit();
	}

	/**
	 * Sla een object op i nde database
	 *
	 * @param o
	 */
	public void store(Object o) {
		entityManager.persist(o);
	}

	/**
	 * Voer een query uit die een enkel resultaat oplevert
	 *
	 * @param query
	 * @return
	 */
	public Object querySingleResult(String query) {
		return entityManager.createQuery(query).getSingleResult();
	}

	/**
	 * Voer een query uit die een enkel resultaat oplevert gecast naar het
	 * gespecificeerde type
	 *
	 * @param query
	 * @param type
	 * @return
	 */
	public <T> T querySingleResult(String query, Class<T> type) {
		List<T> results = entityManager.createQuery(query, type).getResultList();
		if (results.isEmpty())
			return null;
		return results.get(0);
		// return entityManager.createQuery(query, type).getSingleResult();
	}

	/**
	 * Voer een query uit die een lijst van objecten retourneert waarbij deze
	 * gecast zijn naar het gespecificeerde type
	 *
	 * @param query
	 * @param type
	 * @return
	 */
	public <T> List<T> query(String query, Class<T> type) {
		return entityManager.createQuery(query, type).getResultList();
	}

	public void doSomething() {
		openDatabase();
		leesStatus();
		closeDatabase();
	}

	public static void main(String[] args) {
		SpelerDatabase db = new SpelerDatabase();
		db.doSomething();
	}

	public void leesStatus() {
		try {
			startTransaction();

			Status status = leesStatusBestand();

			// Bepaal ronde
			DBRonde ronde = new DBRonde(status.wedstrijden.getSpeeldatum(), 2016, status.wedstrijden.getPeriode(),
					status.wedstrijden.getRonde());
			// check of ronde al bestaat
			if (rondeExists(ronde)) {
				System.out.println("Ronde reeds ingelezen");
				commit();
				return;
			}
			store(ronde);

			// Lees spelers
			ArrayList<DBSpeler> nieuweSpelers = new ArrayList<>();
			for (Groep groep : status.resultaatVerwerkt.getGroepen()) {
				for (Speler gs : groep.getSpelers()) {
					DBSpeler dbSpeler = querySingleResult(
							"select s from DBSpeler s where knsbnummer = " + gs.getKNSBnummer(), DBSpeler.class);
					if (dbSpeler == null) {
						// Speler nog niet in DB, toevoegen
						dbSpeler = new DBSpeler(gs.getNaam(), gs.getInitialen(), gs.getKNSBnummer());
						nieuweSpelers.add(dbSpeler);
						store(dbSpeler);
					}
					DBHistorie shp = new DBHistorie(ronde, gs.getGroep(), gs.getRating(), gs.getPunten());
					dbSpeler.addHistorie(shp);
					store(dbSpeler);
				}
			}
			System.out.println("Aantal nieuwe    spelers : " + nieuweSpelers.size());

			commit();
			startTransaction();

			// Lees wedstrijden
			ArrayList<DBWedstrijd> wedstrijden = new ArrayList<>();
			for (Wedstrijd ws : status.wedstrijden.getAlleWedstrijden()) {
				Speler wit = ws.getWit();
				Speler zwart = ws.getZwart();
				DBSpeler swit = getSpelerByKNSB(wit.getKNSBnummer());
				DBSpeler szwart = getSpelerByKNSB(zwart.getKNSBnummer());
				// Check in bouwen of speler gevonden
				DBWedstrijd wsw = new DBWedstrijd(swit, ronde, szwart, Kleur.WIT, res(ws.getUitslag(), true));
				DBWedstrijd wsz = new DBWedstrijd(szwart, ronde, swit, Kleur.ZWART, res(ws.getUitslag(), false));
				ronde.addWedstrijd(wsw);
				ronde.addWedstrijd(wsz);
				wedstrijden.add(wsw);
				wedstrijden.add(wsz);
			}
			System.out.println("Aantal wedstrijden : " + wedstrijden.size());
			store(ronde);
			commit();
		} catch (Exception e) {
			// Could not read status
			e.printStackTrace();
		}
	}

	public DBSpeler getSpelerByKNSB(int knsb) {
		return querySingleResult("SELECT s FROM DBSpeler s WHERE knsbnummer = " + knsb, DBSpeler.class);
	}

	public Status leesStatusBestand() throws FileNotFoundException, IOException {
		Status status;
		BufferedReader br = new BufferedReader(new FileReader("R1-7\\status20161121_081945-uitslag.json"));
		status = new Gson().fromJson(br, Status.class);
		System.out.println("Done reading");
		br.close();
		return status;
	}

	/**
	 * Check of deze ronde al reeds in de database is opgeslagen
	 * @param ronde
	 * @return
	 */
	private boolean rondeExists(DBRonde ronde) {
		String query = "SELECT r FROM DBRonde r WHERE seizoen = " + (ronde.getSeizoen() + 0);
		query += " AND periode = " + ronde.getPeriode();
		query += " AND ronde = " + ronde.getRonde();
		return querySingleResult(query, DBRonde.class) != null;
	}

	/**
	 * Convert uitslag conform toto notatie naar Resultaat enumeratie
	 * @param uitslag
	 * @param wit
	 * @return
	 */
	private Resultaat res(int uitslag, boolean wit) {
		if (wit) {
			switch (uitslag) {
			case 0:
				return Resultaat.ONBEKEND;
			case 1:
				return Resultaat.WINST;
			case 2:
				return Resultaat.VERLIES;
			case 3:
				return Resultaat.REMISE;
			default:
				return Resultaat.ONBEKEND;
			}
		} else {
			switch (uitslag) {
			case 0:
				return Resultaat.ONBEKEND;
			case 1:
				return Resultaat.VERLIES;
			case 2:
				return Resultaat.WINST;
			case 3:
				return Resultaat.REMISE;
			default:
				return Resultaat.ONBEKEND;
			}
		}
	}
}