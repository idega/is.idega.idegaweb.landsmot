package is.idega.idegaweb.landsmot.data;


import com.idega.user.data.User;
import com.idega.block.trade.data.CreditCardInformation;
import java.sql.Timestamp;
import com.idega.data.IDOEntity;

public interface LandsmotRegistration extends IDOEntity {
	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotRegistrationBMPBean#setUser
	 */
	public void setUser(User user);

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotRegistrationBMPBean#getUser
	 */
	public User getUser();

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotRegistrationBMPBean#setDate
	 */
	public void setDate(Timestamp stamp);

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotRegistrationBMPBean#getDate
	 */
	public Timestamp getDate();

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotRegistrationBMPBean#setCreditCardInformation
	 */
	public void setCreditCardInformation(CreditCardInformation info);

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotRegistrationBMPBean#getCreditCardInformation
	 */
	public CreditCardInformation getCreditCardInformation();

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotRegistrationBMPBean#setCreditCardAuthorizationCode
	 */
	public void setCreditCardAuthorizationCode(String code);

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotRegistrationBMPBean#getCreditCardAuthorizationCode
	 */
	public String getCreditCardAuthorizationCode();

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotRegistrationBMPBean#setEvent
	 */
	public void setEvent(LandsmotEvent event);

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotRegistrationBMPBean#getEvent
	 */
	public LandsmotEvent getEvent();

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotRegistrationBMPBean#setGroupRegistration
	 */
	public void setGroupRegistration(LandsmotGroupRegistration group);

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotRegistrationBMPBean#getGroupRegistration
	 */
	public LandsmotGroupRegistration getGroupRegistration();
}