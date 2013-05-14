package de.shop.bestellverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.bestellverwaltung.domain.BestellPosition;
import de.shop.bestellverwaltung.service.BestellPositionService;
import de.shop.util.LocaleHelper;
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
	
	@Inject
	private BestellPositionService bs;
	
	@Inject
	private BestellungResource br;

	@Inject
	private ArtikelService as;
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public BestellPosition findBestellPositionById(@PathParam("id") Long id, @Context UriInfo uriInfo) {
		final BestellPosition bestellPosition = bs.findBestellPositionById(id);
		if (bestellPosition == null) {
			final String msg = "Keine BestellPosition gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
		uriHelperBestellPosition.updateUriBestellPosition(bestellPosition, uriInfo);
		return bestellPosition;
	}
	@Consumes(APPLICATION_JSON)
	@Produces
	public BestellPosition createBestellPosition(BestellPosition bestellPosition){
		final URI artikelUri = bestellPosition.getArtikelUri();
		final String artikelUriString = artikelUri.toString();
		final String stringArtikelId = artikelUriString.substring(artikelUriString.lastIndexOf("/") + 1);
		final Long artikelId = Long.valueOf(stringArtikelId);
		final Artikel artikel  = as.findArtikelById(artikelId);
		
		bestellPosition.setArtikel(artikel);
		
		bestellPosition = bs.createBestellPosition(bestellPosition);
		
		return bestellPosition;
	}
	
}
