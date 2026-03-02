import React, { useState, useCallback } from 'react';
import { v4 as uuidv4 } from 'uuid';
import { YandexMapComponent, RoutePoint as YandexRoutePoint } from '../Map/YandexMap';
import { RoutePointList, RoutePointItem } from './RoutePointList';
import { TourRoute, RoutePoint } from '../../types';
import './TourConstructor.css';

interface TourConstructorYandexProps {
  initialRoute?: TourRoute;
  onSave?: (route: TourRoute) => void;
  tourId?: number;
  apiKey: string;
}

export const TourConstructorYandex: React.FC<TourConstructorYandexProps> = ({
  initialRoute,
  onSave,
  tourId,
  apiKey
}) => {
  const [points, setPoints] = useState<YandexRoutePoint[]>(
    initialRoute?.points.map((p: RoutePoint) => ({
      id: p.id,
      name: p.name,
      position: [p.longitude, p.latitude] as [number, number],
      order: p.order,
      description: p.description
    })) || []
  );
  
  const [routeInfo, setRouteInfo] = useState<{ distance: string; duration: string } | null>(null);
  const [pointForm, setPointForm] = useState<{
    open: boolean;
    point?: YandexRoutePoint;
    name: string;
    description: string;
  }>({
    open: false,
    name: '',
    description: ''
  });


  const handleMapClick = useCallback((position: [number, number]) => {
  const newPoint: YandexRoutePoint = {
    id: uuidv4(),
    name: `Точка ${points.length + 1}`, 
    position: position,
    order: points.length,
    description: '',
  };
  setPoints(prev => [...prev, newPoint]);
}, [points.length]);

  const handleMarkerDragEnd = useCallback((id: string, position: [number, number]) => {
    setPoints(prev =>
      prev.map(point =>
        point.id === id
          ? { ...point, position }
          : point
      )
    );
  }, []);

  const handleRemovePoint = useCallback((id: string) => {
    setPoints(prev => {
      const filtered = prev.filter(p => p.id !== id);
      return filtered.map((point, idx) => ({
        ...point,
        order: idx
      }));
    });
  }, []);

  const handleEditPoint = useCallback((point: YandexRoutePoint) => {
    setPointForm({
      open: true,
      point,
      name: point.name,
      description: point.description || ''
    });
  }, []);

  const handleSavePoint = useCallback(() => {
    if (pointForm.point) {
      setPoints(prev =>
        prev.map(p =>
          p.id === pointForm.point?.id
            ? { ...p, name: pointForm.name, description: pointForm.description }
            : p
        )
      );
    }
    setPointForm({ open: false, name: '', description: '' });
  }, [pointForm]);

  const handleReorder = useCallback((reordered: RoutePointItem[]) => {
    const yandexPoints: YandexRoutePoint[] = reordered.map(p => ({
      id: p.id,
      name: p.name,
      description: p.description,
      position: [p.longitude, p.latitude] as [number, number],
      order: p.order
    }));
    setPoints(yandexPoints);
  }, []);

  const handleSaveRoute = useCallback(() => {
    if (onSave && points.length > 0) {
      const routePoints: RoutePoint[] = points.map(p => ({
        id: p.id,
        name: p.name,
        description: p.description,
        latitude: p.position[1],
        longitude: p.position[0],
        order: p.order,
        tourId
      }));

      const route: TourRoute = {
        tourId,
        points: routePoints,
        totalDistance: routeInfo ? parseFloat(routeInfo.distance) : undefined,
        totalDuration: routeInfo?.duration
      };
      onSave(route);
    }
  }, [points, routeInfo, tourId, onSave]);

  const handleRouteCalculated = useCallback((distance: string, duration: string) => {
    setRouteInfo({ distance, duration });
  }, []);

  // Преобразуем YandexRoutePoint в RoutePointItem для списка
  const pointItems: RoutePointItem[] = points.map(p => ({
    id: p.id,
    name: p.name,
    description: p.description,
    latitude: p.position[1],
    longitude: p.position[0],
    order: p.order
  }));

  return (
    <div className="tour-constructor">
      <div className="constructor-header">
        <h2>Конструктор маршрута</h2>
        {routeInfo && (
          <div className="route-info">
            <span>📏 {routeInfo.distance}</span>
            <span>⏱️ {routeInfo.duration}</span>
          </div>
        )}
      </div>

      <div className="constructor-layout">
        <div className="map-section">
          <YandexMapComponent
            apiKey={apiKey}
            onMapClick={handleMapClick}
            markers={points}
            onMarkerDragEnd={handleMarkerDragEnd}
            showRoute={points.length >= 2}
            onRouteCalculated={handleRouteCalculated}
          />
        </div>

        <div className="points-section">
          <div className="points-header">
            <h3>Точки маршрута ({points.length})</h3>
            <button
              className="btn-save"
              onClick={handleSaveRoute}
              disabled={points.length < 2}
            >
              Сохранить маршрут
            </button>
          </div>

          <RoutePointList
            points={pointItems}
            onReorder={handleReorder}
            onRemove={handleRemovePoint}
            onEdit={(point) => {
              const yandexPoint = points.find(p => p.id === point.id);
              if (yandexPoint) {
                handleEditPoint(yandexPoint);
              }
            }}
          />
        </div>
      </div>

      {pointForm.open && (
        <div className="modal-overlay" onClick={() => setPointForm({ open: false, name: '', description: '' })}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h3>Редактировать точку</h3>
            
            <div className="form-group">
              <label>Название</label>
              <input
                type="text"
                value={pointForm.name}
                onChange={(e) => setPointForm(prev => ({ ...prev, name: e.target.value }))}
                placeholder="Введите название точки"
                autoFocus
              />
            </div>

            <div className="form-group">
              <label>Описание</label>
              <textarea
                value={pointForm.description}
                onChange={(e) => setPointForm(prev => ({ ...prev, description: e.target.value }))}
                placeholder="Введите описание (необязательно)"
                rows={3}
              />
            </div>

            <div className="modal-actions">
              <button className="btn-secondary" onClick={() => setPointForm({ open: false, name: '', description: '' })}>
                Отмена
              </button>
              <button className="btn-primary" onClick={handleSavePoint}>
                Сохранить
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};