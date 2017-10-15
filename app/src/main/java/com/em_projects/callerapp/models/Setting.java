package com.em_projects.callerapp.models;

/**
 * Created by eyalmuchtar on 15/09/2017.
 */

public class Setting {
    private String name;
    private int iconId;
    private int id;

    public Setting(String name, int iconId, int id) {
        this.name = name;
        this.iconId = iconId;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getIconId() {
        return iconId;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Setting setting = (Setting) o;

        if (iconId != setting.iconId) return false;
        if (id != setting.id) return false;
        return name.equals(setting.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + iconId;
        result = 31 * result + id;
        return result;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "name='" + name + '\'' +
                ", iconId=" + iconId +
                ", id=" + id +
                '}';
    }
}
