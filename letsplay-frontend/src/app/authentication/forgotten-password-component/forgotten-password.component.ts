import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { AuthenticationService } from '../services/authentication.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-forgotten-password',
  templateUrl: './forgotten-password.component.html',
  styleUrl: './forgotten-password.component.css',
})
export class ForgottenPasswordComponent {
  protected fromEmail: boolean = true;
  protected loading: boolean = false;
  protected submitted: boolean = false;

  protected newPassword: string = '';
  protected confirmNewPassword: string = '';

  protected passwordRegex =
    /^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,}$/;

  constructor(
    private authenticationService: AuthenticationService,
    private messageService: MessageService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    const token = this.route.snapshot.params['token'];
    if (token) {
      this.fromEmail = false;
    }
  }
  

  onSubmit(form: NgForm) {
    this.loading = true;

    this.authenticationService.sendResetPasswordEmail(form.value.email).subscribe({
      next: (response) => {
        this.loading = false;
        this.submitted = true;
      },
      error: (error) => {
        this.loading = false;
        console.error('Error while sending reset password email:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Une erreur est survenue. Merci de réessayer ultérieurement.',
        });
      },
    });
  }

  onSubmitNewPassword(form: NgForm) {
    const password = form.value.password;

    if (!this.passwordRegex.test(password)) {
      console.error('Invalid password');
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail:
          'Le mot de passe doit contenir au moins 8 caractères, une lettre, un chiffre et un caractère spécial',
      });
      return;
    }

    if (password !== form.value.confirmPassword) {
      console.error('Invalid password');
      this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Les mots de passe ne correspondent pas' });
      return;
    }

    this.authenticationService.resetPassword(this.route.snapshot.params['token'], password).subscribe({
      next: (response) => {
        this.loading = false;
        this.submitted = true;
      },
      error: (error) => {
        this.loading = false;
        console.error('Error while resetting password:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Une erreur est survenue. Merci de réessayer ultérieurement.',
        });
      },
    });
  }
}
