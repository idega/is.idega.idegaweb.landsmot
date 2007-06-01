package is.idega.idegaweb.landsmot.data;


import com.idega.data.IDORelationshipException;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.user.data.User;
import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class LandsmotRegistrationHomeImpl extends IDOFactory implements
		LandsmotRegistrationHome {
	public Class getEntityInterfaceClass() {
		return LandsmotRegistration.class;
	}

	public LandsmotRegistration create() throws CreateException {
		return (LandsmotRegistration) super.createIDO();
	}

	public LandsmotRegistration findByPrimaryKey(Object pk)
			throws FinderException {
		return (LandsmotRegistration) super.findByPrimaryKeyIDO(pk);
	}

	public LandsmotRegistration findByUserAndEvent(User user,
			LandsmotEvent event) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((LandsmotRegistrationBMPBean) entity)
				.ejbFindByUserAndEvent(user, event);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findByEvent(LandsmotEvent event) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((LandsmotRegistrationBMPBean) entity)
				.ejbFindByEvent(event);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findByGroupRegistration(
			LandsmotGroupRegistration groupRegistration)
			throws IDORelationshipException, FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((LandsmotRegistrationBMPBean) entity)
				.ejbFindByGroupRegistration(groupRegistration);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}