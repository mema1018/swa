package de.shop.kundenverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.Collection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.rest.UriHelperBestellung;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.util.Mock;
import de.shop.util.NotFoundException;

@Path("/kunden")
@Produces(APPLICATION_JSON)
@Consumes
// Spezifizert eine Liste von Mediatypen, die komsumiert werden k�nnen.
@RequestScoped
// Bei jedem Request 1 neues Objekt
public class KundeResource {

	@Context
	// Man benutzt @Context,um Java-Types,bezogen auf Requests und Responses zu
	// bekommen
	private UriInfo uriInfo; // UriInfo bietet Informationen �ber die
								// Komponenten einer URI.

	// @Inject indiziert Methoden oder Attribute.
	@Inject
	private UriHelperKunde uriHelperKunde;

	@Inject
	private UriHelperBestellung uriHelperBestellung;

	@GET
	@Path("{id:[1-9][0-9]*}")
	public AbstractKunde findKundeById(@PathParam("id") Long id) { // Der Wert
																	// der
																	// Annotation
																	// identifiziert
																	// einen
																	// query-Parameter.

		// TODO Anwendungskern statt Mock, Verwendung von Locale
		final AbstractKunde kunde = Mock.findKundeById(id);
		if (kunde == null) {
			throw new NotFoundException("Kein Kunde mit der ID " + id
					+ " gefunden.");
		}
		// URLs innerhalb des gefundenen Kunden anpassen
		uriHelperKunde.updateUriKunde(kunde, uriInfo);

		return kunde;
	}

	@GET
	public Collection<AbstractKunde> findKundenByNachname(
			@QueryParam("nachname") @DefaultValue("") String nachname) {

		Collection<AbstractKunde> kunden = null;
		if ("".equals(nachname)) {
			// TODO Anwendungskern statt Mock, Verwendung von Locale
			kunden = Mock.findAllKunden();
			if (kunden.isEmpty()) {
				throw new NotFoundException("Keine Kunden vorhanden.");
			}
		} 
		else {
			// TODO Anwendungskern statt Mock, Verwendung von Locale
			kunden = Mock.findKundenByNachname(nachname);
			if (kunden.isEmpty()) {
				throw new NotFoundException("Kein Kunde mit Nachname "
						+ nachname + " gefunden.");
			}
		}

		for (AbstractKunde kunde : kunden) {
			uriHelperKunde.updateUriKunde(kunde, uriInfo);
		}

		return kunden;
	}

	@GET
	@Path("{id:[1-9][0-9]*}/bestellungen")
	public Collection<Bestellung> findBestellungenByKundeId(
			@PathParam("id") Long kundeId) {

		// TODO Anwendungskern statt Mock, Verwendung von Locale
		final Collection<Bestellung> bestellungen = Mock
				.findBestellungenByKundeId(kundeId);
		if (bestellungen.isEmpty()) {
			throw new NotFoundException("Zur ID " + kundeId
					+ " wurden keine Bestellungen gefunden");
		}

		// URLs innerhalb der gefundenen Bestellungen anpassen
		for (Bestellung bestellung : bestellungen) {
			uriHelperBestellung.updateUriBestellung(bestellung, uriInfo);
		}

		return bestellungen;
	}

	@POST
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response createKunde(AbstractKunde kunde) {

		// TODO Anwendungskern statt Mock, Verwendung von Locale
		kunde = Mock.createKunde(kunde);
		final URI kundeUri = uriHelperKunde.getUriKunde(kunde, uriInfo);
		return Response.created(kundeUri).build();
	}

	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response updateKunde(AbstractKunde kunde) {

		// TODO Anwendungskern statt Mock, Verwendung von Locale
		Mock.updateKunde(kunde);
		return Response.noContent().build();
	}

	@DELETE
	@Path("{id:[1-9][0-9]*}")
	@Produces
	public Response deleteKunde(@PathParam("id") Long kundeId) {

		// TODO Anwendungskern statt Mock, Verwendung von Locale
		Mock.deleteKunde(kundeId);
		return Response.noContent().build();
	}
}
