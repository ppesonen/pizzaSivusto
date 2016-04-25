package daot;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import bean.Kayttaja;
import bean.Osoite;
import login.KayttajaLista;
import login.Tiiviste;
import tietokanta.Kysely;
import tietokanta.Paivitys;
import tietokanta.Yhteys;

public class KayttajaDao {

	public Kayttaja kirjaudu(String kayttajatunnus, String salasana) {

		// Käyttäjäolion luonti
		Kayttaja kayttaja = new Kayttaja();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		
		if (yhteys.getYhteys() == null) {
			return kayttaja;
		}
		
		Kysely kysely = new Kysely(yhteys.getYhteys());

		// Suolan haku
		String suolakysely = "SELECT suola FROM Kayttaja WHERE tunnus = ?";

		ArrayList<String> parametrit = new ArrayList<String>();
		parametrit.add(kayttajatunnus);
		kysely.suoritaYksiKyselyParam(suolakysely, parametrit);
		ArrayList<HashMap<String, String>> tulokset = kysely.getTulokset();

		// Iteraattorin luonti
		Iterator iteraattori = kysely.getTulokset().iterator();

		String suola = null;
		while (iteraattori.hasNext()) {
			HashMap kayttajaMappi = (HashMap) iteraattori.next();
			suola = (String) kayttajaMappi.get("suola");
		}

		// Ei jatketa, jos käyttäjää ei löydy
		if (suola == null) {
			System.out.println("Käyttäjää ei löydy");
			return kayttaja;
		} else {

			// Salasana tiivisteeksi
			Tiiviste tiiviste = new Tiiviste();
			String salasanatiiviste = null;
			try {
				salasanatiiviste = tiiviste.salaa(salasana, suola, 500);
			} catch (Exception ex) {
				System.out.println("Virhe salasanatiivisteen käsittelyssä - " + ex);
			}

			// Käyttäjän haku tietokannasta
			String sqlkysely = "SELECT id, tunnus, etunimi, sukunimi, puhelin, tyyppi FROM Kayttaja WHERE tunnus = ? AND salasana = ?";
			parametrit.add(salasanatiiviste);

			kysely.suoritaYksiKyselyParam(sqlkysely, parametrit);
			tulokset = kysely.getTulokset();

			// Testaillaan

			iteraattori = kysely.getTulokset().iterator();

			while (iteraattori.hasNext()) {
				HashMap kayttajaMappi = (HashMap) iteraattori.next();
				String idString = (String) kayttajaMappi.get("id");
				String tunnusKanta = (String) kayttajaMappi.get("tunnus");
				String etunimiKanta = (String) kayttajaMappi.get("etunimi");
				String sukunimiKanta = (String) kayttajaMappi.get("sukunimi");
				String puhelinKanta = (String) kayttajaMappi.get("puhelin");
				String tyyppiKanta = (String) kayttajaMappi.get("tyyppi");
				int idKanta = Integer.parseInt(idString);

				// Oliolle attribuutit
				kayttaja.setTunnus(tunnusKanta);
				kayttaja.setId(idKanta);
				kayttaja.setEtunimi(etunimiKanta);
				kayttaja.setSukunimi(sukunimiKanta);
				kayttaja.setPuhelin(puhelinKanta);
				kayttaja.setTyyppi(tyyppiKanta);
			}
			
			// Haetaan käyttäjän osoitteet
			
			sqlkysely = "SELECT osoite_id, toimitusosoite, postinro, postitmp FROM Toimitusosoite WHERE kayttaja_id = ? AND poistomerkinta IS NULL";
			parametrit.clear();
			parametrit.add(String.valueOf(kayttaja.getId()));

			kysely.suoritaYksiKyselyParam(sqlkysely, parametrit);
			tulokset = kysely.getTulokset();

			iteraattori = kysely.getTulokset().iterator();
			
			ArrayList<Osoite> osoitteet = new ArrayList<>();

			while (iteraattori.hasNext()) {
				HashMap osoiteMappi = (HashMap) iteraattori.next();
				String idString = (String) osoiteMappi.get("osoite_id");
				String osoiteStr = (String) osoiteMappi.get("toimitusosoite");
				String postinro = (String) osoiteMappi.get("postinro");
				String postitmp = (String) osoiteMappi.get("postitmp");
				int idKanta = Integer.parseInt(idString);

				// Olio
				Osoite osoite = new Osoite(idKanta, osoiteStr, postinro, postitmp);
				osoitteet.add(osoite);
			}
			
			if (osoitteet.size() > 0) {
				kayttaja.setOsoitteet(osoitteet);
			}

			// Yhteyden sulkeminen
			yhteys.suljeYhteys();

			// Käyttäjän palautus
			return kayttaja;
		}
	}
	
