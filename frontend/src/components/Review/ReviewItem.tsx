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
    const date = new Date(dateString);
    return date.toLocaleDateString('ru-RU', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  };

  return (
    <div className="review-item">
      <div className="review-header">
        <div className="reviewer-info">
          <span className="reviewer-name">{review.userName}</span>
          <span className="review-date">{formatDate(review.date)}</span>
        </div>
        <div className="review-rating">
          {[1, 2, 3, 4, 5].map((star) => (
            <span key={star} className={`star ${star <= review.rating ? 'filled' : ''}`}>
              ★
            </span>
          ))}
        </div>
        {canDelete && (
          <button className="delete-review" onClick={onDelete} title="Удалить отзыв">
            🗑️
          </button>
        )}
      </div>
      <p className="review-text">{review.text}</p>
    </div>
  );
};