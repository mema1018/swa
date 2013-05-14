package de.shop.bestellverwaltung.rest;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.rest.UriHelperKunde;
import de.shop.bestellverwaltung.rest.BestellungResource;


@ApplicationScoped
public class UriHelperBestellung {
	@Inject
	private UriHelperKunde uriHelperKunde;
	
	public void updateUriBestellung(Bestellung bestellung, UriInfo uriInfo) {
		// URL fuer Kunde setzen
		final AbstractKunde kunde = bestellung.getKunde();
		if (kunde != null) {
			final URI kundeUri = uriHelperKunde.getUriKunde(bestellung.getKunde(), uriInfo);
			bestellung.setKundeUri(kundeUri);
		}
//		final UriBuilder ub = uriInfo
//				.getBaseUriBuilder()
//				.path(BestellungResource.class)
//				.path(BestellungResource.class,
//						"findBestellPositionenByBestellungId");
//		final URI bestellPositionURI = ub.build(bestellung.getId());
//		bestellung.setBestellPositionURI(bestellPositionURI);
	}

	public URI getUriBestellung(Bestellung bestellung, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(BestellungResource.class)
		                             .path(BestellungResource.class, "findBestellungById");
		final URI uri = ub.build(bestellung.getId());
		return uri;
	}
}
