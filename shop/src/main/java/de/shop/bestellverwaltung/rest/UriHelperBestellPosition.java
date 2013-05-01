package de.shop.bestellverwaltung.rest;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.rest.UriHelperArtikel;
import de.shop.bestellverwaltung.domain.BestellPosition;
import de.shop.bestellverwaltung.domain.Bestellung;


@ApplicationScoped
public class UriHelperBestellPosition {

	@Inject
	private UriHelperBestellung uriHelperBestellung;
	
	@Inject
	private UriHelperArtikel uriHelperArtikel;
	

	// URL für Bestellung und Artikel setzen
	public void updateUriBestellPosition(BestellPosition bestellPosition,
			UriInfo uriInfo) {
		final Bestellung bestellung = bestellPosition.getBestellung();

		if (bestellung != null) {
			final URI bestellungUri = uriHelperBestellung.getUriBestellung(bestellPosition.getBestellung(), uriInfo);
			bestellPosition.setBestellungURI(bestellungUri);
		}
		final Artikel artikel = bestellPosition.getArtikel();
		if (artikel != null) {
			final URI artikelUri = uriHelperArtikel.getUriArtikel(bestellPosition.getArtikel(), uriInfo);
			bestellPosition.setArtikelUri(artikelUri);
		}
		
		

	}

	public URI getUriBestellPosition(BestellPosition bestellPosition,
			UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
				.path(BestellPosition.class)
				.path(BestellPosition.class, "findBestellPositionById");
		final URI uri = ub.build(bestellPosition.getId());
		return uri;
	}

}
