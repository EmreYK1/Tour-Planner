import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  email = '';
  password = '';
  confirmPassword = '';
  errorMessage = signal<string | null>(null);
  isLoading = signal(false);

  onSubmit(): void {
    if (this.password !== this.confirmPassword) {
      this.errorMessage.set('Die Passwörter stimmen nicht überein.');
      return;
    }

    this.errorMessage.set(null);
    this.isLoading.set(true);

    this.authService.register({ email: this.email, password: this.password }).subscribe({
      next: () => this.router.navigate(['/']),
      error: () => {
        this.errorMessage.set('Registrierung fehlgeschlagen. Bitte versuche es erneut.');
        this.isLoading.set(false);
      }
    });
  }
}
