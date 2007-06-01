package is.idega.idegaweb.landsmot.business;

import is.idega.idegaweb.landsmot.data.LandsmotEvent;
import is.idega.idegaweb.landsmot.data.LandsmotRegistration;

import java.rmi.RemoteException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.ejb.FinderException;

import com.idega.block.creditcard.business.CreditCardAuthorizationException;
import com.idega.block.creditcard.business.CreditCardBusiness;
import com.idega.block.creditcard.business.CreditCardClient;
import com.idega.block.creditcard.data.CreditCardMerchant;
import com.idega.block.creditcard.data.KortathjonustanMerchant;
import com.idega.block.creditcard.data.KortathjonustanMerchantHome;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.business.IBOServiceBean;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.CountryHome;
import com.idega.data.IDOCreateException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWBundle;
import com.idega.user.business.GroupBusiness;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.GroupHome;
import com.idega.user.data.User;
import com.idega.util.Age;
import com.idega.util.IWTimestamp;

public class LandsmotBusinessBean extends IBOServiceBean implements LandsmotBusiness {
	
	private static final long serialVersionUID = 3105168986587179337L;
	
	public final static String IW_BUNDLE_IDENTIFIER = "is.idega.idegaweb.landsmot";
	private static String DEFAULT_SMTP_MAILSERVER = "mail.simnet.is";
	private static String PROP_SYSTEM_SMTP_MAILSERVER = "messagebox_smtp_mailserver";
	private static String PROP_CC_ADDRESS = "messagebox_cc_address";
	private static String PROP_MESSAGEBOX_FROM_ADDRESS = "messagebox_from_mailaddress";
	private static String DEFAULT_MESSAGEBOX_FROM_ADDRESS = "messagebox@idega.com";
	private static String DEFAULT_CC_ADDRESS = "";
	public static final String PROPERTY_MERCHANT_PK = "merchant_pk";
	public static final String PROPERTY_SEND_EMAILS = "send_emails";

	public boolean isRegisteredInRun(int runID, int userID) {
		try {
			User user = getUserBiz().getUserHome().findByPrimaryKey(new Integer(userID));
			
			return getUserBiz().isMemberOfGroup(runID, user);
		}
		catch (RemoteException re) {
			log(re);
		}
		catch (FinderException fe) {
			//User does not exist in database...
		}
		return false;
	}
	
	public boolean isRegisteredInRun(int runID, String personalID) {
		try {
			User user = getUserBiz().getUserHome().findByPersonalID(personalID);
			
			return getUserBiz().isMemberOfGroup(runID, user);
		}
		catch (RemoteException re) {
			log(re);
		}
		catch (FinderException fe) {
			//User does not exist in database...
		}
		return false;
	}

	/**
	 * 
	 * @param pin -
	 *            a social security number - format ddmmyyxxxx or ddmmyyyy
	 * @return IWTimstamp - the date of birth from the pin..
	 */
	private IWTimestamp getBirthDateFromSSN(String pin) {
		//pin format = 14011973
		if (pin.length() == 8) {
			int edd = Integer.parseInt(pin.substring(0, 2));
			int emm = Integer.parseInt(pin.substring(2, 4));
			int eyyyy = Integer.parseInt(pin.substring(4, 8));
			IWTimestamp dob = new IWTimestamp(edd, emm, eyyyy);
			return dob;
		}
		//  pin format = 140173xxxx ddmmyyxxxx
		else if (pin.length() == 10) {
			int dd = Integer.parseInt(pin.substring(0, 2));
			int mm = Integer.parseInt(pin.substring(2, 4));
			int yy = Integer.parseInt(pin.substring(4, 6));
			int century = Integer.parseInt(pin.substring(9, 10));
			int yyyy = 0;
			if (century == 9) {
				yyyy = yy + 1900;
			}
			else if (century == 0) {
				yyyy = yy + 2000;
			}
			IWTimestamp dob = new IWTimestamp(dd, mm, yyyy);
			return dob;
		}
		else {
			return null;
		}
	}
	
