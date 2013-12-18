package de.shop.artikelverwaltung.service;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import com.google.common.base.Strings;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.Log;
import de.shop.util.persistence.ConcurrentDeletedException;

@Log
public class ArtikelService implements Serializable {
	private static final long serialVersionUID = -3076865030092242363L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass());

	@Inject
	private transient EntityManager em;

	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}

	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}

	/**
	 */
	public List<Artikel> findVerfuegbareArtikel() {
		final List<Artikel> result = em.createNamedQuery(
				Artikel.FIND_VERFUEGBARE_ARTIKEL, Artikel.class)
				.getResultList();
		return result;
	}

	/**
	 */
	public Artikel findArtikelById(Long id) {
		Artikel artikel = null;

		artikel = em.find(Artikel.class, id);
		if (artikel == null) {
			return null;
		}

		return artikel;
	}

	public List<Artikel> findArtikelByIds(List<Long> ids) {
		// Ist die ID null oder leer so gebe eine leere Liste zurück
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}

		/**
		 * SELECT a FROM Artikel a WHERE a.id = ? OR a.id = ? OR ...
		 */
		// Über das Objekt des Entitiymanagers wird die Methode
		// getCriteriaBuilder()
		// aufgerufen und dem CriteriaBuilder Objekt builder zugewiesen
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		// Über das Objekt builder von CriteriaBuilder wird die Methode
		// CreateQuery aufgerufen die,
		// die Klasse Artikel beinhaltet bildet das "Select" ab
		final CriteriaQuery<Artikel> criteriaQuery = builder
				.createQuery(Artikel.class);
		// Über das Objekt criteriaQuery wird die Methode from(Artikel.class)
		// aufgerufen die,
		// dem Attribut a die Wurzel zuweist auf was sich die Abfrage beziehen
		// soll
		final Root<Artikel> a = criteriaQuery.from(Artikel.class);
		// Keine AHnung
		final Path<Long> idPath = a.get("id");
		// final Path<String> idPath = a.get(Artikel_.id); // Metamodel-Klassen
		// funktionieren nicht mit Eclipse

		// Ist ein Attribut für True oder False
		Predicate pred = null;
		if (ids.size() == 1) {
			// Genau 1 id: kein OR notwendig
			pred = builder.equal(idPath, ids.get(0));
		} 
		
		else {
			// Mind. 2x id, durch OR verknuepft
			// Dem Array aus Predicate equal wird das erzeugt durch new
			// Predicate mit der Größe von dem Rückgabe Wert
			// der Ids
			final Predicate[] equals = new Predicate[ids.size()];
			int i = 0;

			// Schleife die Solange läuft wie viele IDS es gibt
			for (Long id : ids) {

				// equals[i++] die stelle wird um 1 erhöht = aus dem Objekt
				// builder wird die MEthode equal aufgerufen
				// die vergleicht ob die id mit der Id im IDPath übereinstimmt
				// und gibt true oder fals ein
				equals[i++] = builder.equal(idPath, id);
			}

			// pred wird die ergebnise zugewiesen
			pred = builder.or(equals);
		}
		// Im Objek criteriaQuery wird die Methode where(...) aufgerufen die als
		// Parameter pred erhält
		criteriaQuery.where(pred);

		// Über das Objekt em wird die Methode createQuery aufgerufen die als
		// Parameter die criteriaQuery mitbekommt
		// und diese dann entgültig ausführt
		final TypedQuery<Artikel> query = em.createQuery(criteriaQuery);
		// dem Objekt artikel wir das Ergebnis aus query über die Methode
		// getResultList() zugewiesen
		final List<Artikel> artikel = query.getResultList();
		// Wird zurück gegeben
		return artikel;
	}

	public List<Artikel> findArtikelByBezeichnung(String bezeichnung) {
		if (Strings.isNullOrEmpty(bezeichnung)) {
			final List<Artikel> artikel = findVerfuegbareArtikel();
			return artikel;
		}

		final List<Artikel> artikel = em
				.createNamedQuery(Artikel.FIND_ARTIKEL_BY_BEZ, Artikel.class)
				.setParameter(Artikel.PARAM_BEZEICHNUNG,
						"%" + bezeichnung + "%").getResultList();
		return artikel;
	}

	public List<Artikel> findArtikelByMaxPreis(double preis) {
		final List<Artikel> artikel = em
				.createNamedQuery(Artikel.FIND_ARTIKEL_MAX_PREIS, Artikel.class)
				.setParameter(Artikel.PARAM_PREIS, preis).getResultList();
		return artikel;
	}

	public Artikel createArtikel(Artikel artikel) {

		if (artikel == null) {
			return null;
		}
		em.persist(artikel);

		return artikel;
	}

	public Artikel updateArtikel(Artikel artikel) {
		if (artikel == null) {
			return null;
		}
		// Artikel vom EntityManager trennen, weil anschliessend z.B. nach Id und
		em.detach(artikel);

		final Artikel tmp = findArtikelById(artikel.getId());
		
		if (tmp == null) {
			throw new ConcurrentDeletedException(artikel.getId());
		}
		em.detach(tmp);
		
		artikel = em.merge(artikel); // OptimisticLockException
		
		return artikel;
	}

	public List<Artikel> ladenhueter(int anzahl) {
		return em.createNamedQuery(Artikel.FIND_LADENHUETER, Artikel.class)
				 .setMaxResults(anzahl)
				 .getResultList();
	}
}
