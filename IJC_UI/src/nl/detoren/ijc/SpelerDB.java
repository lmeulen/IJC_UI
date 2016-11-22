package nl.detoren.ijc;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nl.detoren.ijc.db.Point;
import nl.detoren.ijc.db.Speler;
import nl.detoren.ijc.db.SpelerHistoriePunt;

public class SpelerDB {

	public static void main(String[] args) {
		// Open a database connection
		// (create a new database if it doesn't exist yet):
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("$objectdb/db/points.odb");
		EntityManager em = emf.createEntityManager();

		// Store 1000 Point objects in the database:
		em.getTransaction().begin();
		for (int i = 0; i < 1000; i++) {
			Point p = new Point(i, i);
			em.persist(p);
		}
		Speler s = new Speler();
		s.setNaam("Leo van der Meulen");
		s.setAfkorting("LM");s.setKnsbnummer(12345678);
		SpelerHistoriePunt hp = new SpelerHistoriePunt();
		hp.setGroep(1);
		hp.setPunten_na(11);
		hp.setPunten_voor(10);
		hp.setRating_voor(200);
		hp.setRating_na(189);
		s.addHistorie(hp);
		em.persist(s);
		
		em.getTransaction().commit();

		// Find the number of Point objects in the database:
		Query q1 = em.createQuery("SELECT COUNT(p) FROM Point p");
		System.out.println("Total Points: " + q1.getSingleResult());

		// Find the average X value:
		Query q2 = em.createQuery("SELECT AVG(p.x) FROM Point p");
		System.out.println("Average X: " + q2.getSingleResult());

		// Retrieve all the Point objects from the database:
		TypedQuery<Point> query = em.createQuery("SELECT p FROM Point p", Point.class);
		List<Point> results = query.getResultList();
		for (Point p : results) {
			System.out.println(p);
		}

		// Close the database connection:
		em.close();
		emf.close();
	}

}
