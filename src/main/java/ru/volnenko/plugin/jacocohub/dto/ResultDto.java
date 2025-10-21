package ru.volnenko.plugin.jacocohub.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public final class ResultDto {

    private Boolean success = true;

    private String message = "";

    public ResultDto(@NonNull final Exception e) {
        this.success = false;
        this.message = e.getMessage();
    }

}
