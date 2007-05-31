package is.idega.idegaweb.landsmot.data;


import com.idega.data.IDORelationshipException;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class LandsmotGroupRegistrationHomeImpl extends IDOFactory implements
		LandsmotGroupRegistrationHome {
	public Class getEntityInterfaceClass() {
		return LandsmotGroupRegistration.class;
	}

	public LandsmotGroupRegistration create() throws CreateException {
		return (LandsmotGroupRegistration) super.createIDO();
	}

	public LandsmotGroupRegistration findByPrimaryKey(Object pk)
			throws FinderException {
		return (LandsmotGroupRegistration) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findByEvent(LandsmotEvent event)
			throws IDORelationshipException, FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((LandsmotGroupRegistrationBMPBean) entity)
				.ejbFindByEvent(event);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}