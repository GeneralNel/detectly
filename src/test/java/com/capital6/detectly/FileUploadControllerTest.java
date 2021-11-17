package com.capital6.detectly;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FileUploadControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Value("${file.upload-dir}")
    String FILE_DIRECTORY;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFileUpload() throws Exception {
        String fileName = "testFileForUpload";
        MockMultipartFile sampleFile = new MockMultipartFile(
                "uploaded-file",
                fileName,
                "text/plain",
                "This is the file content".getBytes());

        MockMultipartHttpServletRequestBuilder multipartRequest =
                MockMvcRequestBuilders.multipart("/uploadFile");

        mockMvc.perform(multipartRequest.file(sampleFile))
                .andExpect(status().isOk());

    }
    @Test
    void testFileContentsJSON() {
        String fileName = "testFileForUpload";
        MockMultipartFile sampleFile = new MockMultipartFile(
                "uploaded-file",
                fileName,
                "text/plain",
                "This is the file content".getBytes());
    }

    @DisplayName("Should get correct status code for successful GET call")
    @Test
    void testGetFileContentsStatus() throws IOException {
        String pngFile = "src/main/resources/uploadedFiles/painted_paystub.png";
        File pdfFile = new File("src/main/resources/uploadedFiles/testPaystubinPDF.pdf");

//
//        MockMultipartFile sampleFile = new MockMultipartFile(pdfFile);
//        MockMultipartHttpServletRequestBuilder multipartRequest =
//                MockMvcRequestBuilders.multipart("http://localhost:8080/getContentsFromImg?file=");
//
//        mockMvc.perform(multipartRequest.file(pdfFile))
//                .andExpect(status().isOk());

        /* test for pdf */
        // given a pdf file passing thru the endpoint
        HttpUriRequest request = new HttpGet( "http://localhost:8080/getContentsFromImg?file=" + pdfFile );

        // when endpoint called with file
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

        // then should get correct status code for happy path
        assertEquals(
                HttpStatus.OK.value(),
                httpResponse.getStatusLine().getStatusCode());

        /* test for png */
        // given a pdf file passing thru the endpoint
        request = new HttpGet( "http://localhost:8080/getContentsFromImg?file=" + pngFile);

        // when endpoint called with file
        httpResponse = HttpClientBuilder.create().build().execute( request );

        // then should get correct status code for happy path
        assertEquals(
                HttpStatus.OK.value(),
                    httpResponse.getStatusLine().getStatusCode());
    }
//
//    @Test
//    public void getContentsFromImg1() throws IOException, TesseractException {
//        FileUploadController fileUploadController = new FileUploadController();
//        MultipartFile multiFile = mock(MultipartFile.class);
//        OCR ocr = mock(OCR.class);
//        FileOutputStream outstream = mock(FileOutputStream.class);
//        File createdFile = mock(File.class);
//        when(createdFile.createNewFile()).thenReturn(false);
//        when(multiFile.getOriginalFilename()).thenReturn("originalFileNmae");
//        byte[] readBytes = {'a'};
//        when(multiFile.getBytes()).thenReturn(readBytes);
////        doNothing().when(outstream.write(readBytes));
//        when(ocr.getContentsFromFile(createdFile)).thenReturn("Must");
//
//        String contentsFromImg1 = fileUploadController.getContentsFromImg1(multiFile, ocr, outstream, createdFile);;
//
//        assertEquals("Must", contentsFromImg1);
//        verify(createdFile).createNewFile();
//
//    }
}