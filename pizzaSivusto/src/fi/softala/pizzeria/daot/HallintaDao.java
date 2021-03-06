package fi.softala.pizzeria.daot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import fi.softala.pizzeria.bean.Aukioloaika;
import fi.softala.pizzeria.bean.Juoma;
import fi.softala.pizzeria.bean.Pizza;
import fi.softala.pizzeria.bean.Tayte;
import fi.softala.pizzeria.tietokanta.Kysely;
import fi.softala.pizzeria.tietokanta.Paivitys;
import fi.softala.pizzeria.tietokanta.Yhteys;

public class HallintaDao {

	// Tyyppi = 1 ja id pitää määrittää vain, jos haluaa noutaa
	// pizzat joissa on joku tietty täyte
	// Muussa tapauksessa kelpaa mitkä tahansa arvot
	public ArrayList<Pizza> haeKaikkiPizzat(int tyyppi, String tayteId) {
		ArrayList<Pizza> pizzat = new ArrayList<>();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		if (yhteys.getYhteys() == null) {
			return pizzat;
		}
		Kysely kysely = new Kysely(yhteys.getYhteys());

		ArrayList<String> parametrit = new ArrayList<>();

		String sql = "SELECT pizza_id, p.nimi AS pizza, kuvaus, hinta, t.nimi AS tayte, p.poistomerkinta, tayte_id, t.saatavilla FROM PizzanTayte pt JOIN Pizza p USING(pizza_id) JOIN Tayte t USING(tayte_id)";

		if (tyyppi == 1) {
			sql += " WHERE EXISTS (SELECT * FROM PizzanTayte pt2 WHERE pt2.tayte_id = ? AND pt.pizza_id = pt2.pizza_id)";
			parametrit.add(tayteId);
		}

		sql += " ORDER BY hinta ASC";

		kysely.suoritaYksiKyselyParam(sql, parametrit);
		ArrayList<HashMap<String, String>> tulokset = kysely.getTulokset();

		// Iteraattorin luonti
		Iterator iteraattori = kysely.getTulokset().iterator();

		while (iteraattori.hasNext()) {
			HashMap pizzaMappi = (HashMap) iteraattori.next();
			String idString = (String) pizzaMappi.get("pizza_id");
			String nimikanta = (String) pizzaMappi.get("pizza");
			String hintaString = (String) pizzaMappi.get("hinta");
			String tayteKanta = (String) pizzaMappi.get("tayte");
			String tayteIdKanta = (String) pizzaMappi.get("tayte_id");
			String tayteSaatavilla = (String) pizzaMappi.get("saatavilla");
			String poistoKanta = (String) pizzaMappi.get("poistomerkinta");
			String kuvausKanta = (String) pizzaMappi.get("kuvaus");
			int idKanta = Integer.parseInt(idString);
			double hintaKanta = Double.parseDouble(hintaString);

			Tayte tayte = new Tayte();

			// Täyte-oliolle tiedot
			tayte.setId(Integer.parseInt(tayteIdKanta));
			tayte.setNimi(tayteKanta);
			if (tayteSaatavilla.equals("K")) {
				tayte.setSaatavilla(true);
			} else if (tayteSaatavilla.equals("E")) {
				tayte.setSaatavilla(false);
			} else {
				System.out.println("Tuntematon saatavilla-arvo: "
						+ tayteSaatavilla + " - Asetetaan false.");
				tayte.setSaatavilla(false);
			}

			// Katsotaan, onko pizza jo listalla
			// Jos on, lisätään siihen täyte
			// Jos ei, luodaan uusi pizza
			boolean pizzaloyty = false;

			for (int i = 0; i < pizzat.size(); i++) {
				if (pizzat.get(i).getId() == idKanta) {
					ArrayList<Tayte> taytteet = pizzat.get(i).getTaytteet();
					taytteet.add(tayte);
					pizzat.get(i).setTaytteet(taytteet);
					pizzaloyty = true;
				}
			}

			if (pizzaloyty == false) {
				ArrayList<Tayte> taytteet = new ArrayList<>();
				taytteet.add(tayte);
				Pizza pizza = new Pizza(idKanta, nimikanta, hintaKanta,
						taytteet, poistoKanta, null, kuvausKanta);
				pizzat.add(pizza);
			}
		}

		// Yhteyden sulkeminen
		yhteys.suljeYhteys();

		// Pizzojen palautus
		return pizzat;

	}

	public Tayte haeTayte(String id) {

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		Kysely kysely = new Kysely(yhteys.getYhteys());

		String sql = "SELECT * FROM Tayte WHERE tayte_id = ?";
		ArrayList<String> parametrit = new ArrayList<>();
		parametrit.add(id);

		Tayte tayte = new Tayte();

		// Katsotaan, onko täytettä olemassa
		if (kysely.montaRivia(sql, parametrit) != 1) {
			System.out
					.println("Virhe! Täytettä ei ole tietokannassa, tai duplicate id");
			return tayte;
		}

		// Suoritetaan itse kysely
		kysely.suoritaYksiKyselyParam(sql, parametrit);
		ArrayList<HashMap<String, String>> tulokset = kysely.getTulokset();

		// Iteraattorin luonti
		Iterator iteraattori = kysely.getTulokset().iterator();

		// Tulosten läpi käynti
		while (iteraattori.hasNext()) {
			HashMap tayteMappi = (HashMap) iteraattori.next();
			String tayteid = (String) tayteMappi.get("tayte_id");
			String nimikanta = (String) tayteMappi.get("nimi");
			String saatavilla = (String) tayteMappi.get("saatavilla");

			// Oliolle tiedot
			tayte.setId(Integer.parseInt(tayteid));
			tayte.setNimi(nimikanta);
			if (saatavilla.equals("K")) {
				tayte.setSaatavilla(true);
			} else if (saatavilla.equals("E")) {
				tayte.setSaatavilla(false);
			} else {
				System.out.println("Tuntematon saatavilla-arvo: " + saatavilla
						+ " - Asetetaan false.");
				tayte.setSaatavilla(false);
			}

			System.out.println("Olion tiedot: " + tayte.getId() + " - "
					+ tayte.getNimi() + " - " + tayte.getSaatavilla());

		}

		return tayte;
	}

