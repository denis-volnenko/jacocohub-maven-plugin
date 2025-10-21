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
    private String parentGroupId = "";

    @NonNull
    private String parentArtifactId = "";

    @NonNull
    private String parentVersion = "";

    @NonNull
    private String parentType = "APPLICATION";

    @NonNull
    private String groupId = "";

    @NonNull
    private String type = "APPLICATION";

    @NonNull
    private String branch = "master";

    @NonNull
    private String artifactId = "";

    @NonNull
    private String version = "";

    private double instructionMissed;

    private double instructionCovered;

    private double branchMissed;

    private double branchCovered;

    private double lineMissed;

    private double lineCovered;

    private double complexityMissed;

    private double complexityCovered;

    private double methodMissed;

    private double methodCovered;

    private double classMissed;

    private double classCovered;

}
