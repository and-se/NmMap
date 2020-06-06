//'use strict';
	
ymaps.ready(init);

// let response = await fetch("/map/all_points");
let response = fetch("/map/all_points").then(result=>result.json());

function init() {
    var myMap = new ymaps.Map("map", {
        center: [55.76, 37.64],
        zoom: 10,
        controls: ['zoomControl', 'searchControl', 'typeSelector',  'fullscreenControl',
        	'routeButtonControl', 'rulerControl', 'geolocationControl', 'fullscreenControl']
    }, {
        searchControlProvider: 'yandex#search'
    });
    
    response.then(points => drawPoints(myMap, points));
}

function drawPoints(myMap, points){
	// console.log(points);
	
	var clusterer = new ymaps.Clusterer({clusterDisableClickZoom: true});
	
	for(point of points){
		clusterer.add(
			new ymaps.Placemark([point.N,point.E],{
				balloonContentHeader: point.humanFio,
	            balloonContentBody: point.description,
	            balloonContentFooter: '<a href="/persons/'+point.humanId+'">подробнее</a>',
	            hintContent: point.humanFio
			})		
		);
	}
	
	myMap.geoObjects.add(clusterer);
}