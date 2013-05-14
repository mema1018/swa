package de.shop.artikelverwaltung.domain;

import static de.shop.util.Constants.MIN_ID;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.shop.util.IdGroup;

public class Artikel implements Serializable {
	private static final long serialVersionUID = 1472129607838538329L;
	
	public static final int BEZEICHNUNG_LENGTH_MIN = 2;
	public static final int BEZEICHNUNG_LENGTH_MAX = 32;
	
	@Min(value = MIN_ID, message = "{artikelverwaltung.artikel.id.min}", groups = IdGroup.class)
	private Long id;
	
	@NotNull(message = "{artikelverwaltung.artikel.bezeichnung.notNull}")
	@Size(min = BEZEICHNUNG_LENGTH_MIN, max = BEZEICHNUNG_LENGTH_MAX,
	      message = "{artikelverwaltung.artikel.bezeichnung.length}")
	private String bezeichnung;
	
		
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bezeichnung == null) ? 0 : bezeichnung.hashCode());
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
		} else if (!bezeichnung.equals(other.bezeichnung))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Artikel [id=" + id + ", bezeichnung=" + bezeichnung + "]";
	}
}
