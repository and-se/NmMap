//'use strict';

ymaps.ready(init);

// let response = await fetch("/map/all_points");

function init() {
	let response = fetch("/map/all_points").then(result => result.json());

	console.log("Создение ObjectManager и карты");

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
			gridSize: 98,
			// Отключение увеличения масштаба карты при нажатии на кластер.
			clusterDisableClickZoom: true,
			clusterOpenBalloonOnClick: true,
			// Макет метки кластера pieChart.
			clusterIconLayout: "default#pieChart",
		});
	myMap.geoObjects.add(objectManager);

	console.log("ObjectManager и карта созданы");
	console.log("Ожидание ответа с сервера");

	response.then(points => {
		console.log("Ответ с сервера получен");
		console.log("Вызов drawPoints(...)");
		drawPoints(objectManager, points);
		console.log("Вызов registerFiltersState(...)");
		registerFiltersState(objectManager);
	});

	resizeMap(myMap);
	resizeBalloons(objectManager);
	window.onresize = () => {
		resizeMap(myMap);
		resizeBalloons(objectManager);
	};
}

function resizeBalloons(objectManager) {
	var pageWidth = window.innerWidth;
	var newBalloonWidth = pageWidth < 500 ? pageWidth - 100 : 400;
	var newClusterBalloonWidth = newBalloonWidth - 50;

	objectManager.options.set({
		clusterBalloonContentLayoutWidth: newClusterBalloonWidth,
		// На ПК в две колонки - информативный,
		// на вертиакальн. мобильниках "карусель" - помещается на экране
		clusterBalloonContentLayout: pageWidth < 500 ? 'cluster#balloonCarousel'
				: 'cluster#balloonTwoColumns',
	});
	objectManager.objects.options.set({
		balloonMaxWidth: newBalloonWidth
	});
}

function resizeMap(myMap) {
	var pageHeight = window.innerHeight;
	var blocksInVisiblePage = document.querySelectorAll('.site-navigation, .map-filters, .page-footer__container');
	var newMapHeight = pageHeight
		- Array.from(blocksInVisiblePage).map(div => div.offsetHeight).reduce((sum, current) => sum + current, 0)
		- 20;
	document.getElementById('map').style.height = newMapHeight + 'px';

	myMap.container.fitToViewport();
}

function registerFiltersState(objectManager) {
	var applyFilter = function() {
		console.log("Регистрация фильтра в objectManager: applyFilter()");
		objectManager.setFilter(obj => filteringPoint(obj));
	};

	console.log("Привязка событий флажков к установке фильтра: registerFiltersState(...)");

	document.querySelectorAll('.map-point-types li input, .date-picker input')
		.forEach(item => {
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

	console.log("Создание и добавление точек в список: drawPoints(...)");

	//console.log(points[4]);

	var objects = [];
	var i = 0;

	for (point of points) {
		var pointStyle = getPointStyle(point);
		var text = point.description;
		if (point.dating)
			text = point.dating + " — " + text;

		objects.push({
			type: 'Feature',
			id: i++,
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
	console.log("Завершилось добавление точек в список: drawPoints(...)");
	console.log("Добавление точек в objectManager: drawPoints(...)");
	objectManager.add(objects);
	console.log("Завершилось добавление точек в objectManager: drawPoints(...)");
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
