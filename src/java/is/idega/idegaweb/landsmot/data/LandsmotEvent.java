package is.idega.idegaweb.landsmot.data;


import java.sql.Timestamp;
import com.idega.data.IDOEntity;

public interface LandsmotEvent extends IDOEntity {
	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotEventBMPBean#setName
	 */
	public void setName(String name);

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotEventBMPBean#getName
	 */
	public String getName();

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotEventBMPBean#setDesciption
	 */
	public void setDesciption(String description);

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotEventBMPBean#getDescription
	 */
	public String getDescription();

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotEventBMPBean#setFromDate
	 */
	public void setFromDate(Timestamp timestamp);

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotEventBMPBean#getFromDate
	 */
	public Timestamp getFromDate();

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotEventBMPBean#setEndDate
	 */
	public void setEndDate(Timestamp timestamp);

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotEventBMPBean#getEndDate
	 */
	public Timestamp getEndDate();

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotEventBMPBean#setGroups
	 */
	public void setGroups(boolean isGroups);

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotEventBMPBean#getGroups
	 */
	public boolean getGroups();

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotEventBMPBean#setPrice
	 */
	public void setPrice(float price);

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotEventBMPBean#getPrice
	 */
	public float getPrice();

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotEventBMPBean#setCurrency
	 */
	public void setCurrency(String currency);

	/**
	 * @see is.idega.idegaweb.landsmot.data.LandsmotEventBMPBean#getCurrency
	 */
	public String getCurrency();
}