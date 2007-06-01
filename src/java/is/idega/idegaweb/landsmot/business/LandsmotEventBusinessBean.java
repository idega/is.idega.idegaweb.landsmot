package is.idega.idegaweb.landsmot.business;

import is.idega.idegaweb.landsmot.data.LandsmotEvent;
import is.idega.idegaweb.landsmot.data.LandsmotEventHome;
import is.idega.idegaweb.landsmot.data.LandsmotRegistration;
import is.idega.idegaweb.landsmot.data.LandsmotRegistrationHome;

import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.business.IBOServiceBean;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORuntimeException;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;

public class LandsmotEventBusinessBean extends IBOServiceBean implements LandsmotEventBusiness{

	public Collection getAllSingleEvents() {
		try {
			return getEventHome().findAll(false);
		} catch (FinderException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public LandsmotRegistration register(User user, LandsmotEvent event, String email,IWTimestamp date) {
		LandsmotRegistration reg = null;
		try {
			reg = getLandsmotRegistrationHome().findByUserAndEvent(user, event);
		} catch (FinderException e) {
		}
		
		if (reg == null) {
			try {
				reg = getLandsmotRegistrationHome().create();
				reg.setUser(user);
				reg.setEvent(event);
				reg.setDate(date.getTimestamp());
				reg.store();
			} catch (CreateException e) {
				e.printStackTrace();
			}
		}
		return reg;
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
	
	private LandsmotRegistrationHome getLandsmotRegistrationHome() {
		try {
			return (LandsmotRegistrationHome) IDOLookup.getHome(LandsmotRegistration.class);
		} catch (IDOLookupException e) {
			throw new IDORuntimeException(e);
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
