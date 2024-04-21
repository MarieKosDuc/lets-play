import { Component } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { Observable, Subscription } from 'rxjs';

import { MessageService } from 'primeng/api';

import { AuthenticationService } from '../../authentication/services/authentication.service';
import { User } from '../../authentication/models/user.model';
import { StorageService } from '../../_services/storage.service';
import { EventBusService } from 'src/app/_shared/event-bus.service';


@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
  providers: [MessageService]
})
export class HeaderComponent {
  public showDescription!: boolean;
  public userLoggedIn = false;
  public userInfos!: User | null;
  public showDropdown: boolean = false;
  private userSubscription: Subscription;

  private eventBusSubscription?: Subscription;
  

  constructor(
    private router: Router,
    private authService: AuthenticationService,
    private storageService: StorageService,
    private eventBusService: EventBusService,
    private MessageService: MessageService
  ) {
    this.userSubscription = this.storageService.user$.subscribe((user) => {
      this.userInfos = user;
      this.userLoggedIn = !!user;
    });
  }

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

    this.userLoggedIn = this.storageService.isLoggedIn();

    this.eventBusSubscription = this.eventBusService
      .on('logout', () => {
        console.log('Logout event received');
        this.logOut();
      });
  }

  toggleDropdown() {
    this.showDropdown = !this.showDropdown;
  }

  handleKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter' || event.key === ' ') {
      // Si la touche Entrée ou Espace est pressée
      this.toggleDropdown(); // Ouvre ou ferme le menu déroulant
    } //TODO : gérer le problème du menu déroulant qui s'ouvre quand on appuie sur entrée même non connecté
    //TODO : refermer le menu déroulant quand navigation
  }

  logOut() {
    this.authService.logout().subscribe(
      (response) => {
        this.userLoggedIn = false;
        this.userInfos = null;
        this.storageService.clean();
        this.showDropdown = false;
        setTimeout(() => {
          this.router.navigate(['/home']);
        }, 1000);
        ;
      },
      (error) => {
        console.log(error);
      }
    )
  }

}