package teste.quarkussocial.domain.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import teste.quarkussocial.domain.model.User;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

}
