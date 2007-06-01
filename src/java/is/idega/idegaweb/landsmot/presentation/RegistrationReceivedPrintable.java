/**
 * 
 */
package is.idega.idegaweb.landsmot.presentation;


import java.text.NumberFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.PrintButton;
import com.idega.presentation.ui.Window;
import com.idega.user.data.Group;
import com.idega.util.IWTimestamp;
import com.idega.util.LocaleUtil;

/**
 * @author Sigtryggur
 *
 */
public class RegistrationReceivedPrintable extends Window {
	
	public final static String IW_BUNDLE_IDENTIFIER = "is.idega.idegaweb.landsmot";
	
	public RegistrationReceivedPrintable() {
		setResizable(true);
		setHeight(450);
		setWidth(650);
	}

	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = getResourceBundle(iwc);
		setTitle(iwrb.getLocalizedString("run_reg.receipt", "Receipt"));

		Table t = new Table(1, 2);
		t.setStyleAttribute("border-style:solid;border-color:#e1e1e1;border-width:1px;");
		t.setHeight(Table.HUNDRED_PERCENT);
		t.setVerticalAlignment(1, 1, Table.VERTICAL_ALIGN_TOP);
		t.setVerticalAlignment(1, 2, Table.VERTICAL_ALIGN_BOTTOM);

		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		int row = 1;
		
		Collection runners = (Collection) iwc.getSessionAttribute(LandsmotRegistration.SESSION_ATTRIBUTE_PARTICIPANTS);
		double amount = ((Double) iwc.getSessionAttribute(LandsmotRegistration.SESSION_ATTRIBUTE_AMOUNT)).doubleValue();
		String cardNumber = (String) iwc.getSessionAttribute(LandsmotRegistration.SESSION_ATTRIBUTE_CARD_NUMBER);
		IWTimestamp stamp = (IWTimestamp) iwc.getSessionAttribute(LandsmotRegistration.SESSION_ATTRIBUTE_PAYMENT_DATE);

		table.add(getHeader(iwrb.getLocalizedString("run_reg.hello_participant", "Hello participant(s)")), 1, row++);
		table.setHeight(row++, 16);

		table.add(getText(iwrb.getLocalizedString("run_reg.payment received", "We have received payment for the following:")), 1, row++);
		table.setHeight(row++, 8);

		Table runnerTable = new Table(5, runners.size() + 3);
		runnerTable.setWidth(Table.HUNDRED_PERCENT);
		runnerTable.add(getHeader(iwrb.getLocalizedString("run_reg.runner_name", "Runner name")), 1, 1);
		runnerTable.add(getHeader(iwrb.getLocalizedString("run_reg.run", "Run")), 2, 1);
		runnerTable.add(getHeader(iwrb.getLocalizedString("run_reg.distance", "Distance")), 3, 1);
		runnerTable.add(getHeader(iwrb.getLocalizedString("run_reg.race_number", "Race number")), 4, 1);
		runnerTable.add(getHeader(iwrb.getLocalizedString("run_reg.shirt_size", "Shirt size")), 5, 1);
		table.add(runnerTable, 1, row++);
		int runRow = 2;
		Iterator iter = runners.iterator();
		while (iter.hasNext()) {
			//Participant participant = (Participant) iter.next();
			Group run = null;//participant.getRunTypeGroup();
			Group distance = null;//participant.getRunDistanceGroup();
			
			//runnerTable.add(getText(participant.getUser().getName()), 1, runRow);
			runnerTable.add(getText(iwrb.getLocalizedString(run.getName(), run.getName())), 2, runRow);
			runnerTable.add(getText(iwrb.getLocalizedString(distance.getName(), distance.getName())), 3, runRow);
			//runnerTable.add(getText(String.valueOf(participant.getParticipantNumber())), 4, runRow);
		}
		
		Table creditCardTable = new Table(2, 3);
		creditCardTable.add(getHeader(iwrb.getLocalizedString("run_reg.payment_received_timestamp", "Payment received") + ":"), 1, 1);
		creditCardTable.add(getText(stamp.getLocaleDateAndTime(iwc.getCurrentLocale(), IWTimestamp.SHORT, IWTimestamp.SHORT)), 2, 1);
		creditCardTable.add(getHeader(iwrb.getLocalizedString("run_reg.card_number", "Card number") + ":"), 1, 2);
		creditCardTable.add(getText(cardNumber), 2, 2);
		creditCardTable.add(getHeader(iwrb.getLocalizedString("run_reg.amount", "Amount") + ":"), 1, 3);
		creditCardTable.add(getText(formatAmount(iwc.getCurrentLocale(), (float) amount)), 2, 3);
		table.setHeight(row++, 16);
		table.add(creditCardTable, 1, row++);
		
		table.setHeight(row++, 16);
		table.add(getHeader(iwrb.getLocalizedString("run_reg.delivery_of_race_material_headline", "Race material and T-shirt/sweatshirt")), 1, row++);
		table.add(getText(iwrb.getLocalizedString("run_reg.delivery_of_race_material_body", "Participants can collect their race number and the t-shirt/sweatshirt here.")), 1, row++);

		table.setHeight(row++, 16);
		table.add(getHeader(iwrb.getLocalizedString("run_reg.receipt_info_headline", "Receipt - Please Print It Out")), 1, row++);
		table.add(getText(iwrb.getLocalizedString("run_reg.receipt_info_headline_body", "This document is your receipt, please print this out and bring it with you when you get your race number and T-shirt/sweatshirt.")), 1, row++);

		table.setHeight(row++, 16);
		table.add(getText(iwrb.getLocalizedString("run_reg.best_regards", "Best regards,")), 1, row++);
		table.add(getText(iwrb.getLocalizedString("run_reg.landsmot", "Landsmot UMFI")), 1, row++);
		table.add(getText("skraning.felix.is"), 1, row++);
		
		table.setHeight(row++, 16);
		t.add(table, 1, 1);
		
		PrintButton print = new PrintButton(iwrb.getLocalizedString("print", "Print"));
		t.add(print, 1, 2);
		
		add(t);
	}
	
	private String formatAmount(Locale locale, float amount) {
		return NumberFormat.getInstance(locale).format(amount) + " " + (locale.equals(LocaleUtil.getIcelandicLocale()) ? "ISK" : "EUR");
	}
	
	private Text getHeader(String s) {
		Text text = new Text(s);
		text.setBold(true);
		
		return text;
	}
	
	private Text getText(String s) {
		return new Text(s);
	}
	
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
}
