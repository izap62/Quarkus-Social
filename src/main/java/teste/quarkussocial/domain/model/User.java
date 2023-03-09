package teste.quarkussocial.domain.model;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "name")
    private String name;

    @Column(name = "age")
    private Integer age;

}
