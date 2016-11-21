/**
 * Copyright (C) 2016 Lars Dam
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.0
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * See: http://www.gnu.org/licenses/gpl-2.0.html
 *  
 * Problemen in deze code:
 * - ...
 */

package nl.detoren.ijc.ui.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class minimizetriagonal {

	private int iterations;
	private int swaps[][];
	private int A[][];

	private final static Logger logger = Logger.getLogger(minimizetriagonal.class.getName());

	public int getIterations() {
		return iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public int[][] getSwaps() {
		return swaps;
	}

	public void setSwaps(int swaps[][]) {
		this.swaps = swaps;
	}

	public int[][] getA() {
		return A;
	}

	public void setA(int A[][]) {
		this.A = A;
	}

	public int getADimensionx() {
		return A.length;
	}

	public int getADimensiony() {
		if (A.length==0) {
			return 0;
		} else {
			return A[0].length;
		}
	}

	public void Iterminimizetriagonal() {
		int cont, contsum = 0;
		int i = 0;
		do {
			i++;
			cont = minimize();
			contsum += cont;
			System.out.printf("Iteration nr. %d with %d swaps\n", i, cont);
		} while (cont > 0 && i < getIterations());
		if (cont == 0) {
			logger.log(Level.INFO, "Iteration completed in " + i + " iterations");
			logger.log(Level.INFO, "Required " + contsum + " swaps");
		} else {
			logger.log(Level.SEVERE, "Iteration failed. No convergence within maximum of " + getIterations() + " iterations.");
		}
		return;
	}

	public static int gettrio(int M[][], int indexrow)
	// With minimized matrix get minimized trio
	{
		int trio = 0;
		int triosom = 1000000;
		if ((M.length & 1) == 0) {
			trio = 0; // Dit zorgt er voor dat er geen trio plaatsvindt.
		} else {
			for (int i = 1; i < M.length - 1; i += 2) {
				int somt = 0;
				for (int j = (Math.max(i - 1, 0)); j <= Math.min(i + 1, M.length); j++) {
					if (!(i == j)) {
						String result = String.format("M[%d][%d] is %d \n", i, j, M[i][j + indexrow]);
						logger.log(Level.INFO, result);
						result = String.format("M[%d][%d] is %d \n", j, i, M[j][i + indexrow]);
						logger.log(Level.INFO, result);
						somt += M[i][j + indexrow] + M[j][i + indexrow];

					}
				}
				System.out.printf("somt is %d \n", somt);
				if (somt < triosom) {
					triosom = somt;
					trio = i;
				}
			}
		}
		if (trio == 0) {
			logger.log(Level.INFO, "Geen Trio");
		} else {
			logger.log(Level.INFO, String.format("Trio gevonden op %d, %d en %d \n", trio - 1, trio, trio + 1));			}
		return trio;
	}

	public int minimize()
	// Solves best pairing by minimizing matrix triagonal
	{
		int i, j, k;
		int tri[][] = new int[getADimensionx()][getADimensiony()];
		int val;
		int sumo = 0; // som van omliggende waarden rond j en k die in de sub-
						// en superdiagonaal liggen.
		int sumk = 0; // som van omliggende waarden rond j en k die in de sub-
						// en superdiagonaal zouden liggen na kolom en rij swap.
		int trisum1 = 0; // som van de mini matrix voor swap.
		int trisum2 = 0; // som van de mini matrix na swap.
		int swapped = 0;
		logger.log(Level.INFO, "Starting minimizing process\n");
		int indexrow = 1;
		tri = getA();
		for (k = 0; k < tri.length - 1; k++) {
			for (j = (k + 1); j < tri.length; j++) {
				trisum1 = 0;
				trisum2 = 0;
				sumo = 0;
				sumk = 0;
				int oldtrisum = Utils.triagonalsum(tri, indexrow);
				// Utils.printMatrix(tri);
				if ((j - k) > 1) {
					// Simply adding up of the swapped cells will tell what if
					// the swap is lucrative.
					if ((k - 1) >= 0) {
						sumo += tri[k - 1][indexrow + k] + tri[k][indexrow + k - 1];
						sumk += tri[j][indexrow + k - 1] + tri[k - 1][indexrow + j];
					}
					if ((j + 1) < tri.length) {
						sumo += tri[j + 1][indexrow + j] + tri[j][indexrow + j + 1];
						sumk += tri[j + 1][indexrow + k] + tri[k][indexrow + j + 1];
					}
					sumo += tri[j - 1][indexrow + j] + tri[j][indexrow + j - 1] + tri[k][indexrow + k + 1]
							+ tri[k + 1][indexrow + k];
					sumk += tri[j][indexrow + k + 1] + tri[j - 1][indexrow + k] + tri[k][indexrow + j - 1]
							+ tri[k + 1][indexrow + j];
				} else {
					/*
					 * Simply adding up won't work here. Because of the cells
					 * next to each other. It gets mixed up. We need to do a
					 * real swap on a projected mini matrix of 4x4. Or if on the
					 * side a 3x3 matrix will do. Filling the matrix
					 */
					//
					// Filling the matrix
					int minp = Math.max(0, k - 1);
					int maxp = Math.min(tri.length - 1, j + 1);
					int B[][] = new int[maxp - minp + 1][maxp - minp + 1];
					int pmin = Math.min(0, Math.max(0, k - 1));
					int pmax = maxp - minp;
					for (int p = pmin; p <= pmax; p++) {
						for (int q = pmin; q <= pmax; q++) {
							B[p][q] = tri[p + minp][indexrow + q + minp];
						}
					}
					trisum1 = Utils.triagonalsum(B);
					int u;
					int v;
					if ((k == 0)) {
						u = 0;
						v = 1;
					} else {
						u = 1;
						v = 2;
					}
					for (int p = 0; p < B.length; p++) {
						val = B[u][p];
						B[u][p] = B[v][p];
						B[v][p] = val;
					}
					/* swap column */
					for (int p = 0; p < B.length; p++) {
						val = B[p][u];
						B[p][u] = B[p][v];
						B[p][v] = val;
					}
					trisum2 = Utils.triagonalsum(B);
				}
				if ((sumk < sumo) || (trisum2 < trisum1)) {
					 // swapping row/column is lucrative. DO swap
					for (i = 0; i < A[0].length; i++) {
						// System.out.print("i is %d \n", i);
						val = tri[k][i];
						tri[k][i] = tri[j][i];
						tri[j][i] = val;
					}
					for (i = 0; i < A.length; i++) {
						// System.out.print("i is %d \n", i);
						val = tri[i][indexrow + k];
						tri[i][indexrow + k] = tri[i][indexrow + j];
						tri[i][indexrow + j] = val;
					}
					int newtrisum = Utils.triagonalsum(tri, indexrow);
					if (oldtrisum < newtrisum) {
						logger.log(Level.SEVERE, "BUG! New triagonal sum is " + newtrisum
								+ " is greater than old triagonal sum " + oldtrisum + " !!!\n");
					}
					swapped++;
					;
				} else {
					 // swapping row/column is not lucrative. DONT swap
				}
			}
		}
		return swapped;
	}
}
