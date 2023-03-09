package teste.quarkussocial.rest;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import teste.quarkussocial.domain.model.User;
import teste.quarkussocial.domain.repository.UserRepository;
import teste.quarkussocial.rest.dto.CreateUserRequest;
import teste.quarkussocial.rest.dto.ResponseError;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private UserRepository repository;
    private Validator validator;

    @Inject
    public UserResource(UserRepository repository, Validator validator ){
        this.repository = repository;
        this.validator = validator;
    }
    @POST
    @Transactional
    public Response createUser( CreateUserRequest userRequest ){

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);
        if (!violations.isEmpty()){

            return ResponseError.createFromValidation(violations)
                    .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }
        User user = new User();
        user.setAge(userRequest.getAge());
        user.setName(userRequest.getName());

        repository.persist(user);

        return  Response.status(Response.Status.CREATED.getStatusCode())
                .entity(user)
                .build();
    }
    @GET
    public Response listAllUser(){

        PanacheQuery<User> query = repository.findAll();
        return Response.ok(query.list()).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id){

        User userById = repository.findById(id);

        if (repository != null){
            repository.delete(userById);
            return Response.noContent().build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData){

        User upUser = repository.findById(id);

        if (upUser!=null){

            upUser.setName(userData.getName());
            upUser.setAge(userData.getAge());

            return Response.noContent().build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
