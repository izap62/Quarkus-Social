package teste.quarkussocial.rest;


import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import teste.quarkussocial.domain.model.Follower;
import teste.quarkussocial.domain.model.Post;
import teste.quarkussocial.domain.model.User;
import teste.quarkussocial.domain.repository.FollowerRepository;
import teste.quarkussocial.domain.repository.PostRepository;
import teste.quarkussocial.domain.repository.UserRepository;
import teste.quarkussocial.rest.dto.CreatePostRequest;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
public class PostResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    @Inject
    PostRepository postRepository;


    Long UserId;
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    public void setup(){

        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        UserId = user.getId();
        //---------------------------------------------
        User userNotFollower = new User();
        userNotFollower.setName("Ciclano");
        userNotFollower.setAge(25);
        userRepository.persist(userNotFollower);

        userNotFollowerId = userNotFollower.getId();
        //---------------------------------------------
        var userFollower = new User();
        userFollower.setAge(27);
        userFollower.setName("João");
        userRepository.persist(userFollower);

        userFollowerId = userFollower.getId();
        //----------------------------------------------
        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);

        followerRepository.persist(follower);
        //----------------------------------------------
        Post post = new Post();
        post.setUser(user);
        post.setText("Teste Unit");
        postRepository.persist(post);


    }

    @Test
    @DisplayName("Should create a post for an user")
    public void createPostTest(){

        var postRequest = new CreatePostRequest();

        postRequest.setText("Olá Mundo");

        given().contentType(ContentType.JSON).body(postRequest).pathParam("userId", UserId)
                .when().post().then().statusCode(201);
    }

    @Test
    @DisplayName("Should return 404 when try do make a post a null user")
    public void postForInexistentUserTest(){

        var postRequest = new CreatePostRequest();
        var inexistent = 999;

        postRequest.setText("Olá Mundo");

        given().contentType(ContentType.JSON).body(postRequest).pathParam("userId", inexistent)
                .when().post().then().statusCode(404);
    }

    @Test
    @DisplayName("Should return 404 when user does not exist")
    public void ListPostUserNotFoundTest(){

        var inexistentUserId = 999;

        given().pathParam("userId",inexistentUserId).when().get().then().statusCode(404);
    }

    @Test
    @DisplayName("Should return 400 when follower Id header is not present")
    public void ListPostFollowerHeaderNotSendTest(){

        given().pathParam("userId",UserId)
                .when().get().then().statusCode(400).body(Matchers.is("Nonexistent follower header!!"));
    }

    @Test
    @DisplayName("Should return 400 when follower does not exist")
    public void ListPostNotFoundFollowerTest(){

        var inexistentFollowerId = 999;

        given().pathParam("userId",UserId).header("followerId",inexistentFollowerId)
                .when().get().then().statusCode(400).body(Matchers.is("Follower does not exist"));
    }

    @Test
    @DisplayName("Should return 403 when follower is not a follower")
    public void ListPostNotAFolllowerTest(){

        given().pathParam("userId",UserId).header("followerId",userNotFollowerId)
                .when().get().then().statusCode(403).body(Matchers.is("Can not see this user posts"));
    }

    @Test
    @DisplayName("Should return posts")
    public void ListPostTest(){

        given().pathParam("userId",UserId).header("followerId",userFollowerId)
                .when().get().then().statusCode(200).body("size()", Matchers.is(1));
    }
}
