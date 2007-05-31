package is.idega.idegaweb.landsmot.presentation;

import is.idega.idegaweb.landsmot.business.LandsmotBusiness;
import is.idega.idegaweb.landsmot.business.LandsmotEventBusiness;
import is.idega.idegaweb.landsmot.business.Runner;
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
import com.idega.presentation.ui.DateInput;
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
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;

public class LandsmotRegistration extends Block {
	
	private static final long serialVersionUID = 3105168986587179339L;
	
	public final static String STYLENAME_FORM_ELEMENT = "FormElement";
	public final static String STYLENAME_HEADER = "Header";
	public final static String STYLENAME_TEXT = "Text";
	public final static String STYLENAME_SMALL_TEXT = "SmallText";
	public final static String STYLENAME_LINK = "Link";
	public final static String STYLENAME_INTERFACE = "Interface";
	public final static String STYLENAME_INTERFACE_BUTTON = "InterfaceButton";
	public final static String STYLENAME_CHECKBOX = "CheckBox";

	private static final String SESSION_ATTRIBUTE_RUNNER_MAP = "sa_runner_map";
	public static final String SESSION_ATTRIBUTE_PARTICIPANTS = "sa_participants";
	public static final String SESSION_ATTRIBUTE_AMOUNT = "sa_amount";
	public static final String SESSION_ATTRIBUTE_CARD_NUMBER = "sa_card_number";
	public static final String SESSION_ATTRIBUTE_PAYMENT_DATE = "sa_payment_date";
	
	private static final String PARAMETER_ACTION = "prm_action";
	private static final String PARAMETER_FROM_ACTION = "prm_from_action";
	
	private static final String PARAMETER_PERSONAL_ID = "prm_personal_id";
	private static final String PARAMETER_NAME = "prm_name";
	private static final String PARAMETER_ADDRESS = "prm_address";
	private static final String PARAMETER_POSTAL_CODE = "prm_postal_code";
	private static final String PARAMETER_CITY = "prm_city";
	private static final String PARAMETER_COUNTRY = "prm_country";
	private static final String PARAMETER_GENDER = "prm_gender";
	private static final String PARAMETER_NATIONALITY = "prm_nationality";
	private static final String PARAMETER_EMAIL = "prm_email";
	private static final String PARAMETER_HOME_PHONE = "prm_home_phone";
	private static final String PARAMETER_MOBILE_PHONE = "prm_mobile_phone";
	private static final String PARAMETER_AGREE = "prm_agree";
	private static final String PARAMETER_EVENT = "prm_event";
	private static final String PARAMETER_NAME_ON_CARD = "prm_name_on_card";
	private static final String PARAMETER_CARD_NUMBER = "prm_card_number";
	private static final String PARAMETER_EXPIRES_MONTH = "prm_expires_month";
	private static final String PARAMETER_EXPIRES_YEAR = "prm_expires_year";
	private static final String PARAMETER_CCV = "prm_ccv";
	private static final String PARAMETER_AMOUNT = "prm_amount";
	private static final String PARAMETER_CARD_HOLDER_EMAIL = "prm_card_holder_email";
	private static final String PARAMETER_REFERENCE_NUMBER = "prm_reference_number";
	
	private static final int ACTION_STEP_ONE = 1;
	private static final int ACTION_STEP_TWO = 2;
	private static final int ACTION_STEP_FOUR = 4;
	private static final int ACTION_STEP_FIVE = 5;
	private static final int ACTION_STEP_SIX = 6;
	private static final int ACTION_SAVE = 7;
	private static final int ACTION_CANCEL = 8;
	
	private Runner runner;
	private IWResourceBundle iwrb = null;
	private IWBundle iwb = null;
	public final static String IW_BUNDLE_IDENTIFIER = "is.idega.idegaweb.landsmot";


