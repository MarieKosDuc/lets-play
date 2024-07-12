import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthenticationService } from '../services/authentication.service';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
})
export class SignupComponent {
  protected signupForm!: FormGroup;

  protected registerOk: boolean = false;
  protected loading: boolean = false;

  protected passwordRegex =
    /^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,}$/;

  protected defaultProfilePicture: String =
    'https://res.cloudinary.com/daotbgmy2/image/upload/v1711275248/profile_pic_default.jpg';

  constructor(
    private authService: AuthenticationService,
    private router: Router,
    private messageService: MessageService,
    private fb: FormBuilder
  ) {
    this.signupForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
      confirmPassword: ['', Validators.required],
      terms: [false, Validators.requiredTrue],
    });
  }

  protected onSubmit() {
    const password = this.signupForm.value.password;
    this.loading = true;

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

    if (password !== this.signupForm.value.confirmPassword) {
      console.error('Invalid password');
      this.messageService.add({
        severity: 'error',
        summary: 'Erreur',
        detail: 'Les mots de passe ne correspondent pas',
      });
      this.loading = false;
    } else {
      this.createAccount();
    }
  }

  protected createAccount() {
    this.authService
      .register(
        this.signupForm.value.email,
        this.signupForm.value.name,
        this.defaultProfilePicture,
        this.signupForm.value.password
      )
      .subscribe({
        next: (response) => {
          this.loading = false;
          this.registerOk = true;
        },
        error: (error) => {
          this.loading = false;
          this.registerOk = false;
          if (error.status == 409) {
            this.messageService.add({
              severity: 'error',
              summary: 'Erreur',
              detail: "Cet email ou ce nom d'utilisateur est déjà utilisé !",
            });
          } else {
            this.messageService.add({
              severity: 'error',
              summary: 'Erreur',
              detail: 'Erreur lors de la création du compte',
            });
          }
        },
      });
  }

  protected backToHome() {
    this.router.navigate(['/home']);
  }
}
