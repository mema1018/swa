package de.shop.kundenverwaltung.rest;

import static de.shop.util.TestConstants.USERNAME;
import static de.shop.util.TestConstants.KUNDE_ID_UPDATE;
import static de.shop.util.TestConstants.ARTIKEL_URI;
//import static de.shop.util.Constants.FIRST_LINK;
//import static de.shop.util.Constants.LAST_LINK;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNSUPPORTED_TYPE;
import static java.util.Locale.ENGLISH;
import static javax.ws.rs.client.Entity.entity;
//import static de.shop.util.TestConstants.IMAGE_PATH_DOWNLOAD;
import static de.shop.util.TestConstants.IMAGE_INVALID_MIMETYPE;
import static de.shop.util.TestConstants.KUNDEN_ID_FILE_URI;
import static de.shop.util.TestConstants.IMAGE_INVALID_PATH;
import static java.util.Locale.GERMAN;
import static de.shop.util.TestConstants.KUNDE_ID_UPLOAD;
//import static de.shop.util.TestConstants.IMAGE_MIMETYPE;
//import static de.shop.util.TestConstants.IMAGE_PATH_UPLOAD;
import static de.shop.util.TestConstants.KUNDEN_URI;
import static de.shop.util.TestConstants.BESTELLUNGEN_URI;
import static de.shop.util.TestConstants.PASSWORD;
import static de.shop.util.TestConstants.NEUE_PLZ_FALSCH;
import static de.shop.util.TestConstants.NEUE_EMAIL_OHNE;
import static de.shop.util.TestConstants.KUNDE_ID;
import static de.shop.util.TestConstants.KUNDEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.KUNDEN_ID_URI;
import static de.shop.util.TestConstants.KUNDE_ID_NICHT_VORHANDEN;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.filter;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
//import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.auth.domain.RolleType;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Privatkunde;
import de.shop.util.AbstractResourceTest;

import javax.ws.rs.core.Response;

