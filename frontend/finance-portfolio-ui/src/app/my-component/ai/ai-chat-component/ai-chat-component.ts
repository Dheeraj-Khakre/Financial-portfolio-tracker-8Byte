import { Component, OnInit } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { marked } from 'marked';
import { AiService } from '../../../services/ai.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';

interface ChatMessage {
  sender: 'user' | 'ai';
  text: string;
  html?: SafeHtml;
}

@Component({
  selector: 'app-ai-chat',
  standalone: true,   // ✅ make it standalone
  imports: [CommonModule, FormsModule, MatButtonModule, MatInputModule, MatFormFieldModule, MatIconModule],
  templateUrl: './ai-chat-component.html',
  styleUrls: ['./ai-chat-component.scss']
})
export class AiChatComponent implements OnInit {
  messages: ChatMessage[] = [];
  input = '';
  loading = false;

  constructor(private chatService: AiService, private sanitizer: DomSanitizer) { }
  ngOnInit(): void {
    // ✅ Add initial welcome message
    const welcomeText = `Hello! I'm your AI assistant. I can help you analyze your portfolio, provide insights, and answer your investment questions. Just type your message below to get started.`;
    const rawHtml = marked.parse(welcomeText) as string;
    this.messages.push({
      sender: 'ai',
      text: welcomeText,
      html: this.sanitizer.bypassSecurityTrustHtml(rawHtml)
    });
  }


  sendMessage(): void {
    const query = this.input.trim();
    if (!query) return;

    this.messages.push({ sender: 'user', text: query });
    this.input = '';
    this.loading = true;

    this.chatService.getAiChatResponse(query).subscribe({
      next: resp => {
        const rawHtml = marked.parse(resp) as string;   // assert it's a string
        const html = this.sanitizer.bypassSecurityTrustHtml(rawHtml);
        this.messages.push({ sender: 'ai', text: resp, html });
        this.loading = false;
      },
      error: err => {
        this.messages.push({ sender: 'ai', text: 'Error: ' + err.message });
        this.loading = false;
      }
    });
  }
}
