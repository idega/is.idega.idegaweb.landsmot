package is.idega.idegaweb.landsmot.data;


import com.idega.data.IDORelationshipException;
import java.util.Collection;
import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;
import com.idega.user.data.User;

public interface LandsmotRegistrationHome extends IDOHome {
	public LandsmotRegistration create() throws CreateException;

	public LandsmotRegistration findByPrimaryKey(Object pk)
			throws FinderException;

	public LandsmotRegistration findByUserAndEvent(User user,
			LandsmotEvent event) throws FinderException;

	public Collection findByEvent(LandsmotEvent event) throws FinderException;

	public Collection findByGroupRegistration(
			LandsmotGroupRegistration groupRegistration)
			throws IDORelationshipException, FinderException;
}