package de.shop.kundenverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;
import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.FIRST_LINK;
import static de.shop.util.Constants.LAST_LINK;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
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
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import com.google.common.base.Strings;

import static de.shop.util.Constants.SELF_LINK;
import static de.shop.util.Constants.ADD_LINK;
import static de.shop.util.Constants.UPDATE_LINK;
import static de.shop.util.Constants.LIST_LINK;
import de.shop.auth.service.AuthService;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.rest.UriHelperBestellung;
import de.shop.bestellverwaltung.service.BestellungService;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.Log;
import de.shop.util.NotFoundException;
import de.shop.util.UriHelper;
import de.shop.util.persistence.File;

@Path("/kunde")
@Produces({ APPLICATION_JSON, APPLICATION_XML + ";qs=0.75",
		TEXT_XML + ";qs=0.5" })
@Consumes
@Log
@RequestScoped
public class KundeResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass());
	private static final String VERSION = "1.0";
	public static final String KUNDEN_ID_PATH_PARAM = "id";
	private static final String NOT_FOUND_ID = "kunde.notFound.id";
	private static final String NOT_FOUND_FILE = "kunde.notFound.File";
	private static final String NOT_FOUND_NACHNAME = "kunde.notFound.nachname";
	public static final String KUNDEN_NACHNAME_QUERY_PARAM = "nachname";
	public static final String KUNDEN_GESCHLECHT_QUERY_PARAM = "geschlecht";
	
	
	@Inject
	private AuthService as;
	
	@Context
	private UriInfo uriInfo;

	@Inject
	private UriHelper uriHelper;

	@Inject
	private KundeService ks;

	@Inject
	private BestellungService bs;

	@Inject
	private UriHelperKunde uriHelperKunde;

	@Inject
	private UriHelperBestellung uriHelperBestellung;

	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}

	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}

	@GET
	@Produces(TEXT_PLAIN)
	@Path("version")
	public String getVersion() {
		return VERSION;
	}

	public Link[] getTransitionalLinks(AbstractKunde kunde, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriKunde(kunde, uriInfo))
				.rel(SELF_LINK).build();

		final Link list = Link
				.fromUri(uriHelper.getUri(KundeResource.class, uriInfo))
				.rel(LIST_LINK).build();

		final Link add = Link
				.fromUri(uriHelper.getUri(KundeResource.class, uriInfo))
				.rel(ADD_LINK).build();

		final Link update = Link
				.fromUri(uriHelper.getUri(KundeResource.class, uriInfo))
				.rel(UPDATE_LINK).build();

		final Link remove = Link.fromUri(getUriKunde(kunde, uriInfo))
				.rel("REMOVE_LINK").build();

		return new Link[] {self, list, add, update, remove};
	}

	public URI getUriKunde(AbstractKunde kunde, UriInfo uriInfo) {
		return uriHelper.getUri(KundeResource.class, "findKundeById",
				kunde.getId(), uriInfo);
	}

	//
	private URI getUriBestellungen(AbstractKunde kunde, UriInfo uriInfo) {
		return uriHelper.getUri(KundeResource.class,
				"findBestellungenByKundeId", kunde.getId(), uriInfo);
	}

	public void setStructuralLinks(AbstractKunde kunde, UriInfo uriInfo) {
		// URI fuer Bestellungen setzen
		final URI uri = getUriBestellungen(kunde, uriInfo);
		kunde.setBestellungenUri(uri);

		LOGGER.trace(kunde);
	}

	@SuppressWarnings("unused")
	private Link[] getTransitionalLinksKunden(
			List<? extends AbstractKunde> kunden, UriInfo uriInfo) {
		if (kunden == null || kunden.isEmpty()) {
			return null;
		}

		final Link first = Link.fromUri(getUriKunde(kunden.get(0), uriInfo))
				.rel(FIRST_LINK).build();
		final int lastPos = kunden.size() - 1;
		final Link last = Link
				.fromUri(getUriKunde(kunden.get(lastPos), uriInfo))
				.rel(LAST_LINK).build();

		return new Link[] {first, last};
	}

	/**
	 * Mit der URL /kunden/{id} einen Kunden ermitteln
	 * 
	 * @param id
	 *            ID des Kunden
	 * @return Objekt mit Kundendaten, falls die ID vorhanden ist
	 */

	@GET
	@Path("{" + KUNDEN_ID_PATH_PARAM + ":[1-9][0-9]*}")
	public Response findKundeById(@PathParam(KUNDEN_ID_PATH_PARAM) Long id) {
		final AbstractKunde kunde = ks.findKundeById(id, FetchType.NUR_KUNDE);
		if (kunde == null) {
			throw new NotFoundException(NOT_FOUND_ID, id);
		}

		setStructuralLinks(kunde, uriInfo);
		return Response.ok(kunde).links(getTransitionalLinks(kunde, uriInfo))
				.build();
	}

	/**
	 * Mit der URL /kunden werden alle Kunden ermittelt oder mit
	 * kundenverwaltung/kunden?nachname=... diejenigen mit einem bestimmten
	 * Nachnamen.
	 * 
	 * @return Collection mit den gefundenen Kundendaten
	 */
	@GET
	public Collection<AbstractKunde> findKundenByNachname(
			@QueryParam("nachname") @DefaultValue("") String nachname) {
		Collection<AbstractKunde> kunden = null;
		if ("".equals(nachname)) {
			kunden = ks.findAllKunden(FetchType.NUR_KUNDE, null);
			if (kunden.isEmpty()) {
				throw new NotFoundException(NOT_FOUND_NACHNAME, nachname);
			}
		} 
		
		else {

			kunden = ks.findKundenByNachname(nachname, FetchType.NUR_KUNDE);
			if (kunden.isEmpty()) {
				throw new NotFoundException(NOT_FOUND_NACHNAME, nachname);
			}
		}

		// URLs innerhalb der gefundenen Kunden anpassen
		for (AbstractKunde kunde : kunden) {
			uriHelperKunde.updateUriKunde(kunde, uriInfo);
		}

		return kunden;
	}

	@GET
	@Path("/prefix/id/{id:[1-9][0-9]*}")
	public Collection<Long> findIdsByPrefix(@PathParam("id") String idPrefix) {
		final Collection<Long> ids = ks.findIdsByPrefix(idPrefix);
		return ids;
	}

	/**
	 * Mit der URL /kunden/{id}/bestellungen die Bestellungen zu eine Kunden
	 * ermitteln
	 * 
	 * @param kundeId
	 *            ID des Kunden
	 * @return Objekt mit Bestellungsdaten, falls die ID vorhanden ist
	 */
	@GET
	@Path("{id:[1-9][0-9]*}/bestellungen")
	public Collection<Bestellung> findBestellungenByKundeId(
			@PathParam("id") Long kundeId) {

		final AbstractKunde kunde = ks.findKundeById(kundeId,
				FetchType.MIT_BESTELLUNGEN);
		if (kunde == null) {
			throw new NotFoundException("Kein Kunde mit der ID " + kundeId
					+ " gefunden.");
		}

		final Collection<Bestellung> bestellungen = bs
				.findBestellungenByKunde(kunde);

		// URLs innerhalb der gefundenen Bestellungen anpassen
		for (Bestellung bestellung : bestellungen) {
			uriHelperBestellung.updateUriBestellung(bestellung, uriInfo);
		}

		return bestellungen;
	}

	@GET
	@Path("{id:[1-9][0-9]*}/bestellungenIds")
	public Collection<Long> findBestellungenIdsByKundeId(
			@PathParam("id") Long kundeId) {
		final Collection<Bestellung> bestellungen = findBestellungenByKundeId(kundeId);
		if (bestellungen.isEmpty()) {
			final String msg = "Kein Bestellung fuer den Kunden mit der ID "
					+ kundeId + " gefunden.";
			throw new NotFoundException(msg);
		}

		final int anzahl = bestellungen.size();
		final Collection<Long> bestellungenIds = new ArrayList<>(anzahl);
		for (Bestellung bestellung : bestellungen) {
			bestellungenIds.add(bestellung.getId());
		}

		return bestellungenIds;
	}

	/**
	 * Mit der URL /kunden einen Privatkunden per POST anlegen.
	 * 
	 * @param kunde
	 *            neuer Kunde
	 * @return Response-Objekt mit URL des neuen Privatkunden
	 */

	@POST
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	@Transactional
	public Response createKunde(@Valid AbstractKunde kunde) {
		kunde.setId(KEINE_ID);
		kunde.setPassword(as.verschluesseln(kunde.getPassword()));
		final Adresse adresse = kunde.getAdresse();
		if (adresse != null) {
			adresse.setKunde(kunde);
		}
		if (Strings.isNullOrEmpty(kunde.getPasswordWdh())) {
			// ein IT-System als REST-Client muss das Password ggf. nur 1x
			// uebertragen
			kunde.setPasswordWdh(kunde.getPassword());
		}

		kunde = ks.createKunde(kunde);
		LOGGER.trace(kunde);

		return Response.created(getUriKunde(kunde, uriInfo)).build();
	}

	/**
	 * Mit der URL /kunden einen Kunden per PUT aktualisieren
	 * 
	 * @param kunde
	 *            zu aktualisierende Daten des Kunden
	 * @throws Exception
	 */
	@PUT
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Transactional
	public Response updateKunde(@Valid AbstractKunde kunde) throws Exception {
		// Vorhandenen Kunden ermitteln
		final AbstractKunde origKunde = ks.findKundeById(kunde.getId(),
				FetchType.NUR_KUNDE);
		if (origKunde == null) {
			throw new NotFoundException(NOT_FOUND_ID, kunde.getId());
		}

		LOGGER.tracef("Kunde vorher = %s", origKunde);

		// Daten des vorhandenen Kunden ueberschreiben
		origKunde.setValues(kunde);
		LOGGER.tracef("Kunde nachher = %s", origKunde);

		// Update durchfuehren
		kunde = ks.updateKunde(origKunde, false);
		setStructuralLinks(kunde, uriInfo);

		return Response.ok(kunde).links(getTransitionalLinks(kunde, uriInfo))
				.build();
	}

	@Path("{id:[1-9][0-9]*}/file")
	@POST
	@Consumes({ "image/jpeg", "image/pjpeg", "image/png" })
	@Transactional
	public Response upload(@PathParam("id") Long kundeId, byte[] bytes) {
		ks.setFile(kundeId, bytes);
		return Response.created(
				uriHelper.getUri(KundeResource.class, "download", kundeId,
						uriInfo)).build();
	}

	@Path("{id:[1-9][0-9]*}/file")
	@GET
	@Produces({ "image/jpeg", "image/pjpeg", "image/png" })
	@Transactional
	// Nachladen der Datei : AbstractKunde referenziert File mit Lazy Fetching
	public byte[] download(@PathParam("id") Long kundeId) {
		final AbstractKunde kunde = ks.findKundeById(kundeId,
				FetchType.NUR_KUNDE);
		if (kunde == null) {
			throw new NotFoundException(NOT_FOUND_ID, kundeId);
		}

		final File file = kunde.getFile();
		if (file == null) {
			throw new NotFoundException(NOT_FOUND_FILE, kundeId);
		}
		LOGGER.tracef("%s", file.toString());

		return file.getBytes();
	}

}
