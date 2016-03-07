package org.ooka.sfisc12s.runtime.environment.component.scope;


public class Scope {
    private String name;

    public static String UnderTest = "Under Test";
    public static String InProduction = "In Production";
    public static String UnderInspection = "Under Inspection";
    public static String InMaintenance = "In Maintenance";

    public Scope(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
