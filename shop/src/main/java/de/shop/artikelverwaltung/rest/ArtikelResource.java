package de.shop.artikelverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Collection;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.util.LocaleHelper;
import de.shop.util.Log;
import de.shop.util.NotFoundException;

@Path("/artikel")
@Produces(APPLICATION_JSON)
@Consumes
@RequestScoped
@Log
public class ArtikelResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	@Inject
	private ArtikelService as;
	
	@Inject
	private LocaleHelper localeHelper;
	
	@Context
	private HttpHeaders headers;
	
	@Inject
	private UriHelperArtikel uriHelperArtikel;
	
	@Context
	private UriInfo uriInfo;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Artikel findArtikelById(@PathParam("id") Long id, @Context UriInfo uriInfo) {
		final Artikel artikel = as.findArtikelById(id);
		if (artikel == null) {
			final String msg = "Kein Artikel gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}

		return artikel;
	}
	
	@GET
	public Collection<Artikel> findArtikelByBezeichnung(@QueryParam("bezeichnung") @DefaultValue("") String bezeichnung) {
		Collection<Artikel> artikelPlural = null;
		if ("".equals(bezeichnung)) {
			artikelPlural = as.findAllArtikel();
			if (artikelPlural.isEmpty()) {
				throw new NotFoundException("Keine Artikel vorhanden.");
			}
		}
		else {
			final Locale locale = localeHelper.getLocale(headers);
			artikelPlural = as.findArtikelByBezeichnung(bezeichnung, locale);
			if (artikelPlural.isEmpty()) {
				throw new NotFoundException("Kein Artikel mit Bezeichnung " + bezeichnung + " gefunden.");
			}
		}
		
		for (Artikel artikel : artikelPlural) {
			uriHelperArtikel.updateUriArtikel(artikel, uriInfo);
		}
		
		return artikelPlural;
	}
	
	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response updateArtikel(Artikel artikel) {
		final Locale locale = localeHelper.getLocale(headers);
		
		as.updateArtikel(artikel, locale);
		
		return Response.noContent().build();
	}
	@DELETE
	@Path("{id:[1-9][0-9]*}")
	@Produces
	public Response deleteArtikel(@PathParam("id") Long artikelId) {
		final Locale locale = localeHelper.getLocale(headers);
		
		as.deleteArtikel(artikelId, locale);
	
		return Response.noContent().build();
	}
	@POST
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response createArtikel(Artikel artikel) {
		
		final Locale locale = localeHelper.getLocale(headers);
		artikel = as.createArtikel(artikel, locale);
		final URI artikelUri = uriHelperArtikel.getUriArtikel(artikel, uriInfo);
		
		return Response.created(artikelUri).build();
	}
}
