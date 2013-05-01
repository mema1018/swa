package de.shop.util;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.bestellverwaltung.domain.BestellPosition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Firmenkunde;
import de.shop.kundenverwaltung.domain.HobbyType;
import de.shop.kundenverwaltung.domain.Privatkunde;

/**
 * Emulation des Anwendungskerns
 */
public final class Mock {
	private static final int MAX_ID = 99;
	private static final int MAX_KUNDEN = 120;
	private static final int MAX_BESTELLUNGEN = 4;
	private static final int MAX_ARTIKEL = 10;
	private static final int MAX_BESTELLPOSITIONEN = 99;
	private static final int MAX_ANZAHL_BESTPOS = 10;

	public static AbstractKunde findKundeById(Long id) {
		if (id > MAX_ID) {
			return null;
		}

		final AbstractKunde kunde = id % 2 == 1 ? new Privatkunde()
				: new Firmenkunde();
		kunde.setId(id);
		kunde.setNachname("Nachname" + id);
		kunde.setEmail("" + id + "@hska.de");

		final Adresse adresse = new Adresse();
		adresse.setId(id + 1); // andere ID fuer die Adresse
		adresse.setPlz("12345");
		adresse.setOrt("Testort");
		adresse.setKunde(kunde);
		kunde.setAdresse(adresse);

		if (kunde.getClass().equals(Privatkunde.class)) {
			final Privatkunde privatkunde = (Privatkunde) kunde;
			final Set<HobbyType> hobbies = new HashSet<>();
			hobbies.add(HobbyType.LESEN);
			hobbies.add(HobbyType.REISEN);
			privatkunde.setHobbies(hobbies);
		}

		return kunde;
	}

