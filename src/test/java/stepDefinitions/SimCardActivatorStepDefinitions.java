package stepDefinitions;

import au.com.telstra.simcardactivator.SimCardActivator;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = SimCardActivator.class, loader = SpringBootContextLoader.class)
public class SimCardActivatorStepDefinitions {

    @Autowired
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BASE_URL = "http://localhost:8080";

    // holds response from POST /sims/activate
    private ResponseEntity<Map> lastActivationResponse;

    // holds response from GET /sims/query
    private ResponseEntity<Map> lastQueryResponse;

    @When("I activate a SIM with ICCID {string} and email {string}")
    public void i_activate_a_sim_with_iccid_and_email(String iccid, String email) {
        String url = BASE_URL + "/sims/activate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Controller expects: { "iccid": "...", "customerEmail": "..." }
        String json = String.format("{\"iccid\":\"%s\",\"customerEmail\":\"%s\"}", iccid, email);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        lastActivationResponse = restTemplate.postForEntity(url, entity, Map.class);


        Assert.assertTrue("POST /sims/activate should return 200 OK",
                lastActivationResponse.getStatusCode().is2xxSuccessful());
    }

    @Then("the activation response message should be {string}")
    public void the_activation_response_message_should_be(String expectedMessage) {
        Map body = lastActivationResponse.getBody();
        Assert.assertNotNull("Activation response body should not be null", body);
        Object message = body.get("message");
        Assert.assertEquals("Activation response message mismatch", expectedMessage, message);
    }

    @Then("the activation record with id {long} should have:")
    public void the_activation_record_with_id_should_have(Long id, DataTable table) {
        String url = BASE_URL + "/sims/query?simCardId=" + id;
        lastQueryResponse = restTemplate.getForEntity(url, Map.class);

        Assert.assertTrue("GET /sims/query should return 200 OK for existing id",
                lastQueryResponse.getStatusCode().is2xxSuccessful());

        Map body = lastQueryResponse.getBody();
        Assert.assertNotNull("Query response body should not be null", body);

        // The query endpoint returns: { "iccid": string, "customerEmail": string, "active": boolean }
        Map<String, String> expected = table.asMap(String.class, String.class);

        // Compare expected vs actual fields
        Assert.assertEquals("iccid mismatch",
                expected.get("iccid"), String.valueOf(body.get("iccid")));
        Assert.assertEquals("customerEmail mismatch",
                expected.get("customerEmail"), String.valueOf(body.get("customerEmail")));

        // active is boolean; table provides a string "true"/"false"
        String expectedActive = expected.get("active");
        Assert.assertNotNull("Expected 'active' must be provided", expectedActive);
        Assert.assertEquals("active mismatch",
                Boolean.valueOf(expectedActive), Boolean.valueOf(String.valueOf(body.get("active"))));
    }
}