	public Pizza haePizza(String id) {

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		if (yhteys.getYhteys() == null) {
			return null;
		}
		Kysely kysely = new Kysely(yhteys.getYhteys());

		String sql = "SELECT pizza_id, p.nimi AS pizza, kuvaus, hinta, tayte_id, t.nimi AS tayte, saatavilla, p.poistomerkinta FROM PizzanTayte pt JOIN Pizza p USING(pizza_id) JOIN Tayte t USING(tayte_id) WHERE pizza_id = ?";
		ArrayList<String> parametrit = new ArrayList<>();
		parametrit.add(id);

		Pizza pizza = new Pizza();

		if (kysely.montaRivia(sql, parametrit) < 1) {
			System.out
					.println("Virhe! Muokattavaksi haluttua pizzaa ei ole tietokannassa.");
			return pizza;
		}

		kysely.suoritaYksiKyselyParam(sql, parametrit);
		ArrayList<HashMap<String, String>> tulokset = kysely.getTulokset();

		// Iteraattorin luonti
		Iterator iteraattori = kysely.getTulokset().iterator();

		// Hashmap jossa pizzan id ja pizzaolio
		// Pointtina tässä on yhdistää yhteen pizzaolioon jokainen rivi jossa
		// sama pizza_id
		// Kaikki täytteet menevät 'taytteet' Stringiin pilkuilla eroteltuina

		int looppeja = 0;
		ArrayList<String> taytteet = new ArrayList<>();

		while (iteraattori.hasNext()) {
			HashMap pizzaMappi = (HashMap) iteraattori.next();
			String idString = (String) pizzaMappi.get("pizza_id");
			String nimikanta = (String) pizzaMappi.get("pizza");
			String hintaString = (String) pizzaMappi.get("hinta");
			String tayteKanta = (String) pizzaMappi.get("tayte");
			String tayteId = (String) pizzaMappi.get("tayte_id");
			String poistoKanta = (String) pizzaMappi.get("poistomerkinta");
			String tayteSaatavilla = (String) pizzaMappi.get("saatavilla");
			String kuvausKanta = (String) pizzaMappi.get("kuvaus");
			int idKanta = Integer.parseInt(idString);
			double hintaKanta = Double.parseDouble(hintaString);

			taytteet.add(tayteId);

			Tayte tayte = new Tayte();

			// Täyte-oliolle tiedot
			tayte.setId(Integer.parseInt(tayteId));
			tayte.setNimi(tayteKanta);
			if (tayteSaatavilla.equals("K")) {
				tayte.setSaatavilla(true);
			} else if (tayteSaatavilla.equals("E")) {
				tayte.setSaatavilla(false);
			} else {
				System.out.println("Tuntematon saatavilla-arvo: "
						+ tayteSaatavilla + " - Asetetaan false.");
				tayte.setSaatavilla(false);
			}

			if (looppeja == 0) {
				ArrayList<Tayte> taytelista = new ArrayList<>();
				taytelista.add(tayte);
				pizza = new Pizza(idKanta, nimikanta, hintaKanta, taytelista,
						poistoKanta, null, kuvausKanta);
			} else {
				ArrayList<Tayte> taytelista = pizza.getTaytteet();
				taytelista.add(tayte);
				pizza.setTaytteet(taytelista);
			}

			looppeja++;

		}

		pizza.setTayteIdt(taytteet);

		// Yhteyden sulkeminen
		yhteys.suljeYhteys();

		// Pizzojen palautus
		return pizza;

	}

	public ArrayList<Tayte> haeKaikkiTaytteet() {
		ArrayList<Tayte> taytteet = new ArrayList<>();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		if (yhteys.getYhteys() == null) {
			return taytteet;
		}
		Kysely kysely = new Kysely(yhteys.getYhteys());

		String sql = "SELECT tayte_id, nimi, saatavilla FROM Tayte ORDER BY nimi ASC";

		kysely.suoritaKysely(sql);
		ArrayList<HashMap<String, String>> tulokset = kysely.getTulokset();

		// Iteraattorin luonti
		Iterator iteraattori = kysely.getTulokset().iterator();

		while (iteraattori.hasNext()) {
			HashMap kayttajaMappi = (HashMap) iteraattori.next();
			String idString = (String) kayttajaMappi.get("tayte_id");
			String nimikanta = (String) kayttajaMappi.get("nimi");
			String saatavillaKanta = (String) kayttajaMappi.get("saatavilla");
			int idKanta = Integer.parseInt(idString);

			// Oliolle attribuutit
			Tayte tayte = new Tayte();

			tayte.setId(idKanta);
			tayte.setNimi(nimikanta);
			if (saatavillaKanta.equals("K")) {
				tayte.setSaatavilla(true);
			} else if (saatavillaKanta.equals("E")) {
				tayte.setSaatavilla(false);
			} else {
				System.out.println("Täytteellä ID" + idKanta + " (" + nimikanta
						+ ") virheellinen saatavuus: '" + saatavillaKanta
						+ "', asetetaan false");
				tayte.setSaatavilla(false);
			}

			taytteet.add(tayte);

		}

		// Yhteyden sulkeminen
		yhteys.suljeYhteys();

		return taytteet;

	}

