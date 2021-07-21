package com.payconfirm.demo.service;

import com.payconfirm.demo.model.AuthRequest;
import com.payconfirm.demo.model.AuthResponse;
import com.payconfirm.demo.model.FinishAuthResponse;

public interface AuthenticationService {
    AuthResponse startAuthentication(AuthRequest request);

    FinishAuthResponse finishAuthentication(AuthRequest request);

    void callbackReceiver(String request);
}
