package org.example.message.service;

import org.example.commondto.UserDetailInfoEvent;

public interface UserService {
    UserDetailInfoEvent getUserInfo(String userId);
}
