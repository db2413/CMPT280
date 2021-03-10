import com.opencsv.CSVReader;
import lib280.base.Pair280;
import lib280.hashtable.KeyedChainedHashTable280;
import lib280.list.LinkedList280;
import lib280.tree.OrderedSimpleTree280;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

// This project uses a JAR called opencsv which is a library for reading and
// writing CSV (comma-separated value) files.
// 
// You don't need to do this for this project, because it's already done, but
// if you want to use opencsv in other projects on your own, here's the process:
//
// 1. Download opencsv-3.1.jar from http://sourceforge.net/projects/opencsv/
// 2. Drag opencsv-3.1.jar into your project.
// 3. Right-click on the project in the package explorer, select "Properties" (at bottom of popup menu)
// 4. Choose the "Libraries" tab
// 5. Click "Add JARs"
// 6. Select the opencsv-3.1.jar from within your project from the list.
// 7. At the top of your .java file add the following imports:
//        import java.io.FileReader;
//        import com.opencsv.CSVReader;
//
// Reference documentation for opencsv is here:  
// http://opencsv.sourceforge.net/apidocs/overview-summary.html


public class QuestLog extends KeyedChainedHashTable280<String, QuestLogEntry> {

	public QuestLog() {
		super();
	}
	
	/**
	 * Obtain an array of the keys (quest names) from the quest log.  There is 
	 * no expectation of any particular ordering of the keys.
	 * 
	 * @return The array of keys (quest names) from the quest log.
	 */
	public String[] keys() {
		String[] keysArray = new String[this.count()];
		int i =0;
		for (LinkedList280<QuestLogEntry> list :hashArray ) {
			if (list!=null){
				list.goFirst();
				while (list.itemExists()){
					keysArray[i] = list.item().key();
					list.goForth();
					i++ ;
				}
			}
		}
		return keysArray;  // Remove this line you're ready.  It's just to prevent compiler errors.
	}
	
	/**
	 * Format the quest log as a string which displays the quests in the log in 
	 * alphabetical order by name.
	 * 
	 * @return A nicely formatted quest log.
	 */
	public String toString() {
		String result = "";
		String[] k = keys();
		QuestLogEntry qle = null;
		Arrays.sort(k);
		for (int i = 0; i < k.length; i++) {
			qle = obtain(k[i]);
			result += String.format("%s: %s, Level Range: %s-%s\n",qle.questName,qle.questArea,qle.recommendedMinLevel,qle.recommendedMaxLevel);
		}
		// Remove this line you're ready.  It's just to prevent compiler errors.
		return result;
	}
	
	/**
	 * Obtain the quest with name k, while simultaneously returning the number of
	 * items examined while searching for the quest.
	 * @param k Name of the quest to obtain.
	 * @return A pair in which the first item is the QuestLogEntry for the quest named k, and the
	 *         second item is the number of items examined during the search for the quest named k.
	 *         Note: if no quest named k is found, then the first item of the pair should be null.
	 */
	public Pair280<QuestLogEntry, Integer> obtainWithCount(String k) {
		int count = 0;
		int itemHashLocation = this.hashPos(k);
		if (hashArray[itemHashLocation]==null)
			return new Pair280<>(null,0); // Item is not in hash table

		itemListLocation = hashArray[itemHashLocation].iterator();
		count ++;
		while (!itemListLocation.after() && k.compareTo(itemListLocation.item().key()) != 0 ) {
			count ++;
			itemListLocation.goForth();
		}
		if (itemListLocation.itemExists()){
			return new Pair280<>(itemListLocation.item(),count); // Found the item!
		}
		return new Pair280<>(null,count); // Could not find the item in the hash table
	}
	
	
	public static void main(String args[])  {
		// Make a new Quest Log
		QuestLog hashQuestLog = new QuestLog();

		// Make a new ordered binary lib280.tree.
		OrderedSimpleTree280<QuestLogEntry> treeQuestLog =
				new OrderedSimpleTree280<QuestLogEntry>();


		// Read the quest data from a CSV (comma-separated value) file.
		// To change the file read in, edit the argument to the FileReader constructor.
		CSVReader inFile;
		try {
			//input filename on the next line - path must be relative to the working directory reported above.
			inFile = new CSVReader(new FileReader("questLog/quests1000.csv"));
		} catch (FileNotFoundException e) {
			System.out.println("Error: File not found.");
			return;
		}

		String[] nextQuest;
		try {
			// Read a row of data from the CSV file
			while ((nextQuest = inFile.readNext()) != null) {
				// If the read succeeded, nextQuest is an array of strings containing the data from
				// each field in a row of the CSV file.  The first field is the quest name,
				// the second field is the quest region, and the next two are the recommended
				// minimum and maximum level, which we convert to integers before passing them to the
				// constructor of a QuestLogEntry object.
				QuestLogEntry newEntry = new QuestLogEntry(nextQuest[0], nextQuest[1],
						Integer.parseInt(nextQuest[2]), Integer.parseInt(nextQuest[3]));
				// Insert the new quest log entry into the quest log.
				hashQuestLog.insert(newEntry);
				treeQuestLog.insert(newEntry);
			}
		} catch (IOException e) {
			System.out.println("Something bad happened while reading the quest information.");
			e.printStackTrace();
		}

		// Print out the hashed quest log's quests in alphabetical order.
		// COMMENT THIS OUT when you're testing the file with 100,000 quests.  It takes way too long.
		//System.out.println(hashQuestLog);



		// Print out the lib280.tree quest log's quests in alphabetical order.
		// COMMENT THIS OUT when you're testing the file with 100,000 quests.  It takes way too long.
	    //System.out.println(treeQuestLog.toStringInorder());


		// Determine the average number of elements examined during access for hashed quest log.
	    // (call hashQuestLog.obtainWithCount() for each quest in the log and find average # of access)
		String[] keysArray = hashQuestLog.keys();
		int numKeys = keysArray.length;
		float searchTotal = 0;
		for (int i = 0; i < keysArray.length; i++) {
			Pair280<QuestLogEntry,Integer> pair = hashQuestLog.obtainWithCount(keysArray[i]);
			searchTotal += pair.secondItem();
		}
		searchTotal = searchTotal/numKeys;
		System.out.println("Avg. # of items examined per query in Hash quest log with: " + numKeys +" entries: " + searchTotal);

		// Determine the average number of elements examined during access for lib280.tree quest log.
	    // (call treeQuestLog.searchCount() for each quest in the log and find average # of access)
		searchTotal = 0;
		for (int i = 0; i < keysArray.length; i++) {
			QuestLogEntry questLogEntry = hashQuestLog.obtain(keysArray[i]);
			searchTotal += treeQuestLog.searchCount(questLogEntry);
		}
		searchTotal = searchTotal/numKeys;
		System.out.println("Avg. # of items examined per query in Tree quest log with: " + numKeys +" entries: " + searchTotal);
	}
	
	
}
