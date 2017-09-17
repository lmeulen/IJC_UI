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

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.wedstrijden.Groepswedstrijden;
import nl.detoren.ijc.data.wedstrijden.Serie;
import nl.detoren.ijc.data.wedstrijden.Wedstrijd;
import nl.detoren.ijc.data.wedstrijden.Wedstrijden;
import nl.detoren.ijc.ui.view.FoutMelding;

/**
 * Sla het wedstrijdschema op in Excel
 *
 * @author Leo van der Meulen
 *
 */
public class OutputExcel implements WedstrijdenExportInterface {

	private final static Logger logger = Logger.getLogger(OutputExcel.class.getName());

	/**
	 * Exporteer een wedstrijd naar Excel. Wedstrijd wordt in gespecificeerde
	 * sheet op de gespecificeerde rij afgedrukt.
	 *
	 * @param sheet
	 * @param wedstrijd
	 * @param row
	 */
	private void exportWedstrijd(XSSFSheet sheet, Wedstrijd wedstrijd, int row) {
		updateCell(sheet, row, 3, wedstrijd.getWit().getNaam());
		updateCell(sheet, row, 4, "-");
		updateCell(sheet, row, 5, wedstrijd.getZwart().getNaam());
		logger.log(Level.INFO, wedstrijd.getWit().getNaam() + " - " + wedstrijd.getZwart().getNaam());
		borderFull(getCell(sheet, row, 7));
		borderFull(getCell(sheet, row, 8));
	}

	/**
	 * Update a single cell in the Excel Sheet. The cell is specified by its row
	 * and column. Row and column numbers start with 0, so column A equals 0,
	 * column B equals 1, etc.
	 *
	 * @param sheet
	 *            The Excel sheet to update
	 * @param row
	 *            The row number, starting with 0
	 * @param col
	 *            The column number, staring with 0
	 * @param value
	 *            THe value to store in the cell
	 */
	private void updateCell(XSSFSheet sheet, int row, int col, String value) {
		Cell cell = getCell(sheet, row, col);
		cell.setCellValue(value.trim());
	}

	private Cell getCell(XSSFSheet sheet, int row, int col) {
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
		return cell;
	}

	private void borderFull(Cell cell) {
		if (cell != null) {
			Workbook wb = cell.getRow().getSheet().getWorkbook();
			CellStyle style = wb.createCellStyle();
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderTop(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			cell.setCellStyle(style);
		}
	}

	private void borderLeft(Cell cell) {
		if (cell != null) {
			Workbook wb = cell.getRow().getSheet().getWorkbook();
			CellStyle style = wb.createCellStyle();
			style.setBorderLeft(BorderStyle.THIN);
			cell.setCellStyle(style);
		}
	}

	private void borderRight(Cell cell) {
		if (cell != null) {
			Workbook wb = cell.getRow().getSheet().getWorkbook();
			CellStyle style = wb.createCellStyle();
			style.setBorderRight(BorderStyle.THIN);
			cell.setCellStyle(style);
		}
	}

//	private void borderTop(Cell cell) {
//		if (cell != null) {
//			Workbook wb = cell.getRow().getSheet().getWorkbook();
//			CellStyle style = wb.createCellStyle();
//			style.setBorderTop(BorderStyle.THIN);
//			cell.setCellStyle(style);
//		}
//	}

	private void borderBottom(Cell cell) {
		if (cell != null) {
			Workbook wb = cell.getRow().getSheet().getWorkbook();
			CellStyle style = wb.createCellStyle();
			style.setBorderBottom(BorderStyle.THIN);
			cell.setCellStyle(style);
		}
	}

	private void borderLeftBottom(Cell cell) {
		if (cell != null) {
			Workbook wb = cell.getRow().getSheet().getWorkbook();
			CellStyle style = wb.createCellStyle();
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			cell.setCellStyle(style);
		}
	}

	private void borderRightBottom(Cell cell) {
		if (cell != null) {
			Workbook wb = cell.getRow().getSheet().getWorkbook();
			CellStyle style = wb.createCellStyle();
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			cell.setCellStyle(style);
		}
	}

	/**
	 * Create the Excel version of the sheet Original Empty file is stored in
	 * Empty.xlsx Create version with round matches is stored in Indeling.xlsx
	 *
	 * @param wedstrijden
	 *            The round to store in the Excel file
	 */
	public boolean export(Wedstrijden wedstrijden) {
		try {
			logger.log(Level.INFO, "Wedstrijden wegschrijven naar Excel");

			int periode = wedstrijden.getPeriode();
			int ronde = wedstrijden.getRonde();
			String rpString = "Periode " + periode + ", Ronde " + ronde;
			String datum = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(Calendar.getInstance().getTime());

			// Open the empty schedule file, matches are stored in the
			// second sheet (id = 1)
			FileInputStream file = new FileInputStream("Template.xlsx");
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
				int currentRow = 5;
				for (int s = 0; s < nrSeries; ++s) { // For each serie
					Serie serie = gw.getSerie(s);
					updateCell(sheet, currentRow, 2, "Serie " + (s + 1));
					borderLeft(getCell(sheet, currentRow, 1));
					borderLeft(getCell(sheet, currentRow+1, 1));
					borderRight(getCell(sheet, currentRow, 9));
					borderRight(getCell(sheet, currentRow+1, 9));
					currentRow += 2;
					for (Wedstrijd w : serie.getWedstrijden()) {
						exportWedstrijd(sheet, w, currentRow);
						borderLeft(getCell(sheet, currentRow, 1));
						borderLeft(getCell(sheet, currentRow+1, 1));
						borderRight(getCell(sheet, currentRow, 9));
						borderRight(getCell(sheet, currentRow+1, 9));
						currentRow += 2;
					}
				}
				// Export trio
				ArrayList<Wedstrijd> trio = gw.getTriowedstrijden();
				if (trio != null && trio.size() > 0) {
					updateCell(sheet, currentRow, 2, "Trio");
					borderLeft(getCell(sheet, currentRow, 1));
					borderLeft(getCell(sheet, currentRow+1, 1));
					borderRight(getCell(sheet, currentRow, 9));
					borderRight(getCell(sheet, currentRow+1, 9));
					currentRow += 2;
					for (Wedstrijd w : trio) {
						exportWedstrijd(sheet, w, currentRow);
						borderLeft(getCell(sheet, currentRow, 1));
						borderLeft(getCell(sheet, currentRow+1, 1));
						borderRight(getCell(sheet, currentRow, 9));
						borderRight(getCell(sheet, currentRow+1, 9));
						currentRow += 2;
					}
				}
				currentRow--;
				for (int j = 2; j <= 8; j++)
					borderBottom(getCell(sheet, currentRow,j));
				borderLeftBottom(getCell(sheet, currentRow,1));
				borderRightBottom(getCell(sheet, currentRow,9));
			}
			// Close input file
			file.close();
			// Store Excel to new file
			String dirName = "R" + periode + "-" + ronde;
			new File(dirName).mkdirs();
			String filename = dirName + File.separator + "Indeling " + periode + "-" + ronde + ".xlsx";
			File outputFile = new File(filename);
			FileOutputStream outFile = new FileOutputStream(outputFile);
			workbook.write(outFile);
			// Close output file
			workbook.close();
			outFile.close();
			// And open it in the system editor
			Desktop.getDesktop().open(outputFile);
			return true;
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error writing output: " + e.toString());
			FoutMelding.melding("Fout bij opslaan Excel bestand: " + e.getMessage());
			return false;
		}
	}

}
