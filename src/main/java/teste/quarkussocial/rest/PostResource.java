package teste.quarkussocial.rest;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import teste.quarkussocial.domain.model.Post;
import teste.quarkussocial.domain.model.User;
import teste.quarkussocial.domain.repository.FollowerRepository;
import teste.quarkussocial.domain.repository.PostRepository;
import teste.quarkussocial.domain.repository.UserRepository;
import teste.quarkussocial.rest.dto.CreatePostRequest;
import teste.quarkussocial.rest.dto.PostResponse;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {


    private UserRepository userRepository;
    private PostRepository postRepository;
    private FollowerRepository followerRepository;

    @Inject
    public PostResource(UserRepository userRepository, PostRepository postRepository,
                        FollowerRepository followRepository){

        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followerRepository = followRepository;
    }


    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request){

        User user = userRepository.findById(userId);

        if (user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setText(request.getText());
        post.setUser(user);
        postRepository.persist(post);


        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPosts (@PathParam("userId") Long userId, @HeaderParam("followerId") Long followerId){

        User user = userRepository.findById(userId);

        if (user == null){
            return Response.status(Response.Status.NOT_FOUND).entity("User does not exist").build();
        }

        if (followerId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Nonexistent follower header!!").build();
        }

        User follower = userRepository.findById(followerId);

        if (follower == null){
            return Response.status(Response.Status.BAD_REQUEST).entity("Follower does not exist").build();
        }

        boolean follows = followerRepository.Follows(follower, user);
        if (!follows){
            return Response.status(Response.Status.FORBIDDEN).entity("Can not see this user posts").build();
        }

        var userQuery = postRepository.find("user", Sort.by("dateTime", Sort.Direction.Descending), user);
        var list = userQuery.list();

        List<PostResponse> collect = list.stream().map(post -> PostResponse.fromEntity(post)).collect(Collectors.toList());

        return Response.ok(collect).build();
    }
}
