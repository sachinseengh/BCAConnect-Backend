package com.bernhack.BCAConnect.initialDataLoader;

import com.bernhack.BCAConnect.Exception.AppException;
import com.bernhack.BCAConnect.entity.Role;
import com.bernhack.BCAConnect.entity.User;
import com.bernhack.BCAConnect.enums.RoleEnum;
import com.bernhack.BCAConnect.repository.RoleRepository;
import com.bernhack.BCAConnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


//    @Autowired
//    private PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args)  {
        loadRoles();
        try {
            loadAdminUser();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void loadRoles(){

        for(RoleEnum roleEnum : RoleEnum.values()){
            roleRepository.findByName(roleEnum.name()).orElseGet(()->{
                Role role = new Role();
                role.setName(roleEnum.name());
                return roleRepository.save(role);
            });


        }
    }
    public void loadAdminUser() throws Throwable {

        if(!userRepository.findByUserName("sachin").isPresent()){
            Role role = roleRepository.findByName(RoleEnum.ADMIN.name()).orElseThrow(()->new AppException("Role not Found"));
            User user = new User();
            user.setFullName("sachin singh");
            user.setUserName("sachin");
            user.setEmail("sachinseengh@gmail.com");
            user.setSemester("Sixth");
            String password = passwordEncoder.encode("sachin");
            user.setPassword(password);
            user.getRoles().add(role);
            userRepository.save(user);
        }

    }
}
