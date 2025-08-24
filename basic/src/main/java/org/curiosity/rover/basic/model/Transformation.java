package org.curiosity.rover.basic.model;

import java.util.Date;

public class Transformation extends Entity {
    public Transformation(String id, String name, String description, Date createDate, Date updateDate, User createdBy, User updatedBy) {
        super(id, name, description, createDate, updateDate, createdBy, updatedBy);
    }

    public static class TransformationBuilder extends EntityBuilder {

        @Override
        public Transformation build() {
            return null;
        }
    }
}
