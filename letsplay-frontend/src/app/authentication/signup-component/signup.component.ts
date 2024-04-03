import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AuthenticationService } from '../services/authentication.service';
import { Router } from '@angular/router'

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  hasMessage: boolean = false;
  message: String = '';
  passwordRegex = /^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,}$/;
  defaultProfilePicture: String = "https://res.cloudinary.com/daotbgmy2/image/upload/v1711275248/profile_pic_default.jpg"

  constructor(private authService: AuthenticationService, private router: Router) { }


  onSubmit(form: NgForm) {
    const password = form.value.password;

    if (!this.passwordRegex.test(password)){
      this.hasMessage = true;
      console.error('Invalid password')
      this.message = 'Le mot de passe doit contenir au moins 8 caractères, une lettre, un chiffre et un caractère spécial';
      return;
    }

    if (password !== form.value.confirmPassword) {
      this.hasMessage = true;
      console.error('Invalid password')
      this.message = 'Les mots de passe ne correspondent pas';
      return;
    }
//TODO : vérification de compte par email
    this.authService.register(form.value.email, form.value.userName, this.defaultProfilePicture, form.value.password).subscribe(
      (response) => {
        console.log(response);
        this.message = `Bienvenue ${response.username}, ton compte a bien été créé !`
        this.hasMessage = true;
        setTimeout(() => {
          this.router.navigateByUrl('/home');
        }, 2000)
      },
      (error) => {
        this.hasMessage = true;
        if (error.status == 409) {
          this.message = "Cet email ou ce nom d'utilisateur est déjà utilisé !"
        } else {
          this.message = error.message;
        }
      });

    console.log(form.value);
    
  }

}
