package de.shop.artikelverwaltung.rest;

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

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.Mock;
import de.shop.util.NotFoundException;

@Path("/artikel")
@Produces(APPLICATION_JSON)
@Consumes
// Spezifizert eine Liste von Mediatypen, die komsumiert werden können.
@RequestScoped
// Bei jedem Request 1 neues Objekt
public class ArtikelResource {

	@Context
	// Man benutzt @Context,um Java-Types,bezogen auf Requests und Responses zu
	// bekommen
	private UriInfo uriInfo; // UriInfo bietet Informationen über die
								// Komponenten einer URI.

	// @Inject indiziert Methoden oder Attribute.
	@Inject
	private UriHelperArtikel uriHelperArtikel;

	@GET
	@Path("{id:[1-9][0-9]*}")
	public Artikel findArtikelById(@PathParam("id") Long id) { // Der Wert der
																// Annotation
																// identifiziert
																// einen
																// query-Parameter.

		// TODO Anwendungskern statt Mock, Verwendung von Locale
		final Artikel artikel = Mock.findArtikelById(id);
		if (artikel == null) {
			throw new NotFoundException("Kein Artikel mit der ID " + id
					+ " gefunden.");
		}
		// URLs innerhalb des gefundenen Artikels anpassen
		uriHelperArtikel.updateUriArtikel(artikel, uriInfo);

		return artikel;
	}

	@GET
	public Collection<Artikel> findArtikelByBezeichnung(
			@QueryParam("bezeichnung") @DefaultValue("") String bezeichnung) {

		Collection<Artikel> artikelPlural = null;
		if ("".equals(bezeichnung)) {
			// TODO Anwendungskern statt Mock, Verwendung von Locale
			artikelPlural = Mock.findAllArtikel();
			if (artikelPlural.isEmpty()) {
				throw new NotFoundException("Keine Artikel vorhanden.");
			}
		} 
		else {
			// TODO Anwendungskern statt Mock, Verwendung von Locale
			artikelPlural = Mock.findArtikelByBezeichnung(bezeichnung);
			if (artikelPlural.isEmpty()) {
				throw new NotFoundException("Kein Artikel mit Bezeichnung "
						+ bezeichnung + " gefunden.");
			}
		}

		for (Artikel artikel : artikelPlural) {
			uriHelperArtikel.updateUriArtikel(artikel, uriInfo);
		}

		return artikelPlural;
	}

	@POST
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response createArtikel(Artikel artikel) {

		// TODO Anwendungskern statt Mock, Verwendung von Locale
		artikel = Mock.createArtikel(artikel);
		final URI artikelUri = uriHelperArtikel.getUriArtikel(artikel, uriInfo);
		return Response.created(artikelUri).build();
	}

	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response updateArtikel(Artikel artikel) {

		// TODO Anwendungskern statt Mock, Verwendung von Locale
		Mock.updateArtikel(artikel);
		return Response.noContent().build();
	}

	@DELETE
	@Path("{id:[1-9][0-9]*}")
	@Produces
	public Response deleteArtikel(@PathParam("id") Long artikelId) {

		// TODO Anwendungskern statt Mock, Verwendung von Locale
		Mock.deleteArtikel(artikelId);
		return Response.noContent().build();
	}
}
