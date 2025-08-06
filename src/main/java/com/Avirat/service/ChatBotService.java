package com.Avirat.service;

import com.Avirat.model.CoinDTO;
import com.Avirat.response.ApiResponse;

public interface ChatBotService {
    ApiResponse getCoinDetails(String coinName);

    CoinDTO getCoinByName(String coinName);

    String simpleChat(String prompt);
}
