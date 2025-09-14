package org.curiosity.rover.dao.intf;

import org.curiosity.rover.basic.model.Application;

public interface IApplicationRepository {

    public Application lookupApplication(String id);
}
