package is.idega.idegaweb.landsmot.data;


import java.util.Collection;
import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface LandsmotEventHome extends IDOHome {
	public LandsmotEvent create() throws CreateException;

	public LandsmotEvent findByPrimaryKey(Object pk) throws FinderException;

	public Collection findAll(boolean group) throws FinderException;
}