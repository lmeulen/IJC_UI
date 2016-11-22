package nl.detoren.ijc.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Speler {

	@Id
	@GeneratedValue
	private long id;

	private String naam;
	private String afkorting;
	private int knsbnummer;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "speler")
	List<SpelerHistoriePunt> historie;
	
	public Speler() {
		historie = new ArrayList<>(); 
	}

	public List<SpelerHistoriePunt> getHistorie() {
		return historie;
	}
	
	public void addHistorie(SpelerHistoriePunt punt) {
		this.historie.add(punt);
		if (punt.speler != this) {
			punt.setSpeler(this);
		}
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
