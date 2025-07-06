package models;

import io.ebean.Model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user"  )
public class User extends Model {

    public static final io.ebean.Finder<Long, User> find = new io.ebean.Finder<>(User.class);

    @Id
    public Long id;
    public String name;
    public String email;
    public String password;

}
