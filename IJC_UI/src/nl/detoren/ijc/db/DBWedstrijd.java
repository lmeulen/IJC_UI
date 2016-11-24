package nl.detoren.ijc.db;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class DBWedstrijd {

	@Id
	@GeneratedValue
	private long id;

	@ManyToOne
	@JoinColumn(name = "RONDE_ID", nullable = false)
	private DBRonde ronde;

	@ManyToMany (cascade=CascadeType.ALL)
	private DBSpeler speler;

	private boolean intern;

	@Enumerated(EnumType.STRING)
	private Kleur kleur;

	@Enumerated(EnumType.STRING)
	private Resultaat resultaat;

	@ManyToMany (cascade=CascadeType.ALL)
	private DBSpeler tegenstander;

	public DBWedstrijd() {
		intern = true;
		kleur = Kleur.ONKBEKEND;
		resultaat = Resultaat.ONBEKEND;
	}

	public DBWedstrijd (DBSpeler speler, DBRonde ronde, boolean intern, DBSpeler tegen, Kleur kleur, Resultaat resultaat) {
		this.intern = intern;
		this.kleur = kleur;
		this.resultaat = resultaat;
		this.ronde = ronde;
		this.speler = speler;
		this.tegenstander = tegen;
	}

	public DBWedstrijd (DBSpeler speler, DBRonde ronde, DBSpeler tegen, Kleur kleur, Resultaat resultaat) {
		this.kleur = kleur;
		this.resultaat = resultaat;
		this.ronde = ronde;
		this.speler = speler;
		this.tegenstander = tegen;
	}

	public DBRonde getRonde() {
		return ronde;
	}

	public void setRonde(DBRonde ronde) {
		this.ronde = ronde;
	}

	public DBSpeler getSpeler() {
		return speler;
	}

	public void setSpeler(DBSpeler speler) {
		this.speler = speler;
	}

	public boolean isIntern() {
		return intern;
	}

	public void setIntern(boolean intern) {
		this.intern = intern;
	}

	public Kleur getKleur() {
		return kleur;
	}

	public void setKleur(Kleur kleur) {
		this.kleur = kleur;
	}

	public Resultaat getResultaat() {
		return resultaat;
	}

	public void setResultaat(Resultaat resultaat) {
		this.resultaat = resultaat;
	}

	public DBSpeler getTegenstander() {
		return tegenstander;
	}

	public void setTegenstander(DBSpeler tegenstander) {
		this.tegenstander = tegenstander;
	}

	public Long getId() {
		return id;
	}

}