	public int getAgeFromPersonalID(String personalID) {
		if (personalID != null) {
			IWTimestamp dateOfBirth = getBirthDateFromSSN(personalID);
			if (dateOfBirth != null) {
				Age age = new Age(dateOfBirth.getDate());
				return age.getYears();
			}
		}
		return -1;
	}

	/**
	 * saves information on the run for the specific user puts user in the right
	 * group
	 */
	public void saveRun(int userID, String run, String distance, String year, String nationality, String tshirt, String chipOwnershipStatus, String chipNumber, String groupName, String bestTime, String goalTime, Locale locale) {
	}
		
	public Collection saveParticipants(Collection runners, String email, String hiddenCardNumber, double amount, IWTimestamp date, Locale locale) throws IDOCreateException, RemoteException {
		Collection participants = new ArrayList();
		if (runners != null) {
			Iterator iter = runners.iterator();
			while (iter.hasNext()) {
				Runner runner = (Runner) iter.next();
				Collection evs = runner.getEvents();
				if (evs != null && !evs.isEmpty()) {
					Iterator eter = evs.iterator();
					while (eter.hasNext()) {
						LandsmotEvent event = (LandsmotEvent) eter.next();
						LandsmotRegistration reg = getEventBusiness().register(runner.getUser(), event, email, date);
						if (reg != null) {
							participants.add(reg);
						}
					}
				}
			}
		}
		return participants;
	}
	
	
	public String finishPayment(String properties) throws CreditCardAuthorizationException {
		try {
			CreditCardClient client = getCreditCardBusiness().getCreditCardClient(getCreditCardMerchant());
			return client.finishTransaction(properties);
		}
		catch (CreditCardAuthorizationException ccae) {
			throw ccae;
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			throw new CreditCardAuthorizationException("Online payment failed. Unknown error.");
		}
	}
	
	public String authorizePayment(String nameOnCard, String cardNumber, String monthExpires, String yearExpires, String ccVerifyNumber, double amount, String currency, String referenceNumber) throws CreditCardAuthorizationException {
		try {
			CreditCardClient client = getCreditCardBusiness().getCreditCardClient(getCreditCardMerchant());
			return client.creditcardAuthorization(nameOnCard, cardNumber, monthExpires, yearExpires, ccVerifyNumber, amount, currency, referenceNumber);
		}
		catch (CreditCardAuthorizationException ccae) {
			throw ccae;
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			throw new CreditCardAuthorizationException("Online payment failed. Unknown error.");
		}
	}

	public Collection getCreditCardImages() {
		try {
			CreditCardMerchant merchant = getCreditCardMerchant();
			if (merchant != null) {
				return getCreditCardBusiness().getCreditCardTypeImages(getCreditCardBusiness().getCreditCardClient(merchant));
			}
		}
		catch (FinderException fe) {
			fe.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList();
	}
	
	private CreditCardMerchant getCreditCardMerchant() throws FinderException {
		String merchantPK = getIWApplicationContext().getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER).getProperty(PROPERTY_MERCHANT_PK);
		if (merchantPK != null) {
			try {
				return ((KortathjonustanMerchantHome) IDOLookup.getHome(KortathjonustanMerchant.class)).findByPrimaryKey(new Integer(merchantPK));
			}
			catch (IDOLookupException ile) {
				throw new IBORuntimeException(ile);
			}
		}
		return null;
	}
	
	public void savePayment(int userID, int distanceID, String payMethod, String amount) {
	
	}
	
	public void savePaymentByUserID(int userID, String payMethod, String amount) {
		}	

	public Group getRunGroupByGroupId(Integer groupId) {
		try {
			GroupHome groupHome = (GroupHome) getIDOHome(Group.class);
			return groupHome.findByPrimaryKey(groupId);
		}
		catch (RemoteException e) {
			log(e);
		}
		catch (FinderException e) {
			log(e);
		}
		return null;
	}

