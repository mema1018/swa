package de.shop.artikelverwaltung.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class Artikel implements Serializable {
	/**
	 * Generierte Seriennummer
	 */
	private static final long serialVersionUID = 6056633571995891182L;
	private Long id;
	private String bezeichnung;
	private BigDecimal verkaufspreis;
	private BigDecimal einkaufspreis;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}

	public BigDecimal getVerkaufspreis() {
		return verkaufspreis;
	}

	public void setVerkaufspreis(BigDecimal verkaufspreis) {
		this.verkaufspreis = verkaufspreis;
	}

	public BigDecimal getEinkaufspreis() {
		return einkaufspreis;
	}

	public void setEinkaufspreis(BigDecimal einkaufspreis) {
		this.einkaufspreis = einkaufspreis;
	}

	@Override
	public String toString() {
		return "Artikel [id=" + id + ", bezeichnung=" + bezeichnung
				+ ", verkaufspreis=" + verkaufspreis + ", einkaufspreis="
				+ einkaufspreis + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bezeichnung == null) ? 0 : bezeichnung.hashCode());
		result = prime * result
				+ ((einkaufspreis == null) ? 0 : einkaufspreis.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((verkaufspreis == null) ? 0 : verkaufspreis.hashCode());
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
		final Artikel other = (Artikel) obj;
		if (bezeichnung == null) {
			if (other.bezeichnung != null)
				return false;
		} 
		else if (!bezeichnung.equals(other.bezeichnung))
			return false;
		if (einkaufspreis == null) {
			if (other.einkaufspreis != null)
				return false;
		} 
		else if (!einkaufspreis.equals(other.einkaufspreis))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} 
		else if (!id.equals(other.id))
			return false;
		if (verkaufspreis == null) {
			if (other.verkaufspreis != null)
				return false;
		} 
		else if (!verkaufspreis.equals(other.verkaufspreis))
			return false;
		return true;
	}
//martin test
}
