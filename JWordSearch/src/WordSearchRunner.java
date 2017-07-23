import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;


public class WordSearchRunner {

	/**
	 * These are the first 26 primes, they all correspond to a character, so values[1] corresponds to the letter B.
	 * Since we are calculating large hash values by multiplying primes (since each product of primes can only be made by that product of primes) we want to keep the calculation to a low amount,
	 * therefore the primes are ordered in order of how many times a character is used in the words.txt. so for the most used char E gets a 2 and the least used J gets 101, this limits the sizes of the numbers that are being calculated.
	 */
	private static int[] values= {
		7,59,29,31,2,67,47,53,5,101,79,23,43,13,19,41,97,11,3,17,37,71,79,89,61,83
	};
	private static int count = 0;
	private static word[] library = new word[172823];//amount of words in file
	private static ArrayList<word> wordsList = new ArrayList<word>();//Words that are possible
	private static int counter;

	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter your text and I will calculate the hash from it");
		String input = keyboard.nextLine();

		//Get word before calculating runtime


		long startTime = System.nanoTime()/1000;
		//System.out.println("Reading library file");
		try (Stream<String> stream = Files.lines(Paths.get("./words.txt"))) {
			stream.forEach(line -> ProcessLine(line));
		} catch (IOException e) {
			e.printStackTrace();
		}
		long stopTime = System.nanoTime()/1000;
		//System.out.println("Reading file took "+ (stopTime-startTime) + "µs");//Print to test creation of creating the library files
		//printHash();

		//Make sure the input is in lower case without any spaces
		input = input.toLowerCase();
		input = input.trim();
		
		//Create an array that has the amount of all available chars in an int array of length 26
		int[] characterCount = new int[26];
		for(int i = 0 ;i<input.length();i++){//Count all the characters
			characterCount[input.charAt(i)-97]++;
		}
		
		int wordsFound = 0;
		for(int i=0;i<library.length;i++){//Go through all the words
			if(testWord(characterCount, library[i])){
				wordsList.add(library[i]);//Add word to list of possible words
				wordsFound++;
			}
		}
		stopTime = System.nanoTime()/1000;//Done so save the timestamp and start telling the user what was found
		
		//quickPrint();
		sortedPrint();
		System.out.println("Found "+ wordsFound + " words");
		System.out.println("Runtime took "+ (stopTime-startTime) + "µs");
	}
	/**
	 * Processes the input and puts it in the library
	 * @param input word to process
	 */
	private static void ProcessLine(String input){
		library[count] = new word(input, getHash(input), input.length());
		count++;
	}
	/**
	 * Calculates a unique number for a given input by multiplying prime values for each character
	 * @param input Word to calculate the 'hash' from
	 * @return unique hash based on word
	 */
	private static long getHash(String input){
		long temp = 1;
		for (int i = 0; i<input.length();i++){
			temp = temp*values[input.charAt(i)-97];
		}
		return temp;
	}
	/**
	 * Test any given 'word' or line of characters which should already be processed.
	 * It works by testing all the characters in the alphabet, see if the hash can be divided by the corresponding prime number.
	 * If it can it reduces the amount of that character that is available by 1, divides the hash by the prime and goes back.
	 * If at any point the hash becomes 1 it means we have found all the possible characters since only the first prime remains
	 * If after testing all the characters there still is something left in the hash than not all characters are found
	 * @param characters An array of length 26 that has counts of how many of each char we have available
	 * @param wordToTest the word object to test
	 * @return true if the word can be made, false if not
	 */
	private static boolean testWord(int[] characters, word wordToTest){
		int i = 0;
		int[] testArray = new int[26];
		System.arraycopy(characters, 0, testArray, 0, 26);
		while(i<26){//Test all the characters
			if (wordToTest.hash==1){//All characters found
				return true;
			}
			if (testArray[i]==0){//no char to thest
				i++;
			}else{
				if(wordToTest.hash%values[i]==0){//char found
					wordToTest.hash =wordToTest.hash/values[i];//lower the hash 
					testArray[i]--;//lower the count of this char
				}else{//Character was not found, moving on to the next one
					i++;
				}
			}
		}
		return false;//Not all characters where found
	}

	/**
	 * Dumps all the possible words in alphabetical order
	 */
	private static void quickPrint(){
		wordsList.forEach((temp)->{
			System.out.println("(" + temp.length + ")\t" + temp.word);
		});
	}

	/**
	 * Prints all the possible words in order of the amount of chars
	 */
	private static void sortedPrint(){
		counter = 0;
		while (counter<29){//No word has more than 28 chars in the word.txt file 
			wordsList.forEach((temp)->{
				if (temp.length==counter){
					System.out.println("(" + temp.length + ")\t" + temp.word);
				}
			});
			counter++;
		}
	}


}
