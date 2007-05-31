package is.idega.idegaweb.landsmot.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHome;
import java.rmi.RemoteException;

public interface LandsmotEventBusinessHome extends IBOHome {
	public LandsmotEventBusiness create() throws CreateException,
			RemoteException;
}