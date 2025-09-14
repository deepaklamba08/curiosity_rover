package org.curiosity.rover.basic.model;

import org.curiosity.rover.basic.cfg.Configuration;

import java.util.Date;

public class Transformation extends Entity {
    private final String type;

    public Transformation(String id, String name, String description, Date createDate, Date updateDate, User createdBy, User updatedBy, Configuration config, String type) {
        super(id, name, description, createDate, updateDate, createdBy, updatedBy, config);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static class TransformationBuilder extends EntityBuilder {
        private String type;

        public TransformationBuilder withType(String type) {
            this.type = type;
            return this;
        }

        @Override
        public Transformation build() {
            return new Transformation(
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
