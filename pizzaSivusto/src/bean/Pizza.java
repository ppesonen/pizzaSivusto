package bean;

import java.util.ArrayList;

public class Pizza {

	int id;
	String nimi;
	double hinta;
	String taytteet;
	String poistomerkinta;
	ArrayList<String> tayteIdt;

	public Pizza() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Pizza(int id, String nimi, double hinta, String taytteet, String poistomerkinta,
			ArrayList<String> tayteIdt) {
		super();
		this.id = id;
		this.nimi = nimi;
		this.hinta = hinta;
		this.taytteet = taytteet;
		this.poistomerkinta = poistomerkinta;
		this.tayteIdt = tayteIdt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNimi() {
		return nimi;
	}

	public void setNimi(String nimi) {
		this.nimi = nimi;
	}

	public double getHinta() {
		return hinta;
	}

	public void setHinta(double hinta) {
		this.hinta = hinta;
	}

	public String getTaytteet() {
		return taytteet;
	}

	public void setTaytteet(String taytteet) {
		this.taytteet = taytteet;
	}

	public String getPoistomerkinta() {
		return poistomerkinta;
	}

	public void setPoistomerkinta(String poistomerkinta) {
		this.poistomerkinta = poistomerkinta;
	}

	public ArrayList<String> getTayteIdt() {
		return tayteIdt;
	}

	public void setTayteIdt(ArrayList<String> tayteIdt) {
		this.tayteIdt = tayteIdt;
	}

}
