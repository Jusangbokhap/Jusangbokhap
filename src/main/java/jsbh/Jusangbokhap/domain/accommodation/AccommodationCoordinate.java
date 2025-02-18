package jsbh.Jusangbokhap.domain.accommodation;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationCoordinate {

    private static final int SRID = 4326;

    /**
     * SRID(Spatial Reference Identifier)는 데이터 좌표계를 구분할 수 있는 식별 코드.
     * 가장 흔하게 사용되는 값은 4326으로 위도(Latitude)와 경도(Longitude)를 사용하여 위치를 표현하는 WGS84 좌표계를 의미.
     * 카카오맵에서도 해당 좌표계를 사용
     */
    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point coordinate;

    public static Point createCoordinate(Double longitude, Double latitude) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point newPoint = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        newPoint.setSRID(SRID);
        return newPoint;
    }

    public void updateCoordinate(Double longitude, Double latitude) {
        this.coordinate = createCoordinate(longitude, latitude);
    }
}
