package au.com.telstra.simcardactivator.controller;

import au.com.telstra.simcardactivator.dto.ActivateSimRequest;
import au.com.telstra.simcardactivator.dto.ActivateSimResponse;
import au.com.telstra.simcardactivator.service.ActivationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import au.com.telstra.simcardactivator.dto.SimRecordResponse;
import au.com.telstra.simcardactivator.entity.ActivationRecord;
import au.com.telstra.simcardactivator.repository.ActivationRecordRepository;

import java.util.Optional;

@RestController
    @RequestMapping("/sims")
    public class SimActivationController {

        private static final Logger log = LoggerFactory.getLogger(SimActivationController.class);

        private final ActivationClient activationClient;
        private final ActivationRecordRepository repo;

        public SimActivationController(ActivationClient activationClient,
                                       ActivationRecordRepository repo) {
            this.activationClient = activationClient;
            this.repo = repo;
        }

        @PostMapping("/activate")
        public ResponseEntity<ActivateSimResponse> activate(@RequestBody ActivateSimRequest request) {
            if (request == null || isBlank(request.getIccid()) || isBlank(request.getCustomerEmail())) {
                return ResponseEntity.badRequest()
                        .body(new ActivateSimResponse(false, "iccid and customerEmail are required"));
            }

            boolean success = activationClient.activateIccid(request.getIccid());

            // Log per spec
            if (success) {
                log.info("Activation SUCCESS for ICCID={} (customer={})",
                        request.getIccid(), request.getCustomerEmail());
            } else {
                log.warn("Activation FAILED for ICCID={} (customer={})",
                        request.getIccid(), request.getCustomerEmail());
            }

            // Persist the result
            ActivationRecord saved = repo.save(
                    new ActivationRecord(request.getIccid(), request.getCustomerEmail(), success)
            );

            String msg = success ? "SIM activation succeeded" : "SIM activation failed";
            return ResponseEntity.ok(new ActivateSimResponse(success, msg));
        }

        // New GET query endpoint: /sims/query?simCardId=123
        @GetMapping("/query")
        public ResponseEntity<?> queryById(@RequestParam("simCardId") long simCardId) {
            Optional<ActivationRecord> opt = repo.findById(simCardId);
            if (!opt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            ActivationRecord r = opt.get();
            return ResponseEntity.ok(new SimRecordResponse(r.getIccid(), r.getCustomerEmail(), r.isActive()));
        }

        private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    }