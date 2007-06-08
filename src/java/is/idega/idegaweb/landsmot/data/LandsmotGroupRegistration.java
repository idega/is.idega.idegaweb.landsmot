package is.idega.idegaweb.landsmot.data;


import com.idega.block.trade.data.CreditCardInformation;
import java.sql.Timestamp;
import com.idega.data.IDOEntity;

public interface LandsmotGroupRegistration extends IDOEntity {
	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotGroupRegistrationBMPBean#setName
	 */
	public void setName(String name);

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotGroupRegistrationBMPBean#getName
	 */
	public String getName();

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotGroupRegistrationBMPBean#setDate
	 */
	public void setDate(Timestamp stamp);

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotGroupRegistrationBMPBean#getDate
	 */
	public Timestamp getDate();

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotGroupRegistrationBMPBean#setCreditCardInformation
	 */
	public void setCreditCardInformation(CreditCardInformation info);

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotGroupRegistrationBMPBean#getCreditCardInformation
	 */
	public CreditCardInformation getCreditCardInformation();

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotGroupRegistrationBMPBean#setCreditCardAuthorizationCode
	 */
	public void setCreditCardAuthorizationCode(String code);

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotGroupRegistrationBMPBean#getCreditCardAuthorizationCode
	 */
	public String getCreditCardAuthorizationCode();

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotGroupRegistrationBMPBean#setEvent
	 */
	public void setEvent(LandsmotEvent event);

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotGroupRegistrationBMPBean#getEvent
	 */
	public LandsmotEvent getEvent();
}