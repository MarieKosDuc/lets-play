import { Component } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { AuthenticationService } from '../../authentication/services/authentication.service';
import { User } from 'src/app/authentication/models/user.model';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  public showDescription!: boolean;
  public userLoggedIn!: boolean;
  public userInfos!: User | null;
  public showDropdown: boolean = false;

constructor(private router: Router, private authService: AuthenticationService) { }

  isHomeRoute(): boolean {
    return this.router.url === '/home';
  }

  ngOnInit() {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        const currentRoute = this.router.url;
        this.showDescription = currentRoute === '/home';
      }
    });

    this.authService.isLoggedIn().subscribe((loggedIn: boolean) => {
      this.userLoggedIn = loggedIn;
    })

    this.authService.getCurrentUser().subscribe((user: User | null) => {
      if(user != null){
        this.userInfos = user;
      }
    })
    
  }

  toggleDropdown() {
    this.showDropdown = !this.showDropdown;
  }

  handleKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter' || event.key === ' ') { // Si la touche Entrée ou Espace est pressée
        this.toggleDropdown(); // Ouvre ou ferme le menu déroulant
    } //TODO : gérer le problème du menu déroulant qui s'ouvre quand on appuie sur entrée même non connecté
    //TODO : refermer le menu déroulant quand navigation
}

  logOut() { //TODO : gérer l'erreur de parsing Json
    this.authService.logout().subscribe(() => { 
        this.userLoggedIn = false;
        this.userInfos = null; 

        this.router.navigateByUrl('/home');

        //TODO : gérer le reload après déconnexion
    });
}

}
