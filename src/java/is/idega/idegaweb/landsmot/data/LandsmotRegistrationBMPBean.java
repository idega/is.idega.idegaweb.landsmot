package is.idega.idegaweb.landsmot.data;

import java.sql.Timestamp;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.block.trade.data.CreditCardInformation;
import com.idega.data.GenericEntity;
import com.idega.data.IDORelationshipException;
import com.idega.data.query.Column;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.user.data.User;

public class LandsmotRegistrationBMPBean extends GenericEntity implements LandsmotRegistration {

	private static final String TABLE_NAME = "LA_EVENT_REGISTRATION";
	private static final String COLUMN_USER = "IC_USER_ID";
	private static final String COLUMN_DATE = "REGISTRATION_DATE";
	private static final String COLUMN_CREDITCARD_INFO = "CC_INFO_ID";
	private static final String COLUMN_CREDITCARD_AUTHORIZATION = "CC_AUTH_CODE";
	
	public String getEntityName() {
		return TABLE_NAME;
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(COLUMN_USER, "user", User.class);
		addAttribute(COLUMN_DATE, "date", Timestamp.class);
		addManyToOneRelationship(COLUMN_CREDITCARD_INFO, CreditCardInformation.class);
		addAttribute(COLUMN_CREDITCARD_AUTHORIZATION, "cc_auth", String.class, 10);
		addManyToManyRelationShip(LandsmotEvent.class);
		addManyToManyRelationShip(LandsmotGroupRegistrationBMPBean.class);
	}

	public void setUser(User user) {
		setColumn(COLUMN_USER, user);
	}
	
	public User getUser() {
		return (User) getColumnValue(COLUMN_USER);
	}
	
	public void setDate(Timestamp stamp) {
		setColumn(COLUMN_DATE, stamp);
	}
	
	public Timestamp getDate() {
		return getTimestampColumnValue(COLUMN_DATE);
	}
	
	public void setCreditCardInformation(CreditCardInformation info) {
		setColumn(COLUMN_CREDITCARD_INFO, info);
	}
	
	public CreditCardInformation getCreditCardInformation() {
		return (CreditCardInformation) getColumnValue(COLUMN_CREDITCARD_INFO);
	}
	
	public void setCreditCardAuthorizationCode(String code) {
		setColumn(COLUMN_CREDITCARD_AUTHORIZATION, code);
	}
	
	public String getCreditCardAuthorizationCode() {
		return getStringColumnValue(COLUMN_CREDITCARD_AUTHORIZATION);
	}
	
	public Collection ejbFindByEvent(LandsmotEvent event) throws IDORelationshipException, FinderException {
		Table table = new Table(this);
		Table evTable = new Table(event);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));
		query.addJoin(table, evTable);
		query.addCriteria(new MatchCriteria(new Column(evTable, "LA_EVENT_ID"), MatchCriteria.EQUALS, event));
		
		return idoFindPKsByQuery(query);
	}
	
	public Collection ejbFindByGroupRegistration(LandsmotGroupRegistration groupRegistration) throws IDORelationshipException, FinderException {
		Table table = new Table(this);
		Table grvTable = new Table(groupRegistration);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));
		query.addJoin(table, grvTable);
		query.addCriteria(new MatchCriteria(new Column(grvTable, "LA_GROUP_REGISTRATION_ID"), MatchCriteria.EQUALS, groupRegistration));
		
		return idoFindPKsByQuery(query);
	}
}
