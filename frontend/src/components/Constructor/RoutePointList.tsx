import React from 'react';
import {
  DndContext,
  closestCenter,
  KeyboardSensor,
  PointerSensor,
  useSensor,
  useSensors,
  DragEndEvent
} from '@dnd-kit/core';
import {
  arrayMove,
  SortableContext,
  sortableKeyboardCoordinates,
  verticalListSortingStrategy
} from '@dnd-kit/sortable';
import {
  useSortable
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import './RoutePointList.css';

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

interface SortablePointProps {
  point: RoutePointItem;
  index: number;
  onRemove: (id: string) => void;
  onEdit: (point: RoutePointItem) => void;
}

const SortablePoint: React.FC<SortablePointProps> = ({ point, index, onRemove, onEdit }) => {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging
  } = useSortable({ id: point.id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
    zIndex: isDragging ? 999 : 'auto',
    cursor: isDragging ? 'grabbing' : 'grab'
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      className={`route-point ${isDragging ? 'dragging' : ''}`}
    >
      <div className="point-drag-handle" {...attributes} {...listeners}>
        ⋮⋮
      </div>
      
      <div className="point-number">{index + 1}</div>
      
      <div className="point-content">
        <div className="point-name">{point.name || 'Без названия'}</div>
        {point.description && (
          <div className="point-description">{point.description}</div>
        )}
        <div className="point-coordinates">
          {point.latitude.toFixed(4)}, {point.longitude.toFixed(4)}
        </div>
      </div>

      <div className="point-actions">
        <button
          onClick={() => onEdit(point)}
          className="btn-icon edit"
          title="Редактировать"
        >
          ✏️
        </button>
        <button
          onClick={() => onRemove(point.id)}
          className="btn-icon remove"
          title="Удалить"
        >
          🗑️
        </button>
      </div>
    </div>
  );
};

export const RoutePointList: React.FC<RoutePointListProps> = ({
  points,
  onReorder,
  onRemove,
  onEdit
}) => {
  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: {
        distance: 8,
      },
    }),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    })
  );

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;

    if (over && active.id !== over.id) {
      const oldIndex = points.findIndex((p) => p.id === active.id);
      const newIndex = points.findIndex((p) => p.id === over.id);
      
      const reordered = arrayMove(points, oldIndex, newIndex);
      const updated = reordered.map((point, idx) => ({
        ...point,
        order: idx
      }));
      onReorder(updated);
    }
  };

  if (points.length === 0) {
    return (
      <div className="route-points-empty">
        <p>Кликните на карту, чтобы добавить точку маршрута</p>
      </div>
    );
  }

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={closestCenter}
      onDragEnd={handleDragEnd}
    >
      <SortableContext
        items={points.map(p => p.id)}
        strategy={verticalListSortingStrategy}
      >
        <div className="route-points-list">
          {points.map((point, index) => (
            <SortablePoint
              key={point.id}
              point={point}
              index={index}
              onRemove={onRemove}
              onEdit={onEdit}
            />
          ))}
        </div>
      </SortableContext>
    </DndContext>
  );
};