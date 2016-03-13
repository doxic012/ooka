package org.ooka.sfisc12s.runtime.environment.scope;


public interface Scopeable {

    enum Scope {
        UnderTest("Under Test"),
        UnderInspection("Under Inspection"),
        InMaintenance("In Maintenance"),
        InProduction("In Production");

        Scope(String scope) {
        }
    }

    Scope getScope();

    void setScope(Scope scope);
}
