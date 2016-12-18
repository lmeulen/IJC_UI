package nl.detoren.ijc.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class ObjectDatabase {

	private String databaseURL;
	private EntityManagerFactory emFactory;
	private EntityManager entityManager;

	@SuppressWarnings("unused")
	private ObjectDatabase() {
		this.databaseURL = "";
	}

	public ObjectDatabase(String url) {
		super();
		this.databaseURL = url;
	}

	/**
	 * Open de spelers database
	 */
	public void openDatabase() {
		emFactory = Persistence.createEntityManagerFactory(databaseURL);
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
	public void endTransaction() {
		entityManager.getTransaction().commit();
	}

	/**
	 * Sla een object op in de database
	 *
	 * @param o
	 */
	public void store(Object o) {
		if (o != null)
			entityManager.persist(o);
	}

	/**
	 * Delete an object from the database
	 * @param o
	 */
	public void delete(Object o) {
		if (o != null)
			entityManager.remove(o);
	}

	/**
	 * Voer een query uit die een enkel resultaat oplevert
	 *
	 * @param query
	 * @return
	 */
	public Object querySingleResult(String query) {
		if (query != null) {
			return entityManager.createQuery(query).getSingleResult();
		} else {
			return null;
		}
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
		if (query != null && type != null) {
			List<T> results = entityManager.createQuery(query, type).getResultList();
			if (results.isEmpty())
				return null;
			return results.get(0);
		}
		return null;
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
		if (query != null && type != null) {
			return entityManager.createQuery(query, type).getResultList();
		} else {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public List query(String query) {
		if (query != null) {
			return entityManager.createQuery(query).getResultList();
		} else {
			return null;
		}
	}

}