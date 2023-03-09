package teste.quarkussocial.rest;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import teste.quarkussocial.domain.model.Follower;
import teste.quarkussocial.domain.model.User;
import teste.quarkussocial.domain.repository.FollowerRepository;
import teste.quarkussocial.domain.repository.PostRepository;
import teste.quarkussocial.domain.repository.UserRepository;
import teste.quarkussocial.rest.dto.CreateFollowerRequest;
import teste.quarkussocial.rest.dto.ResponseError;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FollowerResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    PostRepository postRepository;

    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    public void setUp(){
        //Usuário
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        //Seguidor
        var follower = new User();
        follower.setName("Maria");
        follower.setAge(23);
        userRepository.persist(follower);
        followerId = follower.getId();

        //Cria um follower
        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);
    }

    @Test
    @DisplayName("Should return 409 if the user wants to follower himself")
    @Order(1)
    public void sameUserAndFollowerTest(){

        var body = new CreateFollowerRequest();
        body.setFollowerId(userId);

        given().contentType(ContentType.JSON).body(body).pathParam("userId", userId).when().put().then()
                .statusCode(409).body(Matchers.is("It's not possible to follow yourself"));

    }

    @Test
    @DisplayName("Should return 404 if the user does not exist when tries do follow")
    @Order(2)
    public void userDoesNotExistWhenTryingToFollowTest(){

        var body = new CreateFollowerRequest();
        body.setFollowerId(userId);

        var inexistentUserId = 100;

        given().contentType(ContentType.JSON).body(body).pathParam("userId", inexistentUserId).when().put().then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should follow a user")
    @Order(3)
    public void followUserTest(){

        var body = new CreateFollowerRequest();
        body.setFollowerId(followerId);

        given().contentType(ContentType.JSON).body(body).pathParam("userId", userId).when().put().then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 on list user followers and user id does not exist")
    @Order(4)
    public void userNotFoundWhenListingFollowersTest(){

        var inexistentUserId = 100;

        given().contentType(ContentType.JSON).pathParam("userId", inexistentUserId).when().get().then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should list a user followers")
    @Order(5)
    public void listFollowersTest(){

        var response = given().
                contentType(ContentType.JSON).pathParam("userId", userId).when().get().then().extract().response();

        var followersCount = response.jsonPath().get("followersCount");
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
        assertEquals(1,followersCount);
    }

    @Test
    @DisplayName("Should return 404 on unfollow user and User does not exist")
    @Order(6)
    public void userNotFoundWhenUnfollowUserTest() {

        var inexistentUserId = 100;

        given().contentType(ContentType.JSON).pathParam("userId", inexistentUserId)
                .queryParam("followerId", followerId).when().delete().then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should unfollow an user")
    @Order(7)
    public void unfollowUserTest() {

        given().contentType(ContentType.JSON).pathParam("userId", userId)
                .queryParam("followerId", followerId).when().delete().then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
}
