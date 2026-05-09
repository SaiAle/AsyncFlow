import axios from 'axios';
import type { Task, CreateTaskRequest, DashboardStats } from '@/types/task';

const TENANT_ID = import.meta.env.VITE_TENANT_ID ?? 'demo-tenant';
const USER_ID   = import.meta.env.VITE_USER_ID   ?? 'demo-user';

const api = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
    'X-Tenant-ID': TENANT_ID,
    'X-User-ID':   USER_ID,
  },
});

export const taskApi = {
  create: (data: CreateTaskRequest) =>
    api.post<Task>('/tasks', data).then(r => r.data),

  list: () =>
    api.get<Task[]>('/tasks').then(r => r.data),

  get: (id: string) =>
    api.get<Task>(`/tasks/${id}`).then(r => r.data),

  updateStatus: (id: string, status: string) =>
    api.patch<Task>(`/tasks/${id}/status`, null, { params: { status } }).then(r => r.data),

  getStats: () =>
    api.get<DashboardStats>('/dashboard/stats').then(r => r.data),
};
