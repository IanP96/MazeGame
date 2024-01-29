package io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * A helper class for reading/writing from text files.
 */
public class File {

    /**
     * Gets the lines in a text file.
     * @param filename The name of the file. {@code src} is prepended to the filename.
     * @return A list of lines in the file, excluding line breaks. If the file is empty,
     * the list will be empty as well.
     * @throws FileNotFoundException When an {@link IOException}) occurs
     * due to the file not being found.
     */
    public static List<String> readLines(String filename) throws FileNotFoundException {
        try (BufferedReader r = new BufferedReader(new FileReader("src/" + filename))) {
            return r.lines().toList();
        } catch (IOException e) {
            throw new FileNotFoundException();
        }
    }

}
