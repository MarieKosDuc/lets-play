import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthenticationService } from '../services/authentication.service';
import { AuthStorageService } from 'src/app/shared/services/storage.service';

import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  protected isLoggedIn: boolean = false;
  protected loading: boolean = false;

  constructor(
    private authService: AuthenticationService,
    private router: Router,
    private authStorageService: AuthStorageService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    if (this.authStorageService.isLoggedIn()) {
      this.isLoggedIn = true;
    }
  }

  protected onSubmit(form: NgForm) {
    this.loading = true;
    this.isLoggedIn = true;

    const name = form.value.email;
    const password = form.value.password;

    this.authService.login(name, password).subscribe(
      (response) => {
        this.authStorageService.saveUser(response);

        this.isLoggedIn = true;
        this.loading = false;

        this.messageService.add({
          severity: 'success',
          summary: 'Connexion réussie',
          detail: `Te voila connecté.e, ${response.name}. Bonne recherche !`,
        });

        setTimeout(() => {
          if (this.authStorageService.getUser().roles.includes('ROLE_ADMIN')) {
            this.router.navigate(['/admin/users']);
          } else {
          this.router.navigate(['/home']);
          }
        }, 2000);
      },
      (error) => {
        this.loading = false;
        this.isLoggedIn = false;
        if (error.status == 403) {
          this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Identifiant ou mot de passe incorrect' });
        } else {
          this.messageService.add({ severity: 'error', summary: 'Erreur', detail: 'Une erreur est survenue' });
        }
      }
    );
  }
}