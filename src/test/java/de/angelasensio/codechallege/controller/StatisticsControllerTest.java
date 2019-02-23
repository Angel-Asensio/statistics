package de.angelasensio.codechallege.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import de.angelasensio.codechallenge.model.Transaction;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StatisticsControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(messageConverter -> messageConverter instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void noTransactionsShouldReturnEmptyStats() throws Exception {
        this.mockMvc.perform(get("/statistics"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value(0d))
                .andExpect(jsonPath("$.avg").value(0d))
                .andExpect(jsonPath("$.max").value(0d))
                .andExpect(jsonPath("$.min").value(0d))
                .andExpect(jsonPath("$.count").value(0d));
    }

    @Test
    public void successfullyCreateTransaction() throws Exception {

        Instant now = Instant.now();
        String transactionAsJson = toJson(new Transaction(12.3d,
                now.toEpochMilli(),
                null));

        this.mockMvc.perform(post("/transactions")
                .contentType(contentType)
                .content(transactionAsJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void successfullyReturnStatistics() throws Exception {
        this.mockMvc.perform(get("/statistics"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value(12.3d))
                .andExpect(jsonPath("$.avg").value(12.3d))
                .andExpect(jsonPath("$.max").value(12.3d))
                .andExpect(jsonPath("$.min").value(12.3d))
                .andExpect(jsonPath("$.count").value(1d));
    }

    private String toJson(Object object) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(object, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}