package de.shop.bestellverwaltung.rest;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import de.shop.bestellverwaltung.domain.Bestellposition;

@ApplicationScoped
public class UriHelperBestellPosition {

	public URI getUriBestellPosition(Bestellposition bestellposition,
			UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
				.path(Bestellposition.class)
				.path(Bestellposition.class, "findBestellpositionById");
		final URI uri = ub.build(bestellposition.getId());
		return uri;
	}

}
