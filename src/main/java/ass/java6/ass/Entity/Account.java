package ass.java6.ass.Entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "accounts")
public class Account implements Serializable {
    @Id
    private String username;
    private String password;
    private String fullname;
    private String email;
    private String address;
    private String mobile;
    private String photo;
    boolean activated;
    private Role role;
    String activationCode;
    @OneToMany(mappedBy = "account")
    List<Order> orders;
}
