// Временный мок API для тестирования без бэкенда
import { Role } from '../types';

// Хранилище пользователей (в памяти)
const users: any[] = [
  {
    id: 1,
    username: 'test',
    password: '123456',
    firstName: 'Тест',
    lastName: 'Тестов',
    roles: [Role.CUSTOMER], // Используем enum, а не строку
    discount: 0
  }
];

// Задержка для имитации сети
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

export const mockAuthApi = {
  login: async (credentials: { username: string; password: string }) => {
    await delay(500);
    
    const user = users.find(u => u.username === credentials.username && u.password === credentials.password);
    
    if (user) {
      const { password, ...userWithoutPassword } = user;
      return {
        data: {
          user: userWithoutPassword,
          token: 'fake-jwt-token-' + Date.now()
        }
      };
    }
    
    throw new Error('Invalid credentials');
  },
  
  register: async (data: any) => {
    await delay(500);
    
    // Проверяем, существует ли пользователь
    const existingUser = users.find(u => u.username === data.username);
    
    if (existingUser) {
      throw new Error('Username already exists');
    }
    
    // Создаем нового пользователя с Role.CUSTOMER (enum)
    const newUser = {
      id: users.length + 1,
      username: data.username,
      password: data.password,
      firstName: data.firstName,
      lastName: data.lastName,
      telephone: data.telephone || '',
      roles: [Role.CUSTOMER], // Используем enum!
      discount: 0
    };
    
    users.push(newUser);
    
    const { password, ...userWithoutPassword } = newUser;
    
    return {
      data: {
        user: userWithoutPassword,
        token: 'fake-jwt-token-' + Date.now()
      }
    };
  },
  
  logout: async () => {
    await delay(200);
    return { data: {} };
  },
  
  getCurrentUser: async () => {
    await delay(200);
    const { password, ...userWithoutPassword } = users[0];
    return { data: userWithoutPassword };
  }
};