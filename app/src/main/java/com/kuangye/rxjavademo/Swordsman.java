package com.kuangye.rxjavademo;

import java.util.Objects;

public class Swordsman {
    private String name;
    private String level;

    public Swordsman(String name, String level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Swordsman)) return false;
        Swordsman swordsman = (Swordsman) o;
        return Objects.equals(name, swordsman.name) &&
                Objects.equals(level, swordsman.level);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, level);
    }

    @Override
    public String toString() {
        return "Swordsman{" +
                "name='" + name + '\'' +
                ", level='" + level + '\'' +
                '}';
    }
}
