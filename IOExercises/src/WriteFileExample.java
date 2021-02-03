import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WriteFileExample {
    public static void main(String[] args) {
        // open the file
        BufferedWriter outfile = null;
        try{
            outfile = new BufferedWriter(new FileWriter("stuff.txt"));
        }
        catch (IOException e){
            System.out.println("Error: File cannot be opened");
        }

        String s = "The answer to the ultimate question of life, the universe, and everything is:";
        int fourtytwo = 42;

        try {
            outfile.write(s + "\n");
            outfile.write(Integer.toString(fourtytwo));
            outfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
