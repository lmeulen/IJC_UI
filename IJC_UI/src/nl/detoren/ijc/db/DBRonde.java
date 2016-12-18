package nl.detoren.ijc.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class DBRonde {

	@Id
	@GeneratedValue
	private long id;

	@OneToOne (cascade = CascadeType.ALL)
	private Date date;
	private int seizoen;
	private int periode;
	private int ronde;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "ronde")
	List<DBWedstrijd> wedstrijden;

	public DBRonde() {
		wedstrijden = new ArrayList<>();
	}

	public DBRonde(Date datum, int seizoen, int periode, int ronde) {
		this();
		this.date = datum;
		this.periode = periode;
		this.ronde = ronde;
		this.seizoen = seizoen;
	}

	public List<DBWedstrijd> getWedstrijden() {
		return wedstrijden;
	}

	public void addWedstrijd(DBWedstrijd wedstrijd) {
		this.wedstrijden.add(wedstrijd);
		if (wedstrijd.getRonde() != this) {
			wedstrijd.setRonde(this);
		}
	}

	public Long getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getSeizoen() {
		return seizoen;
	}

	public void setSeizoen(int seizoen) {
		this.seizoen = seizoen;
	}

	public int getPeriode() {
		return periode;
	}

	public void setPeriode(int periode) {
		this.periode = periode;
	}

	public int getRonde() {
		return ronde;
	}

	public void setRonde(int ronde) {
		this.ronde = ronde;
	}

	public int rondeIdentifier() {
		return (seizoen*100) + (periode*10) + ronde;
	}

	public String getRondeNaam() {
		return "" + seizoen + "-P" + periode + "R" + ronde;
	}
}
