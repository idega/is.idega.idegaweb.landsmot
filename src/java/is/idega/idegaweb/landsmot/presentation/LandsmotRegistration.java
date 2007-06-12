package is.idega.idegaweb.landsmot.presentation;

import is.idega.idegaweb.landsmot.business.EventParticipant;
import is.idega.idegaweb.landsmot.business.LandsmotBusiness;
import is.idega.idegaweb.landsmot.business.LandsmotEventBusiness;
import is.idega.idegaweb.landsmot.data.LandsmotEvent;

import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.FinderException;

import com.idega.block.creditcard.business.CreditCardAuthorizationException;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.PostalCode;
import com.idega.data.IDOCreateException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.help.presentation.Help;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.InterfaceObject;
import com.idega.presentation.ui.SelectionBox;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.util.SelectorUtility;
import com.idega.user.business.GenderBusiness;
import com.idega.user.business.NoEmailFoundException;
import com.idega.user.business.NoPhoneFoundException;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Gender;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;

public class LandsmotRegistration extends Block {
	
	public final static String STYLENAME_HEADER = "Header";
	public final static String STYLENAME_TEXT = "Text";
	public final static String STYLENAME_INTERFACE = "Interface";
	public final static String STYLENAME_INTERFACE_BUTTON = "InterfaceButton";
	public final static String STYLENAME_CHECKBOX = "CheckBox";

	private static final String SESSION_ATTRIBUTE_RUNNER_MAP = "sa_runner_map";
	public static final String SESSION_ATTRIBUTE_PARTICIPANTS = "sa_participants";
	public static final String SESSION_ATTRIBUTE_AMOUNT = "sa_amount";
	public static final String SESSION_ATTRIBUTE_CARD_NUMBER = "sa_card_number";
	public static final String SESSION_ATTRIBUTE_PAYMENT_DATE = "sa_payment_date";
	
	protected static final String PARAMETER_ACTION = "prm_action";
	private static final String PARAMETER_FROM_ACTION = "prm_from_action";
	
	protected static final String PARAMETER_PERSONAL_ID = "prm_personal_id";
	protected static final String PARAMETER_NAME = "prm_name";
	private static final String PARAMETER_ADDRESS = "prm_address";
	private static final String PARAMETER_POSTAL_CODE = "prm_postal_code";
	private static final String PARAMETER_CITY = "prm_city";
	private static final String PARAMETER_COUNTRY = "prm_country";
	private static final String PARAMETER_GENDER = "prm_gender";
	private static final String PARAMETER_EMAIL = "prm_email";
	private static final String PARAMETER_HOME_PHONE = "prm_home_phone";
	private static final String PARAMETER_MOBILE_PHONE = "prm_mobile_phone";
	protected static final String PARAMETER_AGREE = "prm_agree";
	protected static final String PARAMETER_EVENT = "prm_event";
	private static final String PARAMETER_NAME_ON_CARD = "prm_name_on_card";
	private static final String PARAMETER_CARD_NUMBER = "prm_card_number";
	private static final String PARAMETER_EXPIRES_MONTH = "prm_expires_month";
	private static final String PARAMETER_EXPIRES_YEAR = "prm_expires_year";
	private static final String PARAMETER_CCV = "prm_ccv";
	private static final String PARAMETER_AMOUNT = "prm_amount";
	private static final String PARAMETER_CARD_HOLDER_EMAIL = "prm_card_holder_email";
	private static final String PARAMETER_REFERENCE_NUMBER = "prm_reference_number";
	
	//localized strings
	public static final String RR_NAME = "run_reg.name";
	public static final String RR_SSN = "run_reg.ssn";
	public static final String RR_GENDER = "run_reg.gender";
	public static final String RR_ADDRESS = "run_reg.address";
	public static final String RR_POSTAL = "run_reg.postal";
	public static final String RR_CITY = "run_reg.city";
	public static final String RR_COUNTRY = "run_reg.country";
	public static final String RR_TEL = "run_reg.tel";
	public static final String RR_MOBILE = "run_reg.mobile";
	public static final String RR_EMAIL = "run_reg.email";
	
	protected static final int ACTION_STEP_PERSON_LOOKUP = 1;
	protected static final int ACTION_STEP_REGISTER_FOR_EVENT = 2;
	protected static final int ACTION_STEP_CONCENT = 3;
	protected static final int ACTION_STEP_PAYMENT = 4;
	protected static final int ACTION_SAVE = 5;
	protected static final int ACTION_CANCEL = 6;
	
	private EventParticipant runner;
	protected IWResourceBundle iwrb = null;
	protected IWBundle iwb = null;
	public final static String IW_BUNDLE_IDENTIFIER = "is.idega.idegaweb.landsmot";
	
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
	
	
	protected int getStepNumber(int step) {
		switch (step) {
		case ACTION_STEP_PERSON_LOOKUP :
			return 1;
		case ACTION_STEP_CONCENT :
			return 3;
		case ACTION_STEP_REGISTER_FOR_EVENT :
			return 2;
		case ACTION_STEP_PAYMENT :
			return 4;
		case ACTION_SAVE :
			return 5;
		case ACTION_CANCEL :
			return 1;
		}

		return 0;
	}
	
	protected int getPreviousStep(int step) {
		switch (step) {
		case ACTION_STEP_PERSON_LOOKUP :
			return 1;
		case ACTION_STEP_CONCENT :
			return 2;
		case ACTION_STEP_REGISTER_FOR_EVENT :
			return 1;
		case ACTION_STEP_PAYMENT :
			return 3;
		case ACTION_SAVE :
			return 4;
		case ACTION_CANCEL :
			return 1;
		}

		return 0;
	}
	
	protected int getStepCount() {
		return 5;
	}