	public HashMap<String, String> lisaaPizza(String nimi, String kuvaus,
			String hinta, String[] taytteet) {

		HashMap<String, String> vastaus = new HashMap<>();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		if (yhteys.getYhteys() == null) {
			String virhe = "Tietokantayhteyttä ei saatu avattua";
			vastaus.put("virhe", virhe);
			return vastaus;
		}
		Kysely kysely = new Kysely(yhteys.getYhteys());

		// Katsotaan ensin, että samannimistä pizzaa ei ole, ja että kaikki
		// täytteet ovat tietokannassa
		String sql = "SELECT nimi FROM Pizza WHERE nimi = ?";
		ArrayList<String> parametrit = new ArrayList<String>();
		parametrit.add(nimi);

		if (kysely.montaRivia(sql, parametrit) > 0) {
			String virhe = "Saman niminen pizza on jo tietokannassa.";
			vastaus.put("virhe", virhe);
			return vastaus;
		} else {
			// Katsotaan, että kaikki täytteet ovat tietokannassa
			sql = "SELECT tayte_id FROM Tayte WHERE tayte_id = ?";
			parametrit.clear();
			parametrit.add(taytteet[0]);
			if (taytteet.length > 1) {
				for (int i = 1; i < taytteet.length; i++) {
					sql += " OR tayte_id = ?";
					parametrit.add(taytteet[i]);
				}
			}
			if (kysely.montaRivia(sql, parametrit) < taytteet.length) {
				String virhe = "Kaikkia täytteitä ei ole tietokannassa, tai on valittu kaksi samaa täytettä.";
				vastaus.put("virhe", virhe);
				return vastaus;
			}
		}

		// Itse pizzan lisäys Pizza-taulukkoon
		sql = "INSERT INTO Pizza VALUES (null, ?, ?, null, ?)";
		Paivitys paivitys = new Paivitys(yhteys.getYhteys());

		parametrit.clear();
		parametrit.add(nimi);
		parametrit.add(hinta);
		parametrit.add(kuvaus);

		// Palauttaa onnistuneiden rivien määrän, 1 = ok, 0 = error
		int pizzasuccess = paivitys.suoritaSqlLauseParametreilla(sql,
				parametrit);

		System.out.println("Pizzan lisäys kantaan palautti: " + pizzasuccess);

		/*
		 * Lisätään pizzan täytteet PizzanTayte-taulukkoon Ei tiedetä vasta
		 * lisätyn pizzan pizza_id:tä, koska se määräytyy tietokannan puolella
		 * Käytetään täytteiden lisäyksessä pizzan nimeä. Oletetaan että ei
		 * duplicateja. Pitäis tehdä checki. Oletetaan myös, että tayte_id:t on
		 * valideja.
		 */
		sql = "INSERT INTO PizzanTayte VALUES ((SELECT pizza_id FROM Pizza WHERE nimi = ?), ?)";
		parametrit.clear();
		parametrit.add(nimi);
		parametrit.add(taytteet[0]);

		if (taytteet.length > 1) {
			for (int i = 1; i < taytteet.length; i++) {
				sql += ", ((SELECT pizza_id FROM Pizza WHERE nimi = ?), ?)";
				parametrit.add(nimi);
				parametrit.add(taytteet[i]);
			}
		}

		// Palauttaa onnistuneiden määrän, pitäisi olla sama kuin täytteiden
		// määrä
		// Pienempi arvo = error
		int taytesuccess = paivitys.suoritaSqlLauseParametreilla(sql,
				parametrit);

		System.out.println("Täytteiden määrä = " + taytteet.length
				+ ", lisäys kantaan palautti: " + taytesuccess);

		// Yhteyden sulkeminen
		yhteys.suljeYhteys();

		// Palautetaan true jos kaikki onnistui, false jos kaikki ei onnistunut
		if (pizzasuccess == 1 && taytesuccess == taytteet.length) {
			String success = "Pizza lisätty tietokantaan onnistuneesti!";
			vastaus.put("success", success);
			return vastaus;
		} else {
			String virhe = "Tietokantaan lisätessä tapahtui virhe.";
			vastaus.put("virhe", virhe);
			return vastaus;
		}

	}

	public HashMap<String, String> lisaaJuoma(String nimi, String koko,
			String hinta, String kuvaus, String saatavilla) {
		HashMap<String, String> vastaus = new HashMap<>();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		if (yhteys.getYhteys() == null) {
			String virhe = "Tietokantayhteyttä ei saatu avattua";
			vastaus.put("virhe", virhe);
			return vastaus;
		}
		Kysely kysely = new Kysely(yhteys.getYhteys());
		Paivitys paivitys = new Paivitys(yhteys.getYhteys());

		// Katsotaan ensin, että saman nimistä juomaa ei ole
		String sql = "SELECT nimi FROM Juoma WHERE nimi = ?";
		ArrayList<String> parametrit = new ArrayList<String>();
		parametrit.add(nimi);

		if (kysely.montaRivia(sql, parametrit) > 0) {
			String virhe = "Saman niminen juoma on jo tietokannassa";
			vastaus.put("virhe", virhe);
			yhteys.suljeYhteys();
			return vastaus;
		}

		// Lisätään juoma
		sql = "INSERT INTO Juoma VALUES (null, ?, ?, ?, ?, ?, null)";
		parametrit.add(hinta);
		parametrit.add(koko);
		parametrit.add(kuvaus);
		parametrit.add(saatavilla);

		// Palauttaa onnistuneiden rivien määrän, 1 = ok, 0 = error
		int rivit = paivitys.suoritaSqlLauseParametreilla(sql, parametrit);

		if (rivit == 1) {
			String success = "Juoma lisätty tietokantaan onnistuneesti!";
			vastaus.put("success", success);
			yhteys.suljeYhteys();
			return vastaus;
		} else {
			String virhe = "Juomaa lisätessä tietokantaan tapahtui virhe";
			vastaus.put("virhe", virhe);
			yhteys.suljeYhteys();
			return vastaus;
		}

	}

