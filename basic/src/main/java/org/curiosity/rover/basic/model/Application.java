package org.curiosity.rover.basic.model;

import java.util.Date;

public class Application extends Entity {
    public Application(String id, String name, String description, Date createDate, Date updateDate, User createdBy, User updatedBy) {
        super(id, name, description, createDate, updateDate, createdBy, updatedBy);
    }

    public static class ApplicationBuilder extends Entity.EntityBuilder {

        @Override
        public Application build() {
            return null;
        }
    }
}
