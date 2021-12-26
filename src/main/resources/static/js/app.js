const viewport = () => {
  const bounds = map.getBounds();

  return [
    bounds.getNorthEast().toArray().toString(),
    bounds.getSouthEast().toArray().toString(),
    bounds.getSouthWest().toArray().toString(),
    bounds.getNorthWest().toArray().toString(),
    bounds.getNorthEast().toArray().toString(),
  ].join(",");
};

let abortController = null;

mapboxgl.accessToken =
  "pk.eyJ1IjoiemFja2FkcyIsImEiOiJjazZ3bnYyajAwOWp5M2htYW9qemQ2dXRyIn0.GbE76MBYsJpDdStQgE_YHw";

const startingCenter = "-2.59,51.45";

const getQueryCenter = () => {
    const center = new URLSearchParams(window.location.search)
        .get("center") || startingCenter;

    return center
        .split(",")
        .map(parseFloat);
}

const setQueryCenter = (lnglat) => {
  const url = new URL(window.location);
  url.searchParams.set('center', lnglat.toArray());
  window.history.pushState({}, '', url);
}

const map = new mapboxgl.Map({
  container: "map", // ID of the DOM element to contain the map
  style: "mapbox://styles/mapbox/light-v10",
  center: getQueryCenter(),
  zoom: 10,
  minZoom: 10
});

map.addControl(new mapboxgl.NavigationControl());


// Update URL query params to reflect current center
map.on("moveend", () => {
  setQueryCenter(map.getCenter())
})


map.on("load", () => {
  map.addSource("listedBuildings", {
    type: "geojson",
    data: `http://localhost:8080/api/listedBuildings/search/findAllInPolygon?polygon=${viewport(
      map
    )}`,
    cluster: true,
    clusterMaxZoom: 14,
  });

  map.addLayer({
    id: "clusters",
    minzoom: 10,
    type: "circle",
    source: "listedBuildings",
    filter: ["has", "point_count"],
    paint: {
      "circle-color": [
        "step",
        ["get", "point_count"],
        "#51bbd6",
        100,
        "#f1f075",
        750,
        "#f28cb1",
      ],
      "circle-radius": ["step", ["get", "point_count"], 20, 100, 30, 750, 40],
    },
  });

  map.addLayer({
    id: "cluster-count",
    minzoom: 10,
    type: "symbol",
    source: "listedBuildings",
    filter: ["has", "point_count"],
    layout: {
      "text-field": "{point_count_abbreviated}",
      "text-font": ["DIN Offc Pro Medium", "Arial Unicode MS Bold"],
      "text-size": 12,
    },
  });

  map.addLayer({
    id: "unclustered-point",
    minzoom: 10,
    type: "circle",
    source: "listedBuildings",
    filter: ["!", ["has", "point_count"]],
    paint: {
      "circle-color": "#11b4da",
      "circle-radius": 4,
      "circle-stroke-width": 1,
      "circle-stroke-color": "#fff",
    },
  });
});

map.on("moveend", () => {
  if (abortController) abortController.abort();
  abortController = new AbortController();

  map
      .getSource("listedBuildings")
      .setData(
          `http://localhost:8080/api/listedBuildings/search/findAllInPolygon?polygon=${viewport(
              map
          )}`
      );
});
