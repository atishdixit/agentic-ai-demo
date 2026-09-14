import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ChatMessageDto } from '../../core/models/chat.model';

@Component({
  selector: 'app-chat-message',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './chat-message.component.html',
  styleUrl: './chat-message.component.scss'
})
export class ChatMessageComponent {
  @Input({ required: true }) message!: ChatMessageDto;
}
