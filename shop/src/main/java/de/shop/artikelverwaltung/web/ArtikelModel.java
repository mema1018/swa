package de.shop.artikelverwaltung.web;

import static de.shop.util.Constants.JSF_INDEX;
import static de.shop.util.Constants.JSF_REDIRECT_SUFFIX;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.TransactionAttribute;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Model;
import javax.faces.context.Flash;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;
import org.richfaces.push.cdi.Push;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.auth.web.AuthModel;
import de.shop.util.Log;
import de.shop.util.persistence.ConcurrentDeletedException;
import de.shop.util.web.Client;
import de.shop.util.web.Messages;


/**
 * Dialogsteuerung fuer ArtikelService
 * @author <a href="mailto:Juergen.Zimmermann@HS-Karlsruhe.de">J&uuml;rgen Zimmermann</a>
 */
@Model
public class ArtikelModel implements Serializable {
	private static final long serialVersionUID = 1564024850446471639L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String JSF_LIST_ARTIKEL = "/artikelverwaltung/listArtikel";
	private static final String MSG_KEY_CONCURRENT_UPDATE = "persistence.concurrentUpdate";
	private static final String MSG_KEY_CONCURRENT_DELETE = "persistence.concurrentDelete";
	private static final String FLASH_ARTIKEL = "artikel";
//	private static final int ANZAHL_LADENHUETER = 5;
	
	private static final String JSF_SELECT_ARTIKEL = "/artikelverwaltung/selectArtikel";
	private static final String SESSION_VERFUEGBARE_ARTIKEL = "verfuegbareArtikel";
	private boolean geaendertArtikel;

	private String bezeichnung;
	private Artikel artikel;
	
//	private List<Artikel> ladenhueter;

	@Inject
	private ArtikelService as;
	
	@Inject
	@Push(topic = "updateArtikel")
	private transient Event<String> updateArtikelEvent;
	
	
	@Inject
	private Flash flash;
	
	@Inject
	private AuthModel auth;
	
	@Inject
	@Client
	private Locale locale;
	
	@Inject
	private Messages messages;
	
	@Inject
	private transient HttpSession session;

	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}

	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	@Override
	public String toString() {
		return "ArtikelModel [bezeichnung=" + bezeichnung + "]";
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}


//	public List<Artikel> getLadenhueter() {
//		return ladenhueter;
//	}

	@Log
	public String findArtikelByBezeichnung() {
		final List<Artikel> artikel = as.findArtikelByBezeichnung(bezeichnung);
		flash.put(FLASH_ARTIKEL, artikel);

		return JSF_LIST_ARTIKEL;
	}
	

//	@Log
//	public void loadLadenhueter() {
//		ladenhueter = as.ladenhueter(ANZAHL_LADENHUETER);
//	}
	
	@Log
	public String selectArtikel() {
		if (session.getAttribute(SESSION_VERFUEGBARE_ARTIKEL) == null) {
			final List<Artikel> alleArtikel = as.findVerfuegbareArtikel();
			session.setAttribute(SESSION_VERFUEGBARE_ARTIKEL, alleArtikel);
		}
		
		return JSF_SELECT_ARTIKEL;
	}
	public void geaendert(ValueChangeEvent e) {
		if (geaendertArtikel) {
			return;
		}
		
		if (e.getOldValue() == null) {
			if (e.getNewValue() != null) {
				geaendertArtikel = true;
			}
			return;
		}

		if (!e.getOldValue().equals(e.getNewValue())) {
			geaendertArtikel = true;				
		}
	}
	@TransactionAttribute
	@Log
	public String update() {
		auth.preserveLogin();
		
		if (!geaendertArtikel || artikel == null) {
			return JSF_INDEX;
		}
		
		final Artikel orginalArtikel = as.findArtikelById(artikel.getId());
		
		LOGGER.tracef("Artikel vorher: %s", orginalArtikel);

		// Daten des vorhandenen Artikels ueberschreiben
		//orginalArtikel.setValues(artikel);
		LOGGER.tracef("Artikel nachher: %s", orginalArtikel);

		// Update durchfuehren
		artikel = as.updateArtikel(orginalArtikel);
		

		// Push-Event fuer Webbrowser
		updateArtikelEvent.fire(String.valueOf(artikel.getId()));
		return JSF_INDEX + JSF_REDIRECT_SUFFIX;
	}
	
	private String updateErrorMsg(RuntimeException e, Class<? extends Artikel> artikelClass) {
		final Class<? extends RuntimeException> exceptionClass = e.getClass();
		 if (OptimisticLockException.class.equals(exceptionClass)) {
			messages.error(MSG_KEY_CONCURRENT_UPDATE, locale, null);

		}
		else if (ConcurrentDeletedException.class.equals(exceptionClass)) {
			messages.error(MSG_KEY_CONCURRENT_DELETE, locale, null);
		}
		else {
			throw new RuntimeException(e);
		}
		return null;
	}
}