	public void sendMessage(String email, String subject, String body) {

		boolean sendEmail = true;
		String sSendEmail = this.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER).getProperty(PROPERTY_SEND_EMAILS);
		if ("no".equalsIgnoreCase(sSendEmail)) {
			sendEmail = false;
		}
		
		if (sendEmail) {
			String mailServer = DEFAULT_SMTP_MAILSERVER;
			String fromAddress = DEFAULT_MESSAGEBOX_FROM_ADDRESS;
			String cc = DEFAULT_CC_ADDRESS;
			try {
				IWBundle iwb = getIWApplicationContext().getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER);
				mailServer = iwb.getProperty(PROP_SYSTEM_SMTP_MAILSERVER, DEFAULT_SMTP_MAILSERVER);
				fromAddress = iwb.getProperty(PROP_MESSAGEBOX_FROM_ADDRESS, DEFAULT_MESSAGEBOX_FROM_ADDRESS);
				cc = iwb.getProperty(PROP_CC_ADDRESS, DEFAULT_CC_ADDRESS);
			}
			catch (Exception e) {
				System.err.println("MessageBusinessBean: Error getting mail property from bundle");
				e.printStackTrace();
			}
	
			cc = "";
			
			try {
				com.idega.util.SendMail.send(fromAddress, email.trim(), cc, "", mailServer, subject, body);
			}
			catch (javax.mail.MessagingException me) {
				System.err.println("Error sending mail to address: " + email + " Message was: " + me.getMessage());
			}
		}
	}

	public Group getRunGroupOfTypeForGroup(Group group, String type) {
		
		String[] types = {type};
		Collection r = null;
		Group run = null;

		try {
			r = getGroupBiz().getParentGroupsRecursive(group,types,true);
		}
		catch (RemoteException e1) {
			e1.printStackTrace();
		}
		if(r != null) {
			Iterator rIter = r.iterator();
			if(rIter.hasNext()) {
				 run = (Group) rIter.next();
			}
		}
		return run;
	}

	/**
	 * 
	 * @param user
	 * @return an int representing the age of the user
	 */
	public int getUserAge(User user) {
		Date dob = user.getDateOfBirth();
		IWTimestamp t = new IWTimestamp(dob);
		int birthYear = t.getYear();
		IWTimestamp time = IWTimestamp.RightNow();
		int year = time.getYear();
		return year - birthYear;
	}

	/**
	 * Gets all countries. This method is for example used when displaying a
	 * dropdown menu of all countries
	 * 
	 * @return Colleciton of all countries
	 */
	public Collection getCountries() {
		Collection countries = null;
		try {
			CountryHome countryHome = (CountryHome) getIDOHome(Country.class);
			countries = new ArrayList(countryHome.findAll());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return countries;
	}

	private GroupBusiness getGroupBiz() throws IBOLookupException {
		GroupBusiness business = (GroupBusiness) IBOLookup.getServiceInstance(getIWApplicationContext(), GroupBusiness.class);
		return business;
	}

	private CreditCardBusiness getCreditCardBusiness() {
		try {
			return (CreditCardBusiness) IBOLookup.getServiceInstance(getIWApplicationContext(), CreditCardBusiness.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	public UserBusiness getUserBiz() throws IBOLookupException {
		UserBusiness business = (UserBusiness) IBOLookup.getServiceInstance(getIWApplicationContext(), UserBusiness.class);
		return business;
	}
	
	public Country getCountryByNationality(Object nationality) {
		Country country = null;
		try {
			CountryHome home = (CountryHome) getIDOHome(Country.class);
			try {
				int countryPK = Integer.parseInt(nationality.toString());
				country = home.findByPrimaryKey(new Integer(countryPK));
			}
			catch (NumberFormatException nfe) {
				country = home.findByIsoAbbreviation(nationality.toString());
			}
		}
		catch (FinderException fe) {
			//log(fe);
		}
		catch (RemoteException re) {
			//log(re);
		}
		return country;
	}

	public LandsmotEventBusiness getEventBusiness() {
		try {
			return (LandsmotEventBusiness) getServiceInstance(LandsmotEventBusiness.class);
		} catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}
}