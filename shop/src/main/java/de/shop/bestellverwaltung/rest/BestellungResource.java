package de.shop.bestellverwaltung.rest;


import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;









import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.rest.UriHelperArtikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.bestellverwaltung.domain.BestellPosition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.service.BestellPositionService;
import de.shop.bestellverwaltung.service.BestellungService;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.util.LocaleHelper;
import de.shop.util.Log;
import de.shop.util.NotFoundException;

@Path("/bestellungen")
@Produces(APPLICATION_JSON)
@Consumes
@Log
public class BestellungResource {
	@Context
	
	private UriInfo uriInfo;
	
	@Inject
	private UriHelperBestellung uriHelperBestellung;
	
	
	@Inject
	private UriHelperArtikel uriHelperArtikel;
	
	@Inject
	private UriHelperBestellPosition uriHelperBestellPosition;
	
	@Inject
	private BestellungService bs;
	
	@Inject
	private ArtikelService as;
	
	@Inject
	private BestellPositionService bp;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private LocaleHelper localeHelper;
	
	@Context
	private HttpHeaders headers;
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Bestellung findBestellungById(@PathParam("id") Long id) {
		final Bestellung bestellung = bs.findBestellungById(id);
		if (bestellung == null) {
			throw new NotFoundException("Keine Bestellung mit der ID " + id + " gefunden.");
		}
		final Artikel artikel = as.findArtikelById(id);
		final BestellPosition bestellPosition = bp.findBestellPositionById(id);
		// URLs innerhalb der gefundenen Bestellung anpassen
		
		List<BestellPosition> bestellPositionen = new ArrayList<>();
		
		uriHelperBestellung.updateUriBestellung(bestellung, uriInfo);
		bestellPosition.setArtikelUri(uriHelperArtikel.getUriArtikel(artikel, uriInfo));
		
		bestellPosition.setBestellungURI(uriHelperBestellung.getUriBestellung(bestellung, uriInfo));
	
		bestellPositionen.add(bestellPosition);
		bestellung.setBestellPositionen(bestellPositionen);
		
		
		
		return bestellung;
	}
	
//	@GET
//	@Path("{id:[1-9][0-9]*}/bestellPosition")
//	public Collection<BestellPosition> findBestellPositionenByBestellungId(@PathParam("id") Long bestellungId) {
//		final Collection<BestellPosition> bestellPositionen = bs.findBestellPositionenByBestellungId(bestellungId);
//		if (bestellPositionen.isEmpty()) {
//			throw new NotFoundException("Zur ID " + bestellungId + " wurden keine BestellPositionen gefunden");
//		}
//		
//		// URLs innerhalb der gefundenen BestellPosition anpassen
//		for (BestellPosition bestellPosition : bestellPositionen) {
//			uriHelperBestellPosition.updateUriBestellPosition(bestellPosition, uriInfo);
//		}
//		
//		return bestellPositionen;
//	}
	
	@POST
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response createBestellung(Bestellung bestellung){
		
		final Locale locale = localeHelper.getLocale(headers);
		
		
		final URI kundeUri = bestellung.getKundeUri();
		final String kundeUriString = kundeUri.toString();
		final String stringKundeId = kundeUriString.substring(kundeUriString.lastIndexOf("/") + 1);
		final Long kundeId = Long.valueOf(stringKundeId);
		final AbstractKunde kunde  = ks.findKundeById(kundeId, locale);
		
		final BestellPosition hilfsBestellPosition = new BestellPosition();
		final BestellPosition bestellPosition = bp.createBestellPosition(hilfsBestellPosition);
		
		
		
		bestellung.setKunde(kunde);
//		kunde.getBestellungen().add(bestellung);
		
		ArrayList<BestellPosition> bestellPositionen = new ArrayList<>();
		final URI artikelURI = uriHelperArtikel.getUriArtikel(bestellPosition.getArtikel(), uriInfo);

		bestellPosition.setArtikelUri(artikelURI);
		bestellPositionen.add(bestellPosition);
		bestellung.setBestellPositionen(bestellPositionen);	
		bestellung = bs.createBestellung(bestellung, locale);
		
		final URI bestellungUri = uriHelperBestellung.getUriBestellung(bestellung, uriInfo);
		return Response.created(bestellungUri).build();
		
		
		
	}
}