package com.capital6.detectly;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class FileController {

    private String encodeFile(String originalInput) {
        return Base64.getEncoder().encodeToString(originalInput.getBytes());
    }

    private String decodeFile(String encodedInput) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedInput);
        return new String(decodedBytes);
    }

    @PostMapping(path = "/newfile")
    public String createFile(@RequestBody FileObj request) throws IOException {
        try {
            String fileName = request.getName();
            String body = request.getContent();
            String filePath ="src/main/resources/textfiles/" + fileName;
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            String encodedContents = encodeFile(body);
            writer.write(encodedContents);
            writer.close();
            return "File successfully created";
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return "File couldn't be created";
    }


    @GetMapping(path = "/fileContents")
    public String fileContents(@RequestParam(value = "file", defaultValue = "default file") String file) {
        return encodeFile(file);
    }

    @GetMapping("/getFileContents")
    public String getFileContents(@RequestParam(value = "file", defaultValue = "default file") String file) throws IOException {
        String filepath = "src/main/resources/textfiles/" + file;
        FileReader fileReader = new FileReader(filepath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String encodedContents = bufferedReader.readLine();
        String decodedContents = decodeFile(encodedContents);
        return decodedContents;
    }

}