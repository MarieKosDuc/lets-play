import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router'
import { AuthenticationService } from '../services/authentication.service';
import { StorageService } from 'src/app/_services/storage.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  hasError: boolean = false;
  error: String = '';
  isLoggedIn = false;
  isLoginFailed = false;

  constructor(private authService: AuthenticationService, private router: Router, private storageService: StorageService) {}

  ngOnInit(): void {
    if (this.storageService.isLoggedIn()) {
      this.isLoggedIn = true;
    }
  }

  onSubmit(form: NgForm) {
    this.hasError = false;

    const username = form.value.email;
    const password = form.value.password;

    this.authService.login(username, password).subscribe(
      (response) => {
        this.storageService.saveUser(response);
        console.log(response);

        this.isLoggedIn = true;
        this.isLoginFailed = false; //TODO : utiliser pour l'affichage du composant ?

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
          this.isLoginFailed = true;
        }
        
      }
    
    )

  }
}
