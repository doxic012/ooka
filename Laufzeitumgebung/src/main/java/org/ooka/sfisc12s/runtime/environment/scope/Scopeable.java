package org.ooka.sfisc12s.runtime.environment.scope;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface Scopeable {

    enum Scope {
        UnderTest("Under Test", "Under Test", "Under Inspection"),
        UnderInspection("Under Inspection", "Under Test", "Under Inspection"),
        InMaintenance("In Maintenance"),
        InProduction("In Production", "In Production");

        private String name;

        private List<String> applicableScopes;

        public boolean isApplicableScope(Scope scope) {
            return getApplicableScopes().contains(scope.getName());
        }

        public boolean isApplicableScope(String scope) {
            return getApplicableScopes().contains(scope);
        }

        public List<String> getApplicableScopes() {
            return applicableScopes;
        }

        public String getName() {
            return name;
        }

        Scope(String name, String... scopes) {
            this.name = name;
            this.applicableScopes = scopes != null ? Arrays.asList(scopes) : new ArrayList<>();
        }
    }

    Scope getScope();

    void setScope(Scope scope);
}
