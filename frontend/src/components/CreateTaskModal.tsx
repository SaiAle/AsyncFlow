import React, { useState } from 'react';
import { X, Sparkles } from 'lucide-react';
import { taskApi } from '@/services/api';
import type { CreateTaskRequest } from '@/types/task';

interface Props { onClose: () => void; onCreated: () => void; }

export const CreateTaskModal: React.FC<Props> = ({ onClose, onCreated }) => {
  const [form, setForm] = useState<CreateTaskRequest>({ priority: 'MEDIUM' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await taskApi.create(form);
      onCreated();
      onClose();
    } catch {
      setError('Failed to create task. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg p-6">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-lg font-bold text-gray-800 flex items-center gap-2">
            <Sparkles className="w-5 h-5 text-indigo-500" /> Create Task
          </h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Natural Language Input (AI-powered)
            </label>
            <textarea
              className="w-full border rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-indigo-400 outline-none"
              rows={2}
              placeholder='e.g. "Send payment reminder every Friday at 9 AM, high priority"'
              value={form.naturalLanguageInput ?? ''}
              onChange={e => setForm(f => ({ ...f, naturalLanguageInput: e.target.value }))}
            />
            <p className="text-xs text-gray-400 mt-1">AI will extract title, schedule, and priority automatically.</p>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Title (optional if using AI)</label>
            <input
              className="w-full border rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-indigo-400 outline-none"
              placeholder="Task title"
              value={form.title ?? ''}
              onChange={e => setForm(f => ({ ...f, title: e.target.value }))}
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Priority</label>
              <select
                className="w-full border rounded-lg px-3 py-2 text-sm"
                value={form.priority}
                onChange={e => setForm(f => ({ ...f, priority: e.target.value as any }))}
              >
                {['LOW','MEDIUM','HIGH','CRITICAL'].map(p => <option key={p}>{p}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Max Retries</label>
              <input type="number" min={0} max={10}
                className="w-full border rounded-lg px-3 py-2 text-sm"
                value={form.maxRetries ?? 3}
                onChange={e => setForm(f => ({ ...f, maxRetries: +e.target.value }))}
              />
            </div>
          </div>

          {error && <p className="text-red-500 text-sm">{error}</p>}

          <div className="flex gap-3 pt-2">
            <button type="button" onClick={onClose}
              className="flex-1 border rounded-lg py-2 text-sm font-medium text-gray-600 hover:bg-gray-50">
              Cancel
            </button>
            <button type="submit" disabled={loading}
              className="flex-1 bg-indigo-600 text-white rounded-lg py-2 text-sm font-medium hover:bg-indigo-700 disabled:opacity-50">
              {loading ? 'Creating...' : 'Create Task'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
