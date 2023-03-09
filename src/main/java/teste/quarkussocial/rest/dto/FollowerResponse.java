package teste.quarkussocial.rest.dto;

import lombok.Data;
import teste.quarkussocial.domain.model.Follower;

@Data
public class FollowerResponse {
    private Long id;
    private String name;

    public FollowerResponse() {
    }

    public FollowerResponse(Follower follower){
         this(follower.getId(), follower.getFollower().getName());
    }

    public FollowerResponse(Long id, String name){
        this.id = id;
        this.name = name;
    }
}
