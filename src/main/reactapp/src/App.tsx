import React, {useState} from 'react';
import './App.css';
import Map, {Layer, MapProvider, MapRef, Source} from "react-map-gl";
import {MapboxMap} from "react-map-gl/src/types/index";
import {FeatureCollection} from "geojson";

function App() {
    const [landmarks, setLandmarks] = useState<FeatureCollection | string>(emptyGeoJson);

    const mapRef = React.useRef<MapRef>(null);

    return (
        <MapProvider>
            <Map
                ref={mapRef}
                id="map"
                mapboxAccessToken={"pk.eyJ1IjoiemFja2FkcyIsImEiOiJjazZ3bnYyajAwOWp5M2htYW9qemQ2dXRyIn0.GbE76MBYsJpDdStQgE_YHw"}
                initialViewState={{longitude: -2.59, latitude: 51.45}}
                style={{height: "100vh", width: "100vw"}}
                mapStyle="mapbox://styles/mapbox/streets-v9"
                onMove={() =>
                    setLandmarks(`/api/listedBuildings/search/findAllInPolygon?polygon=${viewport(mapRef.current!.getMap())}`)
                }
                onLoad={() =>
                    setLandmarks(`/api/listedBuildings/search/findAllInPolygon?polygon=${viewport(mapRef.current!.getMap())}`)
                }
            >
                <Source id="listed-buildings" type="geojson"
                        data={landmarks}>
                    <Layer id="point" type="circle"/>
                </Source>

            </Map>
        </MapProvider>
    );
}

const viewport = (map: MapboxMap | null): string => {
    if (map == null) return "";

    const bounds = map.getBounds();

    return [
        bounds.getNorthEast().toArray().toString(),
        bounds.getSouthEast().toArray().toString(),
        bounds.getSouthWest().toArray().toString(),
        bounds.getNorthWest().toArray().toString(),
        bounds.getNorthEast().toArray().toString(),
    ].join(",");
};

const emptyGeoJson: FeatureCollection = {
    "type": "FeatureCollection", "features": []
}

export default App;