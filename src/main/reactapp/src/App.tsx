import React, {useState} from 'react';
import './App.css';
import Map, {Layer, MapProvider, MapRef, Source} from "react-map-gl";
import {MapboxMap} from "react-map-gl/src/types/index";
import {FeatureCollection} from "geojson";
import {debounce} from "lodash";

function App() {
    const [landmarks, setLandmarks] = useState<FeatureCollection | string>(emptyGeoJson);

    const mapRef = React.useRef<MapRef>(null);

    const handleMove = debounce(() => {
        setLandmarks(`/api/listedBuildings/search/findAllInPolygon?polygon=${viewport(mapRef.current!.getMap())}`);
    }, 1000);

    return (
        <MapProvider>
            <Map
                ref={mapRef}
                id="map"
                mapboxAccessToken={"pk.eyJ1IjoiemFja2FkcyIsImEiOiJjazZ3bnYyajAwOWp5M2htYW9qemQ2dXRyIn0.GbE76MBYsJpDdStQgE_YHw"}
                initialViewState={{longitude: -2.59, latitude: 51.45, zoom: 10}}
                minZoom={10}
                style={{height: "100vh", width: "100vw"}}
                mapStyle="mapbox://styles/mapbox/light-v10"
                onLoad={handleMove}
                onMove={handleMove}
            >
                <Source id="listed-buildings" type="geojson"
                        data={landmarks}>
                    <Layer id="grade-i" type="circle" filter={['==', "grade", "I"]}
                           paint={{"circle-radius": 8, "circle-color": "#B80C09", "circle-opacity": 0.9}}/>
                    <Layer id="grade-ii*" type="circle" filter={['==', "grade", "II*"]}
                           paint={{"circle-radius": 5, "circle-color": "#0B4F6C", "circle-opacity": 0.9}}/>
                    <Layer id="grade-ii" type="circle" filter={['==', "grade", "II"]}
                           paint={{"circle-radius": 4, "circle-color": "#01BAEF", "circle-opacity": 0.9}}/>
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