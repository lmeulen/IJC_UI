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
 * - ... 
 * - ...
 */

package nl.detoren.ijc.ui.util;

import java.awt.Component;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.TableColumn;

/**
 *
 * @author Leo van der Meulen
 */
public class Utils {
    
    public static void fixedComponentSize(Component c, int width, int height) {
        c.setMinimumSize(new Dimension(width, height));
        c.setMaximumSize(new Dimension(width, height));
        c.setPreferredSize(new Dimension(width, height));
        c.setSize(new Dimension(width, height));
    }
    
    public static void fixedColumSize(TableColumn c, int width) {
        c.setMinWidth(width);
        c.setMaxWidth(width);
    }
    
  //Displays a 2d array in the console, one line per row.
    public static void printMatrix(ArrayList<ArrayList<Integer>> grid) {
        for(int r=0; r<grid.size(); r++) {
           for(int c=0; c<grid.get(r).size(); c++)
               System.out.print(grid.get(r).get(c) + "\t");
           System.out.println();
        }
    }

    public static void printMatrix(int grid[][]) {
        for(int r=0; r<grid.length; r++) {
           for(int c=0; c<grid[0].length; c++)
               System.out.print(grid[r][c] + "\t");
           System.out.println();
        }
    }

    public static void printMatrix(int grid[]) {
        for(int r=0; r<grid.length; r++) {
               System.out.print(grid[r] + " \n");
        }
    }

    public static int[][] add2DArrays(int A[][], int B[][]){
    	// Just for cubic equal size arrays!
    	if (A.length==0) return A;
    	int C[][] = new int[A.length][A.length];
    	for (int i=0;i<A.length;i++) {
    		for (int j=0;j<B.length;j++) {
    			C[i][j]=A[i][j]+B[i][j];
    		}
    	}
    	return C;
    }

    public static int[][] add2DArrays(double mf1, int A[][], double mf2, int B[][]){
    	// Just for [X+1][X] arrays! with index in first row.
    	if (A.length==0) return A;
    	int C[][] = new int[A.length][A[0].length];
    	for (int i=0;i<A.length;i++) {
    		for (int j=0;j<A[0].length;j++) {
    			if (j==0) {
    				// C[i][j]=(int) (A[i][j]+B[i][j]);
    				C[i][j]=(int) (A[i][j]);
    			} else {
    				C[i][j]=(int) (mf1*A[i][j]+mf2*B[i][j]);
    			}
    		}
    	}
    	return C;
    }

    public static int triagonalsum(int A[][]){
    	// Just for [X][X] arrays!
    	if (A.length==0) return 0;
    	int sum = 0;
    	for (int i=0;i<A.length;i++) {
    		for (int j=Math.max(0, i-1);j<Math.min(i+2, A.length);j++) {
    				sum += A[i][j];
    		}
    	}
    	return sum;
    }

    public static int triagonalsum(int A[][], int indexrow){
    	// Just for [X+1][X] arrays! with index (indexrow=1) in first row.
    	int sum = 0;
    	for (int i=0;i<A.length;i++) {
    		for (int j=Math.max(0, i-1);j<Math.min(i+2, A.length);j++) {
    				sum += A[i][indexrow+j];
    		}
    	}
    	return sum;
    }
    
    public static boolean containing(int[] haystack, int needle) {
    	for(int hay: haystack){
    		if(hay == needle)
    			return true;
    	}
    	return false;
    }

    public static boolean containing(ArrayList<Integer> haystack, int needle) {
    	for(int hay: haystack){
    		if(hay == needle)
    			return true;
    	}
    	return false;
    }

    public static int[][] removerowandcolumnfrom2D(int A[][], int[] B, int indexrow) {
    	// Just for cubic equal size arrays!
    	int C[][] = new int[A.length-B.length][A[0].length-B.length];
    	int p = 0;
    	for (int i=0;i<A.length;i++) {
    		int q = 0;
			if (!(Utils.containing(B,A[i][indexrow-1]))) {
				C[p][q]=A[i][0];
				q++;
				for (int j=1;j<A[0].length;j++) {
					if (!(Utils.containing(B,A[j-1][indexrow-1]))) {
						C[p][q]=A[i][j];
						q++;
					}
   				}
				p++;
   			}
    	}
    	return C;
    }

    public static int[][] removerowandcolumnfrom2D(int A[][], ArrayList<Integer> B, int indexrow) {
    	// Just for cubic equal size arrays!
    	int C[][] = new int[A.length-B.size()][A[0].length-B.size()];
    	int p = 0;
    	for (int i=0;i<A.length;i++) {
    		int q = 0;
			if (!(Utils.containing(B,A[i][indexrow-1]))) {
				C[p][q]=A[i][0];
				q++;
				for (int j=1;j<A[0].length;j++) {
					if (!(Utils.containing(B,A[j-1][indexrow-1]))) {
						C[p][q]=A[i][j];
						q++;
					}
   				}
				p++;
   			}
    	}
    	return C;
    }

    public static boolean internet_connectivity(){
    	try {
    		InetAddress adr = InetAddress.getByName("www.google.com");
    		if(adr.isReachable(3000)){
    			return true;
    		} else {
    			return false;
    		}
    	}
    	catch (Exception e) {    	
    		return false;
    	}
    }
    
    public static void stacktrace(Exception ex) {
        StringBuilder sb = new StringBuilder(ex.toString());
        for (StackTraceElement ste : ex.getStackTrace()) {
            sb.append("\n\tat ");
            sb.append(ste);
        }
        String trace = sb.toString();
        
        // Now, trace contains the stack trace
        // (However, you can only use it inside the catch block)
        JOptionPane.showMessageDialog(new JFrame(), trace);

    }
    
    /**
     * Lees een bestand in en retourneer dit als Strings.
     * @param bestandsnaam
     * @return array of strings met bestandsinhoud
     */
    public static String[] leesBestand(String bestandsnaam) {
        List<String> list = new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(bestandsnaam));
            String str;
            while ((str = in.readLine()) != null) {
                list.add(str);
            }
            in.close();
            return list.toArray(new String[0]);
        } catch (IOException ex) {
			//logger.log(Level.INFO, "Lees bestand mislukt " +  ex.getMessage());
        	System.out.println("Exception: " + ex.toString());
            Utils.stacktrace(ex);
        }
        return null;
    }

    public static int vorigePeriode(int perioden, int rondes, int periode, int ronde) {
 	   int vperiode = periode;
	   if (ronde <=1) {
		   vperiode--;		   
	   }
	   if (vperiode < 1)
	   {
		   vperiode = perioden;
	   }
	   return vperiode;
 	   
    }

   public static int vorigeRonde(int perioden, int rondes, int periode, int ronde) {
	   int vronde = ronde;
	   if (vronde <=1) {
		   vronde = rondes;
	   } else {
		   vronde--;
	   }
	   return vronde;
	   
   }

   
}
