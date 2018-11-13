package io.github.omn0mn0m;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class Values {
	Scanner input;
	List<String> lines;
	
	public String GWID;
	public String PIN;
	public String SECRET;
	
	public String[] CRN;
	
	public Values() {
		try {
			input = new Scanner(new File("resources/login.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("Login credentials file not found...");
		}
		
		GWID = input.nextLine();
		PIN = input.nextLine();
		
		if (input.hasNextLine()) {
			SECRET = input.nextLine();
		} else {
			SECRET = null;
		}
		
//		System.out.println("GWID: " + GWID + " | PIN: " + PIN + " | SECRET: " + SECRET);
		
		try {
			input = new Scanner(new File("resources/crns.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("CRNs file not found...");
		}
		
		CRN = new String[Integer.parseInt(input.nextLine())];
		
		for (int i = 0; i < CRN.length; i++) {
			CRN[i] = input.nextLine();
			
			if (i == 0) {
				System.out.print("CRNs Found: " + CRN[i] + " ");
			} else {
				System.out.print("| " + CRN[i] + " ");
			}
		}
		
		System.out.println();
	}
}
