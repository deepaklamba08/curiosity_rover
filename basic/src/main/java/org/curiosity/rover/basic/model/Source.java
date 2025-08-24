package org.curiosity.rover.basic.model;

import java.util.Date;

public class Source extends Entity {
    public Source(String id, String name, String description, Date createDate, Date updateDate, User createdBy, User updatedBy) {
        super(id, name, description, createDate, updateDate, createdBy, updatedBy);
    }

    public static class SourceBuilder extends EntityBuilder {

        @Override
        public Source build() {
            return null;
        }
    }
}
