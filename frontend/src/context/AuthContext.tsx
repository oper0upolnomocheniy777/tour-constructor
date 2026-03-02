import React, { createContext, useState, useContext, useEffect } from 'react';
import { User, LoginCredentials, RegisterData, Role } from '../types';
import { authApi } from '../services/api';

interface AuthContextType {
  user: User | null;
  loading: boolean;
  login: (credentials: LoginCredentials) => Promise<void>;
  register: (data: RegisterData) => Promise<void>;
  logout: () => Promise<void>;
  isAuthenticated: boolean;
  isAgent: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const savedUser = localStorage.getItem('user');
    const token = localStorage.getItem('token');
    
    if (savedUser && token) {
      try {
        const parsedUser = JSON.parse(savedUser) as User;
        // Преобразуем строки ролей в enum Role
        if (parsedUser.roles) {
          parsedUser.roles = parsedUser.roles.map((role: any) => {
            if (typeof role === 'string') {
              return role === 'TOUR_AGENT' ? Role.TOUR_AGENT : Role.CUSTOMER;
            }
            return role;
          });
        }
        setUser(parsedUser);
      } catch (error) {
        console.error('Failed to parse user from localStorage:', error);
        localStorage.removeItem('user');
        localStorage.removeItem('token');
      }
    }
    setLoading(false);
  }, []);

  const login = async (credentials: LoginCredentials) => {
    try {
      const response = await authApi.login(credentials);
      const { user, token } = response.data;
      
      // Преобразуем строки ролей в enum Role
      if (user.roles) {
        user.roles = user.roles.map((role: any) => {
          if (typeof role === 'string') {
            return role === 'TOUR_AGENT' ? Role.TOUR_AGENT : Role.CUSTOMER;
          }
          return role;
        });
      }
      
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(user));
      setUser(user);
    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    }
  };

  const register = async (data: RegisterData) => {
    try {
      const response = await authApi.register(data);
      const { user, token } = response.data;
      
      // Преобразуем строки ролей в enum Role
      if (user.roles) {
        user.roles = user.roles.map((role: any) => {
          if (typeof role === 'string') {
            return role === 'TOUR_AGENT' ? Role.TOUR_AGENT : Role.CUSTOMER;
          }
          return role;
        });
      }
      
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(user));
      setUser(user);
    } catch (error) {
      console.error('Registration failed:', error);
      throw error;
    }
  };

  const logout = async () => {
    try {
      await authApi.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      setUser(null);
    }
  };

  const isAuthenticated = !!user;
  const isAgent = user?.roles.some(role => role === Role.TOUR_AGENT) ?? false;

  return (
    <AuthContext.Provider value={{
      user,
      loading,
      login,
      register,
      logout,
      isAuthenticated,
      isAgent
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};