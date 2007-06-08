package is.idega.idegaweb.landsmot.business;

import is.idega.idegaweb.landsmot.data.LandsmotEvent;
import is.idega.idegaweb.landsmot.data.LandsmotEventHome;
import is.idega.idegaweb.landsmot.data.LandsmotGroupRegistration;
import is.idega.idegaweb.landsmot.data.LandsmotGroupRegistrationHome;
import is.idega.idegaweb.landsmot.data.LandsmotRegistration;
import is.idega.idegaweb.landsmot.data.LandsmotRegistrationHome;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.business.IBOServiceBean;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORuntimeException;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.UnavailableIWContext;
import com.idega.presentation.IWContext;
import com.idega.user.data.User;
import com.idega.user.data.UserHome;
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

	public LandsmotGroupRegistration register(String groupName, Collection participants, LandsmotEvent event, String email,IWTimestamp date) {
		LandsmotGroupRegistration groupRegistration = null;
		try {
			groupRegistration = getLandsmotGroupRegistrationHome().create();
			groupRegistration.setName(groupName);
			groupRegistration.setEvent(event);
			groupRegistration.setDate(date.getTimestamp());
			groupRegistration.store();
			
			if (participants != null && !participants.isEmpty()) {
				Iterator iter = participants.iterator();
				while (iter.hasNext()) {
					User participant = (User) iter.next();
					LandsmotRegistration registration = register(participant, null, null, date);
					if (registration != null) {
						registration.setGroupRegistration(groupRegistration);
						registration.store();
					}
				}
			}
			
		} catch (CreateException e) {
			e.printStackTrace();
		}
		return groupRegistration;
	}
	
	
	public LandsmotRegistration register(User user, LandsmotEvent event, String email,IWTimestamp date) {
		LandsmotRegistration reg = null;
		if (event != null) {
			try {
				reg = getLandsmotRegistrationHome().findByUserAndEvent(user, event);
			} catch (FinderException e) {
			}
		}
		
		if (reg == null) {
			try {
				reg = getLandsmotRegistrationHome().create();
				reg.setUser(user);
				if (event != null) {
					reg.setEvent(event);
				}
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
	
	public String getUserNameDWR(String personalID, String localeStr) {
		try {
			if (personalID != null && !personalID.trim().equals("")) {
				UserHome uHome = (UserHome) IDOLookup.getHome(User.class);
				User user = uHome.findByPersonalID(personalID);
				return user.getName();
			} else {
				return "";
			}
		} catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (FinderException e) {
		}
		
		try {
			IWContext iwc = IWContext.getInstance();
			Locale locale = iwc.getCurrentLocale();
			IWResourceBundle iwrb = getBundle().getResourceBundle(locale);
			return iwrb.getLocalizedString("landsmot.user_not_found", "User not found");
		} catch (UnavailableIWContext e) {
			return "User not found";
		}
	}
	
	public String getBundleIdentifier() {
		return is.idega.idegaweb.landsmot.presentation.LandsmotRegistration.IW_BUNDLE_IDENTIFIER;
	}
	
	private LandsmotGroupRegistrationHome getLandsmotGroupRegistrationHome() {
		try {
			return (LandsmotGroupRegistrationHome) IDOLookup.getHome(LandsmotGroupRegistration.class);
		} catch (IDOLookupException e) {
			throw new IDORuntimeException(e);
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
