export interface ChatRequest {
  username: string;
  message: string;
}

export interface ChatResponse {
  reply: string;
}

export interface ChatMessageDto {
  role: 'user' | 'assistant';
  content: string;
}
