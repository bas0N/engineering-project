package org.example.order.service;

import org.example.commondto.UserDetailInfoEvent;

public interface UserService {
    UserDetailInfoEvent getUserInfo(String userId);
}
