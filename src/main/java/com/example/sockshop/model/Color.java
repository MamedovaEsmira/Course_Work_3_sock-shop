package com.example.sockshop.model;

public enum Color {
    RED("Красный"), GREEN("Зеленый"), BLUE("Синий"), BLACK("Черный"), WHITE("Белый"), YELLOW("Желтый");

    private String translate;

    Color(String translate) {
        this.translate = translate;
    }
}