	public HashMap<String, String> paivitaJuoma(String id, String nimi,
			String koko, String hinta, String kuvaus, String saatavilla) {
		HashMap<String, String> vastaus = new HashMap<>();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		if (yhteys.getYhteys() == null) {
			String virhe = "Tietokantayhteyttä ei saatu avattua";
			vastaus.put("virhe", virhe);
			return vastaus;
		}
		Kysely kysely = new Kysely(yhteys.getYhteys());
		Paivitys paivitys = new Paivitys(yhteys.getYhteys());

		// Katsotaan, että juoma löytyy tietokannasta
		String sql = "SELECT juoma_id FROM Juoma WHERE juoma_id = ?";
		ArrayList<String> parametrit = new ArrayList<String>();
		parametrit.add(id);

		if (kysely.montaRivia(sql, parametrit) == 0) {
			String virhe = "Juomaa ei löydy tietokannasta";
			vastaus.put("virhe", virhe);
			yhteys.suljeYhteys();
			return vastaus;
		}

		// Katsotaan että saman nimistä juomaa ei ole tietokannassa
		sql = "SELECT nimi FROM Juoma WHERE juoma_id != ? AND nimi = ?";
		parametrit.add(nimi);

		if (kysely.montaRivia(sql, parametrit) > 0) {
			String virhe = "Saman niminen juoma on jo tietokannassa";
			vastaus.put("virhe", virhe);
			yhteys.suljeYhteys();
			return vastaus;
		}

		// Päivitetään juoman tiedot
		sql = "UPDATE Juoma SET nimi = ?, hinta = ?, koko = ?, kuvaus = ?, saatavilla = ? WHERE juoma_id = ?";
		parametrit.clear();
		parametrit.add(nimi);
		parametrit.add(hinta);
		parametrit.add(koko);
		parametrit.add(kuvaus);
		parametrit.add(saatavilla);
		parametrit.add(id);

		// Palauttaa onnistuneiden rivien määrän, 1 = ok, 0 = error
		int rivit = paivitys.suoritaSqlLauseParametreilla(sql, parametrit);

		if (rivit == 1) {
			String success = "Juoman tiedot päivitetty";
			vastaus.put("success", success);
			yhteys.suljeYhteys();
			return vastaus;
		} else {
			String virhe = "Juomaa päivittäess tapahtui virhe";
			vastaus.put("virhe", virhe);
			yhteys.suljeYhteys();
			return vastaus;
		}

	}

	public HashMap<String, String> paivitaTayte(String id, String nimi,
			String saatavilla) {

		HashMap<String, String> vastaus = new HashMap<>();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		if (yhteys.getYhteys() == null) {
			String virhe = "Tietokantayhteyttä ei saatu avattua";
			vastaus.put("virhe", virhe);
			return vastaus;
		}
		Kysely kysely = new Kysely(yhteys.getYhteys());

		// Katsotaan, että kyseinen tayte_id on tietokannassa
		String sql = "SELECT tayte_id FROM Tayte WHERE tayte_id = ?";
		ArrayList<String> parametrit = new ArrayList<String>();
		parametrit.add(id);

		if (kysely.montaRivia(sql, parametrit) == 0) {
			String virhe = "Täytettä ei löydy tietokannasta";
			vastaus.put("virhe", virhe);
			return vastaus;
		}

		// Katsotaan, että muilla täytteillä ei ole samaa nimeä
		sql = "SELECT nimi FROM Tayte WHERE nimi = ? AND tayte_id != ?";
		parametrit.clear();
		parametrit.add(nimi);
		parametrit.add(id);

		if (kysely.montaRivia(sql, parametrit) > 0) {
			String virhe = "Saman niminen täyte on jo tietokannassa";
			vastaus.put("virhe", virhe);
			return vastaus;
		}

		sql = "UPDATE Tayte SET nimi = ?, saatavilla = ? WHERE tayte_id = ?";
		Paivitys paivitys = new Paivitys(yhteys.getYhteys());

		parametrit.clear();
		parametrit.add(nimi);
		parametrit.add(saatavilla);
		parametrit.add(id);

		// Palauttaa onnistuneiden rivien määrän, 1 = ok, 0 = error
		int rivit = paivitys.suoritaSqlLauseParametreilla(sql, parametrit);

		System.out.println("Täytteen päivitys palautti " + paivitys);

		if (rivit == 1) {
			String success = "Täytteen tiedot päivitetty!";
			vastaus.put("success", success);
			return vastaus;
		} else {
			String virhe = "Täytteen päivitys tietokantaan ei onnistunut";
			vastaus.put("virhe", virhe);
			return vastaus;
		}
	}

