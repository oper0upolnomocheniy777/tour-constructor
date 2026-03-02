import React from 'react';

export interface RoutePointItem {
  id: string;
  name: string;
  description?: string;
  latitude: number;
  longitude: number;
  order: number;
}

interface RoutePointListProps {
  points: RoutePointItem[];
  onReorder: (points: RoutePointItem[]) => void;
  onRemove: (id: string) => void;
  onEdit: (point: RoutePointItem) => void;
}

export const RoutePointList: React.FC<RoutePointListProps> = ({
  points,
  onRemove,
  onEdit
}) => {
  if (points.length === 0) {
    return (
      <div style={{ textAlign: 'center', padding: '40px 20px', color: '#999' }}>
        <p>Кликните на карту, чтобы добавить точку маршрута</p>
      </div>
    );
  }

  return (
    <div style={{ background: 'white', borderRadius: '8px', maxHeight: '500px', overflowY: 'auto' }}>
      {points.map((point, index) => (
        <div
          key={point.id}
          style={{
            display: 'flex',
            alignItems: 'center',
            padding: '12px',
            borderBottom: '1px solid #eee'
          }}
        >
          <div style={{
            width: '28px',
            height: '28px',
            backgroundColor: '#3498db',
            color: 'white',
            borderRadius: '50%',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            marginRight: '12px'
          }}>
            {index + 1}
          </div>
          
          <div style={{ flex: 1 }}>
            <div style={{ fontWeight: 500 }}>{point.name}</div>
            {point.description && (
              <div style={{ fontSize: '0.9rem', color: '#666' }}>{point.description}</div>
            )}
          </div>

          <div>
            <button
              onClick={() => onEdit(point)}
              style={{
                background: 'none',
                border: 'none',
                padding: '6px',
                cursor: 'pointer',
                color: '#999',
                marginRight: '8px'
              }}
            >
              ✏️
            </button>
            <button
              onClick={() => onRemove(point.id)}
              style={{
                background: 'none',
                border: 'none',
                padding: '6px',
                cursor: 'pointer',
                color: '#999'
              }}
            >
              🗑️
            </button>
          </div>
        </div>
      ))}
    </div>
  );
};