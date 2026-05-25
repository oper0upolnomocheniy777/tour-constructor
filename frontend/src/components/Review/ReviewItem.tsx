import React from 'react';
import { Review } from '../../types/review';
import './ReviewItem.css';

interface ReviewItemProps {
  review: Review;
  canDelete?: boolean;
  onDelete?: () => void;
}

export const ReviewItem: React.FC<ReviewItemProps> = ({ review, canDelete, onDelete }) => {
  const formatDate = (dateString: string) => {
    if (!dateString) return 'Дата неизвестна';
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return 'Дата неизвестна';
    return date.toLocaleDateString('ru-RU', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  };

  // Используем review.createdAt вместо review.date
  const dateToShow = review.createdAt || review.date || '';
const formattedDate = dateToShow ? formatDate(dateToShow) : 'Дата неизвестна';

  return (
    <div className="review-item">
      <div className="review-header">
        <div className="reviewer-info">
          <span className="reviewer-name">{review.userName || 'Аноним'}</span>
          <span className="review-date">{formattedDate}</span>
        </div>
        <div className="review-rating">
          {[1, 2, 3, 4, 5].map((star) => (
            <span key={star} className={`star ${star <= (review.rating || 0) ? 'filled' : ''}`}>
              ★
            </span>
          ))}
        </div>
        {canDelete && (
          <button className="delete-review" onClick={onDelete}>
            🗑️
          </button>
        )}
      </div>
      <p className="review-text">{review.text || 'Без текста'}</p>
    </div>
  );
};