	public HashMap<String, String> paivitaPizza(String id, String nimi,
			String kuvaus, String hinta, String[] taytteet) {
		HashMap<String, String> vastaus = new HashMap<>();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		Kysely kysely = new Kysely(yhteys.getYhteys());

		// Katsotaan, että kyseinen pizza_id on tietokannassa
		String sql = "SELECT pizza_id FROM Pizza WHERE pizza_id = ?";
		ArrayList<String> parametrit = new ArrayList<String>();
		parametrit.add(id);

		if (kysely.montaRivia(sql, parametrit) == 0) {
			String virhe = "Valittua pizzaa ei löydy tietokannasta";
			vastaus.put("virhe", virhe);
			return vastaus;
		}

		// Katsotaan, että muilla pizzoilla ei ole samaa nimeä
		sql = "SELECT nimi FROM Pizza WHERE nimi = ? AND pizza_id != ?";
		parametrit.clear();
		parametrit.add(nimi);
		parametrit.add(id);

		if (kysely.montaRivia(sql, parametrit) > 0) {
			String virhe = "Saman niminen pizza on jo tietokannassa";
			vastaus.put("virhe", virhe);
			return vastaus;
		} else {
			// Katsotaan, että kaikki täytteet ovat tietokannassa
			sql = "SELECT tayte_id FROM Tayte WHERE tayte_id = ?";
			parametrit.clear();
			parametrit.add(taytteet[0]);
			if (taytteet.length > 1) {
				for (int i = 1; i < taytteet.length; i++) {
					sql += " OR tayte_id = ?";
					parametrit.add(taytteet[i]);
				}
			}
			if (kysely.montaRivia(sql, parametrit) < taytteet.length) {
				String virhe = "Kaikkia täytteitä ei ole tietokannassa, tai on valittu kaksi samaa täytettä";
				vastaus.put("virhe", virhe);
				return vastaus;
			}
		}

		// Itse pizzan tietojen päivitys
		sql = "UPDATE Pizza SET nimi = ?, kuvaus = ?, hinta = ? WHERE pizza_id = ?";
		Paivitys paivitys = new Paivitys(yhteys.getYhteys());

		parametrit.clear();
		parametrit.add(nimi);
		parametrit.add(kuvaus);
		parametrit.add(hinta);
		parametrit.add(id);

		// Palauttaa onnistuneiden rivien määrän, 1 = ok, 0 = error
		int pizzasuccess = paivitys.suoritaSqlLauseParametreilla(sql,
				parametrit);

		System.out.println("Pizzan päivitys kantaan palautti: " + pizzasuccess);

		// Poistetaan kokonaan vanhat täytteet kokonaan
		sql = "DELETE FROM PizzanTayte WHERE pizza_id = ?";
		parametrit.clear();
		parametrit.add(id);
		int deletesuccess = paivitys.suoritaSqlLauseParametreilla(sql,
				parametrit);

		System.out.println("Vanhojen täytteiden poisto palautti "
				+ deletesuccess);

		// Lisätään uudet täytteet
		sql = "INSERT INTO PizzanTayte VALUES ((SELECT pizza_id FROM Pizza WHERE nimi = ?), ?)";
		parametrit.clear();
		parametrit.add(nimi);
		parametrit.add(taytteet[0]);

		if (taytteet.length > 1) {
			for (int i = 1; i < taytteet.length; i++) {
				sql += ", ((SELECT pizza_id FROM Pizza WHERE nimi = ?), ?)";
				parametrit.add(nimi);
				parametrit.add(taytteet[i]);
			}
		}

		// Palauttaa onnistuneiden määrän, pitäisi olla sama kuin täytteiden
		// määrä
		// Pienempi arvo = error
		int taytesuccess = paivitys.suoritaSqlLauseParametreilla(sql,
				parametrit);

		System.out.println("Täytteiden määrä = " + taytteet.length
				+ ", lisäys kantaan palautti: " + taytesuccess);

		// Yhteyden sulkeminen
		yhteys.suljeYhteys();

		// Palautetaan true jos kaikki onnistui, false jos kaikki ei onnistunut
		if (pizzasuccess == 1 && taytesuccess == taytteet.length) {
			String success = "Pizzan tiedot päivitetty!";
			vastaus.put("success", success);
			return vastaus;
		} else {
			String virhe = "Tietokantaa päivittäessä tapahtui virhe!";
			vastaus.put("virhe", virhe);
			return vastaus;
		}

	}

	public HashMap<String, String> poistaPizza(String id) {
		HashMap<String, String> vastaus = new HashMap<>();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		if (yhteys.getYhteys() == null) {
			String virhe = "Tietokantayhteyttä ei saatu avattua";
			vastaus.put("virhe", virhe);
			yhteys.suljeYhteys();
			return vastaus;
		}
		Kysely kysely = new Kysely(yhteys.getYhteys());

		// Määritellään lause ja parametrit
		String sql = "SELECT pizza_id FROM Pizza WHERE pizza_id = ?";
		ArrayList<String> parametrit = new ArrayList<String>();
		parametrit.add(id);

		if (kysely.montaRivia(sql, parametrit) < 1) {
			String virhe = "Poistettavaa pizzaa ei löydy tietokannasta";
			vastaus.put("virhe", virhe);
			return vastaus;
		}

		// Määritellään lause ja poistetaan pizza
		sql = "UPDATE Pizza SET poistomerkinta = NOW() WHERE pizza_id = ?";
		Paivitys paivitys = new Paivitys(yhteys.getYhteys());
		int rivit = paivitys.suoritaSqlLauseParametreilla(sql, parametrit);

		// Yhteyden sulkeminen
		yhteys.suljeYhteys();

		if (rivit != 1) {
			String virhe = "Pizzan poistomerkinnän lisääminen tietokantaan ei onnistunut";
			vastaus.put("virhe", virhe);
			return vastaus;
		} else {
			String success = "Pizza merkitty poistetuksi!";
			vastaus.put("success", success);
			return vastaus;
		}

	}

