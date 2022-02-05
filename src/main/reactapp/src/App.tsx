import React from 'react';
import './App.css';
import Map from "react-map-gl";

function App() {
    return (
        <Map
            mapboxAccessToken={"pk.eyJ1IjoiemFja2FkcyIsImEiOiJjazZ3bnYyajAwOWp5M2htYW9qemQ2dXRyIn0.GbE76MBYsJpDdStQgE_YHw"}
            initialViewState={{longitude: -2.59, latitude: 51.45}}
            style={{width: 500, height: 500}}
            mapStyle="mapbox://styles/mapbox/streets-v9"
        />
    );
}

export default App;
