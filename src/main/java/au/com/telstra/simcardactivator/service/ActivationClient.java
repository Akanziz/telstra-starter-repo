package au.com.telstra.simcardactivator.service;

import au.com.telstra.simcardactivator.dto.ActuatorRequest;
import au.com.telstra.simcardactivator.dto.ActuatorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

    @Service
    public class ActivationClient {
        private static final Logger log = LoggerFactory.getLogger(ActivationClient.class);
        private final RestTemplate restTemplate;
        private static final String ACTUATE_URL = "http://localhost:8444/actuate";

        public ActivationClient(RestTemplate actuatorRestTemplate) {
            this.restTemplate = actuatorRestTemplate;
        }

        public boolean activateIccid(String iccid) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<ActuatorRequest> entity = new HttpEntity<>(new ActuatorRequest(iccid), headers);

                ResponseEntity<ActuatorResponse> response =
                        restTemplate.postForEntity(ACTUATE_URL, entity, ActuatorResponse.class);

                ActuatorResponse body = response.getBody();
                return response.getStatusCode().is2xxSuccessful() && body != null && body.isSuccess();
            } catch (Exception ex) {
                log.warn("Error calling actuator: {}", ex.getMessage());
                return false;
            }
        }
    }
