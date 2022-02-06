import React, {useCallback, useState} from 'react';
import './App.css';
import Map, {Layer, MapProvider, MapRef, Popup, Source} from "react-map-gl";
import {MapboxMap} from "react-map-gl/src/types/index";
import {Feature, FeatureCollection, Point} from "geojson";

function App() {
    const [viewState, setViewState] = useState({
        longitude: -2.59,
        latitude: 51.45,
        zoom: 10,
    })
    const [landmarks, setLandmarks] = useState<FeatureCollection | string>(emptyGeoJson);
    const [selectedLandmark, setSelectedLandmark] = useState<Landmark>();

    const mapRef = React.useRef<MapRef>(null);

    const handleMove = useCallback((event) => {
        setViewState(event.viewState);
        setLandmarks(`/api/listedBuildings/search/findAllInPolygon?polygon=${viewport(mapRef.current!.getMap())}`);
    }, []);

    const handleClick = useCallback(event => {
        if (event.features && event.features[0].geometry.type == "Point") {
            const coordinates = event.features[0].geometry.coordinates.slice() as [number, number];
            setViewState({...event.viewState, longitude: coordinates[0], latitude: coordinates[1]})
            setLandmarks(`/api/listedBuildings/search/findAllInPolygon?polygon=${viewport(mapRef.current!.getMap())}`);
            setSelectedLandmark(event.features[0])
        }
    }, [])

    return (
        <MapProvider>
            <Map
                {...viewState}
                ref={mapRef}
                id="map"
                mapboxAccessToken={"pk.eyJ1IjoiemFja2FkcyIsImEiOiJjazZ3bnYyajAwOWp5M2htYW9qemQ2dXRyIn0.GbE76MBYsJpDdStQgE_YHw"}
                minZoom={10}
                style={{height: "100vh", width: "100vw"}}
                mapStyle="mapbox://styles/mapbox/light-v10"
                onLoad={handleMove}
                onMove={handleMove}
                onClick={handleClick}
                onMouseEnter={() => mapRef.current!.getCanvas().style.cursor = 'pointer'}
                onMouseLeave={() => mapRef.current!.getCanvas().style.cursor = ''}
                interactiveLayerIds={["grade-i", "grade-ii*", "grade-ii"]}
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
                {selectedLandmark &&
                <LandmarkPopup landmark={selectedLandmark} onClose={() => setSelectedLandmark(undefined)}/>}
            </Map>
        </MapProvider>
    );
}

const LandmarkPopup = ({landmark, onClose}: { landmark: Landmark, onClose: () => any }): JSX.Element => {
    const [longitude, latitude] = (landmark.geometry as Point).coordinates;
    return <Popup longitude={longitude} latitude={latitude} anchor="bottom" onClose={onClose}
    >
        <h3>{landmark.properties.name}</h3>
        <p><i>{landmark.properties.locationName}</i></p>
        <p><strong>Grade {landmark.properties.grade}</strong></p>
        <a href={landmark.properties.hyperlink + "?section=official-listing"}>Check it out</a>
    </Popup>
};

interface Landmark extends Feature {
    properties: {
        "id": string,
        "name": string,
        "grade": string,
        "locationName": string,
        "listEntry": string,
        "hyperlink": string,
    }
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