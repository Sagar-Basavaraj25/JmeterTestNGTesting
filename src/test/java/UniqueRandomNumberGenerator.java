import org.testng.annotations.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class UniqueRandomNumberGenerator {
    @Test
    public static void numberGenerator() {
        String fileName = "csvFiles/unique_random_numbers.csv";
        Set<Integer> uniqueNumbers = new HashSet<>();
        Random random = new Random();

        // Generate 10,000 unique random numbers between 1 and 100,000
        while (uniqueNumbers.size() < 10) {
            int num = random.nextInt(100) + 1; // Range: 1 to 100,000
            uniqueNumbers.add(num);
        }

        // Write to CSV
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.append("ID,RandomNumber\n");

            int id = 1;
            for (int num : uniqueNumbers) {
                writer.append(id + "," + num + "\n");
                id++;
            }

            System.out.println("CSV file with unique random numbers generated successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

