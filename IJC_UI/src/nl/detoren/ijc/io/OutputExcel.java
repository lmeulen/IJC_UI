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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.wedstrijden.Groepswedstrijden;
import nl.detoren.ijc.data.wedstrijden.Serie;
import nl.detoren.ijc.data.wedstrijden.Wedstrijd;
import nl.detoren.ijc.data.wedstrijden.Wedstrijden;

/**
 * Sla het wedstrijdschema op in Excel
 *
 * @author Leo van der Meulen
 *
 */
public class OutputExcel implements WedstrijdenExportInterface {

	private final static Logger logger = Logger.getLogger(OutputExcel.class.getName());

	/**
	 * Create the Excel version of the sheet
	 * Original Empty file is stored in Empty.xlsx
	 * Create version with round matches is stored in Indeling.xlsx
	 * @param wedstrijden The round to store in the Excel file
	 */
	public boolean export(Wedstrijden wedstrijden) {
		try {
			logger.log(Level.INFO, "Wedstrijden wegschrijven naar Excel");
			int[] rowOffset = { 6, 28 }; // starting row for each serie
			int rowOffsetTrio = 50; // starting row for trio matches

			int periode = wedstrijden.getPeriode();
			int ronde = wedstrijden.getRonde();
			String rpString = "Periode " + periode + ", Ronde " + ronde;
			String datum = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(Calendar.getInstance().getTime());

			// Open the empty schedule file, matches are stored in the
			// second sheet (id = 1)
			FileInputStream file = new FileInputStream("Empty.xlsx");
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			ArrayList<Groepswedstrijden> gws = wedstrijden.getGroepswedstrijden();
			for (Groepswedstrijden gw : gws) {
				int groepID = gw.getNiveau();
				int nrSeries = gw.getSeries().size();
				// Open sheet voor deze groep
				XSSFSheet sheet = workbook.getSheetAt(groepID);
				workbook.setSheetName(groepID, Groep.geefNaam(groepID));
				updateCell(sheet, 2, 8, rpString);
				updateCell(sheet, 2, 4, datum);
				// Export Series
				for (int s = 0; s < nrSeries; ++s) { // For each serie
					Serie serie = gw.getSerie(s);
					int i = 0;
					for (Wedstrijd w : serie.getWedstrijden()) {
						exportWedstrijd(sheet, w, rowOffset[s] + 2 * i);
						i++;
					}
				}
				// Export trio
				ArrayList<Wedstrijd> trio = gw.getTriowedstrijden();
				int i = 0;
				for (Wedstrijd w : trio) {
					exportWedstrijd(sheet, w, rowOffsetTrio + 2 * i);
					i++;
				}
				// Cleanup export sheets
				// Clean up trios
				if (trio.isEmpty()) {
					for (int rowid = 1; rowid < 7; rowid++) {
						XSSFRow row = sheet.getRow(rowOffsetTrio - 2);
						sheet.removeRow(row);
						int lastRowNum = sheet.getLastRowNum();
						sheet.shiftRows(rowOffsetTrio-1, lastRowNum, -1, true, false);
					}
				}
				// Clean up series
				int aantalWedstrijden = gw.getSerie(0).getWedstrijden().size();
				int nrRowsToRemove = 10 - aantalWedstrijden;
				for (int s = nrSeries-1; s >= 0; --s) { // For each serie
					for (int r = 0; r < nrRowsToRemove; r++) {
						int row2delete = 10 - r - 1;
						int lastRowNum = sheet.getLastRowNum();
						int rowid = (rowOffset[s] + (2 * row2delete));
						XSSFRow row = sheet.getRow(rowid);
						sheet.removeRow(row);
						sheet.shiftRows(rowid+1, lastRowNum, -1, true, false);
						row = sheet.getRow(rowid+1);
						sheet.removeRow(row);
						sheet.shiftRows(rowid+2, lastRowNum, -1, true, false);
					}

				}
				if (nrSeries == 1) {
					// Eén serie gespeeld dus tweede serie volledig verwijderen
					int lastRowNum = sheet.getLastRowNum();
					for (int r = rowOffset[1] - 3; r < lastRowNum - 3; r++) {
						XSSFRow row = sheet.getRow(r);
						sheet.removeRow(row);
					}
					int a = rowOffset[1] - 3;
					int b = lastRowNum - 4;
					int c = (lastRowNum - rowOffset[1] - 4);
					logger.log(Level.INFO, "shiftrows " + a + "," + b + "," + c);
					sheet.shiftRows(rowOffset[1]-3, lastRowNum - 4, (lastRowNum - rowOffset[1]), true, false);
				}
			}
			// Close input file
			file.close();
			// Store Excel to new file
			String outputFile = "Indeling " + periode + "-" + ronde + ".xlsx";
			FileOutputStream outFile = new FileOutputStream(new File(outputFile));
			workbook.write(outFile);
			// Close output file
			workbook.close();
			outFile.close();
			// And open it in the system editor
			Desktop.getDesktop().open(new File(outputFile));
			return true;
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error writing output: " + e.toString());
			return false;
		}
	}

	/**
	 * Exporteer een wedstrijd naar Excel. Wedstrijd wordt in gespecificeerde
	 * sheet op de gespecificeerde rij afgedrukt.
	 * @param sheet
	 * @param wedstrijd
	 * @param row
	 */
	private void exportWedstrijd(XSSFSheet sheet, Wedstrijd wedstrijd, int row) {
		updateCell(sheet, row, 3, wedstrijd.getWit().getNaam());
		updateCell(sheet, row, 4, "-");
		updateCell(sheet, row, 5, wedstrijd.getZwart().getNaam());
	}
	/**
	 * Update a single cell in the Excel Sheet. The cell is specified by its row and
	 * column. Row and column numbers start with 0, so column A equals 0, column B
	 * equals 1, etc.
	 * @param sheet The Excel sheet to update
	 * @param row The row number, starting with 0
	 * @param col The column number, staring with 0
	 * @param value THe value to store in the cell
	 */
	private void updateCell(XSSFSheet sheet, int row, int col, String value) {
		Cell cell = null;

		// Retrieve the row and create when not valid
		XSSFRow sheetrow = sheet.getRow(row);
		if (sheetrow == null) {
			sheetrow = sheet.createRow(row);
		}
		// Retrieve the correct cell from the column
		cell = sheetrow.getCell(col);
		if (cell == null) {
			cell = sheetrow.createCell(col);
		}
		// Update the value of cell
		cell.setCellValue(value.trim());
	}
}
