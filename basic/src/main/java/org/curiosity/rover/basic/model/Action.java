package org.curiosity.rover.basic.model;

import java.util.Date;

public class Action extends Entity {
    public Action(String id, String name, String description, Date createDate, Date updateDate, User createdBy, User updatedBy) {
        super(id, name, description, createDate, updateDate, createdBy, updatedBy);
    }

    public static class ActionBuilder extends EntityBuilder {

        @Override
        public Action build() {
            return null;
        }
    }
}
