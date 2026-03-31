export interface Review {
  id: number;
  tourId: number;
  userId: number;
  userName: string;
  rating: number;
  text: string;
  date: string;
}

export interface CreateReviewData {
  tourId: number;
  rating: number;
  text: string;
}