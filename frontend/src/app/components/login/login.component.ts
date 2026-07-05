import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  email = '';
  password = '';
  errorMessage = signal<string | null>(null);
  isLoading = signal(false);

  onSubmit(): void {
    this.errorMessage.set(null);
    this.isLoading.set(true);

    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: () => this.router.navigate(['/']),
      error: () => {
        this.errorMessage.set('E-Mail oder Passwort ungültig. Bitte erneut versuchen.');
        this.isLoading.set(false);
      }
    });
  }
}
