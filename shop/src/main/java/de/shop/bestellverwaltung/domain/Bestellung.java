package de.shop.bestellverwaltung.domain;

import static de.shop.util.Constants.MIN_ID;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;

import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.util.IdGroup;


public class Bestellung implements Serializable {
	private static final long serialVersionUID = 1618359234119003714L;
	public static final int min = 2;
	public static final int max = 32;
	
	@Min(value = MIN_ID, message = "{bestellverwaltung.bestellung.id.min}", groups = IdGroup.class)
	private Long id;
	
	private boolean ausgeliefert;
	
	@NotNull(message = "{bestellverwaltung.bestellung.kunde.notNull}")
	@JsonIgnore
	private AbstractKunde kunde;
	
	@JsonIgnore
	private URI bestellPositionURI;
	
	//@JsonIgnore
	@NotNull(message = "{bestellverwaltung.bestellung.bestellPositionen.notNull}")
	@Size(min = 1)
	@Valid
	private List<BestellPosition> bestellPositionen;
	
	//TODO KundenUri fehlermeldung bei nicht setzung
	private URI kundeUri;
	
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (id == null) {
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Bestellung [id=" + id + ", ausgeliefert=" + ausgeliefert + ", kundeUri=" + kundeUri + "]";
	}
	public URI getBestellPositionURI() {
		return bestellPositionURI;
	}
	public void setBestellPositionURI(URI bestellPositionURI) {
		this.bestellPositionURI = bestellPositionURI;
	}
	public List<BestellPosition> getBestellPositionen() {
		return bestellPositionen;
	}
	public void setBestellPositionen(List<BestellPosition> bestellPositionen) {
		this.bestellPositionen = bestellPositionen;
	}
}