	public HashMap<String, String> poistaJuoma(String id) {
		HashMap<String, String> vastaus = new HashMap<>();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		if (yhteys.getYhteys() == null) {
			String virhe = "Tietokantayhteyttä ei saatu avattua";
			vastaus.put("virhe", virhe);
			yhteys.suljeYhteys();
			return vastaus;
		}
		Kysely kysely = new Kysely(yhteys.getYhteys());

		// Määritellään lause ja parametrit
		String sql = "SELECT juoma_id FROM Juoma WHERE juoma_id = ?";
		ArrayList<String> parametrit = new ArrayList<String>();
		parametrit.add(id);

		if (kysely.montaRivia(sql, parametrit) < 1) {
			String virhe = "Poistettavaa juomaa ei löydy tietokannasta";
			vastaus.put("virhe", virhe);
			return vastaus;
		}

		// Määritellään lause ja poistetaan pizza
		sql = "UPDATE Juoma SET poistomerkinta = NOW() WHERE juoma_id = ?";
		Paivitys paivitys = new Paivitys(yhteys.getYhteys());
		int rivit = paivitys.suoritaSqlLauseParametreilla(sql, parametrit);

		// Yhteyden sulkeminen
		yhteys.suljeYhteys();

		if (rivit != 1) {
			String virhe = "Juoman poistomerkinnän lisääminen tietokantaan ei onnistunut";
			vastaus.put("virhe", virhe);
			return vastaus;
		} else {
			String success = "Juoma merkitty poistetuksi!";
			vastaus.put("success", success);
			return vastaus;
		}

	}

	public HashMap<String, String> palautaPizza(String id) {
		HashMap<String, String> vastaus = new HashMap<>();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		if (yhteys.getYhteys() == null) {
			String virhe = "Tietokantayhteyttä ei saatu avattua";
			vastaus.put("virhe", virhe);
			yhteys.suljeYhteys();
			return vastaus;
		}
		Kysely kysely = new Kysely(yhteys.getYhteys());

		// Määritellään lause ja parametrit
		String sql = "SELECT pizza_id FROM Pizza WHERE pizza_id = ? AND poistomerkinta IS NOT NULL";
		ArrayList<String> parametrit = new ArrayList<String>();
		parametrit.add(id);

		if (kysely.montaRivia(sql, parametrit) < 1) {
			String virhe = "Pizzaa ei ole, tai sillä ei ole poistomerkintää";
			vastaus.put("virhe", virhe);
			return vastaus;
		}

		// Määritellään lause ja palautetaan pizza
		sql = "UPDATE Pizza SET poistomerkinta = null WHERE pizza_id = ?";
		Paivitys paivitys = new Paivitys(yhteys.getYhteys());
		int rivit = paivitys.suoritaSqlLauseParametreilla(sql, parametrit);

		if (rivit != 1) {
			String virhe = "Pizzan palauttaminen tietokannassa ei onnistunut";
			vastaus.put("virhe", virhe);
			return vastaus;
		}

		// Yhteyden sulkeminen
		yhteys.suljeYhteys();

		vastaus.put("success", "Pizza palautettu onnistuneesti");
		return vastaus;
	}

	public HashMap<String, String> palautaJuoma(String id) {
		HashMap<String, String> vastaus = new HashMap<>();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		if (yhteys.getYhteys() == null) {
			String virhe = "Tietokantayhteyttä ei saatu avattua";
			vastaus.put("virhe", virhe);
			yhteys.suljeYhteys();
			return vastaus;
		}
		Kysely kysely = new Kysely(yhteys.getYhteys());

		// Määritellään lause ja parametrit
		String sql = "SELECT juoma_id FROM Juoma WHERE juoma_id = ? AND poistomerkinta IS NOT NULL";
		ArrayList<String> parametrit = new ArrayList<String>();
		parametrit.add(id);

		if (kysely.montaRivia(sql, parametrit) < 1) {
			String virhe = "Juomaa ei ole, tai sillä ei ole poistomerkintää";
			vastaus.put("virhe", virhe);
			yhteys.suljeYhteys();
			return vastaus;
		}

		// Määritellään lause ja palautetaan pizza
		sql = "UPDATE Juoma SET poistomerkinta = null WHERE juoma_id = ?";
		Paivitys paivitys = new Paivitys(yhteys.getYhteys());
		int rivit = paivitys.suoritaSqlLauseParametreilla(sql, parametrit);

		if (rivit != 1) {
			String virhe = "Juoman palauttaminen tietokannassa ei onnistunut";
			vastaus.put("virhe", virhe);
			return vastaus;
		}

		// Yhteyden sulkeminen
		yhteys.suljeYhteys();

		vastaus.put("success", "Juoma palautettu onnistuneesti");
		return vastaus;
	}

	public HashMap<String, String> lisaaTayte(String nimi, String saatavilla) {
		HashMap<String, String> vastaus = new HashMap<>();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		if (yhteys.getYhteys() == null) {
			String virhe = "Tietokantayhteyttä ei saatu avattua";
			vastaus.put("virhe", virhe);
			return vastaus;
		}
		Kysely kysely = new Kysely(yhteys.getYhteys());
		Paivitys paivitys = new Paivitys(yhteys.getYhteys());

		// Katsotaan ensin, että saman nimistä täytettä ei ole
		String sql = "SELECT nimi FROM Tayte WHERE nimi = ?";
		ArrayList<String> parametrit = new ArrayList<String>();
		parametrit.add(nimi);

		if (kysely.montaRivia(sql, parametrit) > 0) {
			String virhe = "Saman niminen täyte on jo tietokannassa!";
			vastaus.put("virhe", virhe);
			return vastaus;
		}

		// Lisätään täyte
		sql = "INSERT INTO Tayte VALUES (null, ?, ?)";
		parametrit.add(saatavilla);

		// Palauttaa onnistuneiden rivien määrän, 1 = ok, 0 = error
		int rivit = paivitys.suoritaSqlLauseParametreilla(sql, parametrit);

		if (rivit == 1) {
			String success = "Täyte lisätty tietokantaan onnistuneesti!";
			vastaus.put("success", success);
			return vastaus;
		} else {
			String virhe = "Täytettä lisätessä tietokantaan tapahtui virhe";
			vastaus.put("virhe", virhe);
			return vastaus;
		}

	}