	public void main(IWContext iwc) throws Exception {
		
		iwb =  getBundle(iwc);
		iwrb = iwb.getResourceBundle(iwc);
		
		switch (parseAction(iwc)) {
			case ACTION_STEP_PERSON_LOOKUP:
				stepPersonLookup(iwc);
				break;
			case ACTION_STEP_REGISTER_FOR_EVENT:
				stepRegisterForEvent(iwc);
				break;
			case ACTION_STEP_CONCENT:
				stepConcent(iwc);
				break;
			case ACTION_STEP_PAYMENT:
				stepPayment(iwc);
				break;
			case ACTION_SAVE:
				save(iwc, true);
				break;
			case ACTION_CANCEL:
				cancel(iwc);
				break;
		}
	}

	protected void stepPersonLookup(IWContext iwc) throws RemoteException {
		Form form = new Form();
		form.maintainParameter(PARAMETER_PERSONAL_ID);
		form.addParameter(PARAMETER_ACTION, ACTION_STEP_REGISTER_FOR_EVENT);
		
		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		form.add(table);
		int row = 1;

		table.add(getPhasesTable(getStepNumber(ACTION_STEP_PERSON_LOOKUP), getStepCount(), "run_reg.registration", "Registration"), 1, row++);
		table.setHeight(row++, 12);

		table.add(getInformationTable(localize("run_reg.information_text_step_1", "Information text 1...")), 1, row++);
		table.setHeight(row++, 6);
		
		table.setCellpadding(1, row, 24);
		table.add(getHeader(localize("run_reg.personal_id", "Personal ID") + ":"), 1, row);
		table.add(Text.getNonBrakingSpace(), 1, row);
		
		TextInput input = (TextInput) getStyledInterface(new TextInput(PARAMETER_PERSONAL_ID));
		input.setAsIcelandicSSNumber(localize("run_reg.not_valid_personal_id", "The personal ID you've entered is not valid"));
		input.setLength(10);
		input.setMaxlength(10);
		input.setInFocusOnPageLoad(true);
		table.add(input, 1, row++);
		
		SubmitButton next = (SubmitButton) getButton(new SubmitButton(localize("next", "Next")));
		
		table.add(next, 1, row);
		table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);

