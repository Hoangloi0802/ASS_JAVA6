package ass.java6.ass.Repository;

import ass.java6.ass.Entity.Account;
import ass.java6.ass.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
        Optional<Account> findByUsername(String username);

        Optional<Account> findByEmail(String email);

        Optional<Account> findByUsernameAndPassword(String username, String password);

        Optional<Account> findById(String username);

        List<Account> findByUsernameContainingIgnoreCaseOrFullnameContainingIgnoreCase(String username,
                        String fullname);

        List<Account> findByRole(Role role);

        List<Account> findByActivated(Boolean activated);

        @Query("SELECT COUNT(a) FROM Account a WHERE a.role = 'CUST'")
        long countNewCustomers();
}
