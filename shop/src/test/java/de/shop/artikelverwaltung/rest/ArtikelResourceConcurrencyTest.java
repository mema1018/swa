package de.shop.artikelverwaltung.rest;

import static de.shop.util.TestConstants.ARTIKEL_ID_URI;
import static de.shop.util.TestConstants.ARTIKEL_URI;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH_PARAM;
import static de.shop.util.TestConstants.PASSWORD;
import static de.shop.util.TestConstants.USERNAME;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.fest.assertions.api.Assertions.assertThat;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.AbstractResourceTest;
import de.shop.util.HttpsConcurrencyHelper;

@RunWith(Arquillian.class)
public class ArtikelResourceConcurrencyTest extends AbstractResourceTest {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass().getName());
	private static final long TIMEOUT = 20;

	private static final Long ARTIKEL_ID_UPDATE = Long.valueOf(301);
	private static final String NEUE_BEZEICHNUNG = "Testbezeichnung";
	private static final String NEUE_BEZEICHNUNG_2 = "Neuebezeichnung";

	@Test
	@InSequence(50)
	public void updateUpdate() throws InterruptedException, ExecutionException,
			TimeoutException {
		LOGGER.finer("BEGINN");

		// Given
		final Long artikelId = ARTIKEL_ID_UPDATE;
		final String neueBezeichnung = NEUE_BEZEICHNUNG;
		final String neueBezeichnung2 = NEUE_BEZEICHNUNG_2;

		// When
		Response response = getHttpsClient().target(ARTIKEL_ID_URI)
				.resolveTemplate(ARTIKEL_ID_PATH_PARAM, artikelId).request()
				.accept(APPLICATION_JSON).get();

		final Artikel artikel = response.readEntity(Artikel.class);

		// Konkurrierendes Update
		// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem
		// Nachnamen bauen
		artikel.setBezeichnung(neueBezeichnung2);

		final Callable<Integer> concurrentUpdate = new Callable<Integer>() {
			@Override
			public Integer call() {
				final Response response = new HttpsConcurrencyHelper()
						.getHttpsClient(USERNAME, PASSWORD).target(ARTIKEL_URI)
						.request().accept(APPLICATION_JSON).put(json(artikel));
				final int status = response.getStatus();
				response.close();
				return Integer.valueOf(status);
			}
		};
		final Integer status = Executors.newSingleThreadExecutor()
				.submit(concurrentUpdate).get(TIMEOUT, SECONDS); // Warten bis
																	// der
																	// "parallele"
																	// Thread
																	// fertig
																	// ist
		assertThat(status.intValue()).isEqualTo(HTTP_OK);

		// Fehlschlagendes Update
		// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem
		// Nachnamen bauen
		artikel.setBezeichnung(neueBezeichnung);
		response = getHttpsClient(USERNAME, PASSWORD).target(ARTIKEL_URI)
				.request().accept(APPLICATION_JSON).put(json(artikel));

		// Then
		assertThat(response.getStatus()).isEqualTo(HTTP_CONFLICT);
		response.close();

		LOGGER.finer("ENDE");
	}
}
