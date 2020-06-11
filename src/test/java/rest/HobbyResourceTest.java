/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import dtos.HobbyDTO;
import entity.Address;
import entity.Hobby;
import entity.Person;
import entity.Role;
import entity.User;
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
import static rest.BaseResourceTest.testProps;
import utils.EMF_Creator;

/**
 *
 * @author root
 */
public class HobbyResourceTest {

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
            em.createQuery("delete from User").executeUpdate();
            em.createQuery("delete from Role").executeUpdate();

            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            User user = new User("user", "test");
            user.addRole(userRole);
            User admin = new User("admin", "test");
            admin.addRole(adminRole);
            User both = new User("user_admin", "test");
            both.addRole(userRole);
            both.addRole(adminRole);
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.persist(both);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    //This is how we hold on to the token after login, similar to that a client must store the token somewhere
    private static String securityToken;

    private static void login(String role, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", role, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                //.when().post("/api/login")
                .when().post("/login")
                .then()
                .extract().path("token");
        System.out.println("TOKEN ---> " + securityToken);
    }

    @Test
    public void testGetAllHobbies() {
        given()
                .contentType(ContentType.JSON)
                .get("hobby/all")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("size()", is(4));
    }

    @Test
    public void testCreateHobby() {
        HobbyDTO hobbyDTO = new HobbyDTO("football", "i just play");
        login("admin", "test");
        given()
                .contentType(ContentType.JSON)
                .header("x-access-token", securityToken)
                .body(hobbyDTO)
                .when()
                .post("/hobby")
                .then()
                .body("name", equalTo("football"))
                .body("description", equalTo("i just play"));
    }

    @Test
    public void testEditHobby() {
        HobbyDTO hobbyDTO = new HobbyDTO(h1);
        hobbyDTO.setName("hejhej");
        login("admin", "test");
        given()
                .contentType(ContentType.JSON)
                .header("x-access-token", securityToken)
                .body(hobbyDTO)
                .when()
                .put("/hobby")
                .then()
                .body("name", equalTo(hobbyDTO.getName()));
    }

    @Disabled
    @Test
    public void testRemoveHobby() {
        HobbyDTO hobbyDTO = new HobbyDTO(h1);
        String loginPayload = "{\"username\":\"" + testProps.getProperty("user1_username") + "\",\"password\":\"" + testProps.getProperty("user1_password") + "\"}";
        login("admin", "test");
        given()
                .contentType(ContentType.JSON)
                .header("x-access-token", securityToken)
                .body(hobbyDTO)
                .when()
                .delete("/hobby")
                .then()
                .body("name", equalTo(hobbyDTO.getName()));
    }

}
