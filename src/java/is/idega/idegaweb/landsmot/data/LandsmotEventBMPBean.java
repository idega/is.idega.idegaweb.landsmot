package is.idega.idegaweb.landsmot.data;

import java.sql.Timestamp;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.query.Column;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.OR;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;

public class LandsmotEventBMPBean extends GenericEntity implements LandsmotEvent {

	private static final String TABLE_NAME = "LA_EVENT";
	private static final String COLUMN_NAME = "NAME";
	private static final String COLUMN_DESCRIPTION = "DESCRIPTION";
	private static final String COLUMN_START_DATE = "START_DATE";
	private static final String COLUMN_END_DATE = "END_DATE";
	private static final String COLUMN_GROUPS = "GROUPS";
	private static final String COLUMN_GROUP_SIZE = "GROUP_SIZE";
	private static final String COLUMN_GROUP_SIZE_MAX = "GROUP_SIZE_MAX";
	private static final String COLUMN_PRICE = "PRICE";
	private static final String COLUMN_CURRENCY = "CURRENCY";
	private static final String COLUMN_IS_VALID = "IS_VALID";
	
	
	public String getEntityName() {
		return TABLE_NAME;
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(COLUMN_NAME, "Name", String.class);
		addAttribute(COLUMN_DESCRIPTION, " Description", String.class, 2000);
		addAttribute(COLUMN_START_DATE, "Start date", Timestamp.class);
		addAttribute(COLUMN_END_DATE, "End date", Timestamp.class);
		addAttribute(COLUMN_GROUPS, "Groups", Boolean.class);
		addAttribute(COLUMN_GROUP_SIZE, "GroupSize", Integer.class);
		addAttribute(COLUMN_GROUP_SIZE_MAX, "GroupSize Max", Integer.class);
		addAttribute(COLUMN_PRICE, "Price", Float.class);
		addAttribute(COLUMN_CURRENCY, "Currency", String.class);
		addAttribute(COLUMN_IS_VALID, "IS valid", Boolean.class);
	}

	public void setName(String name) {
		setColumn(COLUMN_NAME, name);
	}
	
	public String getName() {
		return getStringColumnValue(COLUMN_NAME);
	}
	
	public void setDesciption(String description) {
		setColumn(COLUMN_DESCRIPTION, description);
	}
	
	public String getDescription() {
		return getStringColumnValue(COLUMN_DESCRIPTION);
	}
	
	public void setFromDate(Timestamp timestamp) {
		setColumn(COLUMN_START_DATE, timestamp);
	}
	
	public Timestamp getFromDate() {
		return getTimestampColumnValue(COLUMN_START_DATE);
	}
	
	public void setEndDate(Timestamp timestamp) {
		setColumn(COLUMN_END_DATE, timestamp);
	}
	
	public Timestamp getEndDate() {
		return getTimestampColumnValue(COLUMN_END_DATE);
	}
	
	public void setGroups(boolean isGroups) {
		setColumn(COLUMN_GROUPS, isGroups);
	}
	
	public boolean getGroups() {
		return getBooleanColumnValue(COLUMN_GROUPS);
	}
	
	public int getMinSize() {
		return getIntColumnValue(COLUMN_GROUP_SIZE);
	}
	
	public void setMinSize(int min) {
		setColumn(COLUMN_GROUP_SIZE, min);
	}
	
	public int getMaxSize() {
		return getIntColumnValue(COLUMN_GROUP_SIZE_MAX);
	}
	
	public void setMaxSize(int max) {
		setColumn(COLUMN_GROUP_SIZE_MAX, max);
	}
	
	public void setPrice(float price) {
		setColumn(COLUMN_PRICE, price);
	}
	
	public float getPrice() {
		return getFloatColumnValue(COLUMN_PRICE);
	}
	
	public void setCurrency(String currency) {
		setColumn(COLUMN_CURRENCY, currency);
	}
	
	public String getCurrency() {
		return getStringColumnValue(COLUMN_CURRENCY);
	}
	
	public Collection ejbFindAll(boolean group) throws FinderException {
		Table table = new Table(this);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));
		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_GROUPS), MatchCriteria.EQUALS, group));
		
		MatchCriteria or1 = new MatchCriteria(new Column(table, COLUMN_IS_VALID), MatchCriteria.IS, MatchCriteria.NULL);
		MatchCriteria or2 = new MatchCriteria(new Column(table, COLUMN_IS_VALID), MatchCriteria.EQUALS, true);
		
		OR or = new OR(or1, or2);
		query.addCriteria(or);
		return idoFindPKsByQuery(query);
	}
}