		add(form);
	}


	
	private void stepRegisterForEvent(IWContext iwc) throws RemoteException {
		Form form = new Form();
		form.maintainParameter(PARAMETER_PERSONAL_ID);
		form.addParameter(PARAMETER_ACTION, "-1");
		form.addParameter(PARAMETER_FROM_ACTION, ACTION_STEP_REGISTER_FOR_EVENT);
		
		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		form.add(table);
		int row = 1;

		table.add(getPhasesTable(getStepNumber(ACTION_STEP_REGISTER_FOR_EVENT), getStepCount(), "run_reg.registration", "Registration"), 1, row++);
		table.setHeight(row++, 12);

		table.add(getInformationTable(localize("run_reg.information_text_step_2", "Information text 2...")), 1, row++);
		table.setHeight(row++, 18);
		
		SelectionBox eventSelect = new SelectionBox(PARAMETER_EVENT);
		eventSelect.setAsNotEmpty(localize("run_reg.you_must_select_at_least_one_event", "You must selecte at least one event"));
		Collection events = getEventBusiness(iwc).getAllSingleEvents();
		if (events != null) {
			eventSelect.addMenuElements(events);
		}
		Text redStar = getHeader("*");
		redStar.setFontColor("#ff0000");
		
		TextInput nameField = (TextInput) getStyledInterface(new TextInput(PARAMETER_NAME));
		nameField.setWidth(Table.HUNDRED_PERCENT);
		if (this.runner.getName() != null) {
			nameField.setContent(this.runner.getName());
		}
		nameField.setDisabled(true);
		if (this.runner.getUser() != null) {
			nameField.setContent(this.runner.getUser().getName());
		}

		DropdownMenu genderField = (DropdownMenu) getStyledInterface(new DropdownMenu(PARAMETER_GENDER));
		Collection genders = getGenderBusiness(iwc).getAllGenders();
		genderField.addMenuElement("-1", localize("run_reg.select_gender","Select gender..."));
		if(genders != null) {
			Iterator iter = genders.iterator();
			while (iter.hasNext()) {
				Gender gender = (Gender) iter.next();
				genderField.addMenuElement(gender.getPrimaryKey().toString(), localize("gender." + gender.getName(), gender.getName()));
			}
		}
		if (this.runner.getGender() != null) {
			genderField.setSelectedElement(this.runner.getGender().getPrimaryKey().toString());
		}
		
		genderField.setDisabled(true);
		if (this.runner.getUser() != null) {
			genderField.setSelectedElement(this.runner.getUser().getGenderID());
		}
		
		TextInput ssnISField = (TextInput) getStyledInterface(new TextInput(PARAMETER_PERSONAL_ID));
		ssnISField.setLength(10);
		
		ssnISField.setDisabled(true);
		if (this.runner.getUser() != null) {
			ssnISField.setContent(this.runner.getUser().getPersonalID());
		}
		
		Collection countries = getRunBusiness(iwc).getCountries();
		DropdownMenu countryField = (DropdownMenu) getStyledInterface(new DropdownMenu(PARAMETER_COUNTRY));
		countryField.addMenuElement("-1", localize("run_reg.select_country", "Select country..."));
		SelectorUtility util = new SelectorUtility();
		if (countries != null && !countries.isEmpty()) {
			countryField = (DropdownMenu) util.getSelectorFromIDOEntities(countryField, countries, "getName");
		}
		countryField.setDisabled(true);
		if (this.runner.getUser() != null) {
			Address address = getUserBusiness(iwc).getUsersMainAddress(this.runner.getUser());
			if (address != null && address.getCountry() != null) {
				countryField.setSelectedElement(address.getCountry().getPrimaryKey().toString());
			}
		}
		countryField.setWidth(Table.HUNDRED_PERCENT);

		if (this.runner.getCountry() != null) {
			countryField.setSelectedElement(this.runner.getCountry().getPrimaryKey().toString());
		}
		
		TextInput addressField = (TextInput) getStyledInterface(new TextInput(PARAMETER_ADDRESS));
		addressField.setWidth(Table.HUNDRED_PERCENT);
		if (this.runner.getAddress() != null) {
			addressField.setContent(this.runner.getAddress());
		}
		addressField.setDisabled(true);
		if (this.runner.getUser() != null) {
			Address address = getUserBusiness(iwc).getUsersMainAddress(this.runner.getUser());
			if (address != null) {
				addressField.setContent(address.getStreetAddress());
			}
		}

		TextInput emailField = (TextInput) getStyledInterface(new TextInput(PARAMETER_EMAIL));
		emailField.setAsEmail(localize("run_reg.email_err_msg", "Not a valid email address"));
		emailField.setEmptyConfirm(localize("run_reg.continue_without_email", "Are you sure you want to continue without entering an e-mail?"));
		emailField.setWidth(Table.HUNDRED_PERCENT);
		if (this.runner.getEmail() != null) {
			emailField.setContent(this.runner.getEmail());
		}
		else if (this.runner.getUser() != null) {
			try {
				Email mail = getUserBusiness(iwc).getUsersMainEmail(this.runner.getUser());
				emailField.setContent(mail.getEmailAddress());
			}
			catch (NoEmailFoundException nefe) {
				//No email registered...
			}
		}

		TextInput cityField = (TextInput) getStyledInterface(new TextInput(PARAMETER_CITY));
		cityField.setWidth(Table.HUNDRED_PERCENT);
		if (this.runner.getCity() != null) {
			cityField.setContent(this.runner.getCity());
		}

		cityField.setDisabled(true);
		if (this.runner.getUser() != null) {
			Address address = getUserBusiness(iwc).getUsersMainAddress(this.runner.getUser());
			if (address != null) {
				cityField.setContent(address.getCity());
			}
		}

		TextInput telField = (TextInput) getStyledInterface(new TextInput(PARAMETER_HOME_PHONE));
		telField.setWidth(Table.HUNDRED_PERCENT);
		if (this.runner.getHomePhone() != null) {
			telField.setContent(this.runner.getHomePhone());
		}
		else if (this.runner.getUser() != null) {
			try {
				Phone phone = getUserBusiness(iwc).getUsersHomePhone(this.runner.getUser());
				telField.setContent(phone.getNumber());
			}
			catch (NoPhoneFoundException nefe) {
				//No phone registered...
			}
		}
		
		TextInput postalField = (TextInput) getStyledInterface(new TextInput(PARAMETER_POSTAL_CODE));
		postalField.setMaxlength(10);
		postalField.setLength(10);
		if (this.runner.getPostalCode() != null) {
			postalField.setContent(this.runner.getPostalCode());
		}

		postalField.setDisabled(true);
		if (this.runner.getUser() != null) {
			Address address = getUserBusiness(iwc).getUsersMainAddress(this.runner.getUser());
			if (address != null) {
				PostalCode postal = address.getPostalCode();
				if (postal != null) {
					postalField.setContent(postal.getPostalCode());
				}
			}
		}

		TextInput mobileField = (TextInput) getStyleObject(new TextInput(PARAMETER_MOBILE_PHONE), STYLENAME_INTERFACE);
		mobileField.setWidth(Table.HUNDRED_PERCENT);
		if (this.runner.getMobilePhone() != null) {
			mobileField.setContent(this.runner.getMobilePhone());
		}
		else if (this.runner.getUser() != null) {
			try {
				Phone phone = getUserBusiness(iwc).getUsersMobilePhone(this.runner.getUser());
				mobileField.setContent(phone.getNumber());
			}
			catch (NoPhoneFoundException nefe) {
				//No phone registered...
			}
		}

		Table choiceTable = new Table();
		choiceTable.setColumns(3);
		choiceTable.setCellpadding(2);
		choiceTable.setCellspacing(0);
		choiceTable.setWidth(1, "50%");
		choiceTable.setWidth(2, 12);
		choiceTable.setWidth(3, "50%");
		choiceTable.setWidth(Table.HUNDRED_PERCENT);
		table.add(choiceTable, 1, row++);
		int iRow = 1;
		choiceTable.add(getHeader(localize("run_reg.event", "Event")), 1, iRow);
		choiceTable.add(redStar, 1, iRow++);
		choiceTable.mergeCells(1, iRow, choiceTable.getColumns(), iRow);
		choiceTable.add(eventSelect, 1, iRow);
		choiceTable.setHeight(iRow++, 12);
		choiceTable.add(getHeader(localize(RR_NAME, "Name")), 1, iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable.add(getHeader(localize(RR_SSN, "SSN")), 3, iRow);
		choiceTable.add(redStar, 3, iRow++);
		choiceTable.add(nameField, 1, iRow);
		choiceTable.add(ssnISField, 3, iRow);
		choiceTable.setHeight(iRow++, 3);
		choiceTable.add(getHeader(localize(RR_ADDRESS, "Address")), 1, iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable.add(getHeader(localize(RR_GENDER, "Gender")), 3, iRow);
		choiceTable.add(redStar, 3, iRow++);
		choiceTable.add(addressField, 1, iRow);
		choiceTable.add(genderField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);
		choiceTable.add(getHeader(localize(RR_CITY, "City")), 1, iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable.add(getHeader(localize(RR_EMAIL, "Email")), 3, iRow++);
		choiceTable.add(cityField, 1, iRow);
		choiceTable.add(emailField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);
		choiceTable.add(getHeader(localize(RR_POSTAL, "Postal Code")), 1, iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable.add(getHeader(localize(RR_TEL, "Telephone")), 3, iRow++);
		choiceTable.add(postalField, 1, iRow);
		choiceTable.add(telField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);
		choiceTable.add(getHeader(localize(RR_COUNTRY, "Country")), 1, iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable.add(getHeader(localize(RR_MOBILE, "Mobile Phone")), 3, iRow++);
		choiceTable.add(countryField, 1, iRow);
		choiceTable.add(mobileField, 3, iRow++);

		SubmitButton next = (SubmitButton) getButton(new SubmitButton(localize("next", "Next")));
		next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_STEP_CONCENT));
		
		table.setHeight(row++, 18);
		table.add(next, 1, row);
		table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);
		add(form);
	}

	private void stepConcent(IWContext iwc) {
		Form form = new Form();
		form.maintainParameter(PARAMETER_PERSONAL_ID);
		form.addParameter(PARAMETER_ACTION, "-1");
		form.addParameter(PARAMETER_FROM_ACTION, ACTION_STEP_CONCENT);
		
		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		form.add(table);
		int row = 1;

		table.add(getPhasesTable(getStepNumber(ACTION_STEP_CONCENT), getStepCount(), "run_reg.consent", "Consent"), 1, row++);
		table.setHeight(row++, 18);

		SubmitButton next = (SubmitButton) getButton(new SubmitButton(localize("next", "Next")));
		next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_STEP_PAYMENT));
		if (!this.runner.isAgree()) {
			next.setDisabled(true);
		}

		CheckBox agree = getCheckBox(PARAMETER_AGREE, Boolean.TRUE.toString());
		agree.setToEnableWhenChecked(next);
		agree.setToDisableWhenUnchecked(next);
		agree.setChecked(this.runner.isAgree());
		
		table.add(getText(localize("run_reg.information_text_step_3", "Information text 3...")), 1, row++);
		table.setHeight(row++, 6);
		table.add(agree, 1, row);
		table.add(Text.getNonBrakingSpace(), 1, row);
		table.add(getHeader(localize("run_reg.agree_terms", "Yes, I agree")), 1, row++);
		
		SubmitButton previous = (SubmitButton) getButton(new SubmitButton(localize("previous", "Previous")));
		previous.setValueOnClick(PARAMETER_ACTION, String.valueOf(getPreviousStep(ACTION_STEP_CONCENT)));

		table.setHeight(row++, 18);
		table.add(previous, 1, row);
		table.add(Text.getNonBrakingSpace(), 1, row);
		table.add(next, 1, row);
		table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);

		add(form);
	}

	private void stepPayment(IWContext iwc) throws RemoteException {
		Form form = new Form();
		form.maintainParameter(PARAMETER_PERSONAL_ID);
		form.addParameter(PARAMETER_ACTION, "-1");
		
		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		form.add(table);
		int row = 1;

		DecimalFormatSymbols symbs = new DecimalFormatSymbols(iwc.getLocale());
		NumberFormat nf = new DecimalFormat("#,###", symbs);

		table.add(getPhasesTable(getStepNumber(ACTION_STEP_PAYMENT), getStepCount(), "run_reg.payment_info", "Payment info"), 1, row++);
		table.setHeight(row++, 12);

		table.add(getInformationTable(localize("run_reg.information_text_step_4", "Information text 4...")), 1, row++);
		table.setHeight(row++, 18);
		
		Map runners = (Map) iwc.getSessionAttribute(SESSION_ATTRIBUTE_RUNNER_MAP);
		Table runnerTable = new Table();
		runnerTable.setWidth(Table.HUNDRED_PERCENT);
		runnerTable.setCellspacing(0);
		runnerTable.add(getHeader(localize("run_reg.name", "Name")), 1, 1);
		runnerTable.add(getHeader(localize("run_reg.event", "Event")), 2, 1);
		runnerTable.add(getHeader(localize("run_reg.count", "Count")), 3, 1);
		runnerTable.add(getHeader(localize("run_reg.unit_price", "Unit price")), 4, 1);
		runnerTable.add(getHeader(localize("run_reg.price", "Price")), 5, 1);
		table.add(runnerTable, 1, row++);
		table.setHeight(row++, 18);
		int runRow = 2;
		
		float totalAmount = 0;
		int multiplier = 1;
		Iterator iter = runners.values().iterator();
		while (iter.hasNext()) {
			EventParticipant runner = (EventParticipant) iter.next();
			Collection participants = runner.getParticipants();
			if (participants != null && !participants.isEmpty()) {
				multiplier = participants.size();
			}

			if (runner.getUser() != null) {
				runnerTable.add(getText(runner.getUser().getName()), 1, runRow);
			}
			else {
				runnerTable.add(getText(runner.getName()), 1, runRow);
			}
			
			Collection events = runner.getEvents();
			Iterator eter = events.iterator();
			while (eter.hasNext()) {
				LandsmotEvent event = (LandsmotEvent) eter.next();
				runnerTable.add(getText(event.getName()), 2, runRow);
				float preprice = event.getPrice();
				float price = preprice * multiplier;
				totalAmount += price;
				runnerTable.add(getText(Integer.toString(multiplier)), 3, runRow);
				runnerTable.add(getText(nf.format(preprice)+" ISK"), 4, runRow);
				runnerTable.add(getText(nf.format(price)+" ISK"), 5, runRow);
//				if (multiplier > 1) {
//					runnerTable.add(getText(" ("+multiplier+"*"+nf.format(preprice)+" ISK")+")", 3, runRow);
////					if (!headerAdded) {
////						runnerTable.add(getText(" (per person)"), 3, 1);
////						headerAdded = true;
////					}
//				}
				runRow++;
			}
			addRunner(iwc, runner.getPersonalID(), runner);
		}
		
		if (totalAmount == 0) {
			save(iwc, false);
			return;
		}
		
		runnerTable.setHeight(runRow++, 12);
		runnerTable.add(getHeader(localize("run_reg.total_amount", "Total amount")), 1, runRow);
		runnerTable.add(getHeader(nf.format(totalAmount)+" ISK"), 5, runRow);
		runnerTable.setColumnAlignment(4, Table.HORIZONTAL_ALIGN_RIGHT);
		runnerTable.setColumnAlignment(5, Table.HORIZONTAL_ALIGN_RIGHT);

		Table creditCardTable = new Table();
		creditCardTable.setWidth(Table.HUNDRED_PERCENT);
		creditCardTable.setWidth(1, "50%");
		creditCardTable.setWidth(3, "50%");
		creditCardTable.setWidth(2, 12);
		creditCardTable.setColumns(3);
		creditCardTable.setCellspacing(0);
		creditCardTable.setCellpadding(0);
		table.setTopCellBorder(1, row, 1, "#D7D7D7", "solid");
		table.setCellpaddingBottom(1, row++, 6);
		table.add(creditCardTable, 1, row++);
		int creditRow = 1;
		
		creditCardTable.add(getHeader(localize("run_reg.credit_card_information", "Credit card information")), 1, creditRow);
		creditCardTable.add(getRunBusiness(iwc).getAvailableCardTypes(iwrb), 3, creditRow);
		creditCardTable.add(Text.getNonBrakingSpace(), 3, creditRow);
		Collection images = getRunBusiness(iwc).getCreditCardImages();
		if (images != null) {
			Iterator iterator = images.iterator();
			while (iterator.hasNext()) {
				Image image = (Image) iterator.next();
				creditCardTable.add(image, 3, creditRow);
				if (iterator.hasNext()) {
					creditCardTable.add(Text.getNonBrakingSpace(), 3, creditRow);
				}
			}
		}
//		creditCardTable.setAlignment(3, creditRow++, Table.HORIZONTAL_ALIGN_RIGHT);
		creditCardTable.setHeight(creditRow++, 12);

		TextInput nameField = (TextInput) getStyledInterface(new TextInput(PARAMETER_NAME_ON_CARD));
		nameField.setAsNotEmpty(localize("run_reg.must_supply_card_holder_name", "You must supply card holder name"));
		nameField.keepStatusOnAction(true);
		nameField.setAutoComplete(false);
		
		TextInput ccv = (TextInput) getStyledInterface(new TextInput(PARAMETER_CCV));
		ccv.setLength(3);
		ccv.setMaxlength(3);
//		ccv.setMininumLength(3, localize("run_reg.not_valid_ccv", "Not a valid CCV number"));
		ccv.setAsIntegers(localize("run_reg.not_valid_ccv", "Not a valid CCV number"));
		ccv.setAsNotEmpty(localize("run_reg.must_supply_ccv", "You must enter the CCV number"));
		ccv.keepStatusOnAction(true);
		ccv.setAutoComplete(false);

		IWTimestamp stamp = new IWTimestamp();
		DropdownMenu month = (DropdownMenu) getStyledInterface(new DropdownMenu(PARAMETER_EXPIRES_MONTH));
		for (int a = 1; a <= 12; a++) {
			month.addMenuElement(a < 10 ? "0" + a : String.valueOf(a), a < 10 ? "0" + a : String.valueOf(a));
		}
		month.keepStatusOnAction(true);
		DropdownMenu year = (DropdownMenu) getStyledInterface(new DropdownMenu(PARAMETER_EXPIRES_YEAR));
		for (int a = stamp.getYear(); a <= stamp.getYear() + 8; a++) {
			year.addMenuElement(String.valueOf(a).substring(2), String.valueOf(a));
		}
		year.keepStatusOnAction(true);
		
		creditCardTable.add(getHeader(localize("run_reg.card_holder", "Card holder")), 1, creditRow);
		creditCardTable.add(getHeader(localize("run_reg.card_number", "Card number")), 3, creditRow++);
		creditCardTable.add(nameField, 1, creditRow);
		for (int a = 1; a <= 4; a++) {
			TextInput cardNumber = (TextInput) getStyledInterface(new TextInput(PARAMETER_CARD_NUMBER + "_" + a));
			if (a < 4) {
				cardNumber.setLength(4);
				cardNumber.setMaxlength(4);
			}
			else {
				cardNumber.setLength(4);
				cardNumber.setMaxlength(7);
			}
			cardNumber.setMininumLength(4, localize("run_reg.not_valid_card_number", "Not a valid card number"));
			cardNumber.setAsIntegers(localize("run_reg.not_valid_card_number", "Not a valid card number"));
			cardNumber.setAsNotEmpty(localize("run_reg.must_supply_card_number", "You must enter the credit card number"));
			cardNumber.keepStatusOnAction(true);
			cardNumber.setAutoComplete(false);

			creditCardTable.add(cardNumber, 3, creditRow);
			if (a != 4) {
				creditCardTable.add(Text.getNonBrakingSpace(), 3, creditRow);
			}
		}
		creditRow++;
		creditCardTable.setHeight(creditRow++, 3);

		creditCardTable.add(getHeader(localize("run_reg.card_expires", "Card expires")), 1, creditRow);
		creditCardTable.add(getHeader(localize("run_reg.ccv_number", "CCV number")), 3, creditRow++);
		creditCardTable.add(month, 1, creditRow);
		creditCardTable.add(getText("/"), 1, creditRow);
		creditCardTable.add(year, 1, creditRow);
		creditCardTable.add(ccv, 3, creditRow++);
		
		TextInput emailField = (TextInput) getStyledInterface(new TextInput(PARAMETER_CARD_HOLDER_EMAIL));
		emailField.setAsEmail(localize("run_reg.email_err_msg", "Not a valid email address"));
//		emailField.setWidth(Table.HUNDRED_PERCENT);
		emailField.keepStatusOnAction(true);
		emailField.setStyleClass("emailField");
		
		creditCardTable.setHeight(creditRow++, 3);
		creditCardTable.mergeCells(3, creditRow, 3, creditRow+1);
		creditCardTable.add(getText(localize("run_reg.ccv_explanation_text","A CCV number is a three digit number located on the back of all major credit cards.")), 3, creditRow);
		creditCardTable.add(getHeader(localize("run_reg.card_holder_email", "Cardholder email")), 1, creditRow++);
		creditCardTable.add(emailField, 1, creditRow++);
		creditCardTable.add(new HiddenInput(PARAMETER_AMOUNT, String.valueOf(totalAmount)));
		creditCardTable.setHeight(creditRow++, 18);
		creditCardTable.mergeCells(1, creditRow, creditCardTable.getColumns(), creditRow);
		creditCardTable.add(getText(localize("run_reg.read_conditions", "Please read before you finish your payment") + ": "), 1, creditRow);
		
		Help help = new Help();
		help.setHelpTextBundle(IW_BUNDLE_IDENTIFIER);
		help.setHelpTextKey("terms_and_conditions");
		help.setShowAsText(true);
		help.setLinkText(localize("run_reg.terms_and_conditions", "Terms and conditions"));
		creditCardTable.add(help, 1, creditRow++);

		SubmitButton next = (SubmitButton) getButton(new SubmitButton(localize("run_reg.pay", "Pay")));
		next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_SAVE));
		next.setDisabled(true);

		CheckBox agree = getCheckBox(PARAMETER_AGREE + "_terms", Boolean.TRUE.toString());
		agree.setToEnableWhenChecked(next);
		agree.setToDisableWhenUnchecked(next);
		
		creditCardTable.setHeight(creditRow++, 12);
		creditCardTable.mergeCells(1, creditRow, creditCardTable.getColumns(), creditRow);
		creditCardTable.add(agree, 1, creditRow);
		creditCardTable.add(Text.getNonBrakingSpace(), 1, creditRow);
		creditCardTable.add(getHeader(localize("run_reg.agree_terms_and_conditions", "I agree to the terms and conditions")), 1, creditRow++);

		SubmitButton previous = (SubmitButton) getButton(new SubmitButton(localize("previous", "Previous")));
		previous.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_STEP_CONCENT));
		table.setHeight(row++, 18);
		table.add(previous, 1, row);
		table.add(Text.getNonBrakingSpace(), 1, row);
		table.add(next, 1, row);
		form.setToDisableOnSubmit(next, true);
		table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);

		add(form);
	}

	private void save(IWContext iwc, boolean doPayment) throws RemoteException {
		try {
			Collection runners = ((Map) iwc.getSessionAttribute(SESSION_ATTRIBUTE_RUNNER_MAP)).values();

			String nameOnCard = null;
			String cardNumber = null;
			String hiddenCardNumber = "XXXX-XXXX-XXXX-XXXX";
			String email = ((EventParticipant) runners.iterator().next()).getEmail();
			String expiresMonth = null;
			String expiresYear = null;
			String ccVerifyNumber = null;
			String referenceNumber = null;
			double amount = 0;
			IWTimestamp paymentStamp = new IWTimestamp();

			boolean disablePaymentProcess = "true".equalsIgnoreCase(iwc.getApplicationSettings().getProperty("LANDSMOT_DISABLE_PAYMENT_AUTH","false"));
			if (doPayment && disablePaymentProcess) {
				doPayment = false;
			}

			if (doPayment) {
				nameOnCard = iwc.getParameter(PARAMETER_NAME_ON_CARD);
				cardNumber = "";
				for (int i = 1; i <= 4; i++) {
					cardNumber += iwc.getParameter(PARAMETER_CARD_NUMBER + "_" + i);
				}
				hiddenCardNumber = "XXXX-XXXX-XXXX-" + iwc.getParameter(PARAMETER_CARD_NUMBER + "_" + 4);
				expiresMonth = iwc.getParameter(PARAMETER_EXPIRES_MONTH);
				expiresYear = iwc.getParameter(PARAMETER_EXPIRES_YEAR);
				ccVerifyNumber = iwc.getParameter(PARAMETER_CCV);
				email = iwc.getParameter(PARAMETER_CARD_HOLDER_EMAIL);
				amount = Double.parseDouble(iwc.getParameter(PARAMETER_AMOUNT));
				referenceNumber = iwc.getParameter(PARAMETER_REFERENCE_NUMBER);
				if (referenceNumber == null) {
					referenceNumber = IWTimestamp.RightNow().toSQLDateString();
				}
			}
			
			String properties = null;
			if (doPayment) {
				properties = getRunBusiness(iwc).authorizePayment(nameOnCard, cardNumber, expiresMonth, expiresYear, ccVerifyNumber, amount, "ISK", referenceNumber);
			}
			Collection participants = getRunBusiness(iwc).saveParticipants(runners, email, hiddenCardNumber, amount, paymentStamp, iwc.getCurrentLocale());
			if (doPayment) {
				String authID = getRunBusiness(iwc).finishPayment(properties);
				System.out.println("[LandsmotRegistration] auth ID : "+authID);
				// Set the authIDs on the participants...
				Iterator iter = participants.iterator();
				while (iter.hasNext()) {
					Object obj = iter.next();
					if (obj instanceof is.idega.idegaweb.landsmot.data.LandsmotRegistration) {
						is.idega.idegaweb.landsmot.data.LandsmotRegistration reg = (is.idega.idegaweb.landsmot.data.LandsmotRegistration) obj;
						reg.setCreditCardAuthorizationCode(authID);
						reg.store();
					} else if (obj instanceof is.idega.idegaweb.landsmot.data.LandsmotGroupRegistration) {
						is.idega.idegaweb.landsmot.data.LandsmotGroupRegistration reg = (is.idega.idegaweb.landsmot.data.LandsmotGroupRegistration) obj;
						reg.setCreditCardAuthorizationCode(authID);
						reg.store();
					}
				}

			}
			iwc.removeSessionAttribute(SESSION_ATTRIBUTE_RUNNER_MAP);
			
			showReceipt(iwc, participants, amount, hiddenCardNumber, paymentStamp, doPayment);
		}
		catch (IDOCreateException ice) {
			getParentPage().setAlertOnLoad(localize("run_reg.save_failed", "There was an error when trying to finish registration.  Please contact the Landsmot's office."));
			ice.printStackTrace();
			stepPayment(iwc);
		}
		catch (CreditCardAuthorizationException ccae) {
			IWResourceBundle creditCardBundle = iwc.getIWMainApplication().getBundle("com.idega.block.creditcard").getResourceBundle(iwc.getCurrentLocale());
			getParentPage().setAlertOnLoad(ccae.getLocalizedMessage(creditCardBundle));
			ccae.printStackTrace();
			stepPayment(iwc);
		}
	}
	
	private void showReceipt(IWContext iwc, Collection runners, double amount, String cardNumber, IWTimestamp paymentStamp, boolean doPayment) {
		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		int row = 1;
		iwc.setSessionAttribute(SESSION_ATTRIBUTE_PARTICIPANTS, runners);
		iwc.setSessionAttribute(SESSION_ATTRIBUTE_AMOUNT, new Double(amount));
		iwc.setSessionAttribute(SESSION_ATTRIBUTE_CARD_NUMBER, cardNumber);
		iwc.setSessionAttribute(SESSION_ATTRIBUTE_PAYMENT_DATE, paymentStamp);

		table.add(getPhasesTable(getStepNumber(ACTION_SAVE), getStepCount(), "run_reg.receipt", "Receipt"), 1, row++);
		table.setHeight(row++, 18);
		
		table.add(getHeader(localize("run_reg.hello_participant", "Hello participant(s)")), 1, row++);
		table.setHeight(row++, 16);

		table.add(getText(localize("run_reg.payment_received", "We have received payment for the following:")), 1, row++);
		table.setHeight(row++, 8);

		Table runnerTable = new Table(5, runners.size() + 3);
		runnerTable.setWidth(Table.HUNDRED_PERCENT);
		runnerTable.add(getHeader(localize("run_reg.name", "Name")), 1, 1);
		runnerTable.add(getHeader(localize("run_reg.event", "Event")), 2, 1);
		table.add(runnerTable, 1, row++);
		int runRow = 2;
		Iterator iter = runners.iterator();
		while (iter.hasNext()) {
			Object obj = iter.next();
			if (obj instanceof is.idega.idegaweb.landsmot.data.LandsmotRegistration) {
				is.idega.idegaweb.landsmot.data.LandsmotRegistration reg = (is.idega.idegaweb.landsmot.data.LandsmotRegistration) obj;
	
				runnerTable.add(getText(reg.getUser().getName()), 1, runRow);
				runnerTable.add(getText(reg.getEvent().getName()), 2, runRow++);
			} else if (obj instanceof is.idega.idegaweb.landsmot.data.LandsmotGroupRegistration) {
				is.idega.idegaweb.landsmot.data.LandsmotGroupRegistration reg = (is.idega.idegaweb.landsmot.data.LandsmotGroupRegistration) obj;
				
				runnerTable.add(getText(reg.getName()), 1, runRow);
				runnerTable.add(getText(reg.getEvent().getName()), 2, runRow++);
			}
		}
		
		DecimalFormatSymbols symbs = new DecimalFormatSymbols(iwc.getLocale());
		NumberFormat nf = new DecimalFormat("#,###", symbs);

		
		if (doPayment) {
			Table creditCardTable = new Table(2, 3);
			creditCardTable.add(getHeader(localize("run_reg.payment_received_timestamp", "Payment received") + ":"), 1, 1);
			creditCardTable.add(getText(paymentStamp.getLocaleDateAndTime(iwc.getCurrentLocale(), IWTimestamp.SHORT, IWTimestamp.SHORT)), 2, 1);
			creditCardTable.add(getHeader(localize("run_reg.card_number", "Card number") + ":"), 1, 2);
			creditCardTable.add(getText(cardNumber), 2, 2);
			creditCardTable.add(getHeader(localize("run_reg.amount", "Amount") + ":"), 1, 3);
			creditCardTable.add(getText(nf.format(amount)+ " ISK"), 2, 3);
			table.setHeight(row++, 16);
			table.add(creditCardTable, 1, row++);
		}
		
		table.setHeight(row++, 16);
		table.add(getHeader(localize("run_reg.delivery_of_race_material_headline", "Race material and T-shirt/sweatshirt")), 1, row++);
		table.add(getText(localize("run_reg.delivery_of_race_material_body", "Participants can collect their race number and the t-shirt/sweatshirt here.")), 1, row++);

		table.setHeight(row++, 16);
		table.add(getHeader(localize("run_reg.receipt_info_headline", "Receipt - Please Print It Out")), 1, row++);
		table.add(getText(localize("run_reg.receipt_info_headline_body", "This document is your receipt, please print this out and bring it with you when you get your race number and T-shirt/sweatshirt.")), 1, row++);

		table.setHeight(row++, 16);
		table.add(getText(localize("run_reg.best_regards", "Best regards,")), 1, row++);
		table.add(getText(localize("run_reg.landsmot", "Landsmot UMFI")), 1, row++);
		table.add(getText("skraning.felix.is"), 1, row++);
		
		table.setHeight(row++, 16);
		
		Link print = new Link(localize("print", "Print"));
		print.setPublicWindowToOpen(RegistrationReceivedPrintable.class);
		table.add(print, 1, row);
		
		add(table);
	}
	
	private void cancel(IWContext iwc) {
		iwc.removeSessionAttribute(SESSION_ATTRIBUTE_RUNNER_MAP);
	}

	protected EventParticipant collectValues(IWContext iwc) throws FinderException, RemoteException {
		String personalID = iwc.getParameter(PARAMETER_PERSONAL_ID);
		if (personalID != null && personalID.length() > 0) {
			EventParticipant runner = getRunner(iwc, personalID);
			if (runner == null) {
				runner = new EventParticipant();
				runner.setPersonalID(personalID);
				User user = getUserBusiness(iwc).getUser(personalID);
				runner.setUser(user);
			}

			if (iwc.isParameterSet(PARAMETER_NAME)) {
				runner.setName(iwc.getParameter(PARAMETER_NAME));
			}
			collectContactValues(iwc, runner);

			if (iwc.isParameterSet(PARAMETER_AGREE)) {
				runner.setAgree(true);
			}
			if (iwc.isParameterSet(PARAMETER_EVENT)) {
				String[] events = iwc.getParameterValues(PARAMETER_EVENT);
				for (int i = 0; i < events.length; i++) {
					LandsmotEvent ev = getEventBusiness(iwc).getEvent(new Integer(events[i]));
					runner.addEvent(ev);
				}
			}
			
			runner.setGroup(false);

			addRunner(iwc, personalID, runner);
			return runner;
		}
		return new EventParticipant();
	}

	protected void collectContactValues(IWContext iwc, EventParticipant runner)
			throws FinderException, RemoteException {
		if (iwc.isParameterSet(PARAMETER_ADDRESS)) {
			runner.setAddress(iwc.getParameter(PARAMETER_ADDRESS));
		}
		if (iwc.isParameterSet(PARAMETER_POSTAL_CODE)) {
			runner.setPostalCode(iwc.getParameter(PARAMETER_POSTAL_CODE));
		}
		if (iwc.isParameterSet(PARAMETER_CITY)) {
			runner.setCity(iwc.getParameter(PARAMETER_CITY));
		}
		if (iwc.isParameterSet(PARAMETER_COUNTRY)) {
			runner.setCountry(getUserBusiness(iwc).getAddressBusiness().getCountryHome().findByPrimaryKey(new Integer(iwc.getParameter(PARAMETER_COUNTRY))));
		}
		if (iwc.isParameterSet(PARAMETER_GENDER)) {
			runner.setGender(getGenderBusiness(iwc).getGender(new Integer(iwc.getParameter(PARAMETER_GENDER))));
		}
		if (iwc.isParameterSet(PARAMETER_EMAIL)) {
			runner.setEmail(iwc.getParameter(PARAMETER_EMAIL));
		}
		if (iwc.isParameterSet(PARAMETER_HOME_PHONE)) {
			runner.setHomePhone(iwc.getParameter(PARAMETER_HOME_PHONE));
		}
		if (iwc.isParameterSet(PARAMETER_MOBILE_PHONE)) {
			runner.setMobilePhone(iwc.getParameter(PARAMETER_MOBILE_PHONE));
		}
	}
	
	private int parseAction(IWContext iwc) throws RemoteException {
		int action = ACTION_STEP_PERSON_LOOKUP;
		
		if (iwc.isParameterSet(PARAMETER_ACTION)) {
			action = Integer.parseInt(iwc.getParameter(PARAMETER_ACTION));
		}

		try {
			this.runner = collectValues(iwc);
		}
		catch (FinderException fe) {
//			getParentPage().setAlertOnLoad(localize("run_reg.user_not_found_for_personal_id", "No user found with personal ID."));
			action = ACTION_STEP_PERSON_LOOKUP;
		}
		return action;
	}
	
	protected EventParticipant getRunner(IWContext iwc, String key) {
		Map runnerMap = (Map) iwc.getSessionAttribute(SESSION_ATTRIBUTE_RUNNER_MAP);
		if (runnerMap != null) {
			return (EventParticipant) runnerMap.get(key);
		}
		return null;
	}
	
	protected void addRunner(IWContext iwc, String key, EventParticipant runner) {
		Map runnerMap = (Map) iwc.getSessionAttribute(SESSION_ATTRIBUTE_RUNNER_MAP);
		if (runnerMap == null) {
			runnerMap = new HashMap();
		}
		runnerMap.put(key, runner);
		iwc.setSessionAttribute(SESSION_ATTRIBUTE_RUNNER_MAP, runnerMap);
	}
	
