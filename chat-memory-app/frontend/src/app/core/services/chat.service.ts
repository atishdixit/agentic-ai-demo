import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ChatMessageDto, ChatRequest, ChatResponse } from '../models/chat.model';

@Injectable({ providedIn: 'root' })
export class ChatService {
  private readonly baseUrl = `${environment.apiBaseUrl}/chat/messages`;

  constructor(private readonly http: HttpClient) {}

  sendMessage(request: ChatRequest): Observable<ChatResponse> {
    return this.http.post<ChatResponse>(this.baseUrl, request);
  }

  getHistory(username: string): Observable<ChatMessageDto[]> {
    const params = new HttpParams().set('username', username);
    return this.http.get<ChatMessageDto[]>(this.baseUrl, { params });
  }
}
