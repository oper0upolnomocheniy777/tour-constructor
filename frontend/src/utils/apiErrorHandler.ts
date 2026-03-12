import { AxiosError } from 'axios';

export interface ApiError {
  message: string;
  status?: number;
  errors?: Record<string, string[]>;
}

export const handleApiError = (error: unknown): ApiError => {
  if (error instanceof AxiosError) {
    // Обработка ошибок от сервера
    if (error.response) {
      const data = error.response.data;
      return {
        message: data.message || 'Произошла ошибка при выполнении запроса',
        status: error.response.status,
        errors: data.errors,
      };
    }
    
    // Ошибка сети
    if (error.request) {
      return {
        message: 'Нет соединения с сервером. Проверьте подключение к интернету.',
        status: 0,
      };
    }
  }
  
  // Неизвестная ошибка
  return {
    message: error instanceof Error ? error.message : 'Неизвестная ошибка',
  };
};

export const getErrorMessage = (error: unknown): string => {
  const apiError = handleApiError(error);
  
  // Если есть детальные ошибки валидации
  if (apiError.errors) {
    const firstError = Object.values(apiError.errors)[0]?.[0];
    if (firstError) {
      return firstError;
    }
  }
  
  return apiError.message;
};