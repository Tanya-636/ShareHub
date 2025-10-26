package com.sharehub.hub.dto;

import com.sharehub.hub.entity.Group;
import lombok.Value;
import java.util.stream.Collectors;

@Value
public class GroupResponse {

    Long id;
    String name;
    String createdByName;

    // Constructor to map fields from the Group entity
    public GroupResponse(Group group) {
        this.id = group.getId();
        this.name = group.getName();

        // Accessing EAGERly loaded creator is now safe
        this.createdByName = group.getCreatedBy() != null ? group.getCreatedBy().getName() : "N/A";
    }
}