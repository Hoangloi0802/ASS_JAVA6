package ass.java6.ass.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ass.java6.ass.Entity.Account;

public interface AccountRepository extends JpaRepository<Account, String> {

}
