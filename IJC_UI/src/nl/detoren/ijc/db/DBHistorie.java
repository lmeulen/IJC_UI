package nl.detoren.ijc.db;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class DBHistorie {

	@Id
	@GeneratedValue
	private long id;

	@ManyToOne
	@JoinColumn(name = "SPELER_ID", nullable = false)
	private DBSpeler speler;

	@ManyToOne (cascade = CascadeType.ALL)
	private DBRonde ronde;

	private int rating;
	private int punten;
	private int groep;

	public DBHistorie() {
	}

	public DBHistorie(DBRonde ronde, int groep, int rating, int punten) {
		this.groep = groep;
		this.punten = punten;
		this.rating = rating;
		this.ronde = ronde;
	}

	public DBSpeler getSpeler() {
		return speler;
	}

	public void setSpeler(DBSpeler s) {
		this.speler = s;
		if (!s.getHistorie().contains(this)) {
			s.getHistorie().add(this);
		}
	}

	public DBRonde getRonde() {
		return ronde;
	}

	public void setRonde(DBRonde ronde) {
		this.ronde = ronde;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public int getPunten() {
		return punten;
	}

	public void setPunten(int punten) {
		this.punten = punten;
	}

	public int getGroep() {
		return groep;
	}

	public void setGroep(int groep) {
		this.groep = groep;
	}

	public Long getId() {
		return id;
	}

}
