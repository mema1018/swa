package de.shop.bestellverwaltung.domain;

import static de.shop.util.Constants.MIN_ID;

import java.io.Serializable;
import java.net.URI;

import javax.validation.constraints.Min;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.validation.constraints.NotNull;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.IdGroup;



public class BestellPosition implements Serializable  {
	private static final long serialVersionUID = 2815434747953562015L;

	@Min(value = MIN_ID, message = "{bestellverwaltung.bestellPosition.id.min}", groups = IdGroup.class)
	private Long id;
	
	@Min(value = MIN_ID,message = "{bestellverwaltung.bestellPosition.anzahl.Min}", groups = IdGroup.class)
	private long anzahl;
	
	@JsonIgnore
	@NotNull(message = "{bestellverwaltung.bestellPosition.artikel.notNull}")
	private Artikel artikel;
	
	// TODO bestellungURI
    //@NotNull(message = "{bestellverwaltung.bestellPosition.bestellungURI.notNull}")
	private URI bestellungURI;
	
	// TODO  artikelURI
   @NotNull(message = "{bestellverwaltung.bestellPosition.artikelURI.notNull}")
	private URI artikelUri;
	
	@JsonIgnore
    //@NotNull(message = "{bestellverwaltung.bestellPosition.bestellung.notNull}")
	private Bestellung bestellung;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public long getAnzahl() {
		return anzahl;
	}
	public void setAnzahl(long anzahl) {
		this.anzahl = anzahl;
	}
	public Artikel getArtikel() {
		return artikel;
	}
	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (anzahl ^ (anzahl >>> 32));
		result = prime * result + ((artikel == null) ? 0 : artikel.hashCode());
		result = prime * result
				+ ((bestellung == null) ? 0 : bestellung.hashCode());
		result = prime * result
				+ ((bestellungURI == null) ? 0 : bestellungURI.hashCode());
		result = prime * result
				+ ((id == null) ? 0 : id.hashCode());
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
		final BestellPosition other = (BestellPosition) obj;
		if (anzahl != other.anzahl)
			return false;
		if (artikel == null) {
			if (other.artikel != null)
				return false;
		} 
		else if (!artikel.equals(other.artikel))
			return false;
		if (bestellung == null) {
			if (other.bestellung != null)
				return false;
		} 
		else if (!bestellung.equals(other.bestellung))
			return false;
		if (bestellungURI == null) {
			if (other.bestellungURI != null)
				return false;
		} 
		else if (!bestellungURI.equals(other.bestellungURI))
			return false;
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
		return "BestellPosition [positionId=" + id + ", anzahl="
				+ anzahl + ", artikel=" + artikel + ", bestellungURI="
				+ bestellungURI + ", bestellung=" + bestellung + "]";
	}
	public URI getBestellungURI() {
		return bestellungURI;
	}
	public void setBestellungURI(URI bestellungURI) {
		this.bestellungURI = bestellungURI;
	}
	public Bestellung getBestellung() {
		return bestellung;
	}
	public void setBestellung(Bestellung bestellung) {
		this.bestellung = bestellung;
	}
	public URI getArtikelUri() {
		return artikelUri;
	}
	public void setArtikelUri(URI artikelUri) {
		this.artikelUri = artikelUri;
	}
}