	public ArrayList<Osoite> haeOsoitteet(String kayttajaid) {
		ArrayList<Osoite> osoitteet = new ArrayList<>();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		
		if (yhteys.getYhteys() == null) {
			return osoitteet;
		}
		
		Kysely kysely = new Kysely(yhteys.getYhteys());
		
		ArrayList<String> parametrit = new ArrayList<>();
		
		// Haetaan käyttäjän osoitteet
		String sqlkysely = "SELECT osoite_id, toimitusosoite, postinro, postitmp FROM Toimitusosoite WHERE kayttaja_id = ? AND poistomerkinta IS NULL";
		parametrit.clear();
		parametrit.add(kayttajaid);

		kysely.suoritaYksiKyselyParam(sqlkysely, parametrit);
		ArrayList<HashMap<String, String>> tulokset = kysely.getTulokset();

		Iterator iteraattori = kysely.getTulokset().iterator();

		while (iteraattori.hasNext()) {
			HashMap osoiteMappi = (HashMap) iteraattori.next();
			String idString = (String) osoiteMappi.get("osoite_id");
			String osoiteStr = (String) osoiteMappi.get("toimitusosoite");
			String postinro = (String) osoiteMappi.get("postinro");
			String postitmp = (String) osoiteMappi.get("postitmp");
			int idKanta = Integer.parseInt(idString);

			// Olio
			Osoite osoite = new Osoite(idKanta, osoiteStr, postinro, postitmp);
			osoitteet.add(osoite);
		}

		// Yhteyden sulkeminen
		yhteys.suljeYhteys();
		
		return osoitteet;
	}
	
	public HashMap<String, String> lisaaOsoite(String kayttajaid, String lahiosoite, String postinumero, String postitoimipaikka) {
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

		// Katsotaan ensin duplicaten varalta
		String sql = "SELECT toimitusosoite FROM Toimitusosoite WHERE kayttaja_id = ? AND toimitusosoite = ? AND postinro = ? AND postitmp = ? AND poistomerkinta IS NULL";
		ArrayList<String> parametrit = new ArrayList<String>();
		parametrit.add(kayttajaid);
		parametrit.add(lahiosoite);
		parametrit.add(postinumero);
		parametrit.add(postitoimipaikka);

		if (kysely.montaRivia(sql, parametrit) > 0) {
			String virhe = "Osoite on jo sinulla lisättynä";
			vastaus.put("virhe", virhe);
			yhteys.suljeYhteys();
			return vastaus;
		}

		// Lisätään osoite
		sql = "INSERT INTO Toimitusosoite VALUES (null, ?, ?, ?, ?, null)";

		// Palauttaa onnistuneiden rivien määrän, 1 = ok, 0 = error
		int rivit = paivitys.suoritaSqlLauseParametreilla(sql, parametrit);

		if (rivit == 1) {
			String success = "Osoite lisätty onnistuneesti!";
			vastaus.put("success", success);
			yhteys.suljeYhteys();
			return vastaus;
		} else {
			String virhe = "Osoitetta lisätessä tietokantaan tapahtui virhe";
			vastaus.put("virhe", virhe);
			yhteys.suljeYhteys();
			return vastaus;
		}

	}
	
	public HashMap<String, String> poistaOsoite(String kayttajaid, String poistaOsoite) {
		HashMap<String, String> vastaus = new HashMap<>();

		// Yhteyden määritys
		Yhteys yhteys = new Yhteys();
		if (yhteys.getYhteys() == null) {
			String virhe = "Tietokantayhteyttä ei saatu avattua";
			vastaus.put("virhe", virhe);
			return vastaus;
		}
		Kysely kysely = new Kysely(yhteys.getYhteys());

		// Katsotaan, onko poistettavaa osoitetta olemassa
		String sql = "SELECT osoite_id FROM Toimitusosoite WHERE osoite_id = ? AND kayttaja_id = ?";
		ArrayList<String> parametrit = new ArrayList<String>();
		parametrit.add(poistaOsoite);
		parametrit.add(kayttajaid);

		if (kysely.montaRivia(sql, parametrit) != 1) {
			String virhe = "Osoitetta ei ole tietokannassa";
			vastaus.put("virhe", virhe);
			return vastaus;
		}
		
		Paivitys paivitys = new Paivitys(yhteys.getYhteys());

		// Poistetaan itse osoite
		sql = "UPDATE Toimitusosoite SET poistomerkinta = NOW() WHERE osoite_id = ? AND kayttaja_id = ?";
		
		int rivit = paivitys.suoritaSqlLauseParametreilla(sql, parametrit);

		// Yhteyden sulkeminen
		yhteys.suljeYhteys();

		// Palautetaan tulokset
		if (rivit == 1) {
			String success = "Osoite poistettiin käyttäjätiedoista";
			vastaus.put("success", success);
			return vastaus;
		} else {
			String virhe = "Osoitetta poistaessa tapahtui virhe";
			vastaus.put("virhe", virhe);
			return vastaus;
		}

	}