	public HashMap<String, String> poistaMerkityt() {
		HashMap<String, String> vastaus = new HashMap<>();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		if (yhteys.getYhteys() == null) {
			String virhe = "Tietokantayhteyttä ei saatu avattua";
			vastaus.put("virhe", virhe);
			return vastaus;
		}
		Kysely kysely = new Kysely(yhteys.getYhteys());
		Paivitys paivitys = new Paivitys(yhteys.getYhteys());

		// Katsotaan poistomerkittyjen pizzojen määrä
		String sql = "SELECT pizza_id FROM Pizza WHERE poistomerkinta IS NOT NULL";

		int rivimaara = kysely.montaRivia(sql, new ArrayList<>());
		int rivit = 0;

		if (rivimaara > 0) {
			// Poistetaan ensin PizzanTäytteet
			sql = "DELETE pt FROM PizzanTayte pt JOIN Pizza p USING(pizza_id) WHERE poistomerkinta IS NOT NULL";
			paivitys.suoritaSqlLause(sql);

			// Poistetaan pizzat
			sql = "DELETE FROM Pizza WHERE poistomerkinta IS NOT NULL";
			rivit += paivitys.suoritaSqlLauseParametreilla(sql,
					new ArrayList<>());
		}

		// Katsotaan poistomerkittyjen juomien määrä
		sql = "SELECT juoma_id FROM Juoma WHERE poistomerkinta IS NOT NULL";
		rivimaara = kysely.montaRivia(sql, new ArrayList<>());

		if (rivimaara > 0) {
			// Poistetaan juomat
			sql = "DELETE FROM Juoma WHERE poistomerkinta IS NOT NULL";
			rivit += paivitys.suoritaSqlLauseParametreilla(sql,
					new ArrayList<>());
		}

		// Yhteyden sulkeminen
		yhteys.suljeYhteys();

		// Palautetaan tulokset
		if (rivit == 1) {
			String success = "Tietokannasta poistettiin " + rivit + " tuote";
			vastaus.put("success", success);
			return vastaus;
		} else if (rivit > 1) {
			String success = "Tietokannasta poistettiin " + rivit + " tuotetta";
			vastaus.put("success", success);
			return vastaus;
		} else {
			String error = "Tietokannasta poistaessa tapahtui virhe";
			vastaus.put("error", error);
			return vastaus;
		}

	}

	public HashMap<String, String> poistaTayte(String id) {
		HashMap<String, String> vastaus = new HashMap<>();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		if (yhteys.getYhteys() == null) {
			String virhe = "Tietokantayhteyttä ei saatu avattua";
			vastaus.put("virhe", virhe);
			return vastaus;
		}
		Kysely kysely = new Kysely(yhteys.getYhteys());

		// Katsotaan, onko poistettavaa täytettä olemassa
		String sql = "SELECT tayte_id FROM Tayte WHERE tayte_id = ?";
		ArrayList<String> parametrit = new ArrayList<String>();
		parametrit.add(id);

		if (kysely.montaRivia(sql, parametrit) != 1) {
			String virhe = "Täytettä ei ole tietokannassa";
			vastaus.put("virhe", virhe);
			return vastaus;
		}

		// Tarkistetaan, että poistettava täyte ei ole minkään pizzan ainoa
		// täyte
		sql = "SELECT pizza_id, tayte_id, COUNT(tayte_id) AS taytteita FROM PizzanTayte GROUP BY pizza_id HAVING taytteita = 1 AND tayte_id = ?";
		if (kysely.montaRivia(sql, parametrit) > 0) {
			String virhe = "Täyte on jonkun pizzan ainoa täyte";
			vastaus.put("virhe", virhe);
			return vastaus;
		}

		// Poistetaan ensin viittaukset PizzanTaytteet-taulusta
		sql = "DELETE FROM PizzanTayte WHERE tayte_id = ?";
		Paivitys paivitys = new Paivitys(yhteys.getYhteys());
		paivitys.suoritaSqlLauseParametreilla(sql, parametrit);

		// Poistetaan itse täyte
		sql = "DELETE FROM Tayte WHERE tayte_id = ?";
		int rivit = paivitys.suoritaSqlLauseParametreilla(sql, parametrit);

		// Yhteyden sulkeminen
		yhteys.suljeYhteys();

		// Palautetaan tulokset
		if (rivit == 1) {
			String success = "Täyte poistettiin tietokannasta";
			vastaus.put("success", success);
			return vastaus;
		} else {
			String virhe = "Täytettä poistaessa tapahtui virhe";
			vastaus.put("virhe", virhe);
			return vastaus;
		}

	}

	public ArrayList<String> haePizzatJoillaTayte(String id) {
		ArrayList<String> pizzat = new ArrayList<>();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		Kysely kysely = new Kysely(yhteys.getYhteys());

		String sql = "SELECT DISTINCT nimi FROM PizzanTayte JOIN Pizza USING(pizza_id) WHERE tayte_id = ?";
		ArrayList<String> parametrit = new ArrayList<>();
		parametrit.add(id);

		// Suoritetaan kysely
		kysely.suoritaYksiKyselyParam(sql, parametrit);
		ArrayList<HashMap<String, String>> tulokset = kysely.getTulokset();

		// Iteraattorin luonti
		Iterator iteraattori = kysely.getTulokset().iterator();

		// Tulosten läpi käynti
		while (iteraattori.hasNext()) {
			HashMap resultit = (HashMap) iteraattori.next();
			String pizza = (String) resultit.get("nimi");
			pizzat.add(pizza);
		}

		return pizzat;
	}

