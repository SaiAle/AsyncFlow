import React from 'react';
import type { TaskStatus, TaskPriority } from '@/types/task';

const statusColors: Record<TaskStatus, string> = {
  PENDING:       'bg-yellow-100 text-yellow-800',
  SCHEDULED:     'bg-blue-100 text-blue-800',
  IN_PROGRESS:   'bg-indigo-100 text-indigo-800',
  COMPLETED:     'bg-green-100 text-green-800',
  FAILED:        'bg-red-100 text-red-800',
  RETRY_PENDING: 'bg-orange-100 text-orange-800',
  CANCELLED:     'bg-gray-100 text-gray-600',
};

const priorityColors: Record<TaskPriority, string> = {
  LOW:      'bg-gray-100 text-gray-600',
  MEDIUM:   'bg-blue-50 text-blue-700',
  HIGH:     'bg-orange-50 text-orange-700',
  CRITICAL: 'bg-red-50 text-red-700',
};

export const StatusBadge: React.FC<{ status: TaskStatus }> = ({ status }) => (
  <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${statusColors[status]}`}>
    {status.replace('_', ' ')}
  </span>
);

export const PriorityBadge: React.FC<{ priority: TaskPriority }> = ({ priority }) => (
  <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${priorityColors[priority]}`}>
    {priority}
  </span>
);
