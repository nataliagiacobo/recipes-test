package com.ada.recipes.test.user;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Assertions;

public class UserDefinitions {

    private RequestSpecification request = RestAssured.given()
            .baseUri("http://localhost:8080")
            .contentType(ContentType.JSON);

    private Response response = null;
    private User user = new User();
    private String token;

    @Given("user is unknown")
    public void userIsUnknown() {
        user = new User();
        user.setName(RandomStringUtils.randomAlphabetic(20));
        user.setEmail(RandomStringUtils.randomAlphabetic(11) + "@email.com");
        user.setPassword(RandomStringUtils.randomAlphabetic(3) + RandomStringUtils.randomAlphabetic(3).toUpperCase() + RandomStringUtils.randomNumeric(2) + "*");
    }

    @Given("user with invalid password")
    public void userWithInvalidPassword() {
        user = new User();
        user.setName(RandomStringUtils.randomAlphabetic(20));
        user.setEmail(RandomStringUtils.randomAlphabetic(11) + "@email.com");
        user.setPassword(RandomStringUtils.randomAlphabetic(3));
    }
    @When("user is registered with success")
    public void userIsRegistered() {
        response = request.body(user).when().post("/user");
        response.then().statusCode(201);
        user.setId(response.jsonPath().get("id"));
    }

    @Then("user register failed")
    public void userRegisterFailed() {
        response = request.body(user).when().post("/user");
        response.then().statusCode(400);
        Assertions.assertEquals("Senha não preenche requisitos", response.getBody().asString());
    }

    @And("login failed")
    public void loginFailed() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(user.getEmail());
        loginRequest.setPassword(user.getPassword());

        response = request.body(loginRequest).when().post("/login");
        response.then().statusCode(403);
    }

    @And("user is authenticated")
    public void userIsAuthenticated() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(user.getEmail());
        loginRequest.setPassword(user.getPassword());

        response = request.body(loginRequest).when().post("/login");
        response.then().statusCode(200);
        token = response.jsonPath().get("token");
    }

    @Then("user is known")
    public void userIsKnown() {
        request = RestAssured.given().header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        response = request.when().get("/user/" + user.getId());
        response.then().statusCode(200);
        String name = response.jsonPath().get("name");
        Assertions.assertEquals(user.getName(), name);
    }

}
