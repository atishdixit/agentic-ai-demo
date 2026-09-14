import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChatService } from '../../core/services/chat.service';
import { ChatMessageDto } from '../../core/models/chat.model';
import { ChatMessageListComponent } from './chat-message-list.component';
import { ChatInputComponent } from './chat-input.component';
import { ErrorBannerComponent } from '../../shared/error-banner.component';

const USERNAME_STORAGE_KEY = 'chat-memory-app.username';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule, ChatMessageListComponent, ChatInputComponent, ErrorBannerComponent],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit {
  username = '';
  joined = false;
  messages: ChatMessageDto[] = [];
  waitingForReply = false;
  errorMessage: string | null = null;

  constructor(private readonly chatService: ChatService) {}

  ngOnInit(): void {
    const savedUsername = sessionStorage.getItem(USERNAME_STORAGE_KEY);
    if (savedUsername) {
      this.username = savedUsername;
      this.join();
    }
  }

  join(): void {
    const trimmed = this.username.trim();
    if (!trimmed) {
      return;
    }
    this.username = trimmed;
    this.errorMessage = null;
    sessionStorage.setItem(USERNAME_STORAGE_KEY, trimmed);

    this.chatService.getHistory(trimmed).subscribe({
      next: (history) => {
        this.messages = history;
        this.joined = true;
      },
      error: () => {
        this.errorMessage = 'Could not load your conversation history. Is the backend running?';
        this.joined = true;
      }
    });
  }

  onSend(message: string): void {
    this.errorMessage = null;
    this.messages = [...this.messages, { role: 'user', content: message }];
    this.waitingForReply = true;

    this.chatService.sendMessage({ username: this.username, message }).subscribe({
      next: (response) => {
        this.messages = [...this.messages, { role: 'assistant', content: response.reply }];
        this.waitingForReply = false;
      },
      error: (err) => {
        this.errorMessage = err?.error?.message ?? 'Something went wrong talking to the assistant.';
        this.waitingForReply = false;
      }
    });
  }

  switchUser(): void {
    sessionStorage.removeItem(USERNAME_STORAGE_KEY);
    this.username = '';
    this.joined = false;
    this.messages = [];
  }
}
