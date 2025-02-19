import org.testng.annotations.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomUniqueNameGenerator {
    @Test
    public static void nameGenerator() {
        String fileName = "random_unique_names.csv";
        Set<String> uniqueNames = new HashSet<>();
        Random random = new Random();

        while (uniqueNames.size() < 10000) {
            String name = generateRandomName(random, 6, 10); // Generates a name of length 6-10
            uniqueNames.add(name);
        }

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.append("ID,Name\n");

            int id = 1;
            for (String name : uniqueNames) {
                writer.append(id + "," + name + "\n");
                id++;
            }

            System.out.println("CSV file with unique random names generated successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateRandomName(Random random, int minLen, int maxLen) {
        int nameLength = minLen + random.nextInt(maxLen - minLen + 1);
        StringBuilder name = new StringBuilder();

        // Generate first letter uppercase
        name.append((char) ('A' + random.nextInt(26)));

        // Generate rest of the letters as lowercase
        for (int i = 1; i < nameLength; i++) {
            name.append((char) ('a' + random.nextInt(26)));
        }

        return name.toString();
    }
}

