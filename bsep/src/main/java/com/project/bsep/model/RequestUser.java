package com.project.bsep.model;

import com.project.bsep.dto.UserCreationDto;

public class RequestUser {

        private UserCreationDto param1;
        private String param2;

    public UserCreationDto getParam1() {
        return param1;
    }

    public void setParam1(UserCreationDto param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }
}
