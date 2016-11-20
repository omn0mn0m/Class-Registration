package io.github.omn0mn0m;

import java.io.IOException;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class Main {
	
	static Values values = new Values();
	
	static WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38);
	static HtmlPage registrationPage;
	
	static final boolean DEBUG = false;
	
	static boolean regWindowOpen;
	static boolean loggedIn;
	
	static final int MAX_ATTEMPTS = 75;

	public static void main(String[] args) throws Exception {
		System.out.println("Press ENTER to start...");
		System.in.read();
		
		login();
		navigateToRegistration();
		spam();
		webClient.close();
	}
	
	public static void login() throws Exception {
		System.out.println("Logging in...");
		registrationPage = webClient.getPage("http://banweb.gwu.edu/PRODCartridge/twbkwbis.P_WWWLogin");

		final HtmlForm form = registrationPage.getFormByName("loginform");

		form.getInputByName("sid").setValueAttribute(values.GWID);
		form.getInputByName("PIN").setValueAttribute(values.PIN);

		registrationPage = form.getInputByValue("Login").click();
		
		loggedIn = true;
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
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void spam() throws Exception {
		boolean registered = false;
		int attempts = 0;
		
		while (!registered) {
			if (loggedIn) {
				
				System.out.print("Spamming... ");
				
				try {
					registrationPage.refresh();
					regWindowOpen = true;
				} catch (Exception e) {
					regWindowOpen = false;
				}
				
				try {
					final List<?> inputFields = registrationPage
							.getByXPath("//input [@type='text'] [@name='CRN_IN'] [@size='6'] [@maxlength='5']");

					if (inputFields.size() >= values.CRN.length) {
						for (int i = 0; i < values.CRN.length; i++) {
							((List<HtmlTextInput>) inputFields).get(i)
									.setValueAttribute(values.CRN[i]);
							System.out
									.println(((List<HtmlTextInput>) inputFields)
											.get(i).getValueAttribute()
											+ " typed in...");
						}

						final List<?> submitButton = registrationPage
								.getByXPath("//input [@type='submit'] [@name='REG_BTN'] [@value='Submit Changes']");

						if (submitButton.size() != 0) {
							if (!DEBUG) {
								registrationPage = ((HtmlSubmitInput) submitButton
										.get(0)).click();
							}

							System.out.println("Courses submitted...");
						} else {
							System.out.println("Submit button not found...");
						}

						registered = true;
						System.out.println("All done.");
					} else {
						if (registrationPage.getTitleText().equals("403 Forbidden") || (registrationPage.getByXPath("//form [@name='loginform']").size() != 0)) {
							System.out.println("Need to log back in...");
							loggedIn = false;
						} else {
							System.out.println("Registration Window not open...");
							attempts++;
							
							if (attempts >= MAX_ATTEMPTS) {
								System.out.println("Max attempts reached... Need to log back in...");
								loggedIn = false;
							}
						}
					}
				} catch (Exception e) {
					System.out.println("Unexpected error... Trying again from the beginning");
					loggedIn = false;
				}
			} else if (!loggedIn) {
				login();
				navigateToRegistration();
				spam();
			}
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
