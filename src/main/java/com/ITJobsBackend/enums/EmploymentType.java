package com.ITJobsBackend.enums;

public enum EmploymentType {

    FULL_TIME("Full Time"),
    PART_TIME("Part Time"),
    CONTRACT("Contract"),
    INTERN("Intern"),
    TEMPORARY("Temporary");

    private final String displayName;

    // Constructor del enum
    EmploymentType(String displayName) {
        this.displayName = displayName;
    }

    // Getter para usar el valor legible
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
