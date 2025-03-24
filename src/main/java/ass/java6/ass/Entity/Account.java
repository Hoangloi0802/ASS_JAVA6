package ass.java6.ass.Entity;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "accounts")
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String username;

    private String password;
    private String fullname;
    private String email;
    private String photo;
    private String mobile;
    private String address;
    private Boolean activated;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String activationCode;
    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private List<Order> orders;
}
