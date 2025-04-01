package com.example.ewalled.app.refresh_token.service;

import com.example.ewalled.dto.RefreshTokenDto;
import com.example.ewalled.entity.RefreshToken;
import com.example.ewalled.entity.ServiceData;

public interface IRefreshTokenService {
    ServiceData<RefreshTokenDto.Response> save(RefreshTokenDto.Refresh dto);
}
