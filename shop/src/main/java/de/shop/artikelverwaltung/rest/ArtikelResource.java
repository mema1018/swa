package de.shop.artikelverwaltung.rest;

import static de.shop.util.Constants.KEINE_ID;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
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
import de.shop.util.Transactional;


@Path("/artikel")
@Produces({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
@Consumes
@RequestScoped
@Transactional
@Log
public class ArtikelResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	@Context
	private UriInfo uriInfo;
	
	@Inject
	private ArtikelService as;
	
	@Inject
	private LocaleHelper localeHelper;
	
	@Inject
	private UriHelperArtikel uriHelperArtikel;
	
	@Context
	private HttpHeaders headers;
	
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
	public Artikel findArtikelById(@PathParam("id") Long id) {
		final Artikel artikel = as.findArtikelById(id);
		if (artikel == null) {
			final String msg = "Kein Artikel gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
		return artikel;
	}	
	
	@GET
	public Artikel findArtikelByPreis(@QueryParam("preis") Double preis) {
		final Artikel artikel = as.findArtikelByPreis(preis);
		if (artikel == null) {
			final String msg = "Kein Artikel gefunden mit dem Preis " + preis;
			throw new NotFoundException(msg);
		}
		return artikel;
	}	

	@GET
	public Artikel findArtikelByBezeichnung(@QueryParam("bezeichnung") @DefaultValue("") String bezeichnung) {
		final Artikel artikel = as.findArtikelByBezeichnung(bezeichnung);
		if (artikel == null) {
			final String msg = "Kein Artikel gefunden mit der Bezeichnung " + bezeichnung;
			throw new NotFoundException(msg);
		}
		return artikel;
	}
	
	
	@POST
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response createArtikel(Artikel artikel) {
		final Locale locale = localeHelper.getLocale(headers);

		artikel.setId(KEINE_ID);
		
		artikel = as.createArtikel(artikel, locale);
		
		LOGGER.tracef("Artikel: %s", artikel);
		
		final URI artikelUri = uriHelperArtikel.getUriArtikel(artikel, uriInfo);
		return Response.created(artikelUri).build();
	}
	
	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces
	public void updateArtikel(Artikel artikel) {
		// Vorhandenen Artikel ermitteln
		final Locale locale = localeHelper.getLocale(headers);
		final Artikel orginalArtikel = as.findArtikelById(artikel.getId());
		if (orginalArtikel == null) {
			final String msg = "Kein Artikel gefunden mit der ID " + artikel.getId();
			throw new NotFoundException(msg);
		}
		LOGGER.tracef("Artikel vorher: %s", orginalArtikel);
	
		// Daten des vorhandenen Artikels ueberschreiben
		orginalArtikel.setValues(artikel);
		LOGGER.tracef("Artikel nachher: %s", orginalArtikel);
		
		// Update durchfuehren
		artikel = as.updateArtikel(orginalArtikel, locale);
		if (artikel == null) {
			final String msg = "Kein Artikel gefunden mit der ID " + orginalArtikel.getId();
			throw new NotFoundException(msg);
		}
	}

}
