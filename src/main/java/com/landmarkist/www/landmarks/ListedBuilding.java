package com.landmarkist.www.landmarks;

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
import org.hibernate.annotations.Type;
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

    @Enumerated
    private Grade grade;

    @Column(columnDefinition = "geography")
    private Point location;

    enum Grade {
        ONE, TWO_STAR, TWO
    }
}
