package is.idega.idegaweb.landsmot.business;


import java.util.Collection;
import com.idega.business.IBOService;
import is.idega.idegaweb.landsmot.data.LandsmotEvent;
import java.rmi.RemoteException;

public interface LandsmotEventBusiness extends IBOService {
	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotEventBusinessBean#getAllSingleEvents
	 */
	public Collection getAllSingleEvents() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotEventBusinessBean#getAllGroupEvents
	 */
	public Collection getAllGroupEvents() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotEventBusinessBean#getEvent
	 */
	public LandsmotEvent getEvent(Object primaryKey) throws RemoteException;
}