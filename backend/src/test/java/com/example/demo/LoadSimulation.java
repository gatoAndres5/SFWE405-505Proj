package com.example.demo;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class LoadSimulation extends Simulation {

    String baseUrl = System.getProperty("baseUrl", "http://localhost:8080");

    HttpProtocolBuilder httpProtocol = http
        .baseUrl(baseUrl)
        .acceptHeader("application/json")
        .contentTypeHeader("application/json");

    ChainBuilder login =
        exec(
            http("login")
                .post("/auth/login")
                .body(StringBody("""
                    {
                      "username": "admin",
                      "password": "admin123"
                    }
                    """))
                .asJson()
                .check(status().is(200))
                .check(jsonPath("$.token").saveAs("jwt"))
        ).exitHereIfFailed();

    ChainBuilder register =
        exec(
            http("register")
                .post("/registrations?eventId=1&participantId=1")
                .header("Authorization", "Bearer #{jwt}")
                .check(status().in(200, 201, 403, 404, 409))
        );

    ScenarioBuilder scn = scenario("registration flow")
        .exec(login)
        .pause(1)
        .exec(register)
        .pause(1);

    {
        setUp(
            scn.injectOpen(
                rampUsers(5).during(10),
                constantUsersPerSec(2).during(20)
            )
        )
        .protocols(httpProtocol);
    }
}