	public static Collection<AbstractKunde> findAllKunden() {
		final int anzahl = MAX_KUNDEN;
		final Collection<AbstractKunde> kunden = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final AbstractKunde kunde = findKundeById(Long.valueOf(i));
			kunden.add(kunde);
		}
		return kunden;
	}

	public static Collection<AbstractKunde> findKundenByNachname(String nachname) {
		final int anzahl = nachname.length();
		final Collection<AbstractKunde> kunden = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final AbstractKunde kunde = findKundeById(Long.valueOf(i));
			kunde.setNachname(nachname);
			kunden.add(kunde);
		}
		return kunden;
	}

	public static Collection<Bestellung> findBestellungenByKundeId(Long kundeId) {
		final AbstractKunde kunde = findKundeById(kundeId);

		// Beziehungsgeflecht zwischen Kunde und Bestellungen aufbauen
		final int anzahl = kundeId.intValue() % MAX_BESTELLUNGEN + 1; // 1, 2, 3
																		// oder
																		// 4
																		// Bestellungen
		final List<Bestellung> bestellungen = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final Bestellung bestellung = findBestellungById(Long.valueOf(i));
			bestellung.setKunde(kunde);
			bestellungen.add(bestellung);
		}
		kunde.setBestellungen(bestellungen);

		return bestellungen;
	}

	public static Bestellung findBestellungById(Long id) {
		if (id > MAX_ID) {
			return null;
		}

		final AbstractKunde kunde = findKundeById(id + 1); // andere ID fuer den
															// Kunden
		final Bestellung bestellung = new Bestellung();

		bestellung.setId(id);
		bestellung.setAusgeliefert(false);
		bestellung.setKunde(kunde);

		return bestellung;
	}
	public static Bestellung createBestellung(Bestellung bestellung) {

		final AbstractKunde kunde = bestellung.getKunde();
		final URI kundenUri = bestellung.getKundeUri();
		final boolean ausgeliefert = bestellung.isAusgeliefert();
		final URI bestellPosURI = bestellung.getBestellPositionURI();
		bestellung.setId(Long.valueOf(10));
		bestellung.setAusgeliefert(ausgeliefert);
		bestellung.setKundeUri(kundenUri);
		bestellung.setBestellPositionURI(bestellPosURI);
		bestellung.setKunde(kunde);

		System.out.println("Neue Bestellung " + bestellung);
		return bestellung;
	}

	public static AbstractKunde createKunde(AbstractKunde kunde) {
		// Neue IDs fuer Kunde und zugehoerige Adresse
		// Ein neuer Kunde hat auch keine Bestellungen
		final String nachname = kunde.getNachname();
		kunde.setId(Long.valueOf(nachname.length()));
		final Adresse adresse = kunde.getAdresse();
		adresse.setId((Long.valueOf(nachname.length())) + 1);
		adresse.setKunde(kunde);
		kunde.setBestellungen(null);

		System.out.println("Neuer Kunde: " + kunde);
		return kunde;
	}

	public static void updateKunde(AbstractKunde kunde) {
		System.out.println("Aktualisierter Kunde: " + kunde);
	}

	public static void deleteKunde(Long kundeId) {
		System.out.println("Kunde mit ID=" + kundeId + " geloescht");
	}

	public static Artikel createArtikel(Artikel artikel) {

		final String bezeichnung = artikel.getBezeichnung();
		artikel.setId(Long.valueOf(bezeichnung.length()));

		System.out.println("Neuer Artikel: " + artikel);
		return artikel;

	}

	public static Artikel findArtikelById(Long id) {
		if (id > MAX_ARTIKEL) {
			return null;
		}

		final Artikel artikel = new Artikel();
		artikel.setId(id);
		artikel.setBezeichnung("Tisch");
		artikel.setEinkaufspreis(new BigDecimal("20.0"));
		artikel.setVerkaufspreis(new BigDecimal("30.0"));
		return artikel;
	}

	public static void updateArtikel(Artikel artikel) {

		System.out.println("Aktualisierter Artikel: " + artikel);
	}

	public static Collection<Artikel> findAllArtikel() {

		final int anzahl = MAX_ARTIKEL;
		final Collection<Artikel> artikelPlural = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final Artikel artikel = findArtikelById(Long.valueOf(i));
			artikelPlural.add(artikel);
		}
		return artikelPlural;
	}

	public static Collection<Artikel> findArtikelByBezeichnung(
			String bezeichnung) {

		final int anzahl = bezeichnung.length();
		final Collection<Artikel> artikelPlural = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final Artikel artikel = findArtikelById(Long.valueOf(i));
			artikel.setBezeichnung(bezeichnung);
			artikelPlural.add(artikel);
		}
		return artikelPlural;

	}

	public static void deleteArtikel(Long artikelId) {
		System.out.println("Artikel mit ID=" + artikelId + " geloescht");
	}

	public static BestellPosition findBestellPositionById(Long id) {
		if (id > MAX_ID) {
			return null;
		}
		final BestellPosition bestellPosition = new BestellPosition();
		final Artikel artikel = findArtikelById(id + 1);
		final Bestellung bestellung = findBestellungById(id + 1);
		bestellPosition.setId(id);
		bestellPosition.setArtikel(artikel);
		bestellPosition.setBestellung(bestellung);
		bestellPosition.setAnzahl(MAX_ANZAHL_BESTPOS);

		return bestellPosition;
	}

	public static Collection<BestellPosition> findBestellPositionByBestellungId(
			Long bestellungId) {
		final Bestellung bestellung = findBestellungById(bestellungId);

		// Beziehungsgeflecht zwischen Bestellung und BestellPosition aufbauen
		final int anzahl = bestellungId.intValue() % MAX_BESTELLPOSITIONEN + 1; // 1,
																				// 2,
																				// 3
																				// oder
																				// 4
																				// Bestellungen
		final List<BestellPosition> bestellPositionen = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final BestellPosition bestellPosition = findBestellPositionById(Long
					.valueOf(i));
			bestellPosition.setBestellung(bestellung);
			bestellPositionen.add(bestellPosition);
		}
		bestellung.setBestellPositionen(bestellPositionen);

		return bestellPositionen;
	}

	private Mock() { /**/
	}
}
