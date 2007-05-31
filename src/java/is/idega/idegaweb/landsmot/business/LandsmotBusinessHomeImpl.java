package is.idega.idegaweb.landsmot.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class LandsmotBusinessHomeImpl extends IBOHomeImpl implements LandsmotBusinessHome {
	
	private static final long serialVersionUID = 3105168986587179338L;
	
	public Class getBeanInterfaceClass() {
		return LandsmotBusiness.class;
	}

	public LandsmotBusiness create() throws CreateException {
		return (LandsmotBusiness) super.createIBO();
	}
}