	public void main(IWContext iwc) throws Exception {
		
		iwb =  getBundle(iwc);
		iwrb = iwb.getResourceBundle(iwc);
		
		switch (parseAction(iwc)) {
			case ACTION_STEP_ONE:
				stepOne(iwc);
				break;
			case ACTION_STEP_TWO:
				stepTwo(iwc);
				break;
			case ACTION_STEP_FOUR:
				stepFour(iwc);
				break;
			case ACTION_STEP_FIVE:
				stepFive(iwc);
				break;
			case ACTION_STEP_SIX:
				stepSix(iwc);
				break;
			case ACTION_SAVE:
				save(iwc, true);
				break;
			case ACTION_CANCEL:
				cancel(iwc);
				break;
		}
	}
	
	private void stepOne(IWContext iwc) {
		Form form = new Form();
		form.maintainParameter(PARAMETER_PERSONAL_ID);
		form.addParameter(PARAMETER_ACTION, ACTION_STEP_TWO);
		
		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		form.add(table);
		int row = 1;

		table.add(getPhasesTable(1, 7, "run_reg.registration", "Registration"), 1, row++);
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

	private void stepTwo(IWContext iwc) throws RemoteException {
		Form form = new Form();
		form.maintainParameter(PARAMETER_PERSONAL_ID);
		form.addParameter(PARAMETER_ACTION, "-1");
		form.addParameter(PARAMETER_FROM_ACTION, ACTION_STEP_TWO);
		
		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		form.add(table);
		int row = 1;

		table.add(getPhasesTable(2, 7, "run_reg.registration", "Registration"), 1, row++);
		table.setHeight(row++, 12);

		table.add(getInformationTable(localize("run_reg.information_text_step_2", "Information text 2...")), 1, row++);
		table.setHeight(row++, 18);
		
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

		SelectionBox eventSelect = new SelectionBox(PARAMETER_EVENT);
		Collection events = getEventBusiness(iwc).getAllSingleEvents();
		if (events != null) {
			eventSelect.addMenuElements(events);
		}
		Text redStar = getHeader("*");
		redStar.setFontColor("#ff0000");

		choiceTable.add(getHeader(localize(RR_PRIMARY_DD, "Run") + "/" + localize(RR_SECONDARY_DD, "Distance")), 1, iRow);
		choiceTable.add(redStar, 1, iRow++);
		choiceTable.mergeCells(1, iRow, choiceTable.getColumns(), iRow);
		choiceTable.add(eventSelect, 1, iRow);

		choiceTable.setHeight(iRow++, 12);
		
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
		

		choiceTable.add(getHeader(localize(RR_NAME, "Name")), 1, iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable.add(getHeader(localize(RR_GENDER, "Gender")), 3, iRow);
		choiceTable.add(redStar, 3, iRow++);
		choiceTable.add(nameField, 1, iRow);
		choiceTable.add(genderField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);

		TextInput ssnISField = (TextInput) getStyledInterface(new TextInput(PARAMETER_PERSONAL_ID));
		ssnISField.setLength(10);
		
		ssnISField.setDisabled(true);
		if (this.runner.getUser() != null) {
			ssnISField.setContent(this.runner.getUser().getPersonalID());
		}
		
		IWTimestamp stampNow = new IWTimestamp();
		stampNow.addYears(-3);

		IWTimestamp birthStamp = new IWTimestamp();
		DateInput ssnField = (DateInput) getStyledInterface(new DateInput(PARAMETER_PERSONAL_ID));
		ssnField.setAsNotEmpty("Date of birth can not be empty");
		ssnField.setYearRange(birthStamp.getYear(), birthStamp.getYear() - 100);
		ssnField.setLatestPossibleDate(stampNow.getDate(), "Invalid date of birth.  Please check the date you have selected and try again");
		if (this.runner.getDateOfBirth() != null) {
			ssnField.setDate(this.runner.getDateOfBirth());
		}

		Collection countries = getRunBusiness(iwc).getCountries();
		DropdownMenu nationalityField = (DropdownMenu) getStyledInterface(new DropdownMenu(PARAMETER_NATIONALITY));
		DropdownMenu countryField = (DropdownMenu) getStyledInterface(new DropdownMenu(PARAMETER_COUNTRY));
		nationalityField.addMenuElement("-1", localize("run_reg.select_nationality", "Select nationality..."));
		countryField.addMenuElement("-1", localize("run_reg.select_country", "Select country..."));
		SelectorUtility util = new SelectorUtility();
		if (countries != null && !countries.isEmpty()) {
			nationalityField = (DropdownMenu) util.getSelectorFromIDOEntities(nationalityField, countries, "getName");
			countryField = (DropdownMenu) util.getSelectorFromIDOEntities(countryField, countries, "getName");
		}
		countryField.setDisabled(true);
		nationalityField.setSelectedElement("104");
		if (this.runner.getUser() != null) {
			Address address = getUserBusiness(iwc).getUsersMainAddress(this.runner.getUser());
			if (address != null && address.getCountry() != null) {
				countryField.setSelectedElement(address.getCountry().getPrimaryKey().toString());
			}
		}
		nationalityField.setWidth(Table.HUNDRED_PERCENT);
		nationalityField.setAsNotEmpty(localize("run_reg.must_select_nationality", "You must select your nationality"));
		countryField.setWidth(Table.HUNDRED_PERCENT);

		if (this.runner.getCountry() != null) {
			countryField.setSelectedElement(this.runner.getCountry().getPrimaryKey().toString());
		}
		if (this.runner.getNationality() != null) {
			nationalityField.setSelectedElement(this.runner.getNationality().getPrimaryKey().toString());
		}
		
		choiceTable.add(getHeader(localize(RR_SSN, "SSN")), 1, iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable.add(getHeader(localize(RR_NATIONALITY, "Nationality")), 3, iRow);
		choiceTable.add(redStar, 3, iRow++);
		choiceTable.add(ssnISField, 1, iRow);
		choiceTable.add(nationalityField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);
		
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
		
		choiceTable.add(getHeader(localize(RR_ADDRESS, "Address")), 1, iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable.add(getHeader(localize(RR_EMAIL, "Email")), 3, iRow++);
		choiceTable.add(addressField, 1, iRow);
		choiceTable.add(emailField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);

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

		choiceTable.add(getHeader(localize(RR_CITY, "City")), 1, iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable.add(getHeader(localize(RR_TEL, "Telephone")), 3, iRow++);
		choiceTable.add(cityField, 1, iRow);
		choiceTable.add(telField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);

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

		choiceTable.add(getHeader(localize(RR_POSTAL, "Postal Code")), 1, iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable.add(getHeader(localize(RR_MOBILE, "Mobile Phone")), 3, iRow++);
		choiceTable.add(postalField, 1, iRow);
		choiceTable.add(mobileField, 3, iRow++);
		choiceTable.setHeight(iRow++, 3);

		choiceTable.add(getHeader(localize(RR_COUNTRY, "Country")), 1, iRow);
		choiceTable.add(redStar, 1, iRow);
		choiceTable.add(countryField, 1, iRow);


		SubmitButton next = (SubmitButton) getButton(new SubmitButton(localize("next", "Next")));
		next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_STEP_FOUR));
		
		table.setHeight(row++, 18);
		table.add(next, 1, row);
		table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);
		add(form);
	}

	private void stepFour(IWContext iwc) {
		Form form = new Form();
		form.maintainParameter(PARAMETER_PERSONAL_ID);
		form.addParameter(PARAMETER_ACTION, "-1");
		form.addParameter(PARAMETER_FROM_ACTION, ACTION_STEP_FOUR);
		
		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		form.add(table);
		int row = 1;

		table.add(getPhasesTable(4, 6, "run_reg.consent", "Consent"), 1, row++);
		table.setHeight(row++, 18);

		SubmitButton next = (SubmitButton) getButton(new SubmitButton(localize("next", "Next")));
		next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_STEP_FIVE));
		if (!this.runner.isAgree()) {
			next.setDisabled(true);
		}

		CheckBox agree = getCheckBox(PARAMETER_AGREE, Boolean.TRUE.toString());
		agree.setToEnableWhenChecked(next);
		agree.setToDisableWhenUnchecked(next);
		agree.setChecked(this.runner.isAgree());
		
		table.add(getText(localize("run_reg.information_text_step_4", "Information text 4...")), 1, row++);
		table.setHeight(row++, 6);
		table.add(agree, 1, row);
		table.add(Text.getNonBrakingSpace(), 1, row);
		table.add(getHeader(localize("run_reg.agree_terms", "Yes, I agree")), 1, row++);
		
		SubmitButton previous = (SubmitButton) getButton(new SubmitButton(localize("previous", "Previous")));
		previous.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_STEP_TWO));

