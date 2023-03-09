package teste.quarkussocial.domain.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import teste.quarkussocial.domain.model.Post;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostRepository implements PanacheRepository<Post> {
}