	public ArrayList<Juoma> haeKaikkiJuomat() {
		ArrayList<Juoma> juomat = new ArrayList<>();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		if (yhteys.getYhteys() == null) {
			yhteys.suljeYhteys();
			return juomat;
		}
		Kysely kysely = new Kysely(yhteys.getYhteys());

		ArrayList<String> parametrit = new ArrayList<>();

		String sql = "SELECT juoma_id, nimi, hinta, koko, kuvaus, saatavilla, poistomerkinta FROM Juoma ORDER BY hinta ASC";

		kysely.suoritaKysely(sql);
		ArrayList<HashMap<String, String>> tulokset = kysely.getTulokset();

		// Iteraattorin luonti
		Iterator iteraattori = kysely.getTulokset().iterator();

		while (iteraattori.hasNext()) {
			HashMap juomaMappi = (HashMap) iteraattori.next();
			String idString = (String) juomaMappi.get("juoma_id");
			String nimikanta = (String) juomaMappi.get("nimi");
			String hintaString = (String) juomaMappi.get("hinta");
			String kokoString = (String) juomaMappi.get("koko");
			String kuvausKanta = (String) juomaMappi.get("kuvaus");
			String saatavillaKanta = (String) juomaMappi.get("saatavilla");
			String poistoKanta = (String) juomaMappi.get("poistomerkinta");
			int idKanta = Integer.parseInt(idString);
			double hintaKanta = Double.parseDouble(hintaString);
			double kokoKanta = Double.parseDouble(kokoString);
			boolean saatavilla = false;

			if (poistoKanta != null && poistoKanta.equals("null")) {
				poistoKanta = null;
			}

			if (saatavillaKanta.equals("K")) {
				saatavilla = true;
			} else if (saatavillaKanta.equals("E")) {
				saatavilla = false;
			} else {
				System.out.println("Virheellinen 'saatavilla' arvo ("
						+ saatavillaKanta + ") täytteellä ID" + idString);
			}

			Juoma juoma = new Juoma(idKanta, nimikanta, hintaKanta, kokoKanta,
					kuvausKanta, poistoKanta, saatavilla);
			juomat.add(juoma);
		}

		// Yhteyden sulkeminen
		yhteys.suljeYhteys();

		// Pizzojen palautus
		return juomat;

	}

	public HashMap<String, String> paivitaAukioloajat(
			ArrayList<Aukioloaika> aukioloajat) {
		HashMap<String, String> vastaus = new HashMap<>();
		int rivit = 0;

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		if (yhteys.getYhteys() == null) {
			String virhe = "Tietokantayhteyttä ei saatu avattua";
			vastaus.put("virhe", virhe);
			yhteys.suljeYhteys();
			return vastaus;
		}

		String sql = "UPDATE Aukiolo";

		for (int i = 0; i < aukioloajat.size(); i++) {

			ArrayList<String> parametrit = new ArrayList<String>();
			
			sql = "UPDATE Aukiolo SET aloitusaika = ?, sulkemisaika = ? WHERE paiva = ?";
			parametrit.add(aukioloajat.get(i).getAloitusaika());
			parametrit.add(aukioloajat.get(i).getSulkemisaika());
			parametrit.add(aukioloajat.get(i).getPaiva());

			Paivitys paivitys = new Paivitys(yhteys.getYhteys());
			int rivi = paivitys.suoritaSqlLauseParametreilla(sql, parametrit);
			if (rivi == 1) {
				System.out.println(aukioloajat.get(i).getPaiva() + " aikojen päivitys ");
			}
			else {
				System.out.println(aukioloajat.get(i).getPaiva() + " ajan päivitys ei onnistunut");
				vastaus.put("virhe", aukioloajat.get(i).getPaiva() + " päivitys ei onnistunut");
				yhteys.suljeYhteys();
				return vastaus;
			}
			
			rivit += rivi;
			
		}

		// Yhteyden sulkeminen
		yhteys.suljeYhteys();

		String success = "Aukioloajat päivitetty";
		vastaus.put("success", success);
		return vastaus;

	}

	public ArrayList<Aukioloaika> haeAukioloajat() {
		ArrayList<Aukioloaika> aukioloajat = new ArrayList<>();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		if (yhteys.getYhteys() == null) {
			return aukioloajat;
		}

		Kysely kysely = new Kysely(yhteys.getYhteys());

		String sql = "SELECT paiva, aloitusaika, sulkemisaika FROM Aukiolo ORDER BY paiva ASC";

		kysely.suoritaKysely(sql);

		ArrayList<HashMap<String, String>> tulokset = kysely.getTulokset();

		Iterator iteraattori = kysely.getTulokset().iterator();

		while (iteraattori.hasNext()) {
			HashMap aukioloMappi = (HashMap) iteraattori.next();
			String paiva = (String) aukioloMappi.get("paiva");
			String aloitusaika = (String) aukioloMappi.get("aloitusaika");
			String sulkemisaika = (String) aukioloMappi.get("sulkemisaika");

			Aukioloaika aukioloaika = new Aukioloaika(paiva, aloitusaika,
					sulkemisaika);
			aukioloajat.add(aukioloaika);
		}

		return aukioloajat;
	}

}
