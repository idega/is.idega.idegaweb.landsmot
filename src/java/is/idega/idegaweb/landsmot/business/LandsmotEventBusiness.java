package is.idega.idegaweb.landsmot.business;


import is.idega.idegaweb.landsmot.data.LandsmotRegistration;
import java.util.Collection;
import com.idega.util.IWTimestamp;
import com.idega.business.IBOService;
import is.idega.idegaweb.landsmot.data.LandsmotEvent;
import com.idega.user.data.User;
import java.rmi.RemoteException;

public interface LandsmotEventBusiness extends IBOService {
	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotEventBusinessBean#getAllSingleEvents
	 */
	public Collection getAllSingleEvents() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotEventBusinessBean#register
	 */
	public LandsmotRegistration register(User user, LandsmotEvent event,
			String email, IWTimestamp date) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotEventBusinessBean#getAllGroupEvents
	 */
	public Collection getAllGroupEvents() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.landsmot.business.LandsmotEventBusinessBean#getEvent
	 */
	public LandsmotEvent getEvent(Object primaryKey) throws RemoteException;
}