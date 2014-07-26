/*
Given a function 

getRandomTripplet() 

which returns a random triplet of letters from a string. You don't know the string using calls to this function you have to correctly guess the string. the length of the string is also given. 

Lets say the string is helloworld the function getRandomTriplet will return things like 

hlo 
hew 
wld 
owo 

the function maintains the relative order of the letters. so it will never return 

ohl since h is before o in the string. 
owe since w is after e 

The string is not known you are only given length of the string.
*/
// Status Note: Abandoning this one after determining it's not always
// solvable.
//
import java.util.*;
import java.util.function.*;
import java.util.Random;

class Word {
    Random rand = new Random();
    String word;
    int wordLen;
    Word(String word) {
        this.word = word;
        this.wordLen = word.length();
    }
    String getRandomTriplet() {
        Set<Integer> idxs = new TreeSet<>();
        for (int i = 0; i < 3; i++)
            while (!idxs.add(rand.nextInt(wordLen))) {}
        // At this point, idxs are sorted and unique
        String ret = "";
        for (int i : idxs)
            ret += word.charAt(i);
        return ret;
    }
}
class Guesser {
    Word word;
    Guesser(Word word) {
        this.word = word;
    }
    void train(int numSamples) {
        Map<Character, Integer> cmap = new HashMap<Character, Integer>();
        for (int i = 0; i < numSamples; i++) {
            String s = word.getRandomTriplet();
            for (char c : s.toCharArray())
                cmap.compute(c, (k, v) -> { return v == null ? 1 : v + 1; });
        }
        Set<Map.Entry<Character, Integer>> cmapEntrySet = cmap.entrySet();
        Comparator<Map.Entry<Character, Integer>> comparator = Map.Entry.comparingByValue();
        // TODO: I'm thinking the comparator expected is parameterized only on
        // key.
        TreeMap<Character, Integer> sortedCmap = new TreeMap<Character, Integer>(comparator);
        sortedCmap.putAll(cmap);
        for (Map.Entry<Character, Integer> entry : sortedCmap.entrySet())
            System.out.println(entry.getKey() + ": " + entry.getValue());

    }
}
public class TripletWordGuesser {
    public static void main(String[] args) {
        Word word = new Word(args[0]);
        Guesser guesser = new Guesser(word);
        for (int i = 0; i < 50; i++) {
            System.out.println(word.getRandomTriplet());
        }
    }

}
// vim:ts=4:sw=4:et:tw=78
