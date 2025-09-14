package org.curiosity.rover.basic.model;

import org.curiosity.rover.basic.cfg.Configuration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Application extends Entity {

    private final List<Source> sources;
    private final List<Transformation> transformations;
    private final List<Action> actions;

    public Application(String id, String name, String description, Date createDate, Date updateDate, User createdBy, User updatedBy, Configuration config, List<Source> sources, List<Transformation> transformations, List<Action> actions) {
        super(id, name, description, createDate, updateDate, createdBy, updatedBy, config);
        this.sources = sources;
        this.transformations = transformations;
        this.actions = actions;
    }

    public List<Source> getSources() {
        return sources;
    }

    public List<Transformation> getTransformations() {
        return transformations;
    }

    public List<Action> getActions() {
        return actions;
    }

    public static class ApplicationBuilder extends Entity.EntityBuilder {
        private List<Source> sources;
        private List<Transformation> transformations;
        private List<Action> actions;

        public ApplicationBuilder withSources(List<Source> sources) {
            this.sources = sources;
            return this;
        }

        public ApplicationBuilder withSource(Source source) {
            if (this.sources == null) {
                this.sources = new ArrayList<>();
            }
            this.sources.add(source);
            return this;
        }

        public ApplicationBuilder withTransformation(List<Transformation> transformations) {
            this.transformations = transformations;
            return this;
        }

        public ApplicationBuilder withTransformation(Transformation transformation) {
            if (this.transformations == null) {
                this.transformations = new ArrayList<>();
            }
            this.transformations.add(transformation);
            return this;
        }

        public ApplicationBuilder withAction(List<Action> actions) {
            this.actions = actions;
            return this;
        }

        public ApplicationBuilder withAction(Action action) {
            if (this.actions == null) {
                this.actions = new ArrayList<>();
            }
            this.actions.add(action);
            return this;
        }


        @Override
        public Application build() {
            return new Application(this.id, this.name, this.description, this.createDate, this.updateDate, this.createdBy, this.updatedBy, this.config, this.sources, this.transformations, this.actions);
        }
    }
}
