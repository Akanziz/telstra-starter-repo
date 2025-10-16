Feature: SIM card activation and persistence
  In order to track SIM activation outcomes
  As the SIM Card Activator microservice
  I want to record whether activations succeed or fail and query them by ID

  # NOTE: Make sure the actuator JAR is running on port 8444,
  # and the microservice is running on port 8080 before running these scenarios.

  Scenario: Successful SIM activation is recorded as active=true
    When I activate a SIM with ICCID "1255789453849037777" and email "success@example.com"
    Then the activation response message should be "SIM activation succeeded"
    And the activation record with id 1 should have:
      | iccid         | 1255789453849037777 |
      | customerEmail | success@example.com |
      | active        | true                |

  Scenario: Failed SIM activation is recorded as active=false
    When I activate a SIM with ICCID "8944500102198304826" and email "fail@example.com"
    Then the activation response message should be "SIM activation failed"
    And the activation record with id 2 should have:
      | iccid         | 8944500102198304826 |
      | customerEmail | fail@example.com    |
      | active        | false               |