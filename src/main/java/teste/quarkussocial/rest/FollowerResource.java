package teste.quarkussocial.rest;

import teste.quarkussocial.domain.model.Follower;
import teste.quarkussocial.domain.model.User;
import teste.quarkussocial.domain.repository.FollowerRepository;
import teste.quarkussocial.domain.repository.UserRepository;
import teste.quarkussocial.rest.dto.CreateFollowerRequest;
import teste.quarkussocial.rest.dto.FollowerResponse;
import teste.quarkussocial.rest.dto.FollowersPerUserResponse;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private FollowerRepository followerRepository;
    private UserRepository userRepository;

    @Inject
    public FollowerResource(FollowerRepository followerRepository, UserRepository userRepository){

        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
    }

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, CreateFollowerRequest request ){

        var user = userRepository.findById(userId);
        var follower = userRepository.findById(request.getFollowerId());

        if (user == null){

            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (user.equals(follower)){
            return Response.status(Response.Status.CONFLICT).entity("It's not possible to follow yourself").build();
        }

        boolean followers = followerRepository.Follows(follower, user);
        if (!followers){
            var entity = new Follower();

            entity.setUser(user);
            entity.setFollower(follower);

            followerRepository.persist(entity);
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId){

        List<Follower> list = followerRepository.findByUser(userId);
        User user = userRepository.findById(userId);

        if (user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        FollowersPerUserResponse responseObject = new FollowersPerUserResponse();
        responseObject.setFollowersCount(list.size());

        List<FollowerResponse> listFollowers = list.stream().map(FollowerResponse::new).collect(Collectors.toList());

        responseObject.setContent(listFollowers);

        return Response.ok(responseObject).build();

    }

    @DELETE
    @Transactional
    public Response unfollowUser(@PathParam("userId") Long userId, @QueryParam("followerId") Long followerId){

        User user = userRepository.findById(userId);

        if (user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        followerRepository.deleteByFollowerAndUser(followerId,userId);
        return Response.status(Response.Status.NO_CONTENT).entity(followerId).build();

    }
}
