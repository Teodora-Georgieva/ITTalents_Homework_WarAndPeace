package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class FastReading {
    private int countOfLines;
    private int countOfWords;
    private int countOfCommas;
    private int countOfWar;
    private int countOfPeace;
    private ArrayList<String> allWords = new ArrayList<>();
    private HashMap<String, Integer> wordsOccurances;
    private TreeMap<Integer, TreeSet<String>> wordsByLength;
    private String[] peaceWords = {"мир", "мирно", "мирен", "мирна", "мирни", "мира", "мирът", "мирния",
            "мирната", "мирното", "мирните", "смирение", "смирението", "смирен", "смирена",
            "смирено","смирени", "примирява", "примирие", "примирието", "примиря",
            "усмири", "усмиря", "усмирява", "помирение", "помирението", "помиря",
            "помири", "помириха", "миром", "помирявахме"};

    public FastReading(){
        this.wordsOccurances = new HashMap<>();
        this.wordsByLength = new TreeMap<>();
    }

    private void readBook(){
        File f = new File("Lev_Tolstoj_Vojna_i_mir.txt");

        try(Scanner sc = new Scanner(f);) {
            while(sc.hasNextLine()){
                this.countOfLines++;
                String line = sc.nextLine();
                for (int i = 0; i < line.length(); i++) {
                    if(line.charAt(i) == ','){
                        this.countOfCommas++;
                    }
                }

                String words = line.toLowerCase().replaceAll("\\p{Punct}", "").trim();
                String[] split = words.split("([^a-zA-Zа-яА-Я']+)'*\\1*");
                for(String word : split){
                    if(word.equals("")){
                        continue;
                    }

                    this.countOfWords++;
                    this.allWords.add(word);
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void doStatistics(){
        this.readBook();
        for(String word : this.allWords){
            if(!this.wordsOccurances.containsKey(word)){
                this.wordsOccurances.put(word, 1);
            }
            else{
                int prevCount = this.wordsOccurances.get(word);
                this.wordsOccurances.put(word, prevCount+1);
            }

            int wordLength = word.length();
            if(!this.wordsByLength.containsKey(wordLength)){
                this.wordsByLength.put(wordLength, new TreeSet<>());
            }

            this.wordsByLength.get(wordLength).add(word);

            if(word.startsWith("война")){
                this.countOfWar++;
            }
            else{
                for(String peaceWord : this.peaceWords){
                    if(word.equals(peaceWord)){
                        this.countOfPeace++;
                        break;
                    }
                }
            }
        }
    }

    private void findMostCommonWords(){
        ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>();
        entries.addAll(this.wordsOccurances.entrySet());
        entries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        Map.Entry<String, Integer> mostCommonWord = entries.get(0);
        System.out.println("Most common word: " + mostCommonWord.getKey() + " - " + mostCommonWord.getValue() + " occurances");
        System.out.println("10 most common words: ");
        for (int i = 0; i < 10; i++) {
            System.out.println((i+1) + ": " + entries.get(i).getKey() + " - " + entries.get(i).getValue());
        }
    }

    public void showStatistics(){
        this.doStatistics();
        System.out.println("count of lines = " + this.countOfLines);
        System.out.println("count of words = " + this.countOfWords);
        System.out.println("count of commas = " + this.countOfCommas);
        System.out.println("count of war: " + this.countOfWar);
        System.out.println("count of peace: " + this.countOfPeace);
        System.out.println(this.countOfWar > this.countOfPeace ? "there is more war in the novel" :
                                                                  "there is more peace in the novel");
        this.findMostCommonWords();
    }

    public void createFilesWithWords(){
        File directory = new File("sortedWordsByLength");
        directory.mkdir();
        for(Integer key : this.wordsByLength.keySet()){
            String fileName = key + "_letter_words.txt";
            File file = new File(directory, fileName);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try(PrintStream ps = new PrintStream(file);) {
                TreeSet<String> uniqueWords = this.wordsByLength.get(key);
                for(String uniqueWord : uniqueWords){
                    ps.println(uniqueWord);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Created files with words by length");
    }
}