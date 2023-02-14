package com.example.sockshop.controllers;

import com.example.sockshop.model.Color;
import com.example.sockshop.model.Size;
import com.example.sockshop.model.Socks;
import com.example.sockshop.service.impl.SockShopServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


    @RestController
    @RequestMapping("/api/socks")
    @Tag(name = "Socks market", description = "CRUD операции для работы со складом носков.")
    public class SockShopController {

        private final SockShopServiceImpl storeForSockService;

        public SockShopController(SockShopServiceImpl storeForSockService) {
            this.storeForSockService = storeForSockService;
        }

        @GetMapping("/get/{color}&{size}&{cottonMin}&{cottonMax}")
        @Operation(summary = "Вывод количества товара.", description = "Вывод количества носков по заданным параметрам.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200",
                        description = "Выведено количество товара по запросу."),
                @ApiResponse(responseCode = "400",
                        description = "Параметры запроса отсутствуют или имеют некорректный формат."),
                @ApiResponse(responseCode = "500",
                        description = "Произошла ошибка, не зависящая от вызывающей стороны.")
        })
        public ResponseEntity<Object> getSocks(@PathVariable Color color,
                                               @PathVariable Size size,
                                               @PathVariable  int cottonMin,
                                               @PathVariable  int cottonMax){
            int socksCount = storeForSockService.getSocks(color, size, cottonMin, cottonMax);
            if(socksCount == 0){
                ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(socksCount);
        }


        @PostMapping("/add")
        @Operation(summary = "Приход товара на склад.", description = "Добавление носков на склад по их параметрам.")
        @ApiResponses (value = {
                @ApiResponse(responseCode = "200",
                        description = "Товар добавлен на склад.",
                        content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Socks.class))
                        }
                ),
                @ApiResponse(responseCode = "400",
                        description = "Параметры запроса отсутствуют или имеют некорректный формат."),
                @ApiResponse(responseCode = "500",
                        description = "Произошла ошибка, не зависящая от вызывающей стороны.")
        })
        public ResponseEntity<Socks> addSocks (@RequestBody Socks socks) {
            storeForSockService.addSocks(socks);
            return ResponseEntity.ok().body(socks);
        }

        @PutMapping("/edit")
        @Operation(summary = "Отпуск товара со склада.",
                description = "Получение носков со склада по параметрам, на складе количество уменьшается.")
        @ApiResponses (value = {
                @ApiResponse(responseCode = "200",
                        description = "Товар получен со склада.",
                        content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Socks.class))
                        }
                ),
                @ApiResponse(responseCode = "400",
                        description = "Параметры запроса отсутствуют или имеют некорректный формат."),
                @ApiResponse(responseCode = "500",
                        description = "Произошла ошибка, не зависящая от вызывающей стороны.")
        })
        public ResponseEntity<Socks> editSocks(@RequestBody Socks socks){
            Socks socks1 = storeForSockService.editSocksFromStock(socks);
            if(socks1 == null){
                ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(socks1);
        }

        @DeleteMapping("/remove")
        @Operation(summary = "Списание товара со склада.",
                description = "Получение носков со склада по параметрам, на складе количество уменьшается.")
        @ApiResponses (value = {
                @ApiResponse(responseCode = "200",
                        description = "Товар списан со склада.",
                        content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Socks.class))
                        }
                ),
                @ApiResponse(responseCode = "400",
                        description = "Параметры запроса отсутствуют или имеют некорректный формат."),
                @ApiResponse(responseCode = "500",
                        description = "Произошла ошибка, не зависящая от вызывающей стороны.")
        })
        public ResponseEntity<Void> deleteSocks(@RequestBody Socks socks){
            if(storeForSockService.removeSocks(socks)){
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        }
    }