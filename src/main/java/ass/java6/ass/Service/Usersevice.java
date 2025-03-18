package ass.java6.ass.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ass.java6.ass.Entity.Account;
import ass.java6.ass.Repository.AccountRepository;

@Service
public class Usersevice {
    @Autowired
    AccountRepository accountRepository;

    public void save(Account account) {
        accountRepository.save(account);
    }
}
