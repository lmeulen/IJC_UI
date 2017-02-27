package nl.detoren.ijc.db;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpelerDatabase extends ObjectDatabase {

	private static SpelerDatabase instance = null;
	private final static Logger logger = Logger.getLogger(SpelerDatabase.class.getName());

	private SpelerDatabase() {
		super("$objectdb/db/spelers.odb");
	}

	public static SpelerDatabase getInstance() {
		if (instance == null) {
			instance = new SpelerDatabase();
		}
		return instance;
	}

	/**
	 * Haal speler op met het opgegeven KNSB nummer
	 *
	 * @param knsb
	 * @return
	 */
	public DBSpeler getSpelerByKNSB(int knsb) {
		return querySingleResult("SELECT s FROM DBSpeler s WHERE knsbnummer = " + knsb, DBSpeler.class);
	}

	/**
	 * Haal speler op met het opgegeven KNSB nummer
	 *
	 * @param knsb
	 * @return
	 */
	public DBSpeler getSpelerByName(String naam) {
		return querySingleResult("SELECT s FROM DBSpeler s WHERE naam = \"" + naam + "\"", DBSpeler.class);
	}

	/**
	 * Retourneer lijst met alle spelers
	 *
	 * @return
	 */
	public List<DBSpeler> getSpelers() {
		return query("select s from DBSpeler s", DBSpeler.class);
	}

	/**
	 * Retourneer lijst met alle spelers, gesorteerd op gespecificeerd veld
	 *
	 * @return
	 */
	public List<DBSpeler> getSpelers(String orderField) {
		return query("select s from DBSpeler s ORDER BY s." + orderField, DBSpeler.class);
	}

	/**
	 * Retourneer lijst met spelers in een specifieke groep tijdens de laatste
	 * ronde
	 *
	 * @param groep
	 * @param ronde
	 * @return
	 */
	public List<DBSpeler> getSpelers(int groep) {
		return getSpelers(groep, getLaatsteRonde());
	}

	/**
	 * Retourneer lijst met spelers in een specifieke groep tijdens een
	 * specifieke ronde.
	 *
	 * @param groep
	 * @param ronde
	 * @return
	 */
	public List<DBSpeler> getSpelers(int groep, DBRonde ronde) {
		String query = "SELECT s FROM DBSpeler s JOIN s.historie h ";
		query += "WHERE h.ronde.seizoen = " + ronde.getSeizoen();
		query += " AND h.ronde.periode = " + ronde.getPeriode();
		query += " AND h.ronde.ronde = " + ronde.getRonde();
		query += " AND h.groep = " + groep;
		query += " ORDER BY h.punten DESC";
		return query(query, DBSpeler.class);
	}

	/**
	 * Retourneer lijst die spelen in de betreffende ronde (onafhankelijk van
	 * wel/niet aanwezig) ronde.
	 *
	 * @param groep
	 * @param ronde
	 * @return
	 */
	public List<DBSpeler> getSpelers(DBRonde ronde) {
		if (ronde != null) {
			String query = "SELECT s FROM DBSpeler s JOIN s.historie h ";
			query += "WHERE h.ronde.seizoen = " + ronde.getSeizoen();
			query += " AND h.ronde.periode = " + ronde.getPeriode();
			query += " AND h.ronde.ronde = " + ronde.getRonde();
			query += " ORDER BY s.naam";
			return query(query, DBSpeler.class);
		} else {
			return null;
		}
	}

	/**
	 * Geef alle wedstrijden van een speler
	 *
	 * @param s
	 * @return
	 */
	public List<DBWedstrijd> getWedstrijdenVoorSpeler(DBSpeler s) {
		String query = "SELECT w FROM DBWedstrijd w WHERE w.speler.id = " + s.getId();
		query += " ORDER BY w.ronde.seizoen DESC, w.ronde.periode DESC, w.ronde.ronde DESC";
		logger.log(Level.INFO, query);
		return query(query, DBWedstrijd.class);
	}

	/**
	 * Geef alle wedstrijden van een speler
	 *
	 * @param s
	 * @return
	 */
	public List<DBWedstrijd> getWedstrijdenVoorSpeler(DBSpeler s, DBRonde ronde) {
		String query = "SELECT w FROM DBWedstrijd w WHERE w.speler.id = " + s.getId();
		query += " AND w.ronde.id = " + ronde.getId();
		query += " ORDER BY w.ronde.seizoen DESC, w.ronde.periode DESC, w.ronde.ronde DESC";
		return query(query, DBWedstrijd.class);
	}

	/**
	 * Geef alle wedstrijden uit een ronde
	 *
	 * @param s
	 * @return
	 */
	public List<DBWedstrijd> getWedstrijdenVoorRonde(DBRonde ronde) {
		String query = "SELECT w FROM DBWedstrijd w WHERE w.ronde.id = " + ronde.getId();
		query += " ORDER BY w.speler.naam ASC";
		return query(query, DBWedstrijd.class);
	}

	/**
	 * Geef alle wedstrijden van een speler
	 *
	 * @param s
	 * @return
	 */
	public List<DBWedstrijd> getWedstrijden() {
		String query = "SELECT w FROM DBWedstrijd w";
		return query(query, DBWedstrijd.class);
	}

	/**
	 * Retourneer de laatste ronde opgeslagen
	 *
	 * @return
	 */
	public DBRonde getLaatsteRonde() {
		String query = "SELECT r FROM DBRonde r ORDER BY r.seizoen DESC, r.periode DESC, r.ronde DESC";
		return querySingleResult(query, DBRonde.class);
	}

	/**
	 * Retourneer een lijst met alle opgeslagen rondes
	 *
	 * @return
	 */
	public List<DBRonde> getRondes() {
		String query = "SELECT r FROM DBRonde r ORDER BY r.seizoen, r.periode, r.ronde";
		return query(query, DBRonde.class);
	}

	/**
	 * Retourneer een lijst met alle opgeslagen rondes
	 *
	 * @return
	 */
	public List<DBRonde> getRondes(boolean asc) {
		String query = "SELECT r FROM DBRonde r ORDER BY r.seizoen, r.periode, r.ronde";
		if (!asc)
			query = "SELECT r FROM DBRonde r ORDER BY r.seizoen DESC, r.periode DESC, r.ronde DESC";
		return query(query, DBRonde.class);
	}

	/**
	 * Check of deze ronde al reeds in de database is opgeslagen
	 *
	 * @param ronde
	 * @return
	 */
	public boolean rondeExists(DBRonde ronde) {
		if (ronde != null) {
			String query = "SELECT r FROM DBRonde r WHERE seizoen = " + (ronde.getSeizoen() + 0);
			query += " AND periode = " + ronde.getPeriode();
			query += " AND ronde = " + ronde.getRonde();
			return querySingleResult(query, DBRonde.class) != null;
		} else {
			return false;
		}
	}

	public void cleanupDuplicatePlayers() {
		cleanupDuplicatePlayersAbbrevName();
		cleanupDuplicatePlayersEqualKNSB();
		cleanupDuplicatePlayersName();
	}

	/**
	 * If a player exists multiple times in the database, cleanup.
	 * Players are duplicates if
	 * - abbreviation is equal
	 * - name is equal
	 * - KNSB differ
	 */
	private void cleanupDuplicatePlayersAbbrevName() {
		startTransaction();
		List<DBSpeler> spelers = query("select s from DBSpeler s", DBSpeler.class);
		for (int i = 0; i < spelers.size(); ++i) {
			DBSpeler s1 = spelers.get(i);
			for (int j = i + 1; j < spelers.size(); j++) {
				DBSpeler s2 = spelers.get(j);
				if (s1.getAfkorting().equals(s2.getAfkorting())) {
					System.out.println(s1.getAfkorting() + "-" + s1.getKnsbnummer() + "-" + s1.getNaam());
					System.out.println(s2.getAfkorting() + "-" + s2.getKnsbnummer() + "-" + s2.getNaam());
					if (s2.getNaam().equals(s1.getNaam())) {
						if (s1.getKnsbnummer() > 5000000 && s2.getKnsbnummer() < 20000000) {
							System.out.println(s1.getAfkorting() + "-" + s1.getKnsbnummer() + "-" + s1.getNaam());
							moveHistToAnotherPlayer(s2, s1);
							store(s1);
							delete(s2);
						} else if (s2.getKnsbnummer() > 5000000 && s1.getKnsbnummer() < 20000000) {
							System.out.println(s2.getAfkorting() + "-" + s2.getKnsbnummer() + "-" + s2.getNaam());
							moveHistToAnotherPlayer(s1, s2);
							store(s2);
							delete(s1);
						} else {
							System.out.println("No upgrade in KNSB");
						}
					} else {
						System.out.println("Different names!");
					}
					System.out.println("\n");
				}
			}
		}
		endTransaction();
	}

	/**
	 * If a player exists multiple times in the database, cleanup.
	 * Players are duplicates if
	 * - name is equal
	 * - KNSB differ
	 */
	private void cleanupDuplicatePlayersName() {
		startTransaction();
		List<DBSpeler> spelers = query("select s from DBSpeler s", DBSpeler.class);
		for (int i = 0; i < spelers.size(); ++i) {
			DBSpeler s1 = spelers.get(i);
			for (int j = i + 1; j < spelers.size(); j++) {
				DBSpeler s2 = spelers.get(j);
				if (s1.getNaam().equals(s2.getNaam())) {
					System.out.println(s1.getAfkorting() + "-" + s1.getKnsbnummer() + "-" + s1.getNaam());
					System.out.println(s2.getAfkorting() + "-" + s2.getKnsbnummer() + "-" + s2.getNaam());
					if (s1.getLaatsteRonde() > s2.getLaatsteRonde()) {
						System.out.println("MERGING: " + s1.getAfkorting() + "-" + s1.getKnsbnummer() + "-" + s1.getNaam());
						moveHistToAnotherPlayer(s2, s1);
						store(s1);
						delete(s2);
					} else {
						System.out.println("MERGING: " + s2.getAfkorting() + "-" + s2.getKnsbnummer() + "-" + s2.getNaam());
						moveHistToAnotherPlayer(s1, s2);
						moveHistToAnotherPlayer(s1, s2);
						store(s2);
						delete(s1);
					}
					System.out.println("\n");
				}
			}
		}
		endTransaction();
	}

	/**
	 * If a player exists multiple times in the database, cleanup.
	 * Players are duplicates if
	 * - KNSB is the same
	 */
	private void cleanupDuplicatePlayersEqualKNSB() {
		startTransaction();
		List<DBSpeler> spelers = query("select s from DBSpeler s", DBSpeler.class);
		for (int i = 0; i < spelers.size(); ++i) {
			DBSpeler s1 = spelers.get(i);
			for (int j = i + 1; j < spelers.size(); j++) {
				DBSpeler s2 = spelers.get(j);
				if (s1.getKnsbnummer() == s2.getKnsbnummer()) {
					System.out.println(s1.getAfkorting() + "-" + s1.getKnsbnummer() + "-" + s1.getNaam());
					System.out.println(s2.getAfkorting() + "-" + s2.getKnsbnummer() + "-" + s2.getNaam());
					if (s1.getLaatsteRonde() > s2.getLaatsteRonde()) {
						System.out.println(s1.getAfkorting() + "-" + s1.getKnsbnummer() + "-" + s1.getNaam());
						moveHistToAnotherPlayer(s2, s1);
						store(s1);
						delete(s2);
					} else {
						System.out.println(s2.getAfkorting() + "-" + s2.getKnsbnummer() + "-" + s2.getNaam());
						moveHistToAnotherPlayer(s1, s2);
						moveHistToAnotherPlayer(s1, s2);
						store(s2);
						delete(s1);
					}
					System.out.println("\n");
				}
			}
		}
		endTransaction();
	}

	private void moveHistToAnotherPlayer(DBSpeler from, DBSpeler to) {
		while (!from.getHistorie().isEmpty()) {
			DBHistorie h = from.getHistorie().get(0);
			from.getHistorie().remove(0);
			to.addHistorie(h);
		}
		store(from);
		store(to);
	}

	/**
	 * Als een speler meer historiepunten voor dezelfde ronde heeft, alleen dat
	 * punt houden met het hoogste aantal punten.
	 */
	public void cleanUpMultipleRoundsPerPlayer() {
		startTransaction();
		List<DBSpeler> spelers = query("select s from DBSpeler s", DBSpeler.class);
		for (DBSpeler speler : spelers) {
			ArrayList<Integer> teVerwijderen = new ArrayList<>();
			System.out.println(speler.getKnsbnummer() + " - " + speler.getAfkorting() + " - " + speler.getNaam());
			for (int i = 0; i < speler.getHistorie().size(); ++i) {
				DBHistorie hist = speler.getHistorie().get(i);
				int rid = hist.getRonde().rondeIdentifier();
				System.out.println("  " + rid);
				for (int j = i + 1; j < speler.getHistorie().size(); ++j) {
					DBHistorie hist2 = speler.getHistorie().get(j);
					if ((hist2.getRonde().rondeIdentifier() == rid)) {
						System.out.println("    dubbel");
						teVerwijderen.add(hist.getPunten() > hist2.getPunten() ? j : i);
					}
				}
			}
			for (int i = teVerwijderen.size() - 1; i >= 0; i--) {
				DBHistorie h = speler.getHistorie().get(teVerwijderen.get(i));
				h.setSpeler(null);
				speler.getHistorie().remove(teVerwijderen.get(i));
				System.out.print(" X ");
			}
			store(speler);
		}
		endTransaction();
	}

	/**
	 * Combine two players into one. Leading is the 'to' player and at the end
	 * the 'from' player is delete from the database after copying the history
	 * of the 'from' the from player
	 *
	 * @param fromID
	 * @param toID
	 */
	public void combinePlayers(long fromID, long toID) {
		startTransaction();
		DBSpeler from = querySingleResult("select s from DBSpeler s where s.id=" + fromID, DBSpeler.class);
		DBSpeler to = querySingleResult("select s from DBSpeler s where s.id=80" + toID, DBSpeler.class);
		if (to.getKnsbnummer() < 2000000 && from.getKnsbnummer() > 5000000) {
			to.setKnsbnummer(from.getKnsbnummer());
		}
		moveHistToAnotherPlayer(from, to);
		store(to);
		delete(from);
		endTransaction();
	}

	/**
	 * Converteer uitslag conform TOTO notatie naar Resultaat enumeratie
	 *
	 * @param uitslag
	 * @param wit
	 * @return
	 */
	public Resultaat res(int uitslag, boolean wit) {
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
