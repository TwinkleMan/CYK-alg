import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Implements the CYK Algorithm that uses a context-free grammar file and checks
 * to see whether a given string is accepted by that grammar.
 *
 * @variable = нетерминал
 */
public class Cyk
{
   /**
    * The 2 dimensional table for the CYK algorithm
    **/
   private static ArrayList<String>[][] table;

   /**
    * Custom class that contains id and InnerHashMap with variables data
    * @Variables are in the form of (0 U 1)+
    * They are stored in the InnerHashMap as (0 U 1)+ maps { (0 U 1)+, (0 U 1)+ }
    */
   private class Super_variables {
      private static int id;
      private HashMap<Integer, HashMap<String,String[]>> map;  //1 = A->BC; stored as [1: [0: 1,10]]
      private Set<String> keySet;   //collection of different variable keys

      public Super_variables() {
         id = 0;
         map = new HashMap<>();
         keySet = new HashSet<>();
      }

      public void addVariable(String key,String[] expression) {

         /**
          * adds variable to the inner HashMap and to the keySet
          */

         id++;
         HashMap<String, String[]> variables = new HashMap<>();

         keySet.add(key);

         variables.put(key, expression);
         map.put(id,variables);
      }

      public String[] getValue(int id) {

         /**
          * gets values from the map line with current id
          *
          * [1: [0: 1,10]]
          * [2: [10: 100,101]]
          *
          * with id == 2 returns 100,101
          */

         String key = getKey(id);

         return this.map.get(id).get(key);

      }

      public String getKey(int id) {

         /**
          * gets inner HashTable key from the map line with current id
          *
          * [1: [0: 1,10]]
          * [2: [10: 100,101]]
          *
          * with id == 2 returns 10
          */

         String final_key = "0";

         for (String key:keySet) {
            if( map.get(id).containsKey(key)) {
               final_key = key;
            }
         }

         return final_key;

      }

      public int getSize() {
         return map.size();
      }

   }

   /**
    * @Variables are in the form of (0 U 1)+
    * They are stored in the InnerHashMap as (0 U 1)+ maps { (0 U 1)+, (0 U 1)+ }
    */
   private Super_variables super_variables;

   /**
    * @terminals are in the form of (a U b)
    * They are stored in the hashmap in the form: (0 U 1)+ maps (a U b)
    */
   private HashMap<String, Character> terminals;

   /**
    * The start variable
    */
   private static String startVariable;

   /**
    * Constructs a Cyk object and initializes the HashMaps of the variables
    * and the terminals
    */
   public Cyk()
   {
      super_variables = new Super_variables();
      terminals = new HashMap<String, Character>();
   }

   /**
    * Processes the grammar file and builds the HashMap of the list of terminals
    * and variables. Uses the Scanner object to read the grammar file.
    * @param file the string representing the path of the grammar file
    */
   public void processGrammarFile(String file)
   {
      File grammarFile = null;
      Scanner scanner = null;
      int counter = 0;
      HashMap<String,String[]> temp = new HashMap<String,String[]>();
      try
      {
         grammarFile = new File(file);
         scanner = new Scanner(grammarFile);
         String[] line = scanner.nextLine().split(":");
         startVariable = line[0];   //opening grammar file and setting the startVariable
         do
         {
            String variable = line[0];
            if ((line[1].equals("a") || line[1].equals("b")))
            {
               terminals.put(variable, line[1].charAt(0));
            }
            else
            {
               String[] rest = line[1].split(",");
               if (rest != null)
               {
                  super_variables.addVariable(variable,rest);  //creating new InnerHashMap entry with the key = variable
               }
            }
            if (scanner.hasNextLine())
               line = scanner.nextLine().split(":");
            else
               line = null;
         } while (line != null);
         scanner.close();
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
      }
   }

   /**
    * Tests the string against the given grammar file using the CYK Algorithm.
    * @param w the input string to test
    * @return true if string w is accepted by the grammar, false otherwise.
    */
   @SuppressWarnings("unchecked")
   public boolean processString(String w)
   {
      //initializing table
      int length = w.length();
      table = new ArrayList[length][];
      for (int i = 0; i < length; ++i)
      {
         table[i] = new ArrayList[length];
         for (int j = 0; j < length; ++j)
            table[i][j] = new ArrayList < String > ();
      }
      //pre-processing table fill (only diagonal)
      for (int i = 0; i < length; ++i)
      {
         Set<String> keys = terminals.keySet();
         for (String key : keys)
         {
            if (terminals.get(key).charValue() == w.charAt(i))
               table[i][i].add(key);
         }
      }

      //core of the algorythm
      for (int l = 2; l <= length; ++l)
      {
         for (int i = 0; i <= length - l; ++i)
         {
            int j = i + l - 1;
            for (int k = i; k <= j - 1; ++k)
            {
               for (int id = 1; id <= super_variables.getSize();id++) {
                  String[] values = super_variables.getValue(id);    //for each set of key:value in InnerHashTable
                  if (table[i][k].contains((values[0]))
                          && table[k + 1][j].contains(values[1]))
                     table[i][j].add(super_variables.getKey(id));    //adding current key to the table
               }
            }
         }
      }
      if (table[0][length - 1].contains(startVariable))
         return true;
      return false;
   }

   /**
    * Prints grammar from the grammar.txt to the screen
    */
   public void showGrammar() {
      System.out.println("Current grammar ('grammar.txt' in the main folder):\n");

      for (int i = 1; i <= super_variables.getSize(); i++) {
         System.out.println(super_variables.getKey(i) + ": " + super_variables.getValue(i)[0] + "," + super_variables.getValue(i)[1]);
      }
      for (String key: terminals.keySet()) {
         System.out.println(key + ": " + terminals.get(key));
      }
      System.out.println();
   }

   /**
    * Takes a given grammar file as the input and a given string to test
    * against that grammar.
    */
   public static void main(String[] args)
   {

      Cyk c = new Cyk();
      c.processGrammarFile("grammar.txt");
      c.showGrammar();
      int flag = 2;

      while (true) {

         System.out.println("Enter your tested string:");
         Scanner scanner = new Scanner(System.in);
         String w = scanner.nextLine();

         if (c.processString(w))
            System.out.println("The string fits the given grammar\n");
         else
            System.out.println("The string doesn't fits the given grammar\n");

         System.out.println("1 - enter one more string;\n2 - exit");
         flag = scanner.nextInt();
         if (flag != 1) break;

      }

   }
}

