package com.landmarkist.www.landmarks;

import java.net.URL;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

    private String name;

    /**
     *  The entry number of the building on the statutory list.
     */
    private String listEntry;

    @Enumerated
    private Grade grade;

    private String location;

    @Column(columnDefinition = "geography")
    private Point geometry;

    private URL hyperlink;

    enum Grade {
        ONE, TWO_STAR, TWO
    }
}
