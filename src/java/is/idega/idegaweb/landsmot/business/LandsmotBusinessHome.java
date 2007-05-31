package is.idega.idegaweb.landsmot.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHome;
import java.rmi.RemoteException;

public interface LandsmotBusinessHome extends IBOHome {
	public LandsmotBusiness create() throws CreateException, RemoteException;
}