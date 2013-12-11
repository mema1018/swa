package de.shop.util.web;

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import de.shop.util.Log;


/**
 * @author <a href="mailto:Juergen.Zimmermann@HS-Karlsruhe.de">J&uuml;rgen Zimmermann</a>
 */
@Named
@SessionScoped
public class SpracheModel implements Serializable {
	private static final long serialVersionUID = 1986565724093259408L;
	
	@Produces
	@Named
	@Client
	private Locale locale;

	
	@PostConstruct
	private void postConstruct() {
		locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
	}

	@Log
	public void change(String localeStr) {
		final Locale newLocale = new Locale(localeStr);
		if (newLocale.equals(locale)) {
			return;
		}
		
		locale = newLocale;
		
		final FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getViewRoot().setLocale(locale);
		ctx.renderResponse();
	}
		
	@Override
	public String toString() {
		return "SpracheModel [locale=" + locale + "]";
	}
}
