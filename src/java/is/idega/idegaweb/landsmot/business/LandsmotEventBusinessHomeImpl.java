package is.idega.idegaweb.landsmot.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class LandsmotEventBusinessHomeImpl extends IBOHomeImpl implements
		LandsmotEventBusinessHome {
	public Class getBeanInterfaceClass() {
		return LandsmotEventBusiness.class;
	}

	public LandsmotEventBusiness create() throws CreateException {
		return (LandsmotEventBusiness) super.createIBO();
	}
}