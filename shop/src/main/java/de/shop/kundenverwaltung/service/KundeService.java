package de.shop.kundenverwaltung.service;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.jboss.logging.Logger;

import de.shop.auth.service.AuthService;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellposition_;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Bestellung_;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.domain.AbstractKunde_;
import de.shop.kundenverwaltung.domain.Wartungsvertrag;
import de.shop.util.Log;
import de.shop.util.NoMimeTypeException;
import de.shop.util.persistence.ConcurrentDeletedException;
import de.shop.util.persistence.File;
import de.shop.util.persistence.FileHelper;
import de.shop.util.persistence.MimeType;

/**
 * Anwendungslogik fuer die KundeService
 */
@Log
public class KundeService implements Serializable {
	private static final long serialVersionUID = -5520738420154763865L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass());

	public enum FetchType {
		NUR_KUNDE, MIT_BESTELLUNGEN, MIT_WARTUNGSVERTRAEGEN
	}
	public enum OrderByType {
		UNORDERED
	}
	public enum OrderType {
		KEINE, ID, 
	}

	@Inject
	private transient EntityManager em;

	@Inject
	private transient ManagedExecutorService managedExecutorService;

	@Inject
	private AuthService authService;

	@Inject
	private FileHelper fileHelper;

	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}

	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}

	public List<AbstractKunde> findAllKunden(FetchType fetch, OrderByType order) {
		List<AbstractKunde> kunden;
		switch (fetch) {
		case NUR_KUNDE:
			kunden = OrderType.ID.equals(order) ? em.createNamedQuery(
					AbstractKunde.FIND_KUNDEN_ORDER_BY_ID, AbstractKunde.class)
					.getResultList() : em.createNamedQuery(
					AbstractKunde.FIND_KUNDEN, AbstractKunde.class)
					.getResultList();
			break;

		case MIT_BESTELLUNGEN:
			kunden = em.createNamedQuery(
					AbstractKunde.FIND_KUNDEN_FETCH_BESTELLUNGEN,
					AbstractKunde.class).getResultList();
			break;

		default:
			kunden = OrderType.ID.equals(order) ? em.createNamedQuery(
					AbstractKunde.FIND_KUNDEN_ORDER_BY_ID, AbstractKunde.class)
					.getResultList() : em.createNamedQuery(
					AbstractKunde.FIND_KUNDEN, AbstractKunde.class)
					.getResultList();
			break;
		}

		return kunden;
	}

	public List<AbstractKunde> findKundenByNachname(String nachname,
			FetchType fetch) {

		List<AbstractKunde> kunden;
		switch (fetch) {
		case NUR_KUNDE:
			kunden = em
					.createNamedQuery(AbstractKunde.FIND_KUNDEN_BY_NACHNAME,
							AbstractKunde.class)
					.setParameter(AbstractKunde.PARAM_KUNDE_NACHNAME, nachname)
					.getResultList();
			break;

		case MIT_BESTELLUNGEN:
			kunden = em
					.createNamedQuery(
							AbstractKunde.FIND_KUNDEN_BY_NACHNAME_FETCH_BESTELLUNGEN,
							AbstractKunde.class)
					.setParameter(AbstractKunde.PARAM_KUNDE_NACHNAME, nachname)
					.getResultList();
			break;

		default:
			kunden = em
					.createNamedQuery(AbstractKunde.FIND_KUNDEN_BY_NACHNAME,
							AbstractKunde.class)
					.setParameter(AbstractKunde.PARAM_KUNDE_NACHNAME, nachname)
					.getResultList();
			break;
		}

		return kunden;
	}

	public AbstractKunde findKundeById(Long id, FetchType fetch) {

		AbstractKunde kunde = null;
		try {
			switch (fetch) {
			case NUR_KUNDE:
				kunde = em.find(AbstractKunde.class, id);
				break;

			case MIT_BESTELLUNGEN:
				kunde = em
						.createNamedQuery(
								AbstractKunde.FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN,
								AbstractKunde.class)
						.setParameter(AbstractKunde.PARAM_KUNDE_ID, id)
						.getSingleResult();
				break;

			case MIT_WARTUNGSVERTRAEGEN:
				kunde = em
						.createNamedQuery(
								AbstractKunde.FIND_KUNDE_BY_ID_FETCH_WARTUNGSVERTRAEGE,
								AbstractKunde.class)
						.setParameter(AbstractKunde.PARAM_KUNDE_ID, id)
						.getSingleResult();
				break;

			default:
				kunde = em.find(AbstractKunde.class, id);
				break;
			}
		} catch (NoResultException e) {
			return null;
		}

		return kunde;
	}

	public AbstractKunde findKundeByUserName(String userName) {
		try {
			return em
					.createNamedQuery(AbstractKunde.FIND_KUNDE_BY_USERNAME,
							AbstractKunde.class)
					.setParameter(AbstractKunde.PARAM_KUNDE_USERNAME, userName)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Long> findIdsByPrefix(String idPrefix) {
		final List<Long> ids = em
				.createNamedQuery(AbstractKunde.FIND_IDS_BY_PREFIX, Long.class)
				.setParameter(AbstractKunde.PARAM_KUNDE_ID_PREFIX,
						idPrefix + '%').getResultList();
		return ids;
	}
	
	public List<AbstractKunde> findKundenByIdPrefix(String id) {
		final List<AbstractKunde> kunde  = em.createNamedQuery(
				AbstractKunde.FIND_IDS_BY_PREFIX, AbstractKunde.class)
				.setParameter(AbstractKunde.PARAM_KUNDE_ID_PREFIX, id + '%')
				.getResultList();
		
		return kunde;
	}
	
	public List<String> findNachnamenByPrefix(String nachnamePrefix) {
		final List<String> nachnamen  = em.createNamedQuery(
				AbstractKunde.FIND_NACHNAMEN_BY_PREFIX, String.class)
				.setParameter(AbstractKunde.PARAM_KUNDE_NACHNAME_PREFIX, nachnamePrefix + '%')
				.getResultList();
		
		return nachnamen;
	}

	public AbstractKunde findKundeByEmail(String email) {

		try {
			final AbstractKunde kunde = em
					.createNamedQuery(AbstractKunde.FIND_KUNDE_BY_EMAIL,
							AbstractKunde.class)
					.setParameter(AbstractKunde.PARAM_KUNDE_EMAIL, email)
					.getSingleResult();
			return kunde;
		} catch (NoResultException e) {
			return null;
		}
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

	public <T extends AbstractKunde> T updateKunde(T kunde,
			boolean geaendertPassword) {
		if (kunde == null) {
			return null;
		}

		// kunde vom EntityManager trennen, weil anschliessend z.B. nach Id und
		// Email gesucht wird
		em.detach(kunde);

		// Wurde das Objekt konkurrierend geloescht?
		AbstractKunde tmp = findKundeById(kunde.getId(), FetchType.NUR_KUNDE);
		if (tmp == null) {
			throw new ConcurrentDeletedException(kunde.getId());
		}
		em.detach(tmp);

		// Gibt es ein anderes Objekt mit gleicher Email-Adresse?
		tmp = findKundeByEmail(kunde.getEmail());
		if (tmp != null) {
			em.detach(tmp);
			if (tmp.getId().longValue() != kunde.getId().longValue()) {
				// anderes Objekt mit gleichem Attributwert fuer email
				throw new EmailExistsException(kunde.getEmail());
			}
		}

		// Password verschluesseln
		if (geaendertPassword) {
			passwordVerschluesseln(kunde);
		}

		kunde = em.merge(kunde); // OptimisticLockException
		kunde.setPasswordWdh(kunde.getPassword());

		return kunde;
	}

	public void deleteKunde(AbstractKunde kunde) {
		if (kunde == null) {
			return;
		}

		// Bestellungen laden, damit sie anschl. ueberprueft werden koennen
		try {
			kunde = findKundeById(kunde.getId(), FetchType.MIT_BESTELLUNGEN);
		} catch (InvalidKundeIdException e) {
			return;
		}

		if (kunde == null) {
			return;
		}

		// Gibt es Bestellungen?
		if (!kunde.getBestellungen().isEmpty()) {
			throw new KundeDeleteBestellungException(kunde);
		}

		em.remove(kunde);
	}

	public List<AbstractKunde> findKundenByPLZ(String plz) {
		final List<AbstractKunde> kunden = em
				.createNamedQuery(AbstractKunde.FIND_KUNDEN_BY_PLZ,
						AbstractKunde.class)
				.setParameter(AbstractKunde.PARAM_KUNDE_ADRESSE_PLZ, plz)
				.getResultList();
		return kunden;
	}
	
	public List<AbstractKunde> findKundenBySeit(Date seit) {
		final List<AbstractKunde> kunden = em
				.createNamedQuery(AbstractKunde.FIND_KUNDEN_BY_DATE,
						AbstractKunde.class)
				.setParameter(AbstractKunde.PARAM_KUNDE_SEIT, seit)
				.getResultList();
		return kunden;
	}

	public List<AbstractKunde> findPrivatkundenFirmenkunden() {
		final List<AbstractKunde> kunden = em.createNamedQuery(
				AbstractKunde.FIND_PRIVATKUNDEN_FIRMENKUNDEN,
				AbstractKunde.class).getResultList();
		return kunden;
	}

	public List<AbstractKunde> findKundenByNachnameCriteria(String nachname) {
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<AbstractKunde> criteriaQuery = builder
				.createQuery(AbstractKunde.class);
		final Root<AbstractKunde> k = criteriaQuery.from(AbstractKunde.class);

		final Path<String> nachnamePath = k.get(AbstractKunde_.nachname);
		// final Path<String> nachnamePath = k.get("nachname");

		final Predicate pred = builder.equal(nachnamePath, nachname);
		criteriaQuery.where(pred);

		// Ausgabe des komponierten Query-Strings. Voraussetzung: das Modul
		// "org.hibernate" ist aktiviert
		// if (LOGGER.isLoggable(FINEST)) {
		// query.unwrap(org.hibernate.Query.class).getQueryString();
		// }

		final List<AbstractKunde> kunden = em.createQuery(criteriaQuery)
				.getResultList();
		return kunden;
	}

	public List<AbstractKunde> findKundenMitMinBestMenge(short minMenge) {
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<AbstractKunde> criteriaQuery = builder
				.createQuery(AbstractKunde.class);
		final Root<AbstractKunde> k = criteriaQuery.from(AbstractKunde.class);

		final Join<AbstractKunde, Bestellung> b = k
				.join(AbstractKunde_.bestellungen);
		final Join<Bestellung, Bestellposition> bp = b
				.join(Bestellung_.bestellpositionen);
		criteriaQuery.where(
				builder.gt(bp.<Short>get(Bestellposition_.anzahl), minMenge))
				.distinct(true);

		final List<AbstractKunde> kunden = em.createQuery(criteriaQuery)
				.getResultList();
		return kunden;
	}

	/**
	 */
	public List<Wartungsvertrag> findWartungsvertraege(Long kundeId) {
		final List<Wartungsvertrag> wartungsvertraege = em
				.createNamedQuery(
						Wartungsvertrag.FIND_WARTUNGSVERTRAEGE_BY_KUNDE_ID,
						Wartungsvertrag.class)
				.setParameter(Wartungsvertrag.PARAM_KUNDE_ID, kundeId)
				.getResultList();
		return wartungsvertraege;
	}

	/**
	 */
	public Wartungsvertrag createWartungsvertrag(
			Wartungsvertrag wartungsvertrag, AbstractKunde kunde) {
		if (wartungsvertrag == null || kunde == null) {
			return null;
		}

		try {
			kunde = findKundeById(kunde.getId(), FetchType.NUR_KUNDE);
		} catch (InvalidKundeIdException e) {
			return null;
		}

		wartungsvertrag.setKunde(kunde);
		kunde.addWartungsvertrag(wartungsvertrag);

		em.persist(wartungsvertrag);
		return wartungsvertrag;
	}

	public AbstractKunde setFile(Long kundeId, byte[] bytes) {
		final AbstractKunde kunde = findKundeById(kundeId, FetchType.NUR_KUNDE);
		if (kunde == null) {
			return null;
		}
		final MimeType mimeType = fileHelper.getMimeType(bytes);
		setFile(kunde, bytes, mimeType);
		return kunde;
	}

	public AbstractKunde setFile(AbstractKunde kunde, byte[] bytes,
			String mimeTypeStr) {
		final MimeType mimeType = MimeType.build(mimeTypeStr);
		setFile(kunde, bytes, mimeType);
		return kunde;
	}
	
	private void setFile(AbstractKunde kunde, byte[] bytes, MimeType mimeType) {
		if (mimeType == null) {
			throw new NoMimeTypeException();
		}

		final String filename = fileHelper.getFilename(kunde.getClass(),
				kunde.getId(), mimeType);

		// Gibt es noch kein (Multimedia-) File
		File file = kunde.getFile();
		if (kunde.getFile() == null) {
			file = new File(bytes, filename, mimeType);
			LOGGER.tracef("Neue Datei %s", file);
			kunde.setFile(file);
			em.persist(file);
		} 
		
		else {
			file.set(bytes, filename, mimeType);
			LOGGER.tracef("Ueberschreiben der Datei %s", file);
			em.merge(file);
		}

		// Hochgeladenes Bild/Video/Audio in einem parallelen Thread als Datei
		// fuer die Web-Anwendung abspeichern
		final File newFile = kunde.getFile();
		final Runnable storeFile = new Runnable() {
			@Override
			public void run() {
				fileHelper.store(newFile);
			}
		};
		managedExecutorService.execute(storeFile);
	}

	private void passwordVerschluesseln(AbstractKunde kunde) {
		LOGGER.debugf("passwordVerschluesseln BEGINN: %s", kunde);

		final String unverschluesselt = kunde.getPassword();
		final String verschluesselt = authService
				.verschluesseln(unverschluesselt);
		kunde.setPassword(verschluesselt);
		kunde.setPasswordWdh(verschluesselt);

		LOGGER.debugf("passwordVerschluesseln ENDE: %s", verschluesselt);
	}

}
