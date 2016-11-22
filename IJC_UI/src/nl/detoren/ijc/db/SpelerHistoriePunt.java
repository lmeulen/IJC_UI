package nl.detoren.ijc.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class SpelerHistoriePunt {

	@Id
	@GeneratedValue
	private long id;

	@ManyToOne
	@JoinColumn(name = "SPELER_ID", nullable = false)
	Speler speler;
	@ManyToOne
	private Ronde ronde;

	private int rating_voor;
	private int rating_na;
	private int punten_voor;
	private int punten_na;
	private int groep;


	public Speler getSpeler() {
		return speler;
	}
	
	public void setSpeler(Speler s) {
		this.speler = s;
		if (!s.getHistorie().contains(this)) {
			s.getHistorie().add(this);
		}
	}
	
	public Ronde getRonde() {
		return ronde;
	}

	public void setRonde(Ronde ronde) {
		this.ronde = ronde;
	}

	public int getRating_voor() {
		return rating_voor;
	}

	public void setRating_voor(int rating_voor) {
		this.rating_voor = rating_voor;
	}

	public int getRating_na() {
		return rating_na;
	}

	public void setRating_na(int rating_na) {
		this.rating_na = rating_na;
	}

	public int getPunten_voor() {
		return punten_voor;
	}

	public void setPunten_voor(int punten_voor) {
		this.punten_voor = punten_voor;
	}

	public int getPunten_na() {
		return punten_na;
	}

	public void setPunten_na(int punten_na) {
		this.punten_na = punten_na;
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
