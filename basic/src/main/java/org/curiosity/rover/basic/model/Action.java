package org.curiosity.rover.basic.model;

import org.curiosity.rover.basic.cfg.Configuration;

import java.util.Date;

public class Action extends Entity {

    private final String type;

    public Action(String id, String name, String description, Date createDate, Date updateDate, User createdBy, User updatedBy, Configuration config, String type) {
        super(id, name, description, createDate, updateDate, createdBy, updatedBy, config);
        this.type = type;
    }

    public static class ActionBuilder extends EntityBuilder {
        private String type;

        public ActionBuilder withType(String type) {
            this.type = type;
            return this;
        }

        @Override
        public Action build() {
            return new Action(
                    this.id,
                    this.name,
                    this.description,
                    this.createDate,
                    this.updateDate,
                    this.createdBy,
                    this.updatedBy,
                    this.config,
                    this.type);
        }
    }
}
