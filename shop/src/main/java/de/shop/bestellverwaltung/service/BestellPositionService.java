package de.shop.bestellverwaltung.service;

import static de.shop.util.Constants.KEINE_ID;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import de.shop.bestellverwaltung.domain.Bestellposition;

public class BestellPositionService implements Serializable {

	private static final long serialVersionUID = -2445565397989052361L;

	@Inject
	private transient EntityManager em;

	public Bestellposition findBestellpositionById(Long id) {
		final Bestellposition bestellposition = em.find(Bestellposition.class,
				id);
		return bestellposition;

	}

	public Bestellposition createBestellposition(Bestellposition bestellposition) {
		if (bestellposition == null) {
			return bestellposition;
		}
		bestellposition.setId(KEINE_ID);
		em.persist(bestellposition);
		return bestellposition;
	}

}
