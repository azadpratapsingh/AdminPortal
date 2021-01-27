package in.atstudentzone.azadpratapsingh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.firebase.database.IgnoreExtraProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
@JsonIgnoreProperties(ignoreUnknown = true)
public class HouseProperty {
    private String phoneNumber;
    private String pgName;
    private String city;
    private String area;
    private String ownerName;
    private String preferredLanguage;
    private String applicationStatus;
}
