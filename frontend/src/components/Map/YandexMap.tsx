import React, { useRef, useEffect, useState } from 'react';
import './YandexMap.css';

export interface RoutePoint {
  id: string;
  name: string;
  position: [number, number];
  order: number;
  description?: string;
}

interface YandexMapProps {
  onMapClick?: (position: [number, number]) => void;
  markers?: RoutePoint[];
  onMarkerDragEnd?: (id: string, position: [number, number]) => void;
  showRoute?: boolean;
  onRouteCalculated?: (distance: string, duration: string) => void;
  apiKey: string;
}

const defaultCenter: [number, number] = [37.6173, 55.7558];

declare global {
  interface Window {
    ymaps: any;
  }
}

export const YandexMapComponent: React.FC<YandexMapProps> = ({
  onMapClick,
  markers = [],
  onMarkerDragEnd,
  showRoute = false,
  onRouteCalculated,
  apiKey
}) => {
  const mapRef = useRef<HTMLDivElement>(null);
  const [map, setMap] = useState<any>(null);
  const [ymaps, setYmaps] = useState<any>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isRouteLoading, setIsRouteLoading] = useState(false);
  const markersRef = useRef<any[]>([]);
  const routeRef = useRef<any>(null);

  // Загрузка API Яндекс Карт
  useEffect(() => {
    if (!apiKey) return;

    const loadYmaps = () => {
      if (window.ymaps) {
        window.ymaps.ready(() => {
          console.log('Yandex Maps API ready');
          setYmaps(window.ymaps);
          setIsLoading(false);
        });
      } else {
        setTimeout(loadYmaps, 100);
      }
    };

    if (window.ymaps) {
      loadYmaps();
      return;
    }

    const script = document.createElement('script');
    script.src = `https://api-maps.yandex.ru/2.1/?apikey=${apiKey}&lang=ru_RU`;
    script.async = true;
    
    script.onload = loadYmaps;
    script.onerror = () => {
      console.error('Failed to load Yandex Maps API');
      setIsLoading(false);
    };

    document.head.appendChild(script);

    return () => {};
  }, [apiKey]);

  // Инициализация карты
  useEffect(() => {
    if (!ymaps || !mapRef.current || map) return;

    try {
      const newMap = new ymaps.Map(mapRef.current, {
        center: defaultCenter,
        zoom: 10,
        controls: ['zoomControl', 'fullscreenControl']
      });

      console.log('Map created');
      setMap(newMap);

      newMap.events.add('click', (e: any) => {
        if (onMapClick) {
          const coords = e.get('coords');
          onMapClick(coords);
        }
      });

    } catch (error) {
      console.error('Error creating map:', error);
    }
  }, [ymaps, mapRef.current, onMapClick]);

  // Обновление маркеров
  useEffect(() => {
    if (!map || !ymaps) return;

    markersRef.current.forEach(marker => {
      try {
        map.geoObjects.remove(marker);
      } catch (e) {}
    });
    markersRef.current = [];

    markers.forEach((marker) => {
      try {
        const placemark = new ymaps.Placemark(
          marker.position,
          {
            balloonContent: marker.name,
            hintContent: marker.name
          },
          {
            draggable: true,
            preset: 'islands#circleIcon',
            iconColor: '#3498db'
          }
        );

        placemark.events.add('dragend', (e: any) => {
          if (onMarkerDragEnd) {
            const target = e.get('target');
            const coords = target.geometry.getCoordinates();
            onMarkerDragEnd(marker.id, coords);
          }
        });

        map.geoObjects.add(placemark);
        markersRef.current.push(placemark);
      } catch (error) {
        console.error('Error creating marker:', error);
      }
    });
  }, [map, ymaps, markers, onMarkerDragEnd]);

  // Построение маршрута
  useEffect(() => {
    if (!map || !ymaps || markers.length < 2 || !showRoute) {
      if (routeRef.current) {
        try {
          map?.geoObjects.remove(routeRef.current);
        } catch (e) {}
        routeRef.current = null;
      }
      return;
    }

    const buildRoute = async () => {
      setIsRouteLoading(true);
      
      try {
        if (routeRef.current) {
          map.geoObjects.remove(routeRef.current);
          routeRef.current = null;
        }

        const sortedMarkers = [...markers].sort((a, b) => a.order - b.order);
        
        const route = await ymaps.route(sortedMarkers.map(m => m.position));
        
        map.geoObjects.add(route);
        routeRef.current = route;
        
        const distance = route.getLength();
        const duration = route.getTime();
        
        const distanceKm = (distance / 1000).toFixed(1);
        const hours = Math.floor(duration / 3600);
        const minutes = Math.floor((duration % 3600) / 60);
        const durationStr = hours > 0 ? `${hours}ч ${minutes}м` : `${minutes}м`;
        
        onRouteCalculated?.(`${distanceKm} км`, durationStr);
        setIsRouteLoading(false);

      } catch (error) {
        console.error('Route error:', error);
        setIsRouteLoading(false);
      }
    };

    buildRoute();

    return () => {
      if (routeRef.current && map) {
        try {
          map.geoObjects.remove(routeRef.current);
        } catch (e) {}
      }
    };
  }, [map, ymaps, markers, showRoute, onRouteCalculated]);

  if (!apiKey) {
    return <div className="map-error">Ошибка: API ключ не найден</div>;
  }

  if (isLoading) {
    return <div className="map-loading">Загрузка карты...</div>;
  }

  return (
    <div className="map-container">
      <div 
        ref={mapRef} 
        style={{ width: '100%', height: '500px' }} 
      />
      {isRouteLoading && (
        <div className="route-loading">Построение маршрута...</div>
      )}
    </div>
  );
};