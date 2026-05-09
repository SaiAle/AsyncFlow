export type TaskStatus = 'PENDING' | 'SCHEDULED' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED' | 'RETRY_PENDING' | 'CANCELLED';
export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface Task {
  id: string;
  tenantId: string;
  title: string;
  description?: string;
  naturalLanguageInput?: string;
  status: TaskStatus;
  priority: TaskPriority;
  scheduledAt?: string;
  dueAt?: string;
  retryCount: number;
  maxRetries: number;
  nextRetryAt?: string;
  assignedTo?: string;
  createdBy: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateTaskRequest {
  title?: string;
  description?: string;
  naturalLanguageInput?: string;
  priority?: TaskPriority;
  scheduledAt?: string;
  dueAt?: string;
  maxRetries?: number;
  assignedTo?: string;
}

export interface DashboardStats {
  tenantId: string;
  totalTasks: number;
  pendingTasks: number;
  inProgressTasks: number;
  completedTasks: number;
  failedTasks: number;
  retryPendingTasks: number;
}
