package de.shop.util;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.bestellverwaltung.domain.BestellPosition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Firmenkunde;
import de.shop.kundenverwaltung.domain.HobbyType;
import de.shop.kundenverwaltung.domain.Privatkunde;

/**
 * Emulation der Datenbankzugriffsschicht
 */
public final class Mock {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());

	private static final int MAX_ID = 99;
	private static final int MAX_KUNDEN = 8;
	private static final int MAX_BESTELLUNGEN = 4;
	private static final int MAX_ARTIKEL = 10;
	private static final int JAHR = 2001;
	private static final int MONAT = 0; // bei Calendar werden die Monate von 0 bis 11 gezaehlt
	private static final int TAG = 31;  // bei Calendar die Monatstage ab 1 gezaehlt
	
	
	public static AbstractKunde findKundeById(Long id) {
		if (id > MAX_ID) {
			return null;
		}
		
		final AbstractKunde kunde = id % 2 == 1 ? new Privatkunde() : new Firmenkunde();
		kunde.setId(id);
		kunde.setNachname("Nachname");
		kunde.setEmail("" + id + "@hska.de");
		final GregorianCalendar seitCal = new GregorianCalendar(JAHR, MONAT, TAG);
		final Date seit = seitCal.getTime();
		kunde.setSeit(seit);
		
		final Adresse adresse = new Adresse();
		adresse.setId(id + 1);        // andere ID fuer die Adresse
		adresse.setPlz("12345");
		adresse.setOrt("Testort");
		adresse.setKunde(kunde);
		kunde.setAdresse(adresse);
		
		final List<Bestellung> bestellungen = new ArrayList<Bestellung>();
		final Bestellung bestellung = new Bestellung();
		bestellung.setId(Long.valueOf(10));
		bestellung.setAusgeliefert(false);
		kunde.setBestellungen(bestellungen);
		kunde.getBestellungen().add(bestellung);
		
		if (kunde.getClass().equals(Privatkunde.class)) {
			final Privatkunde privatkunde = (Privatkunde) kunde;
			final Set<HobbyType> hobbies = new HashSet<>();
			hobbies.add(HobbyType.LESEN);
			hobbies.add(HobbyType.REISEN);
			privatkunde.setHobbies(hobbies);
		}
		
		return kunde;
	}

	public static List<AbstractKunde> findAllKunden() {
		final int anzahl = MAX_KUNDEN;
		final List<AbstractKunde> kunden = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final AbstractKunde kunde = findKundeById(Long.valueOf(i));
			kunden.add(kunde);			
		}
		return kunden;
	}

	public static List<AbstractKunde> findKundenByNachname(String nachname) {
		final int anzahl = nachname.length();
		final List<AbstractKunde> kunden = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final AbstractKunde kunde = findKundeById(Long.valueOf(i));
			kunde.setNachname(nachname);
			kunden.add(kunde);			
		}
		return kunden;
	}
	
	public static AbstractKunde findKundeByEmail(String email) {
		if (email.startsWith("x")) {
			return null;
		}
		
		final AbstractKunde kunde = email.length() % 2 == 1 ? new Privatkunde() : new Firmenkunde();
		kunde.setId(Long.valueOf(email.length()));
		kunde.setNachname("Nachname");
		kunde.setEmail(email);
		final GregorianCalendar seitCal = new GregorianCalendar(JAHR, MONAT, TAG);
		final Date seit = seitCal.getTime();
		kunde.setSeit(seit);
		
		final Adresse adresse = new Adresse();
		adresse.setId(kunde.getId() + 1);        // andere ID fuer die Adresse
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
	
	public static List<Bestellung> findBestellungenByKundeId(Long kundeId) {
		final AbstractKunde kunde = findKundeById(kundeId);
		
		// Beziehungsgeflecht zwischen Kunde und Bestellungen aufbauen
		final int anzahl = kundeId.intValue() % MAX_BESTELLUNGEN + 1;  // 1, 2, 3 oder 4 Bestellungen
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

		final AbstractKunde kunde = findKundeById(id + 1);  // andere ID fuer den Kunden

		final Bestellung bestellung = new Bestellung();
		bestellung.setId(id);
		bestellung.setAusgeliefert(false);
		bestellung.setKunde(kunde);
		
		final Artikel artikel = findArtikelById(id+1);
		final BestellPosition bestellPosition = findBestellPositionById(id+1);
		bestellPosition.setArtikel(artikel);
		bestellPosition.setBestellung(bestellung);
		List<BestellPosition>bestellPositionen = new ArrayList<>();
		bestellPositionen.add(bestellPosition);
		
		bestellung.setBestellPositionen(bestellPositionen);
		
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
		
		LOGGER.infof("Neuer Kunde: %s", kunde);
		return kunde;
	}

	public static void updateKunde(AbstractKunde kunde) {
		LOGGER.infof("Aktualisierter Kunde: %s", kunde);
	}

	public static void deleteKunde(AbstractKunde kunde) {
		LOGGER.infof("Geloeschter Kunde: %s", kunde);
	}

	public static Bestellung createBestellung(Bestellung bestellung, AbstractKunde kunde) {
		LOGGER.infof("Neue Bestellung: %s fuer Kunde: %s", bestellung, kunde);
		return bestellung;
	}
	public static Bestellung createBestellung(Bestellung bestellung,Locale locale) {
		
		final AbstractKunde kunde = bestellung.getKunde();
		final URI kundenUri = bestellung.getKundeUri();
		final boolean ausgeliefert = bestellung.isAusgeliefert();
		
		bestellung.setId(Long.valueOf(10));
		bestellung.setAusgeliefert(ausgeliefert);
		bestellung.setKundeUri(kundenUri);
		bestellung.setKunde(kunde);
		

		LOGGER.infof("Neue Bestellung: %s", bestellung);
		System.out.println("Neue Bestellung " + bestellung);
		
		return bestellung;
	}

	public static Artikel findArtikelById(Long id) {
		final Artikel artikel = new Artikel();
		artikel.setId(id);
		artikel.setBezeichnung("Playstation3");
		return artikel;
	}
	public static void updateArtikel(Artikel artikel) {
		LOGGER.infof("Aktualisierter Artikel: %s", artikel);
		
	}
	public static List<Artikel> findAllArtikel() {
		final int anzahl = MAX_ARTIKEL;
		final List<Artikel> artikelPlural = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final Artikel artikel = findArtikelById(Long.valueOf(i));
			artikelPlural.add(artikel);			
		}
		return artikelPlural;
	}
	public static List<Artikel> findArtikelByBezeichnung(String bezeichnung) {
		final int anzahl = bezeichnung.length();
		final List<Artikel> artikelPlural = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final Artikel artikel = findArtikelById(Long.valueOf(i));
			artikel.setBezeichnung(bezeichnung);
			artikelPlural.add(artikel);
		}
		return artikelPlural;
	}
	public static void deleteArtikel(Artikel artikel) {
		LOGGER.infof("Geloeschter Artikel: %s", artikel);	
	}
	public static Artikel createArtikel(Artikel artikel) {
		
		final String bezeichnung = artikel.getBezeichnung();
		artikel.setId(Long.valueOf(bezeichnung.length()));
				
		LOGGER.infof("Neuer Artikel: %s", artikel);
		return artikel;
	}
	public static BestellPosition findBestellPositionById(Long id) {
		
		final BestellPosition bestellPosition = new BestellPosition();
		
		final Artikel artikel = findArtikelById(id+1);
		final Bestellung bestellung  = findBestellungById(id+1);
		bestellPosition.setBestellung(bestellung);
		bestellPosition.setArtikel(artikel);
		bestellPosition.setId(id);
		bestellPosition.setAnzahl(10);
		
		return bestellPosition;
	}
	
	public static BestellPosition createBestellPosition(BestellPosition bestellPosition) {
	
		final Long anzahl  = bestellPosition.getAnzahl();
		bestellPosition.setId(Long.valueOf(1));
		final URI artikelUri = bestellPosition.getArtikelUri();

		final Artikel artikel = findArtikelById(Long.valueOf(1));

		bestellPosition.setAnzahl(anzahl);
		bestellPosition.setArtikelUri(artikelUri);
		bestellPosition.setArtikel(artikel);
		
		
		return bestellPosition;
	}
//	public static Collection<BestellPosition> findBestellPositionenByBestellungId(
//			Long bestellungId) {
//		final Bestellung bestellung = findBestellungById(bestellungId+1);
//
//		// Beziehungsgeflecht zwischen Bestellung und BestellPosition aufbauen
//		final int anzahl = bestellungId.intValue() % MAX_BESTELLPOSITIONEN + 1; // 1,																	// Bestellungen
//		final List<BestellPosition> bestellPositionen = new ArrayList<>(anzahl);
//		for (int i = 1; i <= anzahl; i++) {
//			final BestellPosition bestellPosition = findBestellPositionById(Long
//					.valueOf(i));
//			bestellPosition.setBestellung(bestellung);
//			bestellPositionen.add(bestellPosition);
//		}
//		bestellung.setBestellPositionen(bestellPositionen);
//
//		return bestellPositionen;
//	}
	private Mock() { /**/ }

	

	

	

	

	

	

	
}
