/**
 * Copyright (C) 2016 Leo van der Meulen
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.0
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * See: http://www.gnu.org/licenses/gpl-3.0.html
 *  
 * Problemen in deze code:
 */
package nl.detoren.ijc.io;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;

import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.groepen.Groepen;
import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.ui.control.IJCController;

public class OutputIntekenlijst {

	private final static Logger logger = Logger.getLogger(OutputIntekenlijst.class.getName());

	public void exportIntekenlijst(Groepen uitslag) {
		if (!IJCController.c().exportIntekenlijst)
			return;
		try {
			logger.log(Level.INFO, "Wedstrijden wegschrijven naar Excel");
			FileInputStream file = new FileInputStream("Leeg.docx");
			XWPFDocument document = new XWPFDocument(file);
			XWPFParagraph paragraph = document.getLastParagraph();
			setDoubleLineSpacing(paragraph);
			XWPFRun run = paragraph.createRun();
			run.setFontFamily("Courier New");
			run.setFontSize(12);
			String result;
			for (int i = 0; i < uitslag.getAantalGroepen(); ++i) {
				if (i >= 1)
					run.addBreak();
				Groep groep = uitslag.getGroepById(i);
				result = "Stand na " + uitslag.getRonde() + "e ronde, " + uitslag.getPeriode();
				result += "e periode                " + groep.getNaam() + " (" + groep.getSpelers().size() + ")\n";
				run.setText(result);
				run.addBreak();
				result = "    Naam                           ini   zw rating  gespeeld tegen  pnt\n";
				run.setText(result);
				run.addBreak();
				result = "-----------------------------------------------------------------------\n";
				run.setText(result);
				run.addBreak();
				for (Speler s : groep.getSpelers()) {
					result = s.toPrintableString(false);
					run.setText(result);
					run.addBreak();
				}

				if (IJCController.c().exportDoorschuivers) {
					int ndoor = IJCController.c().bepaalAantalDoorschuivers(uitslag.getPeriode(), uitslag.getRonde());
					if (i + 1 < uitslag.getAantalGroepen()) {
						result = IJCController.c().exportDoorschuiversStart + "\n";
						run.setText(result);
						run.addBreak();
						Groep lager = uitslag.getGroepById(i + 1);
						for (int j = 0; j < ndoor; j++) {
							Speler s = lager.getSpelerByID(j + 1);
							result = s.toPrintableString(false) + "\n";
							run.setText(result);
							run.addBreak();
						}
						result = IJCController.c().exportDoorschuiversStop + "\n" + "\n";
						run.setText(result);
					}
				}
				run.addCarriageReturn(); // separate previous text from break
				run.addBreak(BreakType.PAGE);
			}
			// Close input file
			file.close();
			// Store Excel to new file
			String outputFile = "IntekenlijstR" + uitslag.getPeriode() + "-" + uitslag.getRonde() + ".docx";
			FileOutputStream outFile = new FileOutputStream(new File(outputFile));
			document.write(outFile);
			// Close output file
			document.close();
			outFile.close();
			// And open it in the system editor
			Desktop.getDesktop().open(new File(outputFile));
		} catch (Exception e) {
			logger.log(Level.WARNING, "Export mislukt :" + e.getMessage());
		}
	}

	/**
	 * Set spacing to double
	 * 
	 * @param para
	 */
	public void setDoubleLineSpacing(XWPFParagraph para) {
		CTPPr ppr = para.getCTP().getPPr();
		if (ppr == null)
			ppr = para.getCTP().addNewPPr();
		CTSpacing spacing = ppr.isSetSpacing() ? ppr.getSpacing() : ppr.addNewSpacing();
		spacing.setAfter(BigInteger.valueOf(0));
		spacing.setBefore(BigInteger.valueOf(0));
		spacing.setLineRule(STLineSpacingRule.AUTO);
		spacing.setLine(BigInteger.valueOf(480));
	}
}
