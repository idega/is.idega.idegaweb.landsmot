package is.idega.idegaweb.landsmot.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class LandsmotBusinessHomeImpl extends IBOHomeImpl implements LandsmotBusinessHome {
	public Class getBeanInterfaceClass() {
		return LandsmotBusiness.class;
	}

	public LandsmotBusiness create() throws CreateException {
		return (LandsmotBusiness) super.createIBO();
	}
}