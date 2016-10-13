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

public class minimizetriagonal {

    private static int iterations;
    private static int order[];
	private static int A[][];
    
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

    public static int[][] getA() {
        return A;
    }

    public void setA(int A[][]) {
        minimizetriagonal.A = A;
    }

    public static int getADimension() {
        return A.length;
    }

	public void Iterminimizetriagonal(){
		boolean cont;
		int i = 0;
		do
    	{
    		i++;
    		System.out.printf("Iteration nr. %d\n", i);
    		cont = minimize();
    	} while (cont && i<getIterations());
		if (!cont) {
			System.out.printf("Iteration completed in %d times.\n", i);
		} else {
			System.out.printf("Iteration failed. No convergence within maximum of %d iterations.\n", minimizetriagonal.getIterations());
		}
    	return;
    }
	
	public static int gettrio(int M[][])
	// With minimized matrix get minimized trio
	{
		int trio = 0;
		int triosom = 100000;
		if ( (M.length & 1) == 0 ) {
			trio = 0; // Dit zorgt er voor dat er geen trio plaatsvindt.
		}
		else {
			for (int i = 1; i < M.length-1; i+=2){
				int somt = 0;
				for (int j=(Math.max(i-1, 0)); j<=Math.min(i+1, M.length);j++){
					if (!(i==j)){
						somt += M[i][j] + M[j][i];
					}
				}
				if (somt < triosom) {
					triosom = somt;
					trio = i;
				}
			}
		}
		return trio;
	}
    
    public static boolean minimize ()
    // Solves best pairing by minimizing matrix triagonal 
    {
    	int i, j, k, t;
    	int tri[][] = new int[getADimension()][getADimension()];
    	int val;
    	int sumo;
    	int sumk;
    	boolean swapped = false;
        System.out.print(	"Starting minimizing process\n");
        int[] order = getOrder();
        tri = getA();
    	for (k = 0; k < A.length; k++)
    	{
    		for (j = k; j < A.length; j++)
    		//for (j = 0; j < A.length; j++)
    		{
    			sumo = 0;
    			sumk = 0;
    			for (t = Math.max(0,k-1); t < Math.min(A.length,k+1); t++)
    			{
    				sumo += tri[t][k] + tri[k][t];
    				sumk += tri[j][t] + tri[t][j];
    			}
    			for (t = Math.max(0,j-1); t < Math.min(A.length,j+1); t++)
    			{
    				sumo += tri[t][j] + tri[j][t];
    				sumk += tri[k][t] + tri[t][k];
    			}
    			//System.out.print("Sumo is %d \n", sumo);
    			//System.out.print("Sumk is %d \n", sumk);
    			if (sumk < sumo){
    			  /* swapping row/column is lucrative. DO swap
    			  */
    				System.out.printf("Swapping lucrative! Old tinyblock sum is %d, new tinyblock sum is %d\n", sumo, sumk);
    				System.out.printf("Swapping lucrative! exchanging row/column %d and %d \n", j, k);
    			  /* swap row */
    			  for (i = 0; i < A.length; i++) {
    			    //System.out.print("i is %d \n", i);
    			  	val = tri[k][i];
    			  	tri[k][i] = tri[j][i];
    			  	tri[j][i] = val;
    			  /* swap column */	
    		  	  }		  
    			  for (i = 0; i < A.length; i++) {
    			  	//System.out.print("i is %d \n", i);
    			  	val = tri[i][k];
    			  	tri[i][k] = tri[i][j];
    			  	tri[i][j] = val;
    		  	  }
    		  	  val=order[k];
    		  	  order[k]=order[j];
    		  	  order[j]=val;
    		  	  swapped = true;
    			} else {
    			  /* swapping row/column is not lucrative. DONT swap
    			  */
    			  //System.out.print("Swapping not lucrative!\n");
    		  	}
    		  }
    	}
    	return swapped;
    }

}
