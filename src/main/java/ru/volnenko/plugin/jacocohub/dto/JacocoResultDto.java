package ru.volnenko.plugin.jacocohub.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public final class JacocoResultDto {

    @NonNull
    private String parentGroup = "";

    @NonNull
    private String parentArtifact = "";

    @NonNull
    private String parentVersion = "";

    @NonNull
    private String parentType = "APPLICATION";

    @NonNull
    private String group = "";

    @NonNull
    private String type = "APPLICATION";

    @NonNull
    private String branch = "master";

    @NonNull
    private String artifact = "";

    @NonNull
    private String version = "";

    @NonNull
    private Float branches = 0F;

    @NonNull
    private Float instructions = 0F;

}
