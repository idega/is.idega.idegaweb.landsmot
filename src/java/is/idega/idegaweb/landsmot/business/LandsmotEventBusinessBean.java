package is.idega.idegaweb.landsmot.business;

import is.idega.idegaweb.landsmot.data.LandsmotEvent;
import is.idega.idegaweb.landsmot.data.LandsmotEventHome;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.business.IBOServiceBean;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORuntimeException;

public class LandsmotEventBusinessBean extends IBOServiceBean implements LandsmotEventBusiness{

	public Collection getAllSingleEvents() {
		try {
			return getEventHome().findAll(false);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Collection getAllGroupEvents() {
		try {
			return getEventHome().findAll(true);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public LandsmotEvent getEvent(Object primaryKey) {
		try {
			return getEventHome().findByPrimaryKey(primaryKey);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
	}

	private LandsmotEventHome getEventHome() {
		try {
			return (LandsmotEventHome) IDOLookup.getHome(LandsmotEvent.class);
		} catch (IDOLookupException e) {
			throw new IDORuntimeException(e);
		}
	}
}
