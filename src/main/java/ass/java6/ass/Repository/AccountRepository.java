package ass.java6.ass.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ass.java6.ass.Entity.Account;

public interface AccountRepository extends JpaRepository<Account, String> {
    Account findByUsernameOrEmail(String username, String email);

    Optional<Account> findByUsername(String username);

    Optional<Account> findByActivationCode(String activationCode);
}
