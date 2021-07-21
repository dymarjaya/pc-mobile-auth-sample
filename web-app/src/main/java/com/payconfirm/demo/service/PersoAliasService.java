package com.payconfirm.demo.service;

import com.payconfirm.demo.model.CreateAliasResponse;
import com.payconfirm.demo.model.GetPCUserAliasResponse;
import com.payconfirm.demo.model.GetPCUserAliasRequest;

public interface PersoAliasService {

    CreateAliasResponse genCredential();

    GetPCUserAliasResponse getPCUserAlias(GetPCUserAliasRequest request);
}
