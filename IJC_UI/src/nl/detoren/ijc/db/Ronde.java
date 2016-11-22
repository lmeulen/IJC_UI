package nl.detoren.ijc.db;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Ronde {

	@Id
	@GeneratedValue
	private long id;

	private Date date;
	private String seizoen;
	private int periode;
	private int ronde;

	public Long getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSeizoen() {
		return seizoen;
	}

	public void setSeizoen(String seizoen) {
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

}
