// Конфигурация API для разных окружений
const API_CONFIG = {
  // Для разработки используем локальный бэкенд
  development: {
    baseURL: 'http://localhost:8080/api',
    timeout: 10000,
    withCredentials: true,
  },
  // Для продакшена (потом настроим)
  production: {
    baseURL: 'https://your-domain.com/api',
    timeout: 10000,
    withCredentials: true,
  }
};

// Выбираем конфиг в зависимости от окружения
const environment = process.env.NODE_ENV || 'development';
export const API_BASE_URL = API_CONFIG[environment as keyof typeof API_CONFIG].baseURL;
export const API_TIMEOUT = API_CONFIG[environment as keyof typeof API_CONFIG].timeout;
export const API_WITH_CREDENTIALS = API_CONFIG[environment as keyof typeof API_CONFIG].withCredentials;