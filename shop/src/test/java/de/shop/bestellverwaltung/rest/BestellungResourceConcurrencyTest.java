package de.shop.bestellverwaltung.rest;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;

import de.shop.util.AbstractResourceTest;
import static de.shop.util.TestConstants.BESTELLUNGEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.BESTELLUNGEN_URI;
import static de.shop.util.TestConstants.BESTELLUNGEN_ID_URI;
import static de.shop.util.TestConstants.KUNDEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.KUNDEN_ID_URI;
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

import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.util.HttpsConcurrencyHelper;

@RunWith(Arquillian.class)
public class BestellungResourceConcurrencyTest extends AbstractResourceTest {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass().getName());
	private static final long TIMEOUT = 20;

	private static final Long BESTELLUNG_ID_UPDATE = Long.valueOf(400);
	private static final Long NEUER_KUNDE_PAR = Long.valueOf(104);
	private static final Long NEUER_KUNDE_PAR_ZWEI = Long.valueOf(102);

	@Test
	@InSequence(80)
	public void updateUpdate() throws InterruptedException, ExecutionException,
			TimeoutException {
		LOGGER.finer("BEGINN");

		// Given
		final Long bestellungId = BESTELLUNG_ID_UPDATE;
		final Long neuerKunde = NEUER_KUNDE_PAR;
		final Long neuerKunde2 = NEUER_KUNDE_PAR_ZWEI;

		// When
		Response response = getHttpsClient().target(BESTELLUNGEN_ID_URI)
				.resolveTemplate(BESTELLUNGEN_ID_PATH_PARAM, bestellungId)
				.request().accept(APPLICATION_JSON).get();

		final Bestellung bestellung = response.readEntity(Bestellung.class);

		response = getHttpsClient().target(KUNDEN_ID_URI)
				.resolveTemplate(KUNDEN_ID_PATH_PARAM, neuerKunde).request()
				.accept(APPLICATION_JSON).get();
		final AbstractKunde kunde = response.readEntity(AbstractKunde.class);

		response = getHttpsClient().target(KUNDEN_ID_URI)
				.resolveTemplate(KUNDEN_ID_PATH_PARAM, neuerKunde2).request()
				.accept(APPLICATION_JSON).get();
		final AbstractKunde kunde2 = response.readEntity(AbstractKunde.class);

		bestellung.setKunde(kunde);

		final Callable<Integer> concurrentUpdate = new Callable<Integer>() {
			@Override
			public Integer call() {
				final Response response = new HttpsConcurrencyHelper()
						.getHttpsClient(USERNAME, PASSWORD)
						.target(BESTELLUNGEN_URI).request()
						.accept(APPLICATION_JSON).put(json(bestellung));
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
		bestellung.setKunde(kunde2);

		response = getHttpsClient(USERNAME, PASSWORD).target(BESTELLUNGEN_URI)
				.request().accept(APPLICATION_JSON).put(json(bestellung));

		// Then
		assertThat(response.getStatus()).isEqualTo(HTTP_CONFLICT);
		response.close();

		LOGGER.finer("ENDE");
	}

}
