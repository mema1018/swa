package de.shop.bestellverwaltung.rest;

import static de.shop.util.TestConstants.USERNAME_ADMIN;
import static de.shop.util.TestConstants.PASSWORD_ADMIN;
import static de.shop.util.TestConstants.ARTIKEL_URI;
import static de.shop.util.TestConstants.ARTIKEL_BEZEICHNUNG;
import static de.shop.util.TestConstants.ARTIKEL_PREIS;
import static de.shop.util.TestConstants.BESTELLUNGEN_ID_KUNDE_URI;
import static de.shop.util.TestConstants.BESTELLUNGEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.BESTELLUNGEN_ID_URI;
import static de.shop.util.TestConstants.BESTELLUNGEN_URI;
import static de.shop.util.TestConstants.KUNDEN_ID_URI;
import static de.shop.util.TestConstants.KUNDEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.USERNAME;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static de.shop.util.TestConstants.PASSWORD_FALSCH;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static java.util.Locale.ENGLISH;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;

import static javax.ws.rs.client.Entity.json;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.filter;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import de.shop.util.AbstractResourceTest;

@RunWith(Arquillian.class)
public class BestellungResourceTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass().getName());

	private static final Long BESTELLUNG_ID_VORHANDEN = Long.valueOf(401);
	private static final Long KONSTANTE = Long.valueOf(1000);
	private static final Long BESTELLUNG_FINDEN = Long.valueOf(401);
	private static final Long BESTELLUNG_ID_NICHT_VORHANDEN = Long.valueOf(800);
	private static final String BEGINN = "BEGINN";
	private static final String ENDE = "ENDE";
	@Test
	@InSequence(20)
	public void findBestellungById() {
		LOGGER.finer(BEGINN);

		// Given
		final Long bestellungId = BESTELLUNG_ID_VORHANDEN;

		// When
		final Response response = getHttpsClient().target(BESTELLUNGEN_ID_URI)
				.resolveTemplate(BESTELLUNGEN_ID_PATH_PARAM, bestellungId)
				.request().accept(APPLICATION_JSON).get();

		// Then
		assertThat(response.getStatus()).isEqualTo(HTTP_OK);
		final Bestellung bestellung = response.readEntity(Bestellung.class);

		assertThat(bestellung.getId()).isEqualTo(bestellungId);
		assertThat(bestellung.getBestellpositionen()).isNotEmpty();

		LOGGER.finer(ENDE);
	}

	@Test
	@InSequence(21)
	public void findBestellungIdNichtVorhanden() {

		LOGGER.finer(BEGINN);
		// Given
		final Long bestellungId = KONSTANTE;

		// When
		final Response response = getHttpsClient().target(BESTELLUNGEN_ID_URI)
				.resolveTemplate(BESTELLUNGEN_ID_PATH_PARAM, bestellungId)
				.request().accept(APPLICATION_JSON).get();

		// Then
		assertThat(response.getStatus()).isEqualTo(HTTP_NOT_FOUND);
		LOGGER.finer(ENDE);

	}

	@Test
	@InSequence(22)
	public void createBestellung() throws URISyntaxException {
		LOGGER.finer(BEGINN);

		// Given
		final Long artikelId1 = Long.valueOf(301);
		final Long artikelId2 = Long.valueOf(302);

		// Neues, client-seitiges Bestellungsobjekt als JSON-Datensatz
		final Bestellung bestellung = new Bestellung();

		Bestellposition bp = new Bestellposition();
		bp.setArtikelUri(new URI(ARTIKEL_URI + "/" + artikelId1));
		bp.setAnzahl((short) 1);
		bestellung.addBestellposition(bp);

		bp = new Bestellposition();
		bp.setArtikelUri(new URI(ARTIKEL_URI + "/" + artikelId2));
		bp.setAnzahl((short) 1);
		bestellung.addBestellposition(bp);

		// When
		Long id;
		Response response = getHttpsClient(USERNAME_ADMIN, PASSWORD_ADMIN)
				.target(BESTELLUNGEN_URI).request().post(json(bestellung));

		// Then
		assertThat(response.getStatus()).isEqualTo(HTTP_CREATED);
		final String location = response.getLocation().toString();
		response.close();

		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		id = Long.valueOf(idStr);
		assertThat(id).isPositive();

		// Gibt es die neue Bestellung?
		response = getHttpsClient().target(BESTELLUNGEN_ID_URI)
				.resolveTemplate(BESTELLUNGEN_ID_PATH_PARAM, id).request()
				.accept(APPLICATION_JSON).get();
		assertThat(response.getStatus()).isEqualTo(HTTP_OK);
		response.close();

		LOGGER.finer(ENDE);
	}

	@Test
	@InSequence(23)
	public void createBestellungInvalid() throws URISyntaxException {
		LOGGER.finer(BEGINN);

		// Given
		final Long artikelId1 = Long.valueOf(301);
		final Long artikelId2 = Long.valueOf(302);

		// Neues, client-seitiges Bestellungsobjekt als JSON-Datensatz
		final Bestellung bestellung = new Bestellung();

		Bestellposition bp = new Bestellposition();
		bp.setArtikelUri(new URI(ARTIKEL_URI + "/" + artikelId1));
		bp.setAnzahl((short) 0);
		bestellung.addBestellposition(bp);

		bp = new Bestellposition();
		bp.setArtikelUri(new URI(ARTIKEL_URI + "/" + artikelId2));
		bp.setAnzahl((short) 0);
		bestellung.addBestellposition(bp);

		// When
		final Response response = getHttpsClient(USERNAME_ADMIN, PASSWORD_ADMIN)
				.target(BESTELLUNGEN_URI).request().accept(APPLICATION_JSON)
				// engl. Fehlermeldungen ohne Umlaute ;-)
				.acceptLanguage(ENGLISH).post(json(bestellung));

		// Then
		assertThat(response.getStatus()).isEqualTo(HTTP_BAD_REQUEST);
		assertThat(response.getHeaderString("validation-exception")).isEqualTo(
				"true");
		final ViolationReport violationReport = response
				.readEntity(ViolationReport.class);
		response.close();

		final List<ResteasyConstraintViolation> violations = violationReport
				.getParameterViolations();
		assertThat(violations).isNotEmpty();

		final ResteasyConstraintViolation violation = filter(violations)
				.with("message")
				.equalsTo("The number must have at least a lot of first.")
				.get().iterator().next();
		assertThat(violation.getValue()).isEqualTo(
				String.valueOf(bp.getAnzahl()));

		LOGGER.finer(ENDE);
	}

	@Test
	@InSequence(24)
	public void createBestellungFalschesPassword() {
		LOGGER.finer(BEGINN);

		// Given
		// Bei falschem Passwort muss der Inhalt des JSON-Datensatzes egal sein
		final Bestellung bestellung = new Bestellung();

		final Bestellposition bestellPosition = new Bestellposition();

		final Artikel artikel = new Artikel();
		artikel.setBezeichnung(ARTIKEL_BEZEICHNUNG);
		artikel.setPreis(ARTIKEL_PREIS);

		bestellPosition.setArtikel(artikel);
		final List<Bestellposition> bestellPositionList = new ArrayList<Bestellposition>();
		bestellPositionList.add(bestellPosition);

		bestellung.setBestellpositionen(bestellPositionList);

		// When
		final Response response = getHttpsClient(USERNAME, PASSWORD_FALSCH)
				.target(BESTELLUNGEN_URI).request().post(json(bestellung));

		// Then
		assertThat(response.getStatus()).isEqualTo(HTTP_UNAUTHORIZED);
		response.close();

		LOGGER.finer(ENDE);
	}

	@Test
	@InSequence(25)
	public void findKundeByBestellungId() {
		LOGGER.finer(BEGINN);

		// Given
		final Long bestellungId = Long.valueOf(BESTELLUNG_FINDEN);

		// When

		/*
		 * Zunächst Kundenobjekt anhand ID suchen
		 */
		 Response response = getHttpsClient().target(BESTELLUNGEN_ID_KUNDE_URI)
				.resolveTemplate(BESTELLUNGEN_ID_PATH_PARAM, bestellungId)
				.request().accept(APPLICATION_JSON).get();
		// Then
		assertThat(response.getStatus()).isEqualTo(HTTP_OK);
		/*
		 * Wenn HTTP_OK, dann Kundenobjekt auslesen und überprüfen ob dieses
		 * nicht NULL ist, anschließend Kunde anhand der URI und der gefundenen
		 * ID suchen
		 */
		final AbstractKunde kunde = response.readEntity(AbstractKunde.class);
		assertThat(kunde).isNotNull();

		response = getHttpsClient().target(KUNDEN_ID_URI)
				.resolveTemplate(KUNDEN_ID_PATH_PARAM, kunde.getId()).request()
				.accept(APPLICATION_JSON).get();

		/*
		 * Abschließend nochmals überprüfen auf HTTP_OK und ob die Links auch
		 * gesetzt sind
		 */
		assertThat(response.getStatus()).isEqualTo(HTTP_OK);
		assertThat(response.getLinks()).isNotEmpty();

		LOGGER.finer(ENDE);

	}

	@Test
	@InSequence(25)
	public void findKundeByBestellungIdNichtVorhanden() {
		LOGGER.finer(BEGINN);

		// Given
		final Long bestellungId = Long.valueOf(BESTELLUNG_ID_NICHT_VORHANDEN);

		final Response response = getHttpsClient().target(BESTELLUNGEN_ID_KUNDE_URI)
				.resolveTemplate(BESTELLUNGEN_ID_PATH_PARAM, bestellungId)
				.request().accept(APPLICATION_JSON).get();

		assertThat(response.getStatus()).isEqualTo(HTTP_NOT_FOUND);
		final String fehlermeldung = response.readEntity(String.class);
		assertThat(fehlermeldung).startsWith("Kein Kunde zu der Bestellung")
				.endsWith("gefunden.");
		LOGGER.finer(ENDE);

	}

}
