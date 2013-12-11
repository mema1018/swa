package de.shop.bestellverwaltung.service;

import java.util.List;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Lieferung;
import de.shop.kundenverwaltung.domain.AbstractKunde;


public interface BestellungService {

	Bestellung findBestellungById(Long id);

	Bestellung findBestellungByIdMitLieferungen(Long id);

	AbstractKunde findKundeById(Long id);

	List<Bestellung> findBestellungenByKunde(AbstractKunde kunde);

	Bestellung createBestellung(Bestellung bestellung, String username);

	Bestellung createBestellung(Bestellung bestellung, AbstractKunde kunde);

	List<Artikel> ladenhueter(int anzahl);

	List<Lieferung> findLieferungen(String nr);

	Lieferung createLieferung(Lieferung lieferung, List<Bestellung> bestellungen);

	Bestellung updateBestellung(Bestellung bestellung);
}
