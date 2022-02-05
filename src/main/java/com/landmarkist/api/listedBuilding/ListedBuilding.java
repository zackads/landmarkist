package com.landmarkist.api.listedBuilding;

import java.net.URL;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ListedBuilding {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String grade;

    @NotNull
    @Column(columnDefinition = "geography")
    private Point location;

    @NotEmpty
    private String locationName;

    /**
     * The entry number of the building on the statutory list.
     */
    @NotEmpty
    private String listEntry;

    @NotEmpty
    private URL hyperlink;
}
