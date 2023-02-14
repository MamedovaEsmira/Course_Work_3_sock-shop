package com.example.storeforsocks.service.impl;
import com.example.storeforsocks.exceptions.ProductNotFoundException;
import com.example.storeforsocks.model.Color;
import com.example.storeforsocks.model.OperationType;
import com.example.storeforsocks.model.Size;
import com.example.storeforsocks.model.Socks;
import com.example.storeforsocks.service.StoreForSockService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

    @Service
    public class StoreForSockServiceImpl implements StoreForSockService {

        public Set<Socks> socksStock = new LinkedHashSet<>();
        public Map<Operation, Socks> operationSocksMap = new LinkedHashMap<>();

        private final FilesServiceImpl filesService;

        public StoreForSockServiceImpl(FilesServiceImpl filesService) {
            this.filesService = filesService;
        }

        @PostConstruct
        private void init() {
            try {
                readFromFile();
                readFromOperationFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void addSocks(Socks socks) {
            if (socksStock.contains(socks)) {
                for (Socks socks1 : socksStock) {
                    socks1.setQuantity(socks1.getQuantity() + socks.getQuantity());
                }
            } else {
                socksStock.add(socks);
            }
            operationSocksMap.put(new Operation(OperationType.ACCEPTANCE.getTranslate()), socks);
            saveToFile();
            saveToOperationFile();
        }

        @Override
        public int getSocks(Color color, Size size, int cottonMin, int cottonMax) {
            for (Socks sock : socksStock) {
                if (sock.getColor().equals(color) &&
                        sock.getSize().equals(size) &&
                        sock.getCottonPart() > cottonMin &&
                        sock.getCottonPart() < cottonMax) {
                    return sock.getQuantity();
                } else if (!socksStock.iterator().hasNext()) {
                    throw new ProductNotFoundException("Товар с данными параметрами не найден");
                }
            }
            return 0;
        }


        @Override
        public Socks editSocksFromStock(Socks socks) {
            for (Socks socks1 : socksStock) {
                if (socks1.getColor().equals(socks.getColor()) &&
                        socks1.getSize().equals(socks.getSize()) &&
                        socks1.getCottonPart() == socks.getCottonPart() &&
                        socks1.getQuantity() > socks.getQuantity()) {
                    socks1.setQuantity(socks1.getQuantity() - socks.getQuantity());
                    operationSocksMap.put(new Operation(OperationType.EXTRADITION.getTranslate()), socks1);
                    saveToFile();
                    saveToOperationFile();
                } else if (!socksStock.iterator().hasNext()) {
                    throw new ProductNotFoundException("Недостаточно товара на складе.");
                }
            }
            return socks;
        }
        @Override
        public boolean removeSocks(Socks socks) {
            for (Socks socks1 : socksStock) {
                if (socks1.getColor().equals(socks.getColor()) &&
                        socks1.getSize().equals(socks.getSize()) &&
                        socks1.getCottonPart() == socks.getCottonPart() &&
                        socks1.getQuantity() > socks.getQuantity()) {
                    socks1.setQuantity(socks1.getQuantity() - socks.getQuantity());
                    operationSocksMap.put(new Operation(OperationType.WRITE_DOWNS.getTranslate()), socks1);
                    saveToFile();
                    saveToOperationFile();
                    return true;
                } else if (!socksStock.iterator().hasNext()) {
                    throw new ProductNotFoundException("Невозможно удалить товар. Товар с данными параметрами не найден.");
                }
            }
            return false;
        }

        private void saveToFile() {
            try {
                String json = new ObjectMapper().writeValueAsString(socksStock);
                filesService.saveToFile(json);
            } catch (JsonProcessingException e) {
                throw new ProductNotFoundException("Ошибка в сохранении файла.");
            }
        }

        private void readFromFile() {
            String json = filesService.readFromFile();
            try {
                socksStock = new ObjectMapper().readValue(json, new TypeReference<LinkedHashSet<Socks>>() {
                });
            } catch (JsonProcessingException e) {
                throw new ProductNotFoundException("Ошибка в чтении файла.");
            }
        }

        private void saveToOperationFile() {
            try {
                String json = new ObjectMapper().writeValueAsString(operationSocksMap);
                filesService.saveToOperationFile(json);
            } catch (JsonProcessingException e) {
                throw new ProductNotFoundException("Ошибка в сохранении файла.");
            }
        }

        private void readFromOperationFile() {
            String json = filesService.readFromOperationFile();
            try {
                operationSocksMap = new ObjectMapper().readValue(json, new TypeReference<LinkedHashMap<Operation, Socks>>() {
                });
            } catch (JsonProcessingException e) {
                throw new ProductNotFoundException("Ошибка в чтении файла.");
            }
        }
    }