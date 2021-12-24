package db.migration;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.geotools.geometry.GeometryBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureIterator;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.geometry.primitive.Point;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

public class R__Load_listed_buildings_in_England extends BaseJavaMigration {
    public void migrate(Context context) throws SQLException, IOException, FactoryException, TransformException {
        try (PreparedStatement statement = context.getConnection()
                .prepareStatement("INSERT INTO listed_building " +
                        "(name, grade, location_name, location, list_entry, hyperlink) VALUES " +
                        "(?, ?, ?, ST_Point(?, ?), ?, ?)")) {

            try (FeatureIterator<SimpleFeature> features = getFeaturesFromShapefile("src/main/resources/data/listed_buildings/england/ListedBuildings_16Dec2021.shp")) {
                for (int i = 0; i < 100 && features.hasNext(); i++) {
                    SimpleFeature feature = features.next();

                    Coordinate location = convertBNGtoWGS84(createBNGCoordinate(feature.getAttribute("Easting").toString(), feature.getAttribute("Northing").toString()));
                    System.out.println("=====");
                    System.out.println("Easting: " + feature.getAttribute("Easting"));
                    System.out.println("Northing: " + feature.getAttribute("Northing"));
                    System.out.println(Arrays.toString(new Double[] {location.x, location.y}));

                    statement.setString(1, feature.getAttribute("Name").toString());
                    statement.setString(2, feature.getAttribute("Grade").toString());
                    statement.setString(3, feature.getAttribute("Location").toString());
                    statement.setDouble(4, location.x);
                    statement.setDouble(5, location.y);
                    statement.setString(6, feature.getAttribute("ListEntry").toString());
                    statement.setString(7, feature.getAttribute("Hyperlink").toString());

                    statement.execute();
                }
            }
        }
    }

    private FeatureIterator<SimpleFeature> getFeaturesFromShapefile(String pathname) throws IOException {
        DataStore dataStore = DataStoreFinder.getDataStore(Map.of("url", new File(pathname).toURI().toURL()));
        FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);

        return source.getFeatures(Filter.INCLUDE).features();
    }


    /**
     * The raw listed buildings dataset contains locations using British National Grid coordinates.  For consistency
     * and simplicity, Landmarkist uses the WGS84 coordinate reference system, as also used by Google Maps, GPS etc.
     */
    private Coordinate convertBNGtoWGS84(Coordinate bngPoint) throws FactoryException, TransformException {
        CoordinateReferenceSystem britishNationalGrid = CRS.decode("EPSG:27700");
        CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326");

        MathTransform transform = CRS.findMathTransform(britishNationalGrid, wgs84);
        return JTS.transform(bngPoint, null, transform);
    }

    private Coordinate createBNGCoordinate(String easting, String northing) {
        return new Coordinate(Double.parseDouble(easting), Double.parseDouble(northing));
    }
}