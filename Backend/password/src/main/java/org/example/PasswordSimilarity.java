package org.example;

import java.util.*;

public class PasswordSimilarity {

    public static int countMinimumOperations(String password) {
        int N = password.length();
        int halfN = N / 2;
        int totalCost = 0;

        // Vowel positions in the alphabet ('a' to 'z')
        Set<Integer> vowelPositions = new HashSet<>(Arrays.asList(
                'a' - 'a', 'e' - 'a', 'i' - 'a', 'o' - 'a', 'u' - 'a'
        ));

        // Precompute cost to change each character to any vowel or consonant
        int[] costToVowel = new int[26];
        int[] costToConsonant = new int[26];

        // Precompute nearest vowel and consonant for each character
        for (int i = 0; i < 26; i++) {
            costToVowel[i] = computeMinCost(i, vowelPositions);
            costToConsonant[i] = computeMinCost(i, getConsonantPositions(vowelPositions));
        }

        int[] delta = new int[N];
        Character[] chars = new Character[N];

        for (int i = 0; i < N; i++) {
            char c = password.charAt(i);
            int idx = c - 'a';
            totalCost += costToConsonant[idx]; // Assume all characters are consonants initially
            delta[i] = costToVowel[idx] - costToConsonant[idx];
            chars[i] = c;
        }

        // Sort indices based on delta
        Integer[] indices = new Integer[N];
        for (int i = 0; i < N; i++) indices[i] = i;

        Arrays.sort(indices, Comparator.comparingInt(i -> delta[i]));

        // Select first N/2 positions with minimal delta to change to vowels
        for (int i = 0; i < halfN; i++) {
            totalCost += delta[indices[i]];
        }

        return totalCost;
    }

    // Helper function to compute minimum cost to change a character to a set of target positions
    private static int computeMinCost(int pos, Set<Integer> targetPositions) {
        int minCost = Integer.MAX_VALUE;
        if (targetPositions.contains(pos)) {
            return 0;
        }
        for (int target : targetPositions) {
            int cost = Math.abs(pos - target);
            minCost = Math.min(minCost, cost);
        }
        return minCost;
    }

    // Helper function to get consonant positions
    private static Set<Integer> getConsonantPositions(Set<Integer> vowelPositions) {
        Set<Integer> consonantPositions = new HashSet<>();
        for (int i = 0; i < 26; i++) {
            if (!vowelPositions.contains(i)) {
                consonantPositions.add(i);
            }
        }
        return consonantPositions;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String password = scanner.nextLine();
        int result = countMinimumOperations(password);
        System.out.println(result);
    }
}
