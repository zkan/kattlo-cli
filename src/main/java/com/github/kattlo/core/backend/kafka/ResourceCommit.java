package com.github.kattlo.core.backend.kafka;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.github.kattlo.core.backend.Migration;
import com.github.kattlo.core.backend.OperationType;
import com.github.kattlo.core.backend.ResourceType;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author fabiojose
 */
@RegisterForReflection
@Getter
@Setter
@ToString
public class ResourceCommit {

    private String version;

    private OperationType operation;
    private String notes;
    private ResourceType resourceType;
    private String resourceName;
    private LocalDateTime timestamp;

    /**
     * Current attributes state
     */
    private Map<String, Object> attributes;

    // The kattlo version
    private String kattlo;

    public boolean equals(Object another){
        if(! (another instanceof ResourceCommit)){
            return false;
        }

        var r = (ResourceCommit)another;

        return getResourceType().equals(r.getResourceType()) &&
                getResourceName().equals(r.getResourceName());
    }

    /**
     * Creates a copy of migration with empty {@link ResourceCommit#attributes}
     * and {@link ResourceCommit#history}
     */
    public static ResourceCommit from(Migration migration){

        var commit = new ResourceCommit();
        commit.setVersion(migration.getVersion());
        commit.setOperation(migration.getOperation());
        commit.setNotes(migration.getNotes());
        commit.setResourceType(migration.getResourceType());
        commit.setResourceName(migration.getResourceName());
        commit.setTimestamp(migration.getTimestamp());

        commit.setAttributes(new HashMap<>());

        return commit;
    }
}
