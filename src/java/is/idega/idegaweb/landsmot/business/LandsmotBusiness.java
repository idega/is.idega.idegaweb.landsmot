package is.idega.idegaweb.landsmot.business;


import java.util.Locale;
import java.util.Collection;
import com.idega.block.creditcard.business.CreditCardAuthorizationException;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.util.IWTimestamp;
import com.idega.business.IBOService;
import java.rmi.RemoteException;
import com.idega.data.IDOCreateException;
import com.idega.idegaweb.IWResourceBundle;

public interface LandsmotBusiness extends IBOService {
	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#saveParticipants
	 */
	public Collection saveParticipants(Collection runners, String email,
			String hiddenCardNumber, double amount, IWTimestamp date,
			Locale locale) throws IDOCreateException, RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#finishPayment
	 */
	public String finishPayment(String properties)
			throws CreditCardAuthorizationException, RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#authorizePayment
	 */
	public String authorizePayment(String nameOnCard, String cardNumber,
			String monthExpires, String yearExpires, String ccVerifyNumber,
			double amount, String currency, String referenceNumber)
			throws CreditCardAuthorizationException, RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#getCreditCardImages
	 */
	public Collection getCreditCardImages() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#sendMessage
	 */
	public void sendMessage(String email, String subject, String body) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#getCountries
	 */
	public Collection getCountries() throws RemoteException;
	
	public DropdownMenu getAvailableCardTypes(IWResourceBundle iwrb);
}