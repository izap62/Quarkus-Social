package teste.quarkussocial.rest;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import teste.quarkussocial.rest.dto.CreateUserRequest;
import teste.quarkussocial.rest.dto.ResponseError;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @TestHTTPResource("/users")
    URL apiUrl;


    @Test
    @DisplayName("Should create an user")
    @Order(1)
    public void createUserTest(){
        var user = new CreateUserRequest();
        user.setName("Joao");
        user.setAge(30);

        Response answer =
                given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(apiUrl)
                .then().extract().response();

        assertEquals(201, answer.statusCode());
        assertNotNull(answer.jsonPath().getString("id"));
    }

    @Test
    @DisplayName("Should return an error when JSON is not valid")
    @Order(2)
    public void createUserValidationErrorTest(){

        CreateUserRequest user2 = new CreateUserRequest();

        user2.setName(null);
        user2.setAge(null);

        Response response = given().contentType(ContentType.JSON).body(user2)
                .when().post(apiUrl).then().extract().response();

        // Erro 422
        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS,response.getStatusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String,String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));

        //assertEquals("NeedAnAge!", errors.get(0).get("message"));
        //assertEquals("NeedAName!",errors.get(1).get("message"));
    }

    @Test
    @DisplayName("Should return all the users")
    @Order(3)
    public void listAllUsersTest(){

        given().contentType(ContentType.JSON)
                .when().post(apiUrl).then().statusCode(200).body("size()", Matchers.is(1));

    }
}