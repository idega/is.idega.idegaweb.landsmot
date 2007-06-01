package is.idega.idegaweb.landsmot.business;

import is.idega.idegaweb.landsmot.data.LandsmotRegistration;
import is.idega.idegaweb.landsmot.data.LandsmotRegistrationHome;

import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.transaction.UserTransaction;

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
import com.idega.idegaweb.IWResourceBundle;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;

public class LandsmotBusinessBean extends IBOServiceBean implements LandsmotBusiness {
	
	public final static String IW_BUNDLE_IDENTIFIER = "is.idega.idegaweb.landsmot";
	private static String DEFAULT_SMTP_MAILSERVER = "mail.simnet.is";
	private static String PROP_SYSTEM_SMTP_MAILSERVER = "messagebox_smtp_mailserver";
	private static String PROP_CC_ADDRESS = "messagebox_cc_address";
	private static String PROP_MESSAGEBOX_FROM_ADDRESS = "messagebox_from_mailaddress";
	private static String DEFAULT_MESSAGEBOX_FROM_ADDRESS = "messagebox@idega.com";
	private static String DEFAULT_CC_ADDRESS = "";
	public static final String PROPERTY_MERCHANT_PK = "merchant_pk";
	public static final String PROPERTY_SEND_EMAILS = "send_emails";

		
	public Collection saveParticipants(Collection runners, String email, String hiddenCardNumber, double amount, IWTimestamp date, Locale locale) throws IDOCreateException {
		Collection participants = new ArrayList();

		UserTransaction trans = getSessionContext().getUserTransaction();
		try {
			trans.begin();
			Iterator iter = runners.iterator();
			while (iter.hasNext()) {
				Runner runner = (Runner) iter.next();
				User user = runner.getUser();
				
				try {
					LandsmotRegistrationHome runHome = (LandsmotRegistrationHome) getIDOHome(LandsmotRegistration.class);
					LandsmotRegistration participant = runHome.create();
					participant.setUser(user);
					//if (runner.getAmount() > 0) {
					//	participant.setPayedAmount(String.valueOf(runner.getAmount()));
					//}
					
					//participant.setUserNationality(runner.getNationality().getName());
					participant.store();
					participants.add(participant);
					
					getUserBiz().updateUserHomePhone(user, runner.getHomePhone());
					getUserBiz().updateUserMobilePhone(user, runner.getMobilePhone());
					getUserBiz().updateUserMail(user, runner.getEmail());

					if (runner.getEmail() != null) {
						IWResourceBundle iwrb = getIWApplicationContext().getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER).getResourceBundle(locale);						
						Object[] args = { user.getName(), user.getPersonalID() };
						String subject = iwrb.getLocalizedString("registration_received_subject_mail", "Your registration has been received.");
						String body = MessageFormat.format(iwrb.getLocalizedString("registration_received_body_mail", "Your registration has been received."), args);
						sendMessage(runner.getEmail(), subject, body);
					}
				}
				catch (CreateException ce) {
					ce.printStackTrace();
				}
				catch (RemoteException re) {
					throw new IBORuntimeException(re);
				}
			}

			if (email != null) {
				IWResourceBundle iwrb = getIWApplicationContext().getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER).getResourceBundle(locale);
				Object[] args = { hiddenCardNumber, String.valueOf(amount), date.getLocaleDateAndTime(locale, IWTimestamp.SHORT, IWTimestamp.SHORT) };
				String subject = iwrb.getLocalizedString("receipt_subject_mail", "Your receipt for registration on Marathon.is");
				String body = MessageFormat.format(iwrb.getLocalizedString("receipt_body_mail", "Your registration has been received."), args);
				sendMessage(email, subject, body);
			}
			trans.commit();
		}
		catch (Exception ex) {
			try {
				trans.rollback();
			}
			catch (javax.transaction.SystemException e) {
				throw new IDOCreateException(e.getMessage());
			}
			ex.printStackTrace();
			throw new IDOCreateException(ex);
		}
		
		return participants;
	}
	
	
	public void finishPayment(String properties) throws CreditCardAuthorizationException {
		try {
			CreditCardClient client = getCreditCardBusiness().getCreditCardClient(getCreditCardMerchant());
			client.finishTransaction(properties);
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
			return getCreditCardBusiness().getCreditCardTypeImages(getCreditCardBusiness().getCreditCardClient(getCreditCardMerchant()));
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

}