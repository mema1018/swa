package de.shop.artikelverwaltung.domain;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ArtikelKatalog implements Serializable  {

	private static final long serialVersionUID = -7538264587125620282L;
	
	private List<Artikel> artikel;

	public List<Artikel> getArtikel() {
		return artikel;
	}

	public void setArtikel(List<Artikel> artikel) {
		this.artikel = artikel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artikel == null) ? 0 : artikel.hashCode());
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
		ArtikelKatalog other = (ArtikelKatalog) obj;
		if (artikel == null) {
			if (other.artikel != null)
				return false;
		} else if (!artikel.equals(other.artikel))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ArtikelKatalog [artikel=" + artikel + "]";
	}
	
	
}
