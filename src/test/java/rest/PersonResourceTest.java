/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import entity.Address;
import entity.Hobby;
import entity.Person;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import utils.EMF_Creator;

/**
 *
 * @author root
 */
public class PersonResourceTest {
    
   private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static Hobby h1, h2, h3, h4;
    private static Address a1, a2;
    private static Person p1, p2;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.CREATE);

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        h1 = new Hobby("Badminton", "its fun");
        h2 = new Hobby("Fodbold", "its fun");
        h3 = new Hobby("Tennis", "its fun");
        h4 = new Hobby("Haandbold", "its fun");
        a1 = new Address("street1", "city1", 2880);
        a2 = new Address("street2", "city2", 2890);
        List<Hobby> hobbies = new ArrayList<>();
        List<Hobby> hobbies2 = new ArrayList<>();
        hobbies.add(h1);
        hobbies.add(h2);
        hobbies2.add(h3);
        hobbies2.add(h4);
        p1 = new Person("email1@ok.dk", "firstname1", "lastname1", "44556677", hobbies, a1);
        p2 = new Person("email2@ok.dk", "firstname2", "lastname2", "44556678", hobbies2, a2);
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();  
            em.persist(h1);
            em.persist(h2);
            em.persist(h3);
            em.persist(h4);
            em.persist(a1);
            em.persist(a2);
            em.persist(p1);
            em.persist(p2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    @Test
    public void testGetPersonByEmail() {
        String expectedEmail = "email1@ok.dk";
        given()
                .contentType(ContentType.JSON)
                .get("person/email/" + expectedEmail)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("email", equalTo(expectedEmail));
    }
    
    @Test
    public void testGetPersonByInvalidEmail() {
        String expectedEmail = "asdas@ok.dk";
        given()
                .contentType(ContentType.JSON)
                .get("person/email/" + expectedEmail)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_ACCEPTABLE_406.getStatusCode())
                .body("message", equalTo("No person found with that email"));
    }
    
    @Test
    public void testGetPersonById() {
        long personId = p1.getId();
        String expectedEmail = "email1@ok.dk";
        given()
                .contentType(ContentType.JSON)
                .get("person/id/" + personId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("email", equalTo(expectedEmail));
    }
    
    @Test
    public void testGetPersonsByHobby() {
        String hobby = "Badminton";
        given()
                .contentType(ContentType.JSON)
                .get("person/hobby/" + hobby)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("firstName[0]", equalTo(p1.getFirstName()));
    }
    
    @Test
    public void testGetAllHobbies(){
        given()
                .contentType(ContentType.JSON)
                .get("person/hobbies")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("size()", is(4));
    }
    
}
