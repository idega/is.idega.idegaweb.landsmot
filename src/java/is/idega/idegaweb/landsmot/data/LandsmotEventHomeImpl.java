package is.idega.idegaweb.landsmot.data;


import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class LandsmotEventHomeImpl extends IDOFactory implements
		LandsmotEventHome {
	public Class getEntityInterfaceClass() {
		return LandsmotEvent.class;
	}

	public LandsmotEvent create() throws CreateException {
		return (LandsmotEvent) super.createIDO();
	}

	public LandsmotEvent findByPrimaryKey(Object pk) throws FinderException {
		return (LandsmotEvent) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findAll(boolean group) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((LandsmotEventBMPBean) entity).ejbFindAll(group);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}