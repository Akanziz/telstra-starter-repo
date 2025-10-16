package au.com.telstra.simcardactivator.controller;

import au.com.telstra.simcardactivator.dto.ActivateSimRequest;
import au.com.telstra.simcardactivator.dto.ActivateSimResponse;
import au.com.telstra.simcardactivator.service.ActivationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/sims")
    public class SimActivationController {
        private static final Logger log = LoggerFactory.getLogger(SimActivationController.class);
        private final ActivationClient activationClient;

        public SimActivationController(ActivationClient activationClient) {
            this.activationClient = activationClient;
        }

        @PostMapping("/activate")
        public ResponseEntity<ActivateSimResponse> activate(@RequestBody ActivateSimRequest request) {
            if (request == null || isBlank(request.getIccid()) || isBlank(request.getCustomerEmail())) {
                return ResponseEntity.badRequest()
                        .body(new ActivateSimResponse(false, "iccid and customerEmail are required"));
            }

            boolean success = activationClient.activateIccid(request.getIccid());

            if (success) {
                log.info("Activation SUCCESS for ICCID={} (customer={})",
                        request.getIccid(), request.getCustomerEmail());
            } else {
                log.warn("Activation FAILED for ICCID={} (customer={})",
                        request.getIccid(), request.getCustomerEmail());
            }

            return ResponseEntity.ok(
                    new ActivateSimResponse(success, success ? "SIM activation succeeded" : "SIM activation failed")
            );
        }

        private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    }
