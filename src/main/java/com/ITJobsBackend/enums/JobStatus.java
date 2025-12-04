package com.ITJobsBackend.enums;

public enum JobStatus {
	OPEN("Disponible"), CLOSED("Cerrado"), FILLED("Lleno");

	private final String displayName;

	JobStatus(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
