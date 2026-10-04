const API_BASE_URL = "http://localhost:8080/api";

export type Message = {
  id: string;
  content: string;
  /** Epoch milliseconds */
  createdAt: number;
};

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    cache: "no-store",
    ...init,
  });
  if (!response.ok) {
    throw new Error(`Request failed: ${response.status} ${response.statusText}`);
  }
  return (await response.json()) as T;
}

export function getLatestMessages(limit = 10): Promise<Message[]> {
  return request<Message[]>(`/messages/latest?limit=${limit}`);
}

export function createMessage(): Promise<Message> {
  return request<Message>("/messages", { method: "POST" });
}
