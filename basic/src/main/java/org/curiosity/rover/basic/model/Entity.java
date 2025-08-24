package org.curiosity.rover.basic.model;

import java.util.Date;

public abstract class Entity {

    private final String id;
    private final String name;
    private final String description;
    private final Date createDate;
    private final Date updateDate;
    private final User createdBy;
    private final User updatedBy;

    public Entity(String id, String name, String description, Date createDate, Date updateDate, User createdBy, User updatedBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public static abstract class EntityBuilder {
        private String id;
        private String name;
        private String description;
        private Date createDate;
        private Date updateDate;
        private User createdBy;
        private User updatedBy;

        public EntityBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public EntityBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public EntityBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public EntityBuilder withCreateDate(Date createDate) {
            this.createDate = createDate;
            return this;
        }

        public EntityBuilder withUpdateDate(Date updateDate) {
            this.updateDate = updateDate;
            return this;
        }

        public EntityBuilder withCreatedBy(User createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public EntityBuilder withUpdatedBy(User updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public abstract Entity build();

    }
}
