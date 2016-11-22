package nl.detoren.ijc.db;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

enum Kleur {WIT, ZWART, ONKBEKEND};
enum Resultaat {WINST, REMISE, VERLIES, ONBEKEND};

@Entity
public class Wedstrijd {

	@Id
	@GeneratedValue
	private long id;
	@ManyToOne
	Ronde ronde;
	@ManyToMany
	Speler speler;
	private boolean intern;
	@Enumerated(EnumType.STRING)
	private Kleur kleur;
	@Enumerated(EnumType.STRING)
	private Resultaat resultaat;
	@ManyToOne
	private Speler tegenstander;
	
	public Ronde getRonde() {
		return ronde;
	}
	public void setRonde(Ronde ronde) {
		this.ronde = ronde;
	}
	public Speler getSpeler() {
		return speler;
	}
	public void setSpeler(Speler speler) {
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
	public Speler getTegenstander() {
		return tegenstander;
	}
	public void setTegenstander(Speler tegenstander) {
		this.tegenstander = tegenstander;
	}
	public Long getId() {
		return id;
	}
	
	
}

