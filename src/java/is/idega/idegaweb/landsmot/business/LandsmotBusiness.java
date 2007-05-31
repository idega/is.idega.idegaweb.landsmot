package is.idega.idegaweb.landsmot.business;


import com.idega.block.creditcard.business.CreditCardAuthorizationException;
import com.idega.user.data.User;
import java.rmi.RemoteException;
import com.idega.data.IDOCreateException;
import com.idega.user.data.Group;
import java.util.Locale;
import java.util.Collection;
import com.idega.util.IWTimestamp;
import com.idega.business.IBOService;
import com.idega.user.business.UserBusiness;
import com.idega.business.IBOLookupException;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.AddressHome;

public interface LandsmotBusiness extends IBOService {
	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#isRegisteredInRun
	 */
	public boolean isRegisteredInRun(int runID, int userID) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#isRegisteredInRun
	 */
	public boolean isRegisteredInRun(int runID, String personalID) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#getAgeFromPersonalID
	 */
	public int getAgeFromPersonalID(String personalID) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#saveRun
	 */
	public void saveRun(int userID, String run, String distance, String year, String nationality, String tshirt, String chipOwnershipStatus, String chipNumber, String groupName, String bestTime, String goalTime, Locale locale) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#saveParticipants
	 */
	public Collection saveParticipants(Collection runners, String email, String hiddenCardNumber, double amount, IWTimestamp date, Locale locale) throws IDOCreateException, RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#finishPayment
	 */
	public void finishPayment(String properties) throws CreditCardAuthorizationException, RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#authorizePayment
	 */
	public String authorizePayment(String nameOnCard, String cardNumber, String monthExpires, String yearExpires, String ccVerifyNumber, double amount, String currency, String referenceNumber) throws CreditCardAuthorizationException, RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#getCreditCardImages
	 */
	public Collection getCreditCardImages() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#savePayment
	 */
	public void savePayment(int userID, int distanceID, String payMethod, String amount) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#savePaymentByUserID
	 */
	public void savePaymentByUserID(int userID, String payMethod, String amount) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#getRunGroupByGroupId
	 */
	public Group getRunGroupByGroupId(Integer groupId) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#sendMessage
	 */
	public void sendMessage(String email, String subject, String body) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#getRunGroupOfTypeForGroup
	 */
	public Group getRunGroupOfTypeForGroup(Group group, String type) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#getUserAge
	 */
	public int getUserAge(User user) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#getCountries
	 */
	public Collection getCountries() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#getUserBiz
	 */
	public UserBusiness getUserBiz() throws IBOLookupException, RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#getCountryByNationality
	 */
	public Country getCountryByNationality(Object nationality) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotBusinessBean#getAddressHome
	 */
	public AddressHome getAddressHome() throws RemoteException;
}