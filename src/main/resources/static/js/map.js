//'use strict';

ymaps.ready(init);

// let response = await fetch("/map/all_points");
let response = fetch("/map/all_points").then(result => result.json());

function init() {
	var myMap = new ymaps.Map("map", {
		center: [55.76, 37.64],
		zoom: 10,
		controls: ['zoomControl', 'searchControl', 'typeSelector', 'fullscreenControl',
			'routeButtonControl', 'rulerControl', 'geolocationControl', 'fullscreenControl']
	}, {
		searchControlProvider: 'yandex#search'
	});

	response.then(points => addPointsControl(myMap, points));
}

function addPointsControl(myMap, points) {
	var updatePoints = function() {
		myMap.geoObjects.removeAll();

		var pointTypes = new Array();
		// выбираем типы события для отображения 
		// Репрессии -> репрессии ... Другое -> другое
		document.querySelectorAll('.map-point-types li input:checked').forEach(item => {
			pointTypes.push(item.name);
		});
		var startDate = document.querySelector('#start_date').value;
		var endDate = document.querySelector('#end_date').value;

		drawPoints(myMap, filteringPoints(pointTypes, startDate, endDate, points));
	}

	document.querySelectorAll('.map-point-types li input').forEach(item => {
		if (item.id != 'start_date' && item.id != 'end_date') {
			item.addEventListener('click', updatePoints);
		}
	});

	updatePoints();
}

// получить выборку меток по указанным типам
function filteringPoints(types, startDate, endDate, points) {
	var result = new Array();
	for (var point of points) {
		for (var type of types) {
			var sD = point.startDate;
			var eD = point.endDate;
			var inDateInterval = true;
			if (sD != null && eD != null) {
				inDateInterval = startDate <= parseInt(sD.split('-')[0]) && parseInt(eD.split('-')[0]) <= endDate;
			}
			if (type == point.clusterType && inDateInterval) {
				result.push(point);
			}
		}
	}
	return result;
}

function drawPoints(myMap, points) {
	// console.log(points);

	var clusterer = new ymaps.Clusterer({ clusterDisableClickZoom: true });

	for (point of points) {

		var pointStyle = getPointStyle(point);

		var text = point.description;
		if (point.dating)
			text = point.dating + " — " + text;

		clusterer.add(
			new ymaps.Placemark([point.N, point.E], {
				balloonContentHeader: point.humanFio,
				balloonContentBody: text,
				balloonContentFooter: '<a target="_blank" href="/persons/' + point.humanId + '">подробнее</a>',
				hintContent: point.humanFio
			}, {
				preset: pointStyle
			})
		);
	}

	myMap.geoObjects.add(clusterer);
}

function getPointStyle(point) {
	switch (point.clusterType) {
		case 'репрессии':
			return 'islands#redDotIcon';
		case 'служение':
			return 'islands#darkGreenDotIcon';
		case 'кончина':
			return 'islands#blueDotIcon';
		case 'другое':
		default:
			return 'islands#grayDotIcon';
	}
}
