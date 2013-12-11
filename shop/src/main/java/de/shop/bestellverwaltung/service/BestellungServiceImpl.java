package de.shop.bestellverwaltung.service;

import static de.shop.util.Constants.KEINE_ID;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Lieferung;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.service.EmailExistsException;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.util.Log;
import de.shop.util.persistence.ConcurrentDeletedException;

@Log
public class BestellungServiceImpl implements Serializable, BestellungService {
	private static final long serialVersionUID = -9145947650157430928L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass());

	@Inject
	private transient EntityManager em;

	@Inject
	private KundeService ks;

	@Inject
	@NeueBestellung
	private transient Event<Bestellung> event;

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
	@Override
	public Bestellung findBestellungById(Long id) {
		final Bestellung bestellung = em.find(Bestellung.class, id);
		return bestellung;
	}

	/**
	 */
	@Override
	public Bestellung findBestellungByIdMitLieferungen(Long id) {
		try {
			final Bestellung bestellung = em
					.createNamedQuery(
							Bestellung.FIND_BESTELLUNG_BY_ID_FETCH_LIEFERUNGEN,
							Bestellung.class)
					.setParameter(Bestellung.PARAM_ID, id).getSingleResult();
			return bestellung;
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 */
	@Override
	public AbstractKunde findKundeById(Long id) {
		try {
			final AbstractKunde kunde = em
					.createNamedQuery(Bestellung.FIND_KUNDE_BY_ID,
							AbstractKunde.class)
					.setParameter(Bestellung.PARAM_ID, id).getSingleResult();
			return kunde;
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 */
	@Override
	public List<Bestellung> findBestellungenByKunde(AbstractKunde kunde) {
		if (kunde == null) {
			return Collections.emptyList();
		}
		final List<Bestellung> bestellungen = em
				.createNamedQuery(Bestellung.FIND_BESTELLUNGEN_BY_KUNDE,
						Bestellung.class)
				.setParameter(Bestellung.PARAM_KUNDE, kunde).getResultList();
		return bestellungen;
	}

	/**
	 * Zuordnung einer neuen, transienten Bestellung zu einem existierenden,
	 * persistenten Kunden. Der Kunde ist fuer den EntityManager bekannt, die
	 * Bestellung dagegen nicht. Das Zusammenbauen wird sowohl fuer einen Web
	 * Service aus auch fuer eine Webanwendung benoetigt.
	 */
	@Override
	public Bestellung createBestellung(Bestellung bestellung, String username) {
		if (bestellung == null) {
			return null;
		}

		// Den persistenten Kunden mit der transienten Bestellung verknuepfen
		final AbstractKunde kunde = ks.findKundeByUserName(username);
		return createBestellung(bestellung, kunde);
	}

	/**
	 * Zuordnung einer neuen, transienten Bestellung zu einem existierenden,
	 * persistenten Kunden. Der Kunde ist fuer den EntityManager bekannt, die
	 * Bestellung dagegen nicht. Das Zusammenbauen wird sowohl fuer einen Web
	 * Service aus auch fuer eine Webanwendung benoetigt.
	 */
	@Override
	public Bestellung createBestellung(Bestellung bestellung,
			AbstractKunde kunde) {
		if (bestellung == null || kunde == null) {
			return null;
		}

		// Den persistenten Kunden mit der transienten Bestellung verknuepfen
		if (!em.contains(kunde)) {
			kunde = ks.findKundeById(kunde.getId(),
					KundeService.FetchType.MIT_BESTELLUNGEN);
		}
		kunde.addBestellung(bestellung);
		bestellung.setKunde(kunde);

		// Vor dem Abspeichern IDs zuruecksetzen:
		// IDs koennten einen Wert != null haben, wenn sie durch einen Web
		// Service uebertragen wurden
		bestellung.setId(KEINE_ID);
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			bp.setId(KEINE_ID);
			LOGGER.tracef("Bestellposition: %s", bp);
		}

		em.persist(bestellung);
		event.fire(bestellung);

		return bestellung;
	}

	/**
	 */
	@Override
	public List<Artikel> ladenhueter(int anzahl) {
		final List<Artikel> artikel = em
				.createNamedQuery(Bestellposition.FIND_LADENHUETER,
						Artikel.class).setMaxResults(anzahl).getResultList();
		return artikel;
	}

	/**
	 */
	@Override
	public List<Lieferung> findLieferungen(String nr) {
		final List<Lieferung> lieferungen = em
				.createNamedQuery(
						Lieferung.FIND_LIEFERUNGEN_BY_LIEFERNR_FETCH_BESTELLUNGEN,
						Lieferung.class)
				.setParameter(Lieferung.PARAM_LIEFERNR, nr).getResultList();
		return lieferungen;
	}

	/**
	 */
	@Override
	public Lieferung createLieferung(Lieferung lieferung,
			List<Bestellung> bestellungen) {
		if (lieferung == null || bestellungen == null || bestellungen.isEmpty()) {
			return null;
		}

		// Beziehungen zu existierenden Bestellungen aktualisieren

		// Ids ermitteln
		final List<Long> ids = new ArrayList<>();
		for (Bestellung b : bestellungen) {
			ids.add(b.getId());
		}

		bestellungen = findBestellungenByIds(ids);
		lieferung.setBestellungenAsList(bestellungen);
		for (Bestellung bestellung : bestellungen) {
			bestellung.addLieferung(lieferung);
		}

		lieferung.setId(KEINE_ID);
		em.persist(lieferung);
		return lieferung;
	}

	private List<Bestellung> findBestellungenByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return null;
		}

		// SELECT b
		// FROM Bestellung b LEFT JOIN FETCH b.lieferungen
		// WHERE b.id = <id> OR ...

		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Bestellung> criteriaQuery = builder
				.createQuery(Bestellung.class);
		final Root<Bestellung> b = criteriaQuery.from(Bestellung.class);
		b.fetch("lieferungen", JoinType.LEFT);

		// Die Vergleichen mit "=" als Liste aufbauen
		final Path<Long> idPath = b.get("id");
		final List<Predicate> predList = new ArrayList<>();
		for (Long id : ids) {
			final Predicate equal = builder.equal(idPath, id);
			predList.add(equal);
		}
		// Die Vergleiche mit "=" durch "or" verknuepfen
		final Predicate[] predArray = new Predicate[predList.size()];
		final Predicate pred = builder.or(predList.toArray(predArray));
		criteriaQuery.where(pred).distinct(true);

		final TypedQuery<Bestellung> query = em.createQuery(criteriaQuery);
		final List<Bestellung> bestellungen = query.getResultList();
		return bestellungen;
	}

	@Override
	public Bestellung updateBestellung(Bestellung bestellung) {
		if (bestellung == null) {
			return null;
		}

		// kunde vom EntityManager trennen, weil anschliessend z.B. nach Id und
		// Email gesucht wird
		em.detach(bestellung);

		// Wurde das Objekt konkurrierend geloescht?
		final Bestellung tmp = findBestellungById(bestellung.getId());
		if (tmp == null) {
			throw new ConcurrentDeletedException(bestellung.getId());
		}
		em.detach(tmp);

		// Gibt es ein anderes Objekt mit gleicher Email-Adresse?

		bestellung = em.merge(bestellung); // OptimisticLockException

		return bestellung;

	}

	public AbstractKunde createKunde(AbstractKunde kunde) {
		if (kunde == null) {
			return kunde;
		}

		// Pruefung, ob die Email-Adresse schon existiert
		try {
			em.createNamedQuery(AbstractKunde.FIND_KUNDE_BY_EMAIL,
					AbstractKunde.class)
					.setParameter(AbstractKunde.PARAM_KUNDE_EMAIL,
							kunde.getEmail()).getSingleResult();
			throw new EmailExistsException(kunde.getEmail());
		} catch (NoResultException e) {
			// Noch kein Kunde mit dieser Email-Adresse
			LOGGER.trace("Email-Adresse existiert noch nicht");
		}

		em.persist(kunde);
		return kunde;
	}

}
