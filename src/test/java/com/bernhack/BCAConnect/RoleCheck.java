package com.bernhack.BCAConnect;

import com.bernhack.BCAConnect.enums.RoleEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RoleCheck {

    @Test
    void prints(){
        for(RoleEnum r:RoleEnum.values()){
            System.out.println(r.name());
        }
    }
}
