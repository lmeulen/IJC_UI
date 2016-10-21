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
 * - ...
 */

package nl.detoren.ijc.ui.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class minimizetriagonal {

    private static int iterations;
    private static int order[];
    private static int swaps[][];
	private static int A[][];
	
	private final static Logger logger = Logger.getLogger(minimizetriagonal.class.getName());
    
    public static int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        minimizetriagonal.iterations = iterations;
    }

	public static int[] getOrder() {
		return order;
	}

	public void setOrder(int order[]) {
		minimizetriagonal.order = order;
	}

	public static int[][] getSwaps() {
		return swaps;
	}

	public void setSwaps(int swaps[][]) {
		minimizetriagonal.swaps = swaps;
	}

	public static int[][] getA() {
        return A;
    }

    public void setA(int A[][]) {
        minimizetriagonal.A = A;
    }

    public static int getADimensionx() {
        return A.length;
    }

    public static int getADimensiony() {
        return A[0].length;
    }

    public void Iterminimizetriagonal(){
		int cont, contsum =0;
		int i = 0;
		do
    	{
    		i++;
    		cont = minimize();
    		contsum +=cont;
    		System.out.printf("Iteration nr. %d with %d swaps\n", i, cont);
    	} while (cont>0 && i<getIterations());
		if (cont==0) {
			System.out.printf("Iteration completed in %d iteration and in %d swaps.\n", i, contsum);
		} else {
			System.out.printf("Iteration failed. No convergence within maximum of %d iterations.\n", minimizetriagonal.getIterations());
			logger.log(Level.SEVERE, "Iteration failed. No convergence within maximum of %d iterations.\n", minimizetriagonal.getIterations());    		
		}
    	return;
    }
	
	public static int gettrio(int M[][], int indexrow)
	// With minimized matrix get minimized trio
	{
		int trio = 0;
		int triosom = 1000000;
		if ( (M.length & 1) == 0 ) {
			trio = 0; // Dit zorgt er voor dat er geen trio plaatsvindt.
		}
		else {
			for (int i = 1; i < M.length-1; i+=2){
				int somt = 0;
				for (int j=(Math.max(i-1, 0)); j<=Math.min(i+1, M.length);j++){
					if (!(i==j)){
						System.out.printf("M[%d][%d] is %d \n", i,j,M[i][j+indexrow]);
						System.out.printf("M[%d][%d] is %d \n", j,i,M[j][i+indexrow]);
						somt += M[i][j+indexrow] + M[j][i+indexrow];

					}
				}
				System.out.printf("somt is %d \n", somt);
				if (somt < triosom) {
					triosom = somt;
					trio = i;
				}
			}
		}
		System.out.printf("Trio gevonden op %d, %d en %d \n", trio-1, trio, trio+1);
		return trio;
	}

    public static int minimize ()
    // Solves best pairing by minimizing matrix triagonal 
    {
    	int i, j, k;
    	int tri[][] = new int[getADimensionx()][getADimensiony()];
    	int val;
    	int sumo = 0; // som van omliggende waarden rond j en k die in de sub- en superdiagonaal liggen.
    	int sumk = 0; // som van omliggende waarden rond j en k die in de sub- en superdiagonaal zouden liggen na kolom en rij swap.
    	int trisum1 = 0; // som van de mini matrix voor swap.
    	int trisum2 = 0; // som van de mini matrix na swap.
    	int swapped = 0;
        System.out.print("Starting minimizing process\n");
        int indexrow = 1;
        tri = getA();
        //System.out.printf(	"triagonal sum is %d.\n", Utils.triagonalsum(tri,indexrow));
    	for (k = 0; k < tri.length-1; k++)
    	{
    		for (j = (k+1); j < tri.length; j++)
    		{
				trisum1 = 0;
				trisum2 = 0;
				sumo = 0;	
				sumk = 0;
    			int oldtrisum = Utils.triagonalsum(tri,indexrow);
    			//Utils.printMatrix(tri);
    			if ((j-k)>1)
    			{
    				// Simply adding up of the swapped cells will tell what if the swap is lucrative.
    				if ((k-1)>=0)
    				{ 
    					//System.out.print("sumo index1 = " + (k-1) + ", index2 = " + (indexrow+k) + " = " + tri[k-1][indexrow+k] + "\n ");
    					//System.out.print("sumo index1 = " + k + ", index2 = " + (indexrow+k-1) + " = " + tri[k][indexrow+k-1] + "\n ");
    					sumo += tri[k-1][indexrow+k]+tri[k][indexrow+k-1];
    					//System.out.print("										sumk index1 = " + j + ", index2 = " + (indexrow+k-1) + " = " + tri[j][indexrow+k-1] + "\n ");
    					//System.out.print("										sumk index1 = " + (k-1) + ", index2 = " + (indexrow+j) + " = " + tri[k-1][indexrow+j] + "\n ");
    					sumk += tri[j][indexrow+k-1]+tri[k-1][indexrow+j];
    				}
    				if ((j+1)<tri.length)
    				{
    					//System.out.print("sumo index1 = " + (j+1) + ", index2 = " + (indexrow+j) + " = " + tri[j+1][indexrow+j] + "\n ");
    					//System.out.print("sumo index1 = " + j + ", index2 = " + (indexrow+j+1) + " = " + tri[j][indexrow+j+1] + "\n ");
    					sumo += tri[j+1][indexrow+j]+tri[j][indexrow+j+1];
    					//System.out.print("										sumk index1 = " + (j+1) + ", index2 = " + (indexrow+k) + " = " + tri[j+1][indexrow+k] + "\n ");
    					//System.out.print("										sumk index1 = " + k + ", index2 = " + (indexrow+j+1) + " = " + tri[k][indexrow+j+1] + "\n ");
    					sumk += tri[j+1][indexrow+k]+tri[k][indexrow+j+1];	
    				}
    				//System.out.print("sumo index1 = " + (j-1) + ", index2 = " + (indexrow+j) + " = " + tri[j-1][indexrow+j] + "\n ");
    				//System.out.print("sumo index1 = " + j + ", index2 = " + (indexrow+j-1) + " = " + tri[j][indexrow+j-1] + "\n ");
    				//System.out.print("sumo index1 = " + k + ", index2 = " + (indexrow+k+1) + " = " + tri[k][indexrow+k+1] + "\n ");
    				//System.out.print("sumo index1 = " + (k+1) + ", index2 = " + (indexrow+k) + " = " + tri[k+1][indexrow+k] + "\n ");
    				sumo += tri[j-1][indexrow+j] + tri[j][indexrow+j-1] + tri[k][indexrow+k+1] + tri[k+1][indexrow+k];		
    				//System.out.print("										sumk index1 = " + j + ", index2 = " + (indexrow+k+1) + " = " + tri[j][indexrow+k+1] + "\n ");
    				//System.out.print("										sumk index1 = " + (j-1) + ", index2 = " + (indexrow+k) + " = " + tri[j-1][indexrow+k] + "\n ");
    				//System.out.print("										sumk index1 = " + k + ", index2 = " + (indexrow+j-1) + " = " + tri[k][indexrow+j-1] + "\n ");
    				//System.out.print("										sumk index1 = " + (k+1) + ", index2 = " + (indexrow+j) + " = " + tri[k+1][indexrow+j] + "\n ");
    				sumk += tri[j][indexrow+k+1] + tri[j-1][indexrow+k] + tri[k][indexrow+j-1] + tri[k+1][indexrow+j];

    				//System.out.print("Sumo is %d \n", sumo);
    				//System.out.print("Sumk is %d \n", sumk);
    			} else
    			{
    				/* Simply adding up won't work here. Because of the cells next to each other. It gets mixed up. We need to do a real swap on a projected mini matrix of 4x4.
    				   Or if on the side a 3x3 matrix will do.
    				   Filling the matrix
    				*/
    				//
    				// Filling the matrix	
    				int minp = Math.max(0, k-1);
    				int maxp = Math.min(tri.length-1, j+1);
    				int B[][] = new int[maxp-minp+1][maxp-minp+1]; 
    				int pmin = Math.min(0, Math.max(0, k-1));
    				int pmax = maxp-minp;
    				//System.out.printf("Matrix tri voor B.\n");
    				//Utils.printMatrix(tri);
    				// System.out.printf("k=%d, j=%d, dimB=(%d,%d), pmin=%d, pmax=%d \n", k,j,maxp-minp+1,maxp-minp+1,pmin,pmax);
    				//System.out.printf("Vergelijken rij/kolom %d en %d - ", k,j);
    				for (int p=pmin; p<=pmax;p++) {
    					for (int q=pmin; q<=pmax;q++) {
    						//System.out.printf("tri[%d][%d] is %d - ", p+minp,indexrow+q+minp,tri[p+minp][indexrow+q+minp]);
    						B[p][q] = tri[p+minp][indexrow+q+minp];
    						// System.out.printf("B[%d][%d] is %d \n", p,q,B[p][q]);
    					}
    				}
    				//System.out.printf("Matrix B.\n");
    				//Utils.printMatrix(B);
    				trisum1 = Utils.triagonalsum(B);
    				// Swapping the row/column;
    				/* swap row */	
					int u;
					int v;
						//if ((k==0) || (j==B.length-1)) {
					if ((k==0)) {
							u=0;
							v=1;
					} else {
						u=1;
						v=2;
					}
    				for (int p = 0; p < B.length; p++) {
      			  		//System.out.print("p is %d \n", p);
        			  	val = B[u][p];
        			  	B[u][p] = B[v][p];
        			  	B[v][p] = val;
    				}
          			/* swap column */		  
    				for (int p = 0; p < B.length; p++) {
      			  		//System.out.print("p is %d \n", p);
      			  		val = B[p][u];
      			  		B[p][u] = B[p][v];
      			  		B[p][v] = val;
    				}
    				//System.out.printf("Matrix B.\n");
    				//Utils.printMatrix(B);
    				trisum2 = Utils.triagonalsum(B);
    				//System.out.printf("Trisum of minimatrix before swap is %d \n", trisum1);
    				//System.out.printf("Trisum of minimatrix after swap is %d \n", trisum2);
    			}
    			if ((sumk < sumo) || (trisum2 < trisum1)){
    			  /* swapping row/column is lucrative. DO swap
    			  */ 
    				if (sumk < sumo) {
    					// System.out.printf("Swapping lucrative! Old tinyblock sum is %d, new tinyblock sum is %d\n", sumo, sumk);
    				}
    			    //System.out.printf("Swapping lucrative! exchanging row/column (0-length) %d and %d \n", k, j);
    			  /* swap row */
    			  for (i = 0; i < A[0].length; i++) {
    			    //System.out.print("i is %d \n", i);
    			  	val = tri[k][i];
    			  	tri[k][i] = tri[j][i];
    			  	tri[j][i] = val;
    			  /* swap column */	
    		  	  }		  
    			  for (i = 0; i < A.length; i++) {
    			  	//System.out.print("i is %d \n", i);
    			  	val = tri[i][indexrow+k];
    			  	tri[i][indexrow+k] = tri[i][indexrow+j];
    			  	tri[i][indexrow+j] = val;
    		  	  }
    			  //System.out.print(	"Row/Column swapped.\n");
  				  //System.out.printf("Matrix tri.\n");
    			  //Utils.printMatrix(tri);
  				  int newtrisum = Utils.triagonalsum(tri,indexrow);
  				  if (oldtrisum<newtrisum) {
  					  //System.out.printf("BUG! New triagonal sum is %d is greater than old triagonal sum %d !!!\n", newtrisum, oldtrisum);
  					  logger.log(Level.SEVERE, "BUG! New triagonal sum is "+ newtrisum + " is greater than old triagonal sum " + oldtrisum + " !!!\n");
  				  }
  				  
    			  //System.out.printf("New triagonal sum is %d.\n", newtrisum);
    		  	 // val=order[k];
    		  	  //order[indexrow+k]=order[j];
    		  	  //order[indexrow+j]=val;
    		  	  swapped++;;
    			} else {
    			  /* swapping row/column is not lucrative. DONT swap
    			  */
    				// System.out.printf("Swapping %d and %d not lucrative! Old tinyblock sum is %d, new tinyblock sum is %d\n", k, j, sumo, sumk);
    				// System.out.printf("Swapping %d and %d not lucrative! triagonal sum is still %d \n", k, j, Utils.triagonalsum(tri,indexrow));
    		  	}
    		}
    	}
    	return swapped;
   	}
}

