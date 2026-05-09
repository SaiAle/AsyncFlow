import { useEffect, useRef, useState, useCallback } from 'react';

interface WsMessage<T = unknown> {
  type: string;
  payload: T;
}

export function useWebSocket<T = unknown>(url: string) {
  const ws = useRef<WebSocket | null>(null);
  const [lastMessage, setLastMessage] = useState<WsMessage<T> | null>(null);
  const [connected, setConnected] = useState(false);

  const connect = useCallback(() => {
    ws.current = new WebSocket(url);
    ws.current.onopen  = () => setConnected(true);
    ws.current.onclose = () => { setConnected(false); setTimeout(connect, 3000); };
    ws.current.onmessage = (ev) => {
      try { setLastMessage(JSON.parse(ev.data)); } catch {}
    };
  }, [url]);

  useEffect(() => {
    connect();
    return () => ws.current?.close();
  }, [connect]);

  const send = useCallback((msg: WsMessage) => {
    if (ws.current?.readyState === WebSocket.OPEN) {
      ws.current.send(JSON.stringify(msg));
    }
  }, []);

  return { lastMessage, connected, send };
}
