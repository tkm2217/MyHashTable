package hashing;


/**
 * A class that will be used to display the lines of text that are corrected.
 *
 */
public class LinesToDisplay {

    public static final int LINES = 10;     // Display 10 lines
    private AList<Wordlet>[] lines;
    private int currentLine;

    /**
     * Constructor for objects of class LinesToDisplay
     */
    public LinesToDisplay() {
        //ADD CODE FOR THE CONSTRUCTOR
//>>>>>>>>>>> ADDED CODE >>>>>>>>>>>>>>>>>>>>>>        
        lines = (AList<Wordlet>[]) new AList[LINES + 1];
        lines[0] = new AList<>();
        currentLine = 0;
//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    }

    /**
     * Add a new wordlet to the current line.
     *
     */
    public void addWordlet(Wordlet w) {
        //ADD CODE HERE TO ADD A WORDLET TO THE CURRENT LINE

//>>>>>>>>>>> ADDED CODE >>>>>>>>>>>>>>>>>>>>>>        
        lines[currentLine].add(w);
//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    }

    /**
     * Go to the next line, if the number of lines has exceeded LINES, shift
     * them all up by one
     *
     */
    public void nextLine() {
        //ADD CODE TO HANDLE THE NEXT LINE
//>>>>>>>>>>> ADDED CODE >>>>>>>>>>>>>>>>>>>>>>        
        if (currentLine < LINES) {
            currentLine++;
        } else {
            for (int i = 0; i < LINES; i++) {
                // move each line down by one
                lines[i] = lines[i + 1];
            }  // end for
        }
        // always start a new list
        lines[currentLine] = new AList<Wordlet>();
//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    }

      
    public int getCurrentLine(){
        return currentLine;
    }
    
    public AList<Wordlet>[] getLines(){
        return lines;
    }
}
