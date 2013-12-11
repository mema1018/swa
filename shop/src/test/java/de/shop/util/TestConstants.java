package de.shop.util;

import static de.shop.util.Constants.REST_PATH;

/**
 * @author <a href="mailto:Juergen.Zimmermann@HS-Karlsruhe.de">J&uuml;rgen
 *         Zimmermann</a>
 */
public final class TestConstants {
	public static final String WEB_PROJEKT = "shop";

	// https
	public static final String HTTPS = "https";
	public static final String HOST = "localhost";
	public static final int PORT = 8443;
	public static final String KEYSTORE_TYPE = "JKS";
	public static final String TRUSTSTORE_NAME = "client.truststore";
	public static final String TRUSTSTORE_PASSWORD = "Zimmermann";

	// Basis-URI
	private static final String BASE_URI = HTTPS + "://" + HOST + ":" + PORT
			+ "/" + WEB_PROJEKT + REST_PATH;

	// Pfade und Pfad-Parameter
	public static final String KUNDEN_URI = BASE_URI + "/kunde";
	public static final String KUNDEN_ID_PATH_PARAM = "kundeId";
	public static final String KUNDEN_ID_URI = KUNDEN_URI + "/{"
			+ KUNDEN_ID_PATH_PARAM + "}";
	public static final String KUNDEN_ID_FILE_URI = KUNDEN_ID_URI + "/file";
	// Pfade und Pfad-Parameter

	public static final String BESTELLUNGEN_URI = BASE_URI + "/bestellungen";
	public static final String BESTELLUNGEN_ID_PATH_PARAM = "bestellungId";
	public static final String BESTELLUNGEN_ID_URI = BESTELLUNGEN_URI + "/{"
			+ BESTELLUNGEN_ID_PATH_PARAM + "}";
	public static final String BESTELLUNGEN_ID_KUNDE_URI = BESTELLUNGEN_ID_URI
			+ "/kunde";

	public static final String ARTIKEL_URI = BASE_URI + "/artikel";
	public static final String ARTIKEL_ID_PATH_PARAM = "artikelID";
	public static final String ARTIKEL_ID_URI = ARTIKEL_URI + "/{"
			+ ARTIKEL_ID_PATH_PARAM + "}";

	// Username und Password
	public static final String USERNAME = "102";
	public static final String PASSWORD = "102";
	public static final String USERNAME_ADMIN = "101";
	public static final String PASSWORD_ADMIN = "101";
	public static final String PASSWORD_FALSCH = "falsch";

	// Testklassen fuer Service- und Domain-Tests (nicht in Software
	// Engineering)
	public static final Class<?>[] TEST_CLASSES = {};

	// Artikel

	public static final long ARTIKEL_ID_VORHANDEN = 301;
	public static final long ARTIKEL_ID_NICHT_VORHANDEN = 310;
	public static final String ARTIKEL_BEZEICHNUNG = "Tisch: Mit Flecken";
	public static final double ARTIKEL_PREIS = 10;
	public static final String ARTIKEL_BEZEICHNUNG_FALSCH = "";

	// Kunde

	public static final String NEUE_PLZ_FALSCH = "758";
	public static final String NEUE_EMAIL_OHNE = "ohneAnmeldung@test.de";
	public static final long KUNDE_ID = 101;
	public static final long KUNDE_ID_NICHT_VORHANDEN = 500;
	public static final long KUNDE_ID_UPDATE = 102;
	public static final long KUNDE_ID_UPLOAD = Long.valueOf(102);
	public static final String IMAGE_FILENAME = "image.png";
	public static final String IMAGE_PATH_UPLOAD = "src/test/resources/rest/"
			+ IMAGE_FILENAME;
	public static final String IMAGE_MIMETYPE = "image/png";
	public static final String IMAGE_PATH_DOWNLOAD = "target/" + IMAGE_FILENAME;
	public static final String IMAGE_INVALID = "image.png";
	public static final String IMAGE_INVALID_PATH = "src/test/resources/rest/"
			+ IMAGE_INVALID;
	public static final String IMAGE_INVALID_MIMETYPE = "image/pnd";

	private TestConstants() {
	}
}