//	private void removeRunner(IWContext iwc, String key) {
//		Map runnerMap = (Map) iwc.getSessionAttribute(SESSION_ATTRIBUTE_RUNNER_MAP);
//		if (runnerMap == null) {
//			runnerMap = new HashMap();
//		}
//		runnerMap.remove(key);
//		iwc.setSessionAttribute(SESSION_ATTRIBUTE_RUNNER_MAP, runnerMap);
//	}

	protected Table getPhasesTable(int phase, int totalPhases, String key, String defaultText) {
		Table table = new Table(2, 1);
		table.setCellpadding(3);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		table.setAlignment(2, 1, Table.HORIZONTAL_ALIGN_RIGHT);
		table.setBottomCellBorder(1, 1, 1, "#D7D7D7", "solid");
		table.setBottomCellBorder(2, 1, 1, "#D7D7D7", "solid");
		
		table.add(getHeader(localize(key, defaultText)), 1, 1);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(localize("step", "Step")).append(" ").append(phase).append(" ").append(localize("of", "of")).append(" ").append(totalPhases);
		table.add(getHeader(buffer.toString()), 2, 1);
		
		return table;
	}
	
	protected Table getInformationTable(String information) {
		Table table = new Table(1, 1);
		table.setCellpadding(3);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		table.setBottomCellBorder(1, 1, 1, "#D7D7D7", "solid");
		table.setCellpaddingBottom(1, 1, 6);
		
		table.add(getText(information), 1, 1);
		
		return table;
	}
	
	public Text getHeader(String s) {
		return getStyleText(s, STYLENAME_HEADER);
	}
	
	public Text getText(String text) {
		return getStyleText(text, STYLENAME_TEXT);
	}
	
	protected GenericButton getButton(GenericButton button) {
		button.setHeight("20");
		return (GenericButton) setStyle(button,STYLENAME_INTERFACE_BUTTON);
	}
	
	protected CheckBox getCheckBox(String name, String value) {
		return (CheckBox) setStyle(new CheckBox(name,value),STYLENAME_CHECKBOX);
	}
	
	public InterfaceObject getStyledInterface(InterfaceObject obj) {
		return (InterfaceObject) setStyle(obj, STYLENAME_INTERFACE);
	}

	protected IWBundle getBundle() {
		return this.iwb;
	}

	protected void setBundle(IWBundle bundle) {
		this.iwb = bundle;
	}

	protected IWResourceBundle getResourceBundle() {
		return this.iwrb;
	}

	protected void setResourceBundle(IWResourceBundle resourceBundle) {
		this.iwrb = resourceBundle;
	}
	
	public String localize(String textKey, String defaultText) {
		if (iwrb == null) {
			return defaultText;
		}
		return this.iwrb.getLocalizedString(textKey, defaultText);
	}
	
	protected UserBusiness getUserBusiness(IWApplicationContext iwac) {
		try {
			return (UserBusiness) IBOLookup.getServiceInstance(iwac, UserBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	protected GenderBusiness getGenderBusiness(IWApplicationContext iwac) {
		try {
			return (GenderBusiness) IBOLookup.getServiceInstance(iwac, GenderBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}
	
	protected LandsmotBusiness getRunBusiness(IWApplicationContext iwac) {
		try {
			return (LandsmotBusiness) IBOLookup.getServiceInstance(iwac, LandsmotBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}
	
	protected LandsmotEventBusiness getEventBusiness(IWApplicationContext iwac) {
		try {
			return (LandsmotEventBusiness) IBOLookup.getServiceInstance(iwac, LandsmotEventBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}
}