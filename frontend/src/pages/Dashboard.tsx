import React, { useEffect, useState, useCallback } from 'react';
import {
  CheckCircle, Clock, AlertCircle, RefreshCw, Activity, Plus, Wifi, WifiOff
} from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts';
import { taskApi } from '@/services/api';
import { StatsCard } from '@/components/StatsCard';
import { StatusBadge, PriorityBadge } from '@/components/TaskBadge';
import { CreateTaskModal } from '@/components/CreateTaskModal';
import { useWebSocket } from '@/hooks/useWebSocket';
import type { Task, DashboardStats } from '@/types/task';
import { formatDistanceToNow } from 'date-fns';

const WS_URL = `ws://${window.location.host}/ws/tasks`;

export const Dashboard: React.FC = () => {
  const [tasks, setTasks]   = useState<Task[]>([]);
  const [stats, setStats]   = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const { connected, lastMessage } = useWebSocket(WS_URL);

  const refresh = useCallback(async () => {
    const [t, s] = await Promise.all([taskApi.list(), taskApi.getStats()]);
    setTasks(t);
    setStats(s);
    setLoading(false);
  }, []);

  useEffect(() => { refresh(); }, [refresh]);

  // Live update from WebSocket
  useEffect(() => {
    if (lastMessage?.type === 'TASK_UPDATED') refresh();
  }, [lastMessage, refresh]);

  const chartData = stats ? [
    { name: 'Pending',     value: stats.pendingTasks,     fill: '#F59E0B' },
    { name: 'In Progress', value: stats.inProgressTasks,  fill: '#6366F1' },
    { name: 'Completed',   value: stats.completedTasks,   fill: '#10B981' },
    { name: 'Failed',      value: stats.failedTasks,      fill: '#EF4444' },
    { name: 'Retry',       value: stats.retryPendingTasks,fill: '#F97316' },
  ] : [];

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white border-b border-gray-200 px-6 py-4 flex justify-between items-center">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 bg-indigo-600 rounded-lg flex items-center justify-center">
            <Activity className="w-5 h-5 text-white" />
          </div>
          <div>
            <h1 className="text-lg font-bold text-gray-900">AsyncFlow</h1>
            <p className="text-xs text-gray-500">AI-Powered Task Management</p>
          </div>
        </div>
        <div className="flex items-center gap-3">
          {connected
            ? <span className="flex items-center gap-1 text-xs text-green-600"><Wifi className="w-4 h-4" /> Live</span>
            : <span className="flex items-center gap-1 text-xs text-gray-400"><WifiOff className="w-4 h-4" /> Offline</span>
          }
          <button onClick={() => setShowModal(true)}
            className="flex items-center gap-2 bg-indigo-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-indigo-700">
            <Plus className="w-4 h-4" /> New Task
          </button>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-6 py-6 space-y-6">
        {/* Stats Grid */}
        {stats && (
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <StatsCard label="Total Tasks"   value={stats.totalTasks}       icon={Activity}     color="text-indigo-600" bg="bg-indigo-50" />
            <StatsCard label="In Progress"   value={stats.inProgressTasks}  icon={Clock}        color="text-blue-600"   bg="bg-blue-50" />
            <StatsCard label="Completed"     value={stats.completedTasks}   icon={CheckCircle}  color="text-green-600"  bg="bg-green-50" />
            <StatsCard label="Needs Retry"   value={stats.retryPendingTasks}icon={RefreshCw}    color="text-orange-600" bg="bg-orange-50" />
          </div>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Chart */}
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
            <h2 className="text-sm font-semibold text-gray-700 mb-4">Task Distribution</h2>
            <ResponsiveContainer width="100%" height={200}>
              <BarChart data={chartData} barSize={28}>
                <XAxis dataKey="name" tick={{ fontSize: 11 }} />
                <YAxis tick={{ fontSize: 11 }} />
                <Tooltip />
                <Bar dataKey="value" radius={[4,4,0,0]}>
                  {chartData.map((entry, i) => <Cell key={i} fill={entry.fill} />)}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>

          {/* Task List */}
          <div className="lg:col-span-2 bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
            <h2 className="text-sm font-semibold text-gray-700 mb-4">Recent Tasks</h2>
            {loading ? (
              <div className="flex items-center justify-center h-40 text-gray-400">Loading...</div>
            ) : tasks.length === 0 ? (
              <div className="flex flex-col items-center justify-center h-40 text-gray-400 gap-2">
                <AlertCircle className="w-8 h-8" />
                <p className="text-sm">No tasks yet. Create your first task!</p>
              </div>
            ) : (
              <div className="space-y-2 max-h-80 overflow-y-auto pr-1">
                {tasks.slice(0, 20).map(task => (
                  <div key={task.id}
                    className="flex items-center justify-between p-3 rounded-xl hover:bg-gray-50 transition-colors">
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-gray-800 truncate">{task.title}</p>
                      <p className="text-xs text-gray-400">
                        {formatDistanceToNow(new Date(task.createdAt), { addSuffix: true })}
                        {task.retryCount > 0 && ` · ${task.retryCount} retries`}
                      </p>
                    </div>
                    <div className="flex items-center gap-2 ml-3 flex-shrink-0">
                      <PriorityBadge priority={task.priority} />
                      <StatusBadge status={task.status} />
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </main>

      {showModal && <CreateTaskModal onClose={() => setShowModal(false)} onCreated={refresh} />}
    </div>
  );
};
