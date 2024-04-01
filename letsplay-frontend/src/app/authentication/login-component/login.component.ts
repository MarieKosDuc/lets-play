import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router'
import { AuthenticationService } from '../services/authentication.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  hasError: boolean = false;
  error: String = '';

  constructor(private authService: AuthenticationService, private router: Router) {}

  onSubmit(form: NgForm) {
    this.hasError = false;

    const username = form.value.email;
    const password = form.value.password;

    this.authService.login(username, password).subscribe(
      (response) => {
        console.log(response);
        this.error = `Te voilà connecté, ${response.username}. Bonne recherche !`
        this.hasError = true;
        setTimeout(() => {
          this.router.navigateByUrl('/home');
        }, 2000)        
      },
      (error) => {
        this.hasError = true;
        if(error.status == 403){
          this.error = "Identifiant ou mot de passe incorrect"
        } else {
          this.error = error.message;
        }
        
      }
    
    )

  }
}
