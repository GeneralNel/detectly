package com.capital6.detectly;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import net.sourceforge.tess4j.TesseractException;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.capital6.detectly.KVExtractor.createMockPaystub;

@RestController
public class FileUploadController {

    private OCR myOCR;

    @Autowired
    private KieContainer kieContainer;

    @Value("${file.upload-dir}")
    String FILE_DIRECTORY;
    private Paystub paystub;

    public FileUploadController() {
        myOCR = new OCR();
    }


    @RequestMapping(value = "/fraudCheck", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Paystub detectFraudPaystub(@RequestParam("file") MultipartFile file) throws IOException, TesseractException {
        // pass paystub through OCR
        paystub = performOCR(file);
        // run through rules engine
        KieSession kieSession = kieContainer.newKieSession();
        kieSession.insert(paystub);
        kieSession.fireAllRules();
        kieSession.dispose();
        // return results in json
        return paystub;
    }


    private Paystub performOCR(MultipartFile file) throws IOException, TesseractException {
        File myFile = new File(FILE_DIRECTORY+file.getOriginalFilename());
        myFile.createNewFile();
        String filepath = "src/main/resources/uploadedFiles/" + file.getOriginalFilename();
        File newFile = new File(filepath);

        try (OutputStream os = new FileOutputStream(filepath)) {
            os.write(file.getBytes());
        }

        String ocrOutput = myOCR.getContentsFromFile(newFile);
        String outputPath = "src/main/resources/processedFiles/output.txt";
        Files.write( Paths.get(outputPath), ocrOutput.getBytes());
        File processedFile = new File(outputPath);

        Paystub foundPaystub =  KVExtractor.extractKeyValueToPaystub(processedFile);
        newFile.delete();
        processedFile.delete();

        return foundPaystub;
    }

    @GetMapping("/getContentsFromImg")
    public Paystub getContentsFromImg(@RequestParam("file") MultipartFile file) throws IOException, TesseractException {
        File myFile = new File(FILE_DIRECTORY+file.getOriginalFilename());
        myFile.createNewFile();
        String filepath = "src/main/resources/uploadedFiles/" + file.getOriginalFilename();
        File newFile = new File(filepath);

        try (OutputStream os = new FileOutputStream(filepath)) {
            os.write(file.getBytes());
        }

        String ocrOutput = myOCR.getContentsFromFile(newFile);
        String outputPath = "src/main/resources/processedFiles/output.txt";
        Files.write( Paths.get(outputPath), ocrOutput.getBytes());

        return KVExtractor.extractKeyValueToPaystub(new File(outputPath));

    }

    @RequestMapping(value = "/testFraudCheck", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Paystub testDetectFraudPaystub() throws IOException, TesseractException {

        paystub = createMockPaystub();

        KieSession kieSession = kieContainer.newKieSession();
        kieSession.insert(paystub);
        kieSession.fireAllRules();
        kieSession.dispose();

        return paystub;
    }


    @RequestMapping(value = "/getFraudulence", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Paystub getFraudulenceFromPaystub(@RequestParam(value="name") String name, @RequestParam(value="ssn") String ssn) {
        Paystub paystub = new Paystub(name, ssn);
        KieSession kieSession = kieContainer.newKieSession();
        kieSession.insert(paystub);
        kieSession.fireAllRules();
        kieSession.dispose();
        return paystub;
    }


    //    @GetMapping("/getContentsFromImg")
//    public String getContentsFromImg1(@RequestParam("file") MultipartFile file, OCR ocr, FileOutputStream outputStream, File createdFile) throws IOException, TesseractException {
//        File myFile = createdFile;
//        createdFile.createNewFile();
//        String filepath = "src/main/resources/uploadedFiles/" + file.getOriginalFilename();
////        File newFile = new File(filepath);
//        try (OutputStream os = outputStream) {
//            os.write(file.getBytes());
//        }
////        OCR myOCR = new OCR();
//
//        return ocr.getContentsFromFile(createdFile);
//    }

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