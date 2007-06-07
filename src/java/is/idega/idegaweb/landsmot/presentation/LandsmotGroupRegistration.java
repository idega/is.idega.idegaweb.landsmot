package is.idega.idegaweb.landsmot.presentation;

import is.idega.idegaweb.landsmot.business.EventParticipant;
import is.idega.idegaweb.landsmot.data.LandsmotEvent;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.block.web2.business.Web2Business;
import com.idega.business.IBOLookup;
import com.idega.presentation.IWContext;
import com.idega.presentation.Span;
import com.idega.presentation.Table;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.user.data.User;

public class LandsmotGroupRegistration extends LandsmotRegistration {

	private static String localization_prefix = "landsmot_group_reg";
	
	public void main(IWContext iwc) throws Exception {
		super.main(iwc);
		super.getParentPage().addStyleSheetURL(iwb.getResourcesVirtualPath()+"/style/registration.css");
	}
	
	protected void stepOne(IWContext iwc) throws RemoteException {
		Form form = new Form();
		form.addParameter(PARAMETER_ACTION, ACTION_STEP_FOUR);
		
		Table table = new Table();
		table.setId("landsmot_registration");
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		form.add(table);
		int row = 1;

		LandsmotEvent event = null;
		String sPK = iwc.getParameter(PARAMETER_EVENT);
		if (sPK != null && !sPK.equals("-1")) {
			event = getEventBusiness(iwc).getEvent(new Integer(sPK));
		}
		
		getParentPage().addJavascriptURL("/dwr/interface/LandsmotEventBusiness.js");
		getParentPage().addJavascriptURL("/dwr/engine.js");
		getParentPage().addJavascriptURL("/dwr/util.js");
		Web2Business web2bus = (Web2Business) IBOLookup.getServiceInstance(iwc, Web2Business.class);
		getParentPage().addJavascriptURL(web2bus.getBundleURIToMootoolsLib());
		getParentPage().addJavascriptURL(iwb.getResourcesVirtualPath()+"/js/registration.js");

		
		table.add(getPhasesTable(1, 5, localization_prefix+".registration", "Registration"), 1, row++);
		table.setHeight(row++, 12);

		table.add(getInformationTable(localize(localization_prefix+".information_text_step_1", "Information text 1...")), 1, row++);
		table.setHeight(row++, 6);

		DropdownMenu menu = new DropdownMenu(PARAMETER_EVENT);
		Collection events = getEventBusiness(iwc).getAllGroupEvents();
		menu.setValueOnChange(PARAMETER_ACTION, String.valueOf(ACTION_STEP_ONE));
		menu.setToSubmit(true);
		if (events != null && !events.isEmpty()) {
			menu.addMenuElement("-1", localize(localization_prefix+".select_an_event", "Select an event"));
			menu.addMenuElements(events);
		} else {
			menu.addMenuElement("-1", localize(localization_prefix+".no_event_available", "No event available"));
		}
		if (event != null) {
			menu.setSelectedElement(event.getPrimaryKey().toString());
		}
		
		table.setCellpadding(1, row, 24);
		Label label = new Label(localize("run_reg.event", "Event"), menu);
		table.add(label, 1, row);
		table.add(menu, 1, row++);
		

		
		if (event != null) {

			table.setCellpaddingLeft(1, row, 24);
			TextInput inp = new TextInput(PARAMETER_NAME);
			boolean req = true;
			if (req) {
				inp.setStyleClass("required");
				inp.setAsNotEmpty(localize(localization_prefix+".you_must_name_your_team", "You must name your team"));
			}
			label = new Label(localize(localization_prefix+".team_name", "Team name"), inp);
			table.add(label, 1, row);
			table.add(inp, 1, row);
			++row;
			
			int min = event.getMinSize();
			int max = event.getMaxSize();
			int counter = 0;
			for (int i = 1; i <= max; i++) {
				table.setCellpaddingLeft(1, row, 24);
				inp = new TextInput(PARAMETER_PERSONAL_ID+i);
				inp.setStyleClass("personalID");
				req = i <= min;
				if (req) {
					inp.setAsNotEmpty(localize(localization_prefix+".participants_marked_with_star_must_be_selected", "Participants with star (*) must be selected."));
					inp.setStyleClass("required");
				}
				label = new Label((req?"* ":"") +localize(localization_prefix+".participant", "Participant")+" "+(++counter), inp);
				table.add(label, 1, row);
				table.add(inp, 1, row);
				
				Span p = new Span();
				p.setId("span"+i);
				p.setStyleClass("userResults");

				table.add(p, 1, row);
				
				
				++row;
			}
		}
		
		SubmitButton next = (SubmitButton) getButton(new SubmitButton(localize("next", "Next")));
		
		table.add(next, 1, row);
		table.setAlignment(1, row, Table.HORIZONTAL_ALIGN_RIGHT);

		add(form);
	}

	protected EventParticipant collectValues(IWContext iwc) throws FinderException, RemoteException {
		String personalID = iwc.getParameter(PARAMETER_PERSONAL_ID+1);
		EventParticipant mainPart = null;
		LandsmotEvent ev = null;
		if (personalID != null && personalID.length() > 0) {
			mainPart = getRunner(iwc, personalID);
			if (mainPart == null) {
				mainPart = new EventParticipant();
				mainPart.setPersonalID(personalID);
				User user = getUserBusiness(iwc).getUser(personalID);
				mainPart.setUser(user);
				mainPart.addParticipant(user);
			}

			if (iwc.isParameterSet(PARAMETER_NAME)) {
				mainPart.setName(iwc.getParameter(PARAMETER_NAME));
			}

			collectContactValues(iwc, mainPart);
			
			if (iwc.isParameterSet(PARAMETER_AGREE)) {
				mainPart.setAgree(true);
			}
			if (iwc.isParameterSet(PARAMETER_EVENT)) {
				String[] events = iwc.getParameterValues(PARAMETER_EVENT);
				for (int i = 0; i < events.length; i++) {
					ev = getEventBusiness(iwc).getEvent(new Integer(events[i]));
					mainPart.addEvent(ev);
				}
			}

			mainPart.setGroup(true);
			
			if (mainPart != null && ev != null && ev.getGroups()) {
				int max = ev.getMaxSize();
				// DO the other parts
				for (int i = 2; i <= max; i++) {
					String pid = iwc.getParameter(PARAMETER_PERSONAL_ID+i);
					if (pid != null && !pid.equals("")) {
						User user = getUserBusiness(iwc).getUser(pid);
						mainPart.addParticipant(user);
					}
				}
				
				addRunner(iwc, personalID, mainPart);
			}
			
			return mainPart;
		}
		return new EventParticipant();
	}

}
