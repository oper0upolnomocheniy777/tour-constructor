import { Review, CreateReviewData } from '../types/review';

const STORAGE_KEY = 'tour_reviews';

export const getTourReviews = (tourId: number): Review[] => {
  const allReviews = JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]');
  return allReviews.filter((r: Review) => r.tourId === tourId);
};

export const canUserReview = (tourId: number): boolean => {
  // Проверяем, покупал ли пользователь этот тур
  const purchases = JSON.parse(localStorage.getItem('purchases') || '[]');
  const user = localStorage.getItem('user');
  
  if (!user) return false;
  
  try {
    const currentUser = JSON.parse(user);
    const hasPurchased = purchases.some((p: any) => p.tourId === tourId && p.status !== 'cancelled');
    
    if (!hasPurchased) return false;
    
    // Проверяем, не оставлял ли уже отзыв
    const allReviews = JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]');
    const hasReview = allReviews.some(
      (r: Review) => r.tourId === tourId && r.userId === currentUser.id
    );
    
    return !hasReview;
  } catch {
    return false;
  }
};

export const addReview = (data: CreateReviewData, userName: string): Review => {
  const allReviews = JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]');
  
  // Получаем ID пользователя из localStorage
  const user = localStorage.getItem('user');
  let userId = Date.now();
  let finalUserName = userName;
  
  if (user) {
    try {
      const currentUser = JSON.parse(user);
      userId = currentUser.id;
      finalUserName = `${currentUser.firstName} ${currentUser.lastName}` || currentUser.username || userName;
    } catch {}
  }
  
  // Проверяем существующий отзыв
  const existingReview = allReviews.find(
    (r: Review) => r.tourId === data.tourId && r.userId === userId
  );
  if (existingReview) {
    throw new Error('Вы уже оставляли отзыв на этот тур');
  }
  
  const newReview: Review = {
    id: Date.now(),
    tourId: data.tourId,
    userId: userId,
    userName: finalUserName,
    rating: data.rating,
    text: data.text,
    date: new Date().toISOString()
  };
  
  allReviews.push(newReview);
  localStorage.setItem(STORAGE_KEY, JSON.stringify(allReviews));
  
  updateTourRating(data.tourId);
  
  return newReview;
};

export const deleteReview = (reviewId: number, tourId: number): void => {
  const allReviews = JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]');
  const filtered = allReviews.filter((r: Review) => r.id !== reviewId);
  localStorage.setItem(STORAGE_KEY, JSON.stringify(filtered));
  
  updateTourRating(tourId);
};

const updateTourRating = (tourId: number): void => {
  const reviews = getTourReviews(tourId);
  const tours = JSON.parse(localStorage.getItem('user_tours') || '[]');
  
  const tourIndex = tours.findIndex((t: any) => t.id === tourId);
  if (tourIndex !== -1) {
    const avgRating = reviews.length > 0
      ? reviews.reduce((sum, r) => sum + r.rating, 0) / reviews.length
      : 0;
    
    console.log(`Tour ${tourId}: ${reviews.length} reviews, avgRating = ${avgRating}`);
    
    tours[tourIndex].avgRating = parseFloat(avgRating.toFixed(1));
    tours[tourIndex].votesCount = reviews.length;
    
    localStorage.setItem('user_tours', JSON.stringify(tours));
  }
};