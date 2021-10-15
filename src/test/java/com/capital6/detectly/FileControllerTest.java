package com.capital6.detectly;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FileControllerTest {
    FileEndpointApplication app;

    @BeforeEach
    void setUp() {
//        app = new FileEndpointApplication
    }

    @AfterEach
    void tearDown() {
    }

    @DisplayName("Should get correct encoded string for successful GET call on file name")
    @Test
    void testFileContents() throws IOException {
        String filename = "myfile.txt";
        String expectedEncodedName = "bXlmaWxlLnR4dA==";

        // given an endpoint
        HttpUriRequest request = new HttpGet( "http://localhost:8080/fileContents?file=" + filename );

        // when endpoint called with file
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
        HttpEntity entity = httpResponse.getEntity();

        // then should get correct response entity string
        assertEquals(
                expectedEncodedName, EntityUtils.toString(entity));
    }

    @DisplayName("Should get correct status code for successful GET call")
    @Test
    void testGetFileContents() throws IOException {
        String filename = "myPayStub.txt";

        // given an endpoint
        HttpUriRequest request = new HttpGet( "http://localhost:8080/getFileContents?file=" + filename );

        // when endpoint called with file
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

        // then should get correct status code for happy path
        assertEquals(
                HttpStatus.OK.value(),
                httpResponse.getStatusLine().getStatusCode());
    }

    @DisplayName("Should get correct status code for successful POST call")
    @Test
    void testCreateFile() throws Exception {
        String filename = "testFile.txt";
        String content = "this is for testing only";
        FileObj testObj = new FileObj(filename, content);
        JSONObject json = new JSONObject();
        json.put(filename, content);


        MockMvc mockMvc;
        FileController controller = new FileController();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/newfile")
                .accept(MediaType.APPLICATION_JSON)
                .content(json.toString())
                .contentType(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals("File successfully created", result.getResponse().getContentAsString());

        /*
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("http://localhost:8080/newFile")
                .accept(MediaType.APPLICATION_JSON)
                .content(json.toString())
                .contentType(MediaType.APPLICATION_JSON);

        // when endpoint called with file
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute((HttpUriRequest) requestBuilder);

        // then should get correct status code for happy path
        assertEquals(
                HttpStatus.OK.value(),
                httpResponse.getStatusLine().getStatusCode());
                */
    }
}