export interface User {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  email?: string;
  roles: Role[];
  discount: number;
  telephone?: string;
}

export enum Role {
  CUSTOMER = 'CUSTOMER',
  TOUR_AGENT = 'TOUR_AGENT'
}

export interface LoginCredentials {
  username: string;
  password: string;
}

export interface RegisterData extends LoginCredentials {
  firstName: string;
  lastName: string;
  telephone?: string;
}

export interface AuthResponse {
  user: User;
  token: string;
}

// Типы для конструктора
export interface RoutePoint {
  id: string;
  name: string;
  description?: string;
  latitude: number;
  longitude: number;
  order: number;
  tourId?: number;
}

export interface TourRoute {
  id?: number;
  tourId?: number;
  points: RoutePoint[];
  totalDistance?: number;
  totalDuration?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Tour {
  id: number;
  title: string;
  description: string;
  destination: string;
  type: TourType;
  hot: boolean;
  price: number;
  enabled: boolean;
  avgRating: number;
  votesCount: number;
  discount: number;
  imageUrl?: string;
}

export enum TourType {
  RECREATION = 'RECREATION',
  EXCURSION = 'EXCURSION',
  SHOPPING = 'SHOPPING'
}

export interface Purchase {
  id: number;
  userId: number;
  tourId: number;
  date: string;
  price: number;
  status: PurchaseStatus;
  tour?: Tour;
}

export enum PurchaseStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  CANCELLED = 'CANCELLED',
  COMPLETED = 'COMPLETED'
}

export interface Review {
  id: number;
  text: string;
  rating: number;
  date: string;
  authorId: number;
  tourId: number;
  author?: User;
}