//'use strict';

ymaps.ready(init);

// let response = await fetch("/map/all_points");

function init() {
	let response = fetch("/map/all_points").then(result => result.json());

	var myMap = new ymaps.Map("map", {
		center: [55.76, 37.64],
		zoom: 10,
		controls: ['zoomControl', 'searchControl', 'typeSelector', 'fullscreenControl',
			'routeButtonControl', 'rulerControl', 'geolocationControl', 'fullscreenControl']
	}, {
		searchControlProvider: 'yandex#search'
	}),
		objectManager = new ymaps.ObjectManager({
			// Чтобы метки начали кластеризоваться, выставляем опцию.
			clusterize: true,
			// ObjectManager принимает те же опции, что и кластеризатор.
			gridSize: 50,
			// Отключение увеличения масштаба карты при нажатии на кластер.
			clusterDisableClickZoom: true,
			// Макет метки кластера pieChart.
			clusterIconLayout: "default#pieChart"
		});
	myMap.geoObjects.add(objectManager);

	response.then(points => {
		drawPoints(objectManager, points);
		registerFiltersState(objectManager);
	});
}

function registerFiltersState(objectManager) {
	var applyFilter = function() {
		objectManager.setFilter(obj => filteringPoint(obj));
	};

	document.querySelectorAll('.map-point-types li input').forEach(item => {
		if (item.id == 'start_date' || item.id == 'end_date') {
			item.addEventListener('change', applyFilter);
		} else {
			item.addEventListener('click', applyFilter);
		}
	});
}

function filteringPoint(point) {
	var pointProp = point.properties;
	var eventStartYear = pointProp.startYear;
	var eventEndYear = pointProp.endYear;
	var pointType = pointProp.pointType;

	var pointTypes = new Array();
	// выбираем типы события для отображения 
	// Репрессии -> репрессии ... Другое -> другое
	document.querySelectorAll('.map-point-types li input:checked').forEach(item => {
		pointTypes.push(item.name);
	});
	var startYear = document.querySelector('#start_date').value;
	var endYear = document.querySelector('#end_date').value;
	var withoutDating = document.querySelector('#withoutDating').checked;
	//console.log(withoutDating);

	return isDraw(pointTypes, pointType,
		eventStartYear, startYear, eventEndYear, endYear, withoutDating);
}

function isDraw(pointTypes, pointType,
	pointStartYear, startYear,
	pointEndYear, endYear,
	withoutDating) {

	var inDateInterval = true;
	if (pointStartYear != null && pointEndYear != null) {
		inDateInterval = (startYear <= pointStartYear || !startYear)
			&& (pointEndYear <= endYear || !endYear);
	} else {
		inDateInterval = withoutDating;
	}
	if (pointTypes.indexOf(pointType) > -1 && inDateInterval) {
		return true;
	}
	return false;

}

function drawPoints(objectManager, points) {
	// console.log(points);

	console.log(points[4]);

	for (point of points) {

		var pointStyle = getPointStyle(point);

		var text = point.description;
		if (point.dating)
			text = point.dating + " — " + text;

		objectManager.add({
			type: 'Feature',
			id: point.humanId,
			geometry: {
				type: 'Point',
				coordinates: [point.N, point.E]
			},
			properties: {
				hintContent: point.humanFio,
				balloonContentHeader: point.humanFio,
				balloonContentBody: text,
				balloonContentFooter: '<a target="_blank" href="/persons/' + point.humanId + '">подробнее</a>',
				startYear: point.startDate ? parseInt(point.startDate.split('-')[0]) : null,
				endYear: point.endDate ? parseInt(point.endDate.split('-')[0]) : null,
				pointType: point.clusterType
			},
			options: {
				preset: pointStyle
			}
		});
	}

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
