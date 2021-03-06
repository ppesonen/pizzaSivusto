package fi.softala.pizzeria.bean;

import java.util.ArrayList;

public class Pizza {

	int id;
	String nimi;
	double hinta;
	ArrayList<Tayte> taytteet;
	String poistomerkinta;
	ArrayList<String> tayteIdt;
	String kuvaus;
	ArrayList<String> lisatiedot;
	int suosikkiid;

	public Pizza() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Pizza(int id, String nimi, double hinta, ArrayList<Tayte> taytteet, String poistomerkinta,
			ArrayList<String> tayteIdt, String kuvaus) {
		super();
		this.id = id;
		this.nimi = nimi;
		this.hinta = hinta;
		this.taytteet = taytteet;
		this.poistomerkinta = poistomerkinta;
		this.tayteIdt = tayteIdt;
		this.kuvaus = kuvaus;
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

	public ArrayList<Tayte> getTaytteet() {
		return taytteet;
	}

	public void setTaytteet(ArrayList<Tayte> taytteet) {
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

	public String getKuvaus() {
		return kuvaus;
	}

	public void setKuvaus(String kuvaus) {
		this.kuvaus = kuvaus;
	}

	public ArrayList<String> getLisatiedot() {
		return lisatiedot;
	}

	public void setLisatiedot(ArrayList<String> lisatiedot) {
		this.lisatiedot = lisatiedot;
	}

	public int getSuosikkiid() {
		return suosikkiid;
	}

	public void setSuosikkiid(int suosikkiid) {
		this.suosikkiid = suosikkiid;
	}

	@Override
	public String toString() {
		return "Pizza [id=" + id + ", nimi=" + nimi + ", hinta=" + hinta + ", taytteet=" + taytteet
				+ ", poistomerkinta=" + poistomerkinta + ", tayteIdt=" + tayteIdt + ", kuvaus=" + kuvaus
				+ ", lisatiedot=" + lisatiedot + "]";
	}

}