package com.capital6.detectly;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {

    @Value("${file.upload-dir}")
    String FILE_DIRECTORY;

    @Autowired
    ObjectMapper mapper;

    @PostMapping("/uploadFile")
    public ResponseEntity<Object> fileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        File myFile = new File(FILE_DIRECTORY+file.getOriginalFilename());
        myFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(myFile);
        fos.write(file.getBytes());
        fos.close();
        return new ResponseEntity<Object>("File uploaded successfully", HttpStatus.OK);
    }

    @GetMapping("/getContentsFromImg")
    public String getContentsFromImg(@RequestParam("file") MultipartFile file) throws IOException, TesseractException {
        File myFile = new File(FILE_DIRECTORY+file.getOriginalFilename());
        myFile.createNewFile();
        String filepath = "src/main/resources/uploadedFiles/" + file.getOriginalFilename();
        File newFile = new File(filepath);
        try (OutputStream os = new FileOutputStream(filepath)) {
            os.write(file.getBytes());
        }
        OCR myOCR = new OCR();

        return myOCR.getContentsFromFile(newFile);
    }

    @GetMapping("/getUploadedFile")
    public Map<String, String> fileContentsJSON(@RequestParam(value = "file") String file) throws IOException {
        Map<String, String> payStubMap = new HashMap<>();
        String filepath = "src/main/resources/uploadedFiles/" + file;
        // insert error handling
        FileReader fileReader = new FileReader(filepath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();

        while (line != null) {
            String[] keyVal = getKeyValueFromLine(line);
            payStubMap.put(keyVal[0], keyVal[1]);
            line = bufferedReader.readLine();
        }

        return payStubMap;
    }

    private String[] getKeyValueFromLine(String line) {
        String[] keyVal = new String[2];
        keyVal[0] = "default key";
        keyVal[1] = "default value";
        if (line.length() == 0) return keyVal;
        int idx = 0;
        while (line.charAt(idx)!= ':') {
            idx++;
            if (idx >= line.length())
                return keyVal;
        }
        keyVal[0] = line.substring(0, idx);
        keyVal[1] = line.substring(idx+1);
        return keyVal;
    }

}