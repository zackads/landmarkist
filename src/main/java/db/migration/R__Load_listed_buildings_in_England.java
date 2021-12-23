package db.migration;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

public class R__Load_listed_buildings_in_England extends BaseJavaMigration {
    public void migrate(Context context) throws SQLException, IOException {
        try (PreparedStatement statement = context.getConnection()
                .prepareStatement("INSERT INTO listed_building " +
                        "(name, grade, location, geometry, list_entry, hyperlink) VALUES " +
                        "(?, ?, ?, POINT(?, ?), ?, ?)")) {

            try (FeatureIterator<SimpleFeature> features = getFeaturesFromShapefile("src/main/resources/data/listed_buildings/england/ListedBuildings_16Dec2021.shp")) {
                while (features.hasNext()) {
                    SimpleFeature feature = features.next();

                    statement.setString(1, feature.getAttribute("Name").toString());
                    statement.setInt(2, toListedBuildingGradeInteger(feature.getAttribute("Grade").toString()));
                    statement.setString(3, feature.getAttribute("Location").toString());
                    statement.setFloat(4, Float.parseFloat(feature.getAttribute("Easting").toString()));
                    statement.setFloat(5, Float.parseFloat(feature.getAttribute("Northing").toString()));
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

    private Integer toListedBuildingGradeInteger(String grade) {
        Map<String, Integer> listedBuildingGrades = Map.of("I", 1, "II*", 2, "II", 3);
        return listedBuildingGrades.get(grade);
    }
}