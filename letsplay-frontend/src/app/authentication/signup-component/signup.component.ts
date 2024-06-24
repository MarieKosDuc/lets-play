import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AuthenticationService } from '../services/authentication.service';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
})
export class SignupComponent {

  protected registerOk: boolean = false;

  protected passwordRegex =
    /^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,}$/;

  protected defaultProfilePicture: String =
    'https://res.cloudinary.com/daotbgmy2/image/upload/v1711275248/profile_pic_default.jpg';

  constructor(
    private authService: AuthenticationService,
    private router: Router,
    private messageService: MessageService
  ) {}

  onSubmit(form: NgForm) {
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

    this.authService
      .register(
        form.value.email,
        form.value.userName,
        this.defaultProfilePicture,
        form.value.password
      )
      .subscribe(
        (response) => {
          console.log(response);
          this.registerOk = true;
        },
        (error) => {
          this.registerOk = true;
          if (error.status == 409) {
            this.messageService.add ({ severity: 'error', summary: 'Erreur', detail: 'Cet email ou ce nom d\'utilisateur est déjà utilisé !' });
          } else {
            this.messageService.add ({ severity: 'error', summary: 'Erreur', detail: 'Erreur lors de la création du compte' });
          }
        }
      );
  }

  backToHome() {
    this.router.navigate(['/home']);
  }


}
