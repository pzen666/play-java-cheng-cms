package models;

import io.ebean.Model;
import jakarta.persistence.*;

@Entity
@Table(name = "user",schema = "public")
public class User extends Model {

    public static final io.ebean.Finder<Long, User> find = new io.ebean.Finder<>(User.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String phone;
    public String email;
    public String password;

}