	public HashMap<String, String> luoKayttaja(String kayttajatunnus, String salasana, String etunimi, String sukunimi,
			String puhelinnro) {

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

		// Katsotaan ensin duplicatejen varalta
		String sql = "SELECT tunnus FROM Kayttaja WHERE tunnus = ?";
		ArrayList<String> parametrit = new ArrayList<>();
		parametrit.add(kayttajatunnus);

		if (kysely.montaRivia(sql, parametrit) > 0) {
			String virhe = "Sähköpostiosoite on jo rekisteröity!";
			System.out.println(virhe);
			vastaus.put("virhe", virhe);
			return vastaus;
		} else {
			Tiiviste tiiviste = new Tiiviste();

			try {
				// Suolaus ja tiivistys
				String suola = tiiviste.generoiSuola();
				String passutiiviste = tiiviste.salaa(salasana, suola, 500);

				sql = "INSERT INTO Kayttaja VALUES (null, ?, ?, ?, null, ?, ?, 'user')";
				parametrit.add(etunimi);
				parametrit.add(sukunimi);
				if (puhelinnro != null) {
					sql = "INSERT INTO Kayttaja VALUES (null, ?, ?, ?, ?, ?, ?, 'user')";
					parametrit.add(puhelinnro);
				}
				parametrit.add(suola);
				parametrit.add(passutiiviste);

				int regSuccess = paivitys.suoritaSqlLauseParametreilla(sql, parametrit);

				System.out.println("Käyttäjätilin luonti tietokantaan palautti " + regSuccess);

				if (regSuccess == 1) {
					String success = "Käyttäjätili luotu onnistuneesti!";
					vastaus.put("success", success);
					return vastaus;
				} else {
					String virhe = "Käyttäjätilin luomisessa tietokantaan tapahtui virhe!";
					vastaus.put("virhe", virhe);
					return vastaus;
				}

			} catch (Exception ex) {
				String virhe = "Tietokantayhteydessä tapahtui virhe!";
				System.out.println(virhe);
				System.out.println(ex);
				vastaus.put("virhe", virhe);
				return vastaus;
			}

		}
	}

	// Väliaikainen käyttäjätietojen haku
	public ArrayList<KayttajaLista> haeKayttajat() {
		// Suoritetaan haku

		ArrayList<KayttajaLista> lista = new ArrayList<KayttajaLista>();
		

		Yhteys yhteysolio = new Yhteys();
		Connection yhteys = yhteysolio.getYhteys();
		if (yhteys == null) {
			return lista;
		}

		try {
			String sql = "SELECT id, tunnus, etunimi, sukunimi, tyyppi FROM Kayttaja ORDER BY id DESC LIMIT 10";
			Statement haku = yhteys.createStatement();
			ResultSet tulokset = haku.executeQuery(sql);

			// Käydään hakutulokset läpi
			while (tulokset.next()) {
				int id = tulokset.getInt("id");
				String tunnus = tulokset.getString("tunnus");
				String etunimi = tulokset.getString("etunimi");
				String sukunimi = tulokset.getString("sukunimi");
				String tyyppi = tulokset.getString("tyyppi");

				// Luodaan käyttäjästä olio

				KayttajaLista kayttaja = new KayttajaLista(id, tunnus, etunimi, sukunimi, tyyppi);

				lista.add(kayttaja);

			}
		} catch (Exception ex) {
			System.out.println("Käyttäjien haussa tapahtui virhe.");
		}

		// Yhteyden sulkeminen
		yhteysolio.suljeYhteys();

		return lista;
	}

}