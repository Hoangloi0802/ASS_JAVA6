package ass.java6.ass.Service.Impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import ass.java6.ass.AssApplication;
import ass.java6.ass.Dto.DangkyRequest;
import ass.java6.ass.Entity.Account;
import ass.java6.ass.Entity.Role;
import ass.java6.ass.Repository.AccountRepository;
import ass.java6.ass.Service.AccoutService;
import jakarta.validation.Valid;

@Service
public class Accoutlmpl implements AccoutService  {

    private final AssApplication assApplication;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private AccountRepository accountRepository;

    Accoutlmpl(AssApplication assApplication) {
        this.assApplication = assApplication;
    }

    @Override
    public Account Dangnhap(String username ,String password) {
        Optional<Account> accOptional = accountRepository.findByUsername(username);
        if(accOptional.isPresent()){
            Account account = accOptional.get();
            if(bCryptPasswordEncoder.matches(password, account.getPassword())){
                return account;
            }else{
                throw new IllegalArgumentException("Mật khẩu không đúng!");
            }
             
        }else{
            throw new IllegalArgumentException("Tài khoản không tồn tại!");
        }
    }

    @Override
    public Account Dangky(@Valid @ModelAttribute("accoutDangky") DangkyRequest newaccout) {
        if(accountRepository.findByUsername(newaccout.getUsername()).isPresent()){
            throw new IllegalArgumentException("Tài khoản đã tồn tại!");
        }
        if(accountRepository.findByEmail(newaccout.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email đã tồn tại");
        }
        if(newaccout.getUsername().equals(newaccout.getPassword())){
            throw new IllegalArgumentException("Tài khoản không được trùng với mật khẩu ");
        }
            Account a = new Account();
            a.setUsername(newaccout.getUsername());
            a.setFullname(newaccout.getFullname());
            a.setPassword(bCryptPasswordEncoder.encode(newaccout.getPassword()));
            a.setActivated(false);
            a.setEmail(newaccout.getEmail());
            a.setRole(Role.ROLE_ADMIN);
          return  accountRepository.save(a);
    }

    
}
