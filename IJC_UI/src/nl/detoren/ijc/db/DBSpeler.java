package nl.detoren.ijc.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class DBSpeler {

	@Id
	@GeneratedValue
	private long id;

	private String naam;
	private String afkorting;
	private int knsbnummer;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = false, mappedBy = "speler")
	List<DBHistorie> historie;

	public DBSpeler() {
		historie = new ArrayList<>();
	}

	public DBSpeler(String naam, String afkorting, int knsbnummer) {
		this();
		this.afkorting = afkorting;
		this.knsbnummer = knsbnummer;
		this.naam = naam;
	}

	public String toString() {
		return afkorting + " - " + knsbnummer + " - " + naam + ", hist.points : " + historie.size();
	}

	public List<DBHistorie> getHistorie() {
		return historie;
	}

	public void addHistorie(DBHistorie punt) {
		this.historie.add(punt);
		punt.setSpeler(this);
	}

	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public String getAfkorting() {
		return afkorting;
	}

	public void setAfkorting(String afkorting) {
		this.afkorting = afkorting;
	}

	public int getKnsbnummer() {
		return knsbnummer;
	}

	public void setKnsbnummer(int knsbnummer) {
		this.knsbnummer = knsbnummer;
	}

	public Long getId() {
		return id;
	}

}
