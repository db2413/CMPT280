import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

// For reading a txt file of lines. Each line contains four distinct data values. i'th values of each line are of same type
// Tabular file reading
public class TempRecord implements Cloneable {

    String month;
    String year;
    float minTemp;
    float maxTemp;

    public TempRecord(String month, String year, float minTemp, float maxTemp){
        this.month = month;
        this.year = year;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
    }


    @Override
    public String toString() {
        return "TempRecord{" +
                "month='" + month + '\'' +
                ", year='" + year + '\'' +
                ", minTemp=" + minTemp +
                ", maxTemp=" + maxTemp +
                '}';
    }

    public static void main(String[] args) {
        //  Open File
        Scanner infile = null;
        try{
            infile = new Scanner(new File("temperature.txt"));
        }catch (FileNotFoundException e) {
            System.out.println("Error, file not found");
        }

        //  Read temp records
        ArrayList<TempRecord> tempRecords = new ArrayList<>();
        TempRecord record = null;
        while (infile.hasNextLine()){
            try {
                tempRecords.add(new TempRecord(infile.next(), infile.next(), infile.nextFloat(), infile.nextFloat()));
            }
            catch (InputMismatchException e){
                System.out.println("Error: Bad temp record line input");
                // Clear this line from infile cursor.
                infile.nextLine();
            }
        }

        for (TempRecord i : tempRecords ) {
            System.out.println(i.toString());
        }
    }
}
