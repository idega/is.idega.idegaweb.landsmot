package is.idega.idegaweb.landsmot.data;


import com.idega.data.IDORelationshipException;
import java.util.Collection;
import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface LandsmotGroupRegistrationHome extends IDOHome {
	public LandsmotGroupRegistration create() throws CreateException;

	public LandsmotGroupRegistration findByPrimaryKey(Object pk)
			throws FinderException;

	public Collection findByEvent(LandsmotEvent event)
			throws IDORelationshipException, FinderException;
}