Feature: User register

  Scenario: User is registered
    Given user is unknown
    When user is registered with success
    And user is authenticated
    Then user is known

  Scenario: User is not registered
    Given user with invalid password
    Then user register failed
    And login failed
