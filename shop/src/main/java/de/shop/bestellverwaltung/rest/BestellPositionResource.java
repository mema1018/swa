package de.shop.bestellverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

//import java.util.Locale;






import javax.inject.Inject;
//import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.HttpHeaders;
//import javax.ws.rs.core.UriInfo;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import de.shop.bestellverwaltung.domain.BestellPosition;
import de.shop.util.LocaleHelper;
//import de.shop.util.LocaleHelper;
import de.shop.util.Mock;
import de.shop.util.NotFoundException;

@Path("/bestellPosition")
@Produces(APPLICATION_JSON)
@Consumes
public class BestellPositionResource {
	@Context
	private UriInfo uriInfo;

	@Context
	private HttpHeaders headers;

	@Inject
	private UriHelperBestellPosition uriHelperBestellPosition;

	@Inject
	private UriHelperBestellung uriHelperBestellung;

	@Inject
	private LocaleHelper localeHelper;

	@GET
	@Path("{id:[1-9][0-9]*}")
	public BestellPosition findBestellPositionById(@PathParam("id") Long id) {

		// TODO Anwendungskern statt Mock, Verwendung von Locale
		final BestellPosition bestellPosition = Mock
				.findBestellPositionById(id);
		if (bestellPosition == null) {
			throw new NotFoundException("Keine BestellPosition mit der ID "
					+ id + " gefunden.");
		}
		uriHelperBestellPosition.updateUriBestellPosition(bestellPosition, uriInfo);
		
		return bestellPosition;
	}
}