		table.setHeight(row++, 18);
		table.add(previous, 1, row);
		table.add(Text.getNonBrakingSpace(), 1, row);
		table.add(next, 1, row);
		table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);

		add(form);
	}

	private void stepFive(IWContext iwc) {
		Form form = new Form();
		form.maintainParameter(PARAMETER_PERSONAL_ID);
		form.addParameter(PARAMETER_ACTION, "-1");
		
		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		form.add(table);
		int row = 1;

		table.add(getPhasesTable(5, 6, "run_reg.overview", "Overview"), 1, row++);
		table.setHeight(row++, 12);

		table.add(getInformationTable(localize("run_reg.information_text_step_5", "Information text 5...")), 1, row++);
		table.setHeight(row++, 18);
		
		Map runners = (Map) iwc.getSessionAttribute(SESSION_ATTRIBUTE_RUNNER_MAP);
		Table runnerTable = new Table(3, runners.size() + 1);
		runnerTable.setWidth(Table.HUNDRED_PERCENT);
		runnerTable.add(getHeader(localize("run_reg.name", "Name")), 1, 1);
		runnerTable.add(getHeader(localize("run_reg.event", "Event")), 2, 1);
		table.add(runnerTable, 1, row++);
		int runRow = 2;
		
		Iterator iter = runners.values().iterator();
		while (iter.hasNext()) {
			Runner runner = (Runner) iter.next();
			if (runner.getUser() != null) {
				runnerTable.add(getText(runner.getUser().getName()), 1, runRow);
			}
			else {
				runnerTable.add(getText(runner.getName()), 1, runRow);
			}
			Collection evs = runner.getEvents();
			Iterator eter = evs.iterator();
			while (eter.hasNext()) {
				LandsmotEvent ev =  (LandsmotEvent) eter.next();
				runnerTable.add(ev.getName(), 2, runRow++);
			}
		}
		
		//if (runner.getRun() != null) {
		//	runnerTable.add(getText(localize(runner.getRun().getName(), runner.getRun().getName())), 2, runRow);
		//	runnerTable.add(getText(localize(runner.getDistance().getName(), runner.getDistance().getName())), 3, runRow++);
		//}
		//else {
//			removeRunner(iwc, runner.getPersonalID());
		//}
		
		SubmitButton previous = (SubmitButton) getButton(new SubmitButton(localize("previous", "Previous")));
		previous.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_STEP_FOUR));
		SubmitButton registerOther = (SubmitButton) getButton(new SubmitButton(localize("run_reg.register_other", "Register other")));
		registerOther.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_STEP_ONE));
		registerOther.setValueOnClick(PARAMETER_PERSONAL_ID, "");
		SubmitButton next = (SubmitButton) getButton(new SubmitButton(localize("run_reg.pay", "Pay")));
		next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_STEP_SIX));

		table.setHeight(row++, 18);
		table.add(previous, 1, row);
		table.add(Text.getNonBrakingSpace(), 1, row);
		table.add(registerOther, 1, row);
		table.add(Text.getNonBrakingSpace(), 1, row);
		table.add(next, 1, row);
		table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);

		add(form);
	}

	private void stepSix(IWContext iwc) throws RemoteException {
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

		table.add(getPhasesTable(6, 6, "run_reg.payment_info", "Payment info"), 1, row++);
		table.setHeight(row++, 12);

		table.add(getInformationTable(localize("run_reg.information_text_step_6", "Information text 6...")), 1, row++);
		table.setHeight(row++, 18);
		
		Map runners = (Map) iwc.getSessionAttribute(SESSION_ATTRIBUTE_RUNNER_MAP);
		Table runnerTable = new Table();
		runnerTable.setWidth(Table.HUNDRED_PERCENT);
		runnerTable.setCellspacing(0);
		runnerTable.add(getHeader(localize("run_reg.name", "Name")), 1, 1);
		runnerTable.add(getHeader(localize("run_reg.event", "Event")), 2, 1);
		runnerTable.add(getHeader(localize("run_reg.price", "Price")), 3, 1);
		table.add(runnerTable, 1, row++);
		table.setHeight(row++, 18);
		int runRow = 2;
		
		float totalAmount = 0;
		Iterator iter = runners.values().iterator();
		while (iter.hasNext()) {
			Runner runner = (Runner) iter.next();
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
				float price = event.getPrice();
				totalAmount += price;
				runnerTable.add(getText(nf.format(price)), 3, runRow++);
			}
			
		

			
			addRunner(iwc, runner.getPersonalID(), runner);
			
		}
		
		if (totalAmount == 0) {
			save(iwc, false);
			return;
		}
		
		
		
		
		
		runnerTable.setHeight(runRow++, 12);
		runnerTable.add(getHeader(localize("run_reg.total_amount", "Total amount")), 1, runRow);
		runnerTable.add(getHeader(nf.format(totalAmount)), 3, runRow);
		runnerTable.setColumnAlignment(3, Table.HORIZONTAL_ALIGN_RIGHT);

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
		creditCardTable.setAlignment(3, creditRow++, Table.HORIZONTAL_ALIGN_RIGHT);
		creditCardTable.setHeight(creditRow++, 12);

		TextInput nameField = (TextInput) getStyledInterface(new TextInput(PARAMETER_NAME_ON_CARD));
		nameField.setAsNotEmpty(localize("run_reg.must_supply_card_holder_name", "You must supply card holder name"));
		nameField.keepStatusOnAction(true);
		
		TextInput ccv = (TextInput) getStyledInterface(new TextInput(PARAMETER_CCV));
		ccv.setLength(3);
		ccv.setMaxlength(3);
		ccv.setMininumLength(3, localize("run_reg.not_valid_ccv", "Not a valid CCV number"));
		ccv.setAsIntegers(localize("run_reg.not_valid_ccv", "Not a valid CCV number"));
		ccv.setAsNotEmpty(localize("run_reg.must_supply_ccv", "You must enter the CCV number"));
		ccv.keepStatusOnAction(true);
		
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
		emailField.setWidth(Table.HUNDRED_PERCENT);
		emailField.keepStatusOnAction(true);
		
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
		previous.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_STEP_FIVE));
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
			String email = ((Runner) runners.iterator().next()).getEmail();
			String expiresMonth = null;
			String expiresYear = null;
			String ccVerifyNumber = null;
			String referenceNumber = null;
			double amount = 0;
			IWTimestamp paymentStamp = new IWTimestamp();

			IWBundle iwb = getBundle(iwc);
			boolean disablePaymentProcess = "true".equalsIgnoreCase(iwb.getProperty("disable_payment_authorization_process","false"));
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
			}
			
			String properties = null;
			if (doPayment) {
				properties = getRunBusiness(iwc).authorizePayment(nameOnCard, cardNumber, expiresMonth, expiresYear, ccVerifyNumber, amount, "ISK", referenceNumber);
			}
			Collection participants = getRunBusiness(iwc).saveParticipants(runners, email, hiddenCardNumber, amount, paymentStamp, iwc.getCurrentLocale());
			if (doPayment) {
				getRunBusiness(iwc).finishPayment(properties);
			}
			iwc.removeSessionAttribute(SESSION_ATTRIBUTE_RUNNER_MAP);
			
			showReceipt(iwc, participants, amount, hiddenCardNumber, paymentStamp, doPayment);
		}
		catch (IDOCreateException ice) {
			getParentPage().setAlertOnLoad(localize("run_reg.save_failed", "There was an error when trying to finish registration.  Please contact the marathon.is office."));
			ice.printStackTrace();
			stepSix(iwc);
		}
		catch (CreditCardAuthorizationException ccae) {
			IWResourceBundle creditCardBundle = iwc.getIWMainApplication().getBundle("com.idega.block.creditcard").getResourceBundle(iwc.getCurrentLocale());
			getParentPage().setAlertOnLoad(ccae.getLocalizedMessage(creditCardBundle));
			ccae.printStackTrace();
			stepSix(iwc);
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

		table.add(getPhasesTable(7, 7, "run_reg.receipt", "Receipt"), 1, row++);
		table.setHeight(row++, 18);
		
		table.add(getHeader(localize("run_reg.hello_participant", "Hello participant(s)")), 1, row++);
		table.setHeight(row++, 16);

		table.add(getText(localize("run_reg.payment_received", "We have received payment for the following:")), 1, row++);
		table.setHeight(row++, 8);

		Table runnerTable = new Table(5, runners.size() + 3);
		runnerTable.setWidth(Table.HUNDRED_PERCENT);
		runnerTable.add(getHeader(localize("run_reg.runner_name", "Runner name")), 1, 1);
		runnerTable.add(getHeader(localize("run_reg.run", "Run")), 2, 1);
		runnerTable.add(getHeader(localize("run_reg.distance", "Distance")), 3, 1);
		runnerTable.add(getHeader(localize("run_reg.race_number", "Race number")), 4, 1);
		runnerTable.add(getHeader(localize("run_reg.shirt_size", "Shirt size")), 5, 1);
		table.add(runnerTable, 1, row++);
		int runRow = 2;
		Iterator iter = runners.iterator();
		while (iter.hasNext()) {
			//Participant participant = (Participant) iter.next();
			Group run = null;//participant.getRunTypeGroup();
			Group distance = null;//participant.getRunDistanceGroup();
			
			//runnerTable.add(getText(participant.getUser().getName()), 1, runRow);
			runnerTable.add(getText(localize(run.getName(), run.getName())), 2, runRow);
			runnerTable.add(getText(localize(distance.getName(), distance.getName())), 3, runRow);
			//runnerTable.add(getText(String.valueOf(participant.getParticipantNumber())), 4, runRow);
		}
		
		if (doPayment) {
			Table creditCardTable = new Table(2, 3);
			creditCardTable.add(getHeader(localize("run_reg.payment_received_timestamp", "Payment received") + ":"), 1, 1);
			creditCardTable.add(getText(paymentStamp.getLocaleDateAndTime(iwc.getCurrentLocale(), IWTimestamp.SHORT, IWTimestamp.SHORT)), 2, 1);
			creditCardTable.add(getHeader(localize("run_reg.card_number", "Card number") + ":"), 1, 2);
			creditCardTable.add(getText(cardNumber), 2, 2);
			creditCardTable.add(getHeader(localize("run_reg.amount", "Amount") + ":"), 1, 3);
			creditCardTable.add(getText(String.valueOf(amount)), 2, 3);
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
		table.add(getText(localize("run_reg.reykjavik_marathon", "Reykjavik Marathon")), 1, row++);
		table.add(getText("www.marathon.is"), 1, row++);
		
		table.setHeight(row++, 16);
		
		Link print = new Link(localize("print", "Print"));
		print.setPublicWindowToOpen(RegistrationReceivedPrintable.class);
		table.add(print, 1, row);
		
		add(table);
	}
	
	private void cancel(IWContext iwc) {
		iwc.removeSessionAttribute(SESSION_ATTRIBUTE_RUNNER_MAP);
	}

	private Runner collectValues(IWContext iwc) throws FinderException, RemoteException {
		String personalID = iwc.getParameter(PARAMETER_PERSONAL_ID);
		if (personalID != null && personalID.length() > 0) {
			Runner runner = getRunner(iwc, personalID);
			if (runner == null) {
				runner = new Runner();
				runner.setPersonalID(personalID);
				User user = getUserBusiness(iwc).getUser(personalID);
				runner.setUser(user);
			}

			if (iwc.isParameterSet(PARAMETER_NAME)) {
				runner.setName(iwc.getParameter(PARAMETER_NAME));
			}
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
			if (iwc.isParameterSet(PARAMETER_NATIONALITY)) {
				runner.setNationality(getUserBusiness(iwc).getAddressBusiness().getCountryHome().findByPrimaryKey(new Integer(iwc.getParameter(PARAMETER_NATIONALITY))));
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
			if (iwc.isParameterSet(PARAMETER_AGREE)) {
				runner.setAgree(true);
			}
			if (iwc.isParameterSet(PARAMETER_EVENT)) {
				String[] events = iwc.getParameterValues(PARAMETER_EVENT);
				for (int i = 0; i < events.length; i++) {
					LandsmotEvent ev = getEventBusiness(iwc).getEvent(new Integer(events[i]));
					System.out.println("[LandsmotRegistration] event : "+ev);
					runner.addEvent(ev);
				}
			}

			addRunner(iwc, personalID, runner);
			return runner;
		}
		return new Runner();
	}
	
	private int parseAction(IWContext iwc) throws RemoteException {
		int action = ACTION_STEP_ONE;
		
		if (iwc.isParameterSet(PARAMETER_ACTION)) {
			action = Integer.parseInt(iwc.getParameter(PARAMETER_ACTION));
		}

		try {
			this.runner = collectValues(iwc);
		}
		catch (FinderException fe) {
			getParentPage().setAlertOnLoad(localize("run_reg.user_not_found_for_personal_id", "No user found with personal ID."));
			action = ACTION_STEP_ONE;
		}
		return action;
	}
	
	private Runner getRunner(IWContext iwc, String key) {
		Map runnerMap = (Map) iwc.getSessionAttribute(SESSION_ATTRIBUTE_RUNNER_MAP);
		if (runnerMap != null) {
			return (Runner) runnerMap.get(key);
		}
		return null;
	}
	
	private void addRunner(IWContext iwc, String key, Runner runner) {
		Map runnerMap = (Map) iwc.getSessionAttribute(SESSION_ATTRIBUTE_RUNNER_MAP);
		if (runnerMap == null) {
			runnerMap = new HashMap();
		}
		runnerMap.put(key, runner);
		iwc.setSessionAttribute(SESSION_ATTRIBUTE_RUNNER_MAP, runnerMap);
	}
	
	private void removeRunner(IWContext iwc, String key) {
		Map runnerMap = (Map) iwc.getSessionAttribute(SESSION_ATTRIBUTE_RUNNER_MAP);
		if (runnerMap == null) {
			runnerMap = new HashMap();
		}
		runnerMap.remove(key);
		iwc.setSessionAttribute(SESSION_ATTRIBUTE_RUNNER_MAP, runnerMap);
	}

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
	
	public static final String PROPERTY_MERCHANT_PK = "merchant_pk";
	public static final String PROPERTY_STAFF_GROUP_ID = "staff_group_id";
  
  public static final String GROUP_TYPE_RUN = "iwma_run";
/*  public static final String GROUP_TYPE_RUN_MARATHON = "iwma_run_marathon";
  public static final String GROUP_TYPE_RUN_LAUGAVEGUR = "iwma_run_laugavegur";
  public static final String GROUP_TYPE_RUN_MIDNIGHT = "iwma_run_midnight";*/
  public static final String GROUP_TYPE_RUN_YEAR = "iwma_run_year";
  public static final String GROUP_TYPE_RUN_DISTANCE = "iwma_run_distance";
  public static final String GROUP_TYPE_RUN_GROUP = "iwma_run_group";
  public static final String PARAMETER_SORT_BY = "iwma_sort_by";
  
  public static final String PARAMETER_SSN_IS = "prm_ssn_is";
  public static final String PARAMETER_SSN = "prm_ssn";
  public static final String PARAMETER_FEMALE = "2";
  public static final String PARAMETER_MALE = "1";
  public static final String PARAMETER_POSTAL = "prm_postal";
  public static final String PARAMETER_TEL ="prm_tel";
  public static final String PARAMETER_MOBILE = "prm_mobile";
  public static final String PARAMETER_TSHIRT = "prm_tshirt";
  public static final String PARAMETER_TSHIRT_S = "prm_small";
  public static final String PARAMETER_TSHIRT_M = "prm_medium";
  public static final String PARAMETER_TSHIRT_L = "prm_large";
  public static final String PARAMETER_TSHIRT_XL = "prm_xlarge";
  public static final String PARAMETER_TSHIRT_XXL = "prm_xxlarge";
  public static final String PARAMETER_OWN_CHIP = "prm_own_chip";
  public static final String PARAMETER_BUY_CHIP = "prm_buy_chip";
  public static final String PARAMETER_RENT_CHIP = "prm_rent_chip";
  public static final String PARAMETER_GROUP_COMP = "prm_group_comp";
  public static final String PARAMETER_GROUP_NAME = "prm_group_name";
  public static final String PARAMETER_BEST_TIME = "prm_best_time";
  public static final String PARAMETER_GOAL_TIME = "prm_goal_time";
  public static final String PARAMETER_TOTAL = "prm_total";
  public static final String PARAMETER_GROUPS = "prm_groups";
  public static final String PARAMETER_GROUPS_COMPETITION = "prm_groups_competition";
  public static final String PARAMETER_AGREEMENT = "prm_agreement";
  public static final String PARAMETER_DISAGREE = Boolean.FALSE.toString();
  public static final String PARAMETER_PAY_METHOD = "prm_pay_method";
  public static final String PARAMETER_PARTICIPANT_NUMBER = "prm_parti_nr";
	
  //localized strings
  public static final String RR_INFO_RED_STAR = "run_reg.info_red_star";
  public static final String RR_PRIMARY_DD = "run_reg.primary_dd_lable";
  public static final String RR_SECONDARY_DD = "run_reg.secondary_dd_label";
  public static final String RR_NAME = "run_reg.name";
  public static final String RR_NATIONALITY = "run_reg.nationality";
  public static final String RR_SSN = "run_reg.ssn";
  public static final String RR_GENDER = "run_reg.gender";
  public static final String RR_FEMALE = "run_reg.female";
  public static final String RR_MALE = "run_reg.male";
  public static final String RR_ADDRESS = "run_reg.address";
  public static final String RR_POSTAL = "run_reg.postal";
  public static final String RR_CITY = "run_reg.city";
  public static final String RR_COUNTRY = "run_reg.country";
  public static final String RR_TEL = "run_reg.tel";
  public static final String RR_MOBILE = "run_reg.mobile";
  public static final String RR_EMAIL = "run_reg.email";
  public static final String RR_TSHIRT = "run_reg.t_shirt";
  public static final String RR_CHIP_TIME = "run_reg.champion_chip_timing";
  public static final String RR_CHIP_LINK = "run_reg.champion_chip_link";
  public static final String RR_OWN_CHIP = "run_reg.own_chip";
  public static final String RR_BUY_CHIP = "run_reg.buy_chip";
  public static final String RR_RENT_CHIP = "run_reg.rent_chip";
  public static final String RR_GROUP_COMP = "run_reg.group_competition";
  public static final String RR_GROUP_NAME = "run_reg.group_name";
  public static final String RR_BEST_TIME = "run_reg.best_time";
  public static final String RR_GOAL_TIME = "run_reg.goal_time";
  public static final String RR_AGREEMENT = "run_reg.agreement";
	public static final String RR_AGREE = "run_reg.agree";
	public static final String RR_DISAGREE = "run_reg.disagree";
  public static final int RYSDD_TOTAL = 1;
  public static final int RYSDD_GROUPS = 2;
  public static final int RYSDD_GROUPS_COMP = 3;
}
