package de.shop.bestellverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.net.URI;

//import java.net.URI;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
//import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
//import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
//import de.shop.artikelverwaltung.domain.Artikel;
//import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.service.BestellPositionService;
//import de.shop.util.LocaleHelper;
import de.shop.util.Log;
import de.shop.util.NotFoundException;

@Path("/bestellposition")
@Produces({ APPLICATION_JSON, APPLICATION_XML + ";qs=0.75",
		TEXT_XML + ";qs=0.5" })
@Consumes
@Log
@RequestScoped
public class BestellPositionResource {

	@Inject
	private UriHelperBestellPosition uriHelperBestellPosition;

	@Inject
	private BestellPositionService bs;

	@Inject
	private ArtikelService as;

	@GET
	@Path("{id:[1-9][0-9]*}")
	public Bestellposition findBestellpositionById(@PathParam("id") Long id,
			@Context UriInfo uriInfo) {
		final Bestellposition bestellposition = bs.findBestellpositionById(id);
		if (bestellposition == null) {
			throw new NotFoundException("Fehler");
		}
		uriHelperBestellPosition
				.getUriBestellPosition(bestellposition, uriInfo);
		return bestellposition;

	}

	@POST
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	@Transactional
	public Bestellposition createBestellPosition(
			@Valid Bestellposition bestellposition) {
		final URI artikelUri = bestellposition.getArtikelUri();
		final String artikelUriString = artikelUri.toString();
		final String stringArtikelId = artikelUriString
				.substring(artikelUriString.lastIndexOf("/") + 1);
		final Long artikelId = Long.valueOf(stringArtikelId);
		final Artikel artikel = as.findArtikelById(artikelId);

		bestellposition.setArtikel(artikel);

		bestellposition = bs.createBestellposition(bestellposition);

		return bestellposition;
	}

}
