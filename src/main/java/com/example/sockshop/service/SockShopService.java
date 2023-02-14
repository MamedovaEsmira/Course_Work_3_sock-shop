package com.example.sockshop.service;

import com.example.sockshop.model.Color;
import com.example.sockshop.model.Size;
import com.example.sockshop.model.Socks;

public interface SockShopService {
    void addSocks(Socks socks);

    Socks editSocksFromStock(Socks socks);

    int getSocks(Color color, Size size, int cottonMin, int cottonMax);

    boolean removeSocks(Socks socks);
}