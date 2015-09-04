package io.github.omn0mn0m;

import java.io.IOException;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class Main {
	
	static WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38);
	static HtmlPage registrationPage;

	public static void main(String[] args) throws Exception {
		login();
		navigateToRegistration();
		spam();
		webClient.close();
	}
	
	public static void login() throws Exception {
		System.out.println("Logging in...");
		registrationPage = webClient.getPage("http://banweb.gwu.edu/PRODCartridge/twbkwbis.P_WWWLogin");

		final HtmlForm form = registrationPage.getFormByName("loginform");

		final HtmlPasswordInput usernameField = form.getInputByName("sid");
		usernameField.setValueAttribute(Values.GWID);

		final HtmlPasswordInput passwordField = form.getInputByName("PIN");
		passwordField.setValueAttribute(Values.PIN);

		final HtmlSubmitInput button = form.getInputByValue("Login");
		registrationPage = button.click();
	}
	
	public static void navigateToRegistration() {
		findAndClickLink("Student Records & Registration Menu", "/PRODCartridge/twbkwbis.P_GenMenu?name=bmenu.P_StuMainMnu");	// Enters "Student Records & Registration Menu"
		findAndClickLink("Registration Menu", "/PRODCartridge/twbkwbis.P_GenMenu?name=bmenu.P_RegMnu");							// Enters "Registration Menu"
		findAndClickLink("Register, Drop and/or Add Classes", "/PRODCartridge/bwskfreg.P_AddDropCrse");							// Enters "Register, Drop, and/or Add Classes"
		
		System.out.println("Selecting term...");
		try {
			final List<?> forms = registrationPage.getByXPath("//form [@action='/PRODCartridge/bwskfreg.P_AddDropCrse']");
			
			if (forms.size() != 0) {
				final HtmlForm form = (HtmlForm)forms.get(0);
				final HtmlSubmitInput submitButton = (HtmlSubmitInput)form.getInputByValue("Submit");
				registrationPage = submitButton.click();
			} else {
				System.out.println("WTF");
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void spam() {
		System.out.println("Spamming...");
		
		try {
			registrationPage.refresh();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(0);
		}
		
		try {
			final List<?> inputFields = registrationPage.getByXPath("//input [@type='text'] [@name='CRN_IN'] [@size='6'] [@maxlength='5']");
			
			if (inputFields.size() == 10) {
				for (int i = 0; i < Values.CRN.length; i++) {
					((List<HtmlTextInput>)inputFields).get(i).setValueAttribute(Values.CRN[i]);
				}
				
				
				
				System.out.println("All done.");
			} else {
				System.out.println("WTF");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Trying again...");
			spam();
		}
	}
	
	public static void findAndClickLink(String destinationName, String href) {
		System.out.println("Navigating to " + destinationName + "...");
		final List<?> links = registrationPage.getByXPath("//a [@href='" + href + "']");
		if (links.size() != 0) {
			try {
				registrationPage = ((DomElement) links.get(0)).click();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		} else {
			System.out.println("WTF");
			System.exit(0);
		}
	}
}
