package ru.volnenko.plugin.jacocohub.enumerated;

import lombok.NonNull;

public enum ArtifactType {

    APPLICATION("ПРИЛОЖЕНИЕ"),
    LIBRARY("БИБЛИОТЕКА");

    @NonNull
    private final String displayName;

    ArtifactType(@NonNull final String displayName) {
        this.displayName = displayName;
    }

    @NonNull
    public String getDisplayName() {
        return displayName;
    }

}
