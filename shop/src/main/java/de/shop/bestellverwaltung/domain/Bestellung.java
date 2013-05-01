package de.shop.bestellverwaltung.domain;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import de.shop.kundenverwaltung.domain.AbstractKunde;

public  class Bestellung implements Serializable { // Daten dauerhaft speichern
	private static final long serialVersionUID = 1618359234119003714L;

	private Long id;
	private boolean ausgeliefert;
	@JsonIgnore
	private AbstractKunde kunde;
	private URI kundeUri;
	private URI bestellPositionURI;
	@JsonIgnore
	private List<BestellPosition> bestellPositionen;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isAusgeliefert() {
		return ausgeliefert;
	}

	public void setAusgeliefert(boolean ausgeliefert) {
		this.ausgeliefert = ausgeliefert;
	}

	public AbstractKunde getKunde() {
		return kunde;
	}

	public void setKunde(AbstractKunde kunde) {
		this.kunde = kunde;
	}

	public URI getKundeUri() {
		return kundeUri;
	}

	public void setKundeUri(URI kundeUri) {
		this.kundeUri = kundeUri;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (ausgeliefert ? 1231 : 1237);
		result = prime
				* result
				+ ((bestellPositionen == null) ? 0 : bestellPositionen
						.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((kunde == null) ? 0 : kunde.hashCode());
		result = prime * result
				+ ((kundeUri == null) ? 0 : kundeUri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Bestellung other = (Bestellung) obj;
		if (ausgeliefert != other.ausgeliefert)
			return false;
		if (bestellPositionen == null) {
			if (other.bestellPositionen != null)
				return false;
		} 
		else if (!bestellPositionen.equals(other.bestellPositionen))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} 
		else if (!id.equals(other.id))
			return false;
		if (kunde == null) {
			if (other.kunde != null)
				return false;
		} 
		else if (!kunde.equals(other.kunde))
			return false;
		if (kundeUri == null) {
			if (other.kundeUri != null)
				return false;
		} 
		else if (!kundeUri.equals(other.kundeUri))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Bestellung [id=" + id + ", ausgeliefert=" + ausgeliefert
				+ ", kunde=" + kunde + ", kundeUri=" + kundeUri
				+ ", bestellPositionURI=" + bestellPositionURI
				+ ", bestellPositionen=" + bestellPositionen + "]";
	}

	public URI getBestellPositionURI() {
		return bestellPositionURI;
	}

	public void setBestellPositionURI(URI bestellPosURI) {
		this.bestellPositionURI = bestellPosURI;
	}

	public List<BestellPosition> getBestellPositionen() {
		return bestellPositionen;
	}

	public void setBestellPositionen(List<BestellPosition> bestellPositionen) {
		this.bestellPositionen = bestellPositionen;
	}
}