@RunWith(Arquillian.class)
public class KundeResourceTest extends AbstractResourceTest {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass().getName());

	private static final String NEUER_NACHNAME = "Nachnameneu";
	private static final String NEUER_VORNAME = "Vorname";
	private static final String NACHNAME_NICHT_VORHANDEN = "Falschername";
	private static final String NEUER_NACHNAME_INVALID = "!";
	private static final String NEUE_EMAIL = NEUER_NACHNAME + "@test.de";
	private static final BigDecimal NEUER_UMSATZ = new BigDecimal(10_000_000);
	private static final Integer KONSTANTE_DATE_EINS = 2000;
	private static final Integer KONSTANTE_DATE_ZWEI = 31;
	private static final Date NEU_SEIT = new GregorianCalendar(KONSTANTE_DATE_EINS, 0 , KONSTANTE_DATE_ZWEI)
			.getTime();
	private static final String NEUE_PLZ = "76133";
	private static final String NEUER_ORT = "Karlsruhe";
	private static final String NEUE_STRASSE = "Testweg";
	private static final String NEUE_HAUSNR = "1";
	private static final String NEUES_PASSWORD = "neuesPassword";
	private static final Long ARTIKEL_ID_VORHANDEN = Long.valueOf(301);
	private static final short ANZAHL_ARTIKEL = 3;
	private static final String BEGINN = "BEGINN";
	private static final String ENDE = "ENDE";
	private static final BigDecimal RABATT = new BigDecimal(0_1);

	@Test
	@InSequence(1)
	public void validate() {
		assertThat(true).isTrue();
	}

	@Ignore
	@Test
	@InSequence(2)
	public void beispielIgnore() {
		assertThat(true).isFalse();
	}

	@Ignore
	@Test
	@InSequence(3)
	public void beispielFailMitIgnore() {
		fail("Beispiel fuer fail()");
	}

	@Test
	@InSequence(21)
	public void findKundenByNachnameNichtVorhanden() {
		LOGGER.finer(BEGINN);

		// Given
		final String nachname = NACHNAME_NICHT_VORHANDEN;

		// When
		final Response response = getHttpsClient()
				.target(KUNDEN_URI)
				.queryParam(KundeResource.KUNDEN_NACHNAME_QUERY_PARAM, nachname)
				.request().acceptLanguage(GERMAN).get();

		// Then
		assertThat(response.getStatus()).isEqualTo(HTTP_NOT_FOUND);
		final String fehlermeldung = response.readEntity(String.class);
		assertThat(fehlermeldung).startsWith("Kein Kunde")
				.endsWith("gefunden.");

		LOGGER.finer(ENDE);
	}

	@Test
	@InSequence(41)
	public void createPrivatkundeNachnameInvalid() {
		LOGGER.finer(BEGINN);

		// Given
		final String nachname = NEUER_NACHNAME_INVALID;
		final String vorname = NEUER_VORNAME;
		final String email = NEUE_EMAIL;
		final BigDecimal umsatz = NEUER_UMSATZ;
		final Date seit = NEU_SEIT;
		final String plz = NEUE_PLZ;
		final String ort = NEUER_ORT;
		final String strasse = NEUE_STRASSE;
		final String hausnr = NEUE_HAUSNR;
		final String neuesPassword = NEUES_PASSWORD;

		final Privatkunde kunde = new Privatkunde();
		kunde.setVorname(vorname);
		kunde.setNachname(nachname);
		kunde.setEmail(email);
		kunde.setSeit(seit);
		kunde.setVorname(vorname);
		kunde.setUmsatz(umsatz);
		kunde.setRabatt(RABATT);

		final Adresse adresse = new Adresse();
		adresse.setPlz(plz);
		adresse.setOrt(ort);
		adresse.setStrasse(strasse);
		adresse.setHausnr(hausnr);

		kunde.setAdresse(adresse);
		kunde.setPassword(neuesPassword);
		kunde.setPasswordWdh(neuesPassword);
		kunde.addRollen(Arrays.asList(RolleType.KUNDE));

		// When
		final Response response = getHttpsClient(USERNAME, PASSWORD)
				.target(KUNDEN_URI).request().accept(APPLICATION_JSON)
				// engl. Fehlermeldungen ohne Umlaute ;-)
				.acceptLanguage(ENGLISH).post(json(kunde));

		// Then
		assertThat(response.getStatus()).isEqualTo(HTTP_BAD_REQUEST);
		assertThat(response.getHeaderString("validation-exception")).isEqualTo(
				"true");

		response.close();

		LOGGER.finer(ENDE);
	}

	@Test
	@InSequence(40)
	public void createPrivatkundeMitAnmeldung() throws URISyntaxException {
		LOGGER.finer(BEGINN);

		// Given
		final String nachname = NEUER_NACHNAME;
		final String vorname = NEUER_VORNAME;
		final String email = NEUE_EMAIL;
		final BigDecimal umsatz = NEUER_UMSATZ;
		final Date seit = NEU_SEIT;
		final String plz = NEUE_PLZ;
		final String ort = NEUER_ORT;
		final String strasse = NEUE_STRASSE;
		final String hausnr = NEUE_HAUSNR;
		final String neuesPassword = NEUES_PASSWORD;

		final Privatkunde kunde = new Privatkunde();
		kunde.setVorname(vorname);
		kunde.setNachname(nachname);
		kunde.setEmail(email);
		kunde.setSeit(seit);
		kunde.setVorname(vorname);
		kunde.setUmsatz(umsatz);
		kunde.setRabatt(RABATT);

		final Adresse adresse = new Adresse();
		adresse.setPlz(plz);
		adresse.setOrt(ort);
		adresse.setStrasse(strasse);
		adresse.setHausnr(hausnr);

		kunde.setAdresse(adresse);
		kunde.setPassword(neuesPassword);
		kunde.setPasswordWdh(neuesPassword);
		kunde.addRollen(Arrays.asList(RolleType.KUNDE));

		Response response = getHttpsClient(USERNAME, PASSWORD)
				.target(KUNDEN_URI).request().accept(APPLICATION_JSON)
				.post(json(kunde));

		// Then
		assertThat(response.getStatus()).isEqualTo(HTTP_CREATED);
		String location = response.getLocation().toString();
		response.close();

		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id).isPositive();

		// Einloggen als neuer Kunde und Bestellung aufgeben

		// Given (2)
		final Long artikelId = ARTIKEL_ID_VORHANDEN;
		final String username = id.toString();

		// When (2)
		final Bestellung bestellung = new Bestellung();
		final Bestellposition bp = new Bestellposition();
		bp.setArtikelUri(new URI(ARTIKEL_URI + "/" + artikelId));
		bp.setAnzahl(ANZAHL_ARTIKEL);
		bestellung.addBestellposition(bp);
		bestellung.setKunde(kunde);

		// Then (2)
		response = getHttpsClient(username, neuesPassword)
				.target(BESTELLUNGEN_URI).request().accept(APPLICATION_JSON)
				.post(json(bestellung));

		assertThat(response.getStatus()).isEqualTo(HTTP_CREATED);
		location = response.getLocation().toString();
		response.close();
		assertThat(location).isNotEmpty();

		LOGGER.finer(ENDE);

	}

	@Test
	@InSequence(41)
	public void createPrivatkundeOhneAnmeldung() {

		LOGGER.finer(BEGINN);
		// Given
		final String nachname = NEUER_NACHNAME;
		final String vorname = NEUER_VORNAME;
		final String email = NEUE_EMAIL_OHNE;
		final BigDecimal umsatz = NEUER_UMSATZ;
		final Date seit = NEU_SEIT;
		final String plz = NEUE_PLZ;
		final String ort = NEUER_ORT;
		final String strasse = NEUE_STRASSE;
		final String hausnr = NEUE_HAUSNR;
		final String neuesPassword = NEUES_PASSWORD;

		final Privatkunde kunde = new Privatkunde();
		kunde.setVorname(vorname);
		kunde.setNachname(nachname);
		kunde.setEmail(email);
		kunde.setSeit(seit);
		kunde.setVorname(vorname);
		kunde.setUmsatz(umsatz);
		kunde.setRabatt(RABATT);
		
		final Adresse adresse = new Adresse();
		adresse.setPlz(plz);
		adresse.setOrt(ort);
		adresse.setStrasse(strasse);
		adresse.setHausnr(hausnr);

		kunde.setAdresse(adresse);
		kunde.setPassword(neuesPassword);
		kunde.setPasswordWdh(neuesPassword);
		kunde.addRollen(Arrays.asList(RolleType.KUNDE));

		final Response response = getHttpsClient().target(KUNDEN_URI).request()
				.accept(APPLICATION_JSON).post(json(kunde));

		// Then
		assertThat(response.getStatus()).isEqualTo(HTTP_CREATED);
		response.close();

		LOGGER.finer(ENDE);

	}

	// @Test
	// @InSequence(30)
	// public void findKundenByGeschlecht() {
	// LOGGER.finer(BEGINN);
	//
	// for (GeschlechtType geschlecht : GeschlechtType.values()) {
	// // When
	// final Response response = getHttpsClient().target(KUNDEN_URI)
	// .queryParam(KundeResource.KUNDEN_GESCHLECHT_QUERY_PARAM,
	// geschlecht)
	// .request()
	// .accept(APPLICATION_JSON)
	// .get();
	// final Collection<Privatkunde> kunden = response.readEntity(new
	// GenericType<Collection<Privatkunde>>() { });
	//
	// // Then
	// assertThat(kunden).isNotEmpty() // siehe Testdaten
	// .doesNotContainNull()
	// .doesNotHaveDuplicates();
	// for (Privatkunde k : kunden) {
	// assertThat(k.getGeschlecht()).isEqualTo(geschlecht);
	// }
	// }
	//
	// LOGGER.finer(ENDE);
	// }

	@Test
	@InSequence(42)
	public void createPrivatkundeValidationFehler() {
		LOGGER.finer(BEGINN);

		// Given
		final String nachname = NEUER_NACHNAME;
		final String vorname = NEUER_VORNAME;
		final String email = NEUE_EMAIL;
		final BigDecimal umsatz = NEUER_UMSATZ;
		final Date seit = NEU_SEIT;
		final String plz = NEUE_PLZ_FALSCH;
		final String ort = NEUER_ORT;
		final String strasse = NEUE_STRASSE;
		final String hausnr = NEUE_HAUSNR;
		final String neuesPassword = NEUES_PASSWORD;

		final Privatkunde kunde = new Privatkunde();
		kunde.setVorname(vorname);
		kunde.setNachname(nachname);
		kunde.setEmail(email);
		kunde.setSeit(seit);
		kunde.setVorname(vorname);
		kunde.setUmsatz(umsatz);
		kunde.setRabatt(RABATT);

		final Adresse adresse = new Adresse();
		adresse.setPlz(plz);
		adresse.setOrt(ort);
		adresse.setStrasse(strasse);
		adresse.setHausnr(hausnr);

		kunde.setAdresse(adresse);
		kunde.setPassword(neuesPassword);
		kunde.setPasswordWdh(neuesPassword);
		kunde.addRollen(Arrays.asList(RolleType.KUNDE));

		final Response response = getHttpsClient().target(KUNDEN_URI).request()
				.accept(APPLICATION_JSON).post(json(kunde));

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
				.equalsTo("Eine PLZ muss eine 5-stellige Zahl sein.").get()
				.iterator().next();
		assertThat(violation.getValue()).isEqualTo(
				String.valueOf(NEUE_PLZ_FALSCH));

		LOGGER.finer(ENDE);

	}

	@Test
	@InSequence(43)
	public void findeKundeById() {

		LOGGER.finer(BEGINN);

		final Response response = getHttpsClient().target(KUNDEN_ID_URI)
				.resolveTemplate(KUNDEN_ID_PATH_PARAM, KUNDE_ID).request()
				.accept(APPLICATION_JSON).get();

		assertThat(response.getStatus()).isEqualTo(HTTP_OK);
		final AbstractKunde kunde = response.readEntity(AbstractKunde.class);

		assertThat(kunde.getId()).isEqualTo(KUNDE_ID);

		LOGGER.finer(ENDE);
	}

	@Test
	@InSequence(44)
	public void findeKundeByIdNichtVorhanden() {

		LOGGER.finer(BEGINN);

		final Response response = getHttpsClient()
				.target(KUNDEN_ID_URI)
				.resolveTemplate(KUNDEN_ID_PATH_PARAM, KUNDE_ID_NICHT_VORHANDEN)
				.request().accept(APPLICATION_JSON).get();

		assertThat(response.getStatus()).isEqualTo(HTTP_NOT_FOUND);
		final String fehlermeldung = response.readEntity(String.class);
		assertThat(fehlermeldung).startsWith("Kein Kunde mit der ID").endsWith(
				"gefunden.");

		LOGGER.finer(ENDE);
	}

	@Test
	@InSequence(45)
	public void updateKunde() {
		LOGGER.finer(BEGINN);

		// Given
		final Long kundeId = KUNDE_ID_UPDATE;
		final String neuerNachname = NEUER_NACHNAME;

		// When
		Response response = getHttpsClient().target(KUNDEN_ID_URI)
				.resolveTemplate(KUNDEN_ID_PATH_PARAM, kundeId).request()
				.accept(APPLICATION_JSON).get();
		AbstractKunde kunde = response.readEntity(AbstractKunde.class);
		assertThat(kunde.getId()).isEqualTo(kundeId);
		final int origVersion = kunde.getVersion();

		// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem
		// Nachnamen bauen
		kunde.setNachname(neuerNachname);

		response = getHttpsClient(USERNAME, PASSWORD).target(KUNDEN_URI)
				.request().accept(APPLICATION_JSON).put(json(kunde));
		// Then
		assertThat(response.getStatus()).isEqualTo(HTTP_OK);
		kunde = response.readEntity(AbstractKunde.class);
		assertThat(kunde.getVersion()).isGreaterThan(origVersion);

		// Erneutes Update funktioniert, da die Versionsnr. aktualisiert ist
		response = getHttpsClient(USERNAME, PASSWORD).target(KUNDEN_URI)
				.request().accept(APPLICATION_JSON).put(json(kunde));
		assertThat(response.getStatus()).isEqualTo(HTTP_OK);
		response.close();

		// Erneutes Update funktioniert NICHT, da die Versionsnr. NICHT
		// aktualisiert ist
		response = getHttpsClient(USERNAME, PASSWORD).target(KUNDEN_URI)
				.request().accept(APPLICATION_JSON).put(json(kunde));
		assertThat(response.getStatus()).isEqualTo(HTTP_CONFLICT);
		response.close();

		LOGGER.finer(ENDE);
	}

	// //TODO Upload Bild geht nicht
	// @Test
	// @InSequence(70)
	// public void uploadDownload() throws IOException{
	// LOGGER.finer(BEGINN);
	//
	// // Given
	// final Long kundeId = KUNDE_ID_UPLOAD;
	// final String path = IMAGE_PATH_UPLOAD;
	// final String mimeType = IMAGE_MIMETYPE;
	//
	// // Datei einlesen
	// final byte[] uploadBytes = Files.readAllBytes(Paths.get(path));
	//
	// // When
	// Response response = getHttpsClient("101",
	// "101").target(KUNDEN_ID_FILE_URI)
	// .resolveTemplate(KUNDEN_ID_PATH_PARAM,
	// kundeId)
	// .request()
	// .post(entity(uploadBytes, mimeType));
	//
	// // Then
	// assertThat(response.getStatus()).isEqualTo(HTTP_CREATED);
	// // id extrahieren aus http://localhost:8080/shop/rest/kunden/<id>/file
	// final String location = response.getLocation().toString();
	// response.close();
	//
	// final String idStr = location.replace(KUNDEN_URI + '/', "")
	// .replace("/file", "");
	// assertThat(idStr).isEqualTo(kundeId.toString());
	//
	// byte[] downloadBytes;
	//
	// response = getHttpsClient("101", "101").target(KUNDEN_ID_FILE_URI)
	// .resolveTemplate(KundeResource.KUNDEN_ID_PATH_PARAM, kundeId)
	// .request()
	// .accept(mimeType)
	// .get();
	//
	// downloadBytes = response.readEntity(new GenericType<byte[]>() {} );
	//
	// // Then (2)
	// assertThat(uploadBytes.length).isEqualTo(downloadBytes.length);
	// assertThat(uploadBytes).isEqualTo(downloadBytes);
	//
	// // Abspeichern des heruntergeladenen byte[] als Datei im Unterverz.
	// target zur manuellen Inspektion
	// Files.write(Paths.get(IMAGE_PATH_DOWNLOAD), downloadBytes);
	// LOGGER.info("Heruntergeladene Datei abgespeichert: " +
	// IMAGE_PATH_DOWNLOAD);
	//
	// LOGGER.finer(ENDE);
	// }

	@Test
	@InSequence(71)
	public void uploadInvalidMimeType() throws IOException {
		LOGGER.finer(BEGINN);

		// Given
		final Long kundeId = KUNDE_ID_UPLOAD;
		final String path = IMAGE_INVALID_PATH;
		final String mimeType = IMAGE_INVALID_MIMETYPE;

		// Datei einlesen
		final byte[] uploadBytes = Files.readAllBytes(Paths.get(path));

		// When
		final Response response = getHttpsClient("101", "101")
				.target(KUNDEN_ID_FILE_URI)
				.resolveTemplate(KUNDEN_ID_PATH_PARAM, kundeId).request()
				.post(entity(uploadBytes, mimeType));

		assertThat(response.getStatus()).isEqualTo(HTTP_UNSUPPORTED_TYPE);
		response.close();
		
		LOGGER.finer(ENDE);
	}

}
