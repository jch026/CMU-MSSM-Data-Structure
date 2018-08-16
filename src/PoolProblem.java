import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class Interval{
	private int beg, end;
	
	public int getBeg() {
		return beg;
	}

	public void setBeg(int beg) {
		this.beg = beg;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	Interval(){
		beg = 0;
		end = 0;
	}

	Interval(int BegRange, int EndRange){
		beg = BegRange;
		end = EndRange;
	}
	
	public Interval IntervalCopy() {
		Interval i = new Interval();
		i.setBeg(beg);
		i.setEnd(end);
		return i;
	}
	
	@Override
	public String toString() {
		return "{" + beg + "," + end + "}";
	}
}

public class PoolProblem {
	
	/************************************************************************************
	 * Read input file and return data in ArrayList of Intervals
	 ************************************************************************************/
	public static ArrayList<Interval> ReadCSV(File filename) {

		String line = null;
		ArrayList<Interval> ListIntervals = new ArrayList<Interval>();
				
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			int size = Integer.parseInt(br.readLine());

			for(int l = 0; l < size; l++) {
				line = br.readLine();
				
				if(line != null) {
					String range[] = line.split(" ");
					int f = Integer.parseInt(range[0]);
					int s = Integer.parseInt(range[1]);
					
					Interval i = new Interval(f,s);
					ListIntervals.add(i);
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println("Can't find file: " + filename.toString());

		} catch (IOException e) {
			System.out.println("Unable to read file: " + filename.toString());
		}
		
		return ListIntervals;
	}
	
	/************************************************************************************
	 * Write result to a specified output file 
	 ************************************************************************************/
	public static void WriteCSV(int result, File output) {
		String StringResult = Integer.toString(result);
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(output))) {
			bw.write(StringResult);

		} catch (IOException e) {
			System.out.println("Unable to read file: " + output.toString());
		}
	}
	
	/************************************************************************************
	 * Calculate Max Time by adding the difference of every interval in the list
	 ************************************************************************************/
	public static int AddTime(ArrayList<Interval> ListIntervals) {
		
		int maxtime = 0;
		int intervaltime = 0;
		
		for(Interval a: ListIntervals) {
			intervaltime = a.getEnd() - a.getBeg();
			maxtime += intervaltime;
		}
		
		return maxtime;
	}
	
	/************************************************************************************
	 * Merge Function used to address overlapping of intervals
	 ************************************************************************************/
	public static ArrayList<Interval> MergeIntervals(ArrayList<Interval> ListIntervals) {
		
		Collections.sort(ListIntervals, (a,b) -> a.getBeg() - b.getBeg());
        Interval base = ListIntervals.get(0);
        int beg = base.getBeg();
        int end = base.getEnd();

        ArrayList<Interval> NewList = new ArrayList<Interval>();

        for (int i = 1; i < ListIntervals.size(); i++) {
            Interval current = ListIntervals.get(i);
            
            if (current.getBeg() <= end) {
                end = Math.max(current.getEnd(), end);
                
            } else {
                NewList.add(new Interval(beg, end));
                beg = current.getBeg();
                end = current.getEnd();
            }
        }

        NewList.add(new Interval(beg, end));
        return NewList;
	}
	
	/************************************************************************************
	 * Take in intervals of time and return the maximum time covered after removing the 
	 * interval with minimum impact
	 ************************************************************************************/
	
	public static int FindBestTime (File file) {
		
		ArrayList<Interval> ListIntervals = new ArrayList<Interval>();
		ListIntervals = ReadCSV(file);
		Collections.sort(ListIntervals, (a,b) -> a.getBeg() - b.getBeg());
		
		//Used for calculating max time of unaltered, original list
		int OriginalTime;
		ArrayList<Interval> FullList = new ArrayList<Interval>();
		
		for(Interval a : ListIntervals) {
			FullList.add(a.IntervalCopy());
		}
		Collections.sort(FullList, (a,b) -> a.getBeg() - b.getBeg());
		FullList = MergeIntervals(FullList);
		OriginalTime = AddTime(FullList);
		
		
		//Implementation to collect unique shifts of each employee
		int OverlapTo = ListIntervals.get(0).getBeg(); 
		int overlap = 0;
		int unique;
		int[] distinct = new int[ListIntervals.size()];
		
		for(int i = 0; i < ListIntervals.size(); i++) {

			unique = 0;	
	        Interval curr = ListIntervals.get(i);
	        int beg = curr.getBeg();
	        int end = curr.getEnd();
	        
			//When it reaches the last employee
			if(i == ListIntervals.size() - 1) {
				unique = end - beg - overlap;
				unique = Math.max(unique, 0);
				distinct[i] = unique;
				
			} else {
				Interval next = ListIntervals.get(i+1);
		        int nextbeg = next.getBeg();
		        int nextend = next.getEnd();
		        	        
		        //If next Intervals are completely covered within current Intervals range e.g. (4,10) (5,7)
		        if((nextend <= end) || (end == nextend)) {
		        	return OriginalTime;
		        }
				
				//Calculate the unique time interval
				if(nextbeg < OverlapTo) {
					overlap = end - OverlapTo + overlap;	
				} else {
					overlap = end - nextbeg + overlap;
					overlap = Math.max(overlap, 0);
				}
				
				//Store Interval's distinct time covered into array
				unique = end - beg - overlap;  
				unique = Math.max(unique, 0);
				distinct[i] = unique;
				
				if (nextbeg < OverlapTo) {
					overlap = end - OverlapTo;
				} else {
					overlap = end - nextbeg;
					overlap = Math.max(overlap, 0);
				}
				
				OverlapTo = end;
			}
		}
		//Go through the list of distinct time shifts covered by each employee and return the calculated max time possible
		int maxtime = 0;
		for(int x : distinct) {
			maxtime = Math.max(maxtime, OriginalTime - x);
		}
		
		return maxtime;
	}


/***************************************************************************/	

	public static void main(String[] args) {
		
		File input1 = new File("1.in");
		File output1 = new File("1.out");
		int result1 = FindBestTime(input1);
		WriteCSV(result1, output1);
	
		File input2 = new File("2.in");
		File output2 = new File("2.out");
		int result2 = FindBestTime(input2);
		WriteCSV(result2, output2);

		File input3 = new File("3.in");
		File output3 = new File("3.out");
		int result3 = FindBestTime(input3);
		WriteCSV(result3, output3);

		File input4 = new File("4.in");
		File output4 = new File("4.out");
		int result4 = FindBestTime(input4);
		WriteCSV(result4, output4);
		
		File input5 = new File("5.in");
		File output5 = new File("5.out");
		int result5 = FindBestTime(input5);
		WriteCSV(result5, output5);
	
		File input6 = new File("6.in");
		File output6 = new File("6.out");
		int result6 = FindBestTime(input6);
		WriteCSV(result6, output6);
		
		File input7 = new File("7.in");
		File output7 = new File("7.out");
		int result7 = FindBestTime(input7);
		WriteCSV(result7, output7);
		
		File input8 = new File("8.in");
		File output8 = new File("8.out");
		int result8 = FindBestTime(input8);
		WriteCSV(result8, output8);
		
		File input9 = new File("9.in");
		File output9 = new File("9.out");
		int result9 = FindBestTime(input9);
		WriteCSV(result9, output9);
		
		File input10 = new File("10.in");
		File output10 = new File("10.out");
		int result10 = FindBestTime(input10);
		WriteCSV(result10, output10);
	
	